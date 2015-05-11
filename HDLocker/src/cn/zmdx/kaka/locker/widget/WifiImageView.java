
package cn.zmdx.kaka.locker.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class WifiImageView extends ImageView {
    public WifiImageView(Context context) {
        this(context, null);
    }

    public WifiImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WifiImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getContext().registerReceiver(mWifiReceiver, filter);
    }

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(mWifiReceiver);
        super.onDetachedFromWindow();
    }

    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    if (networkInfo != null && networkInfo.isAvailable()
                            && networkInfo.isConnected()) {
                        setVisibility(View.VISIBLE);
                    } else {
                        setVisibility(View.GONE);
                    }
                }
            }
        }
    };
}
