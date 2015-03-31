
package cn.zmdx.kaka.locker.content.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.view.O2oMarketItemInfo;

public class O2oPageAdapter extends Adapter<O2oPageAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public ImageView mMarketIcon;

        public TextView mMarketCoupon;

        public TextView mMarketCouponEffectiveDate;

        public Button mMarketButtonToExchange;

        public ViewHolder(View view) {
            super(view);
            mMarketIcon = (ImageView) view.findViewById(R.id.market_item_icon);
            mMarketCoupon = (TextView) view.findViewById(R.id.market_item_coupon);
            mMarketCouponEffectiveDate = (TextView) view
                    .findViewById(R.id.market_item_effective_date);
            mMarketButtonToExchange = (Button) view.findViewById(R.id.market_item_to_exchange);
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

    private List<O2oMarketItemInfo> mData;

    public O2oPageAdapter(Context context, List<O2oMarketItemInfo> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pandora_market_item_layout,
                parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        O2oMarketItemInfo sO2oMarketItemInfo = mData.get(position);
        holder.mMarketIcon.setBackgroundResource(sO2oMarketItemInfo.getMarketIcon());
        holder.mMarketCoupon.setText(sO2oMarketItemInfo.getMarketCoupon());
        holder.mMarketCouponEffectiveDate
                .setText(sO2oMarketItemInfo.getMarketCouponEffectiveDate());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
