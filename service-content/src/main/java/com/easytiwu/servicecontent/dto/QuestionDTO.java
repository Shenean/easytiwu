package com.easytiwu.servicecontent.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private String content;
    private String type; // single, multiple, fill_blank, true_false, short_answer
    private List<QuestionOptionDTO> options;
    private String correct_answer; // return empty when is_completed=0
    private String analysis;       // return empty when is_completed=0
    private Integer is_completed;
    private Integer is_correct;    // could be null
}