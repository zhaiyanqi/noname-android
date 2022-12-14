package online.nonamekill.common.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.CallSuper;

import java.util.Optional;

import online.nonamekill.common.Constant;
import online.nonamekill.common.protocol.IWebView;
import online.nonamekill.common.util.ShareUtils;

public class BaseModule {

    // 注意变量顺序，修饰符多的，可见性高的放上面
    protected final Handler mHandler = new Handler();

    private final Object mCreatingLock = new Object();

    private Activity mActivity = null;
    private IWebView mWebView = null;

    private boolean mbPreCreating = false;

    @CallSuper
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

    protected void runOnUiThread(Runnable runnable) {
        if (null != runnable) {
            mHandler.post(runnable);
        }
    }

    protected void runOnUiThread(Runnable runnable, int delay) {
        if (null != runnable) {
            mHandler.postDelayed(runnable, delay);
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
        mHandler.removeCallbacksAndMessages(null);
    }

    protected IWebView getWebView() {
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
        ShareUtils.onActivityResult(getActivity(), requestCode, resultCode, intent);
    }

    public void onCreateView(View view) {

    }
}
