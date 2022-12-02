package online.nonamekill.android.container;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.customview.widget.ViewDragHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import online.nonamekill.android.R;
import online.nonamekill.android.module.ModuleManager;
import online.nonamekill.android.view.CloseButton;
import online.nonamekill.android.view.SettingButton;
import online.nonamekill.common.Constant;
import online.nonamekill.common.data.DataKey;
import online.nonamekill.common.data.DataManager;
import online.nonamekill.common.module.BaseModule;
import online.nonamekill.common.util.ThreadUtil;

public class ContainerUIManager {

    @NonNull
    private final Activity mActivity;

    @NonNull
    private final ModuleManager mModuleManager;

    private RelativeLayout mContainerRoot = null;
    private RelativeLayout mMainContainer = null;
    private SettingButton mSettingButton = null;
    private RelativeLayout.LayoutParams mSettingButtonParams = null;

    private ViewDragHelper mSettingDragHelper = null;

    // animator
    private AnimatorSet mShowRootAnimator = null;
    private AnimatorSet mHideRootAnimator = null;

    private AnimatorSet mShowModuleViewAnimator = null;
    private AnimatorSet mHideModuleViewAnimator = null;

    public ContainerUIManager(@NonNull Activity activity) {
        mActivity = activity;
        mModuleManager = new ModuleManager(activity);
    }

    public void onCreate() {
        mModuleManager.onCreate();

        initContainerView();
        initModuleRecyclerView();
        initAnimator();
    }

    public void onPause() {
        mModuleManager.onPause();
    }

    public void onResume() {
        mModuleManager.onResume();
    }

    public void onDestroy() {
        mModuleManager.onDestroy();
    }

    private void initAnimator() {
        initRootViewAnimator();
        initContainerAnimator();
    }

    private void initRootViewAnimator() {
        mShowRootAnimator = new AnimatorSet();
        ValueAnimator alphaIn = ValueAnimator.ofFloat(0, 1);
        alphaIn.setDuration(Constant.Duration.Alpha);
        alphaIn.setInterpolator(Constant.Interpolator.Alpha);
        alphaIn.addUpdateListener(animation -> {
            mContainerRoot.setAlpha((float) animation.getAnimatedValue());
            mContainerRoot.invalidate();
        });

        ValueAnimator scaleIn = ValueAnimator.ofFloat(0.1f, 1);
        scaleIn.setDuration(Constant.Duration.Scale);
        scaleIn.setInterpolator(Constant.Interpolator.Scale);
        scaleIn.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            mContainerRoot.setScaleY(value);
            mContainerRoot.setScaleX(value);
            mContainerRoot.invalidate();
        });

        mShowRootAnimator.play(alphaIn).with(scaleIn);

        mHideRootAnimator = new AnimatorSet();
        ValueAnimator alphaHide = ValueAnimator.ofFloat(1, 0);
        alphaHide.setDuration(Constant.Duration.Alpha);
        alphaHide.setInterpolator(Constant.Interpolator.Alpha);
        alphaHide.addUpdateListener(animation -> {
            mContainerRoot.setAlpha((float) animation.getAnimatedValue());
            mContainerRoot.invalidate();
        });

        ValueAnimator scaleOut = ValueAnimator.ofFloat(1f, 0.1f);
        scaleOut.setDuration(Constant.Duration.Scale);
        scaleOut.setInterpolator(Constant.Interpolator.Scale);
        scaleOut.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            mContainerRoot.setScaleY(value);
            mContainerRoot.setScaleX(value);
            mContainerRoot.invalidate();
        });

        mHideRootAnimator.play(alphaHide).with(scaleOut);
    }

    private void initContainerAnimator() {
        mShowModuleViewAnimator = new AnimatorSet();
        ValueAnimator alphaIn = ValueAnimator.ofFloat(0, 1);
        alphaIn.setDuration(Constant.Duration.Alpha);
        alphaIn.setInterpolator(Constant.Interpolator.Alpha);
        alphaIn.addUpdateListener(animation -> {
            mMainContainer.setAlpha((float) animation.getAnimatedValue());
            mMainContainer.invalidate();
        });

        ValueAnimator scaleIn = ValueAnimator.ofFloat(0.1f, 1);
        scaleIn.setDuration(Constant.Duration.Scale);
        scaleIn.setInterpolator(Constant.Interpolator.Scale);
        scaleIn.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            mMainContainer.setScaleY(value);
            mMainContainer.setScaleX(value);
            mMainContainer.invalidate();
        });

        mShowModuleViewAnimator.play(alphaIn).with(scaleIn);

        mHideModuleViewAnimator = new AnimatorSet();
        ValueAnimator alphaHide = ValueAnimator.ofFloat(1, 0);
        alphaHide.setDuration(Constant.Duration.Alpha);
        alphaHide.setInterpolator(Constant.Interpolator.Alpha);
        alphaHide.addUpdateListener(animation -> {
            mMainContainer.setAlpha((float) animation.getAnimatedValue());
            mMainContainer.invalidate();
        });

        ValueAnimator scaleOut = ValueAnimator.ofFloat(1f, 0.1f);
        scaleOut.setDuration(Constant.Duration.Scale);
        scaleOut.setInterpolator(Constant.Interpolator.Scale);
        scaleOut.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            mMainContainer.setScaleY(value);
            mMainContainer.setScaleX(value);
            mMainContainer.invalidate();
        });

        mHideModuleViewAnimator.play(alphaHide).with(scaleOut);
    }

    private void initModuleRecyclerView() {
        RecyclerView recyclerView = mActivity.findViewById(R.id.module_container_name_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));

        ModuleListAdapter adapter = new ModuleListAdapter(mModuleManager.getModeNameList(), this::onModuleChanged);
        recyclerView.setAdapter(adapter);
    }

    private void onModuleChanged(String module) {
        BaseModule target = mModuleManager.getModules(module);

        if (mModuleManager.checkToChangeModule(target)) {
            mHideModuleViewAnimator.removeAllListeners();
            mModuleManager.doChange(target);
            mMainContainer.removeAllViews();
            View view = target.getView(mActivity);
            mHideModuleViewAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (null != view) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT);
                        mMainContainer.addView(view, params);
                        target.onCreateView(view);
                    }

                    mShowModuleViewAnimator.start();
                }
            });
            mHideModuleViewAnimator.start();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initContainerView() {
        mContainerRoot = mActivity.findViewById(R.id.module_view_container);
        mContainerRoot.setZ(Integer.MAX_VALUE);
        mContainerRoot.setVisibility(View.INVISIBLE);
        mContainerRoot.setOnTouchListener((v, event) -> true);

        mMainContainer = mActivity.findViewById(R.id.module_container);

        CloseButton closeButton = mActivity.findViewById(R.id.module_container_close_button);
        closeButton.setOnClickListener(v -> {
            setModuleContainerVisible(View.INVISIBLE);
        });

        // setting button
        mSettingButton = new SettingButton(mActivity);
        mSettingButton.setEnabled(true);
        mSettingButton.setBackgroundResource(R.drawable.setting_button_background);
        mSettingButton.setImageResource(R.drawable.ic_settings_button);
        int size = mActivity.getResources().getDimensionPixelSize(R.dimen.setting_button_size);
        int padding = mActivity.getResources().getDimensionPixelSize(R.dimen.setting_button_padding);
        mSettingButtonParams = new RelativeLayout.LayoutParams(size, size);
        mSettingButtonParams.topMargin = DataManager.getInstance().getValue(DataKey.KEY_SETTING_BUTTON_TOP);
        mSettingButtonParams.leftMargin = DataManager.getInstance().getValue(DataKey.KEY_SETTING_BUTTON_LEFT);
        mSettingButton.setPadding(padding, padding, padding, padding);

        mSettingButton.setLayoutParams(mSettingButtonParams);
        RelativeLayout mRootView = mActivity.findViewById(R.id.root_view);
        mRootView.addView(mSettingButton);
        mSettingButton.setOnClickListener(v -> {
            setModuleContainerVisible(View.VISIBLE);
        });

        mSettingDragHelper = ViewDragHelper.create(mRootView, new SettingDragCallback());
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (null != mSettingDragHelper) {
            mSettingDragHelper.processTouchEvent(ev);
        }

        return false;
    }

    public boolean onBackPressed() {
        if ((null != mContainerRoot) && mContainerRoot.isShown()) {
            setModuleContainerVisible(View.INVISIBLE);

            return true;
        }

        return false;
    }


    private class SettingDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return (child == mSettingButton) && mSettingButton.isEnabled();
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

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            DataManager.getInstance().setValue(DataKey.KEY_SETTING_BUTTON_TOP, mSettingButtonParams.topMargin);
            DataManager.getInstance().setValue(DataKey.KEY_SETTING_BUTTON_LEFT, mSettingButtonParams.leftMargin);
        }
    }

    public void openModuleContainer(){
        openModuleContainer(null);
    }

    // 打开设置菜单切换到对应的模块上，就是太卡了，不推荐使用
    public void openModuleContainer(String moduleName){
        setModuleContainerVisible(View.VISIBLE);
        if(moduleName != null)
            new Handler(Looper.getMainLooper()).post(()->this.onModuleChanged(moduleName));
    }

    private void setModuleContainerVisible(int visible) {
        if (View.VISIBLE == visible) {
            if (mHideRootAnimator.isStarted()) {
                mHideRootAnimator.cancel();
            }

            mShowRootAnimator.removeAllListeners();
            mShowRootAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mContainerRoot.setVisibility(View.VISIBLE);
                }
            });
            mShowRootAnimator.start();

            mSettingButton.clearAnimation();
            mSettingButton.animate().alpha(0f).setDuration(250).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSettingButton.setEnabled(false);
                    mSettingButton.setVisibility(View.INVISIBLE);
                }
            }).start();
        } else {
            if (mShowRootAnimator.isStarted()) {
                mShowRootAnimator.cancel();
            }
            mHideRootAnimator.removeAllListeners();
            mHideRootAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mContainerRoot.setVisibility(View.GONE);
                }
            });
            mHideRootAnimator.start();

            mSettingButton.clearAnimation();
            mSettingButton.setVisibility(View.VISIBLE);
            mSettingButton.animate().alpha(1f).setDuration(250).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSettingButton.setEnabled(true);
                }
            }).start();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mModuleManager.onActivityResult(requestCode, resultCode, intent);
    }
}
