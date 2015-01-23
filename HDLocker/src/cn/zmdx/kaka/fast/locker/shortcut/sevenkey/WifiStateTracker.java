package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import cn.zmdx.kaka.fast.locker.R;

public class WifiStateTracker extends StateTracker {
    private static final int[] IMG_ON = {
        R.drawable.ic_dxhome_wifi_on,      // DX Home theme
    };

    private static final int[] IMG_OFF = {
        R.drawable.ic_dxhome_wifi_off,     // DX Home theme
    };

    private WifiManager mWifiMgr;

    public WifiStateTracker() {
        super(WidgetConfig.SWITCH_ID_WIFI);
    }

    @Override
    public int getIconResId(Context cxt, int themeType) {
        if (mState == STATE_ENABLED || mState == STATE_TURNING_ON) {
            return IMG_ON[0];
        } else {
            return IMG_OFF[0];
        }
    }

    private void ensureWifiManager(Context cxt) {
        if (mWifiMgr == null) {
            mWifiMgr = (WifiManager) cxt.getSystemService(Context.WIFI_SERVICE);
        }
    }

    @Override
    public void refreshActualState(Context cxt) {
        ensureWifiManager(cxt);
        if (mWifiMgr != null) {
            mState = wifiStateToFiveState(mWifiMgr.getWifiState());
            return;
        }
//        LogHelper.w(TAG, "no wifi manager");
    }

    @Override
    public void requestStateChange(final Context cxt, final boolean enabled) {
        ensureWifiManager(cxt);
        if (mWifiMgr == null) {
//            LogHelper.w(TAG, "no wifi manager");
            return;
        }

        // Actually request the wifi change and persistent
        // settings write off the UI thread, as it can take a
        // user-noticeable amount of time, especially if there's
        // disk contention.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... args) {
                mWifiMgr.setWifiEnabled(enabled);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                refreshActualState(cxt);
//                OptimizerApp.toast(enabled ? Res.string.switchwidget_wifi_on
//                        : Res.string.switchwidget_wifi_off, Toast.LENGTH_SHORT);
            }
        }.execute();
    }

    private static int wifiStateToFiveState(int wifiState) {
        switch (wifiState) {
            case WifiManager.WIFI_STATE_DISABLED:
                return STATE_DISABLED;
            case WifiManager.WIFI_STATE_ENABLED:
                return STATE_ENABLED;
            case WifiManager.WIFI_STATE_DISABLING:
                return STATE_TURNING_OFF;
            case WifiManager.WIFI_STATE_ENABLING:
                return STATE_TURNING_ON;
            default:
                return STATE_UNKNOWN;
        }
    }
}
