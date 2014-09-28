
package cn.zmdx.kaka.locker;

import java.util.Calendar;
import java.util.List;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Vibrator;
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
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.service.PandoraService;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;
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
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

public class LockScreenManager {

    protected static final int MAX_TIMES_SHOW_GUIDE = 3;

    private SlidingUpPanelLayout mSliderView;

    private View mEntireView, mKeyholeView, mKeyView;

    private ViewFlipper mViewFlipper;

    private ViewGroup mBoxView, mKeyholeLayout;

    private static LockScreenManager INSTANCE = null;

    private WindowManager mWinManager = null;

    private PandoraConfig mPandoraConfig;

    private boolean mIsLocked = false;

    private IPandoraBox mPandoraBox = null;

    private int mKeyholeMarginTop = -1;

    private Theme mCurTheme;

    private LockPatternView mLockPatternView;

    private TextView mGusturePrompt;

    private Vibrator mVibrator;

    private TextView mDate;

    private KeyguardLock mKeyguard;

    private TextView mLockPrompt;

    private ImageView mLockArrow;

    private AnimatorSet mAnimatorSet;

    private ObjectAnimator mObjectAnimator;

    private int mTextGuideTimes;

    private long mLockTime;

    private LockScreenManager() {
        mWinManager = (WindowManager) HDApplication.getInstannce().getSystemService(
                Context.WINDOW_SERVICE);
        mVibrator = (Vibrator) HDApplication.getInstannce().getSystemService(
                Context.VIBRATOR_SERVICE);
        KeyguardManager keyguard = (KeyguardManager) HDApplication.getInstannce().getSystemService(
                Context.KEYGUARD_SERVICE);
        mKeyguard = keyguard.newKeyguardLock("pandora");
        mPandoraConfig = PandoraConfig.newInstance(HDApplication.getInstannce());
        disableSystemLock();
    }

    public void disableSystemLock() {
        mKeyguard.disableKeyguard();
    }

    public void enableSystemLock() {
        mKeyguard.reenableKeyguard();
    }

    public static LockScreenManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LockScreenManager();
        }
        return INSTANCE;
    }

    public void lock() {
        if (mIsLocked || PandoraService.isRinging())
            return;

        PandoraConfig pandoraConfig = PandoraConfig.newInstance(HDApplication.getInstannce());
        boolean isLockerOn = pandoraConfig.isPandolaLockerOn();
        if (!isLockerOn) {
            return;
        }

        checkNewVersion();

        String currentDate = getCurrentDate();
        UmengCustomEventManager.statisticalGuestureLockTime(pandoraConfig, currentDate);
        UmengCustomEventManager.statisticalUseTheme(pandoraConfig, currentDate);
        UmengCustomEventManager.statisticalEntryLockTimes(pandoraConfig, currentDate);

        mTextGuideTimes = pandoraConfig.getGuideTimesInt();
        mIsLocked = true;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_DISMISS_KEYGUARD
                | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | LayoutParams.FLAG_HARDWARE_ACCELERATED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            params.flags |= LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        }

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
        setDate();
        mWinManager.addView(mEntireView, params);
    }

    private void checkNewVersion() {
        PandoraConfig config = PandoraConfig.newInstance(HDApplication.getInstannce());
        String lastCheckTime = config.getFlagCheckNewVersion();
        String today = getCurrentDate();
        if (lastCheckTime.equals(today)) {
            return;
        }
        UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_NOTIFICATION);
        UmengUpdateAgent.update(HDApplication.getInstannce());
        config.setFlagCheckNewVersionTime(today);
    }

    private boolean mIsUseCurrentBox = false;

    private void refreshContent() {
        if (!mIsUseCurrentBox
                || (mPandoraBox != null && mPandoraBox.getCategory() == IPandoraBox.CATEGORY_DEFAULT)) {
            mPandoraBox = PandoraBoxManager.newInstance(HDApplication.getInstannce())
                    .getNextPandoraBox();
        }

        View contentView = mPandoraBox.getRenderedView();
        if (contentView == null) {
            return;
        }
        ViewParent parent = contentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(contentView);
        }
        ViewGroup.LayoutParams lp = mBoxView.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        contentView.requestLayout();
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
        mDate = (TextView) mEntireView.findViewById(R.id.lock_date);
        mLockPrompt = (TextView) mEntireView.findViewById(R.id.lock_prompt);
        mObjectAnimator = ObjectAnimator.ofFloat(mLockPrompt, "alpha", 1, 0.2f, 1);
        mObjectAnimator.setDuration(2000);
        mObjectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mObjectAnimator.setRepeatCount(-1);
        mObjectAnimator.start();

        mLockArrow = (ImageView) mEntireView.findViewById(R.id.lock_arrow1);
        int lenght = (int) HDApplication.getInstannce().getResources()
                .getDimension(R.dimen.locker_arrow_move_lenght);
        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(mLockArrow, "alpha", 0, 1, 0);
        objectAnimatorAlpha.setDuration(2000);
        objectAnimatorAlpha.setRepeatMode(ValueAnimator.RESTART);
        objectAnimatorAlpha.setRepeatCount(-1);
        ObjectAnimator objectAnimatorTranslate = ObjectAnimator.ofFloat(mLockArrow, "translationY",
                0, lenght);
        objectAnimatorTranslate.setDuration(2000);
        objectAnimatorTranslate.setRepeatMode(ValueAnimator.RESTART);
        objectAnimatorTranslate.setRepeatCount(-1);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(objectAnimatorTranslate, objectAnimatorAlpha);
        mAnimatorSet.start();

        mSliderView = (SlidingUpPanelLayout) mEntireView.findViewById(R.id.locker_view);
        mKeyView = mEntireView.findViewById(R.id.lock_key);
        mSliderView.setPanelSlideListener(mSlideListener);
        mKeyholeView = (ImageView) mEntireView.findViewById(R.id.keyhole);
        mKeyholeLayout = (ViewGroup) mEntireView.findViewById(R.id.keyholeLayout);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mKeyholeLayout.getLayoutParams();
        calKeyholeMarginTop();
        lp.setMargins(0, mKeyholeMarginTop, 0, 0);
        mKeyholeLayout.setLayoutParams(lp);
        setDrawable();
    }

    public void setDate() {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String weekString = PandoraUtils.getWeekString(HDApplication.getInstannce(), week);
        mDate.setText("" + month + "月" + "" + day + "日 " + weekString);
    }

    private String getCurrentDate() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return "" + year + "" + month + "" + day;
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
        mObjectAnimator.cancel();
        mObjectAnimator = null;
        mAnimatorSet.end();
        mAnimatorSet.cancel();
        mAnimatorSet = null;

        mWinManager.removeViewImmediate(mEntireView);
        mSliderView.recycle();
        mEntireView = null;
        mIsShowGesture = false;
        mIsLocked = false;
        syncDataIfNeeded();
    }

    private void syncDataIfNeeded() {
        PandoraBoxDispatcher pd = PandoraBoxDispatcher.getInstance();
        pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_BAIDU_DATA);
        pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_SERVER_IMAGE_JOKE);
        pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_SERVER_IMAGE_NEWS);
        pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_SERVER_TEXT_DATA);
        if (!pd.hasMessages(PandoraBoxDispatcher.MSG_LOAD_BAIDU_IMG)) {
            pd.sendEmptyMessageDelayed(PandoraBoxDispatcher.MSG_LOAD_BAIDU_IMG, 5000);
        }
        if (!pd.hasMessages(PandoraBoxDispatcher.MSG_LOAD_SERVER_IMAGE)) {
            pd.sendEmptyMessageDelayed(PandoraBoxDispatcher.MSG_LOAD_SERVER_IMAGE, 10000);
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
            UmengCustomEventManager.statisticalGuestureUnLockSuccess();
            unLock();
            mIsUseCurrentBox = false;
        } else {
            UmengCustomEventManager.statisticalGuestureUnLockFail();
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
            if (mTextGuideTimes < MAX_TIMES_SHOW_GUIDE) {
                if (slideOffset < 1 && slideOffset > 0) {
                    if (null != mLockPrompt) {
                        mLockPrompt.setText(HDApplication.getInstannce().getResources()
                                .getString(R.string.lock_guide_prompt_one));
                    }
                }
            }
        }

        @Override
        public void onPanelCollapsed(View panel) {
            UmengCustomEventManager.statisticalUnLockTimes();
            if (!showGestureView()) {
                unLock();
                //如果从开始下拉到直接解锁所经历的总时间小于1秒，则不更新数据
                if (System.currentTimeMillis() - mLockTime < 800) {
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("操作时间小于1秒，不更新pandoraBox,mLockTime:" + mLockTime);
                    }
                    mIsUseCurrentBox = true;
                } else {
                    mIsUseCurrentBox = false;
                }
            }
        }

        @Override
        public void onPanelExpanded(View panel) {
            if (null != mLockPrompt) {
                mLockPrompt.setText("");
            }
            if (null != mLockArrow) {
                mAnimatorSet.start();
                mLockArrow.setVisibility(View.VISIBLE);
            }
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
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("onPanelFixed");
            }
            UmengCustomEventManager.statisticalFixedTimes();
            mVibrator.vibrate(50);
            if (mTextGuideTimes < MAX_TIMES_SHOW_GUIDE) {
                if (null != mLockPrompt) {
                    mLockPrompt.setText(HDApplication.getInstannce().getResources()
                            .getString(R.string.lock_guide_prompt_two));
                }
            }
        }

        @Override
        public void onPanelClickedDuringFixed() {
            UmengCustomEventManager.statisticalFixedUnLockTimes();
            int duration = (int) (System.currentTimeMillis() - mLockTime);
            UmengCustomEventManager.statisticalLockTime(mPandoraBox, duration);
            if (!showGestureView()) {
                unLock();
                mIsUseCurrentBox = false;
            }
            if (mTextGuideTimes < MAX_TIMES_SHOW_GUIDE) {
                mPandoraConfig.saveGuideTimes(mTextGuideTimes + 1);
            }
        }

        public void onPanelStartDown(View view) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("onPanelStartDown");
            }
            visibleKeyhole();
            mLockTime = System.currentTimeMillis();
            if (null != mLockArrow) {
                mAnimatorSet.end();
                mLockArrow.setVisibility(View.GONE);
            }
        };

        public void onPanelHiddenEnd() {
        };
    };

}
