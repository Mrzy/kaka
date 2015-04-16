
package cn.zmdx.kaka.locker.weather.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherCityInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherFeatureIndexInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherFeatureInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherInfo;

public class ParseWeatherJsonUtils {

    public static SmartWeatherInfo parseWeatherJson(String weatherInfo) {
        SmartWeatherInfo smartWeatherInfo = new SmartWeatherInfo();
        if (!TextUtils.isEmpty(weatherInfo)) {
            try {
                JSONObject weatherObj = new JSONObject(weatherInfo);
                JSONObject cityInfoObj = weatherObj.getJSONObject("c");
                SmartWeatherCityInfo cityInfo = new SmartWeatherCityInfo();
                cityInfo.setCityId(cityInfoObj.optString("c1"));
                cityInfo.setCityNameCh(cityInfoObj.optString("c3"));
                cityInfo.setLocationCityNameCh(cityInfoObj.optString("c5"));
                cityInfo.setProvinceNameCh(cityInfoObj.optString("c7"));
                cityInfo.setCountryNameCh(cityInfoObj.optString("c9"));
                smartWeatherInfo.setSmartWeatherCityInfo(cityInfo);

                JSONObject cityFeatureObj = weatherObj.getJSONObject("f");
                SmartWeatherFeatureInfo featureInfo = new SmartWeatherFeatureInfo();
                featureInfo.setForecastReleasedTime(cityFeatureObj.optString("f0"));
                smartWeatherInfo.setSmartWeatherFeatureInfo(featureInfo);

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
                    smartWeatherFeatureIndexInfoList.add(featureIndexInfo);
                    featureInfo
                            .setSmartWeatherFeatureIndexInfoList(smartWeatherFeatureIndexInfoList);
                    smartWeatherInfo.setSmartWeatherFeatureInfo(featureInfo);
                }
            } catch (JSONException e) {
                return null;
            }
        }
        return smartWeatherInfo;
    }

    public static boolean isWeatherInfoLegal(String smartWeatherInfoStr) {
        boolean isWeatherInfoLegal = false;
        if (TextUtils.isEmpty(smartWeatherInfoStr)) {
            return isWeatherInfoLegal;
        } else {
            SmartWeatherInfo smartWeatherInfo = null;

            String forecastReleasedTime = null;

            String sunriseAndSunset = null;

            String centTempDay = null;

            String centTempNight = null;

            String daytimeFeatureNo = null;

            String nightFeatureNo = null;
            smartWeatherInfo = parseWeatherJson(smartWeatherInfoStr);

            SmartWeatherFeatureInfo smartWeatherFeatureInfo = smartWeatherInfo
                    .getSmartWeatherFeatureInfo();
            List<SmartWeatherFeatureIndexInfo> smartWeatherFeatureIndexInfoList = smartWeatherFeatureInfo
                    .getSmartWeatherFeatureIndexInfoList();

            SmartWeatherFeatureIndexInfo smartWeatherFeatureIndexInfo = smartWeatherFeatureIndexInfoList
                    .get(0);
            if (smartWeatherFeatureInfo != null) {
                forecastReleasedTime = smartWeatherFeatureInfo.getForecastReleasedTime();
                sunriseAndSunset = smartWeatherFeatureIndexInfo.getSunriseAndSunset();
                centTempDay = smartWeatherFeatureIndexInfo.getDaytimeCentTemp();
                centTempNight = smartWeatherFeatureIndexInfo.getNightCentTemp();
                daytimeFeatureNo = smartWeatherFeatureIndexInfo.getDaytimeFeatureNo();
                nightFeatureNo = smartWeatherFeatureIndexInfo.getNightFeatureNo();
            }
            if (!TextUtils.isEmpty(forecastReleasedTime) && !TextUtils.isEmpty(sunriseAndSunset)
                    && !TextUtils.isEmpty(centTempDay) && !TextUtils.isEmpty(centTempNight)
                    && !TextUtils.isEmpty(daytimeFeatureNo) && !TextUtils.isEmpty(nightFeatureNo)) {
                isWeatherInfoLegal = true;
            }
        }
        return isWeatherInfoLegal;
    }
}
