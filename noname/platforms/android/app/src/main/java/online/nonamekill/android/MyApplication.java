package online.nonamekill.android;

import android.app.Application;

import online.nonamekill.common.data.DataManager;
import online.nonamekill.common.util.AppUtils;
import online.nonamekill.common.util.ThreadUtil;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化AppUtils类
        AppUtils.initialize(this);

        DataManager.getInstance().initialize(this);
        ThreadUtil.init();
    }
}
