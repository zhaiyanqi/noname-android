package online.nonamekill.android;

import android.app.Application;

import online.nonamekill.common.data.DataManager;
import online.nonamekill.common.util.AppUtils;
import online.nonamekill.common.util.ThreadUtil;
import online.nonamekill.module.webcore.ModuleWebCore;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化AppUtils类
        AppUtils.initialize(this);
        // 初始化MMKV
        DataManager.getInstance().initialize(this);
        // 初始化线程池
        ThreadUtil.init();

        // 初始化x5内核
        ModuleWebCore.init(this);
    }
}
