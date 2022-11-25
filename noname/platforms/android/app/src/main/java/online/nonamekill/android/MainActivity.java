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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.PathInterpolator;
import android.webkit.WebView;
import android.widget.RelativeLayout;


import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

import org.apache.cordova.*;

import online.nonamekill.android.container.ContainerUIManager;
import online.nonamekill.android.module.ModuleManager;
import online.nonamekill.android.view.CloseButton;
import online.nonamekill.android.view.SettingButton;
import online.nonamekill.common.data.DataKey;
import online.nonamekill.common.data.DataManager;
import online.nonamekill.common.util.GameResourceUtil;
import online.nonamekill.module.imp.ImportActivity;

public class MainActivity extends CordovaActivity {
    private ModuleManager mModuleManager;

    private ContainerUIManager mContainerUIManager = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModuleManager = new ModuleManager(this);

        mContainerUIManager = new ContainerUIManager(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (null != mModuleManager) {
            mModuleManager.onPause();
        }
    }

    @Override
    protected void onResume() {
        if (GameResourceUtil.checkGameResource(this)) {
            loadUrl(launchUrl);

            if (null != mModuleManager) {
                mModuleManager.onResume();
            }
        } else {
            Intent intent = new Intent();
            intent.setClass(this, ImportActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        super.onResume();
    }

    @Override
    protected void createViews() {
        setContentView(R.layout.main_layout);
        RelativeLayout rootView = findViewById(R.id.root_view);

        WebView view = (WebView) appView.getView();
        view.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK) && mContainerUIManager.onBackPressed()) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }
}
