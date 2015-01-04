
package cn.zmdx.kaka.locker.notification;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.provider.Telephony;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

@SuppressLint("NewApi")
public final class PandoraNotificationService extends NotificationListenerService {

    public static final String ACTION_CANCEL_NOTIFICATION = "action_cancel_notification";

    public static final String ACTION_OBTAIN_ACTIVE_NOTIFICATIONS = "action_obtain_active_notification";

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
        initInterceptPackages();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CANCEL_NOTIFICATION);
        filter.addAction(ACTION_OBTAIN_ACTIVE_NOTIFICATIONS);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
        super.onCreate();
    }

    private void initInterceptPackages() {
        final NotificationPreferences np = NotificationPreferences
                .getInstance(getApplicationContext());
        np.putInterceptPkgName("com.tencent.mm");// 微信
        np.putInterceptPkgName("com.tencent.mobileqq");// qq
        np.putInterceptPkgName("com.google.android.dialer");// 拨号
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPkg = Telephony.Sms.getDefaultSmsPackage(getApplicationContext());
            if (defaultSmsPkg != null) {
                np.putInterceptPkgName(defaultSmsPkg);
            }
        } else {
            //4.4以下的手机，对于原生android系统，将环聊通知拦截
            np.putInterceptPkgName("com.google.android.talk");//环聊
            //TODO 确定4.0的设备是否使用环聊作为默认接收短信程序，如果不是，需要找到那个短信包名并设置拦截
        }
        // com.tencent.pb 微信电话本
        // com.google.android.dialer 默认拨号
        // com.google.android.talk 环聊
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
//                    cancelNotification(key);
                }
            } else if (action.equals(ACTION_OBTAIN_ACTIVE_NOTIFICATIONS)) {
                StatusBarNotification[] sbns = getActiveNotifications();
                NotificationInterceptor interceptor = NotificationInterceptor
                        .getInstance(getApplicationContext());
                Message msg = interceptor.obtainMessage();
                msg.obj = sbns;
                msg.what = NotificationInterceptor.MSG_ACTIVE_NOTIFICATIONS_ARRIVED;
                interceptor.sendMessage(msg);
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
