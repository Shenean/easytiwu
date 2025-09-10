package com.easytiwu.servicebank.service.impl;

import com.easytiwu.servicebank.entity.QuestionBank;
import com.easytiwu.servicebank.exception.BusinessException;
import com.easytiwu.servicebank.mapper.QuestionBankMapper;
import com.easytiwu.servicebank.service.QuestionBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 题库服务实现类
 * @author sheny
 */
@Slf4j
@Service
public class QuestionBankServiceImpl implements QuestionBankService {

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Override
    public List<QuestionBank> getAllQuestionBanks() {
        try {
            log.info("开始查询所有题库");
            List<QuestionBank> questionBanks = questionBankMapper.selectList(null);
            log.info("查询到 {} 个题库", questionBanks.size());
            return questionBanks;
        } catch (Exception e) {
            log.error("查询题库列表失败", e);
            throw new BusinessException("查询题库列表失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQuestionBank(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(400, "题库ID不能为空且必须大于0");
        }

        try {
            log.info("开始删除题库，ID: {}", id);
            
            // 先检查题库是否存在
            QuestionBank existingBank = questionBankMapper.selectById(id);
            if (existingBank == null) {
                throw new BusinessException(404, "题库不存在，ID: " + id);
            }

            // 执行删除操作
            int deletedRows = questionBankMapper.deleteById(id);
            boolean success = deletedRows > 0;
            
            if (success) {
                log.info("成功删除题库，ID: {}, 题库名称: {}", id, existingBank.getName());
            } else {
                log.warn("删除题库失败，ID: {}", id);
                throw new BusinessException("删除题库失败");
            }
            
            return success;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除题库时发生异常，ID: {}", id, e);
            throw new BusinessException("删除题库失败，系统异常");
        }
    }
}