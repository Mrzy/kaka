
package cn.zmdx.kaka.locker.weather;

import java.io.InputStream;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.util.Log;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.weather.PandoraLocationManager.ICityNameCallBack;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherCityInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherFeatureIndexInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherFeatureInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherInfo;
import cn.zmdx.kaka.locker.weather.utils.ParseWeatherJsonUtils;
import cn.zmdx.kaka.locker.weather.utils.SmartWeatherUtils;
import cn.zmdx.kaka.locker.weather.utils.XMLParserUtils;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

public class PandoraWeatherManager {
    private static final String TAG = "PandoraWeatherManager";

    private Context mContext = HDApplication.getContext();

    private static PandoraWeatherManager INSTANCE = null;

    private String weatherUrl;

    private String cityNameStr = null;

    private InputStream xmlStream;

    private String areaId = null;

    private String forecastReleasedTime;

    private PandoraWeatherManager() {
    }

    public static PandoraWeatherManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PandoraWeatherManager();
        }
        return INSTANCE;
    }

    public interface ISmartWeatherCallback {
        void onSuccess(SmartWeatherInfo smartWeatherInfo);

        void onFailure();
    }

    public void getCurrentSmartWeather(final ISmartWeatherCallback callback) {
        HDBThreadUtils.runOnWorker(new Runnable() {
            @Override
            public void run() {
                getCurWeatherURL();
                internalGetCurrentSmartWeather(callback);
            }
        });
    }

    private void internalGetCurrentSmartWeather(final ISmartWeatherCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("the callback must not be null");
        }
        if (!HDBNetworkState.isNetworkAvailable()) {
            callback.onFailure();
            return;
        }
        JsonObjectRequest request = null;
        request = new JsonObjectRequest(getCurWeatherURL(), null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response == null) {
                    callback.onFailure();
                    return;
                } else {
                    SmartWeatherInfo smartWeatherInfo = ParseWeatherJsonUtils
                            .parseWeatherJson(response);
                    updateView(response);
                    PandoraConfig.newInstance(mContext).saveLastWeatherInfo(response.toString());
                    callback.onSuccess(smartWeatherInfo);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (BuildConfig.DEBUG) {
                    error.printStackTrace();
                }
                callback.onFailure();
            }
        });
        request.setShouldCache(false);
        RequestManager.getRequestQueue().add(request);
    }

    private String getCityNameByLocation() {
        PandoraLocationManager.getInstance(mContext).setCityNameListener(new ICityNameCallBack() {
            @Override
            public void onGetCityName(String cityName) {
                if (cityName != null) {
                    cityNameStr = cityName;
                    PandoraLocationManager.getInstance(mContext).stopMonitor();
                }
                Log.e(TAG, "cityName: ---->" + cityName);
            }
        });
        return cityNameStr;
    }

    private String getCurWeatherURL() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String cityNameByLocation = getCityNameByLocation();
                if (cityNameByLocation != null) {
                    String areaid = getAreaId(cityNameByLocation);
                    String key = SmartWeatherUtils.getKey(areaid);
                    String date = SmartWeatherUtils.getDate();
                    PandoraConfig.newInstance(mContext).saveLastCheckWeatherTime(date);
                    String publicKeyUrl = SmartWeatherUtils.getPublicKeyUrl(areaid);
                    weatherUrl = SmartWeatherUtils.getWeatherUrl(areaid);
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "--areaid-->>" + areaid + "\n--weatherUrl-->>" + weatherUrl
                                + "\n--publicKeyUrl-->>" + publicKeyUrl);
                        Log.e(TAG, "cityNameByLocation: ---->" + cityNameByLocation);
                    }
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 50);// 打开定时器，执行操作
        handler.removeCallbacksAndMessages(this);// 关闭定时器处理
        return weatherUrl;
    }

    private String getAreaId(String cityNameStr) {
        AssetManager asset = this.mContext.getAssets();
        try {
            xmlStream = asset.open("cityInfo.xml");
            if (cityNameStr != null) {
                areaId = XMLParserUtils.getAreaIdByCityName(xmlStream, cityNameStr);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return areaId;
    }

    private void updateView(JSONObject weatherObj) {
        SmartWeatherInfo smartWeatherInfo = ParseWeatherJsonUtils.parseWeatherJson(weatherObj);
        SmartWeatherCityInfo smartWeatherCityInfo = smartWeatherInfo.getSmartWeatherCityInfo();
        SmartWeatherFeatureInfo smartWeatherFeatureInfo = smartWeatherInfo
                .getSmartWeatherFeatureInfo();
        List<SmartWeatherFeatureIndexInfo> smartWeatherFeatureIndexInfoList = smartWeatherFeatureInfo
                .getSmartWeatherFeatureIndexInfoList();

        forecastReleasedTime = smartWeatherFeatureInfo.getForecastReleasedTime();

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "----得到预报发布时间---->>" + forecastReleasedTime);
        }
        String cityNameCh = smartWeatherCityInfo.getCityNameCh();
        String locationCityNameCh = smartWeatherCityInfo.getLocationCityNameCh();
        String provinceNameCh = smartWeatherCityInfo.getProvinceNameCh();
        String countryNameCh = smartWeatherCityInfo.getCountryNameCh();
        Log.i(TAG, "城市名:" + cityNameCh);
        Log.i(TAG, "所属城市:" + locationCityNameCh);
        Log.i(TAG, "所属省:" + provinceNameCh);
        Log.i(TAG, "所属国家:" + countryNameCh);

        for (int i = 0; i < smartWeatherFeatureIndexInfoList.size(); i++) {
            SmartWeatherFeatureIndexInfo smartWeatherFeatureIndexInfo = smartWeatherFeatureIndexInfoList
                    .get(i);

            String daytimeCentTemp = smartWeatherFeatureIndexInfo.getDaytimeCentTemp();
            String daytimeWindForceNo = smartWeatherFeatureIndexInfo.getDaytimeWindForceNo();

            String sunriseAndSunset = smartWeatherFeatureIndexInfo.getSunriseAndSunset();
            String[] split = sunriseAndSunset.split("\\|");//
            String sunrise = split[0];
            String sunset = split[1];
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "--sunrise-->>" + sunrise + "," + "--sunset-->>" + sunset);
                Log.i(TAG, "--sunriseAndSunset-->>" + sunriseAndSunset);
            }
            Log.i(TAG, "最后更新于" + SmartWeatherUtils.getHourFromString(forecastReleasedTime) + "点");

            Log.i(TAG, "  " + daytimeCentTemp + "℃");
            Log.i(TAG, "  " + daytimeWindForceNo + "级");
            Log.i(TAG, "日出:" + sunrise);
            Log.i(TAG, "日落:" + sunset);
        }
    }
}
