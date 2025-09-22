package com.easytiwu.serviceupload.service;

import com.easytiwu.serviceupload.service.impl.TextExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * @author sheny
 */
@Service
public class FileParsingService {

    // Spring 自动注入所有 TextExtractor 实现类
    private final List<TextExtractor> extractors;

    /**
     * 构造函数注入
     */
    @Autowired
    public FileParsingService(List<TextExtractor> extractors) {
        this.extractors = extractors;
    }

    /**
     * 判断是否支持该文件
     */
    public boolean isSupportedFile(String filename) {
        String lowerName = filename.toLowerCase();
        return extractors.stream()
                .anyMatch(extractor -> extractor.supports(lowerName));
    }

    /**
     * 提取文本内容，自动选择合适的提取器
     */
    public String extractText(MultipartFile file) throws IOException {
        String filename = Objects.requireNonNull(file.getOriginalFilename());
        try (InputStream in = file.getInputStream()) {
            for (TextExtractor extractor : extractors) {
                if (extractor.supports(filename)) {
                    try {
                        return extractor.extract(in);
                    } catch (Exception e) {
                        throw new IOException("文本提取失败: " + filename, e);
                    }
                }
            }
            throw new IOException("不支持的文件格式: " + filename);
        }
    }
}