
package cn.zmdx.kaka.locker.notification.guide;

import android.content.Context;
import android.os.Build;
import cn.zmdx.kaka.locker.notification.NotificationInfo;
import cn.zmdx.kaka.locker.notification.NotificationPreferences;
import cn.zmdx.kaka.locker.notification.PandoraNotificationFactory;
import cn.zmdx.kaka.locker.notification.PandoraNotificationService;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

public class NotificationGuideHelper {

    public static NotificationInfo getNextGuide(Context context) {
        if (!checkDeviceNotificationServiceAvailable(context)) {
            return null;
        }

        // 如果是第一次打开锁屏，会提示解锁及下拉的引导，避免过多引导，这里先不显示通知引导
        if (PandoraConfig.newInstance(context).getLockScreenTimes() < 1) {
            return null;
        }

        int preProgress = getGuideProgress(context);
        switch (preProgress) {
            case 0:// 通知功能说明，提示双击移除
                return PandoraNotificationFactory.createGuideRemoveNotification();
            case 1:// 教学开启通知教程
                long lastPromptTime = NotificationPreferences.getInstance(context)
                        .getLastShowGuideOpenNotificationServiceTime();
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastPromptTime > 24 * 60 * 60 * 1000) {
                    NotificationPreferences.getInstance(context)
                            .saveLastShowGuideOpenNotificationServiceTime(currentTime);
                    return PandoraNotificationFactory.createGuideOpenNotifyPermissionNotification();
                }
                return null;
            default:
        }
        return null;
    }

    public static boolean checkDeviceNotificationServiceAvailable(Context context) {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static void recordGuideProgress(Context context) {
        int preProgress = getGuideProgress(context);
        int curProgress = preProgress + 1;
        PandoraConfig.newInstance(context).saveNotificationGuideProgress(Math.min(curProgress, 2));
    }

    private static int getGuideProgress(Context context) {
        return PandoraConfig.newInstance(context).getNotificationGuideProgress();
    }

    public static boolean hasAlreadyPromptHideNotificationMsg(Context context) {
        return PandoraConfig.newInstance(context).hasAlreadyPromptHideNotificationMsg();
    }

    public static void markAlreadyPromptHideNotificationMsg(Context context) {
        PandoraConfig.newInstance(context).markAlreadyPromptHideNotificationMsg();
    }
}
