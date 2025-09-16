package com.easytiwu.servicebank.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 合并题库请求DTO
 * @author sheny
 */
@Data
public class MergeBankRequest {

    @NotNull(message = "题库ID1不能为空")
    @Min(value = 1, message = "题库ID1必须大于0")
    private Long bankId1;

    @NotNull(message = "题库ID2不能为空")
    @Min(value = 1, message = "题库ID2必须大于0")
    private Long bankId2;

    @NotBlank(message = "新题库名称不能为空")
    private String name;

    private String description;
}
