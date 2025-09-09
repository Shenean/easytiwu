package com.easytiwu.serviceupload.util;

import com.alibaba.fastjson2.JSONObject;

/**
 * @author sheny
 */
public class ValidateJson {
    
    // 私有构造函数，防止实例化
    private ValidateJson() {
        throw new IllegalStateException("Utility class");
    }
    
    public static void ensureQuestion(JSONObject q, int idx) {
        String type = q.getString("type");
        String qt = q.getString("question_text");
        String ca = q.getString("correct_answer");
        if (type == null || qt == null || ca == null) {
            throw new IllegalArgumentException("第 " + idx + " 个题目缺少必要字段(type/question_text/correct_answer)");
        }
        // 基本类型校验
        if (!("single".equals(type) || "multiple".equals(type) || "true_false".equals(type)
                || "fill_blank".equals(type) || "short_answer".equals(type))) {
            throw new IllegalArgumentException("第 " + idx + " 个题目 type 非法: " + type);
        }
    }
}