package online.nonamekill.common.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;

import java.util.Optional;

import online.nonamekill.common.Constant;

public class BaseModule {
    private Activity mActivity = null;
    private WebView mWebView = null;

    public void onCreate(Activity activity) {
        mActivity = activity;
        mWebView = mActivity.findViewById(Constant.WEB_VIEW_ID);
    }

    public void onPreCreate() {
        // todo async load view.
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

    public void onPause() {

    }

    public void onResume() {

    }

    public String getName() {
        return null;
    }

    public void onDestroy() {

    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }

    public void onCreateView(View view) {

    }
}
