
package cn.zmdx.kaka.locker.weather;

import android.content.Context;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager.ISmartWeatherCallback;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherInfo;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class PandoraLocationManager {
    private static PandoraLocationManager INSTANCE = null;

    private BDLocation mBdLocation = null;

    private String cityName = null;

    private String cityProvince = null;

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

    public void requestLocation() {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("start request location");
        }
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
        } else {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("locClient is null or not started");
            }
        }
    }

    private void stopRequestLocation() {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("stop request location");
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    public BDLocation getLocation() {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("baidu get getLocation-->");
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
            cityName = mBdLocation.getDistrict();
        }
        return cityName;
    }

    public String getCityProvince() {
        if (mBdLocation != null) {
            cityProvince = mBdLocation.getProvince();
        }
        return cityProvince;
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                requestLocation();
                return;
            }
            mBdLocation = location;
            PandoraConfig.newInstance(mContext).saveLastCityName(mBdLocation.getDistrict());
            PandoraConfig.newInstance(mContext).saveLastCityProvinceName(mBdLocation.getProvince());
            PandoraConfig.newInstance(mContext).saveLastCheckLocationTime(
                    System.currentTimeMillis());
            stopRequestLocation();
            PandoraWeatherManager.getInstance().getWeatherFromNetwork(new ISmartWeatherCallback() {

                @Override
                public void onSuccess(SmartWeatherInfo smartWeatherInfo) {
//                    PandoraBoxManager.newInstance(mContext).updateView(smartWeatherInfo);
                }

                @Override
                public void onFailure() {
                }
            });
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

}
