package com.easytiwu.serviceupload.service;

import com.easytiwu.serviceupload.service.impl.TextExtractor;
import com.easytiwu.serviceupload.util.CharsetDetector;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author sheny
 */
@Component
public class TxtTextExtractor implements TextExtractor {

    @Override
    public boolean supports(String filename) {
        return filename.toLowerCase().endsWith(".txt");
    }

    @Override
    public String extract(InputStream in) throws Exception {
        byte[] bytes = in.readAllBytes();
        Charset charset = CharsetDetector.detectCharset(bytes);
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        return new String(bytes, charset);
    }
}