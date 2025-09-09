package com.easytiwu.serviceupload.controller;

import com.alibaba.fastjson2.JSON;
import com.easytiwu.serviceupload.dto.UploadRequestDTO;
import com.easytiwu.serviceupload.service.FileProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sheny
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    private final FileProcessService fileProcessService;
    
    @Value("${llm.service.url:http://localhost:8080/api/llm/process}")
    private String llmServiceUrl;

    public UploadController(FileProcessService fileProcessService) {
        this.fileProcessService = fileProcessService;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processFile(UploadRequestDTO request) {
        try {
            // 记录请求信息
            logger.info("开始处理文件上传请求，题库名称: {}", request.getBankName());
            
            // 检查文件是否存在
            MultipartFile file = request.getFile();
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("文件不能为空");
            }

            // 提取文件文本内容
            String fileContent = fileProcessService.extractTextFromFile(file);
            logger.info("成功提取文件内容，长度: {}", fileContent.length());

            // 调用大模型处理服务
            String llmResponse = callLLMService(request.getBankName(), request.getDescription(), fileContent);
            
            // 调用数据导入服务
            String importResult = callImportService(llmResponse);
            
            Map<String, String> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "文件处理完成");
            result.put("importResult", importResult);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("处理文件时发生错误", e);
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "处理文件时发生错误: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 调用大模型处理服务
     *
     * @param bankName 题库名称
     * @param description 描述
     * @param content 文件内容
     * @return 大模型处理结果
     */
    private String callLLMService(String bankName, String description, String content) {
        try {
            logger.info("调用大模型处理服务: {}", llmServiceUrl);
            
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("bankName", bankName);
            requestBody.put("description", description);
            requestBody.put("content", content);
            
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(requestBody), headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(llmServiceUrl, entity, String.class);
            
            logger.info("大模型处理服务调用成功，状态码: {}", response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            logger.error("调用大模型处理服务时发生错误", e);
            throw new RuntimeException("调用大模型处理服务失败: " + e.getMessage());
        }
    }

    /**
     * 调用数据导入服务
     *
     * @param jsonData 大模型处理后的JSON数据
     * @return 导入结果
     */
    private String callImportService(String jsonData) {
        try {
            logger.info("调用数据导入服务");
            
            // 这里应该调用实际的数据导入服务
            // 暂时模拟返回成功结果
            return "数据导入成功";
        } catch (Exception e) {
            logger.error("调用数据导入服务时发生错误", e);
            throw new RuntimeException("调用数据导入服务失败: " + e.getMessage());
        }
    }
}