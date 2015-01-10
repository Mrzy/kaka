
package cn.zmdx.kaka.locker.meiwen.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;


public class DevicesUtils {
    public static boolean legacyDevices() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1;
    }

    public static boolean modernDevices() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1;
    }

    static int memoryMB = -1;
    public static boolean lowPhysicalMemoryDevices() {
        if (memoryMB == -1) {
            memoryMB = (int) (getPhysicalMemoryKBs() / 1024);
        }
        return (memoryMB < 300);
    }

    static long sPhysicalMemory = 0L;

    public static Long getPhysicalMemoryKBs() {
        // read /proc/meminfo to find MemTotal 'MemTotal: 711480 kB'
        // This operation would complete in fixed time

        if (sPhysicalMemory == 0L) {
            final String PATTERN = "MemTotal:";

            InputStream inStream = null;
            InputStreamReader inReader = null;
            BufferedReader inBuffer = null;

            try {
                inStream = new FileInputStream("/proc/meminfo");
                inReader = new InputStreamReader(inStream);
                inBuffer = new BufferedReader(inReader);

                String s;
                while ((s = inBuffer.readLine()) != null && s.length() > 0) {
                    if (s.startsWith(PATTERN)) {
                        String memKBs = s.substring(PATTERN.length()).trim();
                        memKBs = memKBs.substring(0, memKBs.indexOf(' '));
                        sPhysicalMemory = Long.parseLong(memKBs);
                        break;
                    }
                }
            } catch (Exception e) {
            } finally {
                silentlyClose(inStream);
                silentlyClose(inReader);
                silentlyClose(inBuffer);
            }
        }

        return sPhysicalMemory;
    }

    public static void silentlyClose(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
            }
        }
    }

    public static boolean extremeLowMemoryDevices(Context context) {
        return getHeapSize(context) <= 20;
    }

    public static boolean lowMemoryDevices(Context context) {
        return getHeapSize(context) < 30;
    }

    static int sHeapSize = -1;
    public static int getHeapSize(Context context) {
        if (sHeapSize <= 0) {
            ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(
                    Context.ACTIVITY_SERVICE);
            sHeapSize = am.getMemoryClass();
        }
        return sHeapSize;
    }

    static String deviceDesc = null;
    @SuppressLint("NewApi")
    public static String getDeviceDescription() {
        if (deviceDesc == null) {
            StringBuffer sb = new StringBuffer();

            sb.append('\n');
            sb.append('\t').append("Build.MANUFACTURER\t").append(Build.MANUFACTURER).append('\n');
            sb.append('\t').append("Build.MODEL\t").append(Build.MODEL).append('\n');
            sb.append('\t').append("Build.PRODUCT\t").append(Build.PRODUCT).append('\n');
            sb.append('\t').append("Build.DEVICE\t").append(Build.DEVICE).append('\n');
            sb.append('\t').append("Build.BOARD\t").append(Build.BOARD).append('\n');
            sb.append('\t').append("Build.BRAND\t").append(Build.BRAND).append('\n');
            sb.append('\t').append("Build.CPU_ABI\t").append(Build.CPU_ABI).append('\n');
            sb.append('\t').append("Build.DISPLAY\t").append(Build.DISPLAY).append('\n');
            sb.append('\t').append("Build.FINGERPRINT\t").append(Build.FINGERPRINT).append('\n');
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                sb.append('\t').append("Build.HARDWARE\t").append(Build.HARDWARE).append('\n');
                sb.append('\t').append("Build.RADIO\t").append(Build.RADIO).append('\n');
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                sb.append('\t').append("Build.SERIAL\t").append(Build.SERIAL).append('\n');
            }
            sb.append('\t').append("Build.TAGS\t").append(Build.TAGS).append('\n');
            sb.append('\t').append("Build.TYPE\t").append(Build.TYPE).append('\n');
            sb.append('\t').append("Build.SDK_INT\t").append(Build.VERSION.SDK_INT).append('\n');

            deviceDesc = sb.toString();
        }

        return deviceDesc;
    }

    public static boolean isMeizuM9() {
        // bad example: Meizu M9
        final String fingerprint = Build.FINGERPRINT;
        return fingerprint != null ? (fingerprint.indexOf("meizu_m9") != -1) : false;
    }

    private static Boolean isHuaweiC8812E = null;
    public static boolean isHuaweiC8812E() {
        if (isHuaweiC8812E == null) {
            final String fingerPrint = Build.FINGERPRINT;
            isHuaweiC8812E = fingerPrint != null ? fingerPrint.contains("HuaweiC8812E") : false;
        }
        return isHuaweiC8812E;
    }

    private static Boolean isHuaweiU8825D = null;
    public static boolean isHuaweiU8825D() {
        if (isHuaweiU8825D == null) {
            final String fingerPrint = Build.FINGERPRINT;
            isHuaweiU8825D = fingerPrint != null ? fingerPrint.contains("HuaweiU8825D") : false;
        }
        return isHuaweiU8825D;
    }

    static Boolean isSamsungGalaxyNote = null;
    public static boolean isGalaxyNote() {
        if (isSamsungGalaxyNote == null) {
            final String FINGERPRINT = "samsung/GT-N7000/GT-N7000:2.3.5/GINGERBREAD/ZSKJ6:user/release-keys";
            final String MODEL = "GT-N7000";
            if (Build.FINGERPRINT.equals(FINGERPRINT) || Build.FINGERPRINT.contains(MODEL)) {
                isSamsungGalaxyNote = true;
            } else {
                isSamsungGalaxyNote = false;
            }
        }

        return isSamsungGalaxyNote;
    }

    static Boolean isHtcG14 = null;
    public static boolean isHtcG14() {
        if (isHtcG14 == null) {
            // bad official rom: could not use hardware acc or BubbleTextViews are in trouble
            final String FINGERPRINT = "htccn_chs_cu/htc_pyramid/pyramid:4.0.3/IML74K/357408.14:user/release-keys";
            if (Build.FINGERPRINT.equals(FINGERPRINT)) {
                isHtcG14 = true;
            } else {
                isHtcG14 = false;
            }
        }

        return isHtcG14;
    }

    static Boolean isI9100 = null;
    public static boolean isI9100() {
        if (isI9100 == null) {
            final String FINGERPRINT = "samsung/GT-I9100/GT-I9100:4.0.3/IML74K/ZSLPE:user/release-keys";
            final String MODEL = "GT-I9100";
            if (Build.FINGERPRINT.equals(FINGERPRINT) || Build.FINGERPRINT.contains(MODEL)) {
                isI9100 = true;
            } else {
                isI9100 = false;
            }
        }
        return isI9100;
    }

    static Boolean isZTEModernDevice = null;
    public static boolean isZTEModernDevice() {
        if (isZTEModernDevice == null) {
            final String MODEL = "ZTE";
            if (modernDevices() && Build.FINGERPRINT.contains(MODEL) || Build.MODEL.contains(MODEL)) {
                isZTEModernDevice = true;
            } else {
                isZTEModernDevice = false;
            }
        }
        return isZTEModernDevice;
    }
}
