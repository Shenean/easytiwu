package com.easytiwu.servicebank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.easytiwu.commonexception.enums.ErrorCode;
import com.easytiwu.commonexception.exception.BusinessException;
import com.easytiwu.servicebank.entity.QuestionBank;
import com.easytiwu.servicebank.mapper.QuestionBankMapper;
import com.easytiwu.servicebank.service.QuestionBankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 题库服务实现类
 * @author sheny
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionBankServiceImpl implements QuestionBankService {

    private final QuestionBankMapper questionBankMapper;

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

            // 执行删除操作
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
}