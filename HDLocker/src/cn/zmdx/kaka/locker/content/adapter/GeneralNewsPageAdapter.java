
package cn.zmdx.kaka.locker.content.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;

import com.squareup.picasso.Picasso;

public class GeneralNewsPageAdapter extends Adapter<GeneralNewsPageAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;

        public ImageView mImageView;

        public CardView mCardView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);
            mImageView = (ImageView) view.findViewById(R.id.image);
            mCardView = (CardView) view.findViewById(R.id.cardView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClicked(v, getPosition());
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
        Picasso picasso = Picasso.with(mContext);
        picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
        picasso.load(data.getUrl()).into(holder.mImageView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup vg, int position) {
        View view = LayoutInflater.from(vg.getContext()).inflate(
                R.layout.news_page_general_item_layout, vg, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
}
