
package cn.zmdx.kaka.locker.notification;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;

public class PandoraNotificationFactory {

    public static NotificationInfo createCustomNotification(String title, String content,
            Bitmap largeIcon, Drawable smallIcon, String tag, PendingIntent contentIntent) {
        if (TextUtils.isEmpty(title)) {
            if (BuildConfig.DEBUG) {
                throw new NullPointerException("title must not be null");
            }
        }

        final NotificationInfo ni = new NotificationInfo();
        ni.setPostTime(System.currentTimeMillis());
        ni.setId(Long.valueOf(System.currentTimeMillis()).hashCode());
        ni.setTag(tag);
        ni.setTitle(title);
        ni.setContent(content);
        ni.setLargeIcon(largeIcon);
        ni.setSmallIcon(smallIcon);
        ni.setType(NotificationInfo.NOTIFICATION_TYPE_CUSTOM);
        ni.setPendingIntent(contentIntent);
        return ni;
    }

    public static NotificationInfo createTestNotification() {
        // TODO
        return null;
    }

    public static NotificationInfo createGuideOpenNotifyPermissionNotification() {
        // TODO
        return null;
    }

    public static NotificationInfo createGuideOpenPandoraSettingsNotification() {
        // TODO
        return null;
    }
}
