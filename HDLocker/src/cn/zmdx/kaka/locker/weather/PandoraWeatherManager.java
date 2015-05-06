
package cn.zmdx.kaka.locker.weather;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherInfo;
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
        void onSuccess(String smartWeatherInfoStr);

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

    public String getWeatherFromCache() {
        String lastWeatherInfo = PandoraConfig.newInstance(mContext).getLastWeatherInfo();
        return lastWeatherInfo;
    }

    public void getWeatherFromNetwork(final ISmartWeatherCallback callback) {
        internalGetCurrentSmartWeather(callback);
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
        String curWeatherURL = getCurWeatherURL();
        if (TextUtils.isEmpty(curWeatherURL)) {
            return;
        }
        request = new JsonObjectRequest(curWeatherURL, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response == null) {
                    callback.onFailure();
                    return;
                } else {
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("--response-->>" + response);
                    }
                    // boolean weatherInfoLegal =
                    // ParseWeatherJsonUtils.isWeatherInfoLegal(response
                    // .toString());
                    //
                    // if (!weatherInfoLegal) {
                    // callback.onFailure();
                    // return;
                    // }
                    // String lastWeatherInfo =
                    // PandoraConfig.newInstance(mContext)
                    // .getLastWeatherInfo();
                    // if (!TextUtils.isEmpty(lastWeatherInfo)
                    // && lastWeatherInfo.equals(response.toString())) {
                    // return;
                    // }
                    PandoraConfig.newInstance(mContext).saveLastCheckWeatherTime(
                            System.currentTimeMillis());
                    PandoraConfig.newInstance(mContext).saveLastWeatherInfo(response.toString());
                    callback.onSuccess(response.toString());
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
        RequestManager.getRequestQueue().add(request);
    }

    private String getCurWeatherURL() {
        String lastCityName = PandoraWeatherManager.getInstance().getLastCity();
        String cityProvinceName = PandoraWeatherManager.getInstance().getLastProvince();
        if (!TextUtils.isEmpty(lastCityName) && !TextUtils.isEmpty(cityProvinceName)) {
            areaId = XMLParserUtils.getAreaId(lastCityName, cityProvinceName);
        }
        if (TextUtils.isEmpty(areaId)) {
            return null;
        }
        weatherUrl = SmartWeatherUtils.getWeatherUrl(areaId);
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("--areaid-->>" + areaId + "\n--weatherUrl-->>" + weatherUrl);
            HDBLOG.logD("cityNameStr: ---->" + lastCityName);
            HDBLOG.logD("aimCityName: ---->" + cityProvinceName);
        }
        return weatherUrl;
    }

    public String getLastCity() {
        String theCityHasSet = PandoraConfig.newInstance(mContext).getTheCityHasSet();
        if (!TextUtils.isEmpty(theCityHasSet)) {
            return theCityHasSet.split(",")[0];
        }
        return null;
    }

    public String getLastProvince() {
        String theCityHasSet = PandoraConfig.newInstance(mContext).getTheCityHasSet();
        if (!TextUtils.isEmpty(theCityHasSet)) {
            return theCityHasSet.split(",")[1];
        }
        return null;
    }
}
