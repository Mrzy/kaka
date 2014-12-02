
package cn.zmdx.kaka.locker.utils;

import java.util.WeakHashMap;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class HDBNetworkState {
    private static boolean LOGV = HDBConfig.SHOULD_LOG && false;

    static ConnectivityManager sCM;
    static NetworkInfo sNetworkInfo = null;

    static WeakHashMap<NetworkStateTracker, Object> sNetworkStateListeners = new WeakHashMap<HDBNetworkState.NetworkStateTracker, Object>();

    public static interface NetworkStateTracker {
        public void onNetworkStateChange(NetworkInfo info);
    }

    static HDBEventSource.IntentListener sEventSource = new HDBEventSource.IntentListener() {
        @SuppressWarnings("deprecation")
        @Override
        public void onIntentArrival(Intent intent) {
            NetworkInfo ni = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if (ni == null) {
                ni = sCM.getActiveNetworkInfo();
            }

            if (ni != null) {
                // ensure sNetworkInfo never null
                sNetworkInfo = ni;
            }

            final NetworkInfo info = HDBNetworkState.sNetworkInfo;

            synchronized (sNetworkStateListeners) {
                for (final NetworkStateTracker listener : sNetworkStateListeners.keySet()) {
                    if (listener != null) {
                        HDBThreadUtils.runOnWorkerWithPriority(new Runnable() {
                            public void run() {
                                listener.onNetworkStateChange(info);
                            }
                        });
                    }
                }
            }
        }
    };

    public synchronized static void init(Context ctx) {
        sCM = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        sNetworkInfo = sCM.getActiveNetworkInfo();

        if (LOGV && sNetworkInfo == null) {
            HDBLOG.logE("Warning: could not get network info from ConnectivityManager, app might crash");
        }

        HDBEventSource.registerEventListener(sEventSource, ConnectivityManager.CONNECTIVITY_ACTION);
    }

    synchronized static void finish(Context ctx) {
        HDBEventSource.unregisterEventListener(sEventSource);
    }

    public synchronized static NetworkInfo getNetworkState() {
        return sNetworkInfo;
    }

    public static void registerNetworkStateListener(NetworkStateTracker l) {
        synchronized (sNetworkStateListeners) {
            sNetworkStateListeners.put(l, null);
        }
    }

    public static void unregisterNetworkStateListener(NetworkStateTracker l) {
        synchronized (sNetworkStateListeners) {
            sNetworkStateListeners.remove(l);
        }
    }

    public static boolean isNetworkAvailable() {
        final NetworkInfo info = HDBNetworkState.getNetworkState();
        return info != null && info.isConnected() && info.isAvailable();
    }

    public static boolean isWifiNetwork() {
        final NetworkInfo info = HDBNetworkState.getNetworkState();
        return info != null && info.isConnected() && info.isAvailable()
                && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean is2GNetwork() {
        final NetworkInfo info = HDBNetworkState.getNetworkState();
        if (info != null && info.isConnected() && info.isAvailable()
                && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            switch (info.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return true;
                default:
                    return false;
            }
        } else {
            // not connected? treat as not
            return false;
        }
    }

    public static boolean is3GNetwork() {
        final NetworkInfo info = HDBNetworkState.getNetworkState();
        if (info != null && info.isConnected() && info.isAvailable()
                && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            switch (info.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false;
                default:
                    return true;
            }
        } else {
            // not connected? treat as not
            return false;
        }
    }

    public static boolean isNeedProxy() {
        NetworkInfo networkInfo = HDBNetworkState.getNetworkState();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                && android.net.Proxy.getDefaultHost() != null && android.net.Proxy.getDefaultPort() > 0) {
            return true;
        }
        return false;
    }

    public static boolean isWAP() {
        final NetworkInfo info = HDBNetworkState.getNetworkState();
        if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            final String currentAPN = info.getExtraInfo();
            // LOG.logI("currentAPN: " + currentAPN);
            if (!TextUtils.isEmpty(currentAPN)) {
                // cmwap / uniwap / 3gwap / ctwap
                return currentAPN.contains("wap");
            }
        }
        return false;
    }
}
