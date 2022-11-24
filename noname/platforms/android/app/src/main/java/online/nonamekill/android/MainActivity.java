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

import online.nonamekill.android.module.ModuleManager;
import online.nonamekill.android.view.SettingButton;
import online.nonamekill.common.util.GameResourceUtil;
import online.nonamekill.module.imp.ImportActivity;

public class MainActivity extends CordovaActivity {
    private ModuleManager mModuleManager;

    // view
    private RelativeLayout mRootView = null;
    private RelativeLayout mMainContainer = null;
    private SettingButton mSettingButton = null;
    private RelativeLayout.LayoutParams mSettingButtonParams = null;

    private ViewDragHelper mSettingDragHelper = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModuleManager = new ModuleManager(this);
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
        WebView view = (WebView) appView.getView();
        view.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(R.layout.main_layout);

        mRootView = findViewById(R.id.root_view);
        mRootView.addView(view);
        mRootView.requestFocus();
        view.setBackgroundColor(Color.BLACK);
        view.requestFocusFromTouch();
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.setOverScrollMode(View.OVER_SCROLL_NEVER);
        view.addJavascriptInterface(new JavaScriptBridge(this), JavaScriptBridge.JS_PARAMS);

        mMainContainer = findViewById(R.id.module_view_container);
        mMainContainer.setZ(Integer.MAX_VALUE);
        mMainContainer.setVisibility(View.GONE);

        // setting button
        mSettingButton = new SettingButton(this);
        mSettingButton.setEnabled(true);
        mSettingButton.setBackgroundResource(R.drawable.setting_button_background);
        mSettingButton.setImageResource(R.drawable.ic_settings);
        int size = getResources().getDimensionPixelSize(R.dimen.setting_button_size);
        mSettingButtonParams = new RelativeLayout.LayoutParams(size, size);
        mSettingButtonParams.topMargin = getResources().getDimensionPixelSize(R.dimen.setting_button_margin_top);
        mSettingButtonParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.setting_button_margin_left);
        mSettingButton.setLayoutParams(mSettingButtonParams);
        mRootView.addView(mSettingButton);

        mSettingButton.setOnClickListener(v -> setModuleContainerVisible(View.VISIBLE));

        mSettingDragHelper = ViewDragHelper.create(mRootView, new SettingDragCallback());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        mSettingDragHelper.processTouchEvent(ev);

        return super.dispatchTouchEvent(ev);
    }

    private class SettingDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return (child == mSettingButton);
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return top;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            mSettingButtonParams.leftMargin = left;
            mSettingButtonParams.topMargin = top;
            changedView.setLayoutParams(mSettingButtonParams);
        }
    }

    private void setModuleContainerVisible(int visible) {
        if (View.VISIBLE == visible) {
            mMainContainer.setVisibility(View.VISIBLE);
            mMainContainer.clearAnimation();
            mMainContainer.setAlpha(0f);
            mMainContainer.setScaleX(0.1f);
            mMainContainer.setScaleY(0.1f);
            mMainContainer.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(250).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSettingButton.setEnabled(false);
                }
            }).start();
        } else {
            mMainContainer.clearAnimation();
            mMainContainer.animate()
                    .scaleX(0.1f)
                    .scaleY(0.1f)
                    .alpha(0f)
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mSettingButton.setEnabled(true);
                        }
                    })
                    .start();
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK)
                && (null != mMainContainer)
                && (mMainContainer.getVisibility() == View.VISIBLE)) {
            setModuleContainerVisible(View.GONE);

            return true;
        }

        return super.dispatchKeyEvent(event);
    }
}
