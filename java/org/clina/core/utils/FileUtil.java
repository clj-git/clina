package org.clina.core.utils;

import java.io.File;
import java.io.IOException;
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

    public static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
                System.out.println("Directory is deleted : " + file.getAbsolutePath());
            } else {
                String files[] = file.list();
                for (String temp : files) {
                    File fileDelete = new File(file, temp);
                    delete(fileDelete);
                }
                if (file.list().length == 0) {
                    file.delete();
                    System.out.println("Directory is deleted : " + file.getAbsolutePath());
                }
            }
        } else {
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }
}
