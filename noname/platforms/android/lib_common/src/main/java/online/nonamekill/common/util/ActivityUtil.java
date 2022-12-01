package online.nonamekill.common.util;

import android.graphics.Color;
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

    /**
     * 隐藏系统UI
     * @param window 传入 activity 所属的 window 对象
     */
    public static void hideSystemUI(Window window) {
        if (window != null) {
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

}
