
package cn.zmdx.kaka.locker.notification;

import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.StatusBarNotification;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

public class NotificationInterceptor extends Handler {

    protected static final int MSG_NOTIFICATION_POST = 0;

    protected static final int MSG_NOTIFICATION_REMOVED = 1;

    private static final int MSG_CUSTOM_NOTIFICATION_POST = 2;

    private static final int MSG_CUSTOM_NOTIFICATION_REMOVED = 3;

    private static final int MSG_PULL_CUSTOM_NOTIFICATION_DATA = 4;

    private static NotificationInterceptor INSTANCE;

    private Context mContext;

    private Set<StatusBarNotification> mNotificationSet = new HashSet<StatusBarNotification>();

    private INotificationListener mListener;

    public static final int NOTIFICATION_TYPE_SYSTEM = 1;

    public static final int NOTIFICATION_TYPE_CUSTOM = 2;

    public interface INotificationListener {
        void onPosted(NotificationInfo info);

        void onRemoved(int notifyId);
    }

    private NotificationInterceptor(Context context, Looper looper) {
        super(looper);
        mContext = context;
    }

    public static NotificationInterceptor getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NotificationInterceptor(context, HDBThreadUtils.getWorkerLooper());
        }
        return INSTANCE;
    }

    public void setNotificationListener(INotificationListener listener) {
        mListener = listener;
    }

    @SuppressLint("NewApi")
    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case MSG_NOTIFICATION_POST:
                if (!(msg.obj instanceof StatusBarNotification)) {
                    return;
                }
                final StatusBarNotification sbn = (StatusBarNotification) msg.obj;
                if (!checkIntercept(sbn.getPackageName()) || sbn == null) {
                    return;
                }

                if (mListener != null) {
                    Bundle bundle = sbn.getNotification().extras;
                    String title = bundle.getString("android.title");
                    String content = bundle.getString("android.text");
                    Bitmap largeIcon = (Bitmap) bundle.getParcelable("android.largeIcon");
                    long postTime = sbn.getPostTime();

                    final NotificationInfo ni = new NotificationInfo();
                    ni.setLargeIcon(largeIcon);
                    ni.setTitle(title);
                    ni.setContent(content);
                    try {
                        Drawable smallIcon = mContext.getPackageManager().getApplicationIcon(
                                sbn.getPackageName());
                        if (smallIcon != null) {
                            ni.setSmallIcon(smallIcon);
                        }
                    } catch (NameNotFoundException e) {
                    }
                    ni.setPostTime(postTime);
                    ni.setId(sbn.getId());
                    ni.setPkg(sbn.getPackageName());
                    ni.setTag(sbn.getTag());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ni.setKey(sbn.getKey());
                    }
                    ni.setType(NOTIFICATION_TYPE_SYSTEM);
                    ni.setPendingIntent(sbn.getNotification().contentIntent);
                    HDBThreadUtils.runOnUi(new Runnable() {

                        @Override
                        public void run() {
                            mListener.onPosted(ni);
                        }
                    });
                }
                break;
            case MSG_NOTIFICATION_REMOVED:
                if (!(msg.obj instanceof StatusBarNotification)) {
                    return;
                }
                final StatusBarNotification sbn1 = (StatusBarNotification) msg.obj;
                if (!checkIntercept(sbn1.getPackageName()) || sbn1 == null) {
                    return;
                }

                if (mListener != null) {
                    HDBThreadUtils.runOnUi(new Runnable() {

                        @Override
                        public void run() {
                            mListener.onRemoved(sbn1.getId());
                        }
                    });
                }
                break;
            case MSG_CUSTOM_NOTIFICATION_POST:

                break;
            case MSG_CUSTOM_NOTIFICATION_REMOVED:

                break;
            case MSG_PULL_CUSTOM_NOTIFICATION_DATA:

                break;
            default:

        }
    };

    /**
     * 检查是否拦截指定包名的应用
     * 
     * @param pkgName
     * @return 如果pkgName是拦截的应用，返回true，否则返回false
     */
    private boolean checkIntercept(String pkgName) {
        // TODO
        return true;
    }

    /**
     * 检查拦截通知功能是否可用。只有android4.3及以上设备才可用
     * 
     * @return
     */
    public boolean isDeviceAvailable() {
        // TODO
        return false;
    }

    public Set<StatusBarNotification> getActiveNotifications() {
        return mNotificationSet;
    }

    public Set<String> getInterceptPackages() {
        // TODO
        return null;
    }

    public void cancelIntercept(String pkgName) {
        // TODO
    }

    public void addIntercept(String pkgName) {

    }
}
