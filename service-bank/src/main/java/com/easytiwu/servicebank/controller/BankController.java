package com.easytiwu.servicebank.controller;

import com.easytiwu.commonexception.enums.ErrorCode;
import com.easytiwu.commonexception.result.Result;
import com.easytiwu.servicebank.dto.MergeBankRequest;
import com.easytiwu.servicebank.dto.QuestionBankDTO;
import com.easytiwu.servicebank.service.QuestionBankService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public Result<String> healthCheck() {
        return Result.success("Bank service is running");
    }

    /**
     * 查询所有题库
     * @return 题库列表
     */
    @GetMapping
    public Result<List<QuestionBankDTO>> getAllQuestionBanks() {
        try {
            log.info("接收到查询所有题库的请求");
            List<QuestionBankDTO> questionBanks = questionBankService.getAllQuestionBanks();
            
            if (questionBanks == null || questionBanks.isEmpty()) {
                log.warn("查询结果为空");
                return Result.success(questionBanks);
            }
            
            log.info("成功查询到 {} 个题库", questionBanks.size());
            return Result.success(questionBanks);
        } catch (Exception e) {
            log.error("查询题库列表时发生异常: ", e);
            return Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "系统异常，请稍后重试");
        }
    }

    /**
     * 删除题库
     * @param id 题库ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteQuestionBank(
            @PathVariable("id") 
            @NotNull(message = "题库ID不能为空") 
            @Min(value = 1, message = "题库ID必须大于0") 
            Long id) {
        log.info("接收到删除题库的请求，ID: {}", id);
        boolean success = questionBankService.deleteQuestionBank(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error(ErrorCode.BUSINESS_ERROR, "删除失败");
        }
    }

    /**
     * 合并两个题库并创建新题库
     *
     * @param request 合并请求参数
     * @return 新题库ID
     */
    @PostMapping("/merge")
    public Result<Long> mergeBanks(@Validated @RequestBody MergeBankRequest request) {
        log.info("接收到合并题库请求: bankId1={}, bankId2={}, name={}",
                request.getBankId1(), request.getBankId2(), request.getName());

        try {
            Long newBankId = questionBankService.mergeAndCreateNewBank(
                    request.getBankId1(),
                    request.getBankId2(),
                    request.getName(),
                    request.getDescription()
            );
            log.info("题库合并成功，新题库ID: {}", newBankId);
            return Result.success(newBankId);
        } catch (Exception e) {
            log.error("合并题库时发生异常", e);
            throw e;
        }
    }
}