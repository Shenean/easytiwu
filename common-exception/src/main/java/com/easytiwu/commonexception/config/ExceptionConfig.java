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
     * 是否启用详细错误信息（开发环境建议开启，生产环境建议关闭）
     */
    private boolean enableDetailMessage = false;
    
    /**
     * 是否启用异常堆栈跟踪（开发环境建议开启，生产环境建议关闭）
     */
    private boolean enableStackTrace = false;
    
    /**
     * 是否启用异常日志记录
     */
    private boolean enableLogging = true;
    
    /**
     * 异常日志级别（ERROR, WARN, INFO, DEBUG）
     */
    private String logLevel = "ERROR";
    
    /**
     * 是否启用追踪ID
     */
    private boolean enableTraceId = true;
    
    /**
     * 追踪ID的Header名称
     */
    private String traceIdHeader = "X-Trace-Id";
    
    /**
     * 默认错误消息（当异常消息为空时使用）
     */
    private String defaultErrorMessage = "系统繁忙，请稍后重试";
    
    /**
     * 是否启用国际化支持
     */
    private boolean enableI18n = false;
    
    /**
     * 默认语言环境
     */
    private String defaultLocale = "zh_CN";
    
    /**
     * 异常响应格式配置
     */
    private ResponseFormat responseFormat = new ResponseFormat();
    
    // Getter and Setter methods
    public boolean isEnableDetailMessage() {
        return enableDetailMessage;
    }
    
    public void setEnableDetailMessage(boolean enableDetailMessage) {
        this.enableDetailMessage = enableDetailMessage;
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
    
    public ResponseFormat getResponseFormat() {
        return responseFormat;
    }
    
    public void setResponseFormat(ResponseFormat responseFormat) {
        this.responseFormat = responseFormat;
    }
    
    /**
     * 响应格式配置
     */
    public static class ResponseFormat {
        
        /**
         * 是否包含时间戳
         */
        private boolean includeTimestamp = true;
        
        /**
         * 是否包含请求路径
         */
        private boolean includePath = true;
        
        /**
         * 时间戳格式
         */
        private String timestampFormat = "yyyy-MM-dd HH:mm:ss";
        
        // Getter and Setter methods
        public boolean isIncludeTimestamp() {
            return includeTimestamp;
        }
        
        public void setIncludeTimestamp(boolean includeTimestamp) {
            this.includeTimestamp = includeTimestamp;
        }
        
        public boolean isIncludePath() {
            return includePath;
        }
        
        public void setIncludePath(boolean includePath) {
            this.includePath = includePath;
        }
        
        public String getTimestampFormat() {
            return timestampFormat;
        }
        
        public void setTimestampFormat(String timestampFormat) {
            this.timestampFormat = timestampFormat;
        }
    }
}