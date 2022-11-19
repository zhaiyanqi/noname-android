package online.nonamekill.android;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import online.nonamekill.common.util.ThreadUtil;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ThreadUtil.init();
    }
}
