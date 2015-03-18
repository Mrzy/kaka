
package cn.zmdx.kaka.locker.content.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.CardView;
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
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.utils.TimeUtils;

import com.squareup.picasso.Picasso;

public class GeneralNewsPageAdapter extends Adapter<GeneralNewsPageAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView, mFromTv, mTimeTv;

        public ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);
            mFromTv = (TextView) view.findViewById(R.id.from);
            mTimeTv = (TextView) view.findViewById(R.id.time);
            mImageView = (ImageView) view.findViewById(R.id.image);
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

    public GeneralNewsPageAdapter(Context context, List<ServerImageData> data) {
        mContext = context;
        mData = data;
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
            Picasso picasso = Picasso.with(mContext);
//            picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
            picasso.load(data.getUrl()).into(holder.mImageView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup vg, int position) {
        View view = LayoutInflater.from(vg.getContext()).inflate(
                R.layout.news_page_general_item_layout, vg, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
}
