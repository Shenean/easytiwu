package com.easytiwu.servicebank.controller;

import com.easytiwu.servicebank.common.ApiResponse;
import com.easytiwu.servicebank.entity.QuestionBank;
import com.easytiwu.servicebank.service.QuestionBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 题库管理控制器
 * @author sheny
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/bank")
public class BankController {

    private final QuestionBankService questionBankService;

    public BankController(QuestionBankService questionBankService) {
        this.questionBankService = questionBankService;
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("Bank service is running");
    }

    /**
     * 查询所有题库
     * @return 题库列表
     */
    @GetMapping
    public ApiResponse<List<QuestionBank>> getAllQuestionBanks() {
        try {
            log.info("接收到查询所有题库的请求");
            List<QuestionBank> questionBanks = questionBankService.getAllQuestionBanks();
            
            if (questionBanks == null || questionBanks.isEmpty()) {
                log.warn("查询结果为空");
                return ApiResponse.success("查询成功，但没有找到题库数据", questionBanks);
            }
            
            log.info("成功查询到 {} 个题库", questionBanks.size());
            return ApiResponse.success("查询成功", questionBanks);
        } catch (Exception e) {
            log.error("查询题库列表时发生异常: ", e);
            return ApiResponse.error("系统异常，请稍后重试");
        }
    }

    /**
     * 删除题库
     * @param id 题库ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteQuestionBank(
            @PathVariable("id") 
            @NotNull(message = "题库ID不能为空") 
            @Min(value = 1, message = "题库ID必须大于0") 
            Long id) {
        log.info("接收到删除题库的请求，ID: {}", id);
        boolean success = questionBankService.deleteQuestionBank(id);
        if (success) {
            return ApiResponse.success("删除成功", null);
        } else {
            return ApiResponse.error("删除失败");
        }
    }
}