package com.easytiwu.serviceauth.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easytiwu.commonexception.enums.ErrorCode;
import com.easytiwu.commonexception.exception.BusinessException;
import com.easytiwu.serviceauth.dto.UserLoginDTO;
import com.easytiwu.serviceauth.dto.UserRegisterDTO;
import com.easytiwu.serviceauth.entity.User;
import com.easytiwu.serviceauth.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    private final UserMapper userMapper;
    private final PasswordEncoderService passwordEncoderService;
    
    public UserService(UserMapper userMapper, PasswordEncoderService passwordEncoderService) {
        this.userMapper = userMapper;
        this.passwordEncoderService = passwordEncoderService;
    }
    
    /**
     * 用户注册
     * @param userRegisterDTO 用户注册信息
     * @return 注册成功的用户信息
     */
    public User register(UserRegisterDTO userRegisterDTO) {
        // 检查邮箱是否已存在
        User existingUser = userMapper.findByEmail(userRegisterDTO.getEmail());
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.DUPLICATE_KEY_ERROR, "该邮箱已被注册");
        }
        
        // 创建新用户
        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);
        user.setPassword(passwordEncoderService.encode(userRegisterDTO.getPassword()));
        user.setIsActive(1); // 默认激活
        
        // 保存用户
        if (!save(user)) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "用户注册失败");
        }
        
        // 清除密码信息后返回
        user.setPassword(null);
        return user;
    }
    
    /**
     * 用户登录
     * @param userLoginDTO 用户登录信息
     * @return 登录成功的用户信息
     */
    public User login(UserLoginDTO userLoginDTO) {
        // 根据邮箱查找用户
        User user = userMapper.findByEmail(userLoginDTO.getEmail());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        // 验证密码
        if (!passwordEncoderService.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "密码错误");
        }
        
        // 检查账户是否激活
        if (user.getIsActive() == 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "账户未激活");
        }
        
        // 清除密码信息后返回
        user.setPassword(null);
        return user;
    }
}