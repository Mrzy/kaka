
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.LockScreenManager.OnBackPressedListener;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.adapter.BeautyPageAdapter;
import cn.zmdx.kaka.locker.content.adapter.GeneralNewsPageAdapter;
import cn.zmdx.kaka.locker.content.view.CircleSpiritButton;
import cn.zmdx.kaka.locker.content.view.NewsDetailLayout;
import cn.zmdx.kaka.locker.notification.view.NotificationListView;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView.IOnlineWallpaperListener;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;
import cn.zmdx.kaka.locker.weather.entity.MeteorologicalCodeConstant;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherFeatureIndexInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherFeatureInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherInfo;
import cn.zmdx.kaka.locker.weather.utils.SmartWeatherUtils;
import cn.zmdx.kaka.locker.weather.utils.XMLParserUtils;
import cn.zmdx.kaka.locker.widget.PagerSlidingTabStrip;
import cn.zmdx.kaka.locker.widget.PandoraRecyclerView;
import cn.zmdx.kaka.locker.widget.ViewPagerCompat;

public class PandoraBoxManager implements View.OnClickListener {

    private static PandoraBoxManager mPbManager;

    private Context mContext;

    private LayoutInflater mInflater;

    private View mHeaderView;

    private View mEntireView;

    private FrameLayout mDetailLayout;

    private CircleSpiritButton mBackBtn;

    private OnlineWallpaperView mWallpaperView;

//    private ImageView mNotifyTip;

    private ViewPagerCompat mViewPager;

    private static final int[] mTabColors = new int[] {
            Color.parseColor("#26a69a"), Color.parseColor("#e84e40"), Color.parseColor("#ab47bc"),
            Color.parseColor("#8bc34a"), Color.parseColor("#ea861c"), Color.parseColor("#3db7ff")
    };

    private static final int[] mFloatingButtonColors = new int[] {
            Color.parseColor("#8026a69a"), Color.parseColor("#80e84e40"),
            Color.parseColor("#80ab47bc"), Color.parseColor("#808bc34a"),
            Color.parseColor("#80ea861c"), Color.parseColor("#803db7ff")
    };

    private TextView tvLunarCalendar;

    private TextView tvWeatherFeature;

    private TextView tvWeatherCentTemp;

    private TextView tvUnreadNews;

    private TextView tvWeatherWind;

    private TextView tvWeatherWindForce;

    private TextView tvNoWeatherInfo;

    private int featureIndexPicResId;

    private String featureNameByNo;

    private String centTemp;

    private ImageView ivWeatherFeaturePic;

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

    public View initHeader() {
        mHeaderView = mEntireView.findViewById(R.id.header);
        tvLunarCalendar = (TextView) mHeaderView.findViewById(R.id.tvLunarCalendar);
        tvWeatherFeature = (TextView) mHeaderView.findViewById(R.id.tvWeatherFeature);
        tvWeatherCentTemp = (TextView) mHeaderView.findViewById(R.id.tvWeatherCentTemp);
        tvUnreadNews = (TextView) mHeaderView.findViewById(R.id.tvUnreadNews);
        tvWeatherWind = (TextView) mHeaderView.findViewById(R.id.tvWeatherWind);
        tvWeatherWindForce = (TextView) mHeaderView.findViewById(R.id.tvWeatherWindForce);
        tvNoWeatherInfo = (TextView) mHeaderView.findViewById(R.id.tvNoWeatherInfo);
        ivWeatherFeaturePic = (ImageView) mHeaderView.findViewById(R.id.ivWeatherFeaturePic);

        return mHeaderView;
    }

    @SuppressLint("NewApi")
    public void updateView(SmartWeatherInfo smartWeatherInfo) {
        if (smartWeatherInfo == null) {
            String lunarCal = SmartWeatherUtils.getLunarCal();
            if (tvLunarCalendar != null) {
                tvLunarCalendar.setText(lunarCal);
            }
            if ((tvNoWeatherInfo != null)) {
                tvNoWeatherInfo.setVisibility(View.VISIBLE);
            }
            return;
        }
        if ((tvNoWeatherInfo != null)) {
            tvNoWeatherInfo.setVisibility(View.GONE);
        }
        SmartWeatherFeatureInfo smartWeatherFeatureInfo = smartWeatherInfo
                .getSmartWeatherFeatureInfo();
        List<SmartWeatherFeatureIndexInfo> smartWeatherFeatureIndexInfoList = smartWeatherFeatureInfo
                .getSmartWeatherFeatureIndexInfoList();

        SmartWeatherFeatureIndexInfo smartWeatherFeatureIndexInfo = smartWeatherFeatureIndexInfoList
                .get(0);
        String daytimeWindForce = SmartWeatherUtils.getWindForceByNo(smartWeatherFeatureIndexInfo
                .getDaytimeWindForceNo());// 白天风力
        String nightWindForce = SmartWeatherUtils.getWindForceByNo(smartWeatherFeatureIndexInfo
                .getNightWindForceNo());// 夜间风力
        String daytimeWind = SmartWeatherUtils.getWindByNo(smartWeatherFeatureIndexInfo
                .getDaytimeWindNo());// 白天风向
        String nightWind = SmartWeatherUtils.getWindByNo(smartWeatherFeatureIndexInfo
                .getNightWindNo());// 夜间风向
        String forecastReleasedTime = smartWeatherFeatureInfo.getForecastReleasedTime();
        String sunriseAndSunset = smartWeatherFeatureIndexInfo.getSunriseAndSunset();
        String[] split = sunriseAndSunset.split("\\|");
        String sunset = split[1];
        boolean isNight = SmartWeatherUtils.isNight(forecastReleasedTime);
        boolean isSunsetTime = SmartWeatherUtils.isSunsetTime(sunset);
        if (isNight) {
            String nightFeatureNo = smartWeatherFeatureIndexInfo.getNightFeatureNo();
            centTemp = smartWeatherFeatureIndexInfo.getNightCentTemp();
            featureIndexPicResId = SmartWeatherUtils.getFeatureIndexPicByNo(nightFeatureNo);
            featureNameByNo = XMLParserUtils.getFeatureNameByNo(nightFeatureNo);
            if (featureNameByNo.equals(MeteorologicalCodeConstant.meterologicalNames[0])
                    && isSunsetTime) {
                featureIndexPicResId = MeteorologicalCodeConstant.meteorologicalCodePics[16];
            }
            if (tvWeatherWind != null) {
                tvWeatherWind.setText(nightWind == null ? "" : nightWind);
            }
            if (tvWeatherWindForce != null) {
                tvWeatherWindForce.setText(" " + (nightWindForce == null ? "" : nightWindForce));
            }
        } else {
            String daytimeFeatureNo = smartWeatherFeatureIndexInfo.getDaytimeFeatureNo();
            centTemp = smartWeatherFeatureIndexInfo.getDaytimeCentTemp();
            featureIndexPicResId = SmartWeatherUtils.getFeatureIndexPicByNo(daytimeFeatureNo);
            featureNameByNo = XMLParserUtils.getFeatureNameByNo(daytimeFeatureNo);
            if (tvWeatherWind != null) {
                tvWeatherWind.setText(daytimeWind == null ? "" : daytimeWind);
            }
            if (tvWeatherWindForce != null) {
                tvWeatherWindForce
                        .setText(" " + (daytimeWindForce == null ? "" : daytimeWindForce));
            }
        }
        if (tvWeatherFeature != null) {
            tvWeatherFeature.setText(featureNameByNo);
        }
        if (ivWeatherFeaturePic != null) {
            ivWeatherFeaturePic.setBackgroundResource(featureIndexPicResId);
        }
        if (tvWeatherCentTemp != null) {
            tvWeatherCentTemp.setText(centTemp + "℃");
        }
        String lunarCal = SmartWeatherUtils.getLunarCal();
        if (tvLunarCalendar != null) {
            tvLunarCalendar.setText(lunarCal);
        }
    }

    private boolean mInitBody = false;

    public void initBody() {
        if (mInitBody) {
            return;
        }
        mInitBody = true;
        mBackBtn = (CircleSpiritButton) mEntireView.findViewById(R.id.backBtn);
        mBackBtn.setColorNormal(mFloatingButtonColors[1]);
        mBackBtn.setColorPressed(mFloatingButtonColors[1]);
        mBackBtn.setOnClickListener(this);
        mBackBtn.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {

            @Override
            public void onViewAttachedToWindow(View v) {
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mNotifyReceiver);
                IntentFilter filter = new IntentFilter(NotificationListView.ACTION_NOTIFICATION_POSTED);
                LocalBroadcastManager.getInstance(mContext).registerReceiver(mNotifyReceiver, filter);
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
        List<View> pages = new ArrayList<View>();
        initNewsPages(pages);
        List<String> titles = new ArrayList<String>();
        initTitles(titles);
        NewsPagerAdapter pagerAdapter = new NewsPagerAdapter(pages, titles);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(1);

        final PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) mEntireView
                .findViewById(R.id.newsTabStrip);
        tabStrip.setViewPager(mViewPager);
        tabStrip.setTabBgColors(mTabColors);
        tabStrip.setShouldExpand(false);
        // tabStrip.setShouldSizeBigger(true);
        // tabStrip.setTextPressColor(Color.WHITE);
        tabStrip.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mBackBtn.setColorNormal(mFloatingButtonColors[position]);
                mBackBtn.setColorPressed(mFloatingButtonColors[position]);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        LockScreenManager.getInstance().registBackPressedListener(mBackPressedListener);
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
            mBackBtn.notifyNewFeed(bitmap);
        }
    };

    /**
     * 新闻面板完全展开时会调用该方法
     */
    public void notifyNewsPanelExpanded() {
        refreshAllNews();
        initBody();
        mBackBtn.startAppearAnimator();
    }

    public void notifyNewsPanelCollapsed() {
        PandoraBoxManager.newInstance(mContext).closeDetailPage(false);
        PandoraBoxManager.newInstance(mContext).resetDefaultPage();
        if (mBackBtn != null) {
            mBackBtn.setTranslationY(BaseInfoHelper.dip2px(mContext, 100));
        }
    }

    public void refreshNewsByCategory(int category) {
        if (category == NewsFactory.NEWS_TYPE_HEADLINE) {
            NewsFactory.updateNews(category, mHotAdapter, mHotNews, mHotRefreshView, false);
        } else if (category == NewsFactory.NEWS_TYPE_GOSSIP) {
            NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, mGossipAdapter, mGossipNews,
                    mGossipRefreshView, false);
        } else if (category == NewsFactory.NEWS_TYPE_MICRO_CHOICE) {
            NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE, mMicroMediaAdapter,
                    mMicroMediaNews, mMicroMediaRefreshView, false);
        } else if (category == NewsFactory.NEWS_TYPE_BEAUTY) {
            NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, mBeautyAdapter, mBeautyNews,
                    mBeautyRefreshView, false);
        } else if (category == NewsFactory.NEWS_TYPE_JOKE) {
            NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, mJokeAdapter, mJokeNews,
                    mJokeRefreshView, false);
        } else {
            throw new IllegalArgumentException("invalid news category");
        }
    }

    /**
     * 立即拉取热门和八卦的数据，2秒后，加载微精选和美女的数据，4秒后加载搞笑的数据.
     */
    public void refreshAllNews() {
        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_HEADLINE, mHotAdapter, mHotNews,
                mHotRefreshView, false);
        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, mGossipAdapter, mGossipNews,
                mGossipRefreshView, false);
        HDBThreadUtils.postOnUiDelayed(new Runnable() {
            @Override
            public void run() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE, mMicroMediaAdapter,
                        mMicroMediaNews, mMicroMediaRefreshView, false);
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, mBeautyAdapter, mBeautyNews,
                        mBeautyRefreshView, false);
            }
        }, 2000);
        HDBThreadUtils.postOnUiDelayed(new Runnable() {
            @Override
            public void run() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, mJokeAdapter, mJokeNews,
                        mJokeRefreshView, false);
            }
        }, 4000);
        if (null != mWallpaperView) {
            mWallpaperView.pullWallpaperData();
        }
    }

    private void initTitles(List<String> titles) {
        titles.add(mContext.getResources().getString(R.string.pandora_news_classify_wallpaper));
        titles.add(mContext.getResources().getString(R.string.pandora_news_classify_headlines));
        titles.add(mContext.getResources().getString(R.string.pandora_news_classify_gossip));
        titles.add(mContext.getResources().getString(R.string.pandora_news_classify_micro_choice));
        titles.add(mContext.getResources().getString(R.string.pandora_news_classify_beauty));
        titles.add(mContext.getResources().getString(R.string.pandora_news_classify_funny));
    }

    private void initNewsPages(List<View> data) {
        View wallPaperView = initWallPaperView();
        View hotNewsView = initHotNewsView();
        View gossipView = initGossipView();
        View microMediaView = initMicroMediaView();
        View beautyView = initBeautyView();
        View jokeView = initJokeView();
        data.clear();
        data.add(wallPaperView);
        data.add(hotNewsView);
        data.add(gossipView);
        data.add(microMediaView);
        data.add(beautyView);
        data.add(jokeView);
    }

    private List<ServerImageData> mHotNews = new ArrayList<ServerImageData>();

    private List<ServerImageData> mGossipNews = new ArrayList<ServerImageData>();

    private List<ServerImageData> mMicroMediaNews = new ArrayList<ServerImageData>();

    private List<ServerImageData> mBeautyNews = new ArrayList<ServerImageData>();

    private List<ServerImageData> mJokeNews = new ArrayList<ServerImageData>();

    private BeautyPageAdapter mBeautyAdapter, mJokeAdapter, mGossipAdapter;

    private GeneralNewsPageAdapter mMicroMediaAdapter, mHotAdapter;

    private SwipeRefreshLayout mJokeRefreshView, mBeautyRefreshView, mMicroMediaRefreshView,
            mGossipRefreshView, mHotRefreshView;

    private View createEmptyView() {
        TextView view = new TextView(mContext);
        view.setGravity(Gravity.CENTER);
        view.setTextColor(Color.parseColor("#a0000000"));
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
        // sglm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
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
            }
        });
        rv.setAdapter(mJokeAdapter);

        View emptyView = createEmptyView();
        rv.setEmptyView(emptyView);
        view.addView(emptyView);

        mJokeRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mJokeRefreshView.setProgressBackgroundColorSchemeColor(mFloatingButtonColors[5]);

        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, mJokeAdapter, mJokeNews,
                mJokeRefreshView, true);

        mJokeRefreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, mJokeAdapter, mJokeNews,
                        mJokeRefreshView, false);
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
                            mJokeRefreshView, true);
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
            }
        });
        rv.setAdapter(mBeautyAdapter);

        View emptyView = createEmptyView();
        rv.setEmptyView(emptyView);
        view.addView(emptyView);

        mBeautyRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mBeautyRefreshView.setProgressBackgroundColorSchemeColor(mFloatingButtonColors[4]);

        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, mBeautyAdapter, mBeautyNews,
                mBeautyRefreshView, true);

        mBeautyRefreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, mBeautyAdapter, mBeautyNews,
                        mBeautyRefreshView, false);
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
                            mBeautyNews, mBeautyRefreshView, true);
                }
            }
        });
        return view;
    }

    private View initMicroMediaView() {
        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.pager_news_layout, null);
        PandoraRecyclerView rv = (PandoraRecyclerView) view.findViewById(R.id.recyclerView);
        rv.setEmptyView(createEmptyView());
        rv.setVerticalFadingEdgeEnabled(true);
        rv.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final LinearLayoutManager llm = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        // final List<ServerImageData> news = new ArrayList<ServerImageData>();
        mMicroMediaAdapter = new GeneralNewsPageAdapter(mContext, mMicroMediaNews);
        rv.setAdapter(mMicroMediaAdapter);

        View emptyView = createEmptyView();
        rv.setEmptyView(emptyView);
        view.addView(emptyView);

        mMicroMediaAdapter.setOnItemClickListener(new GeneralNewsPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = mMicroMediaNews.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this, sid);
                openDetailPage(ndl);
            }
        });

        mMicroMediaRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mMicroMediaRefreshView.setProgressBackgroundColorSchemeColor(mFloatingButtonColors[3]);

        mMicroMediaRefreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE, mMicroMediaAdapter,
                        mMicroMediaNews, mMicroMediaRefreshView, false);
            }
        });
        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE, mMicroMediaAdapter,
                mMicroMediaNews, mMicroMediaRefreshView, true);

        rv.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = llm.findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                // lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE, mMicroMediaAdapter,
                            mMicroMediaNews, mMicroMediaRefreshView, true);
                }
            }
        });
        return view;
    }

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
            }
        });
        rv.setAdapter(mGossipAdapter);

        View emptyView = createEmptyView();
        rv.setEmptyView(emptyView);
        view.addView(emptyView);

        mGossipRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mGossipRefreshView.setProgressBackgroundColorSchemeColor(mFloatingButtonColors[2]);

        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, mGossipAdapter, mGossipNews,
                mGossipRefreshView, true);

        mGossipRefreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, mGossipAdapter, mGossipNews,
                        mGossipRefreshView, false);
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
                            mGossipNews, mGossipRefreshView, true);
                }
            }
        });
        return view;
    }

    private View initHotNewsView() {
        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.pager_news_layout, null);
        PandoraRecyclerView rv = (PandoraRecyclerView) view.findViewById(R.id.recyclerView);
        rv.setVerticalFadingEdgeEnabled(true);
        rv.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final LinearLayoutManager llm = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        // final List<ServerImageData> news = new ArrayList<ServerImageData>();
        mHotAdapter = new GeneralNewsPageAdapter(mContext, mHotNews);
        rv.setAdapter(mHotAdapter);

        View emptyView = createEmptyView();
        rv.setEmptyView(emptyView);
        view.addView(emptyView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        mHotAdapter.setOnItemClickListener(new GeneralNewsPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = mHotNews.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this, sid);
                openDetailPage(ndl);
            }
        });

        mHotRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mHotRefreshView.setProgressBackgroundColorSchemeColor(mFloatingButtonColors[1]);
        mHotRefreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_HEADLINE, mHotAdapter, mHotNews,
                        mHotRefreshView, false);
            }
        });
        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_HEADLINE, mHotAdapter, mHotNews,
                mHotRefreshView, true);

        rv.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = llm.findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                // lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    NewsFactory.updateNews(NewsFactory.NEWS_TYPE_HEADLINE, mHotAdapter, mHotNews,
                            mHotRefreshView, true);
                }
            }
        });
        return view;
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
                            mDetailLayout.setVisibility(View.INVISIBLE);
                            mDetailLayout.removeAllViews();
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
}
