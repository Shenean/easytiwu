package com.easytiwu.servicecontent.service;

import com.easytiwu.servicecontent.controller.ContentController;

/**
 * 答案验证服务接口
 *
 * @author lingma
 */
public interface AnswerVerificationServiceInterface {

    /**
     * 验证用户答案
     *
     * @param questionId 题目ID
     * @param userAnswer 用户答案
     * @return 验证结果
     */
    ContentController.AnswerVerificationResponse verifyAnswer(Long questionId, String userAnswer);
}