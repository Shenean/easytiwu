package com.easytiwu.servicecontent.controller;

import com.easytiwu.commonexception.enums.ErrorCode;
import com.easytiwu.commonexception.exception.BusinessException;
import com.easytiwu.servicecontent.dto.QuestionDTO;
import com.easytiwu.servicecontent.service.AnswerVerificationServiceInterface;
import com.easytiwu.servicecontent.service.QuestionQueryServiceInterface;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sheny
 */
@RestController
@RequestMapping("/api/v1/content")
public class ContentController {

    private final QuestionQueryServiceInterface questionQueryService;
    private final AnswerVerificationServiceInterface answerVerificationService;

    public ContentController(QuestionQueryServiceInterface questionQueryService,
            AnswerVerificationServiceInterface answerVerificationService) {
        this.questionQueryService = questionQueryService;
        this.answerVerificationService = answerVerificationService;
    }

    @GetMapping("/questions")
    public List<QuestionDTO> getQuestions(@RequestParam Long bankId, @RequestParam(required = false) String type) {
        if (bankId == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "参数 bankId 不能为空");
        }
        return questionQueryService.queryQuestions(bankId, type);
    }

    @GetMapping("/questions-by-type")
    public List<QuestionDTO> getQuestionsByType(@RequestParam Long bankId, @RequestParam String questionType) {
        if (bankId == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "参数 bankId 不能为空");
        }
        if (questionType == null || questionType.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "参数 questionType 不能为空");
        }
        return questionQueryService.queryQuestionsByType(bankId, questionType);
    }

    @PostMapping("/verify-answer")
    public AnswerVerificationResponse verifyAnswer(@RequestBody AnswerVerificationRequest req) {
        if (req.getQuestionId() == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "参数 questionId 不能为空");
        }
        if (req.getUserAnswer() == null || req.getUserAnswer().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "参数 userAnswer 不能为空");
        }
        return answerVerificationService.verifyAnswer(req.getQuestionId(), req.getUserAnswer());
    }

    @Data
    public static class QuestionQueryRequest {
        private Long bankId;
        private String type;
    }

    @Data
    public static class QuestionQueryByTypeRequest {
        private Long bankId;
        private String questionType;
    }

    @Data
    public static class AnswerVerificationRequest {
        private Long questionId;
        private String userAnswer;
    }

    @Data
    public static class AnswerVerificationResponse {
        private Boolean isCorrect;
        private String correctAnswer;
        private String analysis;
        private String message;
        private Long questionId;
        private String userAnswer;
    }
}