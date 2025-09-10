package com.easytiwu.servicegateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务发现配置
 * 动态管理和检测服务端点的可用性
 */
@Configuration
public class ServiceDiscoveryConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryConfig.class);
    
    @Value("${service.upload.url:http://localhost:8081}")
    private String uploadServiceUrl;
    
    @Value("${service.bank.url:http://localhost:8082}")
    private String bankServiceUrl;
    
    @Value("${service.content.url:http://localhost:8083}")
    private String contentServiceUrl;
    
    private final Map<String, Boolean> serviceHealthStatus = new ConcurrentHashMap<>();
    private final WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .build();
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("service-upload", r -> r.path("/api/upload/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(new org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory()
                                        .apply(c -> {
                                            c.setRetries(3);
                                            c.setStatuses(org.springframework.http.HttpStatus.BAD_GATEWAY,
                                                    org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                                                    org.springframework.http.HttpStatus.GATEWAY_TIMEOUT);
                                        }))
                                .filter((exchange, chain) -> {
                                    logger.info("路由到上传服务: {}", uploadServiceUrl);
                                    return chain.filter(exchange);
                                }))
                        .uri(uploadServiceUrl))
                
                .route("service-bank", r -> r.path("/api/bank/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter((exchange, chain) -> {
                                    logger.info("路由到银行服务: {}", bankServiceUrl);
                                    return chain.filter(exchange);
                                }))
                        .uri(bankServiceUrl))
                
                .route("service-content", r -> r.path("/api/content/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter((exchange, chain) -> {
                                    logger.info("路由到内容服务: {}", contentServiceUrl);
                                    return chain.filter(exchange);
                                }))
                        .uri(contentServiceUrl))
                
                .build();
    }
    
    /**
     * 检查服务健康状态
     */
    public Mono<Boolean> checkServiceHealth(String serviceUrl) {
        return webClient.get()
                .uri(serviceUrl + "/actuator/health")
                .retrieve()
                .toBodilessEntity()
                .map(response -> {
                    boolean isHealthy = response.getStatusCode().is2xxSuccessful();
                    serviceHealthStatus.put(serviceUrl, isHealthy);
                    logger.debug("服务健康检查 - URL: {}, 状态: {}", serviceUrl, isHealthy ? "健康" : "不健康");
                    return isHealthy;
                })
                .onErrorReturn(false)
                .timeout(Duration.ofSeconds(5));
    }
    
    /**
     * 获取服务健康状态
     */
    public boolean isServiceHealthy(String serviceUrl) {
        return serviceHealthStatus.getOrDefault(serviceUrl, true);
    }
    
    /**
     * 获取所有服务状态
     */
    public Map<String, Boolean> getAllServiceStatus() {
        return new ConcurrentHashMap<>(serviceHealthStatus);
    }
}