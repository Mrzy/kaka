
package cn.zmdx.kaka.locker.weather.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherCityInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherFeatureIndexInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherFeatureInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherInfo;

public class ParseWeatherJsonUtils {
    private static SmartWeatherInfo smartWeatherInfo;

    public static SmartWeatherInfo parseWeatherJson(JSONObject weatherObj) {
        smartWeatherInfo = new SmartWeatherInfo();
        if (weatherObj != null) {
            try {
//                JSONObject weatherObj = new JSONObject(jsonObj);
                JSONObject cityInfoObj = weatherObj.getJSONObject("c");
                SmartWeatherCityInfo cityInfo = new SmartWeatherCityInfo();
                cityInfo.setCityId(cityInfoObj.optString("c1"));
                cityInfo.setCityNameCh(cityInfoObj.optString("c3"));
                cityInfo.setLocationCityNameCh(cityInfoObj.optString("c5"));
                cityInfo.setProvinceNameCh(cityInfoObj.optString("c7"));
                cityInfo.setCountryNameCh(cityInfoObj.optString("c9"));
                if (BuildConfig.DEBUG) {
                    Log.i("ParseJson", "----国家名---->>" + cityInfoObj.optString("c9"));
                }
                smartWeatherInfo.setSmartWeatherCityInfo(cityInfo);

                JSONObject cityFeatureObj = weatherObj.getJSONObject("f");
                SmartWeatherFeatureInfo featureInfo = new SmartWeatherFeatureInfo();
                featureInfo.setForecastReleasedTime(cityFeatureObj.optString("f0"));
                smartWeatherInfo.setSmartWeatherFeatureInfo(featureInfo);
                if (BuildConfig.DEBUG) {
                    Log.i("ParseJson", "----发布时间---->>" + cityFeatureObj.optString("f0"));
                }

                JSONArray cityFeatureArray = cityFeatureObj.getJSONArray("f1");
                List<SmartWeatherFeatureIndexInfo> smartWeatherFeatureIndexInfoList = new ArrayList<SmartWeatherFeatureIndexInfo>();
                for (int i = 0; i < cityFeatureArray.length(); i++) {
                    JSONObject cityFeatureIndexObj = (JSONObject) cityFeatureArray.get(i);
                    SmartWeatherFeatureIndexInfo featureIndexInfo = new SmartWeatherFeatureIndexInfo();
                    featureIndexInfo.setDaytimeFeatureNo(cityFeatureIndexObj.optString("fa"));
                    featureIndexInfo.setNightFeatureNo(cityFeatureIndexObj.optString("fb"));
                    featureIndexInfo.setDaytimeCentTemp(cityFeatureIndexObj.optString("fc"));
                    featureIndexInfo.setNightCentTemp(cityFeatureIndexObj.optString("fd"));
                    featureIndexInfo.setDaytimeWindNo(cityFeatureIndexObj.optString("fe"));
                    featureIndexInfo.setNightWindNo(cityFeatureIndexObj.optString("ff"));
                    featureIndexInfo.setDaytimeWindForceNo(cityFeatureIndexObj.optString("fg"));
                    featureIndexInfo.setNightWindForceNo(cityFeatureIndexObj.optString("fh"));
                    featureIndexInfo.setSunriseAndSunset(cityFeatureIndexObj.optString("fi"));
                    if (BuildConfig.DEBUG) {
                        Log.i("ParseJson", "----白天温度---->>" + cityFeatureIndexObj.optString("fc"));
                        Log.i("ParseJson", "----白天风力---->>" + cityFeatureIndexObj.optString("fg"));
                    }
                    smartWeatherFeatureIndexInfoList.add(featureIndexInfo);
                    featureInfo
                            .setSmartWeatherFeatureIndexInfoList(smartWeatherFeatureIndexInfoList);
                    smartWeatherInfo.setSmartWeatherFeatureInfo(featureInfo);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return smartWeatherInfo;
    }
}
