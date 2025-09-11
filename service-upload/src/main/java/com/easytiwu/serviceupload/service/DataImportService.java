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
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sheny
 */
@Slf4j
@Service
public class DataImportService {

    private final QuestionBankMapper bankMapper;
    // questionMapper 和 optionMapper 已被 SqlSessionFactory 替代进行批量操作
    // private final QuestionMapper questionMapper;
    // private final QuestionOptionMapper optionMapper;
    private final SqlSessionFactory sqlSessionFactory;

    /**
     * 批量插入的批次大小
     */
    private static final int BATCH_SIZE = 1000;

    public DataImportService(QuestionBankMapper bankMapper,
            QuestionMapper questionMapper,
            QuestionOptionMapper optionMapper,
            SqlSessionFactory sqlSessionFactory) {
        this.bankMapper = bankMapper;
        // questionMapper 和 optionMapper 在构造函数中保留，但实际使用 SqlSessionFactory 进行批量操作
        // this.questionMapper = questionMapper;
        // this.optionMapper = optionMapper;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importQuestionsFromJson(String bankName, String bankDesc, String questionsJson) throws Exception {
        JSONObject root;

        try {
            String clean = questionsJson.trim();

            if (clean.startsWith("[")) {
                JSONArray arr = JSON.parseArray(clean);
                root = new JSONObject();
                root.put("questions", arr);
            } else {
                root = JSON.parseObject(clean);
            }
        } catch (Exception e) {
            log.error("Failed to parse LLM JSON response", e);
            throw new IllegalArgumentException("LLM JSON 无法解析", e);
        }

        JSONArray arr = root.getJSONArray("questions");
        if (arr == null || arr.isEmpty()) {
            throw new IllegalArgumentException("JSON 缺少 questions 数组或数组为空");
        }

        log.info("Starting import process for {} questions", arr.size());

        // === 创建题库 ===
        QuestionBank bank = createQuestionBank(bankName, bankDesc);

        // === 批量导入题目和选项 ===
        batchImportQuestions(bank.getId(), arr);

        log.info("Import finished successfully. bankId={}, questions={}", bank.getId(), arr.size());
    }

    /**
     * 创建题库
     */
    private QuestionBank createQuestionBank(String bankName, String bankDesc) {
        try {
            QuestionBank bank = new QuestionBank();
            bank.setName(bankName);
            bank.setDescription(bankDesc);
            bank.setTotalCount(0);
            bank.setCompletedCount(0);
            bank.setWrongCount(0);
            bankMapper.insert(bank);
            log.debug("Created question bank with id: {}", bank.getId());
            return bank;
        } catch (Exception e) {
            log.error("Failed to create question bank: {}", bankName, e);
            throw new RuntimeException("创建题库失败", e);
        }
    }

    /**
     * 批量导入题目和选项
     */
    private void batchImportQuestions(Long bankId, JSONArray questionsArray) {
        List<Question> questionBatch = new ArrayList<>();
        List<QuestionOption> optionBatch = new ArrayList<>();

        // 预处理所有题目数据
        for (int i = 0; i < questionsArray.size(); i++) {
            try {
                JSONObject q = questionsArray.getJSONObject(i);
                ValidateJson.ensureQuestion(q, i);

                Question question = buildQuestion(bankId, q);
                questionBatch.add(question);

                // 收集选项数据（稍后批量插入）
                JSONArray options = q.getJSONArray("options");
                if (options != null && ("single".equals(question.getType()) || "multiple".equals(question.getType()))) {
                    List<QuestionOption> questionOptions = buildQuestionOptions(options);
                    optionBatch.addAll(questionOptions);
                }
            } catch (Exception e) {
                log.error("Failed to process question at index {}", i, e);
                throw new RuntimeException(String.format("处理第 %d 个题目时发生错误", i + 1), e);
            }
        }

        // 执行批量插入
        batchInsertQuestions(questionBatch);

        // 为选项设置正确的questionId并批量插入
        if (!optionBatch.isEmpty()) {
            assignQuestionIdsToOptions(questionBatch, optionBatch, questionsArray);
            batchInsertOptions(optionBatch);
        }
    }

    /**
     * 构建题目对象
     */
    private Question buildQuestion(Long bankId, JSONObject q) {
        Question question = new Question();
        question.setBankId(bankId);
        question.setContent(q.getString("content"));
        question.setType(q.getString("type"));
        question.setCorrectAnswer(q.getString("correct_answer"));
        question.setAnalysis(q.getString("analysis"));
        question.setIsCompleted(0);
        question.setIsCorrect(null);
        return question;
    }

    /**
     * 构建题目选项列表
     */
    private List<QuestionOption> buildQuestionOptions(JSONArray options) {
        List<QuestionOption> questionOptions = new ArrayList<>();
        for (int j = 0; j < options.size(); j++) {
            JSONObject opt = options.getJSONObject(j);
            String label = opt.getString("label");
            String text = opt.getString("text");
            if (label != null && text != null) {
                QuestionOption option = new QuestionOption();
                option.setSortOrder(label);
                option.setOptionContent(text);
                questionOptions.add(option);
            }
        }
        return questionOptions;
    }

    /**
     * 批量插入题目
     */
    private void batchInsertQuestions(List<Question> questions) {
        if (questions.isEmpty()) {
            return;
        }

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            QuestionMapper mapper = sqlSession.getMapper(QuestionMapper.class);

            for (int i = 0; i < questions.size(); i++) {
                mapper.insert(questions.get(i));

                // 每达到批次大小或最后一批时提交
                if ((i + 1) % BATCH_SIZE == 0 || i == questions.size() - 1) {
                    sqlSession.flushStatements();
                    log.debug("Batch inserted {} questions", Math.min(i + 1, BATCH_SIZE));
                }
            }

            sqlSession.commit();
            log.info("Successfully batch inserted {} questions", questions.size());
        } catch (Exception e) {
            log.error("Failed to batch insert questions", e);
            throw new RuntimeException("批量插入题目失败", e);
        }
    }

    /**
     * 为选项分配正确的questionId
     */
    private void assignQuestionIdsToOptions(List<Question> questions, List<QuestionOption> allOptions,
            JSONArray questionsArray) {
        int optionIndex = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            JSONObject q = questionsArray.getJSONObject(i);
            JSONArray options = q.getJSONArray("options");

            if (options != null && ("single".equals(question.getType()) || "multiple".equals(question.getType()))) {
                int optionCount = 0;
                for (int j = 0; j < options.size(); j++) {
                    JSONObject opt = options.getJSONObject(j);
                    if (opt.getString("label") != null && opt.getString("text") != null) {
                        if (optionIndex < allOptions.size()) {
                            allOptions.get(optionIndex).setQuestionId(question.getId());
                            optionIndex++;
                            optionCount++;
                        }
                    }
                }
                log.debug("Assigned questionId {} to {} options", question.getId(), optionCount);
            }
        }
    }

    /**
     * 批量插入选项
     */
    private void batchInsertOptions(List<QuestionOption> options) {
        if (options.isEmpty()) {
            return;
        }

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            QuestionOptionMapper mapper = sqlSession.getMapper(QuestionOptionMapper.class);

            for (int i = 0; i < options.size(); i++) {
                mapper.insert(options.get(i));

                // 每达到批次大小或最后一批时提交
                if ((i + 1) % BATCH_SIZE == 0 || i == options.size() - 1) {
                    sqlSession.flushStatements();
                    log.debug("Batch inserted {} options", Math.min(i + 1, BATCH_SIZE));
                }
            }

            sqlSession.commit();
            log.info("Successfully batch inserted {} options", options.size());
        } catch (Exception e) {
            log.error("Failed to batch insert options", e);
            throw new RuntimeException("批量插入选项失败", e);
        }
    }
}
