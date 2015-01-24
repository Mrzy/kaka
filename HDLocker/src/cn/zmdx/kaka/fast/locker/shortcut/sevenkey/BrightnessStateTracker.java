package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.provider.Settings;
import cn.zmdx.kaka.fast.locker.R;


public class BrightnessStateTracker extends SwitchBase
        implements SettingsSystemObserver.Listener {
    private static final int[] IMG_AUTO = {
        R.drawable.ic_dxhome_brightness_auto,      // DX Home theme
    };

    private static final int[] IMG_MIN = {
        R.drawable.ic_dxhome_brightness_off,     // DX Home theme
    };

    private static final int[] IMG_MID = {
        R.drawable.ic_dxhome_brightness_fairly,     // DX Home theme
    };

    private static final int[] IMG_MAX = {
        R.drawable.ic_dxhome_brightness_on,     // DX Home theme
    };

    private static final String[] BRIGHTNESS_KEYS = {
        Settings.System.SCREEN_BRIGHTNESS,
        BrightnessSettings.SCREEN_BRIGHTNESS_MODE
    };

    private int mBrightnessCode;
    private SettingsSystemObserver mSettingsObserver;

    public BrightnessStateTracker() {
        super(WidgetConfig.SWITCH_ID_BRIGHTNESS);
    }

    public void setupListener(Context cxt) {
        if (mSettingsObserver == null) {
            mSettingsObserver = new SettingsSystemObserver(cxt, null);
            mSettingsObserver.observe(this, BRIGHTNESS_KEYS);
        }
    }

    @Override
    public void onSettingsSystemChanged(Context cxt) {
        updateWidget(cxt);
    }

    @Override
    public int getIconResId(Context cxt, int themeType) {
        switch (mBrightnessCode) {
            case BrightnessSettings.BRIGHTNESS_CODE_MID:
                return IMG_MID[0];
            case BrightnessSettings.BRIGHTNESS_CODE_MIN:
                return IMG_MIN[0];
            case BrightnessSettings.BRIGHTNESS_CODE_AUTO:
                return IMG_AUTO[0];
            case BrightnessSettings.BRIGHTNESS_CODE_MAX:
                return IMG_MAX[0];
            default:
                break;
        }
        return 0;
    }

    @Override
    public void refreshActualState(Context context) {
        mBrightnessCode = new BrightnessSettings(context).getBrightnessCode();
    }

    @Override
    public void onActualStateChange(Context context, Intent intent) {
        // nothing to do
    }

    @Override
    public void toggleState(Context context, WidgetConfig config, Rect sourceBounds) {
//        /// TODO: use su.
//        Activity moreAcitivity = (Activity) ObjectStore.peekInstance().getObject(
//                DXFastWidgetMoreSwitchActivity.OBJECT_ID);
//        // 如果DXFastWidgetMoreSwitchActivity不为空，则使用此activity; 否则需要开一个新窗口以获取windown对象
//        if (moreAcitivity != null) {
//            BrightnessSettingsActivity.hanldeToggleBrightness(moreAcitivity);
//        } else {
            Intent intent = new Intent(context, BrightnessSettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
//        }
    }

    @Override
    public int getIndicatorState() {
        return INDICATOR_STATE_ENABLED;
    }

    public static void updateWidget(Context cxt) {
        cxt.sendBroadcast(new Intent(Constants.ACTION_UPDATE_TRACKER_STATE));
        cxt.sendBroadcast(new Intent(Constants.ACTION_UPDATE_BRIGHTNESS_TRACKER_STATE));
    }

}
