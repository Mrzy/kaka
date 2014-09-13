
package cn.zmdx.kaka.locker.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.MImageUtils;

public class LockerViewGroup extends FrameLayout {

    private PandoraBoxView mPandoraBoxView;

    private ViewGroup mTopContainerView, mBottomContainerView;

    private View mLockBtn;

    private Scroller scroller;// 滑动控制器

    private VelocityTracker velocityTracker;// 用于得到手势在屏幕上的滑动速度

    private int mTopViewHeight, mBottomViewHeight;

    private int mWinWidth, mWinHeight;

    public interface LockScreenListener {
        void onStartTouch();

        void onDeadLine();

        void onFixed();
    }

    public LockScreenListener mLockListener;

    public LockerViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LockerViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LockerViewGroup(Context context) {
        super(context);
        init();
    }

    private void init() {
        try {
            mWinWidth = Integer.parseInt(BaseInfoHelper.getWidth(getContext()));
            mWinHeight = Integer.parseInt(BaseInfoHelper.getHeight(getContext()));
            Log.e("zy", "mWinWidth and mWinHeight:" + mWinWidth + "," + mWinHeight);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public void setOnLockScreenListener(LockScreenListener listen) {
        mLockListener = listen;
    }

    @Override
    protected void onFinishInflate() {
        int childCount = getChildCount();
        if (childCount != 2) {
            throw new RuntimeException("只能包含3个容器view对象");
        }
        mPandoraBoxView = (PandoraBoxView) getChildAt(0);
        mTopContainerView = (ViewGroup) findViewById(R.id.lock_top);
        mBottomContainerView = (ViewGroup) findViewById(R.id.lock_bottom);
        changeLockerScreenRatio(0.4f);
        mLockBtn = findViewById(R.id.lock_btn);
        mLockBtn.setOnTouchListener(mTouchListener);

        super.onFinishInflate();
    }

    /**
     * 更新pandora box中的内容
     */
    public void updateBoxContent() {
        mPandoraBoxView.updateContent();
    }

    /**
     * 设置屏幕上半部分和下半部分的比例
     * 
     * @param ratio 屏幕上半部分和下半部分的比例值
     */
    public void changeLockerScreenRatio(float ratio) {
        if (ratio > 1 || ratio < 0) {
            throw new IllegalArgumentException("ratio必须为[0, 1]之间的浮点数");
        }
        int height = Integer.parseInt(BaseInfoHelper.getHeight(getContext()));
        mTopViewHeight = (int) (height * ratio);
        mBottomViewHeight = height - mTopViewHeight;

        ViewGroup.LayoutParams lp = mTopContainerView.getLayoutParams();
        lp.height = mTopViewHeight;
        mTopContainerView.setLayoutParams(lp);
        lp = mBottomContainerView.getLayoutParams();
        lp.height = mBottomViewHeight;
        mBottomContainerView.setLayoutParams(lp);
    }

    @SuppressWarnings("deprecation")
    public void setForegroundDrawable(Drawable drawable) {
        Bitmap srcBmp = MImageUtils.drawable2Bitmap(drawable);
        srcBmp = MImageUtils.scaleTo(srcBmp, mWinWidth, mWinHeight);
        Bitmap topBmp = Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), mTopViewHeight);
        Log.e("zy", "topBmp.height:" + topBmp.getHeight());
        Bitmap bottomBmp = Bitmap.createBitmap(srcBmp, 0, mTopViewHeight, srcBmp.getWidth(),
                mBottomViewHeight);
        Log.e("zy", "bottomBmp.height:" + bottomBmp.getHeight());
        mTopContainerView.setBackgroundDrawable(MImageUtils.bitmap2Drawable(getContext(), topBmp));
        mBottomContainerView.setBackgroundDrawable(MImageUtils.bitmap2Drawable(getContext(),
                bottomBmp));
        srcBmp.recycle();
        srcBmp = null;
    }

    public void setForegroundResource(int res) {
        Drawable drawable = getResources().getDrawable(res);
        setForegroundDrawable(drawable);
    }

    @SuppressWarnings("deprecation")
    public void setBackgroundDrawable(Drawable drawable) {
        mPandoraBoxView.setBackgroundDrawable(drawable);

    }

    public void setBackgroundResource(int resid) {
        mPandoraBoxView.setBackgroundResource(resid);

    }

    private float mStartX, mStartY;

    private OnTouchListener mTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mStartY = event.getRawY();
                    // Rect rect = new Rect();
                    // mLockBtn.getHitRect(rect);
                    // mLockBtn.getDrawingRect(rect);
                    // mLockBtn.getFocusedRect(rect);
                    // mLockBtn.getGlobalVisibleRect(rect);
                    // mLockBtn.getLocalVisibleRect(rect);

                    break;
                case MotionEvent.ACTION_MOVE:
                    handleMove(event);
                    break;
                case MotionEvent.ACTION_UP:
                    // mBottomContainerView.layout(0, 1000, mWinWidth, 1900);
                    // mBottomContainerView.scrollBy(0, -100);
                    mLockListener.onDeadLine();

                    break;
            }
            return false;
        }
    };

    private void handleMove(MotionEvent event) {
        int curRawY = (int)event.getRawY();
         mBottomContainerView.layout(0, curRawY, mWinWidth, curRawY+mBottomViewHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();// 取得手势在屏幕上的滑动速度
                    velocityTracker.addMovement(event);
                }

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                if (velocityTracker != null) {
                    velocityTracker.recycle();// 回收
                    velocityTracker = null;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
