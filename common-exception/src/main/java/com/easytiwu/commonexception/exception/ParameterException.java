package com.easytiwu.commonexception.exception;

import com.easytiwu.commonexception.enums.ErrorCode;

/**
 * 参数异常类
 * 用于处理参数校验失败、参数格式错误等异常情况
 * 
 * @author sheny
 */
public class ParameterException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 错误信息
     */
    private final String message;
    
    /**
     * 参数名称
     */
    private final String parameterName;
    
    /**
     * 参数值
     */
    private final Object parameterValue;
    
    /**
     * 详细错误信息（用于开发调试）
     */
    private final String detailMessage;
    
    /**
     * 构造函数 - 使用默认参数错误码
     * 
     * @param message 错误信息
     */
    public ParameterException(String message) {
        super(message);
        this.code = ErrorCode.PARAM_INVALID.getCode();
        this.message = message;
        this.parameterName = null;
        this.parameterValue = null;
        this.detailMessage = null;
    }
    
    /**
     * 构造函数 - 使用ErrorCode枚举
     * 
     * @param errorCode 错误码枚举
     */
    public ParameterException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.parameterName = null;
        this.parameterValue = null;
        this.detailMessage = null;
    }
    
    /**
     * 构造函数 - 使用ErrorCode枚举和参数名
     * 
     * @param errorCode 错误码枚举
     * @param parameterName 参数名称
     */
    public ParameterException(ErrorCode errorCode, String parameterName) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.parameterName = parameterName;
        this.parameterValue = null;
        this.detailMessage = null;
    }
    
    /**
     * 构造函数 - 使用ErrorCode枚举、参数名和参数值
     * 
     * @param errorCode 错误码枚举
     * @param parameterName 参数名称
     * @param parameterValue 参数值
     */
    public ParameterException(ErrorCode errorCode, String parameterName, Object parameterValue) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
        this.detailMessage = null;
    }
    
    /**
     * 构造函数 - 使用ErrorCode枚举和自定义消息
     * 
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误信息
     */
    public ParameterException(ErrorCode errorCode, String customMessage, String parameterName) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.message = customMessage;
        this.parameterName = parameterName;
        this.parameterValue = null;
        this.detailMessage = null;
    }
    
    /**
     * 构造函数 - 完整参数
     * 
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误信息
     * @param parameterName 参数名称
     * @param parameterValue 参数值
     * @param detailMessage 详细错误信息
     */
    public ParameterException(ErrorCode errorCode, String customMessage, String parameterName, 
                            Object parameterValue, String detailMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.message = customMessage;
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
        this.detailMessage = detailMessage;
    }
    
    /**
     * 构造函数 - 使用错误码和错误信息
     * 
     * @param code 错误码
     * @param message 错误信息
     */
    public ParameterException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.parameterName = null;
        this.parameterValue = null;
        this.detailMessage = null;
    }
    
    public Integer getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public String getParameterName() {
        return parameterName;
    }
    
    public Object getParameterValue() {
        return parameterValue;
    }
    
    public String getDetailMessage() {
        return detailMessage;
    }
    
    /**
     * 获取完整的错误信息（包含参数信息）
     * 
     * @return 完整错误信息
     */
    public String getFullMessage() {
        StringBuilder sb = new StringBuilder(message);
        
        if (parameterName != null) {
            sb.append(" [参数: ").append(parameterName);
            if (parameterValue != null) {
                sb.append("=").append(parameterValue);
            }
            sb.append("]");
        }
        
        if (detailMessage != null && !detailMessage.isEmpty()) {
            sb.append(" [详细信息: ").append(detailMessage).append("]");
        }
        
        return sb.toString();
    }
    
    /**
     * 静态工厂方法 - 创建参数异常
     * 
     * @param errorCode 错误码枚举
     * @return ParameterException实例
     */
    public static ParameterException of(ErrorCode errorCode) {
        return new ParameterException(errorCode);
    }
    
    /**
     * 静态工厂方法 - 创建参数异常（带参数名）
     * 
     * @param errorCode 错误码枚举
     * @param parameterName 参数名称
     * @return ParameterException实例
     */
    public static ParameterException of(ErrorCode errorCode, String parameterName) {
        return new ParameterException(errorCode, parameterName);
    }
    
    /**
     * 静态工厂方法 - 创建参数异常（带参数名和值）
     * 
     * @param errorCode 错误码枚举
     * @param parameterName 参数名称
     * @param parameterValue 参数值
     * @return ParameterException实例
     */
    public static ParameterException of(ErrorCode errorCode, String parameterName, Object parameterValue) {
        return new ParameterException(errorCode, parameterName, parameterValue);
    }
    
    /**
     * 静态工厂方法 - 参数缺失异常
     * 
     * @param parameterName 参数名称
     * @return ParameterException实例
     */
    public static ParameterException missing(String parameterName) {
        return new ParameterException(ErrorCode.PARAM_MISSING, 
                "缺少必要参数: " + parameterName, parameterName);
    }
    
    /**
     * 静态工厂方法 - 参数类型错误异常
     * 
     * @param parameterName 参数名称
     * @param expectedType 期望类型
     * @param actualType 实际类型
     * @return ParameterException实例
     */
    public static ParameterException typeError(String parameterName, String expectedType, String actualType) {
        return new ParameterException(ErrorCode.PARAM_TYPE_ERROR, 
                String.format("参数类型错误: %s，期望类型: %s，实际类型: %s", parameterName, expectedType, actualType),
                parameterName);
    }
    
    /**
     * 静态工厂方法 - 参数格式错误异常
     * 
     * @param parameterName 参数名称
     * @param parameterValue 参数值
     * @param expectedFormat 期望格式
     * @return ParameterException实例
     */
    public static ParameterException formatError(String parameterName, Object parameterValue, String expectedFormat) {
        return new ParameterException(ErrorCode.PARAM_FORMAT_ERROR, 
                String.format("参数格式错误: %s，期望格式: %s", parameterName, expectedFormat),
                parameterName, parameterValue, null);
    }
    
    /**
     * 静态工厂方法 - 参数长度错误异常
     * 
     * @param parameterName 参数名称
     * @param actualLength 实际长度
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return ParameterException实例
     */
    public static ParameterException lengthError(String parameterName, int actualLength, int minLength, int maxLength) {
        return new ParameterException(ErrorCode.PARAM_LENGTH_ERROR, 
                String.format("参数长度错误: %s，实际长度: %d，要求长度: %d-%d", parameterName, actualLength, minLength, maxLength),
                parameterName);
    }
    
    /**
     * 静态工厂方法 - 参数范围错误异常
     * 
     * @param parameterName 参数名称
     * @param parameterValue 参数值
     * @param minValue 最小值
     * @param maxValue 最大值
     * @return ParameterException实例
     */
    public static ParameterException rangeError(String parameterName, Object parameterValue, Object minValue, Object maxValue) {
        return new ParameterException(ErrorCode.PARAM_RANGE_ERROR, 
                String.format("参数值超出范围: %s，实际值: %s，要求范围: %s-%s", parameterName, parameterValue, minValue, maxValue),
                parameterName, parameterValue, null);
    }
    
    @Override
    public String toString() {
        return "ParameterException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                (parameterName != null ? ", parameterName='" + parameterName + '\'' : "") +
                (parameterValue != null ? ", parameterValue=" + parameterValue : "") +
                (detailMessage != null ? ", detailMessage='" + detailMessage + '\'' : "") +
                '}';
    }
}