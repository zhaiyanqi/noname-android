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

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.PathInterpolator;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleEventObserver;

import org.apache.cordova.*;

import java.util.ArrayList;

import online.nonamekill.android.module.ModuleManager;
import online.nonamekill.common.function.BaseModule;
import online.nonamekill.common.function.ModuleListener;
import online.nonamekill.common.util.GameResourceUtil;
import online.nonamekill.common.util.ThreadUtil;
import online.nonamekill.module.imp.ImportActivity;
import online.nonemekill.autoimport.ModuleAutoImport;

public class MainActivity extends CordovaActivity implements ModuleListener {
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

        mMainContainer = new RelativeLayout(this);
        mRootView.addView(mMainContainer, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onAutoImportFinished() {
        runOnUiThread(() -> {
            appView.loadUrlIntoView(launchUrl, true);
            mMainContainer.animate().alpha(0)
                    .setDuration(250)
                    .setInterpolator(new PathInterpolator(0.33f, 0, 0.67f, 1f))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mMainContainer.setVisibility(View.GONE);
                        }
                    }).start();
        });
    }
}
