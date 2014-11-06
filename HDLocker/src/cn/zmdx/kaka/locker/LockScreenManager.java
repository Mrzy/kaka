
package cn.zmdx.kaka.locker;

import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.battery.PandoraBatteryManager;
import cn.zmdx.kaka.locker.content.PandoraBoxDispatcher;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.content.box.GifBox;
import cn.zmdx.kaka.locker.content.box.IFoldableBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.service.PandoraService;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.LockPatternUtils;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager.IWeatherCallback;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager.PandoraWeather;
import cn.zmdx.kaka.locker.widget.LockPatternView;
import cn.zmdx.kaka.locker.widget.LockPatternView.Cell;
import cn.zmdx.kaka.locker.widget.LockPatternView.DisplayMode;
import cn.zmdx.kaka.locker.widget.LockPatternView.OnPatternListener;
import cn.zmdx.kaka.locker.widget.PandoraPanelLayout;
import cn.zmdx.kaka.locker.widget.PandoraPanelLayout.PanelSlideListener;
import cn.zmdx.kaka.locker.widget.PandoraPanelLayout.SimplePanelSlideListener;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

@SuppressWarnings("deprecation")
public class LockScreenManager {

    protected static final int MAX_TIMES_SHOW_GUIDE = 3;

    private PandoraPanelLayout mSliderView;

    private View mEntireView;

    private ViewGroup mBoxView;

    private static LockScreenManager INSTANCE = null;

    private WindowManager mWinManager = null;

    private PandoraConfig mPandoraConfig;

    private boolean mIsLocked = false;

    private IPandoraBox mPandoraBox = null;

    // private int mKeyholeMarginTop = -1;

    private Theme mCurTheme;

    private LockPatternView mLockPatternView;

    private TextView mGusturePrompt;

    private Vibrator mVibrator;

    private TextView mDate, mBatteryTipView, mWeatherSummary;

    private View mDigitalClockView;

    private KeyguardLock mKeyguard;

    private TextView mLockPrompt;

    private ImageView mLockArrow;

    private AnimatorSet mAnimatorSet;

    private ObjectAnimator mObjectAnimator;

    private int mTextGuideTimes;

    private long mLockTime;

    private Context mContext;

    private SlidingUpPanelLayout mContentLayout;

    WindowManager.LayoutParams mWinParams;

    private ILockScreenListener mLockListener = null;

    public interface ILockScreenListener {
        void onLock();

        void onUnLock();

        void onInitDefaultImage();
    }

    public void setOnLockScreenListener(ILockScreenListener listener) {
        mLockListener = listener;
    }

    private LockScreenManager() {
        mContext = HDApplication.getInstannce();
        mWinManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        KeyguardManager keyguard = (KeyguardManager) mContext
                .getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguard = keyguard.newKeyguardLock("pandora");
        mPandoraConfig = PandoraConfig.newInstance(mContext);
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void lock() {
        if (mIsLocked || PandoraService.isRinging())
            return;

        PandoraConfig pandoraConfig = PandoraConfig.newInstance(mContext);
        boolean isLockerOn = pandoraConfig.isPandolaLockerOn();
        if (!isLockerOn) {
            return;
        }

        mIsLocked = true;

        mTextGuideTimes = pandoraConfig.getGuideTimesInt();
        mWinParams = new WindowManager.LayoutParams();

        mWinParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mWinParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_DISMISS_KEYGUARD
                | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | LayoutParams.FLAG_HARDWARE_ACCELERATED | LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        if (!PandoraConfig.newInstance(mContext).isNeedNotice()) {
            mWinParams.flags |= LayoutParams.FLAG_FULLSCREEN;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            mWinParams.flags |= LayoutParams.FLAG_TRANSLUCENT_STATUS;
            mWinParams.flags |= LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        }
        mWinParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        final Display display = mWinManager.getDefaultDisplay();
        mWinParams.height = BaseInfoHelper.getRealHeight(display);

        mWinParams.x = 0;
        mWinParams.y = 0;
        // params.format=PixelFormat.RGBA_8888;
        mWinParams.windowAnimations = R.style.anim_locker_window;
        mWinParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        mWinParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        mWinParams.gravity = Gravity.TOP | Gravity.START;

        initLockScreenViews();

        refreshContent();
        setDate();
        mWinManager.addView(mEntireView, mWinParams);
        startFakeActivity();

        notifyLocked();
        onBatteryStatusChanged(PandoraBatteryManager.getInstance().getBatteryStatus());
        syncDataIfNeeded();

        checkNewVersion();

        String currentDate = BaseInfoHelper.getCurrentDate();
        UmengCustomEventManager.statisticalGuestureLockTime(pandoraConfig, currentDate);
        UmengCustomEventManager.statisticalUseTheme(pandoraConfig, currentDate);
        UmengCustomEventManager.statisticalEntryLockTimes(pandoraConfig, currentDate);
    }

    public void setWindowAnimations(int anim) {
        mWinParams.windowAnimations = anim;
        mWinManager.updateViewLayout(mEntireView, mWinParams);
    }

    private void notifyLocked() {
        if (mLockListener != null) {
            mLockListener.onLock();
        }
    }

    private void notifyUnLocked() {
        if (mLockListener != null) {
            mLockListener.onUnLock();
        }
    }

    private void startFakeActivity() {
        Intent intent = new Intent(mContext, FakeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void processWeatherInfo() {
        final long lastCheckTime = mPandoraConfig.getLastCheckWeatherTime();
        if (System.currentTimeMillis() - lastCheckTime < PandoraPolicy.MIN_CHECK_WEATHER_DURAION) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("检查天气条件不满足,使用缓存数据");
            }
            final String info = mPandoraConfig.getLastWeatherInfo();
            if (!TextUtils.isEmpty(info)) {
                try {
                    String[] wi = info.split("#");
                    PandoraWeather pw = new PandoraWeather();
                    pw.setTemp(Integer.parseInt(wi[0]));
                    pw.setSummary(wi[1]);
                    updateWeatherInfo(pw);
                } catch (Exception e) {
                    updateWeatherInfo(null);
                }
            } else {
                updateWeatherInfo(null);
            }
            return;
        }
        // TODO
        String promptString = PandoraUtils.getTimeQuantumString(mContext, Calendar.getInstance()
                .get(Calendar.HOUR_OF_DAY));
        mWeatherSummary.setText(promptString);
        PandoraWeatherManager.getInstance().getCurrentWeather(new IWeatherCallback() {

            @Override
            public void onSuccess(PandoraWeather pw) {
                final int temp = pw.getTemp();
                final String summary = pw.getSummary();
                mPandoraConfig.saveLastWeatherInfo(temp + "#" + summary);
                updateWeatherInfo(pw);
                mPandoraConfig.saveLastCheckWeatherTime(System.currentTimeMillis());
            }

            @Override
            public void onFailed() {
                updateWeatherInfo(null);
            }
        });

    }

    private void updateWeatherInfo(final PandoraWeather pw) {
        HDBThreadUtils.runOnUi(new Runnable() {

            @Override
            public void run() {
                if (mWeatherSummary == null) {
                    return;
                }
                if (pw == null) {
                    String welcomeString = PandoraConfig.newInstance(mContext).getWelcomeString();
                    if (!TextUtils.isEmpty(welcomeString)) {
                        mWeatherSummary.setText(welcomeString);
                        mWeatherSummary.setVisibility(View.VISIBLE);
                    } else {
                        String promptString = PandoraUtils.getTimeQuantumString(mContext, Calendar
                                .getInstance().get(Calendar.HOUR_OF_DAY));
                        mWeatherSummary.setText(promptString);
                        mWeatherSummary.setVisibility(View.VISIBLE);
                    }
                } else {
                    int temp = pw.getTemp();
                    String summary = pw.getSummary();
                    if (mDate != null) {
                        if (mDate.getText() != null && !mDate.getText().toString().endsWith("ºC")) {
                            mDate.append(" " + temp + "ºC");
                        }
                    }
                    if (mWeatherSummary == null) {
                        return;
                    }
                    if (summary.contains("未来一小时")) {
                        String promptString = PandoraUtils.getTimeQuantumString(mContext, Calendar
                                .getInstance().get(Calendar.HOUR_OF_DAY));
                        summary = promptString;
                    }
                    mWeatherSummary.setVisibility(View.VISIBLE);
                    mWeatherSummary.setText(summary);
                }
            }
        });

    }

    private void checkNewVersion() {
        PandoraConfig config = PandoraConfig.newInstance(mContext);
        String lastCheckTime = config.getFlagCheckNewVersion();
        String today = BaseInfoHelper.getCurrentDate();
        if (lastCheckTime.equals(today)) {
            return;
        }
        UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_NOTIFICATION);
        UmengUpdateAgent.update(mContext);
        config.setFlagCheckNewVersionTime(today);
    }

    private boolean mIsUseCurrentBox = false;

    private void refreshContent() {
        // if (!mIsUseCurrentBox
        // || (mPandoraBox != null && mPandoraBox.getCategory() ==
        // IPandoraBox.CATEGORY_DEFAULT)) {
        // mPandoraBox =
        // PandoraBoxManager.newInstance(mContext).getNextPandoraBox();
        //
        // }

        IFoldableBox box = PandoraBoxManager.newInstance(mContext).getFoldableBox();

        View contentView = box.getRenderedView();
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
        mBoxView.addView(contentView, 0, lp);
    }

    @SuppressLint("InflateParams")
    private void initLockScreenViews() {
        mEntireView = LayoutInflater.from(mContext).inflate(R.layout.pandora_lockscreen, null);
        mBatteryTipView = (TextView) mEntireView.findViewById(R.id.batteryTip);
        mBoxView = (ViewGroup) mEntireView.findViewById(R.id.flipper_box);
        initSecurePanel();
        mDate = (TextView) mEntireView.findViewById(R.id.lock_date);
        // mDate.setAlpha(0);
        mLockPrompt = (TextView) mEntireView.findViewById(R.id.lock_prompt);
        mWeatherSummary = (TextView) mEntireView.findViewById(R.id.weather_summary);
        // mWeatherSummary.setAlpha(0);
        mDigitalClockView = mEntireView.findViewById(R.id.digitalClock);
        // mDigitalClockView.setAlpha(0);

        mLockArrow = (ImageView) mEntireView.findViewById(R.id.lock_arrow1);

        mSliderView = (PandoraPanelLayout) mEntireView.findViewById(R.id.locker_view);
        mSliderView.setPanelSlideListener(mSlideListener);
        setDrawable();
    }

    private void initSecurePanel() {
        if (mPandoraConfig.getUnLockType() == PandoraConfig.UNLOCKER_TYPE_DEFAULT) {
            // 若没有开启密码锁，直接返回，无须初始化相关view
            return;
        }
        ViewStub stub = (ViewStub) mEntireView.findViewById(R.id.gesture_stub);
        ViewGroup view = (ViewGroup) stub.inflate();
        mContentLayout = (SlidingUpPanelLayout) mEntireView.findViewById(R.id.content);
        mContentLayout.setDragView(view.findViewById(R.id.fakeDragView));
        // mContentLayout.setAnchorPoint(0.8f);
        mLockPatternView = (LockPatternView) view.findViewById(R.id.gusture);
        mLockPatternView.setOnPatternListener(mPatternListener);
        mGusturePrompt = (TextView) view.findViewById(R.id.gusture_prompt);
    }

    public void setDate() {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String weekString = PandoraUtils.getWeekString(mContext, week);
        mDate.setText("" + month + "月" + "" + day + "日 " + weekString);
    }

    private void setDrawable() {
        mCurTheme = ThemeManager.getCurrentTheme();
        if (mCurTheme.isCustomWallpaper()) {
            mSliderView.setForegroundDrawable(mCurTheme.getmCustomBitmap());
        } else {
            // mViewFlipper.setBackgroundResource(mCurTheme.getmBackgroundResId());
            mSliderView.setForegroundResource(mCurTheme.getmForegroundResId());
        }
    }

    public void onInitDefaultImage() {
        if (mLockListener != null) {
            mLockListener.onInitDefaultImage();
        }
    }

    /**
     * 设置当调用unlock解锁后要执行的动作，解锁后，自动置为null
     * 
     * @param runnable
     */
    public void setRunnableAfterUnLock(Runnable runnable) {
        mUnLockRunnable = runnable;
    }

    /**
     * 调用会解锁，如果开启安全锁，会跳转到安全锁界面；如果希望在解锁后执行其它动作，请在调用unLock()
     * 之前使用setRunnaleAfterUnLock()方法设置
     * 默认会关闭背后的假Activity，如果不希望关闭这个Activity，可以使用unLock(boolean
     * isCloseFakeActivity)方法
     */
    public void unLock() {
        if (!showGestureView()) {
            internalUnLock();
        }
    }

    /**
     * 解锁
     * 
     * @param isCloseFakeActivity 解锁同时，是否关闭背后的假activity,默认为true
     * @param forceClose 如果为true，则忽略密码锁，直接解锁，比如来电话时
     */
    public void unLock(boolean isCloseFakeActivity, boolean forceClose) {
        if (forceClose) {
            internalUnLock(true);
            return;
        }
        if (!showGestureView()) {
            internalUnLock(isCloseFakeActivity);
        }
    }

    private void internalUnLock() {
        internalUnLock(true);
    }

    private void internalUnLock(boolean isCloseFakeActivity) {
        if (!mIsLocked)
            return;
        if (isCloseFakeActivity)
            notifyUnLocked();
        cancelAnimatorIfNeeded();

        if (mUnLockRunnable != null) {
            mWinManager.removeView(mEntireView);
        } else {
            mWinManager.removeViewImmediate(mEntireView);
        }
        mSliderView.recycle();
        mEntireView = null;
        mIsShowGesture = false;
        mIsLocked = false;

        if (mUnLockRunnable != null) {
            HDBThreadUtils.runOnUi(mUnLockRunnable);
            mUnLockRunnable = null;
        }
    }

    private void cancelAnimatorIfNeeded() {
        if (null != mObjectAnimator) {
            mObjectAnimator.cancel();
            mObjectAnimator = null;
        }
        if (null != mAnimatorSet) {
            mAnimatorSet.end();
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

    private long mLastSyncDataTime = 0;

    private void syncDataIfNeeded() {
        long curTime = System.currentTimeMillis();
        long delta = curTime - mLastSyncDataTime;
        if (delta > PandoraPolicy.MIN_DURATION_SYNC_DATA_TIME) {
            PandoraBoxDispatcher pd = PandoraBoxDispatcher.getInstance();
            pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_ORIGINAL_DATA);
            pd.sendEmptyMessageDelayed(PandoraBoxDispatcher.MSG_DOWNLOAD_IMAGES, 4000);
            mLastSyncDataTime = curTime;
        }
        // pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_BAIDU_DATA);
        // pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_SERVER_IMAGE_JOKE);
        // pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_SERVER_IMAGE_NEWS);
        // pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_SERVER_TEXT_DATA);
        // pd.sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_SERVER_GIF);
        // if (!pd.hasMessages(PandoraBoxDispatcher.MSG_LOAD_BAIDU_IMG)) {
        // pd.sendEmptyMessageDelayed(PandoraBoxDispatcher.MSG_LOAD_BAIDU_IMG,
        // 4000);
        // }
        // if (!pd.hasMessages(PandoraBoxDispatcher.MSG_LOAD_SERVER_IMAGE_JOKE))
        // {
        // pd.sendEmptyMessageDelayed(PandoraBoxDispatcher.MSG_LOAD_SERVER_IMAGE_JOKE,
        // 5000);
        // }
        // if (!pd.hasMessages(PandoraBoxDispatcher.MSG_LOAD_SERVER_IMAGE_NEWS))
        // {
        // pd.sendEmptyMessageDelayed(PandoraBoxDispatcher.MSG_LOAD_SERVER_IMAGE_NEWS,
        // 6000);
        // }
        // if (!pd.hasMessages(PandoraBoxDispatcher.MSG_LOAD_SERVER_GIF)) {
        // pd.sendEmptyMessageDelayed(PandoraBoxDispatcher.MSG_LOAD_SERVER_GIF,
        // 7000);
        // }

    }

    public boolean isLocked() {
        return mIsLocked;
    }

    private boolean mIsShowGesture = false;

    private Runnable mUnLockRunnable = null;

    private boolean showGestureView() {
        int unlockType = PandoraConfig.newInstance(mContext).getUnLockType();
        if (unlockType != PandoraConfig.UNLOCKER_TYPE_DEFAULT) {
            if (!mIsShowGesture) {
                mContentLayout.expandPanel();
                mIsShowGesture = true;
            } else {
                mContentLayout.collapsePanel();
                mIsShowGesture = false;
            }
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
            mGusturePrompt.setText("");
            HDBThreadUtils.postOnUiDelayed(new Runnable() {

                @Override
                public void run() {
                    internalUnLock();
                }
            }, 1);
            mIsUseCurrentBox = false;
        } else {
            UmengCustomEventManager.statisticalGuestureUnLockFail();
            mGusturePrompt.setText(mContext.getResources().getString(R.string.gusture_verify_fail));
            mLockPatternView.setDisplayMode(DisplayMode.Wrong);
        }
    }

    private boolean checkPattern(List<Cell> pattern) {
        PandoraConfig mPandoraConfig = PandoraConfig.newInstance(mContext);
        String stored = mPandoraConfig.getLockPaternString();
        if (!stored.equals(null)) {
            return stored.equals(LockPatternUtils.patternToString(pattern)) ? true : false;
        }
        return false;
    }

    private void startGifAnimationIfNeeded() {
        if (mPandoraBox.getCategory() == IPandoraBox.CATEGORY_GIF) {
            GifBox gifBox = (GifBox) mPandoraBox;
            gifBox.startGif();
        }
    }

    private void stopGifAnimationIfNeeded() {
        if (mPandoraBox.getCategory() == IPandoraBox.CATEGORY_GIF) {
            GifBox gifBox = (GifBox) mPandoraBox;
            gifBox.stopGif();
        }
    }

    private PanelSlideListener mSlideListener = new SimplePanelSlideListener() {

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            if (mTextGuideTimes < MAX_TIMES_SHOW_GUIDE) {
                if (slideOffset < 1 && slideOffset > 0) {
                    if (null != mLockPrompt) {
                        mLockPrompt.setText(mContext.getResources().getString(
                                R.string.lock_guide_prompt_one));
                    }
                }
            }
        }

        @Override
        public void onPanelCollapsed(View panel) {
            UmengCustomEventManager.statisticalUnLockTimes();
            if (!showGestureView()) {
                internalUnLock();
                // 如果从开始下拉到直接解锁所经历的总时间小于1秒，则不更新数据
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
                if (null != mAnimatorSet) {
                    mAnimatorSet.start();
                }
                mLockArrow.setVisibility(View.VISIBLE);
            }
            if (mIsShowGesture) {
                mContentLayout.collapsePanel();
                mIsShowGesture = false;
            }
            // stopGifAnimationIfNeeded();
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
                    mLockPrompt.setText(mContext.getResources().getString(
                            R.string.lock_guide_prompt_two));
                }
            }
        }

        @Override
        public void onPanelClickedDuringFixed() {
            UmengCustomEventManager.statisticalFixedUnLockTimes();
            int duration = (int) (System.currentTimeMillis() - mLockTime);
            UmengCustomEventManager.statisticalLockTime(mPandoraBox, duration);
            if (!showGestureView()) {
                internalUnLock();
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
            mLockTime = System.currentTimeMillis();
            if (null != mLockArrow) {
                if (null != mAnimatorSet) {
                    mAnimatorSet.end();
                }
                mLockArrow.setVisibility(View.GONE);
            }

            // startGifAnimationIfNeeded();
        };

        public void onPanelHiddenEnd() {
        };

        public boolean onPanelFastDown(float y) {
            if (y > PandoraPolicy.DEFAULT_MAX_YVEL) {// 向下滑的速度达到一定值时，直接解锁
                mSliderView.collapsePanel();
                return true;
            }
            return false;
        };
    };

    public void onBatteryStatusChanged(int mStatus) {
        if (isLocked() && mBatteryTipView != null) {
            final PandoraBatteryManager pbm = PandoraBatteryManager.getInstance();
            final Resources resource = mContext.getResources();
            switch (mStatus) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    final int maxScale = pbm.getMaxScale();
                    final int curScale = pbm.getCurLevel();
                    final float rate = (float) curScale / (float) maxScale;
                    int percent = (int) (rate * 100.0);
                    mBatteryTipView.setVisibility(View.VISIBLE);
                    mBatteryTipView.setText(resource
                            .getString(R.string.pandora_box_battery_charging) + percent + "%");
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    mBatteryTipView.setVisibility(View.GONE);
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    mBatteryTipView.setVisibility(View.VISIBLE);
                    mBatteryTipView.setText(resource.getString(R.string.pandora_box_battery_full));
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    mBatteryTipView.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    }

    public void onScreenOff() {
        invisiableViews(mDate, mWeatherSummary, mDigitalClockView);
        cancelAnimatorIfNeeded();
    }

    public void onScreenOn() {
        if (mIsLocked) {
            processWeatherInfo();
            processAnimations();
        }
    }

    private void invisiableViews(View... views) {
        for (View view : views) {
            if (view != null)
                view.setAlpha(0);
        }
    }

    private float mBoxRate = -1;

    public float getBoxWidthHeightRate() {
        if (mBoxRate != -1) {
            return mBoxRate;
        }
        if (mSliderView == null) {
            return 1.0f;
        }
        float rate = mSliderView.getContentViewWidthHeightRate();
        if (rate == 0) {
            rate = 1.0f;
        }
        mBoxRate = rate;
        return mBoxRate;
    }

    private void processAnimations() {
        ObjectAnimator digitalAlpha = ObjectAnimator.ofFloat(mDigitalClockView, "alpha", 0, 1);
        ObjectAnimator digitalTrans = ObjectAnimator.ofFloat(mDigitalClockView, "translationY",
                -400, 0);
        AnimatorSet digitalSet = new AnimatorSet();
        digitalSet.playTogether(digitalAlpha, digitalTrans);

        ObjectAnimator dateAlpha = ObjectAnimator.ofFloat(mDate, "alpha", 0, 1);
        ObjectAnimator dateTrans = ObjectAnimator.ofFloat(mDate, "translationY", -400, 0);
        AnimatorSet dateSet = new AnimatorSet();
        dateSet.setStartDelay(100);
        dateSet.playTogether(dateAlpha, dateTrans);

        ObjectAnimator wsAlpha = ObjectAnimator.ofFloat(mWeatherSummary, "alpha", 0, 1);
        ObjectAnimator wsTrans = ObjectAnimator.ofFloat(mWeatherSummary, "translationY", -400, 0);
        AnimatorSet wsSet = new AnimatorSet();
        wsSet.setStartDelay(200);
        wsSet.playTogether(wsAlpha, wsTrans);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playTogether(digitalSet, dateSet, wsSet);
        finalSet.setDuration(700);
        finalSet.setStartDelay(20);
        // finalSet.setInterpolator(new OvershootInterpolator());
        finalSet.setInterpolator(new DecelerateInterpolator());
        // finalSet.setInterpolator(new BounceInterpolator());
        finalSet.start();

        mObjectAnimator = ObjectAnimator.ofFloat(mLockPrompt, "alpha", 1, 0.2f, 1);
        mObjectAnimator.setDuration(2000);
        mObjectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mObjectAnimator.setRepeatCount(-1);
        mObjectAnimator.start();

        int lenght = (int) mContext.getResources().getDimension(R.dimen.locker_arrow_move_lenght);
        ObjectAnimator objectAnimatorAlpha = ObjectAnimator
                .ofFloat(mLockArrow, "alpha", 0, 0.5f, 0);
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
    }

}
