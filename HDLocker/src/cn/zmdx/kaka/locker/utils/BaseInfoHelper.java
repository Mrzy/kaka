
package cn.zmdx.kaka.locker.utils;

import static cn.zmdx.kaka.locker.utils.HDBConfig.LOGE_ENABLED;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class BaseInfoHelper {

    private static final String TAG = "BaseInfoHelper";

    public static String getPkgName(Context context) {
        return context.getApplicationContext().getPackageName();
    }

    private static int mRealScreenHeight = -1;

    @SuppressLint("NewApi")
    public static int getRealHeight(Display display) {
        if (mRealScreenHeight != -1) {
            return mRealScreenHeight;
        }
        Point size = new Point();
        int result = 0;
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
            result = size.y;
        } else {
            try {
                Method getRawH = Display.class.getMethod("getRawHeight");
                result = (Integer) getRawH.invoke(display);
            } catch (Exception e) {
                display.getSize(size);
                result = size.y;
            }
        }
        return result;
    }

    @SuppressLint("NewApi")
    public static int getRealHeight(Context context) {
        if (mRealScreenHeight != -1) {
            return mRealScreenHeight;
        }
        final Display display = getDisplay(context);
        return getRealHeight(display);
    }

    public static String getWifiMac(Context context) {
        try {
            String ret = null;

            WifiManager wifiMgr = (WifiManager) context
                    .getSystemService(android.content.Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if (wifiInfo != null) {
                ret = wifiInfo.getMacAddress();
            }

            return ret;
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the wifiMac info.", e);
            }
            return "";
        }
    }

    public static String getMmcID() {
        // read '/sys/block/mmcblk%d/device/cid'
        // available before SD card mounted, available on user build devices.
        // This operation would complete in fixed time
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            String command = String.format("/system/bin/cat /sys/block/mmcblk%d/device/cid", i);

            try {
                java.lang.Process p = Runtime.getRuntime().exec(command);

                InputStream inStream = p.getInputStream();
                InputStreamReader inReader = new InputStreamReader(inStream);
                BufferedReader inBuffer = new BufferedReader(inReader);

                String s;
                while ((s = inBuffer.readLine()) != null && s.length() > 0) {
                    result.append(s).append(" ");
                }
            } catch (Exception e) {
            }
        }

        return result.toString();
    }

    public static String getFreeMemoryKBs() {
        // read /proc/meminfo to find memfree 'MemFree: 143632 kB'
        // This operation would complete in fixed time
        String command = "/system/bin/cat /proc/meminfo";

        try {
            java.lang.Process p = Runtime.getRuntime().exec(command);

            InputStream inStream = p.getInputStream();
            InputStreamReader inReader = new InputStreamReader(inStream);
            BufferedReader inBuffer = new BufferedReader(inReader);

            String s;
            while ((s = inBuffer.readLine()) != null && s.length() > 0) {
                if (s.startsWith("MemFree:")) {
                    return s;
                }
            }
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the Mem info.", e);
            }
        }
        return "unknown";
    }

    public static String getSN(Context context) {
        try {
            String ret = "";
            ret = Helper.getSystemProperty("ro.serialno");
            if (!TextUtils.isEmpty(ret)) {
                return ret;
            }

            ret = Helper.getSystemProperty("ro.hw.dxos.SN");
            return ret;
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the sn info.", e);
            }
            return "";
        }
    }

    public static String getHwID(Context context) {
        try {
            return Build.MODEL;
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the hw info.", e);
            }
            return "";
        }
    }

    public static String getRam(Context context) {
        try {
            File root = Environment.getDataDirectory();
            StatFs sf = new StatFs(root.getPath());

            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();

            String ret = Long.toString(blockSize * blockCount);
            return ret;
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the hw info.", e);
            }
            return "";
        }

    }

    public static String getResolution(Context context) {
        try {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);

            String ret;
            if (metrics.widthPixels < metrics.heightPixels) {
                ret = metrics.widthPixels + "*" + metrics.heightPixels;
            } else {
                ret = metrics.heightPixels + "*" + metrics.widthPixels;
            }
            return ret;
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the hw info.", e);
            }
            return "";
        }
    }

    private static DisplayMetrics getMetrics(Context context) {
        try {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            return metrics;
        } catch (Exception e) {
            if (LOGE_ENABLED)
                Log.e(TAG, "Failed to getMetrics!", e);
        }
        return null;
    }

    @SuppressLint("NewApi")
    private static Display getDisplay(Context context) {
        try {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            return display;
        } catch (Exception e) {
            if (LOGE_ENABLED)
                Log.e(TAG, "Failed to getDisplay!", e);
        }
        return null;
    }

    public static String getHeight(Context context) {
        try {
            DisplayMetrics metrics = getMetrics(context);
            if (metrics != null)
                return String.valueOf(metrics.heightPixels);
        } catch (Exception e) {
            if (LOGE_ENABLED)
                Log.e(TAG, "Failed to get height info!", e);
        }
        return "";
    }

    private static int sScreenHeight = 0;

    public static int getWidth(Context context) {
        if (sScreenHeight != 0) {
            return sScreenHeight;
        }
        try {
            DisplayMetrics metrics = getMetrics(context);
            if (metrics != null) {
                sScreenHeight = metrics.widthPixels;
            }
            return sScreenHeight;
        } catch (Exception e) {
            if (LOGE_ENABLED)
                Log.e(TAG, "Failed to get width info!", e);
        }
        return sScreenHeight;
    }

    @Deprecated
    public static String getPkgVersion(Context context) {
        return getPkgVersionName(context);
    }

    public static String getPkgVersionName(Context context) {
        try {
            String pkgName = context.getPackageName();
            PackageInfo manager = context.getPackageManager().getPackageInfo(pkgName, 0);
            if (manager != null) {
                return manager.versionName;
            }
        } catch (Exception e) {
            if (LOGE_ENABLED)
                Log.e(TAG, "Failed to get PkgVersionName!", e);
        }
        return "";
    }

    public static int getPkgVersionCode(Context context) {
        try {
            String pkgName = context.getPackageName();
            PackageInfo manager = context.getPackageManager().getPackageInfo(pkgName, 0);
            if (manager != null) {
                return manager.versionCode;
            }
        } catch (Exception e) {
            if (LOGE_ENABLED)
                Log.e(TAG, "Failed to get PkgVersionCode!", e);
        }
        return -1;
    }

    public static String getManufacturer(Context context) {
        try {
            return Build.MANUFACTURER;
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the hw info.", e);
            }

            return "";
        }
    }

    public static String getModel(Context context) {
        try {
            return Build.MODEL;
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "failed to get the model info.", e);
            }
            return "";
        }
    }

    public static String getIMEI(Context context) {
        try {
            TelephonyManager teleMgr;
            teleMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return teleMgr.getDeviceId();
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the hw info.", e);
            }
            return "";
        }
    }

    public static String getIMSI(Context context) {
        try {
            TelephonyManager teleMgr;
            teleMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return teleMgr.getSubscriberId();
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the IMSI info!", e);
            }
        }
        return "";
    }

    public static String getCarrier(Context context) {
        try {
            TelephonyManager teleMgr;
            teleMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return teleMgr.getNetworkOperator();
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the hw info.", e);
            }
            return "";
        }
    }

    public static String getAndroidVersion(Context context) {
        try {
            return String.valueOf(Build.VERSION.SDK_INT);
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the androidVersion info.", e);
            }
            return "";
        }
    }

    public static String getDpi(Context context) {
        try {
            DisplayMetrics metrics = getMetrics(context);
            if (metrics != null)
                return Integer.toString(metrics.densityDpi);
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the dpi info.", e);
            }
        }
        return "";
    }

    public static String getLocale(Context context) {
        try {
            return context.getResources().getConfiguration().locale.toString();
        } catch (Exception e) {
            if (LOGE_ENABLED)
                Log.e(TAG, "failed to getLocale Info!", e);
        }
        return "";
    }

    public static String getNetworkType(Context context) {
        try {
            context = context.getApplicationContext();
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null) {
                String type = netInfo.getTypeName();
                if ("mobile".equals(type.toLowerCase())) {
                    type = netInfo.getSubtypeName();
                }
                return type;
            }
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the netWorkType info.", e);
            }
        }
        return "none";
    }

    public static String getFingerPrint() {
        try {
            return Build.FINGERPRINT;
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the fingerPrint info.", e);
            }
            return "";
        }
    }

    private static String getMd5ByBD(PackageInfo packageinfo) {
        if (packageinfo == null)
            return null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(packageinfo.signatures[0].toCharsString().getBytes());
            byte[] b = md.digest();
            char[] HEXCHAR = {
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
            };
            StringBuilder sb = new StringBuilder(b.length * 2);
            for (int i = 0; i < b.length; i++) {
                sb.append(HEXCHAR[(b[i] & 0xf0) >>> 4]);
                sb.append(HEXCHAR[b[i] & 0x0f]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to getMD5!", e);
            }
            return null;
        }
    }

    private static String createSignInt(String md5) {
        if (md5 == null || md5.length() < 32)
            return "-1";
        String sign = md5.substring(8, 8 + 16);
        long lowDigits = 0;
        long highDigits = 0;
        String s = "";
        for (int i = 0; i < 8; i++) {
            lowDigits *= 16;
            s = sign.substring(i, i + 1);
            lowDigits += Integer.parseInt(s, 16);
        }
        for (int i = 8; i < sign.length(); i++) {
            highDigits *= 16;
            s = sign.substring(i, i + 1);
            highDigits += Integer.parseInt(s, 16);
        }
        long id = (lowDigits + highDigits) & 0xFFFFFFFFL;
        return String.valueOf(id);
    }

    public static String getSignature(Context context) {
        try {
            String packageName = context.getApplicationInfo().packageName;
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            String md5 = getMd5ByBD(packageInfo);
            return createSignInt(md5);
        } catch (Exception e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get signature!", e);
            }
            return null;
        }
    }

    /**
     * 
     * @return year + month + day
     */
    public static String getCurrentDate() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return "" + year + "" + month + "" + day;
    }
}
