
package cn.zmdx.kaka.locker.weather.entity;

import java.util.List;

/**
 * 天气预报天气特征信息类
 */
public class SmartWeatherFeatureInfo {

    private String forecastReleasedTime;// f0预报发布时间

    private List<SmartWeatherFeatureIndexInfo> smartWeatherFeatureIndexInfoList;

    public String getForecastReleasedTime() {
        return forecastReleasedTime;
    }

    public void setForecastReleasedTime(String forecastReleasedTime) {
        this.forecastReleasedTime = forecastReleasedTime;
    }

    public List<SmartWeatherFeatureIndexInfo> getSmartWeatherFeatureIndexInfoList() {
        return smartWeatherFeatureIndexInfoList;
    }

    public void setSmartWeatherFeatureIndexInfoList(
            List<SmartWeatherFeatureIndexInfo> smartWeatherFeatureIndexInfoList) {
        this.smartWeatherFeatureIndexInfoList = smartWeatherFeatureIndexInfoList;
    }

    @Override
    public String toString() {
        return "SmartWeatherFeatureInfo{" + "forecastReleasedTime='" + forecastReleasedTime + '\''
                + ", smartWeatherFeatureIndexInfoList=" + smartWeatherFeatureIndexInfoList + '}';
    }
}
