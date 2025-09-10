package com.easytiwu.serviceupload.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.easytiwu.serviceupload.entity.Question;
import com.easytiwu.serviceupload.entity.QuestionBank;
import com.easytiwu.serviceupload.entity.QuestionOption;
import com.easytiwu.serviceupload.mapper.QuestionBankMapper;
import com.easytiwu.serviceupload.mapper.QuestionMapper;
import com.easytiwu.serviceupload.mapper.QuestionOptionMapper;
import com.easytiwu.serviceupload.util.ValidateJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sheny
 */
@Slf4j
@Service
public class DataImportService {

    private final QuestionBankMapper bankMapper;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper optionMapper;

    public DataImportService(QuestionBankMapper bankMapper,
                             QuestionMapper questionMapper,
                             QuestionOptionMapper optionMapper) {
        this.bankMapper = bankMapper;
        this.questionMapper = questionMapper;
        this.optionMapper = optionMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importQuestionsFromJson(String bankName, String bankDesc, String questionsJson) throws Exception {
        JSONObject root;

        try {
            String clean = questionsJson.trim();

            if (clean.startsWith("[")) {
                // LLM 输出裸数组，自动包装成 {"questions": [...]}
                JSONArray arr = JSON.parseArray(clean);
                root = new JSONObject();
                root.put("questions", arr);
            } else {
                root = JSON.parseObject(clean);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("LLM JSON 无法解析", e);
        }

        JSONArray arr = root.getJSONArray("questions");
        if (arr == null) throw new IllegalArgumentException("JSON 缺少 questions 数组");

        // === 创建题库 ===
        QuestionBank bank = new QuestionBank();
        bank.setName(bankName);
        bank.setDescription(bankDesc);
        bank.setTotalCount(0);
        bank.setCompletedCount(0);
        bank.setWrongCount(0);
        bankMapper.insert(bank);

        // === 遍历题目 ===
        for (int i = 0; i < arr.size(); i++) {
            JSONObject q = arr.getJSONObject(i);
            ValidateJson.ensureQuestion(q, i);

            Question question = new Question();
            question.setBankId(bank.getId());
            question.setContent(q.getString("content"));
            question.setType(q.getString("type"));
            question.setCorrectAnswer(q.getString("correct_answer"));
            question.setAnalysis(q.getString("analysis"));
            question.setIsCompleted(0);
            question.setIsCorrect(null);
            questionMapper.insert(question);

            // 只有选择题才处理选项
            JSONArray options = q.getJSONArray("options");
            if (options != null && ("single".equals(question.getType()) || "multiple".equals(question.getType()))) {
                for (int j = 0; j < options.size(); j++) {
                    JSONObject opt = options.getJSONObject(j);
                    String label = opt.getString("label");
                    String text = opt.getString("text");
                    if (label == null || text == null) continue;

                    QuestionOption o = new QuestionOption();
                    o.setQuestionId(question.getId());
                    o.setSortOrder(label);
                    o.setOptionContent(text);
                    optionMapper.insert(o);
                }
            }
        }

        log.info("Import finished. bankId={}, questions={}", bank.getId(), arr.size());
    }
}
