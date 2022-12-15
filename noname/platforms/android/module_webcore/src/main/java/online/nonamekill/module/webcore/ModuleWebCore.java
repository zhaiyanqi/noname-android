package online.nonamekill.module.webcore;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.util.HashMap;

import online.nonamekill.common.data.DataKey;
import online.nonamekill.common.data.DataManager;
import online.nonamekill.common.module.BaseModule;

public class ModuleWebCore extends BaseModule {

    public static void init(Context context) {

        if (DataManager.getInstance().getValue(DataKey.KEY_IS_X5_CORE)) {
            QbSdk.setDownloadWithoutWifi(true);
            // 在调用TBS初始化、创建WebView之前进行如下配置
            HashMap<String, Object> map = new HashMap<>();
            map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
            map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
            QbSdk.initTbsSettings(map);

            QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
                @Override
                public void onCoreInitFinished() {
                }

                @Override
                public void onViewInitFinished(boolean isX5) {
                }
            });
        }
    }

    @Override
    public View getView(Context context) {
        return super.getView(context);
    }

    @Override
    public String getName() {
        return "X5内核";
    }
}
