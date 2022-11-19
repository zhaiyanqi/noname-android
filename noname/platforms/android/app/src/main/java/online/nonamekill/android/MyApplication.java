package online.nonamekill.android;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {

    private static ExecutorService threadPool = null;

    @Override
    public void onCreate() {
        super.onCreate();
        threadPool = Executors.newFixedThreadPool(3);
    }

    public static ExecutorService getThreadPool() {
        return threadPool;
    }
}
