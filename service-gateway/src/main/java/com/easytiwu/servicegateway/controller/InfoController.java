package com.easytiwu.servicegateway.controller;

import com.easytiwu.commonexception.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 网关信息服务控制器
 * 提供网关的基本信息接口
 */
@RestController
public class InfoController {

    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "service-gateway");
        info.put("description", "EasyTiwu 网关服务");
        info.put("version", "1.0.0");
        
        Map<String, Object> contact = new HashMap<>();
        contact.put("author", "EasyTiwu Team");
        info.put("contact", contact);
        
        return Result.success(info);
    }
}