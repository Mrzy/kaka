
package cn.zmdx.kaka.locker;

import java.util.List;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.zmdx.kaka.locker.animation.AnimationFactory;
import cn.zmdx.kaka.locker.animation.AnimationFactory.FlipDirection;
import cn.zmdx.kaka.locker.content.IPandoraBox;
import cn.zmdx.kaka.locker.content.PandoraBoxDispatcher;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.LockPatternUtils;
import cn.zmdx.kaka.locker.widget.LockPatternView;
import cn.zmdx.kaka.locker.widget.LockPatternView.Cell;
import cn.zmdx.kaka.locker.widget.LockPatternView.DisplayMode;
import cn.zmdx.kaka.locker.widget.LockPatternView.OnPatternListener;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout.PanelSlideListener;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout.SimplePanelSlideListener;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class LockScreenManager {

    private SlidingUpPanelLayout mSliderView;

    private View mEntireView, mKeyholeView, mKeyView;

    private ViewFlipper mViewFlipper;

    private ViewGroup mBoxView;

    private static LockScreenManager INSTANCE = null;

    private WindowManager mWinManager = null;

    private boolean mIsLocked = false;

    private IPandoraBox mPandoraBox = null;

    private int mKeyholeMarginTop = -1;

    private Theme mCurTheme;

    private LockPatternView mLockPatternView;

    private TextView mGusturePrompt;

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

        mIsLocked = true;
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
    }

    private void refreshContent() {
        mPandoraBox = PandoraBoxManager.newInstance(HDApplication.getInstannce())
                .getNextPandoraData();
        View contentView = mPandoraBox.getRenderedView();
        if (contentView == null) {
            return;
        }
        mBoxView.removeView(contentView);
        ViewParent parent = contentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(contentView);
        }
        ViewGroup.LayoutParams lp = mBoxView.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mBoxView.addView(contentView, lp);
    }

    private void initLockScreenViews() {
        mEntireView = LayoutInflater.from(HDApplication.getInstannce()).inflate(
                R.layout.pandora_lockscreen, null);
        mBoxView = (ViewGroup) mEntireView.findViewById(R.id.flipper_box);
        mViewFlipper = (ViewFlipper) mEntireView.findViewById(R.id.viewFlipper);
        mLockPatternView = (LockPatternView) mEntireView.findViewById(R.id.gusture);
        mLockPatternView.setOnPatternListener(mPatternListener);
        mGusturePrompt = (TextView) mEntireView.findViewById(R.id.gusture_prompt);

        mSliderView = (SlidingUpPanelLayout) mEntireView.findViewById(R.id.locker_view);
        mKeyView = mEntireView.findViewById(R.id.lock_key);
        mSliderView.setPanelSlideListener(mSlideListener);
        mKeyholeView = (ImageView) mEntireView.findViewById(R.id.keyhole);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mKeyholeView.getLayoutParams();
        calKeyholeMarginTop();
        lp.setMargins(0, mKeyholeMarginTop, 0, 0);
        mKeyholeView.setLayoutParams(lp);
        setDrawable();
    }

    private void combineIcon(boolean combine) {
        if (combine) {
            mKeyView.setBackgroundResource(mCurTheme.getmKeyholeIconResId());
            mKeyholeView.setVisibility(View.INVISIBLE);
        } else {
            mKeyView.setBackgroundResource(mCurTheme.getmDragViewIconResId());
            mKeyholeView.setVisibility(View.VISIBLE);
        }
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

    private void setDrawable() {
        mCurTheme = ThemeManager.getCurrentTheme();
        mViewFlipper.setBackgroundResource(mCurTheme.getmBackgroundResId());
        mSliderView.setForegroundResource(mCurTheme.getmForegroundResId());
        mKeyView.setBackgroundResource(mCurTheme.getmDragViewIconResId());
        mKeyholeView.setBackgroundResource(mCurTheme.getmHoleIconResId());
    }

    public void unLock() {
        if (!mIsLocked)
            return;

        mWinManager.removeView(mEntireView);
        mIsShowGesture = false;
        mIsLocked = false;
        syncDataIfNeeded();
    }

    private void syncDataIfNeeded() {
        PandoraBoxDispatcher pd = PandoraBoxDispatcher.getInstance();
        pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_BAIDU_DATA);
        pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_SERVER_IMAGE_DATA);
        pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_SERVER_TEXT_DATA);
        if (!pd.hasMessages(PandoraBoxDispatcher.MSG_LOAD_BAIDU_IMG)) {
            pd.sendEmptyMessageDelayed(PandoraBoxDispatcher.MSG_LOAD_BAIDU_IMG, 10000);
        }
        if (!pd.hasMessages(PandoraBoxDispatcher.MSG_LOAD_SERVER_IMAGE)) {
            pd.sendEmptyMessageDelayed(PandoraBoxDispatcher.MSG_LOAD_SERVER_IMAGE, 5000);
        }
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    private void visibleKeyhole() {
        ViewHelper.setAlpha(mKeyholeView, 0);
        mKeyholeView.setVisibility(View.VISIBLE);

        ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(mKeyholeView, "alpha", 0, 1);
        fadeAnimator.setDuration(400);
        fadeAnimator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mSliderView.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mSliderView.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mSliderView.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        fadeAnimator.start();
    }

    private void invisibleKeyhole() {
        mKeyholeView.setVisibility(View.INVISIBLE);
    }

    private boolean mIsShowGesture = false;

    private boolean showGestureView() {
        int unlockType = PandoraConfig.newInstance(HDApplication.getInstannce()).getUnLockType();
        if (unlockType != PandoraConfig.UNLOCKER_TYPE_DEFAULT) {
            if (!mIsShowGesture) {
                AnimationFactory.flipTransition(mViewFlipper, FlipDirection.BOTTOM_TOP, 200);
            }
            mIsShowGesture = true;
            return true;
        }
        return false;
    }

    private OnPatternListener mPatternListener = new OnPatternListener() {

        @Override
        public void onPatternStart() {

        }

        @Override
        public void onPatternDetected(List<Cell> pattern) {
            verifyGustureLock(pattern);
        }

        @Override
        public void onPatternCleared() {

        }

        @Override
        public void onPatternCellAdded(List<Cell> pattern) {

        }
    };

    private void verifyGustureLock(List<Cell> pattern) {
        if (checkPattern(pattern)) {
            unLock();
        } else {
            mGusturePrompt.setText(HDApplication.getInstannce().getResources()
                    .getString(R.string.gusture_verify_fail));
            mLockPatternView.setDisplayMode(DisplayMode.Wrong);
        }
    }

    private boolean checkPattern(List<Cell> pattern) {
        PandoraConfig mPandoraConfig = PandoraConfig.newInstance(HDApplication.getInstannce());
        String stored = mPandoraConfig.getLockPaternString();
        if (!stored.equals(null)) {
            return stored.equals(LockPatternUtils.patternToString(pattern)) ? true : false;
        }
        return false;
    }

    private PanelSlideListener mSlideListener = new SimplePanelSlideListener() {

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            if (slideOffset <= 0.01) {
                combineIcon(true);
            } else {
                combineIcon(false);
            }
        }

        @Override
        public void onPanelCollapsed(View panel) {
            if (!showGestureView()) {
                unLock();
            }
        }

        @Override
        public void onPanelExpanded(View panel) {
            Log.e("zy", "onPanelExpanded");
            invisibleKeyhole();
            if (mIsShowGesture) {
                mViewFlipper.showPrevious();
                mIsShowGesture = false;
            }
        }

        @Override
        public void onPanelHidden(View panel) {

        }

        @Override
        public void onPanelFixed(View panel) {
            Log.e("zy", "onPanelFixed");

        }

        @Override
        public void onPanelClickedDuringFixed() {
            if (!showGestureView()) {
                unLock();
            }
        }

        public void onPanelStartDown(View view) {
            Log.e("zy", "onPanelStartDown");
            visibleKeyhole();
        };

        public void onPanelHiddenEnd() {
        };
    };
}
