package com.easytiwu.commonexception.exception;

import com.easytiwu.commonexception.enums.ErrorCode;

/**
 * 系统异常类
 * 用于处理系统级别的异常情况，如数据库连接失败、外部服务调用失败等
 * 
 * @author sheny
 */
public class SystemException extends RuntimeException {
    
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
     * 构造函数 - 使用默认系统错误码
     * 
     * @param message 错误信息
     */
    public SystemException(String message) {
        super(message);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
        this.detailMessage = null;
    }
    
    /**
     * 构造函数 - 使用ErrorCode枚举
     * 
     * @param errorCode 错误码枚举
     */
    public SystemException(ErrorCode errorCode) {
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
    public SystemException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.message = customMessage;
        this.detailMessage = null;
    }
    
    /**
     * 构造函数 - 使用ErrorCode枚举和原因
     * 
     * @param errorCode 错误码枚举
     * @param cause 原因
     */
    public SystemException(ErrorCode errorCode, Throwable cause) {
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
    public SystemException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.code = errorCode.getCode();
        this.message = customMessage;
        this.detailMessage = cause != null ? cause.getMessage() : null;
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
     * 静态工厂方法 - 创建系统异常
     * 
     * @param errorCode 错误码枚举
     * @return SystemException实例
     */
    public static SystemException of(ErrorCode errorCode) {
        return new SystemException(errorCode);
    }
    
    /**
     * 静态工厂方法 - 创建系统异常（带原因）
     * 
     * @param errorCode 错误码枚举
     * @param cause 原因
     * @return SystemException实例
     */
    public static SystemException of(ErrorCode errorCode, Throwable cause) {
        return new SystemException(errorCode, cause);
    }
    
    /**
     * 静态工厂方法 - 数据库异常
     * 
     * @param cause 原因
     * @return SystemException实例
     */
    public static SystemException databaseError(Throwable cause) {
        return new SystemException(ErrorCode.DATABASE_ERROR, cause);
    }
    
    /**
     * 静态工厂方法 - 外部服务异常
     * 
     * @param serviceName 服务名称
     * @param cause 原因
     * @return SystemException实例
     */
    public static SystemException externalServiceError(String serviceName, Throwable cause) {
        return new SystemException(ErrorCode.INTERNAL_SERVER_ERROR, 
                "调用外部服务失败: " + serviceName, cause);
    }
    
    /**
     * 静态工厂方法 - 文件操作异常
     * 
     * @param operation 操作类型
     * @param fileName 文件名
     * @param cause 原因
     * @return SystemException实例
     */
    public static SystemException fileOperationError(String operation, String fileName, Throwable cause) {
        return new SystemException(ErrorCode.FILE_SIZE_EXCEEDED, 
                String.format("文件%s失败: %s", operation, fileName), cause);
    }
    
    @Override
    public String toString() {
        return "SystemException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                (detailMessage != null ? ", detailMessage='" + detailMessage + '\'' : "") +
                '}';
    }
}