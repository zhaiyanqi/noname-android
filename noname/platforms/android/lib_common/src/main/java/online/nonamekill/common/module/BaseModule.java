package online.nonamekill.common.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.CallSuper;

import java.util.Optional;

import online.nonamekill.common.Constant;

public class BaseModule {
    private Activity mActivity = null;
    private WebView mWebView = null;

    private final Object mCreatingLock = new Object();
    private boolean mbPreCreating = false;

    public void onCreate(Activity activity) {
        mActivity = activity;
        mWebView = mActivity.findViewById(Constant.WEB_VIEW_ID);
    }

    /**
     * 异步初始化线程，提前初始化耗时资源
     * 请勿在非UI线程执行UI操作，不能做UI操作，例如WebView初始化，可以初始化布局、加载字体、加载资源、初始化文件等
     */
    @CallSuper
    public void onPreCreate() {
        updateCreatingState(true);
    }

    private void updateCreatingState(boolean creating) {
        synchronized (mCreatingLock) {
            mbPreCreating = creating;
        }
    }

    public boolean isPreCreating() {
        synchronized (mCreatingLock) {
            return mbPreCreating;
        }
    }

    /**
     * 初始化完成
     */
    @CallSuper
    public void finishPreCreate() {
        updateCreatingState(false);
    }

    public void onResume() {
    }

    /**
     * 当module view可见时回调
     */
    public void onVisible() {

    }

    /**
     * 当module view不可见时回调
     */
    public void onInVisible() {

    }

    public void onPause() {
    }

    public void onDestroy() {
    }

    protected WebView getWebView() {
        return mWebView;
    }

    protected void exec(String cmd) {
        Optional.ofNullable(getWebView()).ifPresent(webView -> webView.loadUrl("javascript:console.log('me: ' + window.lib)"));
    }

    protected Context getContext() {
        return mActivity;
    }

    protected Activity getActivity() {
        return mActivity;
    }

    public View getView(Context context) {
        return null;
    }

    public String getName() {
        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }

    public void onCreateView(View view) {

    }
}
