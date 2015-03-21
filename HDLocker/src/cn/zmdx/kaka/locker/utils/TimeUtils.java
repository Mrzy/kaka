
package cn.zmdx.kaka.locker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.zmdx.kaka.locker.R;

import android.content.Context;

public class TimeUtils {

    public static String getInterval(Context context, long createAt) {
        // 定义最终返回的结果字符串。
        String interval = null;

        long millisecond = new Date().getTime() - createAt;

        long second = millisecond / 1000;

        if (second <= 0) {
            second = 0;
        }

        if (second <= 60) {
            interval = context.getString(R.string.justnow);
        } else if (second < 60 * 60) {
            interval = second / 60 + context.getString(R.string.minute);
        } else if (second >= 60 * 60 && second < 60 * 60 * 24) {
            long hour = (second / 60) / 60;
            interval = hour + context.getString(R.string.hours);
        } else if (second >= 60 * 60 * 24 && second <= 60 * 60 * 24 * 2) {
            interval = context.getString(R.string.yesterday)
                    + getFormatTime(new Date(createAt), "hh:mm");
        } else if (second >= 60 * 60 * 24 * 2 && second <= 60 * 60 * 24 * 7) {
            long day = ((second / 60) / 60) / 24;
            interval = day + context.getString(R.string.day);
        } else if (second >= 60 * 60 * 24 * 7) {
            interval = getFormatTime(new Date(createAt), "MM-dd hh:mm");
        } else if (second >= 60 * 60 * 24 * 365) {
            interval = getFormatTime(new Date(createAt), "YYYY-MM-dd hh:mm");
        } else {
            interval = "0";
        }
        // 最后返回处理后的结果。
        return interval;
    }

    private static String getFormatTime(Date date, String Sdf) {
        return (new SimpleDateFormat(Sdf)).format(date);
    }
}
