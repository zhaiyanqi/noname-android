/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package online.nonamekill.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;


import com.lxj.xpopup.XPopup;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.CordovaWebViewImpl;

import java.io.File;
import java.util.Objects;

import online.nonamekill.android.container.ContainerUIManager;
import online.nonamekill.common.Constant;
import online.nonamekill.common.data.DataKey;
import online.nonamekill.common.data.DataManager;
import online.nonamekill.common.util.ActivityUtil;
import online.nonamekill.common.util.GameResourceUtil;
import online.nonamekill.common.util.RxToast;
import online.nonamekill.common.util.XPopupUtil;
import online.nonamekill.module.imp.ImportActivity;
import online.nonamekill.module.import_progress.ImportProgress;

public class MainActivity extends CordovaActivity {

    private ContainerUIManager mContainerUIManager = null;
    // URL ????????????
    private boolean mbUrlLoaded = false;
    // ?????????????????????
    private boolean mbNullPath = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ???????????????????????????
        ActivityUtil.hideNavigationBar(getWindow());
        ActivityUtil.hideSystemUI(getWindow());

        // UI?????????
        init();

        // ????????????url
        tryLoadUrl(false);
    }

    @Override
    protected CordovaWebViewEngine makeWebViewEngine() {
        // todo x5????????????????????????
        if (DataManager.getInstance().getValue(DataKey.KEY_IS_X5_CORE)) {
            return CordovaWebViewImpl.createX5Engine(this, preferences);
        } else {
            return super.makeWebViewEngine();
        }
    }

    /**
     *  1.??????????????????????????????????????????<br/>
     *  2.?????????????????????????????????????????????????????????????????????<br/>
     *  3.??????file/resource????????????????????????????????????????????????<br/>
     *  4.?????????????????????lib_asset?????????apk,???????????????????????????????????????<br/>
     * ????????????????????????????????????
     */
    private void tryLoadUrl(boolean isOnResume) {
        Intent actionIntent = getIntent();
        boolean isActionView = (null != actionIntent) && Intent.ACTION_VIEW.equals(actionIntent.getAction());
        if (isActionView) {
            Intent intent = new Intent();
            intent.setData(actionIntent.getData());
            intent.setClass(this, ImportProgress.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else if (checkVersionGamePath()) {
            // ??????????????????????????????
            JavaScriptBridge.setGamePath(DataManager.getInstance().getValue(DataKey.KEY_GAME_PATH));
            loadUrl(launchUrl);
            mbUrlLoaded = true;
        } else if (GameResourceUtil.checkGameResource(this)) {
            // ??????????????????????????????
            loadUrl(launchUrl);
            mbUrlLoaded = true;
        } else if(isOnResume) {
            // ????????????apk??????????????????????????????
            if (GameResourceUtil.checkAssetContext(this)) {
                Intent intent = new Intent();
                intent.setClass(this, ImportActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            } else if(!mbNullPath) {
                mbNullPath = true;
                // ??????????????????????????????????????????apk????????????
                RxToast.error(this, "?????????lib_assets??????");
                new Handler().postDelayed(() -> XPopupUtil.asConfirm(this, "??????", "???????????????????????????????????????????????????????????????????????????????????????lib_assets?????????APK", true, () -> mContainerUIManager.openModuleContainer("????????????")), 300);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        mContainerUIManager = new ContainerUIManager(this);
        XPopup.setPrimaryColor(getResources().getColor(R.color.colorPrimary));
    }

    /**
     * init?????????????????????Cordova????????????view???????????????????????????setContentView
     */
    @Override
    protected void createViews() {
        setContentView(R.layout.main_layout);
        RelativeLayout rootView = findViewById(R.id.root_view);

        View view = appView.getView();

        if (view instanceof WebView) {
            WebView webView = (WebView) view;
            webView.setId(Constant.WEB_VIEW_ID);
            webView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            webView.setBackgroundColor(Color.BLACK);
            webView.requestFocusFromTouch();
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            webView.addJavascriptInterface(new JavaScriptBridge(this), JavaScriptBridge.JS_PARAMS);
        } else {
            com.tencent.smtt.sdk.WebView webView = (com.tencent.smtt.sdk.WebView) view;
            webView.setId(Constant.WEB_VIEW_ID);
            webView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            webView.setBackgroundColor(Color.BLACK);
            webView.requestFocusFromTouch();
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            webView.addJavascriptInterface(new JavaScriptBridge(this), JavaScriptBridge.JS_PARAMS);
        }

        rootView.addView(view);
    }

    private boolean checkVersionGamePath() {
        String gamePath = DataManager.getInstance().getValue(DataKey.KEY_GAME_PATH);

        if (TextUtils.isEmpty(gamePath)) {
            return false;
        }

        File file = new File(gamePath);

        if (Objects.isNull(file) || !file.exists()) {
            RxToast.error(this, "?????????????????????????????????");

            return false;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mbUrlLoaded) {
            tryLoadUrl(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if ((null != mContainerUIManager) && mContainerUIManager.dispatchTouchEvent(ev)) {
            return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (null != mContainerUIManager) {
            mContainerUIManager.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK) && mContainerUIManager.onBackPressed()) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }
}
