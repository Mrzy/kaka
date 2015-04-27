
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.LockScreenManager.OnBackPressedListener;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.NewsFactory.IOnLoadingListener;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.adapter.BeautyPageAdapter;
import cn.zmdx.kaka.locker.content.adapter.StickRecyclerAdapter;
import cn.zmdx.kaka.locker.content.adapter.StickRecyclerAdapter.OnStickClickListener;
import cn.zmdx.kaka.locker.content.view.CircleSpiritButton;
import cn.zmdx.kaka.locker.content.view.HeaderCircleButton;
import cn.zmdx.kaka.locker.content.view.NewsDetailLayout;
import cn.zmdx.kaka.locker.event.BottomDockUmengEventManager;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.notification.view.NotificationListView;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView.IOnlineWallpaperListener;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;
import cn.zmdx.kaka.locker.widget.PagerSlidingTabStrip;
import cn.zmdx.kaka.locker.widget.PandoraRecyclerView;
import cn.zmdx.kaka.locker.widget.PandoraSwipeRefreshLayout;
import cn.zmdx.kaka.locker.widget.SwitchButton;
import cn.zmdx.kaka.locker.widget.ViewPagerCompat;

public class PandoraBoxManager implements View.OnClickListener {

    public static final int NEWS_THEME_DAY = 1;

    public static final int NEWS_THEME_NIGHT = 2;

    private static PandoraBoxManager mPbManager;

    private Context mContext;

    private LayoutInflater mInflater;

    private View mHeaderPart1, mHeaderPart2;

    private View mEntireView;

    private FrameLayout mDetailLayout;

    private CircleSpiritButton mBackBtn;

    private OnlineWallpaperView mWallpaperView;

    private HeaderCircleButton mCircle;

    // private ImageView mNotifyTip;

    private ViewPagerCompat mViewPager;

    private static final int[] mTabColors = new int[] {
            Color.parseColor("#26a69a"), Color.parseColor("#e84e40"), Color.parseColor("#ab47bc"),
            Color.parseColor("#8bc34a"), Color.parseColor("#ea861c"), Color.parseColor("#3db7ff")
    };

    private static final int[] mFloatingButtonColors = new int[] {
            Color.parseColor("#a026a69a"), Color.parseColor("#a0e84e40"),
            Color.parseColor("#a0ab47bc"), Color.parseColor("#a08bc34a"),
            Color.parseColor("#a0ea861c"), Color.parseColor("#a03db7ff")
    };

    private static final int NEWS_TIP_HEIGHT = BaseInfoHelper
            .dip2px(HDApplication.getContext(), 40);

    private static final int NEWS_TOP_CIRCLE_HEIGHT = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 80);

    protected static final int KEEP_TIP_TIME_DEFAULT = 4000;

    private List<View> mPages;

    private FrameLayout mTipLayout;

    private int mCurItem = 2;

    private PandoraBoxManager(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mEntireView = mInflater.inflate(R.layout.news_page_layout, null);
        mDetailLayout = (FrameLayout) mEntireView.findViewById(R.id.detailLayout);
    }

    public synchronized static PandoraBoxManager newInstance(Context context) {
        if (mPbManager == null) {
            mPbManager = new PandoraBoxManager(context);
        }
        return mPbManager;
    }

    public void initHeader() {
        mHeaderPart1 = mEntireView.findViewById(R.id.header_part1);
        mHeaderPart1.getLayoutParams().height = BaseInfoHelper.dip2px(mContext, 80);
        mHeaderPart1.requestLayout();
        mHeaderPart2 = mEntireView.findViewById(R.id.header_part2);
        if (PandoraConfig.newInstance(mContext).isNotifyFunctionOn()
                && !BaseInfoHelper.isSupportTranslucentStatus()) {
            // 如果此时设置显示通知栏并且设备不支持通知栏透明，则隐藏此透明区域
            mHeaderPart2.setVisibility(View.GONE);
        } else {
            mHeaderPart2.setVisibility(View.VISIBLE);
        }
        mCircle = (HeaderCircleButton) mHeaderPart1.findViewById(R.id.header_circle);
        // 处理点击时间
        final int height = BaseInfoHelper.dip2px(mContext, 20);
        mHeaderPart1.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        View slider = LockScreenManager.getInstance().getSliderView();
                        if (slider != null) {
                            slider.animate().translationY(-height).setDuration(200).start();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        View sliderView = LockScreenManager.getInstance().getSliderView();
                        if (sliderView != null) {
                            sliderView.animate().translationY(0).setDuration(200).start();
                        }
                        break;
                }
                return false;
            }
        });
    }

    private boolean mIsHeaderCircleAnimating = false;

    public void startHeaderCircleAnimation() {
        if (mIsHeaderCircleAnimating || mCircle == null) {
            return;
        } else {
            if (!HDBNetworkState.isNetworkAvailable()) {
                mCircle.findViewById(R.id.upArrow).setVisibility(View.VISIBLE);
                return;
            }
        }

        mIsHeaderCircleAnimating = true;
        final int offset = mCircle.getHeight() / 3;
        final View arrow = mCircle.findViewById(R.id.upArrow);
        final View newsTv = mCircle.findViewById(R.id.newsTv);
        newsTv.setVisibility(View.GONE);
        arrow.setTranslationY(mCircle.getHeight() / 3);
        arrow.setAlpha(0f);
        arrow.setVisibility(View.VISIBLE);
        arrow.animate().translationY(0).alpha(1f).setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        arrow.animate().translationY(-offset).alpha(0f).setStartDelay(500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        arrow.setVisibility(View.GONE);
                                        newsTv.setTranslationY(offset);
                                        newsTv.setAlpha(0f);
                                        newsTv.setVisibility(View.VISIBLE);
                                        newsTv.animate().translationY(0).alpha(1f).setDuration(500)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        newsTv.animate()
                                                                .translationY(-offset)
                                                                .alpha(0f)
                                                                .setStartDelay(500)
                                                                .setListener(
                                                                        new AnimatorListenerAdapter() {
                                                                            @Override
                                                                            public void onAnimationEnd(
                                                                                    Animator animation) {
                                                                                newsTv.setVisibility(View.GONE);
                                                                                arrow.setTranslationY(offset);
                                                                                arrow.setVisibility(View.VISIBLE);
                                                                                arrow.animate()
                                                                                        .translationY(
                                                                                                0)
                                                                                        .alpha(1f)
                                                                                        .setDuration(
                                                                                                500)
                                                                                        .setListener(
                                                                                                new AnimatorListenerAdapter() {
                                                                                                    public void onAnimationEnd(
                                                                                                            Animator animation) {
                                                                                                        mIsHeaderCircleAnimating = false;
                                                                                                    };
                                                                                                });
                                                                            }
                                                                        });
                                                    }
                                                }).start();
                                    }
                                }).setDuration(500).start();
                    }
                });
        mCircle.startTransitionDrawable(2500);
    }

    private boolean mInitBody = false;

    public void initBody() {
        if (mInitBody) {
            return;
        }
        mInitBody = true;

        mTipLayout = (FrameLayout) mEntireView.findViewById(R.id.news_tip_layout);

        mBackBtn = (CircleSpiritButton) mEntireView.findViewById(R.id.backBtn);
        mBackBtn.setColorNormal(mFloatingButtonColors[2]);
        mBackBtn.setColorPressed(mFloatingButtonColors[2]);
        mBackBtn.setOnClickListener(this);
        mBackBtn.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {

            @Override
            public void onViewAttachedToWindow(View v) {
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mNotifyReceiver);
                IntentFilter filter = new IntentFilter(
                        NotificationListView.ACTION_NOTIFICATION_POSTED);
                LocalBroadcastManager.getInstance(mContext).registerReceiver(mNotifyReceiver,
                        filter);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mNotifyReceiver);
            }
        });
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mNotifyReceiver);
        IntentFilter filter = new IntentFilter(NotificationListView.ACTION_NOTIFICATION_POSTED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mNotifyReceiver, filter);

        mViewPager = (ViewPagerCompat) mEntireView.findViewById(R.id.newsViewPager);
        mPages = new ArrayList<View>();
        initNewsPages();
        List<String> titles = new ArrayList<String>();
        initTitles(titles);
        NewsPagerAdapter pagerAdapter = new NewsPagerAdapter(mPages, titles);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(mCurItem);

        final PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) mEntireView
                .findViewById(R.id.newsTabStrip);
        tabStrip.setViewPager(mViewPager);
        tabStrip.setTabBgColors(mTabColors);
        tabStrip.setShouldExpand(false);
        tabStrip.setShouldChangeTextColor(true);
        tabStrip.setTextColor(0xaaFFFFFF);
        tabStrip.setTextPressColor(0xFFFFFFFF);
        tabStrip.setDefaultPosition(mCurItem);
        tabStrip.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mBackBtn.setColorNormal(mFloatingButtonColors[position]);
                mBackBtn.setColorPressed(mFloatingButtonColors[position]);

                final int category = position;
                // 延迟加载，保证切换tab流畅
                HDBThreadUtils.postOnUiDelayed(new Runnable() {
                    public void run() {
                        refreshNewsByCategory(category);
                    };
                }, 500);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        int theme = PandoraConfig.newInstance(mContext).isNightModeOn() ? NEWS_THEME_NIGHT
                : NEWS_THEME_DAY;
        switchNewsTheme(theme);
    }

    /**
     * 打开页面顶部的提示区
     * 
     * @param contentView 提示内容
     * @param withAnimator 是否需要动画
     * @param keepTime 保持时间，如果小于等于0则默认为4s，4s后将自动关闭这个提示区
     */
    private void openTipLayout(View contentView, boolean withAnimator, final int keepTime) {
        if (mTipLayout != null) {
            mTipLayout.removeAllViews();
            mTipLayout.addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            if (!withAnimator) {
                mTipLayout.setVisibility(View.VISIBLE);
                return;
            }

            final int targetHeight = NEWS_TIP_HEIGHT;
            ViewGroup.LayoutParams lp = mTipLayout.getLayoutParams();
            lp.height = 0;
            mTipLayout.setVisibility(View.VISIBLE);
            Animation anim = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        int time = (keepTime <= 0) ? KEEP_TIP_TIME_DEFAULT : keepTime;
                        HDBThreadUtils.postOnUiDelayed(new Runnable() {
                            public void run() {
                                closeTipLayout(true);
                            };
                        }, time);
                    } else {
                        mTipLayout.getLayoutParams().height = (int) (targetHeight * interpolatedTime);
                        mTipLayout.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            anim.setDuration(500);
            mTipLayout.startAnimation(anim);
        }
    }

    private void closeTipLayout(boolean withAnimator) {
        if (mTipLayout != null && mTipLayout.getVisibility() != View.GONE) {
            if (!withAnimator) {
                mTipLayout.setVisibility(View.GONE);
                mTipLayout.removeAllViews();
                return;
            }

            final int initHeight = NEWS_TIP_HEIGHT;
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        mTipLayout.setVisibility(View.GONE);
                    } else {
                        mTipLayout.getLayoutParams().height = initHeight
                                - (int) (initHeight * interpolatedTime);
                        mTipLayout.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration(300);
            mTipLayout.startAnimation(a);
        }
    }

    private OnBackPressedListener mBackPressedListener = new OnBackPressedListener() {

        @Override
        public void onBackPressed() {
            if (isDetailPageOpened()) {
                closeDetailPage(true);
            } else {
                LockScreenManager.getInstance().collapseNewsPanel();
            }
        }
    };

    private BroadcastReceiver mNotifyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("received notification at news detail page");
            }
            Bitmap bitmap = (Bitmap) intent.getParcelableExtra("icon");
            // 转变为圆形
            Bitmap roundBmp = ImageUtils.getRoundBitmap(bitmap, 100, false);
            mBackBtn.notifyNewFeed(roundBmp);
        }
    };

    private boolean isNewsPanelExpanded() {
        return LockScreenManager.getInstance().isNewsPanelExpanded();
    }

    private boolean isNewsExpanded = false;

    public void notifyNewsPanelSlide(View panel, float slideOffset) {
        ViewGroup.LayoutParams lp = mHeaderPart1.getLayoutParams();
        lp.height = (int) (NEWS_TOP_CIRCLE_HEIGHT * (1.0f - slideOffset));
        mHeaderPart1.requestLayout();
    }

    /**
     * 新闻面板完全展开时会调用该方法
     */
    public void notifyNewsPanelExpanded() {
        isNewsExpanded = true;
        // 缩小顶部区域

        initBody();

        if (mViewPager != null) {
            int position = mViewPager.getCurrentItem();
            int category = position;
            refreshNewsByCategory(category);
        }
        PandoraConfig.newInstance(mContext).saveLastShowUnreadNews(System.currentTimeMillis());
        mBackBtn.startAppearAnimator();

        requestWakeLock();

        // 提示用户开启夜间模式或自动切换到白昼模式
        HDBThreadUtils.postOnUiDelayed(new Runnable() {
            @Override
            public void run() {
                openNightModeTip();
            }
        }, 5000);

        LockScreenManager.getInstance().registBackPressedListener(mBackPressedListener);

        BottomDockUmengEventManager.statisticalNewsPanelExpanded();
    }

    private void openNightModeTip() {
        long lastTipTime = PandoraConfig.newInstance(mContext).getLastTipOpenNightModeTime();
        long current = System.currentTimeMillis();
        if (isNewsPanelExpanded()
                && current - lastTipTime > (BuildConfig.DEBUG ? 60 * 1000 : 3 * 60 * 60 * 1000)) {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (PandoraConfig.newInstance(mContext).isNightModeOn()) {
                if (hour > 7 && hour < 21) {
                    // 如果当前是白昼，且当前模式为夜间模式，则提示是否切换为白昼模式
                    openTipLayout(createOpenNightModeView(), true, 8000);
                    PandoraConfig.newInstance(mContext).saveLastTipOpenNightModeTime(current);
                }
            } else {
                if (hour >= 21 || hour <= 7) {
                    // 当前时间是晚上21点之后，则开启提示，是否打开夜间模式
                    openTipLayout(createOpenNightModeView(), true, 8000);
                    PandoraConfig.newInstance(mContext).saveLastTipOpenNightModeTime(current);
                }
            }
        }
    }

    private View createOpenNightModeView() {
        boolean checked = PandoraConfig.newInstance(mContext).isNightModeOn();
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_tip_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.news_tip_title);
        if (checked) {
            tv.setText(R.string.news_tip_close_nightmode);
        } else {
            tv.setText(R.string.news_tip_open_nightmode);
        }
        SwitchButton sb = (SwitchButton) view.findViewById(R.id.news_tip_switch);
        sb.setChecked(checked);
        sb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchNewsTheme(NEWS_THEME_NIGHT);
                } else {
                    switchNewsTheme(NEWS_THEME_DAY);
                }
                closeTipLayout(true);
            }
        });
        return view;
    }

    private PowerManager.WakeLock mWakeLock;

    @SuppressWarnings("deprecation")
    private void requestWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "pandora");
            mWakeLock.setReferenceCounted(false);
        }
        mWakeLock.acquire();
    }

    private void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
        }
    }

    public void notifyNewsPanelCollapsed() {
        isNewsExpanded = false;
        // hideDateView();
        // ivArrowUp.animate().rotation(0).setDuration(300);
        // expandTopCircle();

        closeDetailPage(false);
        // PandoraBoxManager.newInstance(mContext).resetDefaultPage();
        if (mBackBtn != null) {
            mBackBtn.setTranslationY(BaseInfoHelper.dip2px(mContext, 100));
        }
        if (mJokeRefreshView != null) {
            mJokeRefreshView.setRefreshing(false);
        }
        if (mBeautyRefreshView != null) {
            mBeautyRefreshView.setRefreshing(false);
        }
        if (mMicroMediaRefreshView != null) {
            mMicroMediaRefreshView.setRefreshing(false);
        }
        if (mGossipRefreshView != null) {
            mGossipRefreshView.setRefreshing(false);
        }
        if (mHotRefreshView != null) {
            mHotRefreshView.setRefreshing(false);
        }

        controlAutoScroll(false, false);

        LockScreenManager.getInstance().unRegistBackPressedListener(mBackPressedListener);

        releaseWakeLock();
        BottomDockUmengEventManager.statisticalNewsPanelCollapsed();
    }

    public void refreshNewsByCategory(int category) {
        if (category == NewsFactory.NEWS_TYPE_HEADLINE) {
            NewsFactory.updateNews(category, mHotAdapter, mHotNews, mHotRefreshView, false, false,
                    mLoadingListener);
            controlAutoScroll(true, false);
        } else if (category == NewsFactory.NEWS_TYPE_GOSSIP) {
            NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, mGossipAdapter, mGossipNews,
                    mGossipRefreshView, false, false, null);
            controlAutoScroll(false, false);
        } else if (category == NewsFactory.NEWS_TYPE_MICRO_CHOICE) {
            NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE, mMicroMediaAdapter,
                    mMicroMediaNews, mMicroMediaRefreshView, false, false, mMicroLoadingListener);
            controlAutoScroll(false, true);
        } else if (category == NewsFactory.NEWS_TYPE_BEAUTY) {
            NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, mBeautyAdapter, mBeautyNews,
                    mBeautyRefreshView, false, false, null);
            controlAutoScroll(false, false);
        } else if (category == NewsFactory.NEWS_TYPE_JOKE) {
            NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, mJokeAdapter, mJokeNews,
                    mJokeRefreshView, false, false, null);
            controlAutoScroll(false, false);
        } else if (category == NewsFactory.NEWS_TYPE_WALLPAPER) {
            if (null != mWallpaperView) {
                mWallpaperView.refreshData();
            }
            controlAutoScroll(false, false);
        } else {
            throw new IllegalArgumentException("invalid news category");
        }
    }

    /**
     * 立即拉取热门和八卦的数据，2秒后，加载微精选和美女的数据，4秒后加载搞笑的数据.
     */
    public void refreshAllNews() {
        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_HEADLINE, mHotAdapter, mHotNews,
                mHotRefreshView, false, false, mLoadingListener);
        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, mGossipAdapter, mGossipNews,
                mGossipRefreshView, false, false, null);
        HDBThreadUtils.postOnUiDelayed(new Runnable() {
            @Override
            public void run() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE, mMicroMediaAdapter,
                        mMicroMediaNews, mMicroMediaRefreshView, false, false,
                        mMicroLoadingListener);
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, mBeautyAdapter, mBeautyNews,
                        mBeautyRefreshView, false, false, null);
            }
        }, 2000);
        HDBThreadUtils.postOnUiDelayed(new Runnable() {
            @Override
            public void run() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, mJokeAdapter, mJokeNews,
                        mJokeRefreshView, false, false, null);
            }
        }, 4000);
        if (null != mWallpaperView) {
            mWallpaperView.refreshData();
        }
    }

    private void initTitles(List<String> titles) {
        final Resources res = mContext.getResources();
        titles.add(res.getString(R.string.pandora_news_classify_wallpaper));
        titles.add(res.getString(R.string.pandora_news_classify_headlines));
        titles.add(res.getString(R.string.pandora_news_classify_gossip));
        titles.add(res.getString(R.string.pandora_news_classify_micro_choice));
        titles.add(res.getString(R.string.pandora_news_classify_beauty));
        titles.add(res.getString(R.string.pandora_news_classify_funny));
    }

    private void initNewsPages() {
        View wallPaperView = initWallPaperView();
        View hotNewsView = initHotNewsView();
        View gossipView = initGossipView();
        View microMediaView = initMicroMediaView();
        View beautyView = initBeautyView();
        View jokeView = initJokeView();
        mPages.clear();
        mPages.add(wallPaperView);
        mPages.add(hotNewsView);
        mPages.add(gossipView);
        mPages.add(microMediaView);
        mPages.add(beautyView);
        mPages.add(jokeView);
    }

    private List<ServerImageData> mHotNews = new ArrayList<ServerImageData>();

    private List<ServerImageData> mGossipNews = new ArrayList<ServerImageData>();

    private List<ServerImageData> mMicroMediaNews = new ArrayList<ServerImageData>();

    private List<ServerImageData> mBeautyNews = new ArrayList<ServerImageData>();

    private List<ServerImageData> mJokeNews = new ArrayList<ServerImageData>();

    private BeautyPageAdapter mBeautyAdapter, mJokeAdapter, mGossipAdapter;

    private StickRecyclerAdapter mHotAdapter, mMicroMediaAdapter;

    private PandoraSwipeRefreshLayout mJokeRefreshView, mBeautyRefreshView, mMicroMediaRefreshView,
            mGossipRefreshView, mHotRefreshView;

    public void freeMemory() {
        // if (mPages != null) {
        // mPages.clear();
        // }
        // mHotNews.clear();
        // mGossipNews.clear();
        // mGossipNews.clear();
        // mGossipNews.clear();
        // mJokeNews.clear();
        // mPbManager = null;
    }

    private View createEmptyView() {
        TextView view = new TextView(mContext);
        view.setGravity(Gravity.CENTER);
        boolean isNightMode = PandoraConfig.newInstance(mContext).isNightModeOn();
        if (isNightMode) {
            view.setTextColor(Color.parseColor("#a0ffffff"));
        } else {
            view.setTextColor(Color.parseColor("#a0000000"));
        }
        if (HDBNetworkState.isNetworkAvailable()) {
            view.setText(mContext.getString(R.string.tip_loading_news));
        } else {
            view.setText(mContext.getString(R.string.tip_no_news));
        }
        view.setTextSize(18f);
        return view;
    }

    private View initJokeView() {
        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.pager_news_layout, null);
        PandoraRecyclerView rv = (PandoraRecyclerView) view.findViewById(R.id.recyclerView);
        rv.setVerticalFadingEdgeEnabled(true);
        rv.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        sglm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        // sglm.offsetChildrenHorizontal(100);
        rv.setLayoutManager(sglm);
        rv.setHasFixedSize(true);

        // final List<ServerImageData> news = new ArrayList<ServerImageData>();
        mJokeAdapter = new BeautyPageAdapter(mContext, mJokeNews);

        mJokeAdapter.setOnItemClickListener(new BeautyPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = mJokeNews.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this, sid);
                openDetailPage(ndl);
                UmengCustomEventManager.statisticalOpenNewsDetail(sid.getId(), "joke");
            }
        });
        rv.setAdapter(mJokeAdapter);

        View emptyView = createEmptyView();
        rv.setEmptyView(emptyView);
        view.addView(emptyView);

        mJokeRefreshView = (PandoraSwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mJokeRefreshView.setProgressBackgroundColorSchemeColor(mFloatingButtonColors[5]);
        mJokeRefreshView.setColorSchemeColors(Color.WHITE);

        // NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, mJokeAdapter,
        // mJokeNews,
        // mJokeRefreshView, true, true);

        mJokeRefreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, mJokeAdapter, mJokeNews,
                        mJokeRefreshView, false, true, null);
                UmengCustomEventManager.statisticalPullRefreshNews("joke");
            }

        });

        rv.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] lastVisibleItem = ((StaggeredGridLayoutManager) sglm)
                        .findLastVisibleItemPositions(null);
                int lastItem = Math.max(lastVisibleItem[0], lastVisibleItem[1]);
                int totalItemCount = sglm.getItemCount();
                // lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                // dy>0 表示向下滑动
                if (lastItem >= totalItemCount - 4 && dy > 0) {
                    NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, mJokeAdapter, mJokeNews,
                            mJokeRefreshView, true, false, null);
                }
            }
        });
        return view;
    }

    private View initBeautyView() {
        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.pager_news_layout, null);
        PandoraRecyclerView rv = (PandoraRecyclerView) view.findViewById(R.id.recyclerView);
        rv.setVerticalFadingEdgeEnabled(true);
        rv.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        sglm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        // sglm.offsetChildrenHorizontal(100);
        rv.setLayoutManager(sglm);
        rv.setHasFixedSize(true);

        // final List<ServerImageData> news = new ArrayList<ServerImageData>();
        mBeautyAdapter = new BeautyPageAdapter(mContext, mBeautyNews);

        mBeautyAdapter.setOnItemClickListener(new BeautyPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = mBeautyNews.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this, sid);
                openDetailPage(ndl);
                UmengCustomEventManager.statisticalOpenNewsDetail(sid.getId(), "beauty");
            }
        });
        rv.setAdapter(mBeautyAdapter);

        View emptyView = createEmptyView();
        rv.setEmptyView(emptyView);
        view.addView(emptyView);

        mBeautyRefreshView = (PandoraSwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mBeautyRefreshView.setProgressBackgroundColorSchemeColor(mFloatingButtonColors[4]);
        mBeautyRefreshView.setColorSchemeColors(Color.WHITE);

        // NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, mBeautyAdapter,
        // mBeautyNews,
        // mBeautyRefreshView, true, true);

        mBeautyRefreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, mBeautyAdapter, mBeautyNews,
                        mBeautyRefreshView, false, true, null);
                UmengCustomEventManager.statisticalPullRefreshNews("beauty");
            }

        });

        rv.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] lastVisibleItem = ((StaggeredGridLayoutManager) sglm)
                        .findLastVisibleItemPositions(null);
                int lastItem = Math.max(lastVisibleItem[0], lastVisibleItem[1]);
                int totalItemCount = sglm.getItemCount();
                // lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                // dy>0 表示向下滑动
                if (lastItem >= totalItemCount - 4 && dy > 0) {
                    NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, mBeautyAdapter,
                            mBeautyNews, mBeautyRefreshView, true, false, null);
                }
            }
        });
        return view;
    }

    private PandoraRecyclerView mMicroRecyclerView;

    private View initMicroMediaView() {
        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.pager_news_layout, null);
        mMicroRecyclerView = (PandoraRecyclerView) view.findViewById(R.id.recyclerView);
        mMicroRecyclerView.setEmptyView(createEmptyView());
        mMicroRecyclerView.setVerticalFadingEdgeEnabled(true);
        mMicroRecyclerView.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final LinearLayoutManager llm = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        mMicroRecyclerView.setLayoutManager(llm);
        mMicroRecyclerView.setHasFixedSize(true);

        // final List<ServerImageData> news = new ArrayList<ServerImageData>();
        // mMicroMediaAdapter = new GeneralNewsPageAdapter(mContext,
        // mMicroMediaNews);

        mMicroMediaAdapter = new StickRecyclerAdapter(mContext, mMicroMediaNews, mMicroStickData);
        mMicroRecyclerView.setAdapter(mMicroMediaAdapter);

        View emptyView = createEmptyView();
        mMicroRecyclerView.setEmptyView(emptyView);
        view.addView(emptyView);

        mMicroMediaAdapter.setOnItemClickListener(new StickRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = mMicroMediaNews.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this, sid);
                openDetailPage(ndl);
                UmengCustomEventManager.statisticalOpenNewsDetail(sid.getId(), "microMedia");
            }
        });
        mMicroMediaAdapter.setOnStickClickListener(new OnStickClickListener() {

            @Override
            public void onItemClicked(ServerImageData serverImageData) {
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this, serverImageData);
                openDetailPage(ndl);
                UmengCustomEventManager.statisticalOpenNewsDetail(serverImageData.getId(),
                        "microMedia");
            }
        });

        mMicroMediaRefreshView = (PandoraSwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mMicroMediaRefreshView.setProgressBackgroundColorSchemeColor(mFloatingButtonColors[3]);
        mMicroMediaRefreshView.setColorSchemeColors(Color.WHITE);

        mMicroMediaRefreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory
                        .updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE, mMicroMediaAdapter,
                                mMicroMediaNews, mMicroMediaRefreshView, false, true,
                                mMicroLoadingListener);
                UmengCustomEventManager.statisticalPullRefreshNews("microMedia");
            }
        });
        mMicroRecyclerView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastVisibleItem = llm.findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                // lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE, mMicroMediaAdapter,
                            mMicroMediaNews, mMicroMediaRefreshView, true, false, null);
                }
            }
        });

        // NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE,
        // mMicroMediaAdapter,
        // mMicroMediaNews, mMicroMediaRefreshView, true, true);
        return view;
    }

    private List<ServerImageData> mMicroStickData = new ArrayList<ServerImageData>();

    private IOnLoadingListener mMicroLoadingListener = new IOnLoadingListener() {

        @Override
        public void onLoaded(List<ServerImageData> stickData) {
            mMicroStickData.clear();
            mMicroStickData.addAll(stickData);
            mMicroMediaAdapter.notifyDataSetChanged();
        }
    };

    private View initGossipView() {
        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.pager_news_layout, null);
        PandoraRecyclerView rv = (PandoraRecyclerView) view.findViewById(R.id.recyclerView);
        rv.setVerticalFadingEdgeEnabled(true);
        rv.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(sglm);
        rv.setHasFixedSize(true);

        // final List<ServerImageData> news = new ArrayList<ServerImageData>();
        mGossipAdapter = new BeautyPageAdapter(mContext, mGossipNews);

        mGossipAdapter.setOnItemClickListener(new BeautyPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = mGossipNews.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this, sid);
                openDetailPage(ndl);
                UmengCustomEventManager.statisticalOpenNewsDetail(sid.getId(), "gossip");
            }
        });
        rv.setAdapter(mGossipAdapter);

        View emptyView = createEmptyView();
        rv.setEmptyView(emptyView);
        view.addView(emptyView);

        mGossipRefreshView = (PandoraSwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mGossipRefreshView.setProgressBackgroundColorSchemeColor(mFloatingButtonColors[2]);
        mGossipRefreshView.setColorSchemeColors(Color.WHITE);

        // NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, mGossipAdapter,
        // mGossipNews,
        // mGossipRefreshView, true, true);

        mGossipRefreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, mGossipAdapter, mGossipNews,
                        mGossipRefreshView, false, true, null);
                UmengCustomEventManager.statisticalPullRefreshNews("gossip");
            }

        });

        rv.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] lastVisibleItem = ((StaggeredGridLayoutManager) sglm)
                        .findLastVisibleItemPositions(null);
                int lastItem = Math.max(lastVisibleItem[0], lastVisibleItem[1]);
                int totalItemCount = sglm.getItemCount();
                // lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                // dy>0 表示向下滑动
                if (lastItem >= totalItemCount - 4 && dy > 0) {
                    NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, mGossipAdapter,
                            mGossipNews, mGossipRefreshView, true, false, null);
                }
            }
        });
        return view;
    }

    private PandoraRecyclerView mHotRecyclerView;

    private View initHotNewsView() {
        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.pager_news_layout, null);
        mHotRecyclerView = (PandoraRecyclerView) view.findViewById(R.id.recyclerView);
        mHotRecyclerView.setVerticalFadingEdgeEnabled(true);
        mHotRecyclerView.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final LinearLayoutManager llm = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        mHotRecyclerView.setLayoutManager(llm);
        mHotRecyclerView.setHasFixedSize(true);

        // final List<ServerImageData> news = new ArrayList<ServerImageData>();
        mHotAdapter = new StickRecyclerAdapter(mContext, mHotNews, mHotStickData);
        mHotRecyclerView.setAdapter(mHotAdapter);

        View emptyView = createEmptyView();
        mHotRecyclerView.setEmptyView(emptyView);
        view.addView(emptyView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        mHotAdapter.setOnItemClickListener(new StickRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = mHotNews.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this, sid);
                openDetailPage(ndl);
                UmengCustomEventManager.statisticalOpenNewsDetail(sid.getId(), "headline");
            }
        });

        mHotAdapter.setOnStickClickListener(new OnStickClickListener() {

            @Override
            public void onItemClicked(ServerImageData serverImageData) {
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this, serverImageData);
                openDetailPage(ndl);
                UmengCustomEventManager.statisticalOpenNewsDetail(serverImageData.getId(),
                        "microMedia");
            }
        });

        mHotRefreshView = (PandoraSwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mHotRefreshView.setProgressBackgroundColorSchemeColor(mFloatingButtonColors[1]);
        mHotRefreshView.setColorSchemeColors(Color.WHITE);
        mHotRefreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_HEADLINE, mHotAdapter, mHotNews,
                        mHotRefreshView, false, true, mLoadingListener);
                UmengCustomEventManager.statisticalPullRefreshNews("headline");
            }

        });

        // NewsFactory.updateNews(NewsFactory.NEWS_TYPE_HEADLINE, mHotAdapter,
        // mHotNews,
        // mHotRefreshView, true, true);

        return view;
    }

    private List<ServerImageData> mHotStickData = new ArrayList<ServerImageData>();

    private IOnLoadingListener mLoadingListener = new IOnLoadingListener() {

        @Override
        public void onLoaded(List<ServerImageData> stickData) {
            mHotStickData.clear();
            mHotStickData.addAll(stickData);
            mHotAdapter.notifyDataSetChanged();
        }
    };

    private void controlAutoScroll(boolean isHotAutoScroll, boolean isMicroAutoScroll) {
        if (null != mMicroMediaAdapter) {
            mMicroMediaAdapter.setAutoScroll(isMicroAutoScroll);
            mMicroMediaAdapter.notifyDataSetChanged();
        }
        if (null != mHotAdapter) {
            mHotAdapter.setAutoScroll(isHotAutoScroll);
            mHotAdapter.notifyDataSetChanged();
        }
    }

    private void switchNewsTheme(int theme) {
        final Resources res = mContext.getResources();
        int bgColor = res.getColor(R.color.news_day_mode_behind_color);
        if (theme == NEWS_THEME_DAY) {
            bgColor = res.getColor(R.color.news_day_mode_behind_color);
            PandoraConfig.newInstance(mContext).saveNightModeState(false);
        } else if (theme == NEWS_THEME_NIGHT) {
            bgColor = res.getColor(R.color.news_night_mode_behind_color);
            PandoraConfig.newInstance(mContext).saveNightModeState(true);
        }
        if (mViewPager != null) {
            mViewPager.setBackgroundColor(bgColor);

            mHotAdapter.setTheme(theme);
            mHotAdapter.notifyDataSetChanged();

            mMicroMediaAdapter.setTheme(theme);
            mMicroMediaAdapter.notifyDataSetChanged();

            mBeautyAdapter.setTheme(theme);
            mBeautyAdapter.notifyDataSetChanged();

            mJokeAdapter.setTheme(theme);
            mJokeAdapter.notifyDataSetChanged();

            mGossipAdapter.setTheme(theme);
            mGossipAdapter.notifyDataSetChanged();
        }
    }

    private View initWallPaperView() {
        mWallpaperView = new OnlineWallpaperView(mContext, true);
        mWallpaperView.setOnlineWallpaperListener(new IOnlineWallpaperListener() {

            @Override
            public void onOpenDetailPage(View view) {
                openDetailPage(view);
            }

            @Override
            public void onCloseDetailPage(boolean withAnimator) {
                closeDetailPage(withAnimator);
            }

            @Override
            public void onGoToDetailClick(ServerOnlineWallpaper item) {

            }
        });
        return mWallpaperView;
    }

    public boolean isDetailPageOpened() {
        return mDetailLayout.getChildCount() > 0;
    }

    public void openDetailPage(View view) {
        mDetailLayout.removeAllViews();
        mDetailLayout.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mDetailLayout.setTranslationX(mDetailLayout.getWidth());
        mDetailLayout.bringToFront();
        mDetailLayout.setVisibility(View.VISIBLE);
        mDetailLayout.animate().translationX(0).setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDetailLayout.setVisibility(View.VISIBLE);
                        super.onAnimationEnd(animation);
                    }
                }).start();
    }

    public void closeDetailPage(boolean withAnimator) {
        if (withAnimator) {
            mDetailLayout.animate().translationX(mDetailLayout.getWidth()).setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (mDetailLayout != null) {
                                mDetailLayout.setVisibility(View.INVISIBLE);
                                mDetailLayout.removeAllViews();
                            }
                        }
                    }).start();
        } else {
            mDetailLayout.setVisibility(View.INVISIBLE);
            mDetailLayout.removeAllViews();
        }
    }

    private static class NewsPagerAdapter extends PagerAdapter {
        private List<View> mPages;

        private List<String> mTitles;

        public NewsPagerAdapter(List<View> pages, List<String> titles) {
            this.mPages = pages;
            this.mTitles = titles;
        }

        public View getItem(int position) {
            return mPages.get(position);
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mPages.get(position), 0);
            return mPages.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public View getEntireView() {
        return mEntireView;
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            LockScreenManager.getInstance().collapseNewsPanel();
            BottomDockUmengEventManager.statisticalNewsPanelBackClicked();
        }
    }

    /**
     * 将新闻频道定位到“头条”页
     */
    public void resetDefaultPage() {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(1, false);
        }
    }

    public void onScreenOn() {
        HDBThreadUtils.postOnUiDelayed(new Runnable() {
            @Override
            public void run() {
                startHeaderCircleAnimation();
            }
        }, 1000);
    }

    public void onScreenOff() {
        if (mCircle != null) {
            mCircle.findViewById(R.id.newsTv).setVisibility(View.GONE);
            mCircle.findViewById(R.id.upArrow).setVisibility(View.GONE);
            int theme = PandoraConfig.newInstance(mContext).isNightModeOn() ? NEWS_THEME_NIGHT
                    : NEWS_THEME_DAY;
            switchNewsTheme(theme);
        }
    }
}
