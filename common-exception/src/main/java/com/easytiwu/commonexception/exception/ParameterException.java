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
    
    // ==================== 静态工厂方法 ====================
    
    /**
     * 创建参数异常 - 使用ErrorCode枚举
     * 
     * @param errorCode 错误码枚举
     * @return ParameterException实例
     */
    public static ParameterException of(ErrorCode errorCode) {
        return new ParameterException(errorCode);
    }
    
    /**
     * 创建参数异常 - 使用ErrorCode枚举和参数信息
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
     * 参数缺失异常
     * 
     * @param parameterName 参数名称
     * @return ParameterException实例
     */
    public static ParameterException missing(String parameterName) {
        return new ParameterException(ErrorCode.PARAM_MISSING, parameterName, null);
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