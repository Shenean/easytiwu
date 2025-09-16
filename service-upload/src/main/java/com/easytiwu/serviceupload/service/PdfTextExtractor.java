package com.easytiwu.serviceupload.service;

import com.easytiwu.serviceupload.service.impl.TextExtractor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author sheny
 */
@Component
public class PdfTextExtractor implements TextExtractor {

    @Override
    public boolean supports(String filename) {
        return filename.toLowerCase().endsWith(".pdf");
    }

    @Override
    public String extract(InputStream in) throws Exception {
        // 创建临时文件
        Path tempFile = Files.createTempFile("pdf-", ".tmp");
        try {
            // 将输入流复制到临时文件
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            // 使用基于文件的 RandomAccessRead
            try (RandomAccessReadBufferedFile rarb = new RandomAccessReadBufferedFile(tempFile.toFile());
                 PDDocument pdfDoc = Loader.loadPDF(rarb)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(pdfDoc);
            }
        } finally {
            // 清理临时文件
            Files.deleteIfExists(tempFile);
        }
    }
}