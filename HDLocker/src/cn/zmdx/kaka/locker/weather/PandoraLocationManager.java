
package cn.zmdx.kaka.locker.weather;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class PandoraLocationManager {
    private static final String TAG = "PandoraLocationManager";

    private static PandoraLocationManager INSTANCE = null;

    private LocationManager mLocationManager;

    private BDLocation mBdLocation = null;

    private MLocation mBaseLocation = new MLocation();

    private PandoraLocationManager() {
        mLocationManager = (LocationManager) HDApplication.getContext().getSystemService(
                Context.LOCATION_SERVICE);
    }

    private PandoraLocationManager(Context context) {
        mLocationClient = new LocationClient(context.getApplicationContext());
        initParams();
        mLocationClient.registerLocationListener(bdLocationListener);
    }

    public static PandoraLocationManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PandoraLocationManager(context);
        }
        return INSTANCE;
    }

    private ICityNameCallBack mCityNameCallBack;

    public void setCityNameListener(ICityNameCallBack iCityNameCallBack) {
        mCityNameCallBack = iCityNameCallBack;
    }

    public interface ICityNameCallBack {
        void onGetCityName(String cityName);
    }

    public BDLocationListener bdLocationListener = new MyLocationListener();

    private LocationClient mLocationClient;

    public void startMonitor() {
        Log.d(TAG, "start monitor location");
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
        } else {
            Log.d("LocSDK3", "locClient is null or not started");
        }
    }

    public void stopMonitor() {
        Log.d(TAG, "stop monitor location");
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    public BDLocation getLocation() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "baidu get getLocation-->");
        }
        return mBdLocation;
    }

    public MLocation getBaseLocation() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "baidu get getBaseLocation-->");
        }
        return mBaseLocation;
    }

    private void initParams() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            mBdLocation = location;
            mBaseLocation.latitude = mBdLocation.getLatitude();
            mBaseLocation.longitude = mBdLocation.getLongitude();

            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            sb.append("\ncity : ");
            sb.append(location.getCity());

            if (null != mCityNameCallBack && null != location.getCity()) {
                mCityNameCallBack.onGetCityName(location.getCity());
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "---location.getCity()-->>" + location.getCity());
                }
            }

            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "----->>" + sb);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    public class MLocation {
        public double latitude;

        public double longitude;
    }
}
