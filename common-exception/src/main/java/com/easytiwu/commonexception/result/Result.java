package com.easytiwu.commonexception.result;

import com.easytiwu.commonexception.enums.ErrorCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一响应结果类
 * 用于封装API接口的返回结果
 * 
 * @param <T> 数据类型
 * @author sheny
 */
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应时间戳
     */
    private LocalDateTime timestamp;
    
    /**
     * 请求追踪ID（用于日志追踪）
     */
    private String traceId;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 详细错误信息（开发环境使用）
     */
    private String detailMessage;
    
    /**
     * 私有构造函数
     */
    private Result() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 私有构造函数
     * 
     * @param code 响应码
     * @param message 响应消息
     * @param data 响应数据
     */
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = ErrorCode.SUCCESS.getCode().equals(code);
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 私有构造函数
     * 
     * @param errorCode 错误码枚举
     * @param data 响应数据
     */
    private Result(ErrorCode errorCode, T data) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = data;
        this.success = errorCode.isSuccess();
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 成功响应（无数据）
     * 
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> success() {
        return new Result<>(ErrorCode.SUCCESS, null);
    }
    
    /**
     * 成功响应（带数据）
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCode.SUCCESS, data);
    }
    
    /**
     * 失败响应（使用ErrorCode枚举）
     * 
     * @param errorCode 错误码枚举
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode, null);
    }
    
    /**
     * 失败响应（使用ErrorCode枚举和自定义消息）
     * 
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误消息
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> error(ErrorCode errorCode, String customMessage) {
        return new Result<>(errorCode.getCode(), customMessage, null);
    }
    
    /**
     * 判断是否成功
     * 
     * @return true-成功，false-失败
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }
    
    /**
     * 判断是否失败
     * 
     * @return true-失败，false-成功
     */
    public boolean isError() {
        return !isSuccess();
    }
    
    // Getter and Setter methods
    
    public Integer getCode() {
        return code;
    }
    
    public Result<T> setCode(Integer code) {
        this.code = code;
        this.success = ErrorCode.SUCCESS.getCode().equals(code);
        return this;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public T getData() {
        return data;
    }
    
    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public Result<T> setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }
    
    public String getTraceId() {
        return traceId;
    }
    
    public Result<T> setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
    
    public Boolean getSuccess() {
        return success;
    }
    
    public Result<T> setSuccess(Boolean success) {
        this.success = success;
        return this;
    }
    
    public String getDetailMessage() {
        return detailMessage;
    }
    
    public Result<T> setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }
    
    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", traceId='" + traceId + '\'' +
                ", success=" + success +
                (detailMessage != null ? ", detailMessage='" + detailMessage + '\'' : "") +
                '}';
    }
}