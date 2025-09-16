// src/main/java/com/easytiwu/serviceupload/util/CharsetDetector.java

package com.easytiwu.serviceupload.util;

import org.mozilla.universalchardet.UniversalDetector;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author sheny
 */
public class CharsetDetector {

    // 私有构造函数，防止实例化
    private CharsetDetector() {
    }

    /**
     * 自动检测字节数组的字符编码
     * @param bytes 文件字节内容
     * @return 推荐的 Charset，若无法识别则返回 UTF-8
     */
    public static Charset detectCharset(byte[] bytes) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        detector.reset();

        if (encoding != null) {
            try {
                return Charset.forName(encoding);
            } catch (Exception e) {
                // 忽略非法编码，回退到 UTF-8
            }
        }

        // 默认回退到 UTF-8
        return StandardCharsets.UTF_8;
    }
}