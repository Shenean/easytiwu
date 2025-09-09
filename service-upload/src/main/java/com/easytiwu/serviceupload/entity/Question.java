package com.easytiwu.serviceupload.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sheny
 */
@Data
@TableName("questions")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("bank_id")
    private Long bankId;

    // 题干
    private String content;

    // ENUM('single','multiple','fill_blank','true_false','short_answer') -> 使用 String
    private String type;

    @TableField("user_answer")
    private String userAnswer;

    @TableField("correct_answer")
    private String correctAnswer;

    private String analysis;

    @TableField("is_completed")
    private Integer isCompleted;

    @TableField("is_correct")
    private Integer isCorrect;

    @TableField(value = "created_at", fill = FieldFill.DEFAULT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.DEFAULT)
    private LocalDateTime updatedAt;
}