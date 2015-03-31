
package cn.zmdx.kaka.locker.content.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;

public class O2oMarketDetailLayout extends FrameLayout {

    private O2oMarketItemInfo mMarketItemInfo;

    private PandoraBoxManager mPbManager;

    private TextView mMarketCouponNameDetail;

    private TextView mEffectiveDateDetail;

    private TextView mMarketCouponIsEffective;

    private TextView mMarkerCouponNum;

    private ImageView mMarketIconDetail;

    private ImageView btnBack;

    public O2oMarketDetailLayout(Context context) {
        this(context, null);
    }

    public O2oMarketDetailLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public O2oMarketDetailLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public O2oMarketDetailLayout(PandoraBoxManager pbManager, O2oMarketItemInfo o2oMarketItemInfo) {
        this(HDApplication.getContext());
        mPbManager = pbManager;
        mMarketItemInfo = o2oMarketItemInfo;
        initView();
    }

    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.pandora_market_detail_layout, this);
        mMarketCouponNameDetail = (TextView) view.findViewById(R.id.marketCouponNameDetail);
        mEffectiveDateDetail = (TextView) view.findViewById(R.id.marketCouponEffectiveDateDetail);
        mMarketCouponIsEffective = (TextView) view.findViewById(R.id.marketCouponIsEffective);
        mMarkerCouponNum = (TextView) view.findViewById(R.id.markerCouponNum);
        mMarketIconDetail = (ImageView) view.findViewById(R.id.marketIconDetail);
        btnBack = (ImageView) view.findViewById(R.id.market_back_btn);
        btnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    public void initView() {
        String marketCoupon = mMarketItemInfo.getMarketCoupon();
        String marketCouponEffectiveDate = mMarketItemInfo.getMarketCouponEffectiveDate();
        String marketCouponNum = mMarketItemInfo.getMarketCouponNum();
        int marketIcon = mMarketItemInfo.getMarketIcon();
        boolean effective = mMarketItemInfo.isEffective();
        if (!TextUtils.isEmpty(marketCoupon)) {
            mMarketCouponNameDetail.setText(marketCoupon);
        }
        if (!TextUtils.isEmpty(marketCouponEffectiveDate)) {
            mEffectiveDateDetail.setText(marketCouponEffectiveDate);
        }
        if (!TextUtils.isEmpty(marketCouponNum)) {
            mMarkerCouponNum.setText(marketCouponNum);
        }
        mMarketIconDetail.setBackgroundResource(marketIcon);
        mMarketCouponIsEffective.setText(effective == true ? "当前有效" : "已使用");
    }

    public void back() {
        exit(true);
    }

    private void exit(boolean withAnimator) {
        mPbManager.closeDetailPage(withAnimator);
    }
}
