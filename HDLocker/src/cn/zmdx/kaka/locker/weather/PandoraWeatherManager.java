
package cn.zmdx.kaka.locker.weather;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherInfo;
import cn.zmdx.kaka.locker.weather.utils.ParseWeatherJsonUtils;
import cn.zmdx.kaka.locker.weather.utils.SmartWeatherUtils;
import cn.zmdx.kaka.locker.weather.utils.XMLParserUtils;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

public class PandoraWeatherManager {
    private Context mContext = HDApplication.getContext();

    private static PandoraWeatherManager INSTANCE = null;

    private String weatherUrl;

    private String cityNameStr = null;

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
        SmartWeatherInfo getWeatherFormCache();

        // 从网络获取天气
        void getCurrentSmartWeather(final ISmartWeatherCallback callback);
    }

    public SmartWeatherInfo getWeatherFromCache() {
        SmartWeatherInfo smartWeatherInfo = null;
        String lastWeatherInfo = PandoraConfig.newInstance(mContext).getLastWeatherInfo();
        JSONObject weatherObj;
        try {
            if (!TextUtils.isEmpty(lastWeatherInfo)) {
                weatherObj = new JSONObject(lastWeatherInfo);
                smartWeatherInfo = ParseWeatherJsonUtils.parseWeatherJson(weatherObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return smartWeatherInfo;
    }

    public void getWeatherFromNetwork(final ISmartWeatherCallback callback) {
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
                    String date = SmartWeatherUtils.getDate();
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("--response-->>" + response);
                    }
                    PandoraConfig.newInstance(mContext).saveLastCheckWeatherTime(date);
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
        if (!TextUtils.isEmpty(cityNameStr)) {
            areaId = XMLParserUtils.getAreaId(cityNameStr);
        } else {
            String lastCityName = PandoraConfig.newInstance(mContext).getLastCityName();
            areaId = XMLParserUtils.getAreaId(lastCityName);
        }
        weatherUrl = SmartWeatherUtils.getWeatherUrl(areaId);
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("--areaid-->>" + areaId + "\n--weatherUrl-->>" + weatherUrl);
            HDBLOG.logD("cityNameStr: ---->" + cityNameStr);
        }
        return weatherUrl;
    }
}
