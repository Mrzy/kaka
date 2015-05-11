
package cn.zmdx.kaka.locker.wallpaper;

import java.util.ArrayList;
import java.util.Collections;
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

public class OnlineWallpaperView extends LinearLayout implements IPullWallpaperListener,
        OnItemClickListener {
    private Context mContext;

    private View mEntireView;

    private RecyclerView mRecyclerView;

    private View mErrorView;

    private LinearLayoutManager mLayoutManager;

    private WallpaperPageAdapter mAdapter;

    private List<ServerOnlineWallpaper> mList = new ArrayList<ServerOnlineWallpaperManager.ServerOnlineWallpaper>();

    private boolean isNetWorkError = false;

    private IOnlineWallpaperListener mListener;

    public interface IOnlineWallpaperListener {
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

    public OnlineWallpaperView(Context context) {
        super(context);
        mContext = context;
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

    private long mLastModified = System.currentTimeMillis();

    public void pullWallpaperData() {
        OnlineWallpaperManager.getInstance().pullWallpaperData(mContext, this, 1, mLastModified);
    }

    @Override
    public void onSuccecc(List<ServerOnlineWallpaper> list) {
        isNetWorkError = false;
        mErrorView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        if (list == null) {
            return;
        }
        mList.addAll(list);
        Collections.sort(mList, WallpaperUtils.comparator);
        if (mList.size() > 0) {
            mLastModified = mList.get(mList.size() - 1).getPublishDATE();
        }

        mAdapter.notifyDataSetChanged();
        if (!isLoadMore) {
            mRecyclerView.scrollToPosition(0);
        }
        isLoadMore = false;
    }

    @Override
    public void onFail() {
        isNetWorkError = true;
        mErrorView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        isLoadMore = false;
    }

    @Override
    public void onItemClicked(View view, int position) {
        if (isNetWorkError) {
            return;
        }
        if (null != mListener) {
            mListener.onGoToDetailClick(mList.get(position));
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
