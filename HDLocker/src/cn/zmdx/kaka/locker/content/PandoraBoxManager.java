
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.LockScreenManager.OnBackPressedListener;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.adapter.BeautyPageAdapter;
import cn.zmdx.kaka.locker.content.adapter.StickRecyclerAdapter;
import cn.zmdx.kaka.locker.content.channel.ChannelBoxManager;
import cn.zmdx.kaka.locker.content.channel.ChannelBoxView;
import cn.zmdx.kaka.locker.content.channel.ChannelInfo;
import cn.zmdx.kaka.locker.content.view.CircleSpiritButton;
import cn.zmdx.kaka.locker.content.view.NewsDetailLayout;
import cn.zmdx.kaka.locker.event.BottomDockUmengEventManager;
import cn.zmdx.kaka.locker.notification.view.NotificationListView;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.widget.PagerSlidingTabStrip;
import cn.zmdx.kaka.locker.widget.SwitchButton;
import cn.zmdx.kaka.locker.widget.ViewPagerCompat;

/**
 * 新闻页管理器
 * @author shayne
 *
 */
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

    // private HeaderCircleButton mCircle;

    // private ImageView mNotifyTip;

    private ViewPagerCompat mViewPager;

    private ImageView mAddNewsTabBtn;

    private TextView mNewsHeaderTitle;

    private View mHeaderHasNewsArea;

    private View mHeaderNoNewsArea;

    public static final int[] mTabColors = new int[] {
            Color.parseColor("#26a69a"), Color.parseColor("#e84e40"), Color.parseColor("#ab47bc"),
            Color.parseColor("#8bc34a"), Color.parseColor("#ea861c"), Color.parseColor("#3db7ff"),
            Color.parseColor("#26a69a"), Color.parseColor("#e84e40"), Color.parseColor("#ab47bc"),
            Color.parseColor("#8bc34a"), Color.parseColor("#ea861c"), Color.parseColor("#3db7ff"),
            Color.parseColor("#26a69a"), Color.parseColor("#e84e40"), Color.parseColor("#e84e40")
    };

    private static final int[] mFloatingButtonColors = new int[] {
            Color.parseColor("#a026a69a"), Color.parseColor("#a0e84e40"),
            Color.parseColor("#a0ab47bc"), Color.parseColor("#a08bc34a"),
            Color.parseColor("#a0ea861c"), Color.parseColor("#a03db7ff"),
            Color.parseColor("#a026a69a"), Color.parseColor("#a0e84e40"),
            Color.parseColor("#a0ab47bc"), Color.parseColor("#a08bc34a"),
            Color.parseColor("#a0ea861c"), Color.parseColor("#a03db7ff"),
            Color.parseColor("#a026a69a"), Color.parseColor("#a0e84e40"),Color.parseColor("#e84e40")
    };

    private static final int NEWS_TIP_HEIGHT = BaseInfoHelper
            .dip2px(HDApplication.getContext(), 40);

    private int mNewsHeaderHeight = 0;//新闻页顶部的高度

    protected static final int KEEP_TIP_TIME_DEFAULT = 4000;

    private List<Integer> mPages;

    private FrameLayout mTipLayout;

    private PandoraBoxManager(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mEntireView = mInflater.inflate(R.layout.news_page_layout, null);
        mDetailLayout = (FrameLayout) mEntireView.findViewById(R.id.detailLayout);
//        final boolean hasNavbar = !ViewConfiguration.get(mContext).hasPermanentMenuKey();
        mNewsHeaderHeight = BaseInfoHelper.dip2px(mContext, 80);
    }

    public synchronized static PandoraBoxManager newInstance(Context context) {
        if (mPbManager == null) {
            mPbManager = new PandoraBoxManager(context);
        }
        return mPbManager;
    }

    //初始化新闻header
    public void initHeader() {
        mHeaderPart1 = mEntireView.findViewById(R.id.header_part1);
        mHeaderPart1.getLayoutParams().height = mNewsHeaderHeight;
        mEntireView.requestLayout();//重新布局
        mNewsHeaderTitle = (TextView) mHeaderPart1.findViewById(R.id.news_header_title);
        mHeaderHasNewsArea = mHeaderPart1.findViewById(R.id.news_header_hasContent);
        mHeaderHasNewsArea.setVisibility(View.GONE);
        mHeaderNoNewsArea = mHeaderPart1.findViewById(R.id.news_header_noData);
        mHeaderNoNewsArea.setVisibility(View.VISIBLE);
        renderNewsHeader();
        mHeaderPart2 = mEntireView.findViewById(R.id.header_part2);
        if (PandoraConfig.newInstance(mContext).isNotifyFunctionOn()
                && !BaseInfoHelper.isSupportTranslucentStatus()) {
            // 如果此时设置显示通知栏并且设备不支持通知栏透明，则隐藏此透明区域
            mHeaderPart2.setVisibility(View.GONE);
        } else {
            mHeaderPart2.setVisibility(View.VISIBLE);
        }
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

    private boolean mInitBody = false;

    private NewsPagerAdapter mNewsPagerAdapter;

    private PagerSlidingTabStrip tabStrip;

    public void initBody() {
        if (mInitBody) {
            return;
        }
        mInitBody = true;

        //TODO
        mTipLayout = (FrameLayout) mEntireView.findViewById(R.id.news_tip_layout);

        mBackBtn = (CircleSpiritButton) mEntireView.findViewById(R.id.backBtn);
        mBackBtn.setColorNormal(mFloatingButtonColors[0]);
        mBackBtn.setColorPressed(mFloatingButtonColors[0]);
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
        mPages = new ArrayList<Integer>();
        //TODO
        initNewsPages();
        mNewsPagerAdapter = new NewsPagerAdapter(this, mPages, mTabTitles);
        mViewPager.setAdapter(mNewsPagerAdapter);
        int currentItem = calCurrentItem();
        mViewPager.setCurrentItem(currentItem);


        //TODO
        tabStrip = (PagerSlidingTabStrip) mEntireView.findViewById(R.id.newsTabStrip);
        tabStrip.setViewPager(mViewPager);
        tabStrip.setTabBgColors(mTabColors);
        tabStrip.setShouldExpand(false);
        tabStrip.setShouldChangeTextColor(true);
        tabStrip.setTextColor(0xaaFFFFFF);
        tabStrip.setTextPressColor(0xFFFFFFFF);
        tabStrip.setDefaultPosition(currentItem);
        tabStrip.setTabStripPaddingRight(45);
        tabStrip.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                mBackBtn.setColorNormal(mFloatingButtonColors[position]);
                mBackBtn.setColorPressed(mFloatingButtonColors[position]);
                final int channelId = mPages.get(position);
                // 延迟加载，保证切换tab流畅
                HDBThreadUtils.postOnUiDelayed(new Runnable() {
                    public void run() {
                        //TODO
                        refreshNewsByChannelId(channelId);
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

        final View redPoint = mEntireView.findViewById(R.id.redPoint);
        
        //TODO
        mAddNewsTabBtn = (ImageView) mEntireView.findViewById(R.id.addNewsTabBtn);
        mAddNewsTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PandoraConfig.newInstance(mContext).saveTipAddChannelFunction();
                redPoint.setVisibility(View.GONE);
                View view = ChannelBoxManager.getInstance(mContext).createChannelView();
                openDetailPage(view, true);
            }
        });
        if (PandoraConfig.newInstance(mContext).isTipAddChannelFunction()) {
            redPoint.setVisibility(View.GONE);
        } else {
            redPoint.setVisibility(View.VISIBLE);
        }
    }

    public void openDetailPage(ServerImageData sid, boolean withAnimator) {
        if (sid != null) {
            final NewsDetailLayout view = new NewsDetailLayout(this, sid);
            openDetailPage(view, withAnimator);
        }
    }

    public void notifyDataSetChanged() {
        if (mNewsPagerAdapter != null) {
            initNewsPages();
            mNewsPagerAdapter = new NewsPagerAdapter(this, mPages, mTabTitles);
            mViewPager.setAdapter(mNewsPagerAdapter);
            mViewPager.setCurrentItem(calCurrentItem());
            tabStrip.notifyDataSetChanged();
        }
    }

    private int calCurrentItem() {
       // int count = mNewsPagerAdapter.getCount();
        return 0;//暂默认显示第一个tab
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
        if (mHeaderPart1 != null) {
            ViewGroup.LayoutParams lp = mHeaderPart1.getLayoutParams();
            lp.height = (int) (mNewsHeaderHeight * (1.0f - slideOffset));
            mHeaderPart1.requestLayout();
        }
    }

    private Runnable mExpandedAction;

    public void setExpandedAction(Runnable action) {
        mExpandedAction = action;
    }

    /**
     * 新闻面板完全展开时会调用该方法
     */
    public void notifyNewsPanelExpanded() {
        isNewsExpanded = true;
        // 缩小顶部区域

        initBody();

        if (mExpandedAction != null) {
            HDBThreadUtils.runOnUi(mExpandedAction);
        }

        HDBThreadUtils.postOnUiDelayed(new Runnable() {
            public void run() {
                if (mViewPager != null) {
                    int position = mViewPager.getCurrentItem();
                    int channelId = mPages.get(position);
                    refreshNewsByChannelId(channelId);
                }
            };
        }, 200);

        PandoraConfig.newInstance(mContext).saveLastShowUnreadNews(System.currentTimeMillis());

        mBackBtn.startAppearAnimator();

        requestWakeLock();

        // 1s后,提示用户开启夜间模式或自动切换到白昼模式
        HDBThreadUtils.postOnUiDelayed(new Runnable() {
            @Override
            public void run() {
                openNightModeTip();
            }
        }, 1000);

        LockScreenManager.getInstance().registBackPressedListener(mBackPressedListener);

        BottomDockUmengEventManager.statisticalNewsPanelExpanded();
    }

    //夜间模式提示
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
                if (hour >= 21 || hour <= 5) {
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

        // notifyDataSetChanged();

        // PandoraBoxManager.newInstance(mContext).resetDefaultPage();
        if (mBackBtn != null) {
            mBackBtn.setTranslationY(BaseInfoHelper.dip2px(mContext, 100));
        }

        if (mPages != null) {
            for (Integer i : mPages) {
                ChannelPageGenerator cpg = ChannelPageFactory.getPageGenerator(i);
                if (cpg != null) {
                    SwipeRefreshLayout srl = cpg.getRefreshView();
                    if (srl != null) {
                        srl.setRefreshing(false);
                    }
                }
            }
        }

        controlAutoScroll(false, false);

        LockScreenManager.getInstance().unRegistBackPressedListener(mBackPressedListener);

        releaseWakeLock();
        BottomDockUmengEventManager.statisticalNewsPanelCollapsed();
    }

    public void refreshCurrentNews() {
        if (mPages == null) 
            return;

        final int id = mPages.get(calCurrentItem());
        refreshNewsByChannelId(id);
    }

    public void refreshNewsByChannelId(int channelId) {
        
        ChannelPageGenerator cpg = ChannelPageFactory.getPageGenerator(channelId);
        if (cpg != null) {
            if (channelId == ChannelBoxManager.CHANNEL_WALLPAPER) {
                NewsFactory.updateWallpaper(cpg.getAdapter(), cpg.getWallpaperData(),
                        cpg.getRefreshView(), false, false);
                return;
            }
            NewsFactory.updateNews(channelId, cpg.getAdapter(), cpg.getData(),
                    cpg.getRefreshView(), false, false, cpg.getHeaderLoadingListener());
        }
    }

    /**
     * 立即拉取热门和八卦的数据，2秒后，加载微精选和美女的数据，4秒后加载搞笑的数据.
     */
    // public void refreshAllNews() {
    // NewsFactory.updateNews(NewsFactory.NEWS_TYPE_HEADLINE, mHotAdapter,
    // mHotNews,
    // mHotRefreshView, false, false, mLoadingListener);
    // NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, mGossipAdapter,
    // mGossipNews,
    // mGossipRefreshView, false, false, null);
    // HDBThreadUtils.postOnUiDelayed(new Runnable() {
    // @Override
    // public void run() {
    // NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE,
    // mMicroMediaAdapter,
    // mMicroMediaNews, mMicroMediaRefreshView, false, false,
    // mMicroLoadingListener);
    // NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, mBeautyAdapter,
    // mBeautyNews,
    // mBeautyRefreshView, false, false, null);
    // }
    // }, 2000);
    // HDBThreadUtils.postOnUiDelayed(new Runnable() {
    // @Override
    // public void run() {
    // NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, mJokeAdapter,
    // mJokeNews,
    // mJokeRefreshView, false, false, null);
    // }
    // }, 4000);
    // if (null != mWallpaperView) {
    // mWallpaperView.refreshData();
    // }
    // }
    
    
    //导航栏标题
    private List<String> mTabTitles = new ArrayList<String>();

    private void initNewsPages() {
        
        List<ChannelInfo> channels = ChannelBoxManager.getInstance(mContext).getSelectedChannels();

        mTabTitles.clear();
        for (ChannelInfo ci : channels) {
            mTabTitles.add(ChannelBoxManager.getInstance(mContext).getChannelNameById(
                    ci.getChannelId()));
        }

        
        mPages.clear();
        for (ChannelInfo ci : channels) {
            mPages.add(ci.getChannelId());
        }
    }

    public static void freeMemory() {
        ChannelPageFactory.forceRelease();
        mPbManager = null;
    }

    private void controlAutoScroll(boolean isHotAutoScroll, boolean isMicroAutoScroll) {
        // if (null != mMicroMediaAdapter) {
        // mMicroMediaAdapter.setAutoScroll(isMicroAutoScroll);
        // mMicroMediaAdapter.notifyDataSetChanged();
        // }
        // if (null != mHotAdapter) {
        // mHotAdapter.setAutoScroll(isHotAutoScroll);
        // mHotAdapter.notifyDataSetChanged();
        // }
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
            for (Integer i : mPages) {
                ChannelPageGenerator cpg = ChannelPageFactory.getPageGenerator(i);
                if (cpg != null) {
                    RecyclerView.Adapter<ViewHolder> adapter = cpg.getAdapter();
                    if (adapter != null) {
                        if (adapter instanceof BeautyPageAdapter) {
                            BeautyPageAdapter bpa = (BeautyPageAdapter) adapter;
                            bpa.setTheme(theme);
                        } else if (adapter instanceof StickRecyclerAdapter) {
                            StickRecyclerAdapter sra = (StickRecyclerAdapter) adapter;
                            sra.setTheme(theme);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    public boolean isDetailPageOpened() {
        return mDetailLayout.getChildCount() > 0;
    }

    public void openDetailPage(View view, boolean withAnimator) {
        mDetailLayout.removeAllViews();
        mDetailLayout.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mDetailLayout.bringToFront();
        mDetailLayout.setVisibility(View.VISIBLE);
        if (withAnimator) {
            mDetailLayout.setTranslationX(mDetailLayout.getWidth());
            mDetailLayout.animate().translationX(0).setDuration(300)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mDetailLayout.setVisibility(View.VISIBLE);
                    super.onAnimationEnd(animation);
                }
            }).start();
        } else {
            mDetailLayout.setTranslationX(0);
            mDetailLayout.setVisibility(View.VISIBLE);
        }
    }
    
    //关闭详情页
    public void closeDetailPage(boolean withAnimator) {
        View childView = mDetailLayout.getChildAt(0);
        final boolean channelPage = childView instanceof ChannelBoxView;
        if (withAnimator) {
            mDetailLayout.animate().translationX(mDetailLayout.getWidth()).setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (mDetailLayout != null) {
                                mDetailLayout.setVisibility(View.INVISIBLE);
                                mDetailLayout.removeAllViews();
                            }
                            if (channelPage) {
                                notifyDataSetChanged();
                            }
                        }
                    }).start();
        } else {
            mDetailLayout.setVisibility(View.INVISIBLE);
            mDetailLayout.removeAllViews();
            if (channelPage) {
                notifyDataSetChanged();
            }
        }
    }

    //viewPager适配器
    private static class NewsPagerAdapter extends PagerAdapter {
        private List<Integer> mPages;

        private List<String> mTitles;

        private PandoraBoxManager mBoxManager;

        public NewsPagerAdapter(PandoraBoxManager boxManager, List<Integer> pages,
                List<String> titles) {
            mBoxManager = boxManager;
            this.mPages = pages;
            this.mTitles = titles;
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
          
            final int channelId = mPages.get(position);
           
            final ChannelPageGenerator cpg = ChannelPageFactory.createPageGenerator(mBoxManager,
                    channelId);
            View view = cpg.getView();
            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public View getEntireView() {
        return mEntireView;
    }

    public View getHeaderView() {
        return mHeaderPart1;
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            LockScreenManager.getInstance().collapseNewsPanel();
            BottomDockUmengEventManager.statisticalNewsPanelBackClicked();
        }
    }

    public void onScreenOn() {
        HDBThreadUtils.runOnUi(new Runnable() {
            @Override
            public void run() {
                // startHeaderCircleAnimation();
//                renderNewsHeader();
            }
        });
    }

    private void renderNewsHeader() {
        if (mNewsHeaderTitle == null) {
            return;
        }
        final List<ServerImageData> headerData = ChannelPageFactory.getNewsHeaderData();
        if (headerData != null && headerData.size() > 0 && HDBNetworkState.isNetworkAvailable()) {
            final ServerImageData sid = headerData.get(0);
//            String name = ChannelBoxManager.getInstance(mContext).getChannelNameById(sid.getmType());
//            mNewsHeaderTitle.setText(name + ":" + sid.getTitle());
            mNewsHeaderTitle.setText(sid.getTitle());
            mHeaderHasNewsArea.setVisibility(View.VISIBLE);
            mHeaderNoNewsArea.setVisibility(View.GONE);
            setExpandedAction(new Runnable() {
                @Override
                public void run() {
                    openDetailPage(sid, false);
                    mHeaderHasNewsArea.setVisibility(View.GONE);
                    mHeaderNoNewsArea.setVisibility(View.VISIBLE);
                    setExpandedAction(null);
                }
            });
            headerData.remove(0);
        } else {
            setExpandedAction(null);
            if (mHeaderHasNewsArea != null) {
                mHeaderHasNewsArea.setVisibility(View.GONE);
                mHeaderNoNewsArea.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onScreenOff() {
        int theme = PandoraConfig.newInstance(mContext).isNightModeOn() ? NEWS_THEME_NIGHT
                : NEWS_THEME_DAY;
        switchNewsTheme(theme);

        HDBThreadUtils.runOnUi(new Runnable() {
            @Override
            public void run() {
                // startHeaderCircleAnimation();
                renderNewsHeader();
            }
        });
    }
}
