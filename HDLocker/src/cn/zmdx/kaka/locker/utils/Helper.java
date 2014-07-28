
package cn.zmdx.kaka.locker.utils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.util.Log;

public class Helper {
    public static boolean LOG_ENABLED = false;

    public static boolean LOGE_ENABLED = LOG_ENABLED;
    public static boolean LOGI_ENABLED = LOG_ENABLED;
    public static boolean LOGD_ENABLED = LOG_ENABLED;

    private static final String TAG = "DXBase.Helper";

    public static String getSystemProperty(String name) {
        Class<?> c;
        try {
            c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            return (String) get.invoke(c, name);
        } catch (ClassNotFoundException e) {
            if (LOG_ENABLED) {
                Log.w(TAG, "getSystemProperty has ClassNotFoundException", e);
            }
        } catch (SecurityException e) {
            if (LOG_ENABLED) {
                Log.w(TAG, "getSystemProperty has SecurityException", e);
            }
        } catch (NoSuchMethodException e) {
            if (LOG_ENABLED) {
                Log.w(TAG, "getSystemProperty has NoSuchMethodException", e);
            }
        } catch (IllegalArgumentException e) {
            if (LOG_ENABLED) {
                Log.w(TAG, "getSystemProperty has IllegalArgumentException", e);
            }
        } catch (IllegalAccessException e) {
            if (LOG_ENABLED) {
                Log.w(TAG, "getSystemProperty has IllegalAccessException", e);
            }
        } catch (InvocationTargetException e) {
            if (LOG_ENABLED) {
                Log.w(TAG, "getSystemProperty has InvocationTargetException", e);
            }
        }
        return "";
    }

    public static String readFile(String name) {
        BufferedReader in = null;
        try {
            FileReader mFileReader = new FileReader(new File(name));
            in = new BufferedReader(mFileReader);
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = in.readLine()) != null) {
                sb.append(s);
                sb.append("\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            if (LOGE_ENABLED) {
                Log.w(TAG, name + " FileNotFoundException.");
            }
        } catch (IOException e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "IOException.", e);
            }
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    if (LOGE_ENABLED) {
                        Log.e(TAG, "IOException.", e);
                    }
                }
        }
        return "";
    }
}
