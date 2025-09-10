package com.easytiwu.serviceupload.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sheny
 */
@Data
@TableName("question_banks")
public class QuestionBank {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("total_count")
    private Integer totalCount;

    @TableField("completed_count")
    private Integer completedCount;

    @TableField("wrong_count")
    private Integer wrongCount;

    @TableField(value = "created_at", fill = FieldFill.DEFAULT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.DEFAULT)
    private LocalDateTime updatedAt;
}
