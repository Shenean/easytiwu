package com.easytiwu.serviceupload.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.protocol.ConnectionConfigurations;
import com.alibaba.dashscope.utils.Constants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 调用 Qwen 大模型，将题目文本转为 JSON
 * 
 * @author sheny
 */
@Service
public class LargeModelService {
    private static final Logger logger = LoggerFactory.getLogger(LargeModelService.class);
    // Circuit breaker name
    private static final String QWEN_CB = "qwenService";

    private final Generation generationClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${llm.dashscope.api-key}")
    private String dashscopeApiKey;

    @Value("${llm.dashscope.read-timeout:300}")
    private int readTimeoutSeconds;

    @Value("${llm.dashscope.connect-timeout:120}")
    private int connectTimeoutSeconds;

    public LargeModelService() {
        this.generationClient = new Generation();
    }

    /**
     * Spring初始化后配置连接池
     * 使用@PostConstruct确保@Value注解的值已经注入
     */
    @PostConstruct
    public void initializeConnectionPool() {
        configureConnectionPool();
    }

    /**
     * 配置DashScope连接池参数，包括超时设置
     */
    private void configureConnectionPool() {
        try {
            ConnectionConfigurations connectionConfig = ConnectionConfigurations.builder()
                    .connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
                    .readTimeout(Duration.ofSeconds(readTimeoutSeconds))
                    .writeTimeout(Duration.ofSeconds(60))
                    .connectionIdleTimeout(Duration.ofSeconds(300))
                    .connectionPoolSize(32) 
                    .maximumAsyncRequests(32)
                    .maximumAsyncRequestsPerHost(32)
                    .build();

            Constants.connectionConfigurations = connectionConfig;
            logger.info("DashScope connection pool configured - connectTimeout: {}s, readTimeout: {}s",
                    connectTimeoutSeconds, readTimeoutSeconds);
        } catch (Exception e) {
            logger.warn("Failed to configure DashScope connection pool, using default settings", e);
        }
    }

    /**
     * 从配置文件中读取 system prompt
     * 
     * @return system prompt 内容
     */
    private String loadSystemPrompt() {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/parser_prompt.txt");
            InputStream inputStream = resource.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Failed to load system prompt from file", e);
            throw new RuntimeException("Unable to load system prompt", e);
        }
    }

    @CircuitBreaker(name = QWEN_CB, fallbackMethod = "fallbackQuestionsJson")
    public String generateQuestionsJson(String textContent)
            throws ApiException, NoApiKeyException, InputRequiredException {

        String systemPrompt = loadSystemPrompt();
        Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(textContent).build();

        GenerationParam param = GenerationParam.builder()
                .apiKey(dashscopeApiKey)
                .model("qwen3-max-preview")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.TEXT)
                .temperature(0.1f)
                .topP(0.9)
                .maxTokens(65536)
                .seed(12345)
                .build();

        logger.info("Calling Qwen model API with DashScope...");
        GenerationResult result = generationClient.call(param);

        if (result == null || result.getOutput() == null) {
            logger.error("Received null result or output from DashScope API");
            throw new RuntimeException("Invalid response from LLM API");
        }

        String output = result.getOutput().getText();
        logger.debug("LLM raw output: {}", output);

        // --- 内联清洗逻辑 ---
        String candidate = output.trim();
        if (!candidate.startsWith("{") && !candidate.startsWith("[")) {
            candidate = candidate.replaceAll("(?s)```json", "")
                    .replaceAll("(?s)```", "")
                    .trim();
            logger.debug("LLM cleaned output: {}", candidate);
        }

        // --- JSON 真正校验 ---
        try {
            objectMapper.readTree(candidate);
        } catch (Exception e) {
            logger.error("Invalid JSON from LLM: {}", candidate, e);
            throw new RuntimeException("Invalid JSON format from LLM", e);
        }

        return candidate;
    }

    /**
     * Circuit breaker fallback method
     * 返回一个最小 JSON，保证前端拿到结构
     */
    public String fallbackQuestionsJson(String textContent, Throwable ex) {
        logger.error("Qwen API call failed or timed out, entering fallback. Error: {}", ex.getMessage(), ex);
        return "[]";
    }
}
