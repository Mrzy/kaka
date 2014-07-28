package cn.zmdx.kaka.locker.utils;

import java.lang.reflect.Method;

public class UserIdCompat {
    private static final String TAG = "UserIdCompat";

    private static Class<?> sUserIdClass = null;
    private static Method sMyUserIdMethod = null;
    private static Integer sMyUserId = null;

    static {
        try {
            sUserIdClass = Class.forName("android.os.UserHandle");
        } catch (ClassNotFoundException e) {
            HDBLOG.logE("UserHandle not found, try 4.1 api 16", e);
            try {
                sUserIdClass = Class.forName("android.os.UserId");
            } catch (ClassNotFoundException e1) {
                HDBLOG.logE("Fallback api failed", e1);
                sUserIdClass = null;
            }
        }
        if (sUserIdClass != null) {
            try {
                sMyUserIdMethod = sUserIdClass.getMethod("myUserId");
            } catch (NoSuchMethodException e) {
                HDBLOG.logE("method not found: " + e);
                sMyUserIdMethod = null;
            }
        }
    }

    public static int myUserId() {
        if (sMyUserId != null) {
            return sMyUserId;
        }

        sMyUserId = Integer.valueOf(0);
        if (sMyUserIdMethod != null) {
            try {
                sMyUserId = (Integer) sMyUserIdMethod.invoke(null, (Object[]) null);
            } catch (Exception e) {
                HDBLOG.logE("failed to get myUserId" + e);
            }
        }
        return sMyUserId;
    }
}
