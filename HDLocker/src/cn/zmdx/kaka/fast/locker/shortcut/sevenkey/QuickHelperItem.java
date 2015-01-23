package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import cn.zmdx.kaka.fast.locker.HDApplication;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class QuickHelperItem {
    public static final int TYPE_SWITCH = 1;
    public static final int TYPE_CONTACTS = 2;
    public static final int TYPE_APP = 3;
    public static final int TYPE_ADD = 4;

    public int type;
    public int switchType = -1;
    public SwitchBase tracker;
    public String pkgName = "";
    public String activityName = "";
    public Uri uri;
    public boolean isDefault;
    public int position;
    public Drawable appIcon;
    public String appName;
    public Drawable contactsPhoto;
    public String contactsName;
    public boolean isUnkownContacts;

    public QuickHelperItem() {
    }

    public QuickHelperItem(int type, Uri uri) {
        this.type = type;
        this.uri = uri;
    }

    public QuickHelperItem(int type, boolean isDefault) {
        this.type = type;
        this.isDefault = isDefault;
    }

    public QuickHelperItem(int type, int switchType) {
        this.type = type;
        this.switchType = switchType;
        findTrackerBySwitchType(switchType);
    }

    public QuickHelperItem(int type, int switchType, boolean isDefault, int position) {
        this.type = type;
        this.switchType = switchType;
        this.isDefault = isDefault;
        this.position = position;
        findTrackerBySwitchType(switchType);
    }

    public QuickHelperItem(int type, String pkgName, String activityName, Drawable appIcon, String appName) {
        this.type = type;
        this.pkgName = pkgName;
        this.activityName = activityName;
        this.appIcon = appIcon;
        this.appName = appName;
    }

    public QuickHelperItem(int type, Uri uri, Drawable contactsPhoto, String contactsName) {
        this.type = type;
        this.uri = uri;
        this.contactsPhoto = contactsPhoto;
        this.contactsName = contactsName;
    }

    public void findTrackerBySwitchType(int switchType) {
        if (tracker != null)
            return;
        switch (switchType) {
            case WidgetConfig.SWITCH_ID_WIFI:
                this.tracker = new WifiStateTracker();
                break;
            case WidgetConfig.SWITCH_ID_BLUETOOTH:
//                this.tracker = new BluetoothStateTracker();
                break;
            case WidgetConfig.SWITCH_ID_AUTO_SYNC:
//                this.tracker = new SyncStateTracker();
                break;
            case WidgetConfig.SWITCH_ID_AIRPLANE:
                this.tracker = new AirplaneStateTracker();
                break;
            case WidgetConfig.SWITCH_ID_AUTO_ROTATE:
//                this.tracker = new AutoRotateStateTracker();
//                ((AutoRotateStateTracker) this.tracker).setupListener(OptimizerApp.getInstance());
                break;
            case WidgetConfig.SWITCH_ID_SOUND:
                this.tracker = new SoundStateTracker();
                break;
            case WidgetConfig.SWITCH_ID_ONEKEY_STOP:
//                this.tracker =NoStateSwitch.getOnekeyStopSwitch();
                break;
            case WidgetConfig.SWITCH_ID_BRIGHTNESS:
                this.tracker = new BrightnessStateTracker();
                ((BrightnessStateTracker) this.tracker).setupListener(HDApplication.getContext());
                break;
            case WidgetConfig.SWITCH_ID_APN:
//                this.tracker = new ApnStateTracker();
                break;
            case WidgetConfig.SWITCH_ID_GPS:
                this.tracker = new GpsStateTracker();
                break;
            case WidgetConfig.SWITCH_ID_LOCKSCREEN:
//                this.tracker = NoStateSwitch.getLockScreenSwitch();
                break;
            case WidgetConfig.SWITCH_ID_AUTO_LOCKSCREEN:
//                this.tracker = new AutoLockScreenStateTracker();
                break;
            default:
                break;
        }
    }
}
