package com.easytiwu.servicebank.controller;

import com.easytiwu.servicebank.entity.QuestionBank;
import com.easytiwu.servicebank.mapper.QuestionBankMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sheny
 */
@RestController
@RequestMapping("/api/bank")
public class BankController {

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @GetMapping
    public List<QuestionBank> getAllQuestionBanks() {
        return questionBankMapper.selectList(null);
    }
}