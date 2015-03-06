
package cn.zmdx.kaka.locker.content.adapter;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.squareup.picasso.Picasso;

public class WallpaperPageAdapter extends RecyclerView.Adapter<WallpaperPageAdapter.ViewHolder> {

    private List<ServerOnlineWallpaper> mData;

    private Context mContext;

    private int mImageWidth;

    private int mImageHeight;

    private int mMargins;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public WallpaperPageAdapter(Context context, RecyclerView recyclerView,
            List<ServerOnlineWallpaper> list) {
        mData = list;
        mContext = context;
        mMargins = BaseInfoHelper.dip2px(mContext, 25);
        mImageWidth = BaseInfoHelper.getRealWidth(mContext) - mMargins * 2;
        mImageHeight = (512 * mImageWidth) / 916;
        // 916 512
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public TypefaceTextView mPublicDay;

        public TypefaceTextView mPublicMonth;

        public TypefaceTextView mWallpaperName;

        public ViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.wallpaper_imageView);
            mPublicDay = (TypefaceTextView) view.findViewById(R.id.wallpaper_date_day);
            mPublicMonth = (TypefaceTextView) view.findViewById(R.id.wallpaper_date_month);
            mWallpaperName = (TypefaceTextView) view.findViewById(R.id.wallpaper_name);
            LayoutParams params = mImageView.getLayoutParams();
            params.width = mImageWidth;
            params.height = mImageHeight;
            mImageView.setLayoutParams(params);
            LinearLayout mLayout = (LinearLayout) view.findViewById(R.id.wallpaper_layout);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(mMargins, 0, mMargins, 0);
            mLayout.setLayoutParams(lp);
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ServerOnlineWallpaper item = mData.get(position);
        Picasso.with(mContext).load(item.getThumbURL()).resize(mImageWidth, mImageHeight)
                .centerCrop().into(holder.mImageView);
        holder.mPublicDay.setText("" + getDayByTime(item.getPublishDATE(), Calendar.DAY_OF_MONTH));
        int month = getDayByTime(item.getPublishDATE(), Calendar.MONTH) + 1;
        int xq = getDayByTime(item.getPublishDATE(), Calendar.DAY_OF_WEEK);
        holder.mPublicMonth.setText(month + "月\n" + getWeekStr(xq));
        holder.mWallpaperName.setText(item.getDesc());
        final View itemView = holder.itemView;
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(itemView, position);
                }
            }
        });

    }

    private int getDayByTime(long time, int field) {
        Calendar cal = Calendar.getInstance();
        // if (time > 0) {
        cal.setTimeInMillis(time);
        // }
        return cal.get(field);
    }

    private String getWeekStr(int xq) {
        if (xq == 1) {
            return mContext.getString(R.string.lock_week_sunday);
        } else if (xq == 2) {
            return mContext.getString(R.string.lock_week_monday);
        } else if (xq == 3) {
            return mContext.getString(R.string.lock_week_tuesday);
        } else if (xq == 4) {
            return mContext.getString(R.string.lock_week_wednesday);
        } else if (xq == 5) {
            return mContext.getString(R.string.lock_week_thursday);
        } else if (xq == 6) {
            return mContext.getString(R.string.lock_week_friday);
        } else if (xq == 7) {
            return mContext.getString(R.string.lock_week_saturday);
        } else {
            return "";
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup vg, final int position) {
        final View view = View.inflate(vg.getContext(), R.layout.pager_news_wallpaper_item_layout,
                null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

}
