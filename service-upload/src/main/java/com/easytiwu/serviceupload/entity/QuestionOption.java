package com.easytiwu.serviceupload.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sheny
 */
@Data
@TableName("question_options")
public class QuestionOption {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("question_id")
    private Long questionId;

    @TableField("option_content")
    private String optionContent;

    @TableField("sort_order")
    private String sortOrder;

    @TableField(value = "created_at", fill = FieldFill.DEFAULT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.DEFAULT)
    private LocalDateTime updatedAt;
}