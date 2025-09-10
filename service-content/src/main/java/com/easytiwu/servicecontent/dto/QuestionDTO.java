package com.easytiwu.servicecontent.dto;

import lombok.Data;
import java.util.List;

/**
 * @author sheny
 */
@Data
public class QuestionDTO {
    private Long id;
    private String content;
    private String type;
    private List<QuestionOptionDTO> options;
    private String userAnswer;
    private String correctAnswer;
    private String analysis;
    private Integer isCompleted;
    private Integer isCorrect;
}