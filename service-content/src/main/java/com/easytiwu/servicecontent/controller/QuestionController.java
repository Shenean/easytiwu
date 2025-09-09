package com.easytiwu.servicecontent.controller;

import com.easytiwu.servicecontent.dto.QuestionDTO;
import com.easytiwu.servicecontent.service.QuestionQueryService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/content", "/content"})
public class QuestionController {

    private final QuestionQueryService questionQueryService;

    public QuestionController(QuestionQueryService questionQueryService) {
        this.questionQueryService = questionQueryService;
    }

    @PostMapping("/questions")
    public List<QuestionDTO> getQuestions(@RequestBody QuestionQueryRequest req) {
        Long bankId = Long.valueOf(req.getId());
        String type = req.getType(); // all or wrong
        return questionQueryService.queryQuestions(bankId, type);
    }

    @Data
    public static class QuestionQueryRequest {
        private String id;   // bank id as string per example
        private String type; // all | wrong
    }
}