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

import org.apache.cordova.*;

import java.util.ArrayList;

import online.nonamekill.common.function.BaseModule;
import online.nonamekill.common.function.ModuleListener;
import online.nonamekill.common.util.GameResourceUtil;
import online.nonamekill.common.util.ThreadUtil;
import online.nonemekill.autoimport.ModuleAutoImport;

public class MainActivity extends CordovaActivity implements ModuleListener {

    private static final int REQUEST_MANAGER_PERMISSION = 100;

    private final ArrayList<BaseModule> mModules = new ArrayList<>();

    // view
    private RelativeLayout mRootView = null;
    private RelativeLayout mMainContainer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);
        onCreateInternal();
    }

    @Override
    public void loadUrl(String url) {
        if (appView == null) {
            init();
        }

        // If keepRunning
        this.keepRunning = preferences.getBoolean("KeepRunning", true);
    }

    private void onCreateInternal() {
        for (BaseModule module : mModules) {
            module.onCreate(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


        for (BaseModule module : mModules) {
            module.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (BaseModule module : mModules) {
            module.onResume();
        }
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

//        checkPermissions();

        initModules();
        initContainer();
    }

    private void initContainer() {
        mMainContainer = new RelativeLayout(this);
        mRootView.addView(mMainContainer, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        for (BaseModule module : mModules) {
            View view = module.getView(this);

            if (null != view) {
                mMainContainer.addView(view);
            }
        }
    }

    private void initModules() {
        bindModule(new ModuleAutoImport());
    }

    private void bindModule(BaseModule module) {
        module.setListener(this);
        mModules.add(module);
    }

    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {

        }
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
