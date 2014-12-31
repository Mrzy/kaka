
package cn.zmdx.kaka.locker.notification;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationPreferences {
    private static final String SP_NAME_NOTIFICATION = "notification_sp";

    private Context mContext;

    private SharedPreferences mSp;

    private static NotificationPreferences INSTANCE;

    private NotificationPreferences(Context context) {
        mContext = context;
        mSp = context.getSharedPreferences(SP_NAME_NOTIFICATION, Context.MODE_PRIVATE);
    }

    public static NotificationPreferences getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NotificationPreferences(context);
        }
        return INSTANCE;
    }

    public void putInterceptPkgName(String pkgName) {
        // TODO
    }

    public void removeInterceptPkgName(String pkgName) {
        // TODO
    }

    public Set<String> getInterceptPkgNames() {
        // TODO
        return null;
    }

    public boolean isIntercepted(String pkgName) {
        // TODO
        return false;
    }

}
