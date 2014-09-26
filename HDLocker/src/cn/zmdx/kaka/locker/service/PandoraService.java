
package cn.zmdx.kaka.locker.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class PandoraService extends Service {

    public static final String ALARM_ALERT_ACTION = "com.android.deskclock.ALARM_ALERT";

    @Override
    public void onCreate() {
        registerBroadcastReceiver();
        TelephonyManager manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        manager.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("PandoraService onDestroy()");
        }
        unRegisterBroadcastReceiver();
        startService(new Intent(this, PandoraService.class));
        super.onDestroy();
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ALARM_ALERT_ACTION);
        // filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mReceiver, filter);
    }

    private void unRegisterBroadcastReceiver() {
        unregisterReceiver(mReceiver);
    }

    private class MyPhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE: // 当前电话处于闲置状态
                        if (BuildConfig.DEBUG) {
                            HDBLOG.logD("当前电话处于闲置状态CALL_STATE_IDLE");
                        }
                        // LockScreenManager.getInstance().lock();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING: // 当前电话处于零响状态
                        if (BuildConfig.DEBUG) {
                            HDBLOG.logD("CALL_STATE_RINGING电话号码为 " + incomingNumber);
                        }
                        LockScreenManager.getInstance().unLock();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK: // 当前电话处于接听状态
                        if (BuildConfig.DEBUG) {
                            HDBLOG.logD("当前电话处于通话状态CALL_STATE_OFFHOOK ");
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("receive broadcast,action=" + action);
            }
            if (action.equals(ALARM_ALERT_ACTION)) {
                LockScreenManager.getInstance().unLock();
            } else {
                LockScreenManager.getInstance().lock();
            }
        }
    };
}
