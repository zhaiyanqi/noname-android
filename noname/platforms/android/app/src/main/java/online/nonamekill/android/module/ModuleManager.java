package online.nonamekill.android.module;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import online.nonamekill.android.module.about.ModuleAbout;
import online.nonamekill.android.module.icon.ModuleIcon;
import online.nonamekill.android.module.qqfile.ModuleQQFile;
import online.nonamekill.android.module.server.function.ModuleServer;
import online.nonamekill.android.module.version.ModuleVersion;
import online.nonamekill.common.module.BaseModule;
import online.nonamekill.common.util.ThreadUtil;
import online.nonamekill.module.import_progress.ImportProgress;

public class ModuleManager implements LifecycleEventObserver {

    private static final int MSG_MODULE_PRE_CREATED = 100;

    private final Activity mActivity;
    private final ArrayList<BaseModule> mModules = new ArrayList<>();

    private Runnable mPreCreateCallback = null;
    private BaseModule mCurrentModule = null;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_MODULE_PRE_CREATED: {
                    Optional.ofNullable(msg.obj)
                            .map(name -> getModules((String) name))
                            .filter(module -> (module == mCurrentModule))
                            .ifPresent(module -> {
                                if (null != mPreCreateCallback) {
                                    mPreCreateCallback.run();
                                    mPreCreateCallback = null;
                                }
                            });

                    break;
                }

                default:
                    break;
            }
        }
    };

    public ModuleManager(AppCompatActivity activity) {
        mActivity = activity;
        mModules.add(new ModuleIcon());
        mModules.add(new ModuleQQFile());
        mModules.add(new ModuleVersion());
        mModules.add(new ModuleServer());
        mModules.add(new ImportProgress());
        mModules.add(new ModuleAbout());
        activity.getLifecycle().addObserver(this);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_CREATE:
                onCreate();
                break;
            case ON_RESUME:
                onResume();
                break;
            case ON_PAUSE:
                onPause();
                break;
            case ON_DESTROY:
                onDestroy();
                break;
            default:
                break;
        }
    }

    private void onCreate() {
        mModules.forEach(m -> {
            // 异步线程执行初始化任务
            ThreadUtil.execute(() -> {
                m.onPreCreate();
                m.finishPreCreate();

                // 任务结束，通知UI
                Message message = mHandler.obtainMessage(MSG_MODULE_PRE_CREATED);
                message.obj = m.getName();
                mHandler.sendMessage(message);
            });

            m.onCreate(mActivity);
        });
    }

    private void onResume() {
        mModules.forEach(BaseModule::onResume);
    }

    private void onPause() {
        mModules.forEach(BaseModule::onPause);
    }

    private void onDestroy() {
        mModules.forEach(BaseModule::onDestroy);
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

    public void setPreCreateCallBack(Runnable runnable) {
        mPreCreateCallback = runnable;
    }
}
