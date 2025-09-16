package com.easytiwu.servicebank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easytiwu.servicebank.entity.QuestionOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sheny
 */
@Mapper
public interface QuestionOptionMapper extends BaseMapper<QuestionOption> {
    List<QuestionOption> selectByQuestionIds(@Param("questionIds") List<Long> questionIds);
}
