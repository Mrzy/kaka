
package cn.zmdx.kaka.locker.weather;

import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
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

    private String areaIdInXml = null;

    private String forecastReleasedTime;

    private String areaId = null;

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

    /**
     * 获取气象台天气接口
     */
    public interface IGetSmartWeatherCallback {
        // 从本地获取天气
        void getWeatherFormCache(final ISmartWeatherCallback callback);

        // 从网络获取天气
        void getCurrentSmartWeather(final ISmartWeatherCallback callback);
    }

    public void getWeatherFormCache(final ISmartWeatherCallback callback) {
        HDBThreadUtils.runOnWorker(new Runnable() {
            @Override
            public void run() {
                String lastWeatherInfo = PandoraConfig.newInstance(mContext).getLastWeatherInfo();
                if (lastWeatherInfo != null) {
                    try {
                        JSONObject weatherObj = new JSONObject(lastWeatherInfo);
                        updateView(weatherObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void getCurrentSmartWeather(final ISmartWeatherCallback callback) {
        HDBThreadUtils.runOnWorker(new Runnable() {
            @Override
            public void run() {
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

    private String getCurWeatherURL() {
        cityNameStr = PandoraLocationManager.getInstance(mContext).getCityName();
        if (cityNameStr != null) {
            areaId = getAreaId(cityNameStr);
        } else {
            String lastCityName = PandoraConfig.newInstance(mContext).getLastCityName();
            areaId = getAreaId(lastCityName);
        }
        String date = SmartWeatherUtils.getDate();
        PandoraConfig.newInstance(mContext).saveLastCheckWeatherTime(date);
        weatherUrl = SmartWeatherUtils.getWeatherUrl(areaId);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "--areaid-->>" + areaId + "\n--weatherUrl-->>" + weatherUrl);
            Log.e(TAG, "cityNameStr: ---->" + cityNameStr);
        }
        return weatherUrl;
    }

    private String getAreaId(String cityNameStr) {
        AssetManager asset = this.mContext.getAssets();
        try {
            xmlStream = asset.open("cityInfo.xml");
            if (cityNameStr != null) {
                areaIdInXml = XMLParserUtils.getAreaIdByCityName(xmlStream, cityNameStr);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return areaIdInXml;
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
        }
    }
}
