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
    private final Integer code;
    
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
        this.code = ErrorCode.BUSINESS_ERROR.getCode();
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
        this.code = errorCode.getCode();
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
        this.code = errorCode.getCode();
        this.message = customMessage;
        this.detailMessage = null;
    }
    
    /**
     * 构造函数 - 使用错误码和错误信息
     * 
     * @param code 错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.detailMessage = null;
    }
    
    /**
     * 构造函数 - 使用错误码、错误信息和原因
     * 
     * @param code 错误码
     * @param message 错误信息
     * @param cause 原因
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.detailMessage = cause != null ? cause.getMessage() : null;
    }
    
    /**
     * 构造函数 - 使用ErrorCode枚举和原因
     * 
     * @param errorCode 错误码枚举
     * @param cause 原因
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.detailMessage = cause != null ? cause.getMessage() : null;
    }
    
    /**
     * 构造函数 - 使用ErrorCode枚举、自定义消息和原因
     * 
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误信息
     * @param cause 原因
     */
    public BusinessException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.code = errorCode.getCode();
        this.message = customMessage;
        this.detailMessage = cause != null ? cause.getMessage() : null;
    }
    
    /**
     * 构造函数 - 完整参数
     * 
     * @param code 错误码
     * @param message 错误信息
     * @param detailMessage 详细错误信息
     * @param cause 原因
     */
    public BusinessException(Integer code, String message, String detailMessage, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.detailMessage = detailMessage;
    }
    
    public Integer getCode() {
        return code;
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
    
    /**
     * 静态工厂方法 - 创建业务异常（带原因）
     * 
     * @param errorCode 错误码枚举
     * @param cause 原因
     * @return BusinessException实例
     */
    public static BusinessException of(ErrorCode errorCode, Throwable cause) {
        return new BusinessException(errorCode, cause);
    }
    
    @Override
    public String toString() {
        return "BusinessException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                (detailMessage != null ? ", detailMessage='" + detailMessage + '\'' : "") +
                '}';
    }
}