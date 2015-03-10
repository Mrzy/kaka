
package cn.zmdx.kaka.locker.weather.entity;

/**
 * 天气预报城市信息类
 */
public class SmartWeatherCityInfo {

    private String cityId;// c1区域id

    private String cityNameEn;// c2城市英文名

    private String cityNameCh;// c3城市中文名

    private String locationCityNameEn;// c4城市所在市英文名

    private String locationCityNameCh;// c5城市所在市中文名

    private String provinceNameEn;// c6城市所在省英文名

    private String provinceNameCh;// c7城市所在省中文名

    private String countryNameEn;// c8城市所在国家英文名

    private String countryNameCh;// c9城市所在国家中文名

    private String cityLevel;// c10城市级别

    private String cityCode;// c11城市区号

    private String zipCode;// c12邮编

    private String longitude;// c13经度

    private String latitude;// c14纬度

    private String altitude;// c15海拔

    private String radarStationNo;// c16雷达站号

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityNameEn() {
        return cityNameEn;
    }

    public void setCityNameEn(String cityNameEn) {
        this.cityNameEn = cityNameEn;
    }

    public String getCityNameCh() {
        return cityNameCh;
    }

    public void setCityNameCh(String cityNameCh) {
        this.cityNameCh = cityNameCh;
    }

    public String getLocationCityNameEn() {
        return locationCityNameEn;
    }

    public void setLocationCityNameEn(String locationCityNameEn) {
        this.locationCityNameEn = locationCityNameEn;
    }

    public String getLocationCityNameCh() {
        return locationCityNameCh;
    }

    public void setLocationCityNameCh(String locationCityNameCh) {
        this.locationCityNameCh = locationCityNameCh;
    }

    public String getProvinceNameEn() {
        return provinceNameEn;
    }

    public void setProvinceNameEn(String provinceNameEn) {
        this.provinceNameEn = provinceNameEn;
    }

    public String getProvinceNameCh() {
        return provinceNameCh;
    }

    public void setProvinceNameCh(String provinceNameCh) {
        this.provinceNameCh = provinceNameCh;
    }

    public String getCountryNameEn() {
        return countryNameEn;
    }

    public void setCountryNameEn(String countryNameEn) {
        this.countryNameEn = countryNameEn;
    }

    public String getCountryNameCh() {
        return countryNameCh;
    }

    public void setCountryNameCh(String countryNameCh) {
        this.countryNameCh = countryNameCh;
    }

    public String getCityLevel() {
        return cityLevel;
    }

    public void setCityLevel(String cityLevel) {
        this.cityLevel = cityLevel;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getRadarStationNo() {
        return radarStationNo;
    }

    public void setRadarStationNo(String radarStationNo) {
        this.radarStationNo = radarStationNo;
    }

    @Override
    public String toString() {
        return "SmartWeatherCityInfo{" + "cityId='" + cityId + '\'' + ", cityNameEn='" + cityNameEn
                + '\'' + ", cityNameCh='" + cityNameCh + '\'' + ", locationCityNameEn='"
                + locationCityNameEn + '\'' + ", locationCityNameCh='" + locationCityNameCh + '\''
                + ", provinceNameEn='" + provinceNameEn + '\'' + ", provinceNameCh='"
                + provinceNameCh + '\'' + ", countryNameEn='" + countryNameEn + '\''
                + ", countryNameCh='" + countryNameCh + '\'' + ", cityLevel='" + cityLevel + '\''
                + ", cityCode='" + cityCode + '\'' + ", zipCode='" + zipCode + '\''
                + ", longitude='" + longitude + '\'' + ", latitude='" + latitude + '\''
                + ", altitude='" + altitude + '\'' + ", radarStationNo='" + radarStationNo + '\''
                + '}';
    }
}
