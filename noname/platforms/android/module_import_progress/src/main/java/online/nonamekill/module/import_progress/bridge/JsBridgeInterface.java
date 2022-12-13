package online.nonamekill.module.import_progress.bridge;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class JsBridgeInterface {
    public static final String ROOT_URI = "file:///android_asset/html/start.html";
    private static final String CALL_TAG = "jsBridge";

    private static final String CONFIG_PREFIX = "noname_0.9_";

    private final OnJsBridgeCallback jsBridgeCallback;

    public JsBridgeInterface(Context context, OnJsBridgeCallback callback) {
        jsBridgeCallback = callback;
    }

    @JavascriptInterface
    public void onAddExtension(boolean isAdd,String extname){
        if (null != jsBridgeCallback) {
            jsBridgeCallback.onAddExtension(isAdd, extname);
        }
    }

    @JavascriptInterface
    public void onEnableExtension(String extname,boolean isEnable){
        if (null != jsBridgeCallback) {
            jsBridgeCallback.onEnableExtension(extname,isEnable);
        }
    }

    @JavascriptInterface
    public void onRemoveExtension(boolean isRemove, String extname){
        if (null != jsBridgeCallback) {
            jsBridgeCallback.onRemoveExtension(isRemove, extname);
        }
    }

    public String getCallTag() {
        return CALL_TAG;
    }

    public interface OnJsBridgeCallback {
        void onAddExtension(boolean isAdd, String extname);

        void onRemoveExtension(boolean isRemove, String extname);

        void onEnableExtension(String extname,Boolean isEnable);
    }

}
