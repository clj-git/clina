package org.clina.core.utils;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * Created by zjh on 15-5-21.
 */
public class FileUtil {
    public static String getMimeType(String name) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(name);
        if (mimeType == null) {
            return "application/octet-stream";
        } else {
            return mimeType;
        }
    }

    public static boolean isText(byte[] content) {
        return !Arrays.asList(content).contains(0);
    }

    public static boolean isLarge(Long size) {
        return (size > 1024 * 1000);
    }

    public static boolean isImage(String name) {
        return getMimeType(name).startsWith("image/");
    }

    public static String getContentType(String name, byte[] bytes) {
        String mimeType = getMimeType(name);
        if (mimeType.equals("application/octet-stream") && isText(bytes)) {
            return "text/plain";
        } else {
            return mimeType;
        }
    }
}
