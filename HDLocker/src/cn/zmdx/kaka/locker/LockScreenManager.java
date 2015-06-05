
package cn.zmdx.kaka.locker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.battery.BatteryView;
import cn.zmdx.kaka.locker.battery.BatteryView.ILevelCallBack;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.content.PicassoHelper;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.layout.TimeLayoutManager;
import cn.zmdx.kaka.locker.notification.NotificationInfo;
import cn.zmdx.kaka.locker.notification.NotificationInterceptor;
import cn.zmdx.kaka.locker.notification.NotificationPreferences;
import cn.zmdx.kaka.locker.notification.PandoraNotificationFactory;
import cn.zmdx.kaka.locker.notification.PandoraNotificationService;
import cn.zmdx.kaka.locker.notification.view.NotificationListView;
import cn.zmdx.kaka.locker.security.KeyguardLockerManager;
import cn.zmdx.kaka.locker.security.KeyguardLockerManager.IUnlockListener;
import cn.zmdx.kaka.locker.service.PandoraService;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.sound.LockSoundManager;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils;
import cn.zmdx.kaka.locker.widget.SensorImageView;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout.SimplePanelSlideListener;
import cn.zmdx.kaka.locker.widget.ViewPagerCompat;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

@SuppressWarnings("deprecation")
public class LockScreenManager {

    protected static final int MAX_TIMES_SHOW_GUIDE = 3;

    protected static final String TAG = "LockScreenManager";

    private ViewGroup mEntireView;

    private static LockScreenManager INSTANCE = null;

    private WindowManager mWinManager = null;

    private PandoraConfig mPandoraConfig;

    private boolean mIsLocked = false;

    private Theme mCurTheme;

    private TextView mBatteryInfo;

    private KeyguardLock mKeyguard;

    private Context mContext;

    WindowManager.LayoutParams mWinParams;

    private ILockScreenListener mLockListener = null;

    private boolean mNeedPassword = false;

    private BatteryView batteryView;

    private ViewPagerCompat mPager;

    private SensorImageView mSensorImageView, mBlurImageView;

    private View mMainPage, mDimBg, mMainPagePart1, mFakeStatusDate;

    private SlidingUpPanelLayout mSlidingUpView;

    private boolean mKeepBlurEffect = false;

    private ListView mNotificationListView;

    private View mCommonWidgetLayout;

    private ViewGroup mDateWidget;

    // private ImageView mWifiIcon;

    private ShimmerTextView mShimmerTextView;

    private Shimmer mShimmer;

    private TimeLayoutManager mTimeLayoutManager;

    public interface ILockScreenListener {
        void onLock();

        void onUnLock();
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

        mIsNeedNotice = mPandoraConfig.isNotifyFunctionOn();
        mWinParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mWinParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_DISMISS_KEYGUARD
                | LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | LayoutParams.FLAG_HARDWARE_ACCELERATED | LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        if (!mIsNeedNotice || BaseInfoHelper.isSupportTranslucentStatus()) {
            mWinParams.flags |= LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            final Display display = mWinManager.getDefaultDisplay();
            mWinParams.height = BaseInfoHelper.getRealHeight(display);
        } else {
            mWinParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        }

        mWinParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        mWinParams.x = 0;
        mWinParams.y = 0;
        // mWinParams.format = PixelFormat.TRANSLUCENT;
        mWinParams.format = PixelFormat.RGBA_8888;
        mWinParams.windowAnimations = R.style.anim_locker_window;
        // mWinParams.windowAnimations = R.style.anim_slide_locker_window;
        mWinParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST;
        mWinParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        mWinParams.gravity = Gravity.TOP | Gravity.START;

        initNewLockScreenViews();
        mWinManager.addView(mEntireView, mWinParams);

        FakeActivity.startup(mContext);

        notifyLocked();

        NotificationInterceptor.getInstance(mContext).tryDispatchCustomNotification();
        NotificationInterceptor.getInstance(mContext).tryPullCustomNotificationData();

        checkNewVersion();

        if (PandoraConfig.newInstance(mContext).isLockSoundOn()) {
            LockSoundManager.play(LockSoundManager.SOUND_ID_LOCK);
        }

        String currentDate = BaseInfoHelper.getCurrentDate();
        UmengCustomEventManager.statisticalGuestureLockTime(pandoraConfig, currentDate);

        WallpaperUtils.autoChangeWallpaper();
    }

    public void startShimmer() {
        if (mShimmer != null) {
            if (!mShimmer.isAnimating()) {
                mShimmer.start(mShimmerTextView);
            }
        }
    }

    public boolean isNewsPanelExpanded() {
        return (mSlidingUpView != null) && mSlidingUpView.isPanelExpanded();
    }

    private void initNewLockScreenViews() {
        mEntireView = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.new_pandora_lockscreen, null);

        mSlidingUpView = (SlidingUpPanelLayout) mEntireView.findViewById(R.id.slidingUpPanelLayout);
        mSlidingUpView.setPanelSlideListener(mSlidingUpPanelListener);
        mSlidingUpView.setDragViewClickable(false);

        mBlurImageView = (SensorImageView) mEntireView.findViewById(R.id.blurImageView);
        mBlurImageView.setAlpha(0.0f);
        mSensorImageView = (SensorImageView) mEntireView.findViewById(R.id.lockerBg);
        if (!PandoraConfig.newInstance(mContext).isGravitySenorOn()) {
            mBlurImageView.setTransitionMode(SensorImageView.TRANSITION_MODE_STATIC);
            mSensorImageView.setTransitionMode(SensorImageView.TRANSITION_MODE_STATIC);
        } else {
            mBlurImageView.setTransitionMode(SensorImageView.TRANSITION_MODE_SENSOR);
            mSensorImageView.setTransitionMode(SensorImageView.TRANSITION_MODE_SENSOR);
        }

        mDimBg = mEntireView.findViewById(R.id.dimBg);
        mDimBg.setAlpha(0);

        initWallpaper();

        mPager = (ViewPagerCompat) mEntireView.findViewById(R.id.viewPager);

        List<View> pages = new ArrayList<View>();
        ViewGroup page1 = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pandora_password_pager_layout, null);
        initSecurePanel(page1);
        mMainPage = LayoutInflater.from(mContext).inflate(R.layout.pandora_main_pager_layout, null);
        mMainPage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mMainPagePart1 = mMainPage.findViewById(R.id.part1);
        mFakeStatusDate = mMainPage.findViewById(R.id.fakeStatusDate);
        mShimmerTextView = (ShimmerTextView) mMainPage.findViewById(R.id.unlockShimmerTextView);
        mShimmer = new Shimmer();
        mShimmer.setDuration(3000);
        mShimmer.setStartDelay(1000);

        mCommonWidgetLayout = mMainPage.findViewById(R.id.commonWidgetArea);
        // mWifiIcon = (ImageView) mMainPage.findViewById(R.id.wifi_icon);
        mBatteryInfo = (TextView) mMainPage.findViewById(R.id.battery_info);
        batteryView = (BatteryView) mMainPage.findViewById(R.id.batteryView);
        if (PandoraConfig.newInstance(mContext).isNotifyFunctionOn()) {
            // mBatteryInfo.setVisibility(View.GONE);
            // batteryView.setVisibility(View.GONE);
            mCommonWidgetLayout.setVisibility(View.INVISIBLE);
        } else {
            batteryView.setLevelListener(new ILevelCallBack() {

                @Override
                public void onLevelChanged(int level) {
                    mBatteryInfo.setText(level + "%");
                }
            });
        }

        mDateWidget = (ViewGroup) mMainPage.findViewById(R.id.dateWeatherLayout);
        mTimeLayoutManager = TimeLayoutManager.getInstance(mContext);
        View dateWeatherView = mTimeLayoutManager.createLayoutViewByID(TimeLayoutManager
                .getInstance(mContext).getCurrentLayout());
        mDateWidget.addView(dateWeatherView);

        pages.add(page1);
        pages.add(mMainPage);
        LockerPagerAdapter pagerAdapter = new LockerPagerAdapter(mContext, mPager, pages);
        mPager.setAdapter(pagerAdapter);
        mPager.setCurrentItem(1);
        mPager.setOnPageChangeListener(mViewPagerChangeListener);

        mNotificationListView = ((NotificationListView) mMainPage
                .findViewById(R.id.lock_bottom_notification_layout)).getListView();
        if (mNotificationListView.getChildCount() > 0) {
            mKeepBlurEffect = true;
        } else {
            mKeepBlurEffect = false;
        }
        mNotificationListView.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {

            @Override
            public void onChildViewAdded(View parent, View child) {
                ViewGroup vg = (ViewGroup) parent;
                if (vg.getChildCount() == 1) {
                    fadeInWallpaperBlurAnimator();
                }
                mKeepBlurEffect = true;
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                ViewGroup vg = (ViewGroup) parent;
                if (vg.getChildCount() == 0) {
                    fadeOutWallpaperBlurAnimator();
                    mKeepBlurEffect = false;
                }
            }
        });

        if (PandoraConfig.newInstance(mContext).isNewsOpen()) {
            View newsView = PandoraBoxManager.newInstance(mContext).getEntireView();
            ViewGroup newsLayout = (ViewGroup) mSlidingUpView.findViewById(R.id.newsLayout);
            newsLayout.setVisibility(View.VISIBLE);
            if (newsLayout.getChildCount() == 0) {
                ViewGroup vg = (ViewGroup) newsView.getParent();
                if (vg != null) {
                    vg.removeView(newsView);
                }
                newsLayout.addView(newsView);
                mSlidingUpView.setDragView(newsLayout.findViewById(R.id.header_part1));
            }
            PandoraBoxManager.newInstance(mContext).initHeader();
            PandoraBoxManager.newInstance(mContext).getHeaderView().setAlpha(1);
            HDBThreadUtils.postOnUiDelayed(new Runnable() {
                @Override
                public void run() {
                    PandoraBoxManager.newInstance(mContext).initBody();
                    PandoraBoxManager.newInstance(mContext).refreshCurrentNews();
                }
            }, 3000);
        } else {
            mSlidingUpView.findViewById(R.id.newsLayout).setVisibility(View.GONE);
        }
    }

    public void pauseWallpaperTranslation() {
        mSensorImageView.pauseSensor();
        mBlurImageView.pauseSensor();
    }

    public void resumeWallpaperTranslation() {
        mSensorImageView.resumeSensor();
        mBlurImageView.resumeSensor();
    }

    private ViewPager.SimpleOnPageChangeListener mViewPagerChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                // dismiss news panel
            } else if (position == 1) {
                if (mNeedPassword) {
                    setRunnableAfterUnLock(null);

                    if (mNotificationListView != null) {
                        BaseAdapter adapter = (BaseAdapter) mNotificationListView.getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset == 0 && position == 0) {
                HDBThreadUtils.runOnUi(new Runnable() {

                    @Override
                    public void run() {
                        unLock();
                    }
                });
            }
            if (position == 1 && positionOffset == 0.0) {
                if (!mKeepBlurEffect) {
                    setWallpaperBlurEffect(0);
                }
            } else {
                if (!mKeepBlurEffect) {
                    setWallpaperBlurEffect(1.0f - positionOffset);
                }

                float tmp = 2f * positionOffset - 1f;
                setMainPageAlpha(tmp);
                try {
                    PandoraBoxManager.newInstance(mContext).getHeaderView().setAlpha(tmp);
                } catch (Exception e) {
                }

                if (tmp <= 0 && mPager.getCurrentTouchAction() == MotionEvent.ACTION_UP
                        && !mNeedPassword) {
                    HDBThreadUtils.runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            unLock();
                        }
                    });
                }
            }
        }
    };

    public void expandNewsPanel() {
        mSlidingUpView.expandPanel();
    }

    public void collapseNewsPanel() {
        mSlidingUpView.collapsePanel();
    }

    private SimplePanelSlideListener mSlidingUpPanelListener = new SimplePanelSlideListener() {
        public void onPanelSlide(View panel, float slideOffset) {
            if (slideOffset >= 0) {
                if (!mKeepBlurEffect) {
                    setWallpaperBlurEffect(Math.max(0, slideOffset));
                }
                setDateViewAlpha(1.0f - slideOffset);

                PandoraBoxManager.newInstance(mContext).notifyNewsPanelSlide(panel, slideOffset);
            }
        };

        public void onPanelExpanded(View panel) {
            PandoraBoxManager.newInstance(mContext).notifyNewsPanelExpanded();
            pauseWallpaperTranslation();
            pauseShimmer();
            mFakeStatusDate.setVisibility(View.VISIBLE);
            mSlidingUpView.setDragView(panel.findViewById(R.id.dragview2));
        };

        public void onPanelCollapsed(View panel) {
            PandoraBoxManager.newInstance(mContext).notifyNewsPanelCollapsed();
            resumeWallpaperTranslation();
            startShimmer();
            mFakeStatusDate.setVisibility(View.INVISIBLE);
            mSlidingUpView.setDragView(panel.findViewById(R.id.header_part1));
        };
    };

    private void fadeInWallpaperBlurAnimator() {
        if (mBlurImageView != null) {
            mBlurImageView.animate().alpha(1).setDuration(500).start();
        }

        if (mDimBg != null) {
            mDimBg.animate().alpha(1).setDuration(500).start();
        }
    }

    private void fadeOutWallpaperBlurAnimator() {
        if (mBlurImageView != null) {
            mBlurImageView.animate().alpha(0).setDuration(500).start();
        }
        if (mDimBg != null) {
            mDimBg.animate().alpha(0).setDuration(500).start();
        }
    }

    private void setMainPageAlpha(float alpha) {
        if (mMainPage != null) {
            mMainPage.setAlpha(alpha);
        }
    }

    private void setDateViewAlpha(float alpha) {
        if (mDateWidget != null) {
            mDateWidget.setAlpha(alpha);
        }
    }

    private Interpolator mBlurInterpolator = new DecelerateInterpolator();

    private void setWallpaperBlurEffect(float level) {
        if (mBlurImageView != null) {
            float result = mBlurInterpolator.getInterpolation(level);
            mBlurImageView.setAlpha(result);
        }
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

    public void updateWeatherInfo() {
        if (mTimeLayoutManager != null) {
            mTimeLayoutManager.updateWeather();
        }
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

    private void initSecurePanel(ViewGroup container) {
        final KeyguardLockerManager klm = new KeyguardLockerManager(mContext);
        final View view = klm.getCurrentLockerView(new IUnlockListener() {
            @Override
            public void onSuccess() {
                internalUnLock();
            }

            @Override
            public void onFaild(View view) {
            }

        });
        if (view != null) {
            view.setTag("passwordView");
            container.addView(view);
            mNeedPassword = true;
        } else {
            mNeedPassword = false;
        }
    }

    public void initWallpaper() {
        if (null == mEntireView || null == mBlurImageView) {
            return;
        }
        mCurTheme = ThemeManager.getCurrentTheme();
        final Drawable curWallpaper = mCurTheme.getCurDrawable();
        LockerUtils.renderScreenLockerWallpaper(
                ((ImageView) mEntireView.findViewById(R.id.lockerBg)), curWallpaper, false);

        LockerUtils.renderScreenLockerBlurEffect(mBlurImageView,
                ImageUtils.drawable2Bitmap(curWallpaper, true));
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
            if (PandoraBoxManager.newInstance(mContext).isDetailPageOpened()) {
                PandoraBoxManager.newInstance(mContext).closeDetailPage(false);
            }
            internalUnLock();
            return;
        }
        if (!mNeedPassword) {
            internalUnLock(isCloseFakeActivity);
        } else {
            if (mPager != null && mPager.getCurrentItem() == 1) {
                mPager.setCurrentItem(0, true);
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

        pauseShimmer();
        mWinManager.removeView(mEntireView);
        mEntireView = null;
        mIsLocked = false;

        if (PandoraConfig.newInstance(mContext).isLockSoundOn()) {
            LockSoundManager.play(LockSoundManager.SOUND_ID_UNLOCK);
        }

        if (mUnLockRunnable != null) {
            HDBThreadUtils.runOnUi(mUnLockRunnable);
            mUnLockRunnable = null;
        }

        UmengCustomEventManager.statisticalGuestureUnLockSuccess();

        freeMemory();
    }

    private void freeMemory() {
        HDBThreadUtils.postOnWorkerDelayed(new Runnable() {
            @Override
            public void run() {
                // 释放新闻面板所占的内存
                PandoraBoxManager.freeMemory();
                // INSTANCE = null;
                // picasso的图片内存缓存
                PicassoHelper.clearMemoryCache();
                PicassoHelper.shutdown();
            }
        }, 200);
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    private Runnable mUnLockRunnable = null;

    public void onScreenOff() {
        if (mSlidingUpView != null) {
            if (mSlidingUpView.isPanelExpanded()) {
                mSlidingUpView.collapsePanel();
            }
            if (mSlidingUpView.getSliderView() != null && mPager.getCurrentItem() == 1) {
                mSlidingUpView.getSliderView().setTranslationY(mSlidingUpView.getHeight());
            }
        }

        PandoraBoxManager.newInstance(mContext).onScreenOff();
        if (BuildConfig.DEBUG && false) {
            for (int i = 0; i < 20; i++) {
                NotificationInterceptor.getInstance(mContext).sendCustomNotification(
                        PandoraNotificationFactory.createTestNotification());
            }
        }
    }

    public void pauseShimmer() {
        if (mShimmer != null) {
            mShimmer.cancel();
        }
    }

    public View getSliderView() {
        return mSlidingUpView == null ? null : mSlidingUpView.getSliderView();
    }

    public void onScreenOn() {
        if (mIsLocked) {

            startShimmer();

            if (mSlidingUpView != null && mPager != null && mPager.getCurrentItem() == 1) {
                View sliderView = mSlidingUpView.getSliderView();
                if (sliderView != null) {
                    sliderView.animate().translationY(0).setDuration(800)
                            .setInterpolator(new DecelerateInterpolator()).start();
                }
            }

            sendObtainActiveNotificationMsg();
        }

        PandoraBoxManager.newInstance(mContext).onScreenOn();

        updateWeatherInfo();

        showNotificationPermissionTip();
    }

    private void showNotificationPermissionTip() {
        if (!NotificationInterceptor.isDeviceAvailable()) {
            return;
        }

        boolean granted = NotificationInterceptor.isGrantedNotifyPermission(mContext);
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("检查“读取通知权限”状态：" + granted);
        }
        if (!granted) {
            long lastTime = NotificationPreferences.getInstance(mContext)
                    .getLastTimeCheckNotificationPermission();
            long cur = System.currentTimeMillis();
            if (cur - lastTime > NotificationInterceptor.CHECK_PERMISSION_DURATION) {
                final NotificationInfo info = PandoraNotificationFactory
                        .createGuideOpenNotifyPermissionNotification();
                NotificationInterceptor.getInstance(mContext).sendCustomNotification(info);
                NotificationPreferences.getInstance(mContext)
                        .saveLastTimeCheckNotificationPermission(cur);
            }
        }
    }

    private void sendObtainActiveNotificationMsg() {
        if (NotificationInterceptor.isGrantedNotifyPermission(mContext)) {
            Intent intent = new Intent();
            intent.setAction(PandoraNotificationService.ACTION_OBTAIN_ACTIVE_NOTIFICATIONS);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
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

    // 按下home键调用
    public void onHomePressed() {
        if (isNewsPanelExpanded()) {
            collapseNewsPanel();
        }
    }
}
