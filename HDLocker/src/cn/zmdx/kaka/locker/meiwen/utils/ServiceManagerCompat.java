package cn.zmdx.kaka.locker.meiwen.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.IBinder;

public class ServiceManagerCompat {
    private static final String TAG = "ServiceManagerCompat";

    private static final String CLASSNAME_SERVICE_MANAGER= "android.os.ServiceManager";

    private static Class<?> sServiceManagerClass;
    private static Method sGetServiceMethod;

    static {
        try {
            sServiceManagerClass = Class.forName(CLASSNAME_SERVICE_MANAGER, false, Thread.currentThread().getContextClassLoader());
            sGetServiceMethod = sServiceManagerClass.getMethod("getService",  new Class[] { String.class});
        } catch (ClassNotFoundException e) {
            HDBLOG.logE("unexpected", e);
        } catch (NoSuchMethodException e) {
            HDBLOG.logE("unexpected", e);
        }
    }

    public static IBinder getService(Object name) {
        if (sGetServiceMethod != null) {
            try {
                Method localMethod = sGetServiceMethod;
                Object[] arrayOfObject = new Object[] { name };
                Object ret = localMethod.invoke(null, arrayOfObject);
                return (IBinder) ret;
            } catch (IllegalAccessException e) {
                HDBLOG.logE("unexpected", e);
            } catch (InvocationTargetException e) {
                HDBLOG.logE("unexpected", e);
            } catch (Exception e) {
                HDBLOG.logE("unexpected", e);
            }
        }
        return null;
    }
}
