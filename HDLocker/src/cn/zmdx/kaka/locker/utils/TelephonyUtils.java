package cn.zmdx.kaka.locker.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;


public class TelephonyUtils {
    private static final String TAG = "TelephonyUtils";

    public static final String SMS_EXTRA_NAME = "pdus";
    public static final String SMS_EXTRA_FORMAT = "format";
    public static final int OPER_CHINAMOBILE = 0;
    public static final int OPER_CHINAUNICOM = 1;
    public static final int OPER_CHINATELECOM = 2;
    public static final int OPER_OTHER = 3;
    public static final int OPER_NONE = -1;

    private static boolean sIsInit = false;
    private static boolean sHasMobile = false;

    private static synchronized void init(Context context) {
        if (!sIsInit) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
            if (cm == null){
                return;
            }
            NetworkInfo[] allNetworkInfo = cm.getAllNetworkInfo();
            if (allNetworkInfo == null){
                return;
            }
            sIsInit = true;
            for (NetworkInfo ni : allNetworkInfo) {
                int type = ni.getType();
                if (type == ConnectivityManager.TYPE_MOBILE
                        || type == ConnectivityManager.TYPE_MOBILE_DUN
                        || type == ConnectivityManager.TYPE_MOBILE_HIPRI
                        || type == ConnectivityManager.TYPE_MOBILE_MMS
                        || type == ConnectivityManager.TYPE_MOBILE_SUPL) {
                    sHasMobile = true;
                    break;
                }
            }
        }
    }

    /**
     * check this device has mobile module or not
     * @return true if has mobile module
     */
    public static boolean hasMobile(Context context) {
        init(context);
        return sHasMobile;
    }

    public static int getOperator(Context ctx) {
        TelephonyManager telManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager.getSimState() != TelephonyManager.SIM_STATE_READY) {
            return OPER_NONE;
        }
        String operator = telManager.getSimOperator();
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                // China Mobile
                return OPER_CHINAMOBILE;
            } else if (operator.equals("46001") || operator.equals("46006")) {
                // China Unicom
                return OPER_CHINAUNICOM;
            } else if (operator.equals("46003") || operator.equals("46005")) {
                // China Telecom
                return OPER_CHINATELECOM;
            } else {
                return OPER_OTHER;
            }
        }
        return OPER_NONE;
    }

    public static boolean isExistSimCard(Context ctx) {
        TelephonyManager telManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
            return true;
        }
        return false;
    }

    /**
     * Get device ID (IMEI) of the phone.
     * If not available, return an empty string.
     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (deviceId == null) {
            deviceId = "";
        }
        return deviceId;
    }

    public static boolean isAirPlaneMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }

    public static boolean endCall(Context context) {
        return ITelephonyCompat.endCall(context);
    }
}
