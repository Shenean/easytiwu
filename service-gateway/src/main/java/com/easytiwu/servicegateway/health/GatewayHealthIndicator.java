package com.easytiwu.servicegateway.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * 网关服务健康检查指示器
 * 提供网关服务的健康状态信息
 */
@Component
public class GatewayHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // 这里可以添加具体的健康检查逻辑
        // 例如检查路由状态、下游服务可用性等
        
        // 简单示例：始终返回UP状态
        return Health.up()
                .withDetail("gateway", "Gateway service is running")
                .withDetail("version", "1.0.0")
                .build();
    }
}