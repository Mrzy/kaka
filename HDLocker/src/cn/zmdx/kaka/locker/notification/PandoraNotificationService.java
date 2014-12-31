
package cn.zmdx.kaka.locker.notification;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

@SuppressLint("NewApi")
public final class PandoraNotificationService extends NotificationListenerService {

    public static final String ACTION_CANCEL_NOTIFICATION = "action_cancel_notification";

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CANCEL_NOTIFICATION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_CANCEL_NOTIFICATION)) {
                String key = intent.getStringExtra("key");
                if (TextUtils.isEmpty(key)) {
                    String pkg = intent.getStringExtra("pkgName");
                    String tag = intent.getStringExtra("tag");
                    int id = intent.getIntExtra("id", 0);
                    if (!TextUtils.isEmpty(pkg)) {
                        cancelNotification(pkg, tag, id);
                    }
                } else {
                    cancelNotification(key);
                }
            }
        }
    };

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        NotificationInterceptor interceptor = NotificationInterceptor.getInstance(this);
        Message msg = interceptor.obtainMessage();
        msg.what = NotificationInterceptor.MSG_NOTIFICATION_POST;
        msg.obj = sbn;
        interceptor.sendMessage(msg);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        NotificationInterceptor interceptor = NotificationInterceptor.getInstance(this);
        Message msg = interceptor.obtainMessage();
        msg.what = NotificationInterceptor.MSG_NOTIFICATION_REMOVED;
        msg.obj = sbn;
        interceptor.sendMessage(msg);
    }
}
