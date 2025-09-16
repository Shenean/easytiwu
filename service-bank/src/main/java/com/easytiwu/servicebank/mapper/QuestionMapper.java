package com.easytiwu.servicebank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easytiwu.servicebank.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sheny
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    List<Question> selectByBankIds(@Param("bankIds") List<Long> bankIds);
}
