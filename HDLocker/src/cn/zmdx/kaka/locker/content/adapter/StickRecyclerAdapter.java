
package cn.zmdx.kaka.locker.content.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.content.PicassoHelper;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.TimeUtils;
import cn.zmdx.kaka.locker.widget.AutoScrollViewPager;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class StickRecyclerAdapter extends Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;

    private static final int TYPE_ITEM = 1;

    public class ViewItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView, mFromTv, mTimeTv;

        public ImageView mImageView;

        public View mDivider;

        public ViewItemHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);
            mFromTv = (TextView) view.findViewById(R.id.from);
            mTimeTv = (TextView) view.findViewById(R.id.time);
            mImageView = (ImageView) view.findViewById(R.id.image);
            mDivider = view.findViewById(R.id.general_news_divider);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position < 0 || position >= getItemCount()) {
                    return;
                }
                mListener.onItemClicked(v, position);
            }
        }
    }

    public class ViewHeaderHolder extends RecyclerView.ViewHolder {
        public AutoScrollViewPager mViewPager;

        public ViewGroup mPointViewGroup;

        public TypefaceTextView mTitleView;

        public View mHeaderLayout;

        public ViewHeaderHolder(View view) {
            super(view);
            mHeaderLayout = view.findViewById(R.id.hot_header_layout);
            mTitleView = (TypefaceTextView) view.findViewById(R.id.hot_title);
            mPointViewGroup = (ViewGroup) view.findViewById(R.id.hot_point);
            mViewPager = (AutoScrollViewPager) view.findViewById(R.id.hot_header_viewpaper);
        }

    }

    private Context mContext;

    private List<ServerImageData> mData;

    private int mTheme;

    private Resources mRes;

    private List<ServerImageData> mStickData;

    private boolean isAutoScroll = false;

    public StickRecyclerAdapter(Context context, List<ServerImageData> data,
            List<ServerImageData> stickData) {
        mContext = context;
        mData = data;
        mStickData = stickData;
        mTheme = PandoraConfig.newInstance(context).isNightModeOn() ? PandoraBoxManager.NEWS_THEME_NIGHT
                : PandoraBoxManager.NEWS_THEME_DAY;
        mRes = context.getResources();
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnStickClickListener {
        void onItemClicked(ServerImageData serverImageData);
    }

    private OnStickClickListener mStickListener;

    public void setOnStickClickListener(OnStickClickListener listener) {
        mStickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewItemHolder) {
            ViewItemHolder itemHodler = (ViewItemHolder) holder;
            if (mTheme == PandoraBoxManager.NEWS_THEME_DAY) {
                itemHodler.mTextView.setTextColor(mRes
                        .getColor(R.color.general_news_day_mode_title_color));
                itemHodler.mTimeTv.setTextColor(mRes
                        .getColor(R.color.general_news_day_mode_time_color));
                itemHodler.mFromTv.setTextColor(mRes
                        .getColor(R.color.general_news_day_mode_time_color));
                itemHodler.mDivider.setBackgroundColor(mRes
                        .getColor(R.color.general_news_day_mode_divider_color));
            } else if (mTheme == PandoraBoxManager.NEWS_THEME_NIGHT) {
                itemHodler.mTextView.setTextColor(mRes
                        .getColor(R.color.general_news_night_mode_title_color));
                itemHodler.mTimeTv.setTextColor(mRes
                        .getColor(R.color.general_news_night_mode_time_color));
                itemHodler.mFromTv.setTextColor(mRes
                        .getColor(R.color.general_news_night_mode_time_color));
                itemHodler.mDivider.setBackgroundColor(mRes
                        .getColor(R.color.general_news_night_mode_divider_color));
            } else {
                itemHodler.mTextView.setTextColor(mRes
                        .getColor(R.color.general_news_day_mode_title_color));
                itemHodler.mTimeTv.setTextColor(mRes
                        .getColor(R.color.general_news_day_mode_time_color));
                itemHodler.mFromTv.setTextColor(mRes
                        .getColor(R.color.general_news_day_mode_time_color));
                itemHodler.mDivider.setBackgroundColor(mRes
                        .getColor(R.color.general_news_day_mode_divider_color));
            }

            ServerImageData data = mData.get(position);
            itemHodler.mTextView.setText(data.getTitle());
            itemHodler.mFromTv.setText(data.getCollectWebsite());
            long time = 0;
            try {
                time = Long.valueOf(data.getCollectTime());
            } catch (Exception e) {
            }
            itemHodler.mTimeTv.setText(TimeUtils.getInterval(mContext, time));
            if (TextUtils.isEmpty(data.getUrl())) {
                itemHodler.mImageView.setVisibility(View.GONE);
            } else {
                itemHodler.mImageView.setVisibility(View.VISIBLE);
                Picasso picasso = PicassoHelper.getPicasso(mContext);
                picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
                RequestCreator rc = null;
                try {
                    rc = picasso.load(data.getUrl());
                } catch (Exception e) {
                }

                if (rc == null) {
                    picasso.load(R.drawable.icon_newsimage_load_error).into(itemHodler.mImageView);
                } else {
                    int errorRes = R.drawable.icon_newsimage_load_error;
                    if (PandoraConfig.newInstance(mContext).isOnlyWifiLoadImage()
                            && !HDBNetworkState.isWifiNetwork()) {
                        rc.networkPolicy(NetworkPolicy.OFFLINE);
                        errorRes = R.drawable.icon_newsimage_loading;
                    }
                    rc.placeholder(R.drawable.icon_newsimage_loading).error(errorRes).fit()
                            .centerInside().into(itemHodler.mImageView);
                }
            }
        } else if (holder instanceof ViewHeaderHolder) {
            final ViewHeaderHolder headerHolder = (ViewHeaderHolder) holder;
            if (mStickData.size() != 0) {
                LayoutParams params = headerHolder.mHeaderLayout.getLayoutParams();
                params.height = LayoutParams.WRAP_CONTENT;
                headerHolder.mHeaderLayout.setLayoutParams(params);
                headerHolder.mHeaderLayout.setVisibility(View.VISIBLE);
                if (null == mMicroStickAdapter) {
                    mMicroStickAdapter = new StickPageAdapter(getStickView(mStickData.size()),
                            initStickPointView(headerHolder.mViewPager, mStickData.size(),
                                    headerHolder.mPointViewGroup), headerHolder.mTitleView,
                            mStickData);
                    headerHolder.mViewPager.setAdapter(mMicroStickAdapter);
                    headerHolder.mViewPager.setOnPageChangeListener(mMicroStickAdapter);
                    headerHolder.mViewPager.setCurrentItem(0);
                    headerHolder.mTitleView.setText(mStickData.size() == 0 ? "" : mStickData.get(0)
                            .getTitle());
                    headerHolder.mViewPager.setOnTouchListener(new OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    mPrevX = MotionEvent.obtain(event).getX();
                                    isOnItemClick = true;
                                    return false;
                                case MotionEvent.ACTION_MOVE:
                                    final float eventX = event.getX();
                                    float xDiff = Math.abs(eventX - mPrevX);
                                    if (xDiff < mTouchSlop) {
                                        isOnItemClick = true;
                                        return true;
                                    }
                                    isOnItemClick = false;
                                    return false;
                                case MotionEvent.ACTION_UP:
                                    if (isOnItemClick) {
                                        ServerImageData sid = null;
                                        int category = headerHolder.mViewPager.getCurrentItem();
                                        sid = mStickData.get(category);
                                        if (mStickListener != null) {
                                            mStickListener.onItemClicked(sid);
                                        }
                                    }
                                    mPrevX = 0;
                                    return false;

                                default:
                                    return false;
                            }

                        }
                    });
                    headerHolder.mViewPager.startAutoScroll();
                    headerHolder.mViewPager.setAutoScrollDurationFactor(5);
                } else {
                    if (isAutoScroll) {
                        headerHolder.mViewPager.startAutoScroll();
                    } else {
                        headerHolder.mViewPager.stopAutoScroll();
                    }
                    mMicroStickAdapter.notifyDataChanged(
                            getStickView(mStickData.size()),
                            initStickPointView(headerHolder.mViewPager, mStickData.size(),
                                    headerHolder.mPointViewGroup));
                }
            } else {
                LayoutParams params = headerHolder.mHeaderLayout.getLayoutParams();
                params.height = 1;
                headerHolder.mHeaderLayout.setLayoutParams(params);
                headerHolder.mHeaderLayout.setVisibility(View.GONE);
            }
        }
    }

    private StickPageAdapter mMicroStickAdapter;

    public void setTheme(int theme) {
        mTheme = theme;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(vg.getContext()).inflate(
                    R.layout.news_page_general_item_layout, vg, false);
            ViewItemHolder vh = new ViewItemHolder(view);
            return vh;
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(vg.getContext()).inflate(
                    R.layout.pager_stick_header_layout, vg, false);
            ViewHeaderHolder vh = new ViewHeaderHolder(view);
            return vh;
        }

        throw new RuntimeException("there is no type that matches the type " + viewType
                + " + make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int viewType) {
        if (isPositionHeader(viewType)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private ArrayList<ImageView> getStickView(int size) {
        ArrayList<ImageView> pageViews = new ArrayList<ImageView>();
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(HDApplication.getContext());
            pageViews.add(imageView);
        }
        return pageViews;
    }

    private ImageView[] initStickPointView(ViewPager viewPaper, int size, ViewGroup group) {
        ImageView[] pointViews = new ImageView[size];
        group.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(HDApplication.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(16, 16);
            lp.setMargins(5, 0, 5, 0);
            imageView.setLayoutParams(lp);
            pointViews[i] = imageView;
            if (i == viewPaper.getCurrentItem()) {
                imageView.setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                imageView.setBackgroundResource(R.drawable.page_indicator_normal);
            }
            group.addView(imageView);
        }
        return pointViews;
    }

    public void setAutoScroll(boolean isAutoScroll) {
        this.isAutoScroll = isAutoScroll;
    }

    private boolean isOnItemClick = false;

    private int mTouchSlop;

    private float mPrevX;

}
