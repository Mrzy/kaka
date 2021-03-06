
package cn.zmdx.kaka.fast.locker.notification;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import cn.zmdx.kaka.fast.locker.BuildConfig;
import cn.zmdx.kaka.fast.locker.utils.HDBLOG;

public class NotificationPreferences {
    private static final String SP_NAME_NOTIFICATION = "notification_sp";

    private static final String KEY_ACTIVE_NOTIFICATIONS = "kan";

    private static final String KEY_LAST_PULL_CUSTOM_NOTIFICATION_DATA_TIME = "klpcnd";

    private static final String KEY_PULL_CUSTOM_NOTIFICATION_LAST_MODIFIED = "kpcnlm";

    private static final String KEY_CUSTOM_NOTIFICATION_DATA = "kcnd";

    private static final String KEY_FLAG_REMOVED_NOTIFICATION = "kfrn";

    private static final String KEY_FLAG_OPENED_NOTIFICATION = "kfon";

    private Context mContext;

    private SharedPreferences mSp;

    private static NotificationPreferences INSTANCE;

    private Set<String> mActiveNotificationCache;

    private NotificationPreferences(Context context) {
        mContext = context;
        mSp = context.getSharedPreferences(SP_NAME_NOTIFICATION, Context.MODE_PRIVATE);
        mActiveNotificationCache = getInterceptPkgNames();
    }

    public synchronized static NotificationPreferences getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NotificationPreferences(context);
        }
        return INSTANCE;
    }

    // FIXME 需要将包名做hash后再存储
    public void putInterceptPkgName(String pkgName) {
        if (mActiveNotificationCache.add(pkgName)) {
            write(mActiveNotificationCache);
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("ActiveNotificationCache put:" + pkgName);
            }
        }
    }

    public void removeInterceptPkgName(String pkgName) {
        if (mActiveNotificationCache.remove(pkgName)) {
            write(mActiveNotificationCache);
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("ActiveNotificationCache remove:" + pkgName);
            }
        }
    }

    private void write(Set<String> interceptPkgs) {
        mSp.edit().clear().putStringSet(KEY_ACTIVE_NOTIFICATIONS, interceptPkgs).commit();
    }

    public Set<String> getInterceptPkgNames() {
        if (mActiveNotificationCache == null) {
            mActiveNotificationCache = mSp.getStringSet(KEY_ACTIVE_NOTIFICATIONS,
                    new HashSet<String>());
        }
        return mActiveNotificationCache;
    }

    public boolean isIntercepted(String pkgName) {
        return mActiveNotificationCache.contains(pkgName);
    }

    public long getLastPullCustomNotificationTime() {
        return mSp.getLong(KEY_LAST_PULL_CUSTOM_NOTIFICATION_DATA_TIME, 0);
    }

    public void saveLastPullCustomNotificationTime(long current) {
        mSp.edit().putLong(KEY_LAST_PULL_CUSTOM_NOTIFICATION_DATA_TIME, current).commit();
    }

    public long getPullCustomNotificationLastModified() {
        return mSp.getLong(KEY_PULL_CUSTOM_NOTIFICATION_LAST_MODIFIED, 0);
    }

    public void savePullCustomNotificationLastModified(long lastModified) {
        mSp.edit().putLong(KEY_PULL_CUSTOM_NOTIFICATION_LAST_MODIFIED, lastModified).commit();
    }

    public boolean isAlreadyRemovedNotification() {
        return mSp.getBoolean(KEY_FLAG_REMOVED_NOTIFICATION, false);
    }

    public void markAlreadyRemovedNotification() {
        mSp.edit().putBoolean(KEY_FLAG_REMOVED_NOTIFICATION, true).commit();
    }

    public boolean isAlreadyOpenedNotification() {
        return mSp.getBoolean(KEY_FLAG_OPENED_NOTIFICATION, false);
    }

    public void markAlreadyOpenedNotification() {
        mSp.edit().putBoolean(KEY_FLAG_OPENED_NOTIFICATION, true).commit();
    }
}
