package com.easytiwu.serviceupload.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author sheny
 */
@Service
public class FileProcessService {

    /**
     * 提取文件文本内容
     *
     * @param file 上传的文件
     * @return 文件中的文本内容
     * @throws IOException IO异常
     */
    public String extractTextFromFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("文件名不能为空");
        }

        String fileExtension = getFileExtension(fileName).toLowerCase();

        switch (fileExtension) {
            case ".pdf":
                return extractTextFromPDF(file);
            case ".docx":
                return extractTextFromDOCX(file);
            case ".txt":
                return extractTextFromTXT(file);
            default:
                throw new IOException("不支持的文件格式: " + fileExtension);
        }
    }

    /**
     * 从PDF文件中提取文本
     *
     * @param file PDF文件
     * @return 提取的文本内容
     * @throws IOException IO异常
     */
    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * 从DOCX文件中提取文本
     *
     * @param file DOCX文件
     * @return 提取的文本内容
     * @throws IOException IO异常
     */
    private String extractTextFromDOCX(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {
            return document.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .filter(text -> text != null && !text.trim().isEmpty())
                    .collect(Collectors.joining("\n"));
        }
    }

    /**
     * 从TXT文件中提取文本
     *
     * @param file TXT文件
     * @return 提取的文本内容
     * @throws IOException IO异常
     */
    private String extractTextFromTXT(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines()
                    .collect(Collectors.joining("\n"));
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }
}