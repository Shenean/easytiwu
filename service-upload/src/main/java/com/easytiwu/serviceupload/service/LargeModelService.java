package com.easytiwu.serviceupload.service;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
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
 * 调用 Model Studio 的 Application（智能体）将题目文本转为 JSON
 * @author sheny
 */
@Service
public class LargeModelService {
    private static final Logger logger = LoggerFactory.getLogger(LargeModelService.class);
    private static final String QWEN_CB = "qwenService";

    private final Application applicationClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${llm.dashscope.api-key}")
    private String dashscopeApiKey;

    // Model Studio 中创建的 Application ID（必需）
    @Value("${llm.dashscope.app-id}")
    private String dashscopeAppId;

    @Value("${llm.dashscope.read-timeout:300}")
    private int readTimeoutSeconds;

    @Value("${llm.dashscope.connect-timeout:120}")
    private int connectTimeoutSeconds;

    public LargeModelService() {
        this.applicationClient = new Application();
    }

    @PostConstruct
    public void initializeConnectionPool() {
        try {
            configureConnectionPool(connectTimeoutSeconds, readTimeoutSeconds);
        } catch (Exception e) {
            logger.error("Failed to initialize connection pool: {}", e.getMessage(), e);
            logger.error("Connection pool initialization failed with error: {}, StackTrace: {}",
                    e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
    }

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
    public String generateQuestionsJson(String textContent) throws ApiException {
        String systemPrompt = loadSystemPrompt();
        Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(textContent).build();

        ApplicationParam param = ApplicationParam.builder()
                .apiKey(dashscopeApiKey)
                .appId(dashscopeAppId)
                .messages(Arrays.asList(systemMsg, userMsg))
                .build();

        logger.info("Calling Model Studio Application (agent) with DashScope...");
        ApplicationResult result;
        try {
            result = applicationClient.call(param);
        } catch (ApiException e) {
            String errorMsg = String.format("调用智能体Application API失败: %s", e.getMessage());
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
            String errorMsg = String.format("调用智能体服务时发生未知错误: %s", e.getMessage());
            logger.error(errorMsg, e);
            throw new SystemException(ErrorCode.INTERNAL_SERVER_ERROR, errorMsg, e);
        }

        if (result.getOutput() == null) {
            String errorMsg = "从智能体Application收到无效响应";
            logger.error(errorMsg);
            throw BusinessException.of(ErrorCode.BUSINESS_ERROR, errorMsg);
        }

        String output = result.getOutput().getText().trim();
        String[] lines = output.split("\n");

        if (lines.length > 100) {
            String errorMsg = String.format("智能体返回的数据超过100行限制，实际行数: %d", lines.length);
            logger.error(errorMsg);
            throw BusinessException.of(ErrorCode.BUSINESS_ERROR, errorMsg);
        }

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

    private String fallbackQuestionsJson(String textContent, Throwable t) {
        logger.warn("Fallback triggered for textContent: {}, reason: {}", textContent, t == null ? "null" : t.getMessage());
        return "[]";
    }
}
