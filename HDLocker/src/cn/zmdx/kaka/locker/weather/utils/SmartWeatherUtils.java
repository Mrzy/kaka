
package cn.zmdx.kaka.locker.weather.utils;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Base64;
import cn.zmdx.kaka.locker.weather.entity.MeteorologicalCodeConstant;

@SuppressLint("SimpleDateFormat")
public class SmartWeatherUtils {
    private static final String ENCODING = "UTF-8";

    private static final String MAC_NAME = "HmacSHA1";

    private static final String BASE_WEATHER_URL = "http://open.weather.com.cn/data/?";

    private static final String APPID = "7033c892d8437240";

    private static final String PRIVATE_KEY = "ca5bc8_SmartWeatherAPI_0b06585";

    private static final String TYPE = "forecast_v";

    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     * 
     * @param url 被签名的字符串
     * @param privatekey 密钥
     * @return
     * @throws Exception
     */
    private static byte[] HmacSHA1Encrypt(String url, String privatekey) throws Exception {
        byte[] data = privatekey.getBytes(ENCODING);
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = url.getBytes(ENCODING);
        // 完成 Mac 操作
        return mac.doFinal(text);
    }

    private static String getEncodedKey(String url, String privatekey) throws Exception {
        String encodedKey = "";
        byte[] key_bytes = new byte[0];
        key_bytes = HmacSHA1Encrypt(url, privatekey);
        String base64encoderStr = Base64.encodeToString(key_bytes, Base64.NO_WRAP);
        encodedKey = URLEncoder.encode(base64encoderStr, ENCODING);
        return encodedKey;
    }

    public static String getWeatherUrl(String areaid) {
        StringBuilder sb = new StringBuilder(BASE_WEATHER_URL);
        sb.append("areaid=" + areaid);
        sb.append("&type=" + TYPE);
        sb.append("&date=" + getDate());
        sb.append("&appid=" + APPID.substring(0, 6));
        sb.append("&key=" + getKey(areaid));
        return sb.toString();
    }

    private static String getPublicKeyUrl(String areaid) {
        StringBuilder sb = new StringBuilder(BASE_WEATHER_URL);
        sb.append("areaid=" + areaid);
        sb.append("&type=" + TYPE);
        sb.append("&date=" + getDate());
        sb.append("&appid=" + APPID);
        return sb.toString();
    }

    public static String getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String formatDate = dateFormat.format(cal.getTime());
        return formatDate;
    }

    public static String getLunarCal() {
        Calendar today = Calendar.getInstance();
        String todayFormatDate = String.valueOf(today.get(Calendar.YEAR)) + "年"
                + String.valueOf(today.get(Calendar.MONTH) + 1) + "月"
                + String.valueOf(today.get(Calendar.DAY_OF_MONTH)) + "日";
        SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        try {
            today.setTime(chineseDateFormat.parse(todayFormatDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LunarUtils lunarCal = new LunarUtils(today);
        String lunar = lunarCal.toString();
        return lunar;
    }

    public static String getCyclicalm() {
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        LunarUtils lunarCal = new LunarUtils(today);
        String cyclical = lunarCal.cyclical(year);
        return cyclical;
    }

    public static long str2TimeMillis(String dataStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        long timeMillis = 0;
        try {
            if (dataStr != null) {
                Date date = dateFormat.parse(dataStr);
                timeMillis = date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeMillis;
    }

    public static String getHourFromString(String str) {
        // 201503061100
        String hour = str.substring(8, 10);
        return hour;
    }

    public static boolean isNight(String hour) {
        boolean isNightTime = false;
        Calendar cal = Calendar.getInstance();
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= 18 && hourOfDay <= 24 || hourOfDay >= 1 && hourOfDay <= 7) {
            return !isNightTime;
        }
        return isNightTime;
    }

    private static String getKey(String areaid) {
        String key = null;
        try {
            key = getEncodedKey(getPublicKeyUrl(areaid), PRIVATE_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * 根据白天天气指数值获取对应的天气特征图片
     * 
     * @param featureNoStr
     * @return
     */
    public static int getFeatureIndexPicByNo(String featureNoStr) {
        int result = 0;
        if (featureNoStr.equals("00")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[0];
        } else if (featureNoStr.equals("01")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[1];
        } else if (featureNoStr.equals("02")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[2];
        } else if (featureNoStr.equals("04")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[3];
        } else if (featureNoStr.equals("06")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[4];
        } else if (featureNoStr.equals("07")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[5];
        } else if (featureNoStr.equals("08")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[6];
        } else if (featureNoStr.equals("09")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[7];
        } else if (featureNoStr.equals("10")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[8];
        } else if (featureNoStr.equals("14")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[9];
        } else if (featureNoStr.equals("15")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[10];
        } else if (featureNoStr.equals("16")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[11];
        } else if (featureNoStr.equals("17")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[12];
        } else if (featureNoStr.equals("18")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[13];
        } else if (featureNoStr.equals("29")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[14];
        } else if (featureNoStr.equals("53")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[15];
        } else if (featureNoStr.equals("000")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[16];
        } else if (featureNoStr.equals("001")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[17];
        }
        return result;
    }

    /**
     * 根据风力值得到风力
     */

    public static String getWindForceByNo(String windForceNo) {
        String windForce = null;
        if (!TextUtils.isEmpty(windForceNo)) {
            windForce = MeteorologicalCodeConstant.meterologicalWindForces[Integer
                    .parseInt(windForceNo)];
        }
        return windForce;
    }

    /**
     * 根据风向值得到风向
     */
    public static String getWindByNo(String windNo) {
        String wind = null;
        if (!TextUtils.isEmpty(windNo)) {
            wind = MeteorologicalCodeConstant.meterologicalWindFeatures[Integer.parseInt(windNo)];
        }
        return wind;
    }
}
