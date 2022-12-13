package online.nonamekill.module.import_progress.bridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.xiaoleilu.hutool.util.StrUtil;

public class BridgeHelper {
    private static final String JS_PREFIX = "javascript:";
    private static final String JS_GET_EXTENSIONS = "javascript:app.getExtensions();";

    private final WebView webView;
    private JavaBridgeInterface javaBridge;
    private final JsBridgeInterface.OnJsBridgeCallback jsBridgeCallback;

    public BridgeHelper(WebView webView, JsBridgeInterface.OnJsBridgeCallback callback) {
        this.webView = webView;
        jsBridgeCallback = callback;

        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        javaBridge = new JavaBridgeInterface(webView);

        webView.setInitialScale(0);
        webView.setVerticalScrollBarEnabled(false);
        final WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        settings.setAllowFileAccess(true);

        //We don't save any form data in the application
        settings.setSaveFormData(false);
        settings.setSavePassword(false);

        // Jellybean rightfully tried to lock this down. Too bad they didn't give us a whitelist
        // while we do this
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        // Enable database
        // We keep this disabled because we use or shim to get around DOM_EXCEPTION_ERROR_16
        String dir = "database";
        String databasePath = webView.getContext().getApplicationContext().getDir(dir, Context.MODE_PRIVATE).getPath();
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(databasePath);
        settings.setGeolocationDatabasePath(databasePath);
        settings.setDomStorageEnabled(true);

        settings.setGeolocationEnabled(true);
        settings.setAppCachePath(databasePath);
        settings.setAppCacheEnabled(true);
        JsBridgeInterface jsBridge = new JsBridgeInterface(webView.getContext(), jsBridgeCallback);
        webView.addJavascriptInterface(jsBridge, jsBridge.getCallTag());

        webView.loadUrl(JsBridgeInterface.ROOT_URI);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    public void getExtensions() {
        if (null != webView) {
            webView.post(()-> javaBridge.callJs(JS_GET_EXTENSIONS));
        }
    }

    public void getExtensionState(String extName) {
        if (null != webView) {
            webView.post(()-> javaBridge.callFun("getExtensionState",
                    "'" + extName + "'"));
        }
    }

    public void enableExtension(String extName, boolean enable) {
        if (null != webView) {
            webView.post(() -> javaBridge.callFun("enableExtension",
                    "'" + extName + "'", enable));
        }
    }

    public void addExtension(String extName) {
        System.out.println("extName = " + extName);
        addExtension(extName,true);
    }

    public void addExtension(String extName,boolean enable) {
        if (null != webView) {
            javaBridge.callFun("addExtension",
                    "'" + extName + "'", enable);
        }
    }

    public void removeExtension(String extName) {
        if (null != webView) {
            webView.post(() -> javaBridge.callFun("removeExtension", "'" + extName + "'"));
        }
    }

    public void setServerIp(String ip) {
        if (null != webView) {
            webView.post(() -> javaBridge.callFun("setServerIp",
                    "'" + ip + "'"));
        }
    }

    public void setServerIp(String ip, boolean directStart) {
        if (null != webView) {
            webView.post(() -> javaBridge.callFun("setServerIp",
                    "'" + ip + "'", directStart));
        }
    }
}



class JavaBridgeInterface {
    private static final String JS_PREFIX = "javascript:app.";

    private final WebView webView;

    public JavaBridgeInterface(@NonNull WebView webView) {
        this.webView = webView;
    }

    public void callJs(String js) {
        webView.loadUrl(js);
    }

    public void callFun(String funName, Object... param) {
        if (StrUtil.isNotEmpty(funName)) {
            return;
        }

        String url = toParams(funName, param);
        Log.v("JavaBridgeInterface", "callFun: " + url);
        webView.loadUrl(url);
    }

    public String toParams(String funName, Object... params) {
        StringBuilder b = new StringBuilder();
        b.append(JS_PREFIX).append(funName);

        if (params == null)
            return b.append("();").toString();

        int iMax = params.length - 1;
        if (iMax == -1)
            return b.append("();").toString();

        b.append('(');

        for (int i = 0; ; i++) {
            b.append(params[i]);

            if (i == iMax){
                return b.append(");").toString();
            }

            b.append(", ");
        }
    }
}
