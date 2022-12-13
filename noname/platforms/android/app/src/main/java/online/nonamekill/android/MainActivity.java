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
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.impl.ConfirmPopupView;
import com.lxj.xpopup.interfaces.XPopupCallback;

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
import online.nonamekill.common.util.XPopupUtil;
import online.nonamekill.module.imp.ImportActivity;
import online.nonamekill.module.import_progress.ImportProgress;

public class MainActivity extends CordovaActivity {

    private ContainerUIManager mContainerUIManager = null;
    private boolean mbUrlLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 隐藏导航条和状态栏
        ActivityUtil.hideNavigationBar(getWindow());
        ActivityUtil.hideSystemUI(getWindow());

        // UI初始化
        init();

        // 尝试加载url
        tryLoadUrl(false);
    }

    /**
     *  1.首先查看是否从外部打开此应用<br/>
     *  2.检查游戏版本路径设置，如果设置了，那么加载路径<br/>
     *  3.检查file/resource是否存在，如果存在，那么加载路径<br/>
     *  4.检查是否安装了lib_asset的资源apk,如果安装了那就进行资源导入<br/>
     * 尝试通过各种路径加载资源
     */
    private void tryLoadUrl(boolean isOnResume) {
        Intent actionIntent = getIntent();
        boolean isActionView = (null != actionIntent) && Intent.ACTION_VIEW.equals(actionIntent.getAction());
        if(isActionView){
            Intent intent = new Intent();
            intent.setData(actionIntent.getData());
            intent.setClass(this, ImportProgress.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }else if (checkVersionGamePath()) {
            // 优先查看游戏版本设置
            JavaScriptBridge.setGamePath(DataManager.getInstance().getValue(DataKey.KEY_GAME_PATH));
            loadUrl(launchUrl);
            mbUrlLoaded = true;
        } else if (GameResourceUtil.checkGameResource(this)) {
            // 查看资源目录是否存在
            loadUrl(launchUrl);
            mbUrlLoaded = true;
        } else if(isOnResume) {
            // 查看资源apk是否存在，存在就导入
            if (GameResourceUtil.checkAssetContext(this)) {
                Intent intent = new Intent();
                intent.setClass(this, ImportActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            } else {
                // 没有版本目录不是主文件，资源apk也不存在
                RxToast.error(this, "未找到lib_assets资源");
                new Handler().postDelayed(() -> XPopupUtil.asConfirm(this, "警告", "未找到资源目录，请在版本管理界面进行切换游戏版本，或者下载lib_assets纯资源APK", true, () -> mContainerUIManager.openModuleContainer("版本管理")), 300);
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
     * init的时候会嗲用到Cordova中去创建view，最终会中通过此处setContentView
     */
    @Override
    protected void createViews() {
        setContentView(R.layout.main_layout);
        RelativeLayout rootView = findViewById(R.id.root_view);

        WebView webView = (WebView) appView.getView();
        webView.setId(Constant.WEB_VIEW_ID);
        webView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        webView.setBackgroundColor(Color.BLACK);
        webView.requestFocusFromTouch();
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.addJavascriptInterface(new JavaScriptBridge(this), JavaScriptBridge.JS_PARAMS);

        rootView.addView(webView);
    }

    private boolean checkVersionGamePath() {
        String gamePath = DataManager.getInstance().getValue(DataKey.KEY_GAME_PATH);

        if (TextUtils.isEmpty(gamePath)) {
            return false;
        }

        File file = getExternalFilesDir(gamePath);

        if (Objects.isNull(file) || !file.exists()) {
            RxToast.error(this, "设置的游戏主体不存在！");

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
