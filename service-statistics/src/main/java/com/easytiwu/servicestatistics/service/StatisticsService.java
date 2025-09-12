package com.easytiwu.servicestatistics.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.easytiwu.commonexception.exception.BusinessException;
import com.easytiwu.commonexception.exception.SystemException;
import com.easytiwu.commonexception.utils.ExceptionLogger;
import com.easytiwu.servicestatistics.dto.StatisticsDTO;
import com.easytiwu.servicestatistics.mapper.StatisticsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计服务业务逻辑层
 * @author sheny
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {
    
    private final StatisticsMapper statisticsMapper;
    private final ExceptionLogger exceptionLogger = new ExceptionLogger();
    
    /**
     * 获取统计数据概览
     * @return 统计数据DTO
     */
    public StatisticsDTO getStatisticsOverview() {
        try {
            // 1. 调用存储过程获取数据
            String jsonResult = statisticsMapper.getQuestionStatistics();
            
            if (jsonResult == null || jsonResult.trim().isEmpty()) {
                log.error("存储过程返回数据为空");
                throw new BusinessException("统计数据为空");
            }
            
            // 2. 解析JSON数据
            JSONObject jsonObject = JSON.parseObject(jsonResult);
            
            // 3. 转换为DTO
            StatisticsDTO statisticsDTO = new StatisticsDTO();
            statisticsDTO.setBankTotal(jsonObject.getLong("bank_total"));
            statisticsDTO.setQuestionTotal(jsonObject.getLong("question_total"));
            
            // 4. 处理分类统计数据
            JSONObject byTypeJson = jsonObject.getJSONObject("by_type");
            if (byTypeJson != null) {
                Map<String, StatisticsDTO.TypeStatistics> byType = new HashMap<>();
                
                for (String type : byTypeJson.keySet()) {
                    JSONObject typeData = byTypeJson.getJSONObject(type);
                    StatisticsDTO.TypeStatistics typeStats = new StatisticsDTO.TypeStatistics();
                    typeStats.setCount(typeData.getLong("count"));
                    typeStats.setCompletedCount(typeData.getLong("completed_count"));
                    typeStats.setCorrectCount(typeData.getLong("correct_count"));
                    byType.put(type, typeStats);
                }
                
                statisticsDTO.setByType(byType);
            }
            
            log.info("成功获取统计数据，题库总数：{}，题目总数：{}", 
                    statisticsDTO.getBankTotal(), statisticsDTO.getQuestionTotal());
            
            return statisticsDTO;
            
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            
            log.error("获取统计数据失败", e);
            exceptionLogger.logException(e);
            throw new SystemException("获取统计数据失败：" + e.getMessage());
        }
    }
}