
package cn.zmdx.kaka.locker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.battery.BatteryView;
import cn.zmdx.kaka.locker.battery.BatteryView.ILevelCallBack;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
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
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.wallpaper.OldOnlineWallpaperView;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager.ISmartWeatherCallback;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherInfo;
import cn.zmdx.kaka.locker.weather.utils.SmartWeatherUtils;
import cn.zmdx.kaka.locker.widget.DigitalClocks;
import cn.zmdx.kaka.locker.widget.SensorImageView;
import cn.zmdx.kaka.locker.widget.SlidingPaneLayout;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout.SimplePanelSlideListener;
import cn.zmdx.kaka.locker.widget.ViewPagerCompat;
import cn.zmdx.kaka.locker.widget.WallpaperPanelLayout;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

@SuppressWarnings("deprecation")
public class LockScreenManager {

    protected static final int MAX_TIMES_SHOW_GUIDE = 3;

    protected static final String TAG = "LockScreenManager";

    private SlidingUpPanelLayout mSliderView;

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

    private OldOnlineWallpaperView mOnlineWallpaperView;

    private LinearLayout mOnlineViewContainer, mLockDataView;

    private boolean mNeedPassword = false;

    private BatteryView batteryView;

    private ImageView mCameraIcon;

    private ViewPagerCompat mPager;

    private SensorImageView mSensorImageView, mBlurImageView;

    private View mMainPage, mDimBg;

    private SlidingUpPanelLayout mSlidingUpView;

    private boolean mKeepBlurEffect = false;

    private NotificationListView mNotificationListView;

    private ShimmerTextView mShimmerTextView;

    private Shimmer mShimmer;

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
        mWinParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_DISMISS_KEYGUARD
                | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | LayoutParams.FLAG_HARDWARE_ACCELERATED | LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        mWinParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        final Display display = mWinManager.getDefaultDisplay();
        mWinParams.height = BaseInfoHelper.getRealHeight(display);

        mWinParams.x = 0;
        mWinParams.y = 0;
        mWinParams.format = PixelFormat.TRANSPARENT;
        // params.format=PixelFormat.RGBA_8888;
        mWinParams.windowAnimations = R.style.anim_locker_window;
        // mWinParams.softInputMode = WindowManager.LayoutParams.SOFT_INPU
        mWinParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        mWinParams.gravity = Gravity.TOP | Gravity.START;

        // initLockScreenViews();

        initNewLockScreenViews();
        mWinManager.addView(mEntireView, mWinParams);
        startFakeActivity();

        // refreshContent();
        // setDate();

        notifyLocked();

        // 尝试拉取资讯数据及图片的预下载
        // PandoraBoxDispatcher.getInstance().pullData();

        // NotificationInterceptor.getInstance(mContext).tryDispatchCustomNotification();
        // NotificationInterceptor.getInstance(mContext).tryPullCustomNotificationData();

        checkNewVersion();

        String currentDate = BaseInfoHelper.getCurrentDate();
        UmengCustomEventManager.statisticalGuestureLockTime(pandoraConfig, currentDate);
    }

    private void toggleAnimation(View target) {
        if (mShimmer != null) {
            mShimmer.setDuration(5000);// 默认是1s
            mShimmer.setStartDelay(1800);// 默认间隔为0
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
        mShimmerTextView = (ShimmerTextView) mMainPage.findViewById(R.id.unlockShimmerTextView);
        mShimmer = new Shimmer();
        toggleAnimation(mMainPage);

        mMainPage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mDate = (TextView) mMainPage.findViewById(R.id.lock_date);
        setDate();

        mBatteryInfo = (TextView) mMainPage.findViewById(R.id.battery_info);
        batteryView = (BatteryView) mMainPage.findViewById(R.id.batteryView);
        batteryView.setLevelListener(new ILevelCallBack() {

            @Override
            public void onLevelChanged(int level) {
                mBatteryInfo.setText(level + "%");
            }
        });
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
                mSlidingUpView.hidePanel();
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

                mSlidingUpView.showPanel();
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

    // private SimpleDrawerListener mRightDrawableListener = new
    // SimpleDrawerListener() {
    //
    // public void onDrawerSlide(View drawerView, float slideOffset) {
    // setWallpaperBlurEffect(slideOffset);
    // setMainPageAlpha(1.0f - slideOffset * 0.5f);
    // if (slideOffset == 0) {
    // mSlidingUpView.showPanel();
    // } else {
    // mSlidingUpView.hidePanel();
    // }
    // };
    //
    // public void onDrawerClosed(View drawerView) {
    // // boolean isOpened = mRightDrawerLayout.isDrawerOpen(drawerView);
    // };
    //
    // public void onDrawerOpened(View drawerView) {
    // };
    // };

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
            PandoraBoxManager.newInstance(mContext).initBody();
            PandoraBoxManager.newInstance(mContext).refreshAllNews();
            pauseWallpaperTranslation();
        };

        public void onPanelCollapsed(View panel) {
            resumeWallpaperTranslation();
            PandoraBoxManager.newInstance(mContext).closeDetailPage(false);
            PandoraBoxManager.newInstance(mContext).reset();
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
        long str2TimeMillis = SmartWeatherUtils.str2TimeMillis(mPandoraConfig
                .getLastCheckWeatherTime());
        if (System.currentTimeMillis() - str2TimeMillis < PandoraPolicy.MIN_CHECK_WEATHER_DURAION) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("检查天气条件不满足,使用缓存数据");
            }
            SmartWeatherInfo smartWeatherInfo = PandoraWeatherManager.getInstance()
                    .getWeatherFromCache();
            if (smartWeatherInfo != null) {
                PandoraBoxManager.newInstance(mContext).updateView(smartWeatherInfo);
            } else {
                PandoraBoxManager.newInstance(mContext).updateView(null);
            }
        } else {
            PandoraWeatherManager.getInstance().getWeatherFromNetwork(new ISmartWeatherCallback() {

                @Override
                public void onSuccess(SmartWeatherInfo smartWeatherInfo) {
                    PandoraBoxManager.newInstance(mContext).updateView(smartWeatherInfo);
                }

                @Override
                public void onFailure() {
                    PandoraBoxManager.newInstance(mContext).updateView(null);
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

    private void refreshContent() {
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

        // mFoldablePage =
        // PandoraBoxManager.newInstance(mContext).getFoldablePage();

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
    }

    // @SuppressLint("InflateParams")
    // private void initLockScreenViews() {
    // mEntireView = (ViewGroup) LayoutInflater.from(mContext).inflate(
    // R.layout.pandora_lockscreen, null);
    // initGuideView();
    // // initSecurePanel();
    // mSlidingPanelLayout = (SlidingPaneLayout)
    // mEntireView.findViewById(R.id.sliding_layout);
    // mSlidingPanelLayout.setPanelSlideListener(mSlideOutListener);
    // mSlidingPanelLayout.setSliderFadeColor(Color.parseColor("#a0000000"));
    // mSlidingPanelLayout.setOverhangVisiable(mNeedPassword);
    // mSlidingPanelLayout.setShadowDrawableLeft(mContext.getResources().getDrawable(
    // R.drawable.sliding_panel_layout_shadow));
    // mSlidingBehindLayout = (FrameLayout)
    // mEntireView.findViewById(R.id.sliding_behind_layout);
    // mSlidingBehindBlurView = (ImageView)
    // mEntireView.findViewById(R.id.sliding_behind_blur);
    // mBatteryInfo = (TextView) mEntireView.findViewById(R.id.battery_info);
    // mBoxView = (ViewGroup) mEntireView.findViewById(R.id.flipper_box);
    // mDate = (TextView) mEntireView.findViewById(R.id.lock_date);
    // mTemperature = (TextView)
    // mEntireView.findViewById(R.id.lock_temperature);
    // mLockDataView = (LinearLayout)
    // mEntireView.findViewById(R.id.lock_date_view);
    // mWeatherSummary = (TextView)
    // mEntireView.findViewById(R.id.weather_summary);
    // mDigitalClockView = (DigitalClocks)
    // mEntireView.findViewById(R.id.digitalClock);
    //
    // batteryView = (BatteryView) mEntireView.findViewById(R.id.batteryView);
    // batteryView.setLevelListener(new ILevelCallBack() {
    //
    // @Override
    // public void onLevelChanged(int level) {
    // mBatteryInfo.setText(level + "%");
    // }
    // });
    // mSliderView = (SlidingUpPanelLayout)
    // mEntireView.findViewById(R.id.locker_view);
    // mSliderView.setPanelSlideListener(mSlideUpListener);
    // if (!ViewConfiguration.get(mContext).hasPermanentMenuKey()) {// 存在虚拟按键
    // mSliderView.setPanelHeight(BaseInfoHelper.dip2px(mContext, 110));
    // } else {
    // mSliderView.setPanelHeight(BaseInfoHelper.dip2px(mContext, 80));
    // }
    // setDrawable();
    // initCamera();
    // initOnlinePaperPanel();
    // }

    private SlidingUpPanelLayout.PanelSlideListener mSlideUpListener = new SlidingUpPanelLayout.PanelSlideListener() {

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPanelCollapsed(View panel) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPanelExpanded(View panel) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPanelAnchored(View panel) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPanelHidden(View panel) {
            // TODO Auto-generated method stub

        }

    };

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
        guideView.setAlpha(0);
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
            // mSlidingBehindLayout = (FrameLayout) mEntireView
            // .findViewById(R.id.sliding_behind_layout);

            view.setTag("passwordView");
            container.addView(view);
            mNeedPassword = true;
        } else {
            mNeedPassword = false;
        }
    }

    protected void initOnlinePaperPanelView() {
        if (null == mOnlineWallpaperView) {
            mOnlineWallpaperView = new OldOnlineWallpaperView(mContext);
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

    public void initWallpaper() {
        mCurTheme = ThemeManager.getCurrentTheme();
        final Drawable curWallpaper = mCurTheme.getCurDrawable();
        Drawable lockBg = LockerUtils.renderScreenLockerWallpaper(
                ((ImageView) mEntireView.findViewById(R.id.lockerBg)), curWallpaper);

        LockerUtils.renderScreenLockerBlurEffect(mBlurImageView, lockBg);
    }

    private void doFastBlur(Drawable bgDrawable) {
        Bitmap bitmap = PandoraUtils.doFastBlur(mContext, mSlidingPanelLayout.getOverhangSize(),
                ImageUtils.drawable2Bitmap(bgDrawable), mSlidingPanelLayout);
        mSlidingBehindBlurView.setImageBitmap(bitmap);
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
        cancelAnimatorIfNeeded();

        PandoraBoxManager.newInstance(mContext).onFinish();

        mWinManager.removeView(mEntireView);
        mEntireView = null;
        mIsLocked = false;
        isInit = false;

        mOnlineWallpaperView = null;
        // mOnlineViewContainer.removeAllViews();

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
        // invisiableViews(mLockDataView, mWeatherSummary, mDigitalClockView);
        // cancelAnimatorIfNeeded();
        if (mSliderView != null && !mSliderView.isPanelExpanded()) {
            mSliderView.expandPanel();
        }
        if (mDigitalClockView != null) {
            mDigitalClockView.setTickerStoped(true);
        }

        // 检查是否有读取通知权限
        NotificationInterceptor.getInstance(mContext).checkPermission();
    }

    public void onScreenOn() {
        if (mIsLocked) {
            // processAnimations();
            // processWeatherInfo();
            // refreshContent();
            if (mDigitalClockView != null) {
                mDigitalClockView.setTickerStoped(false);
            }
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
        // float rate = mSliderView.getContentViewWidthHeightRate();
        float rate = 0;
        if (rate == 0) {
            rate = 1.0f;
        }
        mBoxRate = rate;
        return mBoxRate;
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
