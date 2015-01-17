
package cn.zmdx.kaka.fast.locker.weather;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import cn.zmdx.kaka.fast.locker.HDApplication;
import cn.zmdx.kaka.fast.locker.utils.HDBLOG;
import cn.zmdx.kaka.fast.locker.BuildConfig;

public class PandoraLocationManager {

    private static PandoraLocationManager INSTANCE = null;

    private LocationManager mLocationManager;

    private static Location mRecentLocation;

    private PandoraLocationManager() {
        mLocationManager = (LocationManager) HDApplication.getContext().getSystemService(
                Context.LOCATION_SERVICE);
    }

    public static PandoraLocationManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PandoraLocationManager();
        }
        return INSTANCE;
    }

    public void registLocationUpdates() {
        boolean isNetworkProvider = false;
        boolean isGpsProvider = false;
        try {
            isNetworkProvider = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            isGpsProvider = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception e) {
            return;
        }

        if (!isNetworkProvider && !isGpsProvider) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("没有可用的provider，注册位置更新监听失败");
            }
            return;
        }

        if (isNetworkProvider) {
            Location loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc == null) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1,
                        mLocaitonListener);
            } else {
                mRecentLocation = loc;
                return;
            }
            return;
        }

        if (isGpsProvider) {
            Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc == null) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
                        mLocaitonListener);
            } else {
                mRecentLocation = loc;
                return;
            }
        }
    }

    public void unRegistLocationUpdates() {
        if (mLocaitonListener != null) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("unRegistLocationUpdates()");
            }
            mLocationManager.removeUpdates(mLocaitonListener);
        }
    }

    /**
     * 获取当前位置
     * 
     * @return 没有位置信息时返回null
     */
    public static Location getRecentLocation() {
        return mRecentLocation;
    }

    private LocationListener mLocaitonListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("onLocationChanged");
            }
            mRecentLocation = location;
            // mLocationManager.removeUpdates(mLocaitonListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("onStatusChanged");
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("onProviderEnabled");
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("onProviderDisabled");
            }
        }
    };
}
