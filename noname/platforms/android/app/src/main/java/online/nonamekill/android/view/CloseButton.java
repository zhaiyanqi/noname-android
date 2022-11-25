package online.nonamekill.android.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.PathInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CloseButton extends AppCompatImageView {

    private static final float SCALE_PRESSED = 1.2f;
    private static final float SCALE_NORMAL = 1f;

    private static final int MSG_AUTO_HIDE = 101;
    private static final int MSG_AUTO_HIDE_DELAY = 3000;
    private static final int ANIM_TIME_SHOW = 150;
    private static final int TIME_CLICK = 300;

    private final ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();
    private final PathInterpolator mAlphaInterpolator = new PathInterpolator(0.33f, 0f, 0.67f, 1f);

    private ObjectAnimator mShowColorAnimator = null;
    private ObjectAnimator mHideColorAnimator = null;

    private long mDownTime = 0;

    public CloseButton(@NonNull Context context) {
        super(context);
        init();
    }

    public CloseButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CloseButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        int mDefaultColor = Color.WHITE;
        int mPressColor = Color.parseColor("#faa755");

        mShowColorAnimator = ObjectAnimator.ofArgb(this, "colorFilter", mDefaultColor, mPressColor);
        mShowColorAnimator.setEvaluator(mArgbEvaluator);
        mShowColorAnimator.setDuration(ANIM_TIME_SHOW);

        mHideColorAnimator = ObjectAnimator.ofArgb(this, "colorFilter", mPressColor, mDefaultColor);
        mHideColorAnimator.setEvaluator(mArgbEvaluator);
        mHideColorAnimator.setDuration(ANIM_TIME_SHOW);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                mDownTime = SystemClock.uptimeMillis();
                clearAnimation();
                animate().scaleX(SCALE_PRESSED)
                        .setInterpolator(mAlphaInterpolator)
                        .scaleY(SCALE_PRESSED)
                        .alpha(1f)
                        .setDuration(ANIM_TIME_SHOW)
                        .start();
                if (mHideColorAnimator.isStarted()) {
                    mHideColorAnimator.cancel();
                }

                mShowColorAnimator.start();
                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                clearAnimation();
                animate().scaleX(SCALE_NORMAL)
                        .setInterpolator(mAlphaInterpolator)
                        .scaleY(SCALE_NORMAL)
                        .setDuration(ANIM_TIME_SHOW)
                        .start();

                if (mShowColorAnimator.isStarted()) {
                    mShowColorAnimator.cancel();
                }

                mHideColorAnimator.start();

                if (SystemClock.uptimeMillis() - mDownTime < TIME_CLICK) {
                    performClick();
                }
                break;
        }

        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
