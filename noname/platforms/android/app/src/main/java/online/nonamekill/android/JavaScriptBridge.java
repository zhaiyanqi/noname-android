package online.nonamekill.android;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import java.io.File;

import online.nonamekill.common.Constant;
import online.nonamekill.common.data.DataKey;
import online.nonamekill.common.data.DataManager;

public class JavaScriptBridge {
    public static final String TAG = "JavaScriptBridge";
    public static final String JS_PARAMS = "jsBridge";
    private static String gamePath = null;

    public static void setGamePath(String gamePath){
        JavaScriptBridge.gamePath = gamePath;
    }


    private final Context mContext;

    public JavaScriptBridge(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public String getGamePath() {
        File rootFiles = null;

        if (null != mContext && TextUtils.isEmpty(gamePath)) {
            rootFiles = mContext.getExternalFilesDir(Constant.GAME_FOLDER);
            String path = rootFiles.getAbsolutePath();
            setGamePath(path);
            DataManager.getInstance().setValue(DataKey.KEY_GAME_PATH, path);
        }else {
            rootFiles = new File(gamePath);
        }

        return Uri.fromFile(rootFiles).toString() + File.separator;
    }
}
