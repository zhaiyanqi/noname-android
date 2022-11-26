package online.nonamekill.android.container;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

import online.nonamekill.android.R;
import online.nonamekill.android.module.ModuleManager;
import online.nonamekill.android.view.CloseButton;
import online.nonamekill.android.view.SettingButton;
import online.nonamekill.common.data.DataKey;
import online.nonamekill.common.data.DataManager;

public class ContainerUIManager {

    @NonNull
    private final Activity mActivity;

    @NonNull
    private final ModuleManager mModuleManager;

    private RelativeLayout mMainContainer = null;
    private SettingButton mSettingButton = null;
    private RelativeLayout.LayoutParams mSettingButtonParams = null;

    private ViewDragHelper mSettingDragHelper = null;

    public ContainerUIManager(Activity activity) {
        mActivity = activity;

        mModuleManager = new ModuleManager(activity);
    }

    public void onCreate() {
        mModuleManager.onCreate();

        initContainerView();
        initModuleRecyclerView();
    }

    private void initModuleRecyclerView() {
//        RecyclerView recyclerView = new RecyclerView(mActivity);

//        ModuleListAdapter adapter = new ModuleListAdapter(mModuleManager.getModeNameList());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initContainerView() {
        mMainContainer = mActivity.findViewById(R.id.module_view_container);
        mMainContainer.setZ(Integer.MAX_VALUE);
        mMainContainer.setVisibility(View.INVISIBLE);
        mMainContainer.setOnTouchListener((v, event) -> true);

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
            PackageManager pm = mActivity.getPackageManager();
            pm.setComponentEnabledSetting(mActivity.getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(new ComponentName(mActivity,
                            "online.nonamekill.android.module.icon.fangtian"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

//            setModuleContainerVisible(View.VISIBLE)
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
        if ((null != mMainContainer) && mMainContainer.isShown()) {
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

            mSettingButton.clearAnimation();
            mSettingButton.animate().alpha(0f).setDuration(250).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSettingButton.setEnabled(false);
                    mSettingButton.setVisibility(View.INVISIBLE);
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
                            mMainContainer.setVisibility(View.INVISIBLE);
                        }
                    })
                    .start();
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
}
