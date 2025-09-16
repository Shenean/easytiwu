package com.easytiwu.serviceupload.service;

import com.easytiwu.serviceupload.service.impl.TextExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @author sheny
 */
@Component
public class DocxTextExtractor implements TextExtractor {

    @Override
    public boolean supports(String filename) {
        return filename.toLowerCase().endsWith(".docx");
    }

    @Override
    public String extract(InputStream in) throws Exception {
        try (XWPFDocument docx = new XWPFDocument(in);
             XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
            return extractor.getText();
        }
    }
}