package com.easytiwu.servicegateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 网关请求日志过滤器
 * 记录完整的请求和响应信息用于问题追踪
 * @author sheny
 */
@Component
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingGatewayFilterFactory.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            
            String requestId = generateRequestId();
            String timestamp = LocalDateTime.now().format(FORMATTER);
            
            // 记录请求信息
            logger.info("[{}] [{}] 请求开始 - Method: {}, URI: {}, Headers: {}, RemoteAddress: {}",
                    requestId, timestamp, request.getMethod(), request.getURI(), 
                    request.getHeaders(), request.getRemoteAddress());
            
            long startTime = System.currentTimeMillis();
            
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                String endTimestamp = LocalDateTime.now().format(FORMATTER);
                
                // 记录响应信息
                logger.info("[{}] [{}] 请求完成 - Status: {}, Duration: {}ms, Headers: {}",
                        requestId, endTimestamp, response.getStatusCode(), duration, response.getHeaders());
            }));
        };
    }
    
    private String generateRequestId() {
        return "REQ-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }
    
    public static class Config {
        // 配置类，可以添加配置参数
    }
}