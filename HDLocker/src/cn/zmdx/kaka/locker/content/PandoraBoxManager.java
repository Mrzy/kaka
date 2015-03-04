
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.adapter.WallpaperPageAdapter;
import cn.zmdx.kaka.locker.content.box.DefaultBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.content.view.FlipperView;
import cn.zmdx.kaka.locker.content.view.NewsDetailLayout;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.widget.PagerSlidingTabStrip;
import cn.zmdx.kaka.locker.widget.ViewPagerCompat;

public class PandoraBoxManager {

    private static PandoraBoxManager mPbManager;

    private Context mContext;

    private LayoutInflater mInflater;

    private FlipperView mHeaderView;

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
        mHeaderView = (FlipperView) mEntireView.findViewById(R.id.header);
        mHeaderView.closeUpperView(false);
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

    public void openHotHeader() {
        if (mHeaderView != null) {
            mHeaderView.openUpperView(true);
        }
    }

    public void closeHotHeader() {
        if (mHeaderView != null) {
            mHeaderView.closeUpperView(true);
        }
    }

    private void initTitles(List<String> titles) {
        titles.add("壁纸");
        titles.add("热门");
        titles.add("社会");
        titles.add("科技");
        titles.add("美女");
        titles.add("搞笑");
    }

    private void initNewsPages(List<View> data) {
        View wallPaperView = initWallPaperView();
        View hotNewsView = initHotNewsView();
        View girlView = initGirlView();
        View techView = initTechView();
        View sociView = initSociView();
        View jokeView = initJokeView();
        data.clear();
        data.add(wallPaperView);
        data.add(hotNewsView);
        data.add(girlView);
        data.add(techView);
        data.add(sociView);
        data.add(jokeView);
    }

    private View initJokeView() {
        RecyclerView rv = (RecyclerView) mInflater.inflate(R.layout.pager_news_layout, null);
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv.setHasFixedSize(true);
        List<String> news = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            news.add("今日头条" + i);
        }
        NewsPageAdapter adapter = new NewsPageAdapter(news);
        rv.setAdapter(adapter);
        return rv;
    }

    private View initSociView() {
        RecyclerView rv = (RecyclerView) mInflater.inflate(R.layout.pager_news_layout, null);
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv.setHasFixedSize(true);
        List<String> news = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            news.add("今日头条" + i);
        }
        NewsPageAdapter adapter = new NewsPageAdapter(news);
        rv.setAdapter(adapter);
        return rv;
    }

    private View initTechView() {
        RecyclerView rv = (RecyclerView) mInflater.inflate(R.layout.pager_news_layout, null);
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv.setHasFixedSize(true);
        List<String> news = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            news.add("今日头条" + i);
        }
        NewsPageAdapter adapter = new NewsPageAdapter(news);
        rv.setAdapter(adapter);
        return rv;
    }

    private View initGirlView() {
        RecyclerView rv = (RecyclerView) mInflater.inflate(R.layout.pager_news_layout, null);
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        // rv.setHasFixedSize(true);
        List<String> news = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            news.add("今日头条" + i);
        }
        NewsPageAdapter adapter = new NewsPageAdapter(news);
        rv.setAdapter(adapter);
        return rv;
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
        return view;
    }

    private View initWallPaperView() {
        RecyclerView rv = (RecyclerView) mInflater.inflate(R.layout.pager_news_layout, null);
        rv.setHasFixedSize(true);
        final List<String> news = new ArrayList<String>();
        news.add("http://e.hiphotos.baidu.com/image/w%3D400/sign=fe5ab378b1de9c82a665f88f5c8180d2/9345d688d43f8794c8c01f6fd01b0ef41bd53ab7.jpg");
        news.add("http://a.hiphotos.baidu.com/image/w%3D400/sign=78c33218357adab43dd01a43bbd5b36b/3c6d55fbb2fb4316edf8fb4a23a4462309f7d31f.jpg");
        news.add("http://a.hiphotos.baidu.com/image/w%3D400/sign=079be1e0b68f8c54e3d3c42f0a282dee/d0c8a786c9177f3ecfc3db0372cf3bc79e3d56cc.jpg");
        news.add("http://c.hiphotos.baidu.com/image/w%3D400/sign=e075723e718da9774e2f872b8050f872/f603918fa0ec08fafbd8a2aa5bee3d6d54fbdae7.jpg");
        news.add("http://e.hiphotos.baidu.com/image/w%3D400/sign=a60dc1920a24ab18e016e03705fbe69a/f703738da97739124770a1d5fb198618367ae234.jpg");
        news.add("http://a.hiphotos.baidu.com/image/w%3D400/sign=b40ee8fd5ddf8db1bc2e7d643922dddb/bba1cd11728b4710517c14a9c0cec3fdfc032300.jpg");
        news.add("http://c.hiphotos.baidu.com/image/w%3D400/sign=df63aede4410b912bfc1f7fef3fcfcb5/72f082025aafa40f598927b8a864034f78f01903.jpg");
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        WallpaperPageAdapter adapter = new WallpaperPageAdapter(mContext, rv, news);
        adapter.setOnItemClickListener(new WallpaperPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                String url = news.get(position);
                TextView detailView = new TextView(mContext);
                detailView.setBackgroundColor(Color.WHITE);
                detailView.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        closeDetailPage();
                    }
                });
                detailView.setText("详情页");
                openDetailPage(detailView);
            }
        });
        rv.setAdapter(adapter);
        return rv;
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

    private static class NewsPageAdapter extends RecyclerView.Adapter<NewsPageAdapter.ViewHolder> {

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mTextView = (TextView) view;
            }
        }

        private List<String> mNews;

        public NewsPageAdapter(List<String> news) {
            mNews = news;
        }

        public String getValueAt(int position) {
            return mNews.get(position);
        }

        @Override
        public int getItemCount() {
            // TODO Auto-generated method stub
            return mNews.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.mTextView.setText(mNews.get(position));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
            TextView tv = new TextView(arg0.getContext());
            ViewHolder vh = new ViewHolder(tv);
            return vh;
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
