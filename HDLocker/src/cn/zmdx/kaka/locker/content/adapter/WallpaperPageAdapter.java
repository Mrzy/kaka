
package cn.zmdx.kaka.locker.content.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

import com.squareup.picasso.Picasso;

public class WallpaperPageAdapter extends RecyclerView.Adapter<WallpaperPageAdapter.ViewHolder>{

    private List<String> mData;

    private Context mContext;

    private int mImageWidth;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public WallpaperPageAdapter(Context context, RecyclerView recyclerView, List<String> data) {
        mData = data;
        mContext = context;
        mImageWidth = BaseInfoHelper.getRealWidth(mContext) - BaseInfoHelper.dip2px(mContext, 20);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String imgUrl = mData.get(position);
        Picasso.with(mContext).load(imgUrl).resize(mImageWidth, mImageWidth).centerCrop()
                .into(holder.mImageView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup vg, final int position) {
        final View view = View.inflate(vg.getContext(), R.layout.pager_news_wallpaper_item_layout, null);
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(view, position);
                }
            }
        });

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
}
