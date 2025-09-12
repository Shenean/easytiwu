package com.easytiwu.servicestatistics.dto;

import lombok.Data;

import java.util.Map;

/**
 * 统计数据传输对象
 * @author sheny
 */
@Data
public class StatisticsDTO {
    
    /**
     * 题库总数
     */
    private Long bankTotal;
    
    /**
     * 题目总数
     */
    private Long questionTotal;
    
    /**
     * 按题目类型统计
     */
    private Map<String, TypeStatistics> byType;
    
    /**
     * 题目类型统计
     */
    @Data
    public static class TypeStatistics {
        
        /**
         * 该类型题目总数
         */
        private Long count;
        
        /**
         * 已完成题目数
         */
        private Long completedCount;
        
        /**
         * 正确题目数
         */
        private Long correctCount;
    }
}