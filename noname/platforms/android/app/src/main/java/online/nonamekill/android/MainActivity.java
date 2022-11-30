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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.ConfirmPopupView;

import org.apache.cordova.CordovaActivity;

import java.io.File;
import java.util.Objects;

import online.nonamekill.android.container.ContainerUIManager;
import online.nonamekill.common.Constant;
import online.nonamekill.common.data.DataKey;
import online.nonamekill.common.data.DataManager;
import online.nonamekill.common.util.ActivityUtil;
import online.nonamekill.common.util.GameResourceUtil;
import online.nonamekill.common.util.RxToast;
import online.nonamekill.module.imp.ImportActivity;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends CordovaActivity {

    private ContainerUIManager mContainerUIManager = null;

    private volatile boolean mbUrlLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityUtil.hideNavigationBar(getWindow());

        mContainerUIManager = new ContainerUIManager(this);
        init();
        // 优先查看游戏版本设置
        if (checkVersionGamePath()){
            JavaScriptBridge.setGamePath(DataManager.getInstance().getValue(DataKey.KEY_GAME_PATH));
            loadUrl(launchUrl);
            mbUrlLoaded = true;
        } else if (GameResourceUtil.checkGameResource(this)) {
            loadUrl(launchUrl);
            mbUrlLoaded = true;
        } else {
            if (GameResourceUtil.checkAssetContext(this)) {
                Intent intent = new Intent();
                intent.setClass(this, ImportActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            } else {
                RxToast.error(this, "未找到lib_assets资源");
                new Handler().postDelayed(() -> {
                    XPopup.Builder builder = new XPopup.Builder(this);
                    builder.isDestroyOnDismiss(true)
                            .hasStatusBar(false)
                            .dismissOnTouchOutside(false)
                            .dismissOnBackPressed(false);
                    ConfirmPopupView confirm = builder.asConfirm("警告", "未找到资源目录，请在版本管理界面进行切换游戏版本，或者下载lib_assets纯资源APK", () -> {
                        mContainerUIManager.openModuleContainer();
                    });
                    confirm.isHideCancel = true;
                    confirm.show();
                }, 500);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean checkVersionGamePath() {
        String gamePath = DataManager.getInstance().getValue(DataKey.KEY_GAME_PATH);
        if(TextUtils.isEmpty(gamePath)) return false;

        File file = cordovaInterface.getContext().getExternalFilesDir(gamePath);
        if(Objects.isNull(file) || !file.exists()) {
            RxToast.error(this, "设置的游戏主体不存在！");
            return false;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mContainerUIManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mbUrlLoaded && GameResourceUtil.checkGameResource(this)) {
            loadUrl(launchUrl);
            mbUrlLoaded = true;
        }

        mContainerUIManager.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mContainerUIManager.onDestroy();
    }

    @Override
    protected void createViews() {
        setContentView(R.layout.main_layout);
        RelativeLayout rootView = findViewById(R.id.root_view);

        WebView view = (WebView) appView.getView();
        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(Color.BLACK);
        view.requestFocusFromTouch();
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.setOverScrollMode(View.OVER_SCROLL_NEVER);
        view.addJavascriptInterface(new JavaScriptBridge(this), JavaScriptBridge.JS_PARAMS);

        rootView.addView(view);
        mContainerUIManager.onCreate();
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
