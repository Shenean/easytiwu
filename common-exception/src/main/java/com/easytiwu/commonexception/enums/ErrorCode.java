package com.easytiwu.commonexception.enums;

/**
 * 错误码枚举类
 * 统一定义系统中所有的错误码和错误信息
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
    REQUEST_TIMEOUT(40008, "请求超时"),
    
    // 参数校验错误 401xx
    PARAM_INVALID(40100, "参数校验失败"),
    PARAM_MISSING(40101, "缺少必要参数"),
    PARAM_TYPE_ERROR(40102, "参数类型错误"),
    PARAM_FORMAT_ERROR(40103, "参数格式错误"),
    PARAM_LENGTH_ERROR(40104, "参数长度错误"),
    PARAM_RANGE_ERROR(40105, "参数值超出范围"),
    
    // 认证授权错误 402xx
    TOKEN_INVALID(40200, "Token无效"),
    TOKEN_EXPIRED(40201, "Token已过期"),
    TOKEN_MISSING(40202, "Token缺失"),
    PERMISSION_DENIED(40203, "权限不足"),
    ACCOUNT_DISABLED(40204, "账户已禁用"),
    ACCOUNT_LOCKED(40205, "账户已锁定"),
    
    // ========== 服务端错误 5xxxx ==========
    INTERNAL_SERVER_ERROR(50000, "服务器内部错误"),
    SERVICE_UNAVAILABLE(50003, "服务不可用"),
    GATEWAY_TIMEOUT(50004, "网关超时"),
    
    // 数据库错误 501xx
    DATABASE_ERROR(50100, "数据库操作失败"),
    DATABASE_CONNECTION_ERROR(50101, "数据库连接失败"),
    DATABASE_TIMEOUT(50102, "数据库操作超时"),
    DATA_INTEGRITY_VIOLATION(50103, "数据完整性约束违反"),
    DUPLICATE_KEY_ERROR(50104, "数据重复"),
    
    // 外部服务错误 502xx
    EXTERNAL_SERVICE_ERROR(50200, "外部服务调用失败"),
    EXTERNAL_SERVICE_TIMEOUT(50201, "外部服务调用超时"),
    EXTERNAL_SERVICE_UNAVAILABLE(50202, "外部服务不可用"),
    
    // 文件操作错误 503xx
    FILE_UPLOAD_ERROR(50300, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(50301, "文件下载失败"),
    FILE_NOT_FOUND(50302, "文件不存在"),
    FILE_SIZE_EXCEEDED(50303, "文件大小超出限制"),
    FILE_TYPE_NOT_SUPPORTED(50304, "文件类型不支持"),
    
    // ========== 业务错误 6xxxx ==========
    BUSINESS_ERROR(60000, "业务处理失败"),
    
    // 用户相关错误 601xx
    USER_NOT_FOUND(60100, "用户不存在"),
    USER_ALREADY_EXISTS(60101, "用户已存在"),
    USER_PASSWORD_ERROR(60102, "密码错误"),
    USER_STATUS_INVALID(60103, "用户状态异常"),
    
    // 订单相关错误 602xx
    ORDER_NOT_FOUND(60200, "订单不存在"),
    ORDER_STATUS_INVALID(60201, "订单状态异常"),
    ORDER_CANNOT_CANCEL(60202, "订单无法取消"),
    ORDER_AMOUNT_ERROR(60203, "订单金额错误"),
    
    // 库存相关错误 603xx
    STOCK_NOT_ENOUGH(60300, "库存不足"),
    PRODUCT_NOT_FOUND(60301, "商品不存在"),
    PRODUCT_OFFLINE(60302, "商品已下架"),
    
    // 支付相关错误 604xx
    PAYMENT_FAILED(60400, "支付失败"),
    PAYMENT_TIMEOUT(60401, "支付超时"),
    PAYMENT_CANCELLED(60402, "支付已取消"),
    PAYMENT_AMOUNT_ERROR(60403, "支付金额错误");
    
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