
package cn.zmdx.kaka.locker.content.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.content.PicassoHelper;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.TimeUtils;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class GeneralNewsPageAdapter extends Adapter<GeneralNewsPageAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView, mFromTv, mTimeTv;

        public ImageView mImageView;

        public View mDivider;

        public ViewHolder(View view) {
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

    private Context mContext;

    private List<ServerImageData> mData;

    private int mTheme;

    private Resources mRes;

    public GeneralNewsPageAdapter(Context context, List<ServerImageData> data) {
        mContext = context;
        mData = data;
        mTheme = PandoraConfig.newInstance(context).isNightModeOn() ? PandoraBoxManager.NEWS_THEME_NIGHT
                : PandoraBoxManager.NEWS_THEME_DAY;
        mRes = context.getResources();
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mTheme == PandoraBoxManager.NEWS_THEME_DAY) {
            holder.mTextView.setTextColor(mRes.getColor(R.color.general_news_day_mode_title_color));
            holder.mTimeTv.setTextColor(mRes.getColor(R.color.general_news_day_mode_time_color));
            holder.mFromTv.setTextColor(mRes.getColor(R.color.general_news_day_mode_time_color));
            holder.mDivider.setBackgroundColor(mRes
                    .getColor(R.color.general_news_day_mode_divider_color));
        } else if (mTheme == PandoraBoxManager.NEWS_THEME_NIGHT) {
            holder.mTextView.setTextColor(mRes
                    .getColor(R.color.general_news_night_mode_title_color));
            holder.mTimeTv.setTextColor(mRes.getColor(R.color.general_news_night_mode_time_color));
            holder.mFromTv.setTextColor(mRes.getColor(R.color.general_news_night_mode_time_color));
            holder.mDivider.setBackgroundColor(mRes
                    .getColor(R.color.general_news_night_mode_divider_color));
        } else {
            holder.mTextView.setTextColor(mRes.getColor(R.color.general_news_day_mode_title_color));
            holder.mTimeTv.setTextColor(mRes.getColor(R.color.general_news_day_mode_time_color));
            holder.mFromTv.setTextColor(mRes.getColor(R.color.general_news_day_mode_time_color));
            holder.mDivider.setBackgroundColor(mRes
                    .getColor(R.color.general_news_day_mode_divider_color));
        }

        ServerImageData data = mData.get(position);
        holder.mTextView.setText(data.getTitle());
        holder.mFromTv.setText(data.getCollectWebsite());
        long time = 0;
        try {
            time = Long.valueOf(data.getCollectTime());
        } catch (Exception e) {
        }
        holder.mTimeTv.setText(TimeUtils.getInterval(mContext, time));
        if (TextUtils.isEmpty(data.getUrl())) {
            holder.mImageView.setVisibility(View.GONE);
        } else {
            holder.mImageView.setVisibility(View.VISIBLE);
            Picasso picasso = PicassoHelper.getPicasso(mContext);
            picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
            RequestCreator rc = null;
            try {
                rc = picasso.load(data.getUrl());
            } catch (Exception e) {
            }

            if (rc == null) {
                picasso.load(R.drawable.icon_newsimage_load_error).into(holder.mImageView);
            } else {
                int errorRes = R.drawable.icon_newsimage_load_error;
                if (PandoraConfig.newInstance(mContext).isOnlyWifiLoadImage()
                        && !HDBNetworkState.isWifiNetwork()) {
                    rc.networkPolicy(NetworkPolicy.OFFLINE);
                    errorRes = R.drawable.icon_newsimage_loading;
                }
                rc.placeholder(R.drawable.icon_newsimage_loading).error(errorRes).fit()
                        .centerInside().into(holder.mImageView);
            }
        }
    }

    public void setTheme(int theme) {
        mTheme = theme;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup vg, int position) {
        View view = LayoutInflater.from(vg.getContext()).inflate(
                R.layout.news_page_general_item_layout, vg, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
}
