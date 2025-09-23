package com.easytiwu.servicebank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.easytiwu.commonexception.enums.ErrorCode;
import com.easytiwu.commonexception.exception.BusinessException;
import com.easytiwu.servicebank.entity.Question;
import com.easytiwu.servicebank.entity.QuestionBank;
import com.easytiwu.servicebank.entity.QuestionOption;
import com.easytiwu.servicebank.mapper.QuestionBankMapper;
import com.easytiwu.servicebank.mapper.QuestionMapper;
import com.easytiwu.servicebank.mapper.QuestionOptionMapper;
import com.easytiwu.servicebank.service.QuestionBankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 题库服务实现类
 *
 * @author sheny
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionBankServiceImpl implements QuestionBankService {

    private final QuestionBankMapper questionBankMapper;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;

    @Override
    public List<QuestionBank> getAllQuestionBanks() {
        log.info("开始查询所有题库");

        QueryWrapper<QuestionBank> wrapper = new QueryWrapper<>();
        wrapper.select(
                "id", "name", "description",
                "total_count", "completed_count", "wrong_count"
        );

        List<QuestionBank> questionBanks = questionBankMapper.selectList(wrapper);
        log.info("查询到 {} 个题库", questionBanks.size());
        return questionBanks;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQuestionBank(Long id) {
        if (id == null || id <= 0) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "题库ID不能为空且必须大于0");
        }

        try {
            log.info("开始删除题库，ID: {}", id);

            // 先检查题库是否存在
            QuestionBank existingBank = questionBankMapper.selectById(id);
            if (existingBank == null) {
                throw BusinessException.of(ErrorCode.NOT_FOUND, "题库不存在，ID: " + id);
            }

            // 执行删除操作（会级联删除题目和选项，因表有 ON DELETE CASCADE）
            int deletedRows = questionBankMapper.deleteById(id);

            if (deletedRows > 0) {
                log.info("成功删除题库，ID: {}, 题库名称: {}", id, existingBank.getName());
                return true;
            } else {
                log.warn("删除题库失败，ID: {}", id);
                throw BusinessException.of(ErrorCode.BUSINESS_ERROR, "删除题库失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除题库时发生异常，ID: {}", id, e);
            throw BusinessException.of(ErrorCode.INTERNAL_SERVER_ERROR, "删除题库失败，系统异常");
        }
    }

    /**
     * 合并两个题库，创建新题库并复制题目和选项
     *
     * @param bankId1     第一个题库ID
     * @param bankId2     第二个题库ID
     * @param name        新题库名称
     * @param description 新题库描述
     * @return 新题库ID
     */
    @Override
    public Long mergeAndCreateNewBank(Long bankId1, Long bankId2, String name, String description) {
        // 参数校验
        if (bankId1 == null || bankId2 == null || bankId1 <= 0 || bankId2 <= 0) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "题库ID不能为空且必须大于0");
        }
        if (name == null || name.trim().isEmpty()) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "题库名称不能为空");
        }

        log.info("开始合并题库，bankId1: {}, bankId2: {}, 新题库名: {}", bankId1, bankId2, name);

        // 检查两个源题库是否存在（只读操作，无需事务）
        QuestionBank bank1 = questionBankMapper.selectById(bankId1);
        QuestionBank bank2 = questionBankMapper.selectById(bankId2);
        if (bank1 == null) {
            throw BusinessException.of(ErrorCode.NOT_FOUND, "题库1不存在，ID: " + bankId1);
        }
        if (bank2 == null) {
            throw BusinessException.of(ErrorCode.NOT_FOUND, "题库2不存在，ID: " + bankId2);
        }

        // 执行需要事务的操作
        return doMergeInTransaction(bankId1, bankId2, name, description);
    }

    /**
     * 在事务中执行合并操作
     *
     * @param bankId1     第一个题库ID
     * @param bankId2     第二个题库ID
     * @param name        新题库名称
     * @param description 新题库描述
     * @return 新题库ID
     */
    @Transactional(rollbackFor = Exception.class)
    protected Long doMergeInTransaction(Long bankId1, Long bankId2, String name, String description) {
        // 创建新题库
        QuestionBank newBank = new QuestionBank();
        newBank.setName(name);
        newBank.setDescription(description != null ? description : "");
        newBank.setTotalCount(0);
        newBank.setCompletedCount(0);
        newBank.setWrongCount(0);
        newBank.setCreatedAt(LocalDateTime.now());
        newBank.setUpdatedAt(LocalDateTime.now());

        questionBankMapper.insert(newBank);
        Long newBankId = newBank.getId();
        log.info("新题库创建成功，ID: {}", newBankId);

        // 查询两个题库的所有题目
        List<Long> sourceBankIds = Arrays.asList(bankId1, bankId2);
        QueryWrapper<Question> qw = new QueryWrapper<>();
        qw.in("bank_id", sourceBankIds);
        List<Question> oldQuestions = questionMapper.selectList(qw);

        if (oldQuestions.isEmpty()) {
            log.info("无题目可复制，直接返回新题库ID: {}", newBankId);
            return newBankId;
        }

        // 复制题目（重置用户相关字段）
        List<Question> newQuestions = new ArrayList<>();
        Map<Long, Long> oldToNewQuestionIdMap = new HashMap<>();

        for (Question oldQ : oldQuestions) {
            Question newQ = new Question();
            newQ.setBankId(newBankId);
            newQ.setContent(oldQ.getContent());
            newQ.setType(oldQ.getType());
            newQ.setCorrectAnswer(oldQ.getCorrectAnswer());
            newQ.setAnalysis(oldQ.getAnalysis());
            newQ.setUserAnswer(null);
            newQ.setIsCompleted(0);
            newQ.setIsCorrect(null);
            newQ.setCreatedAt(LocalDateTime.now());
            newQ.setUpdatedAt(LocalDateTime.now());

            questionMapper.insert(newQ);
            newQuestions.add(newQ);
            oldToNewQuestionIdMap.put(oldQ.getId(), newQ.getId());
        }

        log.info("复制题目 {} 条", newQuestions.size());

        // 获取所有旧题目的ID，用于查询选项
        List<Long> oldQuestionIds = oldQuestions.stream().map(Question::getId).collect(Collectors.toList());
        if (!oldQuestionIds.isEmpty()) {
            QueryWrapper<QuestionOption> optionQw = new QueryWrapper<>();
            optionQw.in("question_id", oldQuestionIds);
            List<QuestionOption> oldOptions = questionOptionMapper.selectList(optionQw);

            // 批量插入新选项
            for (QuestionOption oldOpt : oldOptions) {
                Long newQuestionId = oldToNewQuestionIdMap.get(oldOpt.getQuestionId());
                if (newQuestionId == null) {
                    continue;
                }

                QuestionOption newOpt = new QuestionOption();
                newOpt.setQuestionId(newQuestionId);
                newOpt.setOptionContent(oldOpt.getOptionContent());
                newOpt.setSortOrder(oldOpt.getSortOrder());
                newOpt.setCreatedAt(LocalDateTime.now());
                newOpt.setUpdatedAt(LocalDateTime.now());

                questionOptionMapper.insert(newOpt);
            }

            log.info("复制选项 {} 条", oldOptions.size());
        }

        // 更新新题库的题目总数
        newBank.setTotalCount(newQuestions.size());
        questionBankMapper.updateById(newBank);

        log.info("题库合并完成，新题库ID: {}，共复制题目 {} 条", newBankId, newQuestions.size());
        return newBankId;
    }
}