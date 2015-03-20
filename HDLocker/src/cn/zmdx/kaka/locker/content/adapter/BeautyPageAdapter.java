
package cn.zmdx.kaka.locker.content.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.utils.TimeUtils;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

public class BeautyPageAdapter extends RecyclerView.Adapter<BeautyPageAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView, mLikeCount, mTimeTv;

        public ImageView mImageView;

        public CardView mCardView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);
            mLikeCount = (TextView) view.findViewById(R.id.likeCount);
            mTimeTv = (TextView) view.findViewById(R.id.time);
            mImageView = (ImageView) view.findViewById(R.id.image);
            mCardView = (CardView) view.findViewById(R.id.cardView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClicked(v, getAdapterPosition());
            }
        }
    }

    private Context mContext;

    private List<ServerImageData> mData;

    public BeautyPageAdapter(Context context, List<ServerImageData> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ServerImageData data = mData.get(position);
        holder.mTextView.setText(data.getTitle());
        holder.mLikeCount.setText(data.getTop());
        String time = "";
        try {
            time = TimeUtils.getInterval(mContext, Long.valueOf(data.getCollectTime()));
        } catch (Exception e) {
        }
        holder.mTimeTv.setText(time);
        Picasso picasso = Picasso.with(mContext);
        picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
        RequestCreator rc = picasso.load(data.getUrl());
        int errorRes = R.drawable.icon_newsimage_load_error;
        if (PandoraConfig.newInstance(mContext).isOnlyWifiLoadImage()
                && !HDBNetworkState.isWifiNetwork()) {
            rc.networkPolicy(NetworkPolicy.OFFLINE);
            errorRes = R.drawable.icon_newsimage_loading;
        }
        rc.placeholder(R.drawable.icon_newsimage_loading)
                .error(errorRes).transform(new Transformation() {

                    @Override
                    public String key() {
                        return "matrix()";
                    }

                    @Override
                    public Bitmap transform(Bitmap source) {
                        int cardWidth = holder.mCardView.getWidth();
                        int imgWidth = source.getWidth();
                        int imgHeight = source.getHeight();
                        float scaleRate = (float) cardWidth / (float) imgWidth;
                        int newHeight = (int) (scaleRate * imgHeight);
                        Bitmap result = ImageUtils.scaleTo(source, cardWidth, newHeight, true);
                        return result == null ? source : result;
                    }
                }).into(holder.mImageView);
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup vg, final int position) {
        View view = LayoutInflater.from(vg.getContext()).inflate(
                R.layout.news_page_beauty_item_layout, vg, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
}
