
package cn.zmdx.kaka.locker.weather;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class PandoraLocationManager {

    private static PandoraLocationManager INSTANCE = null;

    private LocationManager mLocationManager;

    private static Location mRecentLocation;

    private PandoraLocationManager() {
        mLocationManager = (LocationManager) HDApplication.getInstannce().getSystemService(
                Context.LOCATION_SERVICE);
    }

    public static PandoraLocationManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PandoraLocationManager();
        }
        return INSTANCE;
    }

    public void registLocationUpdates() {
        boolean isNetworkProvider = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGpsProvider = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isNetworkProvider && !isGpsProvider) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("没有可用的provider，注册位置更新监听失败");
            }
            return;
        }
        if (isGpsProvider) {
            Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mRecentLocation = loc;
            if (loc == null)
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10 * 60000, 1000,
                        mLocaitonListener);
            return;
        }

        if (isNetworkProvider) {
            Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mRecentLocation = loc;
            if (loc == null)
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 60000, 1000,
                        mLocaitonListener);
            return;
        }
    }

    public void unRegistLocationUpdates() {
        if (mLocaitonListener != null) {
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
            mLocationManager.removeUpdates(mLocaitonListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };
}
