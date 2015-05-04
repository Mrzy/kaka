
package cn.zmdx.kaka.locker.content.adapter;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.PicassoHelper;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.RequestCreator;

public class WallpaperPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ServerOnlineWallpaper> mData;

    private Context mContext;

    private int mImageWidth;

    private int mImageHeight;

    private int mMargins;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClicked(View view, int position);
    }

    public WallpaperPageAdapter(Context context, RecyclerView recyclerView,
            List<ServerOnlineWallpaper> list) {
        mData = list;
        mContext = context;
        mMargins = BaseInfoHelper.dip2px(mContext, 8);
        mImageWidth = BaseInfoHelper.getRealWidth(mContext) - mMargins * 2;
        mImageHeight = (720 * mImageWidth) / 1080;
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public TypefaceTextView mPublicDay;

        public TypefaceTextView mPublicMonth;

        public TypefaceTextView mWallpaperName;

        public ViewItemHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.wallpaper_imageView);
            mPublicDay = (TypefaceTextView) view.findViewById(R.id.wallpaper_date_day);
            mPublicMonth = (TypefaceTextView) view.findViewById(R.id.wallpaper_date_month);
            mWallpaperName = (TypefaceTextView) view.findViewById(R.id.wallpaper_name);
            LayoutParams params = mImageView.getLayoutParams();
            params.width = mImageWidth;
            params.height = mImageHeight;
            mImageView.setLayoutParams(params);
//            LinearLayout mLayout = (LinearLayout) view.findViewById(R.id.wallpaper_layout);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            lp.setMargins(mMargins - 5, 0, mMargins, 0);
//            mLayout.setLayoutParams(lp);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewItemHolder holder = (ViewItemHolder) viewHolder;
        final ServerOnlineWallpaper item = mData.get(position);
        RequestCreator rc = PicassoHelper.getPicasso(mContext).load(item.getThumbURL());
        if (PandoraConfig.newInstance(mContext).isOnlyWifiLoadImage()
                && !HDBNetworkState.isWifiNetwork()) {
            rc.networkPolicy(NetworkPolicy.OFFLINE);
        }
        rc.resize(mImageWidth, mImageHeight).centerCrop().into(holder.mImageView);
        holder.mPublicDay.setText("" + getDayByTime(item.getPublishDATE(), Calendar.DAY_OF_MONTH));
        int month = getDayByTime(item.getPublishDATE(), Calendar.MONTH);
        holder.mPublicMonth.setText("" + getMonthEn(month));
        holder.mWallpaperName.setText(item.getDesc());
        final View itemView = holder.itemView;
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClicked(itemView, position);
                }
            }
        });

    }

    private int getDayByTime(long time, int field) {
        Calendar cal = Calendar.getInstance();
        if (time > 0) {
            cal.setTimeInMillis(time);
        }
        return cal.get(field);
    }

    private String getMonthEn(int month) {
        if (month == 0) {
            return mContext.getString(R.string.pandora_wallpaper_month_january);
        } else if (month == 1) {
            return mContext.getString(R.string.pandora_wallpaper_month_february);
        } else if (month == 2) {
            return mContext.getString(R.string.pandora_wallpaper_month_march);
        } else if (month == 3) {
            return mContext.getString(R.string.pandora_wallpaper_month_april);
        } else if (month == 4) {
            return mContext.getString(R.string.pandora_wallpaper_month_may);
        } else if (month == 5) {
            return mContext.getString(R.string.pandora_wallpaper_month_june);
        } else if (month == 6) {
            return mContext.getString(R.string.pandora_wallpaper_month_july);
        } else if (month == 7) {
            return mContext.getString(R.string.pandora_wallpaper_month_august);
        } else if (month == 8) {
            return mContext.getString(R.string.pandora_wallpaper_month_september);
        } else if (month == 9) {
            return mContext.getString(R.string.pandora_wallpaper_month_october);
        } else if (month == 10) {
            return mContext.getString(R.string.pandora_wallpaper_month_november);
        } else if (month == 11) {
            return mContext.getString(R.string.pandora_wallpaper_month_december);
        } else {
            return "";
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, final int position) {
        final View view = View.inflate(vg.getContext(), R.layout.pager_news_wallpaper_item_layout,
                null);
        ViewItemHolder vh = new ViewItemHolder(view);
        return vh;
    }

}
