
package cn.zmdx.kaka.locker.weather;

import android.content.Context;
import android.util.Log;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class PandoraLocationManager {
    private static final String TAG = "PandoraLocationManager";

    private static PandoraLocationManager INSTANCE = null;

    private BDLocation mBdLocation = null;

    private String cityName = null;

    private Context mContext = HDApplication.getContext();

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

    public BDLocationListener bdLocationListener = new MyLocationListener();

    private LocationClient mLocationClient;

    public void startMonitor() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "start monitor location");
        }
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
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "stop monitor location");
        }
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

    private void initParams() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setCoorType("bd09ll");// 设置坐标系为百度经纬度坐标系,默认值gcj02
        option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
        mLocationClient.setLocOption(option);
    }

    public String getCityName() {
        if (mBdLocation != null) {
            cityName = mBdLocation.getCity();
        }
        return cityName;
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            mBdLocation = location;
            PandoraConfig.newInstance(mContext).saveLastCityName(mBdLocation.getCity());
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

}
