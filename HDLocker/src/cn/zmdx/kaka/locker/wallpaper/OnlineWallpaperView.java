
package cn.zmdx.kaka.locker.wallpaper;

import java.util.List;

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

    private LinearLayoutManager mLayoutManager;

    private WallpaperPageAdapter mAdapter;

    private List<ServerOnlineWallpaper> mList;

    private IOnlineWallpaperListener mListener;

    public interface IOnlineWallpaperListener {
        void onCloseDetailPage();

        void onOpenDetailPage(View view);
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

    private void initView() {
        mEntireView = LayoutInflater.from(mContext).inflate(R.layout.pager_wallpaper_layout, null);
        addView(mEntireView);
        mRecyclerView = (RecyclerView) mEntireView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVerticalFadingEdgeEnabled(true);
        mRecyclerView.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 10));
        mRecyclerView.setOnScrollListener(mScrollListener);
        pullWallpaperData();
    }

    public void setOnlineWallpaperListener(IOnlineWallpaperListener listener) {
        mListener = listener;
    }

    private OnScrollListener mScrollListener = new OnScrollListener() {
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItem = ((LinearLayoutManager) mLayoutManager)
                    .findLastVisibleItemPosition();
            int totalItemCount = mLayoutManager.getItemCount();
            // lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
            // dy>0 表示向下滑动
            if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                // loadPage(currentQueryMap);
            }
        };
    };

    private void pullWallpaperData() {
        OnlineWallpaperManager.getInstance().pullWallpaperData(mContext, this);
    }

    @Override
    public void onSuccecc(List<ServerOnlineWallpaper> list) {
        mList = list;
        mAdapter = new WallpaperPageAdapter(mContext, mRecyclerView, list);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onFail() {

    }

    @Override
    public void onItemClick(View view, int position) {
        WallpaperDetailView detailView = new WallpaperDetailView(mContext);
        detailView.setData(mList.get(position));
        detailView.setWallpaperDetailListener(new IWallpaperDetailListener() {

            @Override
            public void onBack() {
                if (null != mListener) {
                    mListener.onCloseDetailPage();
                }
            }
        });
        if (null != mListener) {
            mListener.onOpenDetailPage(detailView);
        }
    }
}
