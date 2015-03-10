
package cn.zmdx.kaka.locker.weather.entity;

/**
 * XML文档里涉及的城市信息表
 */
public class CityInfo {

    private String areaId;

    private String cityName;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String toString() {
        return "CityInfo{" + "areaId='" + areaId + '\'' + ", cityName='" + cityName + '\'' + '}';
    }
}
