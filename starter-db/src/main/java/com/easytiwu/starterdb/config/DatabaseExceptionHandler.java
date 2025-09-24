package com.easytiwu.starterdb.config;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.easytiwu.commonexception.enums.ErrorCode;
import com.easytiwu.commonexception.result.Result;
import com.easytiwu.commonexception.utils.ExceptionLogger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 数据库相关异常处理类
 * @author sheny
 */
@RestControllerAdvice
public class DatabaseExceptionHandler {
    
    /**
     * 异常日志记录工具
     */
    private final ExceptionLogger exceptionLogger;

    public DatabaseExceptionHandler() {
        this.exceptionLogger = new ExceptionLogger();
    }

    /**
     * 数据库连接异常处理
     */
    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    public ResponseEntity<Result<Void>> handleConnectionException(CannotGetJdbcConnectionException e) {
        exceptionLogger.logDatabaseException(e);
        Result<Void> result = Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "数据库连接异常: " + e.getMessage());
        return ResponseEntity.status(500).body(result);
    }

    /**
     * 数据完整性 violation 异常处理
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        exceptionLogger.logDatabaseException(e);
        Result<Void> result = Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "数据完整性约束违反: " + e.getMessage());
        return ResponseEntity.status(500).body(result);
    }

    /**
     * 重复键异常处理
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Void>> handleDuplicateKeyException(DuplicateKeyException e) {
        exceptionLogger.logDatabaseException(e);
        Result<Void> result = Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "违反唯一约束: " + e.getMessage());
        return ResponseEntity.status(500).body(result);
    }

    /**
     * 空结果异常处理
     */
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Result<Void>> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        exceptionLogger.logDatabaseException(e);
        Result<Void> result = Result.error(ErrorCode.NOT_FOUND, "查询结果为空: " + e.getMessage());
        return ResponseEntity.status(404).body(result);
    }

    /**
     * SQL异常处理
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Result<Void>> handleSqlException(SQLException e) {
        exceptionLogger.logDatabaseException(e);
        Result<Void> result = Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "SQL执行异常: " + e.getMessage());
        return ResponseEntity.status(500).body(result);
    }

    /**
     * MyBatis Plus 异常处理
     */
    @ExceptionHandler(MybatisPlusException.class)
    public ResponseEntity<Result<Void>> handleMybatisPlusException(MybatisPlusException e) {
        exceptionLogger.logDatabaseException(e);
        Result<Void> result = Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "MyBatis Plus执行异常: " + e.getMessage());
        return ResponseEntity.status(500).body(result);
    }

    /**
     * 数据访问异常处理
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Result<Void>> handleDataAccessException(DataAccessException e) {
        exceptionLogger.logDatabaseException(e);
        Result<Void> result = Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "数据访问异常: " + e.getMessage());
        return ResponseEntity.status(500).body(result);
    }
}