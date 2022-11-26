package online.nonamekill.android.module;

import android.app.Activity;

import java.util.ArrayList;

import online.nonamekill.common.module.BaseModule;

public class ModuleManager {
    private final Activity mActivity;
    private final ArrayList<BaseModule> mModules = new ArrayList<>();

    public ModuleManager(Activity activity) {
        mActivity = activity;
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onDestroy() {

    }

    public void onCreate() {

    }

    public ArrayList<String> getModeNameList() {
        ArrayList<String> list = new ArrayList<>();

        for (BaseModule module : mModules) {
            String name = module.getName();

            if (name != null) {
                list.add(name);
            }
        }

        // todo
        list.add("测试");
        list.add("测试");

        return list;
    }
}
