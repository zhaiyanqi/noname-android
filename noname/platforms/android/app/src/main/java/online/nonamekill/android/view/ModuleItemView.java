package online.nonamekill.android.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.PathInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class ModuleItemView extends AppCompatTextView {

    private static final float SCALE_PRESSED = 1.2f;
    private static final float SCALE_NORMAL = 1f;

    private static final int ANIM_TIME_SHOW = 150;

    private final ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();
    private final PathInterpolator mAlphaInterpolator = new PathInterpolator(0.33f, 0f, 0.67f, 1f);

    private ObjectAnimator mShowColorAnimator = null;
    private ObjectAnimator mHideColorAnimator = null;
    private long mDownTime = 0;
    private boolean mbSelected = false;

    public ModuleItemView(@NonNull Context context) {
        super(context);
        init();
    }

    public ModuleItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ModuleItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int mDefaultColor = Color.WHITE;
        int mPressColor = Color.parseColor("#fffaa755");

        mShowColorAnimator = ObjectAnimator.ofArgb(this, "textColor", mDefaultColor, mPressColor);
        mShowColorAnimator.setEvaluator(mArgbEvaluator);
        mShowColorAnimator.setInterpolator(mAlphaInterpolator);
        mShowColorAnimator.setDuration(ANIM_TIME_SHOW);

        mHideColorAnimator = ObjectAnimator.ofArgb(this, "textColor", mPressColor, mDefaultColor);
        mHideColorAnimator.setEvaluator(mArgbEvaluator);
        mShowColorAnimator.setInterpolator(mAlphaInterpolator);
        mHideColorAnimator.setDuration(ANIM_TIME_SHOW);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                mDownTime = SystemClock.uptimeMillis();
                clearAnimation();

                animate().scaleX(SCALE_PRESSED)
                        .setInterpolator(mAlphaInterpolator)
                        .scaleY(SCALE_PRESSED)
                        .setDuration(ANIM_TIME_SHOW)
                        .start();

                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                clearAnimation();
                animate().scaleX(SCALE_NORMAL)
                        .setInterpolator(mAlphaInterpolator)
                        .scaleY(SCALE_NORMAL)
                        .setDuration(ANIM_TIME_SHOW)
                        .start();

                performClick();

            case MotionEvent.ACTION_CANCEL:
                clearAnimation();
                animate().scaleX(SCALE_NORMAL)
                        .setInterpolator(mAlphaInterpolator)
                        .scaleY(SCALE_NORMAL)
                        .setDuration(ANIM_TIME_SHOW)
                        .start();
                break;
        }

        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void setSelect(boolean selected) {
        if (mbSelected != selected) {
            mbSelected = selected;

            if (selected) {
                if (mHideColorAnimator.isStarted()) {
                    mHideColorAnimator.cancel();
                }

                mShowColorAnimator.start();
            } else {
                if (mShowColorAnimator.isStarted()) {
                    mShowColorAnimator.cancel();
                }

                mHideColorAnimator.start();
            }
        }
    }

}
