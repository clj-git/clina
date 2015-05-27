package org.clina.core.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.IOException;

/**
 * Created by zjh on 15-5-21.
 */
public class StringUtil {
    public static String detectEncoding(byte[] content) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(content, 0, content.length);
        detector.dataEnd();
        String result = detector.getDetectedCharset();
        if (result == null) {
            return "UTF-8";
        } else {
            return result;
        }
    }

    /**
     * Make string from byte array. Character encoding is detected automatically by [[util.StringUtil.detectEncoding]].
     * And if given bytes contains UTF-8 BOM, it's removed from returned string.
     */
    public static String convertFromByteArray(byte[] content) {
        try {
            return IOUtils.toString(new BOMInputStream(new java.io.ByteArrayInputStream(content)), detectEncoding(content));
        } catch (IOException e) {
            e.printStackTrace();
            return "UTF-8";
        }
    }
}
