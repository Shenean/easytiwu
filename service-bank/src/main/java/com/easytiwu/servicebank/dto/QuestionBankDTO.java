package com.easytiwu.servicebank.dto;

import lombok.Data;

/**
 * 题库信息DTO，用于接口响应
 * @author sheny
 */
@Data
public class QuestionBankDTO {
    private Long id;
    private String name;
    private String description;
    private Integer totalCount;
    private Integer completedCount;
    private Integer wrongCount;
}