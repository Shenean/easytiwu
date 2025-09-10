package com.easytiwu.servicecontent.controller;

import com.easytiwu.servicecontent.dto.QuestionDTO;
import com.easytiwu.servicecontent.service.QuestionQueryService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sheny
 */
@RestController
@RequestMapping({"/api/content", "/content"})
public class ContentController {

    private final QuestionQueryService questionQueryService;

    public ContentController(QuestionQueryService questionQueryService) {
        this.questionQueryService = questionQueryService;
    }

    @PostMapping("/questions")
    public List<QuestionDTO> getQuestions(@RequestBody QuestionQueryRequest req) {
        Long bankId = req.getId();
        String type = req.getType();
        return questionQueryService.queryQuestions(bankId, type);
    }

    @Data
    public static class QuestionQueryRequest {
        private Long id;
        private String type;
    }
}