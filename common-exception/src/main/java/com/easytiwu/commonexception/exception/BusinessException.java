package com.easytiwu.commonexception.exception;

import com.easytiwu.commonexception.enums.ErrorCode;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 * 
 * @author sheny
 */
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final ErrorCode errorCode;
    
    /**
     * 错误信息
     */
    private final String message;
    
    /**
     * 详细错误信息（用于开发调试）
     */
    private final String detailMessage;
    
    /**
     * 构造函数 - 使用默认业务错误码
     * 
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = ErrorCode.BUSINESS_ERROR;
        this.message = message;
        this.detailMessage = null;
    }
    
    /**
     * 构造函数 - 使用ErrorCode枚举
     * 
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
        this.detailMessage = null;
    }
    
    /**
     * 构造函数 - 使用ErrorCode枚举和自定义消息
     * 
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误信息
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.message = customMessage;
        this.detailMessage = null;
    }
    
    /**
     * 构造函数 - 使用ErrorCode枚举和原因
     * 
     * @param errorCode 错误码枚举
     * @param cause 原因
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
        this.detailMessage = cause != null ? cause.getMessage() : null;
    }
    
    public ErrorCode getCode() {
        return errorCode;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public String getDetailMessage() {
        return detailMessage;
    }
    
    /**
     * 获取完整的错误信息（包含详细信息）
     * 
     * @return 完整错误信息
     */
    public String getFullMessage() {
        if (detailMessage != null && !detailMessage.isEmpty()) {
            return message + " [详细信息: " + detailMessage + "]";
        }
        return message;
    }
    
    /**
     * 静态工厂方法 - 创建业务异常
     * 
     * @param errorCode 错误码枚举
     * @return BusinessException实例
     */
    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode);
    }
    
    /**
     * 静态工厂方法 - 创建业务异常（自定义消息）
     * 
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误信息
     * @return BusinessException实例
     */
    public static BusinessException of(ErrorCode errorCode, String customMessage) {
        return new BusinessException(errorCode, customMessage);
    }
    
    @Override
    public String toString() {
        return "BusinessException{" +
                "code=" + errorCode +
                ", message='" + message + '\'' +
                (detailMessage != null ? ", detailMessage='" + detailMessage + '\'' : "") +
                '}';
    }
}