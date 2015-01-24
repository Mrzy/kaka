package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("static-access")
public class WidgetConfig implements Parcelable {
    public static final String EXTRA_WIDGET_TYPE = "widget_type";
    public static final String EXTRA_WIDGET_CONFIG = "widget_config";
    public static final String EXTRA_SWITCH_ID = "switch_id";

    public static final int WIDGET_TYPE_INVALID = -1;
    public static final int WIDGET_TYPE_STANDARD = 0;
    public static final int WIDGET_TYPE_DX = 1;
    public static final int WIDGET_TYPE_DXFAST = 2;

    public static final int THEME_TYPE_DEFAULT = 0;
    public static final int THEME_TYPE_DXHOME = 1;

    public static final int BKG_TYPE_WHITE = 0;
    public static final int BKG_TYPE_TRANSLUCENT = 1;
    public static final int BKG_TYPE_TRANSPARENT = 2;

    public static final int SWITCH_ID_INVALID = -1;
    public static final int SWITCH_ID_FROM = 0;
    public static final int SWITCH_ID_TO = 17;
    public static final int SWITCH_ID_WIFI = 0;
    public static final int SWITCH_ID_BLUETOOTH = 1;
    public static final int SWITCH_ID_AUTO_SYNC = 2;
    public static final int SWITCH_ID_AIRPLANE = 3;
    public static final int SWITCH_ID_AUTO_ROTATE = 4;
    public static final int SWITCH_ID_SOUND = 5;
    public static final int SWITCH_ID_PROCESS_MGR = 6;
    public static final int SWITCH_ID_TRASH_CLEAN = 7;
    public static final int SWITCH_ID_FILE_MANAGER = 8;
    public static final int SWITCH_ID_INSTALL_UNINSTALL = 9;
    public static final int SWITCH_ID_POWER_MODE = 10;
    public static final int SWITCH_ID_ONEKEY_STOP = 11;
    public static final int SWITCH_ID_BRIGHTNESS = 12;
    public static final int SWITCH_ID_APN = 13;
    public static final int SWITCH_ID_GPS = 14;
    public static final int SWITCH_ID_LOCKSCREEN = 15;
    public static final int SWITCH_ID_WIDGET_SETTINGS = 16;
    // public static final int SWITCH_ID_NET_FLOW_SWITCH = 17;
    public static final int SWITCH_ID_AUTO_LOCKSCREEN = 18;
    public static final int SWITCH_ID_DXFAST_WIDGET_ACC = 19;
    public static final int SWITCH_ID_DXFAST_WIDGET_SPACE_CLEAR= 20;
    public static final int SWITCH_ID_DXFAST_WIDGET_DASHI_DIAGNOSE= 21;
    public static final int SWITCH_ID_DXFAST_WIDGET_MORE= 22;
    public static final int SWITCH_ID_DXFAST_WIDGET_AD= 23;
    public static final int SWITCH_ID_DXFAST_WIDGET_TOOLS_BOX= 24;
    public static final int SWITCH_ID_DXFAST_WIDGET_MASTER= 25;
    public static final int SWITCH_ID_SETTINGS= 26;

    public static final int[] SWITCH_ID_LIST = new int[] {
        SWITCH_ID_WIFI,
        SWITCH_ID_BLUETOOTH,
        SWITCH_ID_AUTO_SYNC,
        SWITCH_ID_AIRPLANE,
        SWITCH_ID_AUTO_ROTATE,
        SWITCH_ID_SOUND,
        SWITCH_ID_PROCESS_MGR,
        SWITCH_ID_TRASH_CLEAN,
        SWITCH_ID_FILE_MANAGER,
        SWITCH_ID_INSTALL_UNINSTALL,
        SWITCH_ID_POWER_MODE,
        SWITCH_ID_ONEKEY_STOP,
        SWITCH_ID_BRIGHTNESS,
        SWITCH_ID_APN,
        SWITCH_ID_GPS,
        SWITCH_ID_LOCKSCREEN,
        SWITCH_ID_WIDGET_SETTINGS,
        SWITCH_ID_AUTO_LOCKSCREEN,
        SWITCH_ID_DXFAST_WIDGET_ACC,
        SWITCH_ID_DXFAST_WIDGET_SPACE_CLEAR,
        SWITCH_ID_DXFAST_WIDGET_DASHI_DIAGNOSE,
        SWITCH_ID_DXFAST_WIDGET_MORE,
        SWITCH_ID_DXFAST_WIDGET_MASTER
    };

    public static final int[] SWITCH_NAME_LIST = new int[] {
//        Res.string.widget_wifi_set,
//        Res.string.widget_blueTooth_set,
//        Res.string.widget_sysn_set,
//        Res.string.widget_airplane_set,
//        Res.string.widget_rotate_set,
//        Res.string.widget_ring_set,
//        Res.string.widget_task_manager,
//        Res.string.widget_ccleaner,
//        Res.string.widget_file_manager,
//        Res.string.widget_app_manager,
//        Res.string.widget_power_manager,
//        Res.string.widget_onekey_stop,
//        Res.string.widget_brightness_set,
//        Res.string.widget_apn_set,
//        Res.string.widget_gps_set,
//        Res.string.widget_lock_screen,
//        Res.string.widget_settings,
//        Res.string.widget_anto_lock_screen,
//        Res.string.widget_accelerate_content,
//        Res.string.trash_clean_title,
//        Res.string.dashi_diagnose,
//        Res.string.widget_more,
//        Res.string.widget_master
    };

    public static final int[] SWITCH_ICON_DXHOME_LIST = new int[] {
//        Res.drawable.ic_dxhome_wifi_off,
//        Res.drawable.ic_dxhome_bluetooth_off,
//        Res.drawable.ic_dxhome_sync_off,
//        Res.drawable.ic_dxhome_airplane_off,
//        Res.drawable.ic_dxhome_rotate_off,
//        Res.drawable.ic_dxhome_sound_silent,
//        Res.drawable.ic_dxhome_process_manager,
//        Res.drawable.ic_dxhome_ccleaner,
//        Res.drawable.ic_dxhome_file_manager,
//        Res.drawable.app_manager_back,
//        Res.drawable.ic_dxhome_power,
//        Res.drawable.ic_dxhome_onekey_stop,
//        Res.drawable.ic_dxhome_brightness_off,
//        Res.drawable.ic_dxhome_apn_off,
//        Res.drawable.ic_dxhome_gps_off,
//        Res.drawable.ic_dxhome_lockscreen,
//        Res.drawable.ic_dxhome_settings,
//        Res.drawable.ic_dxhome_autolock_30s,
//        Res.drawable.dxfast_widget_accelerate_botton,
//        Res.drawable.dxfast_widget_clean_icon,
//        Res.drawable.widget_logo_bkg,
//        Res.drawable.ic_dxhome_more,
//        Res.drawable.widget_logo_bkg
    };

    private static final int[] SWITCH_ID_LIST_WIDGET = new int[] {
        SWITCH_ID_WIFI,
        SWITCH_ID_BLUETOOTH,
        SWITCH_ID_AUTO_SYNC,
        SWITCH_ID_AIRPLANE,
        SWITCH_ID_AUTO_ROTATE,
        SWITCH_ID_SOUND,
        SWITCH_ID_PROCESS_MGR,
        SWITCH_ID_TRASH_CLEAN,
        SWITCH_ID_FILE_MANAGER,
        SWITCH_ID_INSTALL_UNINSTALL,
        SWITCH_ID_POWER_MODE,
        SWITCH_ID_ONEKEY_STOP,
        SWITCH_ID_BRIGHTNESS,
        SWITCH_ID_APN,
        SWITCH_ID_GPS,
        SWITCH_ID_LOCKSCREEN,
        SWITCH_ID_WIDGET_SETTINGS,
    };

    private static final int[] SWITCH_ID_LIST_WIDGET_PAD = new int[] {
        SWITCH_ID_WIFI,
        SWITCH_ID_BLUETOOTH,
        SWITCH_ID_AUTO_SYNC,
        SWITCH_ID_AIRPLANE,
        SWITCH_ID_AUTO_ROTATE,
        SWITCH_ID_SOUND,
        SWITCH_ID_PROCESS_MGR,
        SWITCH_ID_TRASH_CLEAN,
        SWITCH_ID_FILE_MANAGER,
        SWITCH_ID_INSTALL_UNINSTALL,
        SWITCH_ID_POWER_MODE,
        SWITCH_ID_ONEKEY_STOP,
        SWITCH_ID_BRIGHTNESS,
        SWITCH_ID_GPS,
        SWITCH_ID_LOCKSCREEN,
        SWITCH_ID_WIDGET_SETTINGS,
    };

    public static final int[] SWITCH_ID_LIST_WIDGET_DEFAULT = new int[] {
        SWITCH_ID_WIFI,
        SWITCH_ID_APN,
        SWITCH_ID_BRIGHTNESS,
        SWITCH_ID_AUTO_SYNC,
        SWITCH_ID_AIRPLANE,
        SWITCH_ID_SOUND,
        SWITCH_ID_WIDGET_SETTINGS
    };

    public static final int[] SWITCH_ID_LIST_DXFAST_WIDGET_DEFAULT = new int[] {
         SWITCH_ID_WIFI,
         SWITCH_ID_APN,
         SWITCH_ID_SOUND,
         SWITCH_ID_BRIGHTNESS,
         SWITCH_ID_DXFAST_WIDGET_MORE,
    };

    public static final int[] SWITCH_ID_LIST_DXFAST_WIDGET_DEFAULT_PAD = new int[] {
         SWITCH_ID_WIFI,
         SWITCH_ID_ONEKEY_STOP,
         SWITCH_ID_SOUND,
         SWITCH_ID_BRIGHTNESS,
         SWITCH_ID_DXFAST_WIDGET_MORE,
    };

    public static final int[] SWITCH_ID_LIST_WIDGET_DEFAULT_PAD = new int[] {
        SWITCH_ID_WIFI,
        SWITCH_ID_AUTO_ROTATE,
        SWITCH_ID_BRIGHTNESS,
        SWITCH_ID_AUTO_SYNC,
        SWITCH_ID_AIRPLANE,
        SWITCH_ID_SOUND,
        SWITCH_ID_WIDGET_SETTINGS
    };

    public static final int SWITCH_COUNT = 7;
    public static final int SWITCH_COUNT_DXFAST_WIDGET = 5;

    public int widgetType;
    public int widgetId;
    public int themeType;
    public int bkgType;
    public int[] switchIds;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WidgetConfig{");
        sb.append("type: ").append(widgetType);
        sb.append(", id: ").append(widgetId);
        sb.append(", theme: ").append(themeType);
        sb.append(", bkg: ").append(bkgType);
        sb.append(", switches: ").append(switchConfigToString(switchIds));
        sb.append('}');
        return sb.toString();
    }

    public WidgetConfig cloneIt() {
        WidgetConfig result = new WidgetConfig();
        result.widgetType = widgetType;
        result.widgetId = widgetId;
        result.themeType = themeType;
        result.bkgType = bkgType;
        result.switchIds = new int[switchIds.length];
        System.arraycopy(switchIds, 0, result.switchIds, 0, switchIds.length);
        return result;
    }

    public void copy(WidgetConfig ref) {
        widgetType = ref.widgetType;
        widgetId = ref.widgetId;
        themeType = ref.themeType;
        bkgType = ref.bkgType;
        System.arraycopy(ref.switchIds, 0, switchIds, 0, switchIds.length);
    }

    public boolean containsSwitch(int switchId) {
        for (int id : switchIds) {
            if (id == switchId) {
                return true;
            }
        }
        return false;
    }

    public boolean isLockScreenSwitchUsed() {
        for (int switchId : switchIds) {
            if (switchId == WidgetConfig.SWITCH_ID_LOCKSCREEN) {
                return true;
            }
        }
        return false;
    }

    public static String switchConfigToString(int[] switchConfig) {
        StringBuilder builder = new StringBuilder();
        builder.append(switchConfig[0]);
        for (int i = 1; i < switchConfig.length; i++) {
            builder.append(':');
            builder.append(switchConfig[i]);
        }
        return builder.toString();
    }

    public static final Parcelable.Creator<WidgetConfig> CREATOR = new Parcelable.Creator<WidgetConfig>() {
        public WidgetConfig createFromParcel(Parcel src) {
            WidgetConfig config = new WidgetConfig();
            config.widgetType = src.readInt();
            config.widgetId = src.readInt();
            config.themeType = src.readInt();
            config.bkgType = src.readInt();
            config.switchIds = (config.widgetType == WIDGET_TYPE_DXFAST) ? new int[SWITCH_COUNT_DXFAST_WIDGET] : new int[SWITCH_COUNT];
            src.readIntArray(config.switchIds);
            return config;
        }

        public WidgetConfig[] newArray(int size) {
            return new WidgetConfig[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(widgetType);
        dest.writeInt(widgetId);
        dest.writeInt(themeType);
        dest.writeInt(bkgType);
        dest.writeIntArray(switchIds);
    }

    public static int getSwitchImage(int switchId, int themeType) {
        for (int i = 0; i < WidgetConfig.SWITCH_ID_LIST.length; i++) {
            int id = WidgetConfig.SWITCH_ID_LIST[i];
            if (id == switchId) {
                return WidgetConfig.SWITCH_ICON_DXHOME_LIST[i];
            }
        }
//        return Res.drawable.ic_dxhome_settings;
        return 0;
    }

    public static int getSwitchName(int switchId) {
        for (int i = 0; i < WidgetConfig.SWITCH_ID_LIST.length; i++) {
            int id = WidgetConfig.SWITCH_ID_LIST[i];
            if (id == switchId) {
                return WidgetConfig.SWITCH_NAME_LIST[i];
            }
        }
//        return Res.string.widget_settings;
        return 0;
    }
}
