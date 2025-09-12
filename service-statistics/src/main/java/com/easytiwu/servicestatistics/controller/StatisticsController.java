package com.easytiwu.servicestatistics.controller;

import com.easytiwu.commonexception.result.Result;
import com.easytiwu.servicestatistics.dto.StatisticsDTO;
import com.easytiwu.servicestatistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计控制器
 * @author sheny
 */
@RestController
@RequestMapping("/statistics")
@Validated
@Slf4j
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    /**
     * 健康检查接口
     * @return 服务状态
     */
    @GetMapping("/health")
    public Result<String> healthCheck() {
        log.info("统计服务健康检查");
        return Result.success("Statistics service is running");
    }
    
    /**
     * 获取统计数据概览
     * @return 统计数据
     */
    @GetMapping("/overview")
    public Result<StatisticsDTO> getStatisticsOverview() {
        log.info("开始获取统计数据概览");
        StatisticsDTO statistics = statisticsService.getStatisticsOverview();
        log.info("成功获取统计数据概览");
        return Result.success(statistics);
    }
}