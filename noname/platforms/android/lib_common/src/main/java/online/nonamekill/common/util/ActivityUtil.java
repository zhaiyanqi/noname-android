package online.nonamekill.common.util;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ActivityUtil {

    /**
     * 隐藏导航条
     * @param window 传入 activity 所属的 window 对象
     */
    public static void hideNavigationBar(Window window) {
        if (null != window) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
            window.setAttributes(params);
        }
    }

}
