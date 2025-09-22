package com.easytiwu.servicecontent.service;

import com.easytiwu.servicecontent.dto.QuestionOptionDTO;
import com.easytiwu.servicecontent.entity.QuestionOption;

import java.util.List;
import java.util.Map;

/**
 * 题目选项服务接口
 *
 * @author lingma
 */
public interface QuestionOptionServiceInterface {

    /**
     * 根据题目ID列表查询选项
     *
     * @param questionIds 题目ID列表
     * @return 以题目ID为键，选项列表为值的映射
     */
    Map<Long, List<QuestionOption>> getOptionsMap(List<Long> questionIds);

    /**
     * 将实体选项列表转换为DTO选项列表
     *
     * @param options 实体选项列表
     * @return DTO选项列表
     */
    List<QuestionOptionDTO> convertToDTO(List<QuestionOption> options);
}