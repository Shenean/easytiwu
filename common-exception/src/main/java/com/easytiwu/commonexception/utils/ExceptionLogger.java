package com.easytiwu.commonexception.utils;

import com.easytiwu.commonexception.exception.BusinessException;
import com.easytiwu.commonexception.exception.ParameterException;
import com.easytiwu.commonexception.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 异常日志记录工具类
 * 提供统一的异常日志记录功能，支持不同级别的日志记录和追踪
 * 
 * @author sheny
 */
public class ExceptionLogger {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionLogger.class);

    /**
     * 追踪ID的MDC键名
     */
    private static final String TRACE_ID_KEY = "traceId";

    /**
     * 异常类型的MDC键名
     */
    private static final String EXCEPTION_TYPE_KEY = "exceptionType";

    /**
     * 错误码的MDC键名
     */
    private static final String ERROR_CODE_KEY = "errorCode";

    /**
     * 记录业务异常日志
     * 
     * @param exception 业务异常
     */
    public void logBusinessException(BusinessException exception) {
        String traceId = generateTraceId();

        try {
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(EXCEPTION_TYPE_KEY, "BUSINESS");
            MDC.put(ERROR_CODE_KEY, String.valueOf(exception.getCode()));

            logger.warn("[业务异常] 错误码: {}, 错误信息: {}, 详细信息: {}, 追踪ID: {}",
                    exception.getCode(),
                    exception.getMessage(),
                    exception.getDetailMessage(),
                    traceId);

            // 如果有原因异常，记录堆栈信息
            if (exception.getCause() != null) {
                logger.warn("[业务异常] 原因异常堆栈信息:", exception.getCause());
            }

        } finally {
            MDC.clear();
        }
    }

    /**
     * 记录系统异常日志
     * 
     * @param exception 系统异常
     */
    public void logSystemException(SystemException exception) {
        String traceId = generateTraceId();

        try {
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(EXCEPTION_TYPE_KEY, "SYSTEM");
            MDC.put(ERROR_CODE_KEY, String.valueOf(exception.getCode()));

            logger.error("[系统异常] 错误码: {}, 错误信息: {}, 详细信息: {}, 追踪ID: {}",
                    exception.getCode(),
                    exception.getMessage(),
                    exception.getDetailMessage(),
                    traceId,
                    exception);

        } finally {
            MDC.clear();
        }
    }

    /**
     * 记录参数异常日志
     * 
     * @param exception 参数异常
     */
    public void logParameterException(ParameterException exception) {
        String traceId = generateTraceId();

        try {
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(EXCEPTION_TYPE_KEY, "PARAMETER");
            MDC.put(ERROR_CODE_KEY, String.valueOf(exception.getCode()));

            logger.warn("[参数异常] 错误码: {}, 错误信息: {}, 参数名: {}, 参数值: {}, 追踪ID: {}",
                    exception.getCode(),
                    exception.getMessage(),
                    exception.getParameterName(),
                    exception.getParameterValue(),
                    traceId);

        } finally {
            MDC.clear();
        }
    }

    /**
     * 记录参数校验异常日志
     * 
     * @param exception 参数校验异常
     */
    public void logValidationException(Exception exception) {
        String traceId = generateTraceId();

        try {
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(EXCEPTION_TYPE_KEY, "VALIDATION");

            logger.warn("[参数校验异常] 异常类型: {}, 错误信息: {}, 追踪ID: {}",
                    exception.getClass().getSimpleName(),
                    exception.getMessage(),
                    traceId);

        } finally {
            MDC.clear();
        }
    }

    /**
     * 记录HTTP相关异常日志
     * 
     * @param exception HTTP异常
     */
    public void logHttpException(Exception exception) {
        String traceId = generateTraceId();

        try {
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(EXCEPTION_TYPE_KEY, "HTTP");

            logger.warn("[HTTP异常] 异常类型: {}, 错误信息: {}, 追踪ID: {}",
                    exception.getClass().getSimpleName(),
                    exception.getMessage(),
                    traceId);

        } finally {
            MDC.clear();
        }
    }

    /**
     * 记录文件操作异常日志
     * 
     * @param exception 文件操作异常
     */
    public void logFileException(Exception exception) {
        String traceId = generateTraceId();

        try {
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(EXCEPTION_TYPE_KEY, "FILE");

            logger.error("[文件操作异常] 异常类型: {}, 错误信息: {}, 追踪ID: {}",
                    exception.getClass().getSimpleName(),
                    exception.getMessage(),
                    traceId,
                    exception);

        } finally {
            MDC.clear();
        }
    }

    /**
     * 记录安全相关异常日志
     * 
     * @param exception 安全异常
     */
    public void logSecurityException(Exception exception) {
        String traceId = generateTraceId();

        try {
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(EXCEPTION_TYPE_KEY, "SECURITY");

            logger.warn("[安全异常] 异常类型: {}, 错误信息: {}, 追踪ID: {}",
                    exception.getClass().getSimpleName(),
                    exception.getMessage(),
                    traceId);

        } finally {
            MDC.clear();
        }
    }

    /**
     * 记录数据库异常日志
     * 
     * @param exception 数据库异常
     */
    public void logDatabaseException(Exception exception) {
        String traceId = generateTraceId();

        try {
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(EXCEPTION_TYPE_KEY, "DATABASE");

            logger.error("[数据库异常] 异常类型: {}, 错误信息: {}, 追踪ID: {}",
                    exception.getClass().getSimpleName(),
                    exception.getMessage(),
                    traceId,
                    exception);

        } finally {
            MDC.clear();
        }
    }

    /**
     * 记录通用异常日志
     * 
     * @param exception 异常
     */
    public void logException(Exception exception) {
        String traceId = generateTraceId();

        try {
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(EXCEPTION_TYPE_KEY, "GENERAL");

            logger.error("[通用异常] 异常类型: {}, 错误信息: {}, 追踪ID: {}",
                    exception.getClass().getSimpleName(),
                    exception.getMessage(),
                    traceId,
                    exception);

        } finally {
            MDC.clear();
        }
    }

    /**
     * 记录自定义异常日志
     * 
     * @param level         日志级别（INFO, WARN, ERROR）
     * @param exceptionType 异常类型
     * @param errorCode     错误码
     * @param message       错误信息
     * @param exception     异常对象（可选）
     */
    public void logCustomException(String level, String exceptionType, Integer errorCode, String message,
            Exception exception) {
        String traceId = generateTraceId();

        try {
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(EXCEPTION_TYPE_KEY, exceptionType);
            if (errorCode != null) {
                MDC.put(ERROR_CODE_KEY, String.valueOf(errorCode));
            }

            String logMessage = String.format("[%s异常] 错误码: %s, 错误信息: %s, 追踪ID: %s",
                    exceptionType, errorCode, message, traceId);

            switch (level.toUpperCase()) {
                case "INFO":
                    if (exception != null) {
                        logger.info(logMessage, exception);
                    } else {
                        logger.info(logMessage);
                    }
                    break;
                case "WARN":
                    if (exception != null) {
                        logger.warn(logMessage, exception);
                    } else {
                        logger.warn(logMessage);
                    }
                    break;
                case "ERROR":
                default:
                    if (exception != null) {
                        logger.error(logMessage, exception);
                    } else {
                        logger.error(logMessage);
                    }
                    break;
            }

        } finally {
            MDC.clear();
        }
    }

    /**
     * 生成追踪ID
     * 
     * @return 追踪ID
     */
    private String generateTraceId() {
        // 先尝试从MDC中获取已存在的追踪ID
        String existingTraceId = MDC.get(TRACE_ID_KEY);
        if (existingTraceId != null && !existingTraceId.isEmpty()) {
            return existingTraceId;
        }

        // 生成新的追踪ID
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return timestamp + "-" + uuid;
    }

    /**
     * 设置追踪ID到MDC
     * 
     * @param traceId 追踪ID
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    /**
     * 获取当前追踪ID
     * 
     * @return 追踪ID
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    /**
     * 清除MDC中的追踪ID
     */
    public static void clearTraceId() {
        MDC.remove(TRACE_ID_KEY);
    }

    /**
     * 清除MDC中的所有信息
     */
    public static void clearMDC() {
        MDC.clear();
    }
}