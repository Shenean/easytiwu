package com.easytiwu.serviceupload.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.InputRequiredException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author sheny
 */
@Service
public class LargeModelService {
    private static final Logger logger = LoggerFactory.getLogger(LargeModelService.class);
    // Circuit breaker name
    private static final String QWEN_CB = "qwenService";

    private final Generation generationClient;
    
    @Value("${llm.dashscope.api-key}")
    private String dashscopeApiKey;

    public LargeModelService() {
        this.generationClient = new Generation();
    }

    /**
     * Sends the text content to Qwen model to generate questions JSON.
     * Uses Resilience4j CircuitBreaker to handle failures gracefully.
     */
    @CircuitBreaker(name = QWEN_CB, fallbackMethod = "fallbackQuestionsJson")
    public String generateQuestionsJson(String textContent) throws ApiException, NoApiKeyException, InputRequiredException {
        String systemPrompt = "你是一个智能助手，根据输入的题目内容生成题目列表的JSON，格式为{ \"questions\": [ ... ] }，请严格按照要求输出。";
        Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build();
        Message userMsg   = Message.builder().role(Role.USER.getValue()).content(textContent).build();
        // Build parameters for generation
        GenerationParam param = GenerationParam.builder()
                .apiKey(dashscopeApiKey)
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .temperature(0.1f)
                .topP(0.8)
                .maxTokens(2000)
                .seed(12345)
                .build();
        // Call the model API
        logger.info("Calling Qwen model API with DashScope...");
        GenerationResult result = generationClient.call(param);
        // Extract the generated text (JSON string) from result
        String output = result.getOutput().getChoices().get(0).getMessage().getContent();
        // Log and return the JSON output
        logger.debug("LLM raw output: {}", output);
        
        // 简单校验是否包含 JSON 结构
        if (!output.trim().startsWith("{") || !output.contains("\"questions\"")) {
            logger.warn("LLM output does not seem to be valid JSON: {}", output);
            throw new RuntimeException("Invalid JSON format from LLM");
        }
        
        return output;
    }

    /**
     * Fallback method for circuit breaker – returns an empty questions JSON if the LLM call fails.
     */
    private String fallbackQuestionsJson(String textContent, Throwable ex) {
        logger.error("Qwen API call failed or timed out, entering fallback. Error: {}", ex.getMessage(), ex);
        // Return a minimal JSON structure indicating failure (could also throw a custom exception)
        return "{\"questions\":[]}";
    }
}