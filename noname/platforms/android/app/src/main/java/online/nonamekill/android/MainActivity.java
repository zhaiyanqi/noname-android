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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import org.apache.cordova.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import online.nonamekill.android.container.ContainerUIManager;
import online.nonamekill.common.Constant;
import online.nonamekill.common.util.GameResourceUtil;
import online.nonamekill.module.imp.ImportActivity;

public class MainActivity extends CordovaActivity {

    private ContainerUIManager mContainerUIManager = null;

    private boolean mbUrlLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideNavigationBar();

        mContainerUIManager = new ContainerUIManager(this);
        init();

        if (GameResourceUtil.checkGameResource(this)) {
            loadUrl(launchUrl);
            mbUrlLoaded = true;
        } else {
            if (GameResourceUtil.checkAssetContext(this)) {
                Intent intent = new Intent();
                intent.setClass(this, ImportActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        }
    }

    /**
     * 隐藏导航条
     */
    private void hideNavigationBar() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        getWindow().setAttributes(params);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mbUrlLoaded && GameResourceUtil.checkGameResource(this)) {
            loadUrl(launchUrl);
            mbUrlLoaded = true;
        }
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
