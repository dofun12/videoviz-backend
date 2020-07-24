package org.lemanoman.videoviz;

import java.util.concurrent.ExecutorService;

public class Constants {
    public final static String API_BASE_URL = "/api";
    public final static String MP4_BASE_PATH = "/var/www/html/v1/mp4";
    public final static int MAX_THREADS = 3;
    public static ExecutorService EXECUTOR_SERVICE = null;
}
