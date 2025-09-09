package com.easytiwu.starterdb.config;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库相关异常处理类
 */
@RestControllerAdvice
public class DatabaseExceptionHandler {

    /**
     * 数据库连接异常处理
     */
    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    public Map<String, Object> handleConnectionException(CannotGetJdbcConnectionException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "数据库连接异常: " + e.getMessage());
        return result;
    }

    /**
     * 数据完整性 violation 异常处理
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "数据完整性约束违反: " + e.getMessage());
        return result;
    }

    /**
     * 重复键异常处理
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Map<String, Object> handleDuplicateKeyException(DuplicateKeyException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "违反唯一约束: " + e.getMessage());
        return result;
    }

    /**
     * 空结果异常处理
     */
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public Map<String, Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 404);
        result.put("message", "查询结果为空: " + e.getMessage());
        return result;
    }

    /**
     * SQL异常处理
     */
    @ExceptionHandler(SQLException.class)
    public Map<String, Object> handleSQLException(SQLException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "SQL执行异常: " + e.getMessage());
        return result;
    }

    /**
     * MyBatis Plus 异常处理
     */
    @ExceptionHandler(MybatisPlusException.class)
    public Map<String, Object> handleMybatisPlusException(MybatisPlusException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "MyBatis Plus执行异常: " + e.getMessage());
        return result;
    }

    /**
     * 数据访问异常处理
     */
    @ExceptionHandler(DataAccessException.class)
    public Map<String, Object> handleDataAccessException(DataAccessException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "数据访问异常: " + e.getMessage());
        return result;
    }
}