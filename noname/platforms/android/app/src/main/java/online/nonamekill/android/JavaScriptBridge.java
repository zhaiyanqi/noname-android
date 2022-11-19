package online.nonamekill.android;

import android.content.Context;
import android.net.Uri;
import android.webkit.JavascriptInterface;

import java.io.File;

import online.nonamekill.common.Constant;

public class JavaScriptBridge {
    public static final String TAG = "JavaScriptBridge";
    public static final String JS_PARAMS = "jsBridge";


    private final Context mContext;

    public JavaScriptBridge(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public String getGamePath() {
        File rootFiles = null;

        if (null != mContext) {
            rootFiles = mContext.getExternalFilesDir(Constant.GAME_FOLDER);
        }

        return Uri.fromFile(rootFiles).toString() + File.separator;
    }
}
