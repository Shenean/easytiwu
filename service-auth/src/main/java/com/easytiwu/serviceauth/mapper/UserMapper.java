package com.easytiwu.serviceauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easytiwu.serviceauth.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户实体
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);
}