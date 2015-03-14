
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.adapter.BeautyPageAdapter;
import cn.zmdx.kaka.locker.content.adapter.GeneralNewsPageAdapter;
import cn.zmdx.kaka.locker.content.box.DefaultBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.content.view.NewsDetailLayout;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView.IOnlineWallpaperListener;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;
import cn.zmdx.kaka.locker.widget.PagerSlidingTabStrip;
import cn.zmdx.kaka.locker.widget.ViewPagerCompat;

public class PandoraBoxManager {

    private static PandoraBoxManager mPbManager;

    private Context mContext;

    private LayoutInflater mInflater;

    private View mHeaderView;

    private View mEntireView;

    private FrameLayout mDetailLayout;

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
        // TODO bind data
        return mHeaderView;
    }

    private boolean mInitBody = false;

    public void initBody() {
        if (mInitBody) {
            return;
        }
        mInitBody = true;
        final ViewPagerCompat viewPager = (ViewPagerCompat) mEntireView
                .findViewById(R.id.newsViewPager);
        List<View> pages = new ArrayList<View>();
        initNewsPages(pages);
        List<String> titles = new ArrayList<String>();
        initTitles(titles);
        NewsPagerAdapter pagerAdapter = new NewsPagerAdapter(pages, titles);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);

        final PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) mEntireView
                .findViewById(R.id.newsTabStrip);
        tabStrip.setViewPager(viewPager);
        tabStrip.setTabBgColors(new int[] {
                Color.parseColor("#26a69a"), Color.parseColor("#e84e40"),
                Color.parseColor("#ab47bc"), Color.parseColor("#8bc34a"),
                Color.parseColor("#ea861c"), Color.parseColor("#3db7ff")
        });
        tabStrip.setShouldExpand(false);
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

    private View initJokeView() {
        View view = mInflater.inflate(R.layout.pager_news_layout, null);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setVerticalFadingEdgeEnabled(true);
        rv.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        // sglm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        // sglm.offsetChildrenHorizontal(100);
        rv.setLayoutManager(sglm);
        rv.setHasFixedSize(true);

        final List<ServerImageData> news = new ArrayList<ServerImageData>();
        final BeautyPageAdapter adapter = new BeautyPageAdapter(mContext, news);

        adapter.setOnItemClickListener(new BeautyPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = news.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this);
                ndl.loadUrl(url);
                openDetailPage(ndl);
            }
        });
        rv.setAdapter(adapter);

        final SwipeRefreshLayout refreshView = (SwipeRefreshLayout) view
                .findViewById(R.id.refreshLayout);

        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, adapter, news, refreshView, false);

        refreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, adapter, news, refreshView,
                        false);
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
                    NewsFactory.updateNews(NewsFactory.NEWS_TYPE_JOKE, adapter, news, refreshView,
                            true);
                }
            }
        });
        return view;
    }

    private View initBeautyView() {
        View view = mInflater.inflate(R.layout.pager_news_layout, null);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setVerticalFadingEdgeEnabled(true);
        rv.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        sglm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        // sglm.offsetChildrenHorizontal(100);
        rv.setLayoutManager(sglm);
        rv.setHasFixedSize(true);

        final List<ServerImageData> news = new ArrayList<ServerImageData>();
        final BeautyPageAdapter adapter = new BeautyPageAdapter(mContext, news);

        adapter.setOnItemClickListener(new BeautyPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = news.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this);
                ndl.loadUrl(url);
                openDetailPage(ndl);
            }
        });
        rv.setAdapter(adapter);

        final SwipeRefreshLayout refreshView = (SwipeRefreshLayout) view
                .findViewById(R.id.refreshLayout);

        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, adapter, news, refreshView, false);

        refreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, adapter, news, refreshView,
                        false);
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
                    NewsFactory.updateNews(NewsFactory.NEWS_TYPE_BEAUTY, adapter, news,
                            refreshView, true);
                }
            }
        });
        return view;
    }

    private View initMicroMediaView() {
        View view = mInflater.inflate(R.layout.pager_news_layout, null);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setVerticalFadingEdgeEnabled(true);
        rv.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final LinearLayoutManager llm = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        final List<ServerImageData> news = new ArrayList<ServerImageData>();
        final GeneralNewsPageAdapter adapter = new GeneralNewsPageAdapter(mContext, news);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new GeneralNewsPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = news.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this);
                ndl.loadUrl(url);
                openDetailPage(ndl);
            }
        });

        final SwipeRefreshLayout refreshView = (SwipeRefreshLayout) view
                .findViewById(R.id.refreshLayout);
        refreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE, adapter, news,
                        refreshView, false);
            }
        });
        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE, adapter, news, refreshView,
                false);

        rv.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = llm.findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                // lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    NewsFactory.updateNews(NewsFactory.NEWS_TYPE_MICRO_CHOICE, adapter, news,
                            refreshView, true);
                }
            }
        });
        return view;
    }

    private View initGossipView() {
        View view = mInflater.inflate(R.layout.pager_news_layout, null);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setVerticalFadingEdgeEnabled(true);
        rv.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(sglm);
        rv.setHasFixedSize(true);

        final List<ServerImageData> news = new ArrayList<ServerImageData>();
        final BeautyPageAdapter adapter = new BeautyPageAdapter(mContext, news);

        adapter.setOnItemClickListener(new BeautyPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = news.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this);
                ndl.loadUrl(url);
                openDetailPage(ndl);
            }
        });
        rv.setAdapter(adapter);

        final SwipeRefreshLayout refreshView = (SwipeRefreshLayout) view
                .findViewById(R.id.refreshLayout);

        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, adapter, news, refreshView, false);

        refreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, adapter, news, refreshView,
                        false);
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
                    NewsFactory.updateNews(NewsFactory.NEWS_TYPE_GOSSIP, adapter, news,
                            refreshView, true);
                }
            }
        });
        return view;
    }

    private View initHotNewsView() {
        View view = mInflater.inflate(R.layout.pager_news_layout, null);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setVerticalFadingEdgeEnabled(true);
        rv.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final LinearLayoutManager llm = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        final List<ServerImageData> news = new ArrayList<ServerImageData>();
        final GeneralNewsPageAdapter adapter = new GeneralNewsPageAdapter(mContext, news);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new GeneralNewsPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = news.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(PandoraBoxManager.this);
                ndl.loadUrl(url);
                openDetailPage(ndl);
            }
        });

        final SwipeRefreshLayout refreshView = (SwipeRefreshLayout) view
                .findViewById(R.id.refreshLayout);
        refreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(NewsFactory.NEWS_TYPE_HEADLINE, adapter, news, refreshView,
                        false);
            }
        });
        NewsFactory.updateNews(NewsFactory.NEWS_TYPE_HEADLINE, adapter, news, refreshView, false);

        rv.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = llm.findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                // lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    NewsFactory.updateNews(NewsFactory.NEWS_TYPE_HEADLINE, adapter, news,
                            refreshView, true);
                }
            }
        });
        return view;
    }

    private View initWallPaperView() {
        OnlineWallpaperView view = new OnlineWallpaperView(mContext, true);
        view.setOnlineWallpaperListener(new IOnlineWallpaperListener() {

            @Override
            public void onOpenDetailPage(View view) {
                openDetailPage(view);
            }

            @Override
            public void onCloseDetailPage() {
                closeDetailPage();
            }

            @Override
            public void onGoToDetailClick(ServerOnlineWallpaper item) {

            }
        });
        return view;
    }

    public void openDetailPage(View view) {
        mDetailLayout.removeAllViews();
        mDetailLayout.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        view.setTranslationX(mDetailLayout.getWidth());
        mDetailLayout.bringChildToFront(view);
        mDetailLayout.setVisibility(View.VISIBLE);
        view.animate().translationX(0).setDuration(300).start();
    }

    public void closeDetailPage() {
        mDetailLayout.setVisibility(View.INVISIBLE);
        mDetailLayout.removeAllViews();
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

    public List<ServerImageData> cursorToList(Cursor cursor, List<ServerImageData> list) {
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    ServerImageData imgData = new ServerImageData();
                    imgData.setId(cursor.getInt(0));
                    imgData.setImageDesc(cursor.getString(1));
                    imgData.setTitle(cursor.getString(2));
                    imgData.setUrl(cursor.getString(3));
                    imgData.setCollectTime(cursor.getString(4));
                    imgData.setCollectWebsite(cursor.getString(5));
                    imgData.setReleaseTime(cursor.getString(6));
                    imgData.setCloudId(cursor.getString(7));
                    imgData.setDataType(cursor.getString(8));
                    list.add(imgData);
                }
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    public List<ServerImageData> getDataFormLocalDB(int count) {
        boolean containHtml = HDBNetworkState.isNetworkAvailable()
                && HDBNetworkState.isWifiNetwork();
        return ServerImageDataModel.getInstance().queryByRandom(count, containHtml);
    }

    public IPandoraBox getDefaultBox() {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("获得默认页面");
        }
        PandoraData pd = new PandoraData();
        pd.setmImage(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.pandora_box_default));
        pd.setDataType("DEFAULT_PAGE");
        return new DefaultBox(mContext, pd);
    }

    public View getEntireView() {
        return mEntireView;
    }
}
