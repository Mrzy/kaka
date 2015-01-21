
package cn.zmdx.kaka.fast.locker.widget;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;


/**
 * 一个特殊的LinearLayout,任何放入内部的clickable元素都具有波纹效果，当它被点击的时候， 为了性能，尽量不要在内部放入复杂的元素
 * note: long click listener is not supported current for fix compatible bug.
 */
public class RevealLayout extends LinearLayout implements Runnable {

    private static final String TAG = "DxRevealLayout";

    private static final boolean DEBUG = true;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mTargetWidth;

    private int mTargetHeight;

    private int mMinBetweenWidthAndHeight;

    private int mMaxBetweenWidthAndHeight;

    private int mMaxRevealRadius;

    private int mRevealRadiusGap;

    private int mRevealRadius = 0;

    private float mCenterX;

    private float mCenterY;

    private int[] mLocationInScreen = new int[2];

    private boolean mShouldDoAnimation = true;

    private boolean mIsPressed = false;

    private View mTouchTarget;

    private DispatchUpTouchEventRunnable mDispatchUpTouchEventRunnable = new DispatchUpTouchEventRunnable();

    public RevealLayout(Context context) {
        super(context);
        init();
    }

    public RevealLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RevealLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        mPaint.setColor(Color.parseColor("#20ffffff"));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.getLocationOnScreen(mLocationInScreen);
    }

    private void initParametersForChild(MotionEvent event, View view) {
        mCenterX = event.getX();
        mCenterY = event.getY();
        mTargetWidth = view.getMeasuredWidth();
        mTargetHeight = view.getMeasuredHeight();
        mMinBetweenWidthAndHeight = Math.min(mTargetWidth, mTargetHeight);
        mMaxBetweenWidthAndHeight = Math.max(mTargetWidth, mTargetHeight);
        mRevealRadius = 0;
        mShouldDoAnimation = true;
        mIsPressed = true;
        
        int top = view.getTop();
        int bottom = view.getBottom();
        int right = view.getRight();
        int left = view.getLeft();
        
        int a = getDistance(mCenterX, mCenterY, left, top);
        int b = getDistance(mCenterX, mCenterY, left, bottom);
        int c = getDistance(mCenterX, mCenterY, right, top);
        int d = getDistance(mCenterX, mCenterY, right, bottom);
        mMaxRevealRadius = Math.max(Math.max(a, b), Math.max(c, d));
//        mRevealRadiusGap = mMinBetweenWidthAndHeight / 8;

//        int[] location = new int[2];
//        view.getLocationOnScreen(location);
//        int left = location[0] - mLocationInScreen[0];
//        int transformedCenterX = (int) mCenterX - left;
//        mMaxRevealRadius = Math.max(transformedCenterX, mTargetWidth - transformedCenterX);
    }

    private int getDistance(float startX, float startY, int targetX, int targetY) {
        int a = (int) Math.abs(startX - targetX);
        int b = (int) Math.abs(startY - targetY);
        return (int) Math.sqrt(a * a + b * b);
    }

    private RadialGradient mRadialGradient;

    private Interpolator mRadiusInterpolator = new AccelerateInterpolator();

    private long mStartTime = 0;

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!mShouldDoAnimation || mTargetWidth <= 0 || mTouchTarget == null) {
            return;
        }

        if (mStartTime == 0) {
            mStartTime = SystemClock.elapsedRealtime();
        }
        float inter = (float) (SystemClock.elapsedRealtime() - mStartTime) / 500;
        if (inter > 1.0) {
            mStartTime = 0;
            return;
        } else {
            float value = mRadiusInterpolator.getInterpolation(inter);
            mRevealRadius = (int) (value * mMaxRevealRadius);
        }

        this.getLocationOnScreen(mLocationInScreen);
        int[] location = new int[2];
        mTouchTarget.getLocationOnScreen(location);
        int left = location[0] - mLocationInScreen[0];
        int top = location[1] - mLocationInScreen[1];
        int right = left + mTouchTarget.getMeasuredWidth();
        int bottom = top + mTouchTarget.getMeasuredHeight();

        canvas.save();
        canvas.clipRect(left, top, right, bottom);
        canvas.drawCircle(mCenterX, mCenterY, mRevealRadius, mPaint);
        canvas.restore();
        postInvalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            View touchTarget = getTouchTarget(this, x, y);
            if (touchTarget != null && touchTarget.isClickable() && touchTarget.isEnabled()) {
                mTouchTarget = touchTarget;
                initParametersForChild(event, touchTarget);
                postInvalidate();
            }
        } else if (action == MotionEvent.ACTION_UP) {
            mIsPressed = false;
            postInvalidate();
            mDispatchUpTouchEventRunnable.event = event;
            postDelayed(mDispatchUpTouchEventRunnable, 40);
            return true;
        } else if (action == MotionEvent.ACTION_CANCEL) {
            mIsPressed = false;
            postInvalidate();
        }

        return super.dispatchTouchEvent(event);
    }

    private View getTouchTarget(View view, int x, int y) {
        View target = null;
        ArrayList<View> TouchableViews = view.getTouchables();
        for (View child : TouchableViews) {
            if (isTouchPointInView(child, x, y)) {
                target = child;
                break;
            }
        }

        return target;
    }

    private boolean isTouchPointInView(View view, int x, int y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (view.isClickable() && y >= top && y <= bottom && x >= left && x <= right) {
            return true;
        }
        return false;
    }

    @Override
    public boolean performClick() {
        postDelayed(this, 400);
        return true;
    }

    @Override
    public void run() {
        super.performClick();
    }

    private class DispatchUpTouchEventRunnable implements Runnable {
        public MotionEvent event;

        @Override
        public void run() {
            if (mTouchTarget == null || !mTouchTarget.isEnabled()) {
                return;
            }

            if (isTouchPointInView(mTouchTarget, (int) event.getRawX(), (int) event.getRawY())) {
                mTouchTarget.performClick();
            }
        }
    };

}
