package com.easytiwu.starterdb.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;

/**
 * 基础Mapper接口
 * 提供所有实体对应的Mapper接口的公共功能
 * 
 * @param <T> 实体类型
 * @author YourName
 * @date 2025-05-13
 */
public interface BaseDbMapper<T> extends Mapper<T> {
    /**
     * 该接口继承自MyBatis Plus的Mapper接口
     * 包含了基本的CRUD操作方法
     * 
     * 后续可在此处添加项目通用的数据库操作方法
     * 以避免在每个实体对应的Mapper中重复定义
     */
}