package online.nonamekill.android.module;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Objects;

import online.nonamekill.android.module.icon.ModuleIcon;
import online.nonamekill.common.module.BaseModule;

public class ModuleManager {
    private final Activity mActivity;
    private final ArrayList<BaseModule> mModules = new ArrayList<>();
    private BaseModule mCurrentModule = null;

    public ModuleManager(Activity activity) {
        mActivity = activity;
        mModules.add(new ModuleIcon());
    }

    public void onPause() {
        for (BaseModule module : mModules) {
            module.onPause();
        }
    }

    public void onResume() {
        for (BaseModule module : mModules) {
            module.onResume();
        }
    }

    public void onDestroy() {
        for (BaseModule module : mModules) {
            module.onDestroy();
        }
    }

    public void onCreate() {
        for (BaseModule module : mModules) {
            module.onCreate(mActivity);
        }
    }

    public ArrayList<String> getModeNameList() {
        ArrayList<String> list = new ArrayList<>();

        for (BaseModule module : mModules) {
            String name = module.getName();

            if (name != null) {
                list.add(name);
            }
        }

        return list;
    }

    public BaseModule getModules(String module) {
        BaseModule target = null;

        for (BaseModule m : mModules) {
            if (Objects.equals(module, m.getName())) {
                target = m;
                break;
            }
        }

        return target;
    }

    public boolean checkToChangeModule(BaseModule module) {
        if ((null == module) && (null == mCurrentModule)) {
            return false;
        }

        return (module != mCurrentModule);
    }

    public void doChange(BaseModule module) {
        mCurrentModule = module;
    }
}
