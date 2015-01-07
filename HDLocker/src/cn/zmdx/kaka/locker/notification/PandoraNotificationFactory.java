
package cn.zmdx.kaka.locker.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.MainSettingsActivity;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;

public class PandoraNotificationFactory {

    public static final int ID_CUSTOM_NOTIFICATION_OPEN_PERMISSION = 83568;

    public static final int ID_CUSTOM_NOTIFICATION_GUIDE_OPENDETAIL = 83569;

    public static final int ID_CUSTOM_NOTIFICATION_GUIDE_REMOVE = 83570;

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

    public static NotificationInfo createCustomNotification(NotificationEntity entity) {
        NotificationInfo ni = new NotificationInfo();
        String iconStr = entity.getIcon();
        if (!TextUtils.isEmpty(iconStr)) {
            try {
                byte[] iconByte = iconStr.getBytes();
                byte[] decodeResult = Base64.decode(iconByte, Base64.DEFAULT);
                Bitmap icon = BitmapFactory.decodeByteArray(decodeResult, 0, decodeResult.length);
                ni.setLargeIcon(icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ni.setTitle(entity.getTitle());
        ni.setContent(entity.getContent());
        ni.setId(entity.getId());
        ni.setCloudId(entity.getCloudId());
        ni.setType(NotificationInfo.NOTIFICATION_TYPE_CUSTOM);
        ni.setPostTime(System.currentTimeMillis());
        Intent intent = null;
        if (!TextUtils.isEmpty(entity.getTargetApp())) {
            try {
                intent = new Intent(HDApplication.getContext(),
                        Class.forName(entity.getTargetApp()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            if (!TextUtils.isEmpty(entity.getTargetUrl())) {
                Uri uri = Uri.parse(entity.getTargetUrl());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(HDApplication.getContext(),
                    entity.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ni.setPendingIntent(pendingIntent);
        }
        return ni;
    }

    public static NotificationInfo createTestNotification() {
        // TODO
        return null;
    }

    public static NotificationInfo createGuideOpenNotifyPermissionNotification() {
        Context context = HDApplication.getContext();
        final NotificationInfo ni = new NotificationInfo();
        ni.setId(ID_CUSTOM_NOTIFICATION_OPEN_PERMISSION);
        ni.setTitle(context.getString(R.string.notify_guide_openpermission_title));
        ni.setContent(context.getString(R.string.notify_guide_openpermission_content));
        ni.setType(NotificationInfo.NOTIFICATION_TYPE_CUSTOM);
        Intent intent = PandoraUtils.getReadNotificationPermissionIntent(context);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        ni.setPendingIntent(pi);
        ni.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.notification_custom_youhua_icon));
        return ni;
    }

    public static NotificationInfo createGuideOpenNotificationDetail() {
        Context context = HDApplication.getContext();
        final NotificationInfo ni = new NotificationInfo();
        ni.setId(ID_CUSTOM_NOTIFICATION_GUIDE_OPENDETAIL);
        ni.setTitle(context.getString(R.string.notify_guide_opendetail_title));
        ni.setContent(context.getString(R.string.notify_guide_opendetail_content));
        ni.setType(NotificationInfo.NOTIFICATION_TYPE_CUSTOM);
        ni.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.notification_custom_youhua_icon));
        Intent intent = new Intent(context, MainSettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        ni.setPendingIntent(pi);
        return ni;
    }

    public static NotificationInfo createGuideRemoveNotification() {
        Context context = HDApplication.getContext();
        final NotificationInfo ni = new NotificationInfo();
        ni.setId(ID_CUSTOM_NOTIFICATION_GUIDE_REMOVE);
        ni.setTitle(context.getString(R.string.notify_guide_remove_title));
        ni.setContent(context.getString(R.string.notify_guide_remove_content));
        ni.setType(NotificationInfo.NOTIFICATION_TYPE_CUSTOM);
        ni.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.notification_custom_remove_icon));
        return ni;
    }

    public static NotificationInfo createGuideOpenPandoraSettingsNotification() {

        return null;
    }
}
