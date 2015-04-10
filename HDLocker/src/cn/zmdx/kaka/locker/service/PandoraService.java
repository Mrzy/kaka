
package cn.zmdx.kaka.locker.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.settings.MainSettingActivity;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.weather.PandoraLocationManager;

public class PandoraService extends Service {

    // 安卓原生系统闹钟action
    private static final String ALARM_ALERT_ACTION = "com.android.deskclock.ALARM_ALERT";

    /**
     * 中兴手机闹钟的action
     */
    private static final String ALARMALERT_ACTION_ZX = "com.zdworks.android.zdclock.ACTION_ALARM_ALERT";

    // 三星手机闹钟action
    private static final String ALARMALERT_ACTION_SAMSUNG = "com.samsung.sec.android.clockpackage.alarm.ALARM_ALERT";

    // 索尼手机闹钟action
    private static final String ALARMALERT_ACTION_SONY = "com.sonyericsson.alarm.ALARM_ALERT";

    // 联想手机闹钟action
    private static final String ALARMALERT_ACTION_LENOVO = "com.lenovo.deskclock.ALARM_ALERT";

    // vivo手机闹钟action
    private static final String ALARMALERT_ACTION_VIVO = "com.cn.google.AlertClock.ALARM_ALERT";

    // 魅族手机闹钟action
    private static final String ALARMALERT_ACTION_MEIZU = "com.android.alarmclock.ALARM_ALERT";

    private static final int FOREGROUND_SERVICE_ID = 123465;

    private Context mContext = HDApplication.getContext();

    private List<String> mAlarmActions = new ArrayList<String>();

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("PandoraService is startup");
        }
        timingUpdateCurLocation();
        loadAlarmActions();
        registerBroadcastReceiver();
        TelephonyManager manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        manager.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);

        if (PandoraConfig.newInstance(this).isPandoraProtectOn()) {
            Notification noti = createNotification();
            startForeground(FOREGROUND_SERVICE_ID, noti);
        }
        super.onCreate();
    }

    private void loadAlarmActions() {
        mAlarmActions.add(ALARM_ALERT_ACTION);
        mAlarmActions.add(ALARMALERT_ACTION_ZX);
        mAlarmActions.add(ALARMALERT_ACTION_SAMSUNG);
        mAlarmActions.add(ALARMALERT_ACTION_SONY);
        mAlarmActions.add(ALARMALERT_ACTION_LENOVO);
        mAlarmActions.add(ALARMALERT_ACTION_VIVO);
        mAlarmActions.add(ALARMALERT_ACTION_MEIZU);
    }

    @SuppressWarnings("deprecation")
    private Notification createNotification() {
        Notification notification = new Notification(R.drawable.ic_launcher, "开启成功",
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainSettingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, "潘多拉守护神", "点击可关闭守护", pendingIntent);
        return notification;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "stop".equals(intent.getStringExtra("action"))) {
            stopForeground(true);
        } else if (intent != null && "startGuard".equals(intent.getStringExtra("action"))) {
            Notification noti = createNotification();
            startForeground(FOREGROUND_SERVICE_ID, noti);
        }
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public static void startForegroudService(Context context) {
        Intent in = new Intent(context, PandoraService.class);
        in.putExtra("action", "startGuard");
        context.startService(in);
    }

    public static void stopForegroundService(Context context) {
        Intent in = new Intent(context, PandoraService.class);
        in.putExtra("action", "stop");
        context.startService(in);
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("PandoraService onDestroy()");
        }
        super.onDestroy();
        unRegisterBroadcastReceiver();
        stopForeground(true);
        startService(new Intent(this, PandoraService.class));
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        addAlarmActions(filter);
        registerReceiver(mReceiver, filter);
    }

    private void addAlarmActions(IntentFilter filter) {
        for (String action : mAlarmActions) {
            filter.addAction(action);
        }
    }

    private void unRegisterBroadcastReceiver() {
        unregisterReceiver(mReceiver);
    }

    private void timingUpdateCurLocation() {
        long lastCheckLocationTime = PandoraConfig.newInstance(mContext).getLastCheckLocationTime();
        if (System.currentTimeMillis() - lastCheckLocationTime >= PandoraPolicy.MIN_UPDATE_LOCATION_TIME) {
            PandoraLocationManager.getInstance(mContext).requestLocation();
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("---Timing update CurLocation-->>");
            }
        }
    }

    private static boolean isComingCall = false;

    private static boolean isCalling = false;

    private boolean isLockedWhenComingCall = true;

    public static boolean isCalling() {
        return isCalling;
    }

    private class MyPhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE: // 当前电话处于闲置状态
                        if (BuildConfig.DEBUG) {
                            HDBLOG.logD("当前电话处于闲置状态CALL_STATE_IDLE, isRinging:");
                        }
                        isCalling = false;
                        if (isComingCall && isLockedWhenComingCall) {
                            LockScreenManager.getInstance().lock();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING: // 当前电话处于零响状态
                        if (BuildConfig.DEBUG) {
                            HDBLOG.logD("CALL_STATE_RINGING电话号码为 " + incomingNumber);
                        }
                        isCalling = true;
                        isLockedWhenComingCall = LockScreenManager.getInstance().isLocked();
                        isComingCall = true;
                        LockScreenManager.getInstance().unLock(true, true);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK: // 当前电话处于接听状态
                        if (BuildConfig.DEBUG) {
                            HDBLOG.logD("当前电话处于通话状态CALL_STATE_OFFHOOK ");
                        }
                        isCalling = true;
                        isLockedWhenComingCall = false;
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
            if (mAlarmActions.contains(action)) {
                LockScreenManager.getInstance().unLock(true, true);
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                // 如果开启延迟锁定，则1.5s后再锁屏
                if (PandoraConfig.newInstance(mContext).isDelayLockScreenOn()
                        && !LockScreenManager.getInstance().isLocked()) {
                    HDBThreadUtils.postOnUiDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PowerManager pm = (PowerManager) mContext
                                    .getSystemService(Context.POWER_SERVICE);
                            if (!pm.isScreenOn()) {
                                LockScreenManager.getInstance().lock();
                                LockScreenManager.getInstance().onScreenOff();
                            }
                        };
                    }, 1500);
                } else {
                    LockScreenManager.getInstance().lock();
                    LockScreenManager.getInstance().onScreenOff();
                }
                timingUpdateCurLocation();
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                LockScreenManager.getInstance().onScreenOn();
            }
        }
    };
}
