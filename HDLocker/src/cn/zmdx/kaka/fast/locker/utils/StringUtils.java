package cn.zmdx.kaka.fast.locker.utils;

import java.text.DecimalFormat;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;


public class StringUtils {
    /**
     * Format bytes count in proper suffix.
     * @param size Bytes count in bytes (B)
     * @return Formatted string in "B" or "KB" or "MB" or "GB"
     */
    public static String formatBytes(long size) {
        return formatBytes(size, true);
    }

    public static String formatBytes(long size, boolean hasByte) {
        // TODO: try the following two methods
        // Formatter.formatShortFileSize(context, size);
        // Formatter.formatFileSize(context, size);
        DecimalFormat formatter = new DecimalFormat("#0.0");
        if (size >= 1024 * 1024 * 1024l) {
            // in GB
            return formatter.format(size / (1024 * 1024 * 1024f)) + "G" + (hasByte? "B" : "");
        } else if (size >= 1024 * 1024l) {
            // in MB
            return formatter.format(size / (1024 * 1024f)) + "M" + (hasByte? "B" : "");
        } else if (size >= 1024) {
            // in KB
            return formatter.format(size / 1024f) + "K" + (hasByte? "B" : "");
        } else {
            return size + (hasByte? "B" : "");
        }
    }

    /**
     * Format bytes count in proper suffix.
     * @param size Bytes count in KB
     * @return Formatted string in "KB" or "MB" or "GB"
     */
    public static String formatBytesInKB(long size) {
        return formatBytes(size * 1024, true);
    }

    public static String formatBytesInK(long size) {
        return formatBytes(size * 1024, false);
    }

    public static String formatFloat(float f, int pos) {
        float p = 1f;
        StringBuilder format = new StringBuilder("#0");
        for (int i = 0; i < pos; i++) {
            if (i == 0) {
                format.append('.');
            }
            p *= 10f;
            format.append('0');
        }
        f = Math.round(f * p) / p;
        DecimalFormat formatter = new DecimalFormat(format.toString());
        return formatter.format(f);
    }

    /**
     * Extract the decimal positive integer from specified string.
     * @param str The string to extract.
     * @return
     */
    public static int extractPositiveInteger(String str, int defValue) {
        final int N = str.length();
        int index = 0;

        // Search the first digit character
        while (index < N) {
            char curCh = str.charAt(index);
            if (curCh >= '0' && curCh <= '9') {
                int start = index;
                // Search the first non-digit character
                index++;
                while (index < N) {
                    curCh = str.charAt(index);
                    if (curCh >= '0' && curCh <= '9') {
                        index++;
                    } else {
                        break;
                    }
                }
                String numberStr = str.substring(start, index);
                return Integer.parseInt(numberStr);
            }
            index++;
        }
        return defValue;
    }

    /**
     * Highlight part of a string.
     */
    public static SpannableStringBuilder highlight(String text, int start, int end, int color) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        ForegroundColorSpan span = new ForegroundColorSpan(color);
        spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static int parseInt(String s, int def) {
        try {
            int i = Integer.parseInt(s);
            return i;
        } catch (Exception e) {
            return def;
        }
    }

    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    public static long parseLong(String value, long def) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return def;
        }
    }

    public static float parseFloat(String s) {
        if (s != null) {
            try {
                float i = Float.parseFloat(s);
                return i;
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public static boolean isEmpty(String s) {
        if (s != null) {
            for (int i = 0, count = s.length(); i < count; i++) {
                char c = s.charAt(i);
                if (c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                    return false;
                }
            }
        }
        return true;
    }

    public static String trimAppName(String str) {
        int length = str.length();
        int index = 0;
        while (index < length && (str.charAt(index) <= '\u0020' || str.charAt(index) == '\u00a0'))
            index++;
        if (index > 0)
            return str.substring(index);
        return str;
    }
}
