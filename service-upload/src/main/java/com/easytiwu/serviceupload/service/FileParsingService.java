package com.easytiwu.serviceupload.service;

import com.easytiwu.serviceupload.util.CharsetDetector;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author sheny
 */
@Service
public class FileParsingService {

    public boolean isSupportedFile(String filename) {
        String fname = filename.toLowerCase();
        return fname.endsWith(".pdf") || fname.endsWith(".docx") || fname.endsWith(".txt");
    }

    /**
     * Extracts text content from the given file. Supports PDF, Word (.docx), and TXT.
     */
    public String extractText(MultipartFile file) throws IOException {
        String fname = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
        try (InputStream in = file.getInputStream()) {
            if (fname.endsWith(".pdf")) {
                // Use PDFBox to extract text from PDF
                byte[] pdfBytes = in.readAllBytes();
                try (PDDocument pdfDoc = Loader.loadPDF(pdfBytes)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(pdfDoc);
                }
            } else if (fname.endsWith(".docx")) {
                // Use POI XWPF for .docx files
                try (XWPFDocument docx = new XWPFDocument(in);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
                    return extractor.getText();
                }
            } else if (fname.endsWith(".txt")) {
                // 读取全部字节，自动检测编码
                byte[] bytes = in.readAllBytes();
                Charset charset = CharsetDetector.detectCharset(bytes);
                return new String(bytes, charset);
            } else {
                throw new IOException("Unsupported file format: " + fname);
            }
        }
    }
}