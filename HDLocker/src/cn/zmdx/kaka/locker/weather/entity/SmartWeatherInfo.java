
package cn.zmdx.kaka.locker.weather.entity;

/**
 * 天气信息类
 */
public class SmartWeatherInfo {
    private SmartWeatherFeatureInfo smartWeatherFeatureInfo;

    private SmartWeatherCityInfo smartWeatherCityInfo;

    public SmartWeatherFeatureInfo getSmartWeatherFeatureInfo() {
        return smartWeatherFeatureInfo;
    }

    public void setSmartWeatherFeatureInfo(SmartWeatherFeatureInfo smartWeatherFeatureInfo) {
        this.smartWeatherFeatureInfo = smartWeatherFeatureInfo;
    }

    public SmartWeatherCityInfo getSmartWeatherCityInfo() {
        return smartWeatherCityInfo;
    }

    public void setSmartWeatherCityInfo(SmartWeatherCityInfo smartWeatherCityInfo) {
        this.smartWeatherCityInfo = smartWeatherCityInfo;
    }

    @Override
    public String toString() {
        return "SmartWeatherInfo{" + "smartWeatherFeatureInfo=" + smartWeatherFeatureInfo
                + ", smartWeatherCityInfo=" + smartWeatherCityInfo + '}';
    }
}
