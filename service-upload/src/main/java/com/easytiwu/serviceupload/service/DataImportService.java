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
import com.easytiwu.commonexception.exception.BusinessException;
import com.easytiwu.commonexception.exception.ParameterException;
import com.easytiwu.commonexception.exception.SystemException;
import com.easytiwu.commonexception.enums.ErrorCode;
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

    private static final String QUESTIONS_KEY = "questions";

    private final QuestionBankMapper bankMapper;
    private final SqlSessionFactory sqlSessionFactory;

    /**
     * 批量插入的批次大小
     */
    private static final int BATCH_SIZE = 1000;

    public DataImportService(QuestionBankMapper bankMapper,
            SqlSessionFactory sqlSessionFactory) {
        this.bankMapper = bankMapper;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importQuestionsFromJson(String bankName, String bankDesc, String questionsJson) {
        JSONObject root;

        try {
            String clean = questionsJson.trim();

            if (clean.startsWith("[")) {
                JSONArray arr = JSON.parseArray(clean);
                root = new JSONObject();
                root.put(QUESTIONS_KEY, arr);
            } else {
                root = JSON.parseObject(clean);
            }
        } catch (Exception e) {
            log.error("Failed to parse LLM JSON response", e);
            throw BusinessException.of(ErrorCode.PARAM_INVALID, "LLM JSON 无法解析");
        }

        JSONArray arr = root.getJSONArray(QUESTIONS_KEY);
        if (arr == null || arr.isEmpty()) {
            throw ParameterException.of(ErrorCode.PARAM_INVALID, QUESTIONS_KEY, arr);
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
            throw SystemException.databaseError(e);
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
                throw BusinessException.of(ErrorCode.BUSINESS_ERROR, String.format("处理第 %d 个题目时发生错误", i + 1));
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
            throw SystemException.databaseError(e);
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

            if (shouldProcessOptions(question, options)) {
                int[] result = processOptions(question, options, allOptions, optionIndex);
                optionIndex = result[0];
                int optionCount = result[1];
                log.debug("Assigned questionId {} to {} options", question.getId(), optionCount);
            }
        }
    }

    /**
     * 判断是否应该处理选项
     */
    private boolean shouldProcessOptions(Question question, JSONArray options) {
        return options != null && ("single".equals(question.getType()) || "multiple".equals(question.getType()));
    }

    /**
     * 处理选项并分配questionId
     */
    private int[] processOptions(Question question, JSONArray options, List<QuestionOption> allOptions,
            int optionIndex) {
        int optionCount = 0;
        for (int j = 0; j < options.size(); j++) {
            JSONObject opt = options.getJSONObject(j);
            if (opt.getString("label") != null && opt.getString("text") != null && optionIndex < allOptions.size()) {
                allOptions.get(optionIndex).setQuestionId(question.getId());
                optionIndex++;
                optionCount++;
            }
        }
        return new int[] { optionIndex, optionCount };
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
            throw SystemException.databaseError(e);
        }
    }
}