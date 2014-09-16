
package cn.zmdx.kaka.locker;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.content.DiskImageHelper;
import cn.zmdx.kaka.locker.content.IPandoraBox;
import cn.zmdx.kaka.locker.content.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.database.DatabaseModel;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout.PanelSlideListener;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout.SimplePanelSlideListener;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class LockScreenManager {

    private SlidingUpPanelLayout mSliderView;

    private View mEntireView, mKeyholeView;

    private ViewGroup mMainView;

    private static LockScreenManager INSTANCE = null;

    private WindowManager mWinManager = null;

    private boolean mIsLocked = false;

    private IPandoraBox mPandoraBox = null;

    private int mKeyholeMarginTop = -1;

    private LockScreenManager() {
        mWinManager = (WindowManager) HDApplication.getInstannce().getSystemService(
                Context.WINDOW_SERVICE);

    }

    public static LockScreenManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LockScreenManager();
        }
        return INSTANCE;
    }

    public void lock() {
        if (mIsLocked)
            return;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_DISMISS_KEYGUARD
                | LayoutParams.FLAG_LAYOUT_IN_SCREEN | LayoutParams.FLAG_HARDWARE_ACCELERATED;

        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        params.x = 0;
        params.y = 0;
        params.windowAnimations = R.style.anim_locker_window;
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        params.gravity = Gravity.TOP | Gravity.LEFT;

        initLockScreenViews();

        refreshContent();
        mWinManager.addView(mEntireView, params);

        mIsLocked = true;
    }

    private void refreshContent() {
        mPandoraBox = PandoraBoxManager.newInstance(HDApplication.getInstannce())
                .getNextPandoraData();
        View contentView = mPandoraBox.getRenderedView();
        if (contentView == null) {
            return;
        }
        mMainView.removeAllViews();
        mMainView.addView(contentView);
    }

    private void initLockScreenViews() {
        mEntireView = LayoutInflater.from(HDApplication.getInstannce()).inflate(
                R.layout.pandora_lockscreen, null);
        mMainView = (ViewGroup) mEntireView.findViewById(R.id.mainView);
        mSliderView = (SlidingUpPanelLayout) mEntireView.findViewById(R.id.locker_view);
        mSliderView.setPanelSlideListener(mSlideListener);
        mKeyholeView = (ImageView) mEntireView.findViewById(R.id.keyhole);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mKeyholeView.getLayoutParams();
        calKeyholeMarginTop();
        lp.setMargins(0, mKeyholeMarginTop, 0, 0);
        mKeyholeView.setLayoutParams(lp);
        setDrawable();
    }

    private void calKeyholeMarginTop() {
        if (mKeyholeMarginTop != -1) {
            return;
        }
        String screenHeight = BaseInfoHelper.getHeight(HDApplication.getInstannce());
        int padding = HDApplication.getInstannce().getResources()
                .getDimensionPixelOffset(R.dimen.locker_key_padding_top);
        mKeyholeMarginTop = Integer.parseInt(screenHeight) - mSliderView.getPanelHeight() + padding;
    }

    @SuppressWarnings("deprecation")
    private void setDrawable() {
        HDApplication context = HDApplication.getInstannce();
        Theme curTheme = ThemeManager.getCurrentTheme();
        Drawable foreDrawable = context.getResources().getDrawable(curTheme.getmForegroundResId());
        mSliderView.setForegroundDrawable(foreDrawable);
        Drawable backDrawable = context.getResources().getDrawable(curTheme.getmBackgroundResId());
        mMainView.setBackgroundDrawable(backDrawable);
    }

    public void unLock() {
        if (!mIsLocked)
            return;

        mWinManager.removeView(mEntireView);
        mIsLocked = false;
        releaseResource();
        // HDBThreadUtils.runOnWorker(new Runnable() {
        //
        // @Override
        // public void run() {
        // }
        // });
    }

    private void releaseResource() {
        PandoraData data = mPandoraBox.getData();
        if (data != null && data.getFrom() != PandoraBoxManager.DATA_FROM_DEFAULT) {
            Bitmap bmp = data.getmImage();
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
            DatabaseModel.getInstance().deleteById(data.getmId());
            DiskImageHelper.remove(data.getmImageUrl());
        }
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    private ObjectAnimator mFadeAnimator;

    private void visibleKeyhole() {
        ViewHelper.setAlpha(mKeyholeView, 0);
        mKeyholeView.setVisibility(View.VISIBLE);
        if (mFadeAnimator != null && mFadeAnimator.isRunning()) {
            mFadeAnimator.cancel();
        }
        mFadeAnimator = ObjectAnimator.ofFloat(mKeyholeView, "alpha", 0, 1);
        mFadeAnimator.setDuration(300).start();
    }

    private void invisibleKeyhole() {
        mFadeAnimator = ObjectAnimator.ofFloat(mKeyholeView, "alpha", 1, 0);
        mFadeAnimator.setDuration(300);
        if (mFadeAnimator != null && mFadeAnimator.isRunning()) {
            mFadeAnimator.cancel();
            mKeyholeView.setVisibility(View.VISIBLE);
        }

        mFadeAnimator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mKeyholeView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

        });
        mFadeAnimator.start();
    }

    private PanelSlideListener mSlideListener = new SimplePanelSlideListener() {

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
        }

        @Override
        public void onPanelCollapsed(View panel) {
            // mSliderView.hidePanel();
            unLock();
        }

        @Override
        public void onPanelExpanded(View panel) {
            invisibleKeyhole();
        }

        @Override
        public void onPanelHidden(View panel) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPanelFixed(View panel) {

        }

        @Override
        public void onPanelClickedDuringFixed() {
            Log.e("zy", "onPanelClickedDuringFixed");
            // 判断是否开启密码解锁
            // mSliderView.hidePanel();
            unLock();
        }

        public void onPanelStartDown(View view) {
            Log.e("zy", "onPanelStartDown");
            visibleKeyhole();
        };

        public void onPanelHiddenEnd() {
        };
    };
}
