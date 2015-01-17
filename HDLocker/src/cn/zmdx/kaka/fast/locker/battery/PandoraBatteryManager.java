
package cn.zmdx.kaka.fast.locker.battery;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import cn.zmdx.kaka.fast.locker.HDApplication;

/**
 * @deprecated 已废弃，使用BatteryView代替
 * @author zhangyan
 *
 */
public class PandoraBatteryManager {

    private static PandoraBatteryManager INSTANCE = null;

    private int mStatus = -1;

    private int mMaxScale = -1;

    private int mCurLevel = -1;

    private PandoraBatteryManager() {
    }

    public static PandoraBatteryManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PandoraBatteryManager();
        }
        return INSTANCE;
    }

    public int getBatteryStatus() {
        return mStatus;
    }

    public int getMaxScale() {
        return mMaxScale;
    }

    public int getCurLevel() {
        return mCurLevel;
    }

    public void registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        HDApplication.getContext().registerReceiver(receiver, filter);
    }

    public void unRegisterListener() {
        HDApplication.getContext().unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(android.content.Context context, Intent intent) {
            mStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            mMaxScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            mCurLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//            LockScreenManager.getInstance().onBatteryStatusChanged(mStatus);
        };
    };
}
