package com.easytiwu.commonexception.handler;

import com.easytiwu.commonexception.enums.ErrorCode;
import com.easytiwu.commonexception.exception.BusinessException;
import com.easytiwu.commonexception.exception.ParameterException;
import com.easytiwu.commonexception.exception.SystemException;
import com.easytiwu.commonexception.result.Result;
import com.easytiwu.commonexception.utils.ExceptionLogger;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
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
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
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
    public ResponseEntity<Result<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        exceptionLogger.logParameterException((Exception) e);
        
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
    public ResponseEntity<Result<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        exceptionLogger.logParameterException(e);
        
        String errorMessage = String.format("参数类型错误: %s，期望类型: %s", 
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
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
        exceptionLogger.logParameterException(e);
        
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
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        exceptionLogger.logHttpException((Exception) e);
        
        String errorMessage = String.format("不支持的请求方法: %s", e.getMethod());
        Result<Void> result = Result.error(ErrorCode.METHOD_NOT_ALLOWED, errorMessage);
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(result);
    }
    
    /**
     * 处理媒体类型不支持异常
     * 
     * @param e 媒体类型不支持异常
     * @return 错误响应
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        exceptionLogger.logHttpException((Exception) e);
        
        Result<Void> result = Result.error(ErrorCode.BAD_REQUEST, "不支持的媒体类型");
        if (isDevelopmentEnvironment()) {
            result.setDetailMessage(((Exception) e).getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(result);
    }
    
    /**
     * 处理找不到处理器异常
     * 
     * @param e 找不到处理器异常
     * @return 错误响应
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        exceptionLogger.logHttpException((Exception) e);
        
        Result<Void> result = Result.error(ErrorCode.NOT_FOUND, "请求的资源不存在");
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }
    
    /**
     * 处理文件上传大小超限异常
     * 
     * @param e 文件上传大小超限异常
     * @return 错误响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        exceptionLogger.logFileException(e);
        
        Result<Void> result = Result.error(ErrorCode.FILE_SIZE_EXCEEDED, "文件大小超出限制");
        
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(result);
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
     * 处理数据库异常
     * 
     * @param e 数据库异常
     * @return 错误响应
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Result<Void>> handleDatabaseException(SQLException e) {
        exceptionLogger.logDatabaseException(e);
        
        Result<Void> result;
        // 根据SQLException的错误码或消息判断具体错误类型
        String errorMessage = e.getMessage();
        if (errorMessage != null && (errorMessage.contains("Duplicate") || errorMessage.contains("duplicate"))) {
            result = Result.error(ErrorCode.DUPLICATE_KEY_ERROR, "数据重复");
        } else if (errorMessage != null && (errorMessage.contains("constraint") || errorMessage.contains("integrity"))) {
            result = Result.error(ErrorCode.DATA_INTEGRITY_VIOLATION, "数据完整性约束违反");
        } else {
            result = Result.error(ErrorCode.DATABASE_ERROR, "数据库操作失败");
        }
        
        if (isDevelopmentEnvironment()) {
            result.setDetailMessage(e.getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
    
    /**
     * 处理空指针异常
     * 
     * @param e 空指针异常
     * @return 错误响应
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Result<Void>> handleNullPointerException(NullPointerException e) {
        exceptionLogger.logSystemException(e);
        
        Result<Void> result = Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "系统内部错误");
        if (isDevelopmentEnvironment()) {
            result.setDetailMessage("空指针异常: " + e.getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
    
    /**
     * 处理IllegalArgumentException异常
     * 
     * @param e IllegalArgumentException异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        exceptionLogger.logParameterException(e);
        
        Result<Void> result = Result.error(ErrorCode.PARAM_INVALID, "参数错误: " + e.getMessage());
        
        return ResponseEntity.badRequest().body(result);
    }
    
    /**
     * 处理其他未知异常
     * 
     * @param e 未知异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        exceptionLogger.logUnknownException(e);
        
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