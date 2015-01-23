package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
//import cn.com.opda.android.sevenkey.GpsSettings;



@SuppressWarnings("static-access")
public class GpsStateTracker extends StateTracker {
    private static final int[] IMG_ON = {
//        Res.drawable.ic_dxhome_gps_on,      // DX Home theme
    };

    private static final int[] IMG_OFF = {
//        Res.drawable.ic_dxhome_gps_off,     // DX Home theme
    };

    public GpsStateTracker() {
        super(WidgetConfig.SWITCH_ID_GPS);
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
    public void refreshActualState(Context cxt) {
        LocationManager locationMgr = (LocationManager) cxt.getSystemService(Context.LOCATION_SERVICE);
        boolean on = false;
        try {
            on = locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (IllegalArgumentException e) {
            // should not happen, but in some system it happened
        } catch (SecurityException e) {
            // Already added "android.permission.ACCESS_FINE_LOCATION" but still got SecurityException
            // Please refer bug [YHDS-1657]
        }
        mState = (on ? STATE_ENABLED : STATE_DISABLED);
    }

    @Override
    public void onActualStateChange(Context cxt, Intent unused) {
        // Note: the broadcast location providers changed intent
        // doesn't include an extras bundles saying what the new value is.
        refreshActualState(cxt);
        setCurrentState(cxt, mState);
    }

    @Override
    public void requestStateChange(Context cxt, boolean enabled) {
//        GpsSettings.ToggleGpsState(cxt, enabled ? 1 : 0);
    }

}
