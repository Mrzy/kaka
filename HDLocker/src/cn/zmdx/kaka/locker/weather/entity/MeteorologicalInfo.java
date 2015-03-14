
package cn.zmdx.kaka.locker.weather.entity;

/**
 * XML文档里涉及的气象信息类
 */
public class MeteorologicalInfo {
    private String daytimeFeatureNo;

    private String weatherFeatureIndexNameCH;

    private String weatherFeatureIndexNameEN;

    public String getDaytimeFeatureNo() {
        return daytimeFeatureNo;
    }

    public void setDaytimeFeatureNo(String daytimeFeatureNo) {
        this.daytimeFeatureNo = daytimeFeatureNo;
    }

    public String getWeatherFeatureIndexNameCH() {
        return weatherFeatureIndexNameCH;
    }

    public void setWeatherFeatureIndexNameCH(String weatherFeatureIndexNameCH) {
        this.weatherFeatureIndexNameCH = weatherFeatureIndexNameCH;
    }

    public String getWeatherFeatureIndexNameEN() {
        return weatherFeatureIndexNameEN;
    }

    public void setWeatherFeatureIndexNameEN(String weatherFeatureIndexNameEN) {
        this.weatherFeatureIndexNameEN = weatherFeatureIndexNameEN;
    }

    @Override
    public String toString() {
        return "MeteorologicalInfo [daytimeFeatureNo=" + daytimeFeatureNo
                + ", weatherFeatureIndexNameCH=" + weatherFeatureIndexNameCH
                + ", weatherFeatureIndexNameEN=" + weatherFeatureIndexNameEN + "]";
    }

}
