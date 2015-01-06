
package cn.zmdx.kaka.locker.notification;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.provider.Telephony;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

@SuppressLint("NewApi")
public final class PandoraNotificationService extends NotificationListenerService {

    public static final String ACTION_CANCEL_NOTIFICATION = "action_cancel_notification";

    public static final String ACTION_OBTAIN_ACTIVE_NOTIFICATIONS = "action_obtain_active_notification";

    private Context mContext = getApplicationContext();

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
        // 获取拨号的包名
        String[] dialerPkgName = getDialerPkgName(mContext, Intent.ACTION_DIAL);
        List<String> dialerPkgNameList = removeDuplicate(dialerPkgName);

        for (int i = 0; i < dialerPkgNameList.size(); i++) {
            boolean systemApp = isSystemApp(mContext, dialerPkgNameList.get(i));
            if (systemApp) {
                // 显示系统级别的拨号软件包名
                np.putInterceptPkgName(dialerPkgNameList.get(i));
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPkg = Telephony.Sms.getDefaultSmsPackage(getApplicationContext());
            if (defaultSmsPkg != null) {
                np.putInterceptPkgName(defaultSmsPkg);
            }
        } else {
            // 4.4以下的手机，对于原生android系统，将环聊通知拦截
            // TODO 确定4.0的设备是否使用环聊作为默认接收短信程序，如果不是，需要找到那个短信包名并设置拦截
            // 获取短信的包名
            String androidOrigin = "com.google.android.talk";
            String[] smsPkgName = getSmsPkgName(mContext);
            List<String> smsPkgNameList = removeDuplicate(smsPkgName);

            for (int i = 0; i < smsPkgNameList.size(); i++) {
                boolean systemApp = isSystemApp(mContext, smsPkgNameList.get(i));
                if (systemApp) {
                    if (smsPkgNameList.contains(androidOrigin)) {
                        np.putInterceptPkgName(androidOrigin);
                        return;
                    } else {
                        np.putInterceptPkgName(smsPkgNameList.get(i));
                        return;
                    }
                }
            }
        }
        // com.tencent.pb 微信电话本
        // com.google.android.dialer 默认拨号
        // com.google.android.talk 环聊
    }

    // 得到所有拨号程序的包名
    private String[] getDialerPkgName(Context context, String intentStr) {
        PackageManager sPackageManager = context.getPackageManager();
        Intent dialerIntent = new Intent(intentStr);
        List<ResolveInfo> intentResolveInfos = sPackageManager.queryIntentActivities(dialerIntent,
                PackageManager.GET_RECEIVERS);
        int size = intentResolveInfos.size();
        if (size < 1) {
            return null;
        }
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = intentResolveInfos.get(i).activityInfo.packageName;
        }
        return result;
    }

    // 得到所有信息程序的包名数组
    private String[] getSmsPkgName(Context context) {
        PackageManager sPackageManager = context.getPackageManager();
        Intent smsIntent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            smsIntent.setAction("android.provider.Telephony.SMS_DELIVER");// 4.4以上
        } else {
            smsIntent.setAction("android.provider.Telephony.SMS_RECEIVED");
        }
        List<ResolveInfo> resolveInfos = sPackageManager.queryBroadcastReceivers(smsIntent,
                PackageManager.GET_RECEIVERS);
        int size = resolveInfos.size();
        if (size < 1) {
            return null;
        }
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = resolveInfos.get(i).activityInfo.packageName;
        }
        return result;
    }

    // 判断是否为系统应用
    private boolean isSystemApp(Context context, String packageName) {
        try {
            PackageInfo mPackageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if ((mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    // 去除数组中的重复
    private List<String> removeDuplicate(String[] strings) {
        List<String> result = new ArrayList<String>();
        for (String str : strings) {
            if (!result.contains(str)) {
                result.add(str);
            }
        }
        return result;
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
