package com.easytiwu.servicecontent.service;

import com.easytiwu.servicecontent.dto.QuestionDTO;

import java.util.List;

/**
 * 题目查询服务接口
 *
 * @author lingma
 */
public interface QuestionQueryServiceInterface {

    /**
     * 根据题库ID和类型查询题目
     *
     * @param bankId 题库ID
     * @param type 题目类型 (all:所有题目, wrong:错题)
     * @return 题目列表
     */
    List<QuestionDTO> queryQuestions(Long bankId, String type);

    /**
     * 根据题库ID和题目类型查询题目
     *
     * @param bankId 题库ID
     * @param questionType 题目类型
     * @return 题目列表
     */
    List<QuestionDTO> queryQuestionsByType(Long bankId, String questionType);
}