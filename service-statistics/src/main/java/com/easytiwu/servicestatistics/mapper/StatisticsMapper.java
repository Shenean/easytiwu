package com.easytiwu.servicestatistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 统计数据访问层接口
 * @author sheny
 */
@Mapper
public interface StatisticsMapper extends BaseMapper<Object> {
    
    /**
     * 调用数据库存储过程获取统计数据
     * @return JSON格式的统计数据字符串
     */
    @Select("SELECT get_question_stats() as stats")
    String getQuestionStatistics();
}