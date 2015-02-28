
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.StickyLayout.OnGiveUpTouchEventListener;
import cn.zmdx.kaka.locker.content.box.DefaultBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.widget.PagerSlidingTabStrip;
import cn.zmdx.kaka.locker.widget.ViewPagerCompat;

public class PandoraBoxManager {

    private static PandoraBoxManager mPbManager;

    private Context mContext;

    private LayoutInflater mInflater;

    private PandoraBoxManager(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public synchronized static PandoraBoxManager newInstance(Context context) {
        if (mPbManager == null) {
            mPbManager = new PandoraBoxManager(context);
        }
        return mPbManager;
    }

    public View getNewsPage() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_page_layout, null);
        final ViewPagerCompat viewPager = (ViewPagerCompat) view.findViewById(R.id.newsViewPager);
        List<View> pages = new ArrayList<View>();
        initNewsPages(pages);
        List<String> titles = new ArrayList<String>();
        initTitles(titles);
        NewsPagerAdapter pagerAdapter = new NewsPagerAdapter(pages, titles);
        viewPager.setAdapter(pagerAdapter);

        final PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) view
                .findViewById(R.id.newsTabStrip);
        tabStrip.setViewPager(viewPager);
        tabStrip.setShouldExpand(true);

        final StickyLayout sl = (StickyLayout) view.findViewById(R.id.stickyLayout);
        sl.setOnGiveUpTouchEventListener(new OnGiveUpTouchEventListener() {
            @Override
            public boolean giveUpTouchEvent(MotionEvent event) {
                if (tabStrip.getTop() >= 0) {
                    final NewsPagerAdapter pagerAdapter = (NewsPagerAdapter) viewPager.getAdapter();
                    int curPosi = viewPager.getCurrentItem();
                    final ViewGroup curPage = (ViewGroup) pagerAdapter.getItem(curPosi);
                    int position = -1;
                    if (curPage != null && curPage instanceof RecyclerView) {
                        final RecyclerView rv = (RecyclerView) curPage;
                        final LayoutManager lm = rv.getLayoutManager();
                        if (lm instanceof LinearLayoutManager || lm instanceof GridLayoutManager) {
                            final LinearLayoutManager llm = (LinearLayoutManager) lm;
                            position = llm.findFirstVisibleItemPosition();
                        }
                    }
                    if (position == 0) {
                        return true;
                    }
                }
                return false;
            }
        });
        return view;
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
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
        // rv.setHasFixedSize(true);
        List<String> news = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            news.add("今日头条" + i);
        }
        NewsPageAdapter adapter = new NewsPageAdapter(news);
        rv.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(mContext, 3);
        glm.setSpanSizeLookup(new MyGridSpanSizeLookup(adapter, glm));
        rv.setLayoutManager(glm);
        return rv;
    }

    private static class MyGridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        private NewsPageAdapter mAdapter;

        private GridLayoutManager mLayoutManager;

        public MyGridSpanSizeLookup(NewsPageAdapter adapter, GridLayoutManager glm) {
            mAdapter = adapter;
            mLayoutManager = glm;
        }

        @Override
        public int getSpanSize(int position) {
            String item = mAdapter.getValueAt(position);
            return 1 + (Math.abs(item.hashCode()) % mLayoutManager.getSpanCount());
        }
    };

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

    // public IFoldablePage getFoldablePage() {
    // List<ServerImageData> data =
    // getDataFormLocalDB(PandoraPolicy.MIN_COUNT_FOLDABLE_BOX);
    // if (BuildConfig.DEBUG) {
    // HDBLOG.logD("从本地取出数据条数：" + data.size());
    // }
    // FoldablePage box = new FoldablePage(mContext, data);
    // FoldableBoxAdapter adapter = new FoldableBoxAdapter(mContext,
    // box.makeCardList(data));
    // box.setAdapter(adapter);
    // return box;
    // }

    // public IFoldablePage getFavoriteFoldablePage() {
    // FavoritesManager manager = new FavoritesManager(mContext);
    // List<ServerImageData> listData = new ArrayList<ServerImageData>();
    // Cursor cursor = manager.getFavoritesInfo();
    // if (cursor == null) {
    // return null;
    // }
    // List<ServerImageData> cursorToList = cursorToList(cursor, listData);
    // if (BuildConfig.DEBUG) {
    // HDBLOG.logD("从本地取出收藏条数：" + cursorToList.size());
    // }
    // FoldablePage foldablePage = null;
    // if (null != cursorToList) {
    // foldablePage = new FoldablePage(mContext, cursorToList);
    // FoldableBoxAdapter adapter = new
    // FoldableBoxAdapter(HDApplication.getContext(),
    // foldablePage.makeCardList(cursorToList));
    // foldablePage.setAdapter(adapter);
    // }
    // return foldablePage;
    // }

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
}
