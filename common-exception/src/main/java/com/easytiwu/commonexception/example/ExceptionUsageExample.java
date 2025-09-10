package com.easytiwu.commonexception.example;

import com.easytiwu.commonexception.enums.ErrorCode;
import com.easytiwu.commonexception.exception.BusinessException;
import com.easytiwu.commonexception.exception.ParameterException;
import com.easytiwu.commonexception.exception.SystemException;
import com.easytiwu.commonexception.result.Result;
import com.easytiwu.commonexception.utils.ExceptionLogger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异常处理模块使用示例
 * 展示如何在实际项目中使用各种异常类和工具
 * 
 * @author easytiwu
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/example")
public class ExceptionUsageExample {
    
    /**
     * 业务异常示例
     */
    @GetMapping("/business-error")
    public Result<String> businessError() {
        // 抛出业务异常
        throw BusinessException.of(ErrorCode.BUSINESS_ERROR, "用户余额不足");
    }
    
    /**
     * 参数异常示例
     */
    @GetMapping("/parameter-error")
    public Result<String> parameterError(@RequestParam(required = false) String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            // 抛出参数缺失异常
            throw ParameterException.missing("userId");
        }
        
        if (!userId.matches("\\d+")) {
            // 抛出参数类型错误异常
            throw ParameterException.typeError("userId", "数字", userId);
        }
        
        return Result.success("参数验证通过");
    }
    
    /**
     * 系统异常示例
     */
    @GetMapping("/system-error")
    public Result<String> systemError() {
        try {
            // 模拟数据库连接失败
            simulateDatabaseError();
            return Result.success("操作成功");
        } catch (Exception e) {
            // 记录异常日志
            ExceptionLogger exceptionLogger = new ExceptionLogger();
            exceptionLogger.logSystemException(e);
            // 抛出系统异常
            throw SystemException.databaseError(e);
        }
    }
    
    /**
     * 自定义错误码示例
     */
    @GetMapping("/custom-error")
    public Result<String> customError() {
        // 使用自定义错误码
        throw new BusinessException("这是一个自定义错误");
    }
    
    /**
     * 成功响应示例
     */
    @GetMapping("/success")
    public Result<String> success() {
        return Result.success("操作成功", "这是返回的数据");
    }
    
    /**
     * 带详细信息的异常示例
     */
    @GetMapping("/detailed-error")
    public Result<String> detailedError() {
        BusinessException exception = new BusinessException(
            ErrorCode.BUSINESS_ERROR,
            "订单处理失败"
        );
        
        // 记录业务异常日志
        ExceptionLogger exceptionLogger = new ExceptionLogger();
        exceptionLogger.logBusinessException(exception);
        
        throw exception;
    }
    
    /**
     * 模拟数据库错误
     */
    private void simulateDatabaseError() throws Exception {
        throw new Exception("Connection refused: database server is down");
    }
}