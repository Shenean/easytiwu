package com.easytiwu.servicecontent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easytiwu.servicecontent.dto.QuestionDTO;
import com.easytiwu.servicecontent.entity.Question;
import com.easytiwu.servicecontent.mapper.QuestionMapper;
import com.easytiwu.servicecontent.service.QuestionOptionServiceInterface;
import com.easytiwu.servicecontent.service.QuestionQueryServiceInterface;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 题目查询服务实现
 *
 * @author lingma
 */
@Service
public class QuestionQueryServiceImpl implements QuestionQueryServiceInterface {

    private final QuestionMapper questionMapper;
    private final QuestionOptionServiceInterface questionOptionService;

    public QuestionQueryServiceImpl(QuestionMapper questionMapper, 
                                  QuestionOptionServiceInterface questionOptionService) {
        this.questionMapper = questionMapper;
        this.questionOptionService = questionOptionService;
    }

    @Override
    public List<QuestionDTO> queryQuestions(Long bankId, String type) {
        LambdaQueryWrapper<Question> qw = new LambdaQueryWrapper<>();
        qw.eq(Question::getBankId, bankId);
        if ("wrong".equalsIgnoreCase(type)) {
            qw.eq(Question::getIsCompleted, 1).eq(Question::getIsCorrect, 0);
        }

        List<Question> questions = questionMapper.selectList(qw);
        if (questions.isEmpty()) {
            return Collections.emptyList();
        }

        return buildQuestionDTOs(questions);
    }

    @Override
    public List<QuestionDTO> queryQuestionsByType(Long bankId, String questionType) {
        if (bankId == null || questionType == null || questionType.trim().isEmpty()) {
            return Collections.emptyList();
        }
        questionType = questionType.trim().toLowerCase();

        // 可选：进一步限制合法类型（假设支持的题型如下）
        Set<String> validTypes = Set.of("single", "multiple", "true_false", "fill_in");
        if (!validTypes.contains(questionType)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<Question> qw = new LambdaQueryWrapper<>();
        qw.eq(Question::getBankId, bankId);
        qw.eq(Question::getType, questionType);

        List<Question> questions = questionMapper.selectList(qw);
        if (questions.isEmpty()) {
            return Collections.emptyList();
        }

        return buildQuestionDTOs(questions);
    }

    /**
     * 构建题目DTO列表
     *
     * @param questions 题目实体列表
     * @return 题目DTO列表
     */
    private List<QuestionDTO> buildQuestionDTOs(List<Question> questions) {
        List<Long> qids = questions.stream().map(Question::getId).toList();
        // 获取选项映射
        var optionMap = questionOptionService.getOptionsMap(qids);

        // 转换为DTO
        List<QuestionDTO> dtos = new ArrayList<>(questions.size());
        for (Question q : questions) {
            QuestionDTO dto = new QuestionDTO();
            dto.setId(q.getId());
            dto.setContent(q.getContent());
            dto.setType(q.getType());
            dto.setUserAnswer(q.getUserAnswer());
            dto.setCorrectAnswer(q.getCorrectAnswer());
            dto.setAnalysis(q.getAnalysis());
            dto.setIsCompleted(q.getIsCompleted());
            dto.setIsCorrect(q.getIsCorrect());

            // 选项转换
            var options = optionMap.getOrDefault(q.getId(), Collections.emptyList());
            dto.setOptions(questionOptionService.convertToDTO(options));
            dtos.add(dto);
        }
        return dtos;
    }
}