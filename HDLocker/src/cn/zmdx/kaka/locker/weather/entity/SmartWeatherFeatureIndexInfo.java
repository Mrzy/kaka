
package cn.zmdx.kaka.locker.weather.entity;

/**
 * 天气指数信息类
 */
public class SmartWeatherFeatureIndexInfo {
    private String daytimeFeatureNo;// fa白天天气现象编号

    private String nightFeatureNo;// fb晚上天气现象编号

    private String daytimeCentTemp;// fc白天天气摄氏温度

    private String nightCentTemp;// fd晚上天气摄氏温度

    private String daytimeWindNo;// fe白天风向编号

    private String nightWindNo;// ff晚上风向编号

    private String daytimeWindForceNo;// fg白天风力编号

    private String nightWindForceNo;// fh晚上风力编号

    private String sunriseAndSunset;// fi日出日落时间（用|分割）

    public String getDaytimeFeatureNo() {
        return daytimeFeatureNo;
    }

    public void setDaytimeFeatureNo(String daytimeFeatureNo) {
        this.daytimeFeatureNo = daytimeFeatureNo;
    }

    public String getNightFeatureNo() {
        return nightFeatureNo;
    }

    public void setNightFeatureNo(String nightFeatureNo) {
        this.nightFeatureNo = nightFeatureNo;
    }

    public String getDaytimeCentTemp() {
        return daytimeCentTemp;
    }

    public void setDaytimeCentTemp(String daytimeCentTemp) {
        this.daytimeCentTemp = daytimeCentTemp;
    }

    public String getNightCentTemp() {
        return nightCentTemp;
    }

    public void setNightCentTemp(String nightCentTemp) {
        this.nightCentTemp = nightCentTemp;
    }

    public String getDaytimeWindNo() {
        return daytimeWindNo;
    }

    public void setDaytimeWindNo(String daytimeWindNo) {
        this.daytimeWindNo = daytimeWindNo;
    }

    public String getNightWindNo() {
        return nightWindNo;
    }

    public void setNightWindNo(String nightWindNo) {
        this.nightWindNo = nightWindNo;
    }

    public String getDaytimeWindForceNo() {
        return daytimeWindForceNo;
    }

    public void setDaytimeWindForceNo(String daytimeWindForceNo) {
        this.daytimeWindForceNo = daytimeWindForceNo;
    }

    public String getNightWindForceNo() {
        return nightWindForceNo;
    }

    public void setNightWindForceNo(String nightWindForceNo) {
        this.nightWindForceNo = nightWindForceNo;
    }

    public String getSunriseAndSunset() {
        return sunriseAndSunset;
    }

    public void setSunriseAndSunset(String sunriseAndSunset) {
        this.sunriseAndSunset = sunriseAndSunset;
    }

    @Override
    public String toString() {
        return "SmartWeatherFeatureIndexInfo{" + "daytimeFeatureNo='" + daytimeFeatureNo + '\''
                + ", nightFeatureNo='" + nightFeatureNo + '\'' + ", daytimeCentTemp='"
                + daytimeCentTemp + '\'' + ", nightCentTemp='" + nightCentTemp + '\''
                + ", daytimeWindNo='" + daytimeWindNo + '\'' + ", nightWindNo='" + nightWindNo
                + '\'' + ", daytimeWindForceNo='" + daytimeWindForceNo + '\''
                + ", nightWindForceNo='" + nightWindForceNo + '\'' + ", sunriseAndSunset='"
                + sunriseAndSunset + '\'' + '}';
    }
}
