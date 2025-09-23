package com.easytiwu.serviceupload.service;

import com.easytiwu.serviceupload.service.impl.TextExtractor;
import jakarta.xml.bind.JAXBElement;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Text;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * DOCX文本提取器
 * @author sheny
 */
@Component
public class DocxTextExtractor implements TextExtractor {

    @Override
    public boolean supports(String filename) {
        return filename != null && filename.toLowerCase().endsWith(".docx");
    }

    @Override
    public String extract(InputStream in) throws IOException {
        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(in);
            MainDocumentPart mainPart = wordMLPackage.getMainDocumentPart();
            
            // 使用XPath表达式直接获取所有文本节点
            String textNodesXPath = "//w:t";
            List<Object> textNodes = mainPart.getJAXBNodesViaXPath(textNodesXPath, true);
            
            StringBuilder sb = new StringBuilder();
            // 遍历所有文本节点并拼接文本内容
            for (Object obj : textNodes) {
                if (obj instanceof JAXBElement) {
                    Object value = ((JAXBElement<?>) obj).getValue();
                    if (value instanceof Text) {
                        String textValue = ((Text) value).getValue();
                        sb.append(textValue);
                    }
                }
            }

            return sb.toString().trim();
        } catch (Exception e) {
            throw new IOException("DOCX文本提取失败", e);
        }
    }
}