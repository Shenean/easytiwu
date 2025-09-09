package com.easytiwu.servicecontent.dto;

import lombok.Data;

@Data
public class QuestionOptionDTO {
    private String label; // A, B, C, D
    private String text;  // option content
}