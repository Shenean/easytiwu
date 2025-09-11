package com.easytiwu.commonexception.enums;

/**
 * 错误码枚举类
 * 统一定义系统中核心的错误码和错误信息
 * 
 * 错误码规范：
 * - 成功：200
 * - 客户端错误：4xxxx
 * - 服务端错误：5xxxx
 * - 业务错误：6xxxx
 * 
 * @author sheny
 */
public enum ErrorCode {
    
    // ========== 成功状态码 ==========
    SUCCESS(200, "操作成功"),
    
    // ========== 客户端错误 4xxxx ==========
    BAD_REQUEST(40000, "请求参数错误"),
    UNAUTHORIZED(40001, "未授权访问"),
    FORBIDDEN(40003, "禁止访问"),
    NOT_FOUND(40004, "资源不存在"),
    METHOD_NOT_ALLOWED(40005, "请求方法不允许"),
    
    // 参数校验错误
    PARAM_INVALID(40100, "参数校验失败"),
    PARAM_MISSING(40101, "缺少必要参数"),
    PARAM_TYPE_ERROR(40102, "参数类型错误"),
    PARAM_FORMAT_ERROR(40103, "参数格式错误"),
    
    // ========== 服务端错误 5xxxx ==========
    INTERNAL_SERVER_ERROR(50000, "服务器内部错误"),
    
    // 数据库错误
    DATABASE_ERROR(50100, "数据库操作失败"),
    DATA_INTEGRITY_VIOLATION(50103, "数据完整性约束违反"),
    DUPLICATE_KEY_ERROR(50104, "数据重复"),
    
    // 文件操作错误
    FILE_SIZE_EXCEEDED(50300, "文件大小超出限制"),
    
    // ========== 业务错误 6xxxx ==========
    BUSINESS_ERROR(60000, "业务处理失败");
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 错误信息
     */
    private final String message;
    
    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    /**
     * 根据错误码获取错误信息
     * 
     * @param code 错误码
     * @return 错误信息
     */
    public static String getMessageByCode(Integer code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode.getMessage();
            }
        }
        return "未知错误";
    }
    
    /**
     * 根据错误码获取ErrorCode枚举
     * 
     * @param code 错误码
     * @return ErrorCode枚举
     */
    public static ErrorCode getByCode(Integer code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }
    
    /**
     * 判断是否为成功状态码
     * 
     * @return true-成功，false-失败
     */
    public boolean isSuccess() {
        return SUCCESS.equals(this);
    }
    
    /**
     * 判断是否为客户端错误
     * 
     * @return true-客户端错误，false-非客户端错误
     */
    public boolean isClientError() {
        return this.code >= 40000 && this.code < 50000;
    }
    
    /**
     * 判断是否为服务端错误
     * 
     * @return true-服务端错误，false-非服务端错误
     */
    public boolean isServerError() {
        return this.code >= 50000 && this.code < 60000;
    }
    
    /**
     * 判断是否为业务错误
     * 
     * @return true-业务错误，false-非业务错误
     */
    public boolean isBusinessError() {
        return this.code >= 60000;
    }
}