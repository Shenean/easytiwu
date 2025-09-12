package com.easytiwu.starterdb.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;

/**
 * 基础Mapper接口
 * 提供所有实体对应的Mapper接口的公共功能
 * 
 * @param <T> 实体类型
 * @author sheny
 */
public interface BaseDbMapper<T> extends Mapper<T> {
}