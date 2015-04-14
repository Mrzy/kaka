
package cn.zmdx.kaka.locker.weather.entity;

import cn.zmdx.kaka.locker.R;

/**
 * 天气气象编码表对应图像所组成的数组
 */
public class MeteorologicalCodeConstant {
    // 天气气象特征数组
    public static final int[] meteorologicalCodePics = new int[] {
            R.drawable.weather_sunny,// 晴0
            R.drawable.weather_cloudy,// 多云1
            R.drawable.weather_overcast,// 阴2
            R.drawable.weather_thundershower,// 雷阵雨3
            R.drawable.weather_sleet,// 雨夹雪4
            R.drawable.weather_light_rain,// 小雨5
            R.drawable.weather_moderate_rain,// 中雨6
            R.drawable.weather_heavy_rain,// 大雨7
            R.drawable.weather_storm,// 暴雨8
            R.drawable.weather_light_snow,// 小雪9
            R.drawable.weather_moderate_snow,// 中雪10
            R.drawable.weather_heavy_snow,// 大雪11
            R.drawable.weather_snowstorm,// 暴雪12
            R.drawable.weather_foggy,// 雾13
            R.drawable.weather_dust,// 扬尘14
            R.drawable.weather_haze,// 霾15
            R.drawable.weather_sunny_night,// 夜间晴16
    };

    public static final String[] meterologicalNames = new String[] {
            "晴", "多云", "阴", "雷阵雨", "雨夹雪", "小雨", "中雨", "大雨", "暴雨", "小雪", "中雪", "大雪", "暴雪", "雾",
            "扬尘", "霾", "夜间晴", "夜间多云"
    };

    // 风向数组
    public static final String[] meterologicalWindFeatures = new String[] {
            "",// 0
            "东北风",// 1
            "东风",// 2
            "东南风",// 3
            "南风",// 4
            "西南风",// 5
            "西风",// 6
            "西北风",// 7
            "北风",// 8
            "旋转风",// 9
    };

    // 风力数组
    public static final String[] meterologicalWindForces = new String[] {
            "微风",// 0
            "3~4级",// 1
            "4~5级",// 2
            "5~6级",// 3
            "6~7级",// 4
            "7~8级",// 5
            "8~9级",// 6
            "9~10级",// 7
            "10~11级",// 8
            "11~12级",// 9
    };

    // 热门城市
    public static final String[] hotCityNameStrings = new String[] {
            "定位", "北京", "上海", "广州", "深圳", "天津", "南京", "杭州", "成都", "武汉", "重庆", "沈阳", "西安", "大连",
            "青岛", "苏州", "长沙", "宁波", "无锡", "郑州", "香港", "台北", "澳门", "高雄"
    };
}
