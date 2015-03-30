
package cn.zmdx.kaka.locker;

import java.util.ArrayList;
import java.util.Calendar;
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
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.battery.BatteryView;
import cn.zmdx.kaka.locker.battery.BatteryView.ILevelCallBack;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.font.FontManager;
import cn.zmdx.kaka.locker.notification.NotificationInterceptor;
import cn.zmdx.kaka.locker.notification.view.NotificationListView;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.security.KeyguardLockerManager;
import cn.zmdx.kaka.locker.security.KeyguardLockerManager.IUnlockListener;
import cn.zmdx.kaka.locker.service.PandoraService;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager.ISmartWeatherCallback;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherInfo;
import cn.zmdx.kaka.locker.widget.SensorImageView;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout.SimplePanelSlideListener;
import cn.zmdx.kaka.locker.widget.TextClockCompat;
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

    private TextView mDate, mBatteryInfo;

    private KeyguardLock mKeyguard;

    private Context mContext;

    WindowManager.LayoutParams mWinParams;

    private ILockScreenListener mLockListener = null;

    private boolean mNeedPassword = false;

    private BatteryView batteryView;

    private ViewPagerCompat mPager;

    private SensorImageView mSensorImageView, mBlurImageView;

    private View mMainPage, mDimBg, mMainPagePart1;

    private SlidingUpPanelLayout mSlidingUpView;

    private boolean mKeepBlurEffect = false;

    private NotificationListView mNotificationListView;

    private ShimmerTextView mShimmerTextView;

    private Shimmer mShimmer;

    private TextClockCompat mClock;

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

        mIsNeedNotice = mPandoraConfig.isNotifyFunctionOn();
        if (mIsNeedNotice) {
            mWinParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            mWinParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
        mWinParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | LayoutParams.FLAG_HARDWARE_ACCELERATED | LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | LayoutParams.FLAG_FULLSCREEN;
        if (!mIsNeedNotice || Build.VERSION.SDK_INT >= 19) { // 如果不显示通知栏或者系统版本大于等于19(支持透明通知栏),则添加下面flag从屏幕顶部开始绘制
            mWinParams.flags |= LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }

        mWinParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        // final Display display = mWinManager.getDefaultDisplay();
        // mWinParams.height = BaseInfoHelper.getRealHeight(display);
        mWinParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        mWinParams.x = 0;
        mWinParams.y = 0;
        mWinParams.format = PixelFormat.TRANSPARENT;
        // params.format=PixelFormat.RGBA_8888;
        mWinParams.windowAnimations = R.style.anim_locker_window;
        // mWinParams.softInputMode = WindowManager.LayoutParams.SOFT_INPU
        mWinParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        mWinParams.gravity = Gravity.TOP | Gravity.START;

        initNewLockScreenViews();
        mWinManager.addView(mEntireView, mWinParams);
        startFakeActivity();

        notifyLocked();

        NotificationInterceptor.getInstance(mContext).tryDispatchCustomNotification();
        NotificationInterceptor.getInstance(mContext).tryPullCustomNotificationData();

        checkNewVersion();

        String currentDate = BaseInfoHelper.getCurrentDate();
        UmengCustomEventManager.statisticalGuestureLockTime(pandoraConfig, currentDate);
    }

    private void startShimmer() {
        if (mShimmer != null) {
            mShimmer.start(mShimmerTextView);
        }
    }

    private void initNewLockScreenViews() {
        mEntireView = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.new_pandora_lockscreen, null);

        // 初始化用于显示热点头条的上拉面板view
        mSlidingUpView = (SlidingUpPanelLayout) mEntireView.findViewById(R.id.slidingUpPanelLayout);
        mSlidingUpView.setPanelSlideListener(mSlidingUpPanelListener);
        mSlidingUpView.setDragViewClickable(false);

        // 初始化处理壁纸模糊的view
        mBlurImageView = (SensorImageView) mEntireView.findViewById(R.id.blurImageView);
        mBlurImageView.setAlpha(0.0f);// 默认模糊的view不显示，透明度设置为0
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

        // 初始化右划解锁的viewpager
        mPager = (ViewPagerCompat) mEntireView.findViewById(R.id.viewPager);

        List<View> pages = new ArrayList<View>();
        ViewGroup page1 = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pandora_password_pager_layout, null);
        initSecurePanel(page1);
        mMainPage = LayoutInflater.from(mContext).inflate(R.layout.pandora_main_pager_layout, null);
        mMainPagePart1 = mMainPage.findViewById(R.id.part1);
        mShimmerTextView = (ShimmerTextView) mMainPage.findViewById(R.id.unlockShimmerTextView);
        mShimmer = new Shimmer();
        mShimmer.setDuration(5000);// 默认是1s
        mShimmer.setStartDelay(1800);// 默认间隔为0

        startShimmer();

        mMainPage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mDate = (TextView) mMainPage.findViewById(R.id.lock_date);
        mClock = (TextClockCompat) mMainPage.findViewById(R.id.clock);
        mClock.setTypeface(FontManager.getTypeface("fonts/Roboto-Thin.ttf"));
        setDate();

        mBatteryInfo = (TextView) mMainPage.findViewById(R.id.battery_info);
        batteryView = (BatteryView) mMainPage.findViewById(R.id.batteryView);
        if (PandoraConfig.newInstance(mContext).isNotifyFunctionOn()) {
            mBatteryInfo.setVisibility(View.GONE);
            batteryView.setVisibility(View.GONE);
        } else {
            batteryView.setLevelListener(new ILevelCallBack() {

                @Override
                public void onLevelChanged(int level) {
                    mBatteryInfo.setText(level + "%");
                }
            });
        }
        pages.add(page1);
        pages.add(mMainPage);
        LockerPagerAdapter pagerAdapter = new LockerPagerAdapter(mContext, mPager, pages);
        mPager.setAdapter(pagerAdapter);
        mPager.setCurrentItem(1);
        mPager.setOnPageChangeListener(mViewPagerChangeListener);

        // 监听是否有通知，以处理背景模糊效果
        mNotificationListView = (NotificationListView) mMainPage
                .findViewById(R.id.lock_bottom_notification_layout);
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

        // 初始化新闻页
        View newsView = PandoraBoxManager.newInstance(mContext).getEntireView();
        ViewGroup newsLayout = (ViewGroup) mSlidingUpView.findViewById(R.id.newsLayout);
        if (newsLayout.getChildCount() == 0) {
            ViewGroup vg = (ViewGroup) newsView.getParent();
            if (vg != null) {
                vg.removeView(newsView);
            }
            newsLayout.addView(newsView);
            mSlidingUpView.setDragView(newsLayout.findViewById(R.id.header));
        }
        // 初始化新闻页header
        PandoraBoxManager.newInstance(mContext).initHeader();
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
                // mSlidingUpView.hidePanel();
            } else if (position == 1) {
                if (mNeedPassword) {
                    // 如果从密码页滑回锁屏页，将之前设置的解锁后执行动作清除。即此处认为用户没有输入密码解锁，又滑回了锁屏页
                    setRunnableAfterUnLock(null);

                    // 刷新通知的items恢复原位
                    if (mNotificationListView != null) {
                        BaseAdapter adapter = (BaseAdapter) mNotificationListView.getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset == 0.0 && position == 0) {
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
                setMainPageAlpha(positionOffset);
            }

            if (position == 0) {
                // 滑动同时，底部新闻面板同步下滑
                int translationY = (int) (mSlidingUpView.getPanelHeight() * (1.0f - positionOffset));
                mSlidingUpView.smoothSlideTo(translationY, 0);

                // 时间控件同步向上滑动
                // int offset = (int) -(mMainPagePart1.getHeight() * (1.0f -
                // positionOffset));
                // mMainPagePart1.setTranslationY(offset);
            }

        }
    };

    // 展开上拉的新闻面板
    public void expandNewsPanel() {
        mSlidingUpView.expandPanel();
    }

    // 收缩上拉的新闻面板
    public void collapseNewsPanel() {
        mSlidingUpView.collapsePanel();
    }

    private SimplePanelSlideListener mSlidingUpPanelListener = new SimplePanelSlideListener() {
        public void onPanelSlide(View panel, float slideOffset) {
            // 模糊背景
            if (slideOffset >= 0) {
                if (!mKeepBlurEffect) {
                    setWallpaperBlurEffect(Math.max(0, slideOffset));
                }
                // 渐隐时间，天气文字
                setMainPageAlpha(1.0f - slideOffset);
            }
        };

        public void onPanelExpanded(View panel) {
            PandoraBoxManager.newInstance(mContext).notifyNewsPanelExpanded();
            pauseWallpaperTranslation();
            pauseShimmer();
        };

        public void onPanelCollapsed(View panel) {
            PandoraBoxManager.newInstance(mContext).notifyNewsPanelCollapsed();
            resumeWallpaperTranslation();
            startShimmer();
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

    /**
     * 设置锁屏主页（包含时间，天气等信息）的整体透明度
     * 
     * @param alpha
     */
    private void setMainPageAlpha(float alpha) {
        if (mMainPage != null) {
            mMainPage.setAlpha(alpha);
        }
    }

    /**
     * 设置锁屏页壁纸的模糊效果。level值范围为[0, 1]，值越大，模糊程度越高。
     * 
     * @param level 模糊的程度
     */
    private void setWallpaperBlurEffect(float level) {
        if (mBlurImageView != null) {
            mBlurImageView.setAlpha(level);
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

    private void startFakeActivity() {
        Intent intent = new Intent(mContext, FakeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void processWeatherInfo() {
        long str2TimeMillis = mPandoraConfig.getLastCheckWeatherTime();

        SmartWeatherInfo smartWeatherInfo = PandoraWeatherManager.getInstance()
                .getWeatherFromCache();
        PandoraBoxManager.newInstance(mContext).updateView(smartWeatherInfo);
        if (System.currentTimeMillis() - str2TimeMillis >= PandoraPolicy.MIN_CHECK_WEATHER_DURAION) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("检查天气条件满足,请求网络数据");
            }
            PandoraWeatherManager.getInstance().getWeatherFromNetwork(new ISmartWeatherCallback() {

                @Override
                public void onSuccess(SmartWeatherInfo smartWeatherInfo) {
                    PandoraBoxManager.newInstance(mContext).updateView(smartWeatherInfo);
                }

                @Override
                public void onFailure() {

                }
            });
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
            view.setTag("passwordView");
            container.addView(view);
            mNeedPassword = true;
        } else {
            mNeedPassword = false;
        }
    }

    public void setDate() {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String weekString = PandoraUtils.getWeekString(mContext, week);
        String dateString = "" + month + "月" + "" + day + "日 " + weekString;
        mDate.setText(dateString);
    }

    public void initWallpaper() {
        mCurTheme = ThemeManager.getCurrentTheme();
        final Drawable curWallpaper = mCurTheme.getCurDrawable();
        Drawable lockBg = LockerUtils.renderScreenLockerWallpaper(
                ((ImageView) mEntireView.findViewById(R.id.lockerBg)), curWallpaper);

        LockerUtils.renderScreenLockerBlurEffect(mBlurImageView, lockBg);
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
            // 修复，锁屏时，来电话后，再次打开锁屏时，底部新闻栏会显示新闻详情的问题，这里要关闭详情页
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

        mWinManager.removeView(mEntireView);
        mEntireView = null;
        mIsLocked = false;

        if (mUnLockRunnable != null) {
            HDBThreadUtils.runOnUi(mUnLockRunnable);
            mUnLockRunnable = null;
        }
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
            // 如果当前页在锁屏主页（而不是密码锁页），在关闭屏幕时将底部新闻栏缩回到屏幕底部
            if (mSlidingUpView.getSliderView() != null && mPager.getCurrentItem() == 1) {
                mSlidingUpView.getSliderView().setTranslationY(mSlidingUpView.getHeight());
            }
        }

        pauseShimmer();

        // 检查是否有读取通知权限
        NotificationInterceptor.getInstance(mContext).checkPermission();

        PandoraBoxManager.newInstance(mContext).onScreenOff();
    }

    public void pauseShimmer() {
        if (mShimmer != null) {
            mShimmer.cancel();
        }
    }

    public void onScreenOn() {
        if (mIsLocked) {

            startShimmer();

            // 将缩到屏幕底部的新闻栏展开
            if (mSlidingUpView != null && mPager.getCurrentItem() == 1) {
                mSlidingUpView.getSliderView().animate().translationY(0).setDuration(400)
                        .setInterpolator(new DecelerateInterpolator()).start();
            }
        }

        PandoraBoxManager.newInstance(mContext).onScreenOn();
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
