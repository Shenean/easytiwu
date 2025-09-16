package com.easytiwu.serviceupload.service.impl;

import java.io.InputStream;

/**
 * 文本提取策略接口
 * @author sheny
 */
public interface TextExtractor {

    /**
     * 判断当前策略是否支持该文件名（或扩展名）
     */
    boolean supports(String filename);

    /**
     * 从输入流中提取文本内容
     */
    String extract(InputStream in) throws Exception;
}