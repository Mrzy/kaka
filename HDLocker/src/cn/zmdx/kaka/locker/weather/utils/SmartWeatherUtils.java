
package cn.zmdx.kaka.locker.weather.utils;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

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

    public static long str2TimeMillis(String dataStr) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        long timeMillis = 0;
        try {
            Date date = dateFormat.parse(dataStr);
            timeMillis = date.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeMillis;
    }

    public static String getHourFromString(String str) {
        // 201503061100
        String hour = str.substring(8, 10);
        return hour;
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
}
