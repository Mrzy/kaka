
package cn.zmdx.kaka.locker.daemon;

import java.io.Closeable;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.Socket;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class Utilities {
    static Context sApplicationContext = null;

    public static boolean ensureDirectory(String dirPath) {
        try {
            File dir = new File(dirPath);
            if (dir.exists() && !dir.isDirectory()) {
                dir.delete();
            }
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            Log.e("Utilities", "ensureDirectory - " + e);
            return false;
        }
        return true;
    }

    public static void initEnvironment(Context ctx) {
        sApplicationContext = ctx;
    }

    public static Context getApplicationContext() {
        if (sApplicationContext == null) {
            throw new java.lang.IllegalStateException("Common library is used before initialize!");
        }

        return sApplicationContext;
    }

    public static Object getSystemService(String name) {
        if (sApplicationContext == null) {
            throw new java.lang.IllegalStateException("Common library is used before initialize!");
        }

        return sApplicationContext.getSystemService(name);
    }

    public static void setVmMemoryPolicy() {
        // // set minumum heap size to 12MB
        // VMRuntime.getRuntime().setMinimumHeapSize(12 * 1024 * 1024);
        // // set expected heap utilization to 85%, default is 50%; reduce GC frequency
        // VMRuntime.getRuntime().setTargetHeapUtilization(0.85f);

        try {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            Class<?> clsVMRuntime;

            clsVMRuntime = cl.loadClass("dalvik.system.VMRuntime");

            Method methodGetRuntime = clsVMRuntime.getMethod("getRuntime", new Class<?>[] {});
            Method methodSetMinimumHeapSize = clsVMRuntime.getMethod("setMinimumHeapSize", new Class<?>[] {
                long.class
            });
            Method methodSetTargetHeapUtilization = clsVMRuntime.getMethod("setTargetHeapUtilization", new Class<?>[] {
                float.class
            });

            final int minHeap = Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 ? 8 : 12;
            Object objRuntime = methodGetRuntime.invoke(null, (Object[]) null);
            methodSetMinimumHeapSize.invoke(objRuntime, minHeap * 1024 * 1024);
            methodSetTargetHeapUtilization.invoke(objRuntime, 0.85f);

        } catch (ClassNotFoundException e) {
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static void silentlyClose(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
            }
        }
    }

    public static void silentlyClose(Cursor c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
            }
        }
    }

    public static void silentlyClose(SQLiteDatabase c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
            }
        }
    }

    public static void silentlyClose(Socket c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
            }
        }
    }

    public static void silentlyClose(DatagramSocket c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
            }
        }
    }

    public static void silentlyClose(AssetFileDescriptor afd) {
        if (afd != null) {
            try {
                afd.close();
            } catch (Throwable w) {
            }
        }
    }

    public static boolean supportVelocityWithParam() {
        return Build.VERSION.SDK_INT >= 8;
    }
}
