
package cn.zmdx.kaka.fast.locker;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zmdx.kaka.fast.locker.battery.BatteryView;
import cn.zmdx.kaka.fast.locker.battery.BatteryView.ILevelCallBack;
import cn.zmdx.kaka.fast.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.fast.locker.notification.NotificationInterceptor;
import cn.zmdx.kaka.fast.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.fast.locker.security.KeyguardLockerManager;
import cn.zmdx.kaka.fast.locker.security.KeyguardLockerManager.IUnlockListener;
import cn.zmdx.kaka.fast.locker.service.PandoraService;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.fast.locker.shortcut.ShortcutManager;
import cn.zmdx.kaka.fast.locker.theme.ThemeManager;
import cn.zmdx.kaka.fast.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.fast.locker.utils.HDBLOG;
import cn.zmdx.kaka.fast.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.fast.locker.utils.ImageUtils;
import cn.zmdx.kaka.fast.locker.wallpaper.OnlineWallpaperView;
import cn.zmdx.kaka.fast.locker.wallpaper.OnlineWallpaperView.IOnlineWallpaper;
import cn.zmdx.kaka.fast.locker.weather.PandoraWeatherManager;
import cn.zmdx.kaka.fast.locker.weather.PandoraWeatherManager.IWeatherCallback;
import cn.zmdx.kaka.fast.locker.weather.PandoraWeatherManager.PandoraWeather;
import cn.zmdx.kaka.fast.locker.widget.DigitalClocks;
import cn.zmdx.kaka.fast.locker.widget.PandoraPanelLayout;
import cn.zmdx.kaka.fast.locker.widget.SlidingPaneLayout;
import cn.zmdx.kaka.fast.locker.widget.WallpaperPanelLayout;
import cn.zmdx.kaka.fast.locker.widget.PandoraPanelLayout.PanelSlideListener;
import cn.zmdx.kaka.fast.locker.widget.PandoraPanelLayout.SimplePanelSlideListener;
import cn.zmdx.kaka.fast.locker.widget.PandoraPanelLayout.SlideState;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

@SuppressWarnings("deprecation")
public class LockScreenManager {

    protected static final int MAX_TIMES_SHOW_GUIDE = 3;

    private PandoraPanelLayout mSliderView;

    private ViewGroup mEntireView;

    private SlidingPaneLayout mSlidingPanelLayout;

    private FrameLayout mSlidingBehindLayout;

    private ImageView mSlidingBehindBlurView;

    private ViewGroup mBoxView;

    private static LockScreenManager INSTANCE = null;

    private WindowManager mWinManager = null;

    private PandoraConfig mPandoraConfig;

    private boolean mIsLocked = false;

    private Theme mCurTheme;

    private TextView mDate, mTemperature, mWeatherSummary, mBatteryInfo;

    private DigitalClocks mDigitalClockView;

    private KeyguardLock mKeyguard;

    private AnimatorSet mAnimatorSet;

    private ObjectAnimator mObjectAnimator;

    private Context mContext;

    WindowManager.LayoutParams mWinParams;

    private ILockScreenListener mLockListener = null;

    private boolean isInit = false;

    private WallpaperPanelLayout mOnlinePanel;

    private OnlineWallpaperView mOnlineWallpaperView;

    private LinearLayout mOnlineViewContainer, mLockDataView;

    private View mTopOverlay, mBottomOverlay;

    private boolean mNeedPassword = false;

    // private ImageView mGuide;

    private BatteryView batteryView;

    private ImageView mCameraIcon;

    public interface ILockScreenListener {
        void onLock();

        void onUnLock();

        void onInitDefaultImage();
    }

    public void setOnLockScreenListener(ILockScreenListener listener) {
        mLockListener = listener;
    }

    private LockScreenManager() {
        mContext = HDApplication.getContext();
        mWinManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
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

    private boolean mIsNeedNotice = false;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void lock() {
        if (mIsLocked || PandoraService.isCalling())
            return;

        PandoraConfig pandoraConfig = PandoraConfig.newInstance(mContext);
        boolean isLockerOn = pandoraConfig.isPandolaLockerOn();
        if (!isLockerOn) {
            return;
        }

        mIsLocked = true;

        mWinParams = new WindowManager.LayoutParams();

        mIsNeedNotice = mPandoraConfig.isNeedNotice(mContext);
        if (mIsNeedNotice) {
            mWinParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            mWinParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
        mWinParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_DISMISS_KEYGUARD
                | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | LayoutParams.FLAG_HARDWARE_ACCELERATED | LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        if (!PandoraConfig.newInstance(mContext).isNeedNotice(mContext)) {
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
        mWinParams.format = PixelFormat.TRANSPARENT;
        // params.format=PixelFormat.RGBA_8888;
        mWinParams.windowAnimations = R.style.anim_locker_window;
        mWinParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        mWinParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        mWinParams.gravity = Gravity.TOP | Gravity.START;

        initLockScreenViews();

        mWinManager.addView(mEntireView, mWinParams);
        startFakeActivity();

        initShortcutApps();
        setDate();

        notifyLocked();

        NotificationInterceptor.getInstance(mContext).tryDispatchCustomNotification();
        NotificationInterceptor.getInstance(mContext).tryPullCustomNotificationData();

        checkNewVersion();

        String currentDate = BaseInfoHelper.getCurrentDate();
        UmengCustomEventManager.statisticalGuestureLockTime(pandoraConfig, currentDate);
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
        mWeatherSummary.setVisibility(View.VISIBLE);
        if (null != mOnlineWallpaperView) {
            mOnlineWallpaperView.setWeatherString(promptString);
        }
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
                    String promptString = PandoraUtils.getTimeQuantumString(mContext, Calendar
                            .getInstance().get(Calendar.HOUR_OF_DAY));
                    mWeatherSummary.setText(promptString);
                    mWeatherSummary.setVisibility(View.VISIBLE);
                    if (null != mOnlineWallpaperView) {
                        mOnlineWallpaperView.setWeatherString(promptString);
                    }
                } else {
                    int temp = pw.getTemp();
                    String summary = pw.getSummary();
                    if (mTemperature != null) {
                        if (mTemperature.getText() != null
                                && !mTemperature.getText().toString().endsWith("ºC")) {
                            mTemperature.append(" " + temp + "ºC");
                            if (null != mOnlineWallpaperView) {
                                mOnlineWallpaperView.setTemperature(" " + temp + "ºC");
                            }
                        }
                    }
                    if (mWeatherSummary == null) {
                        return;
                    }
                    mWeatherSummary.setVisibility(View.VISIBLE);
                    mWeatherSummary.setText(summary);
                    if (null != mOnlineWallpaperView) {
                        mOnlineWallpaperView.setWeatherString(summary);
                    }
                }
            }
        });

    }

    /**
     * 检查新版本
     */
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

    private void initShortcutApps() {
        View view = ShortcutManager.getInstance(mContext).createShortcutAppsView();

        ViewGroup.LayoutParams lp = mBoxView.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mBoxView.removeAllViews();
        mBoxView.addView(view, mBoxView.getChildCount(), lp);
    }

    // private void refreshContent() {
    // if (mBoxView != null && mBoxView.getChildCount() > 0) {
    // if (mFoldablePage != null && mFoldablePage instanceof FoldablePage) {
    // FoldablePage page = (FoldablePage) mFoldablePage;
    // if (page.isDataValidate()) {
    // if (!HDBNetworkState.isWifiNetwork()) {
    // page.removeItemsByCategory(ServerDataMapping.S_DATATYPE_HTML);
    // page.removeItemsByCategory(ServerDataMapping.S_DATATYPE_MULTIIMG);
    // }
    // return;
    // }
    // }
    // }
    //
    // mFoldablePage =
    // PandoraBoxManager.newInstance(mContext).getFoldablePage();
    //
    // View contentView = mFoldablePage.getRenderedView();
    // if (contentView == null) {
    // initDefaultPhoto(false);
    // return;
    // }
    // ViewParent parent = contentView.getParent();
    // if (parent != null) {
    // ((ViewGroup) parent).removeView(contentView);
    // }
    // ViewGroup.LayoutParams lp = mBoxView.getLayoutParams();
    // lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
    // lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
    // mBoxView.removeAllViews();
    // mBoxView.addView(contentView, mBoxView.getChildCount(), lp);
    // }

    @SuppressLint("InflateParams")
    private void initLockScreenViews() {
        mEntireView = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pandora_lockscreen, null);
        initGuideView();
        initSecurePanel();
        mSlidingPanelLayout = (SlidingPaneLayout) mEntireView.findViewById(R.id.sliding_layout);
        mSlidingPanelLayout.setPanelSlideListener(mSlideOutListener);
        mSlidingPanelLayout.setSliderFadeColor(Color.parseColor("#a0000000"));
        mSlidingPanelLayout.setOverhangVisiable(mNeedPassword);
        mSlidingPanelLayout.setShadowDrawableLeft(mContext.getResources().getDrawable(
                R.drawable.sliding_panel_layout_shadow));
        mSlidingBehindLayout = (FrameLayout) mEntireView.findViewById(R.id.sliding_behind_layout);
        mSlidingBehindBlurView = (ImageView) mEntireView.findViewById(R.id.sliding_behind_blur);
        mBatteryInfo = (TextView) mEntireView.findViewById(R.id.battery_info);
        mBoxView = (ViewGroup) mEntireView.findViewById(R.id.flipper_box);
        mDate = (TextView) mEntireView.findViewById(R.id.lock_date);
        mTemperature = (TextView) mEntireView.findViewById(R.id.lock_temperature);
        mLockDataView = (LinearLayout) mEntireView.findViewById(R.id.lock_date_view);
        mWeatherSummary = (TextView) mEntireView.findViewById(R.id.weather_summary);
        mDigitalClockView = (DigitalClocks) mEntireView.findViewById(R.id.digitalClock);

        batteryView = (BatteryView) mEntireView.findViewById(R.id.batteryView);
        batteryView.setLevelListener(new ILevelCallBack() {

            @Override
            public void onLevelChanged(int level) {
                mBatteryInfo.setText(level + "%");
            }
        });
        mSliderView = (PandoraPanelLayout) mEntireView.findViewById(R.id.locker_view);
        mSliderView.setPanelSlideListener(mSlideListener);
        if (!ViewConfiguration.get(mContext).hasPermanentMenuKey()) {// 存在虚拟按键
            mSliderView.setPanelHeight(BaseInfoHelper.dip2px(mContext, 110));
        } else {
            mSliderView.setPanelHeight(BaseInfoHelper.dip2px(mContext, 80));
        }
        mTopOverlay = mEntireView.findViewById(R.id.lock_top_overlay);
        mBottomOverlay = mEntireView.findViewById(R.id.lock_bottom_overlay);
        setDrawable();
        initCamera();
        initOnlinePaperPanel();
    }

    private void initCamera() {
        final View outerView = mEntireView.findViewById(R.id.camera_outline);
        mCameraIcon = (ImageView) mEntireView.findViewById(R.id.camera);
        outerView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mCameraIcon.setImageResource(R.drawable.camera_press_icon);
                        setRunnableAfterUnLock(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    Intent intent = new Intent(
                                            MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA); // 启动照相机
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(intent);
                                    UmengCustomEventManager.statisticalEnterCamera();
                                } catch (Exception e) {
                                }
                            }
                        });
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    case MotionEvent.ACTION_UP:
                        setRunnableAfterUnLock(null);
                        mCameraIcon.setImageResource(R.drawable.camera_icon);
                        break;
                }
                return false;
            }
        });
        final int distance = BaseInfoHelper.dip2px(mContext, 35);
        outerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(outerView, "translationX",
                        distance);
                animator1.setDuration(300);

                ObjectAnimator animator = ObjectAnimator.ofFloat(outerView, "translationX", 0);
                animator.setInterpolator(new BounceInterpolator());
                animator.setDuration(700);

                AnimatorSet set = new AnimatorSet();
                set.playSequentially(animator1, animator);
                set.start();
            }
        });
    }

    private void initGuideView() {
        int lockScreenTime = PandoraConfig.newInstance(mContext).getLockScreenTimes();
        if (lockScreenTime == 1) {
            return;
        }
        final ImageView guideView = new ImageView(mContext);
        guideView.setScaleType(ScaleType.FIT_XY);
        guideView.setVisibility(View.VISIBLE);
        ViewCompat.setAlpha(guideView, 0);
        mEntireView.addView(guideView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        guideView.animate().alpha(1).setDuration(1000).setStartDelay(700).start();
        guideView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mEntireView.removeView(guideView);
                PandoraConfig.newInstance(mContext).saveLockScreenTimes(1);
            }
        });
        guideView.setImageResource(R.drawable.pandora_lock_screen_guide);
    }

    /**
     * 向右侧滑的监听
     */
    private SlidingPaneLayout.PanelSlideListener mSlideOutListener = new SlidingPaneLayout.PanelSlideListener() {

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            if (!mNeedPassword) {
                mSlidingBehindLayout.setAlpha(1.0f - slideOffset);
            } else {
                View passView = mSlidingBehindLayout.findViewWithTag("passwordView");
                if (passView != null) {
                    passView.setTranslationX((1.0f - slideOffset) * passView.getMeasuredWidth());
                }
            }
            dispatchMainPanelSlide(panel, slideOffset);
        }

        @Override
        public void onPanelOpened(View panel) {
            dispatchMainPanelOpened();
            unLock(true, false);
        }

        @Override
        public void onPanelClosed(View panel) {
            dispatchMainPanelClosed();
            // 取消侧滑展开操作，将解锁后的操作恢复
            setRunnableAfterUnLock(null);
        }
    };

    public interface IMainPanelListener {
        void onMainPanelOpened();

        void onMainPanelClosed();

        void onMainPanelSlide(View panel, float slideOffset);
    }

    private Set<IMainPanelListener> mMainPanelCallback = new HashSet<IMainPanelListener>();

    public void registMainPanelListener(IMainPanelListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mMainPanelCallback) {
            mMainPanelCallback.add(listener);
        }
    }

    public void unRegistMainPanelListener(IMainPanelListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mMainPanelCallback) {
            mMainPanelCallback.remove(listener);
        }
    }

    private void dispatchMainPanelSlide(View panel, float slideOffset) {
        synchronized (mMainPanelCallback) {
            for (IMainPanelListener listener : mMainPanelCallback) {
                listener.onMainPanelSlide(panel, slideOffset);
            }
        }
    }

    private void dispatchMainPanelOpened() {
        synchronized (mMainPanelCallback) {
            for (IMainPanelListener listener : mMainPanelCallback) {
                listener.onMainPanelOpened();
            }
        }
    }

    private void dispatchMainPanelClosed() {
        if (null != mCameraIcon) {
            mCameraIcon.setImageResource(R.drawable.camera_icon);
        }
        synchronized (mMainPanelCallback) {
            for (IMainPanelListener listener : mMainPanelCallback) {
                listener.onMainPanelClosed();
            }
        }
    }

    /**
     * 设置拉开后内容的背景图片，如果onlyDisplayCustomImage为true，则只有当设置了个性化背景时才会显示，否则不显示任何东西（
     * 包括引导设置页）；如果onlyDisplayCustomImage为false，则可能会显示引导设置页
     * 
     * @param onlyDisplayCustomImage
     */
    // private void initDefaultPhoto(boolean onlyDisplayCustomImage) {
    // final DefaultBox box = (DefaultBox)
    // PandoraBoxManager.newInstance(mContext).getDefaultBox();
    // if (onlyDisplayCustomImage) {
    // if (box.isSetCustomImage()) {
    // View defaultView = box.getRenderedView();
    // mBoxView.addView(defaultView);
    // }
    // } else {
    // mBoxView.addView(box.getRenderedView());
    // }
    // }

    private void initSecurePanel() {
        final KeyguardLockerManager klm = new KeyguardLockerManager(mContext);
        final View view = klm.getCurrentLockerView(new IUnlockListener() {
            @Override
            public void onSuccess() {
                // delay 3s 是为了解决从windowmanager中将view立即移除时出现的残影bug
                HDBThreadUtils.postOnUiDelayed(new Runnable() {

                    @Override
                    public void run() {
                        internalUnLock();
                    }
                }, 3);
            }

            @Override
            public void onFaild(View view) {
            }

        });
        if (view != null) {
            mSlidingBehindLayout = (FrameLayout) mEntireView
                    .findViewById(R.id.sliding_behind_layout);

            view.setTag("passwordView");
            mSlidingBehindLayout.addView(view);
            mNeedPassword = true;
        } else {
            mNeedPassword = false;
        }
    }

    private void initOnlinePaperPanel() {
        mOnlineViewContainer = (LinearLayout) mEntireView
                .findViewById(R.id.pandora_online_wallpaper);
        mOnlinePanel = (WallpaperPanelLayout) mEntireView
                .findViewById(R.id.locker_wallpaper_sliding);
        mOnlinePanel
                .setPanelSlideListener(new cn.zmdx.kaka.fast.locker.widget.WallpaperPanelLayout.PanelSlideListener() {

                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {
                        if (!isInit) {
                            isInit = true;
                            initOnlinePaperPanelView();
                        }
                    }

                    @Override
                    public void onPanelHidden(View panel) {
                    }

                    @Override
                    public void onPanelExpanded(View panel) {
                        mSliderView.setEnabled(false);
                        if (null != mOnlineWallpaperView) {
                            mOnlineWallpaperView.initContentView();
                            mOnlineWallpaperView.setOnWallpaperListener(new IOnlineWallpaper() {

                                @Override
                                public void applyOnlinePaper(String filePath) {
                                    if (null != mSliderView && !TextUtils.isEmpty(filePath)) {
                                        Drawable drawable = mSliderView.setForgroundFile(filePath);
                                        if (mNeedPassword) {
                                            if (null != drawable) {
                                                doFastBlur(drawable);
                                            }
                                        }
                                    }
                                    mOnlinePanel.collapsePanel();
                                }
                            });
                            mOnlineWallpaperView.setWeatherString(mWeatherSummary.getText()
                                    .toString());
                            mOnlineWallpaperView.setDate(mDate.getText().toString());
                        }
                    }

                    @Override
                    public void onPanelCollapsed(View panel) {
                        isInit = false;
                        mSliderView.setEnabled(true);
                    }

                    @Override
                    public void onPanelAnchored(View panel) {
                    }
                });
    }

    protected void initOnlinePaperPanelView() {
        if (null == mOnlineWallpaperView) {
            mOnlineWallpaperView = new OnlineWallpaperView(mContext);
        }
        mOnlineViewContainer.removeAllViews();
        mOnlineViewContainer.addView(mOnlineWallpaperView);
    }

    public void setDate() {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String weekString = PandoraUtils.getWeekString(mContext, week);
        String dateString = "" + month + "月" + "" + day + "日 " + weekString;
        mDate.setText(dateString);
        if (null != mOnlineWallpaperView) {
            mOnlineWallpaperView.setDate(dateString);
        }
    }

    private void setDrawable() {
        mCurTheme = ThemeManager.getCurrentTheme();
        Drawable bgDrawable = mCurTheme.getCurDrawable();
        mSliderView.setForegroundDrawable(bgDrawable);
        if (mNeedPassword) {
            if (null != bgDrawable) {
                doFastBlur(bgDrawable);
            }
        }
    }

    private void doFastBlur(Drawable bgDrawable) {
        Bitmap bitmap = PandoraUtils.doFastBlur(mContext, mSlidingPanelLayout.getOverhangSize(),
                ImageUtils.drawable2Bitmap(bgDrawable), mSlidingPanelLayout);
        mSlidingBehindBlurView.setImageBitmap(bitmap);
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
        unLock(true, false);
    }

    /**
     * 解锁
     * 
     * @param isCloseFakeActivity 解锁同时，是否关闭背后的假activity,默认为true
     * @param forceClose 如果为true，则忽略密码锁，直接解锁，比如来电话时
     */
    public void unLock(boolean isCloseFakeActivity, boolean forceClose) {
        if (forceClose) {
            internalUnLock();
            return;
        }
        if (!mNeedPassword) {
            internalUnLock(isCloseFakeActivity);
        } else {
            if (!mSlidingPanelLayout.isOpen()) {
                mSlidingPanelLayout.openPane();
            }
        }
    }

    private void internalUnLock() {
        internalUnLock(true);
    }

    private void internalUnLock(boolean isCloseFakeActivity) {
        if (!mIsLocked) {
            if (isCloseFakeActivity)
                notifyUnLocked();
            return;
        }
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
        mIsLocked = false;
        isInit = false;

        mOnlineWallpaperView = null;
        mOnlineViewContainer.removeAllViews();

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

    public boolean isLocked() {
        return mIsLocked;
    }

    private Runnable mUnLockRunnable = null;

    private PanelSlideListener mSlideListener = new SimplePanelSlideListener() {

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            setLockScreenDim(slideOffset);
        }

        private void setLockScreenDim(float slideOffset) {
            float localSlideOffset = 0.6f * (1.0f - slideOffset);
            if (mTopOverlay != null && mBottomOverlay != null) {
                mTopOverlay.setAlpha(localSlideOffset);
                mBottomOverlay.setAlpha(localSlideOffset);
            }
        }

        @Override
        public void onPanelCollapsed(View panel) {
            UmengCustomEventManager.statisticalPullDownTimes();
        }

        @Override
        public void onPanelExpanded(View panel) {
        }

        @Override
        public void onPanelHidden(View panel) {

        }

        @Override
        public void onPanelFixed(View panel) {
        }

        @Override
        public void onPanelClickedDuringFixed() {
        }

        public void onPanelStartDown(View view) {
            dispatchStartPullDownEvent();
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

    public interface IPullDownListener {
        void onStartPullDown();
    }

    private Set<IPullDownListener> mPullDownListener = new HashSet<IPullDownListener>();

    private void dispatchStartPullDownEvent() {
        synchronized (mPullDownListener) {
            for (IPullDownListener listener : mPullDownListener) {
                listener.onStartPullDown();
            }
        }
    }

    public void registPullDownListener(IPullDownListener listener) {
        synchronized (mPullDownListener) {
            mPullDownListener.add(listener);
        }
    }

    public void unRegistPullDownListener(IPullDownListener listener) {
        synchronized (mPullDownListener) {
            mPullDownListener.remove(listener);
        }
    }

    public void onScreenOff() {
        invisiableViews(mLockDataView, mWeatherSummary, mDigitalClockView);
        cancelAnimatorIfNeeded();
        if (mSliderView != null && !mSliderView.isPanelExpanded()) {
            mSliderView.expandPanel();
        }
        if (mDigitalClockView != null) {
            mDigitalClockView.setTickerStoped(true);
        }
    }

    public void onScreenOn() {
        if (mIsLocked) {
            processAnimations();
            processWeatherInfo();
            if (mDigitalClockView != null) {
                mDigitalClockView.setTickerStoped(false);
            }
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

    public SlideState getLockPanelState() {
        return mSliderView != null ? mSliderView.getSlideState() : null;
    }

    private static final int DATE_WIDGET_TRANSLATIONY_DISTANCE = -BaseInfoHelper.dip2px(
            HDApplication.getContext(), 100);

    private void processAnimations() {
        ObjectAnimator digitalAlpha = ObjectAnimator.ofFloat(mDigitalClockView, "alpha", 0, 1);
        ObjectAnimator digitalTrans = ObjectAnimator.ofFloat(mDigitalClockView, "translationY",
                DATE_WIDGET_TRANSLATIONY_DISTANCE, 0);
        AnimatorSet digitalSet = new AnimatorSet();
        digitalSet.playTogether(digitalAlpha, digitalTrans);

        ObjectAnimator dateAlpha = ObjectAnimator.ofFloat(mLockDataView, "alpha", 0, 1);
        ObjectAnimator dateTrans = ObjectAnimator.ofFloat(mLockDataView, "translationY",
                DATE_WIDGET_TRANSLATIONY_DISTANCE, 0);
        AnimatorSet dateSet = new AnimatorSet();
        dateSet.setStartDelay(100);
        dateSet.playTogether(dateAlpha, dateTrans);

        ObjectAnimator wsAlpha = ObjectAnimator.ofFloat(mWeatherSummary, "alpha", 0, 1);
        ObjectAnimator wsTrans = ObjectAnimator.ofFloat(mWeatherSummary, "translationY",
                DATE_WIDGET_TRANSLATIONY_DISTANCE, 0);
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
    }

    private Set<OnBackPressedListener> mBackPressedListeners = new HashSet<OnBackPressedListener>();

    public void onBackPressed() {
        for (OnBackPressedListener listener : mBackPressedListeners) {
            listener.onBackPressed();
        }
    }

    public boolean registBackPressedListener(OnBackPressedListener listener) {
        if (listener != null)
            return mBackPressedListeners.add(listener);
        return false;
    }

    public boolean unRegistBackPressedListener(OnBackPressedListener listener) {
        if (listener != null) {
            return mBackPressedListeners.remove(listener);
        }
        return false;
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }
}
