package com.easytiwu.servicecontent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.easytiwu.servicecontent.controller.ContentController.AnswerVerificationResponse;
import com.easytiwu.servicecontent.dto.QuestionDTO;
import com.easytiwu.servicecontent.dto.QuestionOptionDTO;
import com.easytiwu.servicecontent.entity.Question;
import com.easytiwu.servicecontent.entity.QuestionOption;
import com.easytiwu.servicecontent.mapper.QuestionMapper;
import com.easytiwu.servicecontent.mapper.QuestionOptionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sheny
 */
@Service
public class QuestionQueryService {

    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper optionMapper;
    private final ObjectMapper objectMapper;

    public QuestionQueryService(QuestionMapper questionMapper, QuestionOptionMapper optionMapper) {
        this.questionMapper = questionMapper;
        this.optionMapper = optionMapper;
        this.objectMapper = new ObjectMapper();
    }

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

        List<Long> qids = questions.stream().map(Question::getId).toList();
        LambdaQueryWrapper<QuestionOption> ow = new LambdaQueryWrapper<>();
        ow.in(QuestionOption::getQuestionId, qids);
        List<QuestionOption> options = optionMapper.selectList(ow);
        Map<Long, List<QuestionOption>> optionMap = options.stream()
                .collect(Collectors.groupingBy(QuestionOption::getQuestionId));

        // Map to DTO
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

            // options mapping: convert to label/text
            List<QuestionOption> list = optionMap.getOrDefault(q.getId(), Collections.emptyList());
            List<QuestionOptionDTO> optDtos = list.stream()
                    .sorted(Comparator.comparing(QuestionOption::getSortOrder))
                    .map(opt -> {
                        QuestionOptionDTO od = new QuestionOptionDTO();
                        od.setLabel(opt.getSortOrder());
                        od.setText(opt.getOptionContent());
                        return od;
                    })
                    .toList();
            dto.setOptions(optDtos);
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * 验证用户答案
     * @param questionId 题目ID
     * @param userAnswer 用户答案
     * @return 验证结果
     */
    @Transactional
    public AnswerVerificationResponse verifyAnswer(Long questionId, String userAnswer) {
        // 查询题目信息
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new IllegalArgumentException("题目不存在，ID: " + questionId);
        }

        // 标准化用户答案
        String normalizedUserAnswer = normalizeAnswer(userAnswer, question.getType());
        String correctAnswer = question.getCorrectAnswer();
        
        // 验证答案是否正确
        boolean isCorrect = verifyAnswerCorrectness(normalizedUserAnswer, correctAnswer, question.getType());
        
        // 更新题目的用户答案和完成状态
        updateQuestionAnswer(questionId, normalizedUserAnswer, isCorrect);
        
        // 构建响应结果
        AnswerVerificationResponse response = new AnswerVerificationResponse();
        response.setQuestionId(questionId);
        response.setUserAnswer(normalizedUserAnswer);
        response.setCorrectAnswer(formatCorrectAnswer(correctAnswer, question.getType()));
        response.setIsCorrect(isCorrect);
        response.setAnalysis(question.getAnalysis());
        response.setMessage(isCorrect ? "回答正确！🎉" : "回答错误，继续加油！");
        
        return response;
    }

    /**
     * 标准化用户答案格式
     */
    private String normalizeAnswer(String userAnswer, String questionType) {
        if (userAnswer == null) {
            return "";
        }
        
        String trimmed = userAnswer.trim();
        
        // 多选题需要排序和JSON格式化
        if ("multiple".equals(questionType)) {
            try {
                // 如果已经是JSON格式，解析后重新排序
                if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                    List<String> options = Arrays.stream(trimmed.substring(1, trimmed.length() - 1)
                            .replaceAll("\"|", "")
                            .split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .toList();
                    return objectMapper.writeValueAsString(options.stream().map(String::trim).sorted().toList());
                } else {
                    // 如果是逗号分隔的格式，转换为JSON
                    List<String> options = Arrays.stream(trimmed.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .sorted()
                            .toList();
                    return objectMapper.writeValueAsString(options);
                }
            } catch (Exception e) {
                // 解析失败，返回原始答案
                return trimmed;
            }
        }
        
        return trimmed;
    }

    /**
     * 验证答案正确性
     */
    private boolean verifyAnswerCorrectness(String userAnswer, String correctAnswer, String questionType) {
        if (correctAnswer == null || userAnswer == null) {
            return false;
        }
        
        // 多选题需要特殊处理
        if ("multiple".equals(questionType)) {
            return compareMultipleChoiceAnswers(userAnswer, correctAnswer);
        }
        
        // 其他题型直接比较
        return userAnswer.equals(correctAnswer);
    }

    /**
     * 比较多选题答案
     */
    private boolean compareMultipleChoiceAnswers(String userAnswer, String correctAnswer) {
        try {
            // 解析用户答案
            List<String> userOptions = parseAnswerOptions(userAnswer);
            List<String> correctOptions = parseAnswerOptions(correctAnswer);
            
            // 排序后比较
            Collections.sort(userOptions);
            Collections.sort(correctOptions);
            
            return userOptions.equals(correctOptions);
        } catch (Exception e) {
            // 解析失败，直接字符串比较
            return userAnswer.equals(correctAnswer);
        }
    }

    /**
     * 解析答案选项
     */
    private List<String> parseAnswerOptions(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String trimmed = answer.trim();
        
        // JSON格式
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            try {
                return Arrays.stream(trimmed.substring(1, trimmed.length() - 1)
                        .replaceAll("\"|", "")
                        .split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
            } catch (Exception e) {
                return Arrays.asList(trimmed);
            }
        }
        
        // 逗号分隔格式
        return Arrays.stream(trimmed.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * 更新题目答案和状态
     */
    private void updateQuestionAnswer(Long questionId, String userAnswer, boolean isCorrect) {
        LambdaUpdateWrapper<Question> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Question::getId, questionId)
                .set(Question::getUserAnswer, userAnswer)
                .set(Question::getIsCompleted, 1)
                .set(Question::getIsCorrect, isCorrect ? 1 : 0)
                .set(Question::getUpdatedAt, LocalDateTime.now());
        
        questionMapper.update(null, updateWrapper);
    }

    /**
     * 格式化正确答案显示
     */
    private String formatCorrectAnswer(String correctAnswer, String questionType) {
        if (correctAnswer == null) {
            return "无";
        }
        
        // 判断题格式化
        if ("true_false".equals(questionType)) {
            return "1".equals(correctAnswer) ? "正确" : "错误";
        }
        
        // 多选题格式化
        if ("multiple".equals(questionType)) {
            try {
                List<String> options = parseAnswerOptions(correctAnswer);
                return String.join(", ", options);
            } catch (Exception e) {
                return correctAnswer;
            }
        }
        
        return correctAnswer;
    }
}