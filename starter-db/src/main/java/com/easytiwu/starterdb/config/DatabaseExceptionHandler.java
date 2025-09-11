package com.easytiwu.starterdb.config;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.easytiwu.commonexception.enums.ErrorCode;
import com.easytiwu.commonexception.result.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
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
     * 数据库连接异常处理
     */
    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    public Result<Void> handleConnectionException(CannotGetJdbcConnectionException e) {
        return Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "数据库连接异常: " + e.getMessage());
    }

    /**
     * 数据完整性 violation 异常处理
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<Void> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "数据完整性约束违反: " + e.getMessage());
    }

    /**
     * 重复键异常处理
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        return Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "违反唯一约束: " + e.getMessage());
    }

    /**
     * 空结果异常处理
     */
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public Result<Void> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        return Result.error(ErrorCode.NOT_FOUND, "查询结果为空: " + e.getMessage());
    }

    /**
     * SQL异常处理
     */
    @ExceptionHandler(SQLException.class)
    public Result<Void> handleSQLException(SQLException e) {
        return Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "SQL执行异常: " + e.getMessage());
    }

    /**
     * MyBatis Plus 异常处理
     */
    @ExceptionHandler(MybatisPlusException.class)
    public Result<Void> handleMybatisPlusException(MybatisPlusException e) {
        return Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "MyBatis Plus执行异常: " + e.getMessage());
    }

    /**
     * 数据访问异常处理
     */
    @ExceptionHandler(DataAccessException.class)
    public Result<Void> handleDataAccessException(DataAccessException e) {
        return Result.error(ErrorCode.INTERNAL_SERVER_ERROR, "数据访问异常: " + e.getMessage());
    }
}