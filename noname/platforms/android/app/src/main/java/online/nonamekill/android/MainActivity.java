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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.PathInterpolator;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import androidx.customview.widget.ViewDragHelper;

import org.apache.cordova.*;

import online.nonamekill.android.module.ModuleManager;
import online.nonamekill.common.util.GameResourceUtil;
import online.nonamekill.module.imp.ImportActivity;

public class MainActivity extends CordovaActivity {
    private ModuleManager mModuleManager;

    // view
    private RelativeLayout mRootView = null;
    private RelativeLayout mMainContainer = null;

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        processDoubleFingerEvent(ev);

        return super.dispatchTouchEvent(ev);
    }

    private int[] mDownX = new int[2];
    private int[] mDownY = new int[2];

    private void processDoubleFingerEvent(MotionEvent ev) {
//        if (ev.getPointerCount() != 3) {
//            return;
//        }

//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_POINTER_DOWN: {
//                setModuleContainerVisible(View.VISIBLE);
//            }
//        }
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
        view.setBackgroundColor(Color.BLACK);
        view.requestFocusFromTouch();
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.setOverScrollMode(View.OVER_SCROLL_NEVER);
        view.addJavascriptInterface(new JavaScriptBridge(this), JavaScriptBridge.JS_PARAMS);

        mMainContainer = findViewById(R.id.module_view_container);
        mMainContainer.setZ(Integer.MAX_VALUE);
        mMainContainer.setVisibility(View.GONE);
    }

    private void setModuleContainerVisible(int visible) {
        if (View.VISIBLE == visible) {
            mMainContainer.setTranslationX(-mMainContainer.getWidth());
            mMainContainer.setVisibility(View.VISIBLE);

            mMainContainer.animate()
                    .translationX(0)
                    .setDuration(500)
                    .setInterpolator(new PathInterpolator(0.3f, 0, 0.1f, 1f))
                    .start();
        } else {
            mMainContainer.animate()
                    .translationX(-mMainContainer.getWidth())
                    .setDuration(500)
                    .setInterpolator(new PathInterpolator(0.3f, 0, 0.1f, 1f))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (null != mMainContainer) {
                                mMainContainer.setVisibility(View.GONE);
                            }
                        }
                    }).start();
        }
    }

    @Override
    public void onBackPressed() {
//        if ((null != mMainContainer) && (mMainContainer.getVisibility() == View.VISIBLE)) {
//            setModuleContainerVisible(View.GONE);
//
//            return;
//        }

        super.onBackPressed();
    }
}
