
package cn.zmdx.kaka.locker.content.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.utils.ImageUtils;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class BeautyPageAdapter extends RecyclerView.Adapter<BeautyPageAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ImageView mImageView;

        public CardView mCardView;
        
        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);
            mImageView = (ImageView) view.findViewById(R.id.image);
            mCardView = (CardView) view.findViewById(R.id.cardView);
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
        holder.mTextView.setText(data.getImageDesc());
        Picasso picasso = Picasso.with(mContext);
                picasso.load(data.getUrl()).transform(new Transformation() {

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
                        return ImageUtils.scaleTo(source, cardWidth, newHeight, true);
                    }
                }).into(holder.mImageView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup vg, int position) {
        View view = LayoutInflater.from(vg.getContext()).inflate(
                R.layout.news_page_beauty_item_layout, vg, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
}
