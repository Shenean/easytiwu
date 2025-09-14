package com.easytiwu.serviceupload.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.protocol.ConnectionConfigurations;
import com.alibaba.dashscope.utils.Constants;
import com.easytiwu.commonexception.enums.ErrorCode;
import com.easytiwu.commonexception.exception.BusinessException;
import com.easytiwu.commonexception.exception.SystemException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;

/**
 * 调用 Qwen 大模型，将题目文本转为 JSON
 * 
 * @author sheny
 */
@Service
public class LargeModelService {
    private static final Logger logger = LoggerFactory.getLogger(LargeModelService.class);
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
        try {
            configureConnectionPool(connectTimeoutSeconds, readTimeoutSeconds);
        } catch (Exception e) {
            logger.error("Failed to initialize connection pool: {}", e.getMessage(), e);
            // 添加详细的日志记录
            logger.error("Connection pool initialization failed with error: {}, StackTrace: {}", 
                         e.getMessage(), 
                         Arrays.toString(e.getStackTrace()));
            // 不重新抛出异常，避免影响服务启动
        }
    }

    /**
     * 配置DashScope连接池参数，包括超时设置
     */
    private static void configureConnectionPool(int connectTimeoutSeconds, int readTimeoutSeconds) {
        try {
            Constants.connectionConfigurations = ConnectionConfigurations.builder()
                    .connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
                    .readTimeout(Duration.ofSeconds(readTimeoutSeconds))
                    .writeTimeout(Duration.ofSeconds(60))
                    .connectionIdleTimeout(Duration.ofSeconds(45))
                    .connectionPoolSize(32)
                    .maximumAsyncRequests(32)
                    .maximumAsyncRequestsPerHost(32)
                    .build();
            LoggerFactory.getLogger(LargeModelService.class).info("DashScope connection pool configured - connectTimeout: {}s, readTimeout: {}s",
                    connectTimeoutSeconds, readTimeoutSeconds);
        } catch (Exception e) {
            LoggerFactory.getLogger(LargeModelService.class).error("Failed to configure DashScope connection pool: {}", e.getMessage(), e);
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
            String errorMsg = String.format("加载系统提示词失败，资源路径: %s，错误信息: %s", 
                                          "prompts/parser_prompt.txt", e.getMessage());
            logger.error(errorMsg, e);
            throw new SystemException(ErrorCode.INTERNAL_SERVER_ERROR, errorMsg, e);
        }
    }

    @CircuitBreaker(name = QWEN_CB, fallbackMethod = "fallbackQuestionsJson")
    public String generateQuestionsJson(String textContent)
            throws ApiException {

        String systemPrompt = loadSystemPrompt();
        Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(textContent).build();

        GenerationParam param = GenerationParam.builder()
                .apiKey(dashscopeApiKey)
                .model("qwen-flash")
                .messages(Arrays.asList(systemMsg, userMsg))
                .temperature(0.1f)
                .topP(0.8)
                .maxTokens(30000)
                .build();

        logger.info("Calling Qwen model API with DashScope...");
        GenerationResult result;
        try {
            result = generationClient.call(param);
        } catch (ApiException e) {
            String errorMsg = String.format("调用大模型API失败: %s", e.getMessage());
            logger.error(errorMsg, e);
            throw BusinessException.of(ErrorCode.BAD_REQUEST, errorMsg);
        } catch (NoApiKeyException e) {
            String errorMsg = "缺少API密钥";
            logger.error("{}: {}", errorMsg, e.getMessage(), e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, e);
        } catch (InputRequiredException e) {
            String errorMsg = "缺少必要输入参数";
            logger.error("{}: {}", errorMsg, e.getMessage(), e);
            throw new BusinessException(ErrorCode.PARAM_MISSING, e);
        } catch (Exception e) {
            String errorMsg = String.format("调用大模型服务时发生未知错误: %s", e.getMessage());
            logger.error(errorMsg, e);
            throw new SystemException(ErrorCode.INTERNAL_SERVER_ERROR, errorMsg, e);
        }

        if (result.getOutput() == null) {
            String errorMsg = "从大模型API收到无效响应";
            logger.error(errorMsg);
            throw BusinessException.of(ErrorCode.BUSINESS_ERROR, errorMsg);
        }

        String output = result.getOutput().getText().trim();
        String[] lines = output.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                objectMapper.readTree(line);
            } catch (Exception e) {
                throw BusinessException.of(
                        ErrorCode.PARAM_FORMAT_ERROR,
                        String.format("第 %d 行不是合法 JSON: %s", i + 1, line)
                );
            }
        }

        return output;
    }

}