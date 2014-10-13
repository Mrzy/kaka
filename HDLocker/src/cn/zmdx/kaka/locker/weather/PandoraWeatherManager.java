
package cn.zmdx.kaka.locker.weather;

import java.text.NumberFormat;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager.PandoraWeather;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class PandoraWeatherManager {

    private static final String BASE_WEATHER_URL = "http://caiyunapp.com/fcgi-bin/v1/api.py?";

    private static final String TOKEN = "TAkhjf8d1nlSlspN";

    private static PandoraWeatherManager INSTANCE = null;

    public interface IWeatherCallback {
        void onSuccess(PandoraWeather pw);

        void onFailed();
    }

    private PandoraWeatherManager() {
    }

    public static PandoraWeatherManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PandoraWeatherManager();
        }
        return INSTANCE;
    }

    public void getCurrentWeather(final IWeatherCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("the callback must not be null");
        }

        if (!HDBNetworkState.isNetworkAvailable()) {
            return;
        }
        final String lonlat = getLocation();
        if (TextUtils.isEmpty(lonlat)) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("未获得位置信息，中断请求天气数据");
            }
            return;
        }
        JsonObjectRequest request = null;
        request = new JsonObjectRequest(getUrl(lonlat), null, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                HDBLOG.logD("weather url:" + getUrl(lonlat));
                if (response == null) {
                    callback.onFailed();
                    return;
                }
                String status = response.optString("status");
                if (!TextUtils.isEmpty(status) && status.equals("ok")) {
                    PandoraWeather pw = new PandoraWeather();
                    pw.setTemp(response.optInt("temp"));
                    pw.setServerTime(response.optString("server_time"));
                    pw.setDescriptNow(response.optString("descript_now"));
                    pw.setSummary(response.optString("summary"));
                    callback.onSuccess(pw);
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("caiyun weather data returned:" + pw.toString());
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("获取天气数据失败, status code:" + status);
                    }
                    callback.onFailed();
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (BuildConfig.DEBUG) {
                    error.printStackTrace();
                }
                callback.onFailed();
            }
        });
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 返回的字符串格式为：[经度],[纬度]
     * 精确到小数点后4位
     */
    private String getLocation() {
        final LocationManager locationManager = (LocationManager) HDApplication.getInstannce()
                .getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();
        Location location = null;
        for (int i=0;i<providers.size();i++) {
            location = locationManager.getLastKnownLocation(providers.get(i));
            if (location != null) {
                break;
            }
        }
        if (location == null) {
            return null;
        }
        final double latitude = location.getLatitude(); // 经度
        final double longitude = location.getLongitude(); // 纬度
        final double altitude = location.getAltitude(); // 海拔

        final NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(4);
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("latitude " + latitude + "  longitude:" + longitude + " altitude:"
                    + altitude);
        }
        return format.format(longitude) + "," + format.format(latitude);
    }

    private String getUrl(String lonlat) {
        StringBuilder sb = new StringBuilder(BASE_WEATHER_URL);
        sb.append("token=" + TOKEN);
        sb.append("&product=minutes_prec");
        sb.append("&format=json");
        sb.append("&lonlat=" + lonlat);
        return sb.toString();
    }

    public static final class PandoraWeather {
        // 当前温度
        private int temp;

        // 当前天气和风力的描述
        private String descriptNow;

        // 未来一小时雨量描述
        private String summary;

        // 服务器时间
        private String serverTime;

        public int getTemp() {
            return temp;
        }

        public void setTemp(int temp) {
            this.temp = temp;
        }

        public String getDescriptNow() {
            return descriptNow;
        }

        public void setDescriptNow(String descriptNow) {
            this.descriptNow = descriptNow;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getServerTime() {
            return serverTime;
        }

        public void setServerTime(String serverTime) {
            this.serverTime = serverTime;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("temp:" + temp);
            sb.append(", descriptNow" + descriptNow);
            sb.append(", summary" + summary);
            sb.append(", serverTime" + serverTime);
            return sb.toString();
        }
    }
}
