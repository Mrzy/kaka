package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import cn.zmdx.kaka.fast.locker.R;

public class AirplaneStateTracker extends StateSwitch {
    private static final int[] IMG_ON = {
        R.drawable.ic_dxhome_airplane_on,      // DX Home theme
    };

    private static final int[] IMG_OFF = {
        R.drawable.ic_dxhome_airplane_off,     // DX Home theme
    };

    public AirplaneStateTracker() {
        super(WidgetConfig.SWITCH_ID_AIRPLANE);
    }

    @Override
    public int getIconResId(Context cxt, int themeType) {
        if (mState == STATE_ENABLED) {
            return IMG_ON[0];
        } else {
            return IMG_OFF[0];
        }
    }

    @Override
    public void refreshActualState(Context context) {
        AirplaneSettings airplaneSettings = new AirplaneSettings(context);
        mState = (airplaneSettings.getAirplaneState() ? STATE_ENABLED : STATE_DISABLED);
    }

    @Override
    public void onActualStateChange(Context context, Intent intent) {
        // nothing to do
    }

    @Override
    public void toggleState(Context cxt, WidgetConfig config, Rect sourceBounds) {
        AirplaneSettings airplaneSettings = new AirplaneSettings(cxt);
        boolean b = airplaneSettings.getAirplaneState();
        airplaneSettings.setAirplaneState(!b);
    }

}
