package online.nonamekill.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {

    private static ExecutorService threadPool = null;


    public static void init() {
        threadPool = Executors.newFixedThreadPool(3);
    }

    public static void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }
}
