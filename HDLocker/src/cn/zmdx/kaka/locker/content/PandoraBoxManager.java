
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.adapter.NewsPageAdapter;
import cn.zmdx.kaka.locker.content.box.DefaultBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.content.view.NewsDetailLayout;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView.IOnlineWallpaperListener;
import cn.zmdx.kaka.locker.widget.PagerSlidingTabStrip;
import cn.zmdx.kaka.locker.widget.ViewPagerCompat;

public class PandoraBoxManager {

    private static PandoraBoxManager mPbManager;

    private Context mContext;

    private LayoutInflater mInflater;

    private View mHeaderView;

    private View mEntireView;

    private NewsDetailLayout mDetailLayout;

    private PandoraBoxManager(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mEntireView = mInflater.inflate(R.layout.news_page_layout, null);
        mDetailLayout = (NewsDetailLayout) mEntireView.findViewById(R.id.detailLayout);
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

        final PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) mEntireView
                .findViewById(R.id.newsTabStrip);
        tabStrip.setViewPager(viewPager);
        tabStrip.setShouldExpand(true);
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
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv.setHasFixedSize(true);
        List<String> news = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            news.add("今日头条" + i);
        }
        NewsPageAdapter adapter = new NewsPageAdapter(news);
        rv.setAdapter(adapter);

        final SwipeRefreshLayout refreshView = (SwipeRefreshLayout) view
                .findViewById(R.id.refreshLayout);
        refreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshView.setRefreshing(true);
                HDBThreadUtils.postOnUiDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refreshView.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        return view;
    }

    private View initBeautyView() {
        View view = mInflater.inflate(R.layout.pager_news_layout, null);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv.setHasFixedSize(true);
        List<String> news = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            news.add("今日头条" + i);
        }
        NewsPageAdapter adapter = new NewsPageAdapter(news);
        rv.setAdapter(adapter);

        final SwipeRefreshLayout refreshView = (SwipeRefreshLayout) view
                .findViewById(R.id.refreshLayout);
        refreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshView.setRefreshing(true);
                HDBThreadUtils.postOnUiDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refreshView.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        return view;
    }

    private View initMicroMediaView() {
        View view = mInflater.inflate(R.layout.pager_news_layout, null);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv.setHasFixedSize(true);
        List<String> news = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            news.add("今日头条" + i);
        }
        NewsPageAdapter adapter = new NewsPageAdapter(news);
        rv.setAdapter(adapter);

        final SwipeRefreshLayout refreshView = (SwipeRefreshLayout) view
                .findViewById(R.id.refreshLayout);
        refreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshView.setRefreshing(true);
                HDBThreadUtils.postOnUiDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refreshView.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        return view;
    }

    private View initGossipView() {
        View view = mInflater.inflate(R.layout.pager_news_layout, null);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv.setHasFixedSize(true);
        List<String> news = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            news.add("今日头条" + i);
        }
        NewsPageAdapter adapter = new NewsPageAdapter(news);
        rv.setAdapter(adapter);

        final SwipeRefreshLayout refreshView = (SwipeRefreshLayout) view
                .findViewById(R.id.refreshLayout);
        refreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshView.setRefreshing(true);
                HDBThreadUtils.postOnUiDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refreshView.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        return view;
    }

    private View initHotNewsView() {
        View view = mInflater.inflate(R.layout.pager_news_layout, null);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv.setHasFixedSize(true);
        List<String> news = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            news.add("今日头条" + i);
        }
        NewsPageAdapter adapter = new NewsPageAdapter(news);
        rv.setAdapter(adapter);

        final SwipeRefreshLayout refreshView = (SwipeRefreshLayout) view
                .findViewById(R.id.refreshLayout);
        refreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshView.setRefreshing(true);
                HDBThreadUtils.postOnUiDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refreshView.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        return view;
    }

    private View initWallPaperView() {
        OnlineWallpaperView view = new OnlineWallpaperView(mContext);
        view.setOnlineWallpaperListener(new IOnlineWallpaperListener() {

            @Override
            public void onOpenDetailPage(View view) {
                openDetailPage(view);
            }

            @Override
            public void onCloseDetailPage() {
                closeDetailPage();
            }
        });
        return view;
    }

    private void openDetailPage(View view) {
        mDetailLayout.addDetailView(view);
        mDetailLayout.open();
    }

    private void closeDetailPage() {
        mDetailLayout.close();
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
