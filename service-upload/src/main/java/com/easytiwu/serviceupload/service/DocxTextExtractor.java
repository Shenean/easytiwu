package com.easytiwu.serviceupload.service;

import com.easytiwu.serviceupload.service.impl.TextExtractor;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.P;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
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
            StringBuilder sb = new StringBuilder();

            // 遍历段落
            List<Object> paragraphs = mainPart.getContent();
            for (Object obj : paragraphs) {
                appendParagraphText(obj, sb);
            }

            return sb.toString().trim();
        } catch (Exception e) {
            throw new IOException("DOCX文本提取失败", e);
        }
    }

    /**
     * 递归解析段落和表格
     */
    private void appendParagraphText(Object obj, StringBuilder sb) {
        if (obj instanceof P) {
            P paragraph = (P) obj;
            List<Object> texts = paragraph.getContent();
            for (Object tObj : texts) {
                if (tObj instanceof Text) {
                    sb.append(((Text) tObj).getValue());
                } else {
                    appendParagraphText(tObj, sb);
                }
            }
            sb.append("\n");
        } else if (obj instanceof Tr) {
            Tr row = (Tr) obj;
            List<Object> cells = row.getContent();
            for (Object cellObj : cells) {
                appendParagraphText(cellObj, sb);
                sb.append("\t"); // 单元格分隔
            }
            sb.append("\n");
        } else if (obj instanceof Tc) { // 表格单元格
            Tc cell = (Tc) obj;
            List<Object> cellContent = cell.getContent();
            for (Object cObj : cellContent) {
                appendParagraphText(cObj, sb);
            }
        } else if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                appendParagraphText(o, sb);
            }
        }
        // 其他类型可根据需要继续扩展
    }
}
