package org.lemanoman.videoviz;

import java.io.File;
import java.util.concurrent.ExecutorService;

public class Constants {
    public final static String API_BASE_URL = "/api";
    public final static String MEDIA_BASE_URL = "/media";
    public final static String MP4_BASE_PATH = "/var/www/html/v1/mp4";
    public final static int MAX_THREADS = 3;

    public static ExecutorService EXECUTOR_SERVICE = null;

    public static String getBaseMP4Location(String basePath) {
        File dir = new File(basePath);
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(basePath);
        stringBuilder.append(File.separator);
        stringBuilder.append("mp4");
        return stringBuilder.toString();
    }

    public static String getBaseImageLocation(String basePath) {
        File dir = new File(basePath);
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(basePath);
        stringBuilder.append(File.separator);
        stringBuilder.append("img");
        return stringBuilder.toString();
    }

    public static String getImageFile(String basePath, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getBaseImageLocation(basePath));
        stringBuilder.append(File.separator);
        stringBuilder.append(fileName);
        return stringBuilder.toString();
    }

    public static String getVideoFile(String basePath, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getBaseMP4Location(basePath));
        stringBuilder.append(File.separator);
        stringBuilder.append(fileName);
        return stringBuilder.toString();

    }

}
