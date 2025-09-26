package com.easytiwu.serviceauth.controller;

import com.easytiwu.commonexception.result.Result;
import com.easytiwu.serviceauth.dto.UserLoginDTO;
import com.easytiwu.serviceauth.dto.UserRegisterDTO;
import com.easytiwu.serviceauth.entity.User;
import com.easytiwu.serviceauth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 
 * @author sheny
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     * 
     * @param userRegisterDTO 用户注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        User user = userService.register(userRegisterDTO);
        return Result.success(user);
    }

    /**
     * 用户登录
     * 
     * @param userLoginDTO 用户登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<User> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        User user = userService.login(userLoginDTO);
        return Result.success(user);
    }
}