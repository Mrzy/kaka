
package cn.zmdx.kaka.locker.wallpaper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.adapter.WallpaperPageAdapter;
import cn.zmdx.kaka.locker.content.adapter.WallpaperPageAdapter.OnItemClickListener;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperManager.IPullWallpaperListener;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;
import cn.zmdx.kaka.locker.wallpaper.WallpaperDetailView.IWallpaperDetailListener;

public class OnlineWallpaperView extends LinearLayout implements IPullWallpaperListener,
        OnItemClickListener {
    private Context mContext;

    private View mEntireView;

    private RecyclerView mRecyclerView;

    private View mErrorView;

    private LinearLayoutManager mLayoutManager;

    private WallpaperPageAdapter mAdapter;

    private List<ServerOnlineWallpaper> mList = new ArrayList<ServerOnlineWallpaperManager.ServerOnlineWallpaper>();

    private boolean isLockScreen;

    private boolean isNetWorkError = false;

    private IOnlineWallpaperListener mListener;

    public interface IOnlineWallpaperListener {
        void onCloseDetailPage(boolean withAnimator);

        void onOpenDetailPage(View view);

        void onGoToDetailClick(ServerOnlineWallpaper item);
    }

    public OnlineWallpaperView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    public OnlineWallpaperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public OnlineWallpaperView(Context context, boolean isScreen) {
        super(context);
        mContext = context;
        isLockScreen = isScreen;
        initView();
    }

    @SuppressLint("InflateParams")
    private void initView() {
        mEntireView = LayoutInflater.from(mContext).inflate(R.layout.pager_wallpaper_layout, null);
        addView(mEntireView);
        mErrorView = mEntireView.findViewById(R.id.error_view);
        mRecyclerView = (RecyclerView) mEntireView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setVerticalFadingEdgeEnabled(true);
        mRecyclerView.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOnScrollListener(mScrollListener);
        mAdapter = new WallpaperPageAdapter(mContext, mRecyclerView, mList);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setOnlineWallpaperListener(IOnlineWallpaperListener listener) {
        mListener = listener;
        pullWallpaperData();
    }

    private boolean isLoadMore = false;

    private OnScrollListener mScrollListener = new OnScrollListener() {
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItem = ((LinearLayoutManager) mLayoutManager)
                    .findLastVisibleItemPosition();
            int totalItemCount = mLayoutManager.getItemCount();
            // lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
            // dy>0 表示向下滑动
            if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                if (!isLoadMore) {
                    isLoadMore = true;
                    pullWallpaperData();
                }
            }
        };
    };

    private long mPublishDate;

    public void pullWallpaperData() {
        OnlineWallpaperManager.getInstance().pullWallpaperData(mContext, this, mPublishDate);
    }

    public void pullWallpaperData(int publishDate) {
        OnlineWallpaperManager.getInstance().pullWallpaperData(mContext, this, publishDate);
    }

    @Override
    public void onSuccecc(List<ServerOnlineWallpaper> list) {
        isNetWorkError = false;
        mErrorView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        if (list == null) {
            return;
        }
        Collections.sort(list, comparator);
        mPublishDate = list.get(list.size() - 1).getPublishDATE();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
        isLoadMore = false;
    }

    public static Comparator<ServerOnlineWallpaper> comparator = new Comparator<ServerOnlineWallpaper>() {

        @Override
        public int compare(ServerOnlineWallpaper lhs, ServerOnlineWallpaper rhs) {
            return (lhs.getPublishDATE() - rhs.getPublishDATE()) > 0 ? -1 : 1;
        }
    };

    @Override
    public void onFail() {
        isNetWorkError = true;
        mErrorView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        isLoadMore = false;
    }

    @Override
    public void onItemClick(View view, int position) {
        if (isNetWorkError) {
            return;
        }
        if (isLockScreen) {
            WallpaperDetailView detailView = new WallpaperDetailView(mContext, isLockScreen);
            detailView.setData(mList.get(position).getImageURL(), mList.get(position).getDesc());
            detailView.setWallpaperDetailListener(new IWallpaperDetailListener() {

                @Override
                public void onBack() {
                    if (null != mListener) {
                        mListener.onCloseDetailPage(true);
                    }
                }

                @Override
                public void onApplyWallpaper() {
                    if (null != mListener) {
                        mListener.onCloseDetailPage(false);
                    }
                }
            });
            if (null != mListener) {
                mListener.onOpenDetailPage(detailView);
            }
        } else {
            if (null != mListener) {
                mListener.onGoToDetailClick(mList.get(position));
            }
        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void loadMore() {
        if (!isLoadMore) {
            isLoadMore = true;
            pullWallpaperData();
        }
    }
}
