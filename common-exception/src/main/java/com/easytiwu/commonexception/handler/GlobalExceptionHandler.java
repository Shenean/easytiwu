package com.easytiwu.commonexception.handler;

import com.easytiwu.commonexception.enums.ErrorCode;
import com.easytiwu.commonexception.exception.BusinessException;
import com.easytiwu.commonexception.exception.ParameterException;
import com.easytiwu.commonexception.exception.SystemException;
import com.easytiwu.commonexception.result.Result;
import com.easytiwu.commonexception.utils.ExceptionLogger;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.nio.file.AccessDeniedException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统中的各种异常，返回标准化的错误响应
 * 
 * @author sheny
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // logger 已被 exceptionLogger 替代，移除未使用的字段

    /**
     * 是否为开发环境
     */
    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    /**
     * 异常日志记录工具
     */
    private final ExceptionLogger exceptionLogger;

    public GlobalExceptionHandler() {
        this.exceptionLogger = new ExceptionLogger();
    }

    /**
     * 处理业务异常
     * 
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        exceptionLogger.logBusinessException(e);

        Result<Void> result = Result.error(e.getCode(), e.getMessage());
        if (isDevelopmentEnvironment()) {
            result.setDetailMessage(e.getDetailMessage());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 处理系统异常
     * 
     * @param e 系统异常
     * @return 错误响应
     */
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<Result<Void>> handleSystemException(SystemException e) {
        exceptionLogger.logSystemException(e);

        Result<Void> result = Result.error(e.getCode(), e.getMessage());
        if (isDevelopmentEnvironment()) {
            result.setDetailMessage(e.getDetailMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理参数异常
     * 
     * @param e 参数异常
     * @return 错误响应
     */
    @ExceptionHandler(ParameterException.class)
    public ResponseEntity<Result<Void>> handleParameterException(ParameterException e) {
        exceptionLogger.logParameterException(e);

        Result<Void> result = Result.error(e.getCode(), e.getMessage());
        if (isDevelopmentEnvironment()) {
            result.setDetailMessage(e.getFullMessage());
        }

        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理参数校验异常（@Valid注解）
     * 
     * @param e 方法参数校验异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        exceptionLogger.logValidationException(e);

        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        Result<Void> result = Result.error(ErrorCode.PARAM_INVALID, errorMessage);
        if (isDevelopmentEnvironment()) {
            String detailMessage = e.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            result.setDetailMessage(detailMessage);
        }

        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理绑定异常
     * 
     * @param e 绑定异常
     * @return 错误响应
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException e) {
        exceptionLogger.logValidationException(e);

        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        Result<Void> result = Result.error(ErrorCode.PARAM_INVALID, errorMessage);
        if (isDevelopmentEnvironment()) {
            String detailMessage = e.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            result.setDetailMessage(detailMessage);
        }

        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理约束违反异常
     * 
     * @param e 约束违反异常
     * @return 错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        exceptionLogger.logValidationException(e);

        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        Result<Void> result = Result.error(ErrorCode.PARAM_INVALID, errorMessage);
        if (isDevelopmentEnvironment()) {
            String detailMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            result.setDetailMessage(detailMessage);
        }

        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理缺少请求参数异常
     * 
     * @param e 缺少请求参数异常
     * @return 错误响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        exceptionLogger.logHttpException(e);

        String errorMessage = "缺少必要参数: " + e.getParameterName();
        Result<Void> result = Result.error(ErrorCode.PARAM_MISSING, errorMessage);

        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理方法参数类型不匹配异常
     * 
     * @param e 方法参数类型不匹配异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        exceptionLogger.logHttpException(e);

        Class<?> requiredType = e.getRequiredType();
        String typeName = requiredType != null ? requiredType.getSimpleName() : "未知";
        String errorMessage = String.format("参数类型错误: %s，期望类型: %s",
                e.getName(), typeName);
        Result<Void> result = Result.error(ErrorCode.PARAM_TYPE_ERROR, errorMessage);

        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理HTTP消息不可读异常
     * 
     * @param e HTTP消息不可读异常
     * @return 错误响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        exceptionLogger.logHttpException(e);

        Result<Void> result = Result.error(ErrorCode.PARAM_FORMAT_ERROR, "请求参数格式错误");
        if (isDevelopmentEnvironment()) {
            result.setDetailMessage(e.getMessage());
        }

        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理请求方法不支持异常
     * 
     * @param e 请求方法不支持异常
     * @return 错误响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        exceptionLogger.logHttpException(e);

        String errorMessage = String.format("不支持的请求方法: %s", e.getMethod());
        Result<Void> result = Result.error(ErrorCode.METHOD_NOT_ALLOWED, errorMessage);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(result);
    }

    /**
     * 处理访问拒绝异常
     * 
     * @param e 访问拒绝异常
     * @return 错误响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDeniedException(AccessDeniedException e) {
        exceptionLogger.logSecurityException(e);

        Result<Void> result = Result.error(ErrorCode.FORBIDDEN, "访问被拒绝");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
    }

    /**
     * 处理其他未知异常
     * 
     * @param e 未知异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        exceptionLogger.logException(e);

        Result<Void> result = Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "系统内部错误");
        if (isDevelopmentEnvironment()) {
            result.setDetailMessage(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 判断是否为开发环境
     * 
     * @return true-开发环境，false-非开发环境
     */
    private boolean isDevelopmentEnvironment() {
        return "dev".equalsIgnoreCase(activeProfile) || "development".equalsIgnoreCase(activeProfile);
    }
}