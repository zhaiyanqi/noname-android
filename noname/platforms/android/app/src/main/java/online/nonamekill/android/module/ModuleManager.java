package online.nonamekill.android.module;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;

import java.util.ArrayList;
import java.util.Objects;

import online.nonamekill.android.module.about.ModuleAbout;
import online.nonamekill.android.module.icon.ModuleIcon;
import online.nonamekill.android.module.qqfile.ModuleQQFile;
import online.nonamekill.android.module.server.function.ModuleServer;
import online.nonamekill.android.module.version.ModuleVersion;
import online.nonamekill.common.module.BaseModule;
import online.nonamekill.module.import_progress.ImportProgress;

public class ModuleManager {
    private final Activity mActivity;
    private final ArrayList<BaseModule> mModules = new ArrayList<>();
    private BaseModule mCurrentModule = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ModuleManager(Activity activity) {
        mActivity = activity;
        mModules.add(new ModuleIcon());
        mModules.add(new ModuleQQFile());
        mModules.add(new ModuleVersion());
        mModules.add(new ModuleServer());
        mModules.add(new ImportProgress());
        mModules.add(new ModuleAbout());
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        for (BaseModule m : mModules) {
            m.onActivityResult(requestCode, resultCode, intent);
        }
    }
}
