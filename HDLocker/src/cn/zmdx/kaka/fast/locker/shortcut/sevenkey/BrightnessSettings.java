package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

public class BrightnessSettings {
    // Same to Settings.System.SCREEN_BRIGHTNESS_MODE (API 8)
    public static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";

    // Same to Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL (API 8)
    public static final int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;

    // Same to Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC (API 8)
    public static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;

    public static final int BRIGHTNESS_CODE_AUTO = 3;
    public static final int BRIGHTNESS_CODE_MAX = 1;
    public static final int BRIGHTNESS_CODE_MID = 0;
    public static final int BRIGHTNESS_CODE_MIN = 2;

    private static final int BRIGHTNESS_VALUE_INVALID = -1;
    private static final int BRIGHTNESS_VALUE_MIN = 50;
    private static final int BRIGHTNESS_VALUE_MID = 128;
    private static final int BRIGHTNESS_VALUE_MAX = 255;
    private static final int BRIGHTNESS_SEP_MIN_MID = 70;
    private static final int BRIGHTNESS_SEP_MID_MAX = 220;

    private ContentResolver mContentResolver;

    public BrightnessSettings(Context cxt) {
        mContentResolver = cxt.getContentResolver();
    }

    /**
     * @return One of {@link #BRIGHTNESS_CODE_AUTO}, {@link #BRIGHTNESS_CODE_MAX},
     *         {@link #BRIGHTNESS_CODE_MID} or {@link #BRIGHTNESS_CODE_MIN}
     */
    public int getBrightnessCode() {
        if (isAutoModeEnabled()) {
            return BRIGHTNESS_CODE_AUTO;
        }

        int curBrightnessValue = Settings.System.getInt(mContentResolver,
                Settings.System.SCREEN_BRIGHTNESS, 0);
        if (curBrightnessValue < BRIGHTNESS_SEP_MIN_MID) {
            return BRIGHTNESS_CODE_MIN;
        } else if (curBrightnessValue < BRIGHTNESS_SEP_MID_MAX) {
            return BRIGHTNESS_CODE_MID;
        } else {
            return BRIGHTNESS_CODE_MAX;
        }
    }

    /**
     * @return Return the new brightness
     */
    @SuppressWarnings("static-access")
    public int toggleBrightness(Context cxt) {
        int brightnessCode = getBrightnessCode();
        int msgid = 0;
        if (brightnessCode == BrightnessSettings.BRIGHTNESS_CODE_AUTO) {
            // to 1/4 brightness
            enableAutoMode(false);
            setBrightness(BRIGHTNESS_VALUE_MIN);
//            msgid = Res.string.switchwidget_brightness_min;
//            OptimizerApp.toast(msgid, Toast.LENGTH_SHORT);
            cxt.sendBroadcast(new Intent(Constants.ACTION_UPDATE_TRACKER_STATE));
            return BRIGHTNESS_VALUE_MIN;
        } else if (brightnessCode == BrightnessSettings.BRIGHTNESS_CODE_MIN) {
            // to 1/2 brightness
            setBrightness(BRIGHTNESS_VALUE_MID);
//            msgid = Res.string.switchwidget_brightness_mid;
//            OptimizerApp.toast(msgid, Toast.LENGTH_SHORT);
            cxt.sendBroadcast(new Intent(Constants.ACTION_UPDATE_TRACKER_STATE));
            return BRIGHTNESS_VALUE_MID;
        } else if (brightnessCode == BrightnessSettings.BRIGHTNESS_CODE_MID) {
            // to max brightness
            setBrightness(BRIGHTNESS_VALUE_MAX);
//            msgid = Res.string.switchwidget_brightness_max;
//            OptimizerApp.toast(msgid, Toast.LENGTH_SHORT);
            cxt.sendBroadcast(new Intent(Constants.ACTION_UPDATE_TRACKER_STATE));
            return BRIGHTNESS_VALUE_MAX;
        } else if (brightnessCode == BrightnessSettings.BRIGHTNESS_CODE_MAX) {
            // to auto mode
            enableAutoMode(true);
//            msgid = Res.string.switchwidget_brightness_auto;
//            OptimizerApp.toast(msgid, Toast.LENGTH_SHORT);
            cxt.sendBroadcast(new Intent(Constants.ACTION_UPDATE_TRACKER_STATE));
            return BRIGHTNESS_VALUE_INVALID;
        }
        return BRIGHTNESS_VALUE_INVALID;
    }

    private void setBrightness(int value) {
        Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, value);
        // TODO
    }

    private boolean isAutoModeEnabled() {
        int mode = Settings.System.getInt(mContentResolver, SCREEN_BRIGHTNESS_MODE,
                SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        return (mode == SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    private void enableAutoMode(boolean enable) {
        int mode = (enable ? SCREEN_BRIGHTNESS_MODE_AUTOMATIC : SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(mContentResolver, SCREEN_BRIGHTNESS_MODE, mode);
    }
}