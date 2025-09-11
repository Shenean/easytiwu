package com.easytiwu.commonexception.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 异常处理配置类
 * 提供可配置的异常处理参数
 * 
 * @author easytiwu
 * @since 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "easytiwu.exception")
public class ExceptionConfig {
    
    /**
     * 是否启用详细错误信息（开发环境建议启用）
     */
    private boolean enableDetailedErrorInfo = false;
    
    /**
     * 是否启用异常日志记录
     */
    private boolean enableExceptionLogging = true;
    
    /**
     * 是否启用异常堆栈跟踪
     */
    private boolean enableStackTrace = false;
    
    /**
     * 是否启用日志记录
     */
    private boolean enableLogging = true;
    
    /**
     * 日志级别
     */
    private String logLevel = "ERROR";
    
    /**
     * 是否启用追踪ID
     */
    private boolean enableTraceId = true;
    
    /**
     * 追踪ID的请求头名称
     */
    private String traceIdHeader = "X-Trace-Id";
    
    /**
     * 默认错误信息
     */
    private String defaultErrorMessage = "系统内部错误";
    
    /**
     * 是否启用国际化
     */
    private boolean enableI18n = false;
    
    /**
     * 默认语言环境
     */
    private String defaultLocale = "zh_CN";
    
    // Getter and Setter methods
    public boolean isEnableDetailedErrorInfo() {
        return enableDetailedErrorInfo;
    }
    
    public void setEnableDetailedErrorInfo(boolean enableDetailedErrorInfo) {
        this.enableDetailedErrorInfo = enableDetailedErrorInfo;
    }
    
    public boolean isEnableExceptionLogging() {
        return enableExceptionLogging;
    }
    
    public void setEnableExceptionLogging(boolean enableExceptionLogging) {
        this.enableExceptionLogging = enableExceptionLogging;
    }
    
    public boolean isEnableStackTrace() {
        return enableStackTrace;
    }
    
    public void setEnableStackTrace(boolean enableStackTrace) {
        this.enableStackTrace = enableStackTrace;
    }
    
    public boolean isEnableLogging() {
        return enableLogging;
    }
    
    public void setEnableLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }
    
    public String getLogLevel() {
        return logLevel;
    }
    
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
    
    public boolean isEnableTraceId() {
        return enableTraceId;
    }
    
    public void setEnableTraceId(boolean enableTraceId) {
        this.enableTraceId = enableTraceId;
    }
    
    public String getTraceIdHeader() {
        return traceIdHeader;
    }
    
    public void setTraceIdHeader(String traceIdHeader) {
        this.traceIdHeader = traceIdHeader;
    }
    
    public String getDefaultErrorMessage() {
        return defaultErrorMessage;
    }
    
    public void setDefaultErrorMessage(String defaultErrorMessage) {
        this.defaultErrorMessage = defaultErrorMessage;
    }
    
    public boolean isEnableI18n() {
        return enableI18n;
    }
    
    public void setEnableI18n(boolean enableI18n) {
        this.enableI18n = enableI18n;
    }
    
    public String getDefaultLocale() {
        return defaultLocale;
    }
    
    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
    

}