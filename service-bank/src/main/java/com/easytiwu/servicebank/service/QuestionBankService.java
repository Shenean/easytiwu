package com.easytiwu.servicebank.service;

import com.easytiwu.servicebank.entity.QuestionBank;

import java.util.List;

/**
 * 题库服务接口
 * @author sheny
 */
public interface QuestionBankService {

    /**
     * 查询所有题库
     * @return 题库列表
     */
    List<QuestionBank> getAllQuestionBanks();

    /**
     * 根据ID删除题库
     * @param id 题库ID
     * @return 是否删除成功
     */
    boolean deleteQuestionBank(Long id);
}