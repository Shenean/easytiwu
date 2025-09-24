package com.easytiwu.servicebank.service;

import com.easytiwu.servicebank.dto.QuestionBankDTO;

import java.util.List;

/**
 * 题库服务接口
 *
 * @author sheny
 */
public interface QuestionBankService {

    /**
     * 查询所有题库
     *
     * @return 题库列表
     */
    List<QuestionBankDTO> getAllQuestionBanks();

    /**
     * 根据ID删除题库
     *
     * @param id 题库ID
     * @return 是否删除成功
     */
    boolean deleteQuestionBank(Long id);

    /**
     * 合并两个题库，创建新题库并复制题目和选项
     *
     * @param bankId1     第一个题库ID
     * @param bankId2     第二个题库ID
     * @param name        新题库名称
     * @param description 新题库描述（可选）
     * @return 新创建题库的ID
     * @throws com.easytiwu.commonexception.exception.BusinessException 参数非法或题库不存在时抛出
     */
    Long mergeAndCreateNewBank(Long bankId1, Long bankId2, String name, String description);
}