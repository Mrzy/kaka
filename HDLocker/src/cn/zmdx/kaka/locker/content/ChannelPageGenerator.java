
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.NewsFactory.IOnLoadingListener;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.adapter.BeautyPageAdapter;
import cn.zmdx.kaka.locker.content.adapter.StickRecyclerAdapter;
import cn.zmdx.kaka.locker.content.adapter.StickRecyclerAdapter.OnStickClickListener;
import cn.zmdx.kaka.locker.content.view.NewsDetailLayout;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView.IOnlineWallpaperListener;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;
import cn.zmdx.kaka.locker.widget.PandoraRecyclerView;
import cn.zmdx.kaka.locker.widget.PandoraSwipeRefreshLayout;

public class ChannelPageGenerator {

    public static final int NEWS_THEME_LIST = 1;// 列表式新闻

    public static final int NEWS_THEME_STAGGERED = 2;// 交错瀑布流式新闻

    public static final int NEWS_THEME_WALLPAPER = 3;// 壁纸样式

    private int mChannelId;

    private int mStyle;

    /**
     * 主题色
     */
    private int mColor;

    private RecyclerView.Adapter<ViewHolder> mAdapter;

    private PandoraSwipeRefreshLayout mRefreshView;

    private PandoraRecyclerView mRecyclerView;

    private LayoutInflater mInflater;

    private Context mContext;

    private PandoraBoxManager mBoxManager;

    private List<ServerImageData> mNewsData = new ArrayList<ServerImageData>();

    private List<ServerImageData> mStickData = new ArrayList<ServerImageData>();

    private View mPageView;

    private IOnLoadingListener mLoadingListener;

    public IOnLoadingListener getHeaderLoadingListener() {
        return mLoadingListener;
    }

    public ChannelPageGenerator(PandoraBoxManager boxManager, int channelId, int style, int color) {
        if (style != NEWS_THEME_LIST && style != NEWS_THEME_STAGGERED && style != NEWS_THEME_WALLPAPER) {
            throw new IllegalArgumentException("error theme");
        }

        mBoxManager = boxManager;
        mContext = HDApplication.getContext();
        mInflater = LayoutInflater.from(mContext);
        mChannelId = channelId;
        mStyle = style;
        mColor = color;
        initView();
    }

    private void initView() {
        if (mStyle == NEWS_THEME_LIST) {
            mPageView = initListThemeView();
        } else if (mStyle == NEWS_THEME_STAGGERED) {
            mPageView = initStaggeredThemeView();
        } else if (mStyle == NEWS_THEME_WALLPAPER) {
            mPageView = initWallpaperView();
        }
    }

    public View getView() {
        return mPageView;
    }

    private View initWallpaperView() {
        OnlineWallpaperView mWallpaperView = new OnlineWallpaperView(mContext, true);
        mWallpaperView.setOnlineWallpaperListener(new IOnlineWallpaperListener() {

            @Override
            public void onOpenDetailPage(View view) {
                mBoxManager.openDetailPage(view);
            }

            @Override
            public void onCloseDetailPage(boolean withAnimator) {
                mBoxManager.closeDetailPage(withAnimator);
            }

            @Override
            public void onGoToDetailClick(ServerOnlineWallpaper item) {

            }
        });
        return mWallpaperView;
    }
    private View initStaggeredThemeView() {
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

        mAdapter = new BeautyPageAdapter(mContext, mNewsData);

        BeautyPageAdapter adapetr = (BeautyPageAdapter) mAdapter;
        adapetr.setOnItemClickListener(new BeautyPageAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = mNewsData.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(mBoxManager, sid);
                mBoxManager.openDetailPage(ndl);
                UmengCustomEventManager.statisticalOpenNewsDetail(sid.getId(), mChannelId+"");
            }
        });
        rv.setAdapter(mAdapter);

        View emptyView = createEmptyView();
        rv.setEmptyView(emptyView);
        view.addView(emptyView);

        mRefreshView = (PandoraSwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mRefreshView.setProgressBackgroundColorSchemeColor(mColor);
        mRefreshView.setColorSchemeColors(Color.WHITE);

        mRefreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(mChannelId, mAdapter, mNewsData,
                        mRefreshView, false, true, null);
                UmengCustomEventManager.statisticalPullRefreshNews(mChannelId + "");
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
                    NewsFactory.updateNews(mChannelId, mAdapter,
                            mNewsData, mRefreshView, true, false, null);
                }
            }
        });
        return view;
    }

    private View initListThemeView() {
        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.pager_news_layout, null);
        mRecyclerView = (PandoraRecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setVerticalFadingEdgeEnabled(true);
        mRecyclerView.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        final LinearLayoutManager llm = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        // final List<ServerImageData> news = new
        // ArrayList<ServerImageData>();
        mAdapter = new StickRecyclerAdapter(mContext, mNewsData, mStickData);
        mRecyclerView.setAdapter(mAdapter);

        View emptyView = createEmptyView();
        mRecyclerView.setEmptyView(emptyView);
        view.addView(emptyView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        StickRecyclerAdapter adapter = (StickRecyclerAdapter) mAdapter;

        adapter.setOnItemClickListener(new StickRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClicked(View view, int position) {
                final ServerImageData sid = mNewsData.get(position);
                String url = sid.getImageDesc();
                NewsDetailLayout ndl = new NewsDetailLayout(mBoxManager, sid);
                mBoxManager.openDetailPage(ndl);
                UmengCustomEventManager.statisticalOpenNewsDetail(sid.getId(), mChannelId + "");
            }
        });

        adapter.setOnStickClickListener(new OnStickClickListener() {

            @Override
            public void onItemClicked(ServerImageData serverImageData) {
                NewsDetailLayout ndl = new NewsDetailLayout(mBoxManager, serverImageData);
                mBoxManager.openDetailPage(ndl);
                UmengCustomEventManager.statisticalOpenNewsDetail(serverImageData.getId(),
                        mChannelId + "");
            }
        });

        mLoadingListener = new IOnLoadingListener() {

            @Override
            public void onLoaded(List<ServerImageData> stickData) {
                mStickData.clear();
                mStickData.addAll(stickData);
                mAdapter.notifyDataSetChanged();
            }
        };
        mRefreshView = (PandoraSwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mRefreshView.setProgressBackgroundColorSchemeColor(mColor);
        mRefreshView.setColorSchemeColors(Color.WHITE);
        mRefreshView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewsFactory.updateNews(mChannelId, mAdapter, mNewsData, mRefreshView, false,
                        true, mLoadingListener);
                UmengCustomEventManager.statisticalPullRefreshNews(mChannelId + "");
            }

        });
        return view;
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

    public RecyclerView.Adapter<ViewHolder> getAdapter() {
        return mAdapter;
    }

    public PandoraSwipeRefreshLayout getRefreshView() {
        return mRefreshView;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public int getChannelId() {
        return mChannelId;
    }

    public int getStyle() {
        return mStyle;
    }

    public List<ServerImageData> getData() {
        return mNewsData;
    }
}
