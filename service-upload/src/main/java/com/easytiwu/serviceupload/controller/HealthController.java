package com.easytiwu.serviceupload.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 提供服务健康状态检查端点
 */
@RestController
@RequestMapping("/actuator")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "service-upload");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "EasyTiWu Upload Service");
        info.put("description", "文件上传和题库处理服务");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(info);
    }
}