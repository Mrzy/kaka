
package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class ToolbarView extends GridView {

    public ToolbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ToolbarView(Context context) {
        this(context, null);
    }

    @Override
    protected void onAttachedToWindow() {
        IntentFilter mTrackerFilter = new IntentFilter(Constants.ACTION_UPDATE_TRACKER_STATE);
        mTrackerFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mTrackerFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        mTrackerFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        mTrackerFilter.addAction("android.bluetooth.intent.action.BLUETOOTH_STATE_CHANGED");
//        mTrackerFilter.addAction("com.android.sync.SYNC_CONN_STATUS_CHANGED");
        mTrackerFilter.addAction(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED);
//        mTrackerFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        mTrackerFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        mTrackerFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
//        mTrackerFilter.addAction("com.android.settings.GPS_STATUS_CHANGED");
        getContext().registerReceiver(mTackerReceiver, mTrackerFilter);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(mTackerReceiver);
        super.onDetachedFromWindow();
    }

    private BroadcastReceiver mTackerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((BaseAdapter) getAdapter()).notifyDataSetChanged();
        }
    };

}
