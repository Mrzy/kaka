package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import android.os.Environment;

public class Constants {
    //点心包名前缀
    public static final String DX_PKG_NAME_PREFIX = "com.dianxinos";

    public static final String REAL_PACKAGE_NAME = "com.dianxinos.optimizer.duplay";
    public static final String ACTION_PACKAGE_NAME = "com.dianxinos.optimizer.duplay";

    // Actions
    public static final String ACTION_UPDATE = ACTION_PACKAGE_NAME + ".action.UPDATE";
    public static final String ACTION_TK_AUTO_TIME_KILLER = ACTION_PACKAGE_NAME + ".action.TK_AUTO_TIME_KILLER";
    public static final String ACTION_TK_SCREEN_OFF_KILLER = ACTION_PACKAGE_NAME + ".action.TK_SCREEN_OFF_KILLER";
    public static final String ACTION_TK_MEMORY_LOW_KILLER = ACTION_PACKAGE_NAME + ".action.TK_MEMORY_LOW_KILLER";
    public static final String ACTION_TK_ONE_HOUR_KILLER = ACTION_PACKAGE_NAME + ".action.TK_ONE_HOUR_KILLER";
    public static final String ACTION_TK_APP_CACHE_KILLER = ACTION_PACKAGE_NAME + ".action.TK_APP_CACHE_KILLER";
    public static final String ACTION_TK_REMAIN_PIC_KILLER = ACTION_PACKAGE_NAME + ".action.TK_REMAIN_PIC_KILLER";
    public static final String ACTION_TK_WIDGET_CLEAR = ACTION_PACKAGE_NAME + ".action.TK_WIDGET_CLEAR";
    public static final String ACTION_TK_WIDGET_REFRESH = ACTION_PACKAGE_NAME + ".action.TK_WIDGET_REFRESH";
    public static final String ACTION_DXWIDGET_UPDATE = ACTION_PACKAGE_NAME + ".action.DXWIDGET_UPDATE";
    public static final String ACTION_FAST_DXWIDGET_UPDATE = ACTION_PACKAGE_NAME + ".action.FAST_DXWIDGET_UPDATE";
    public static final String ACTION_APP_START = ACTION_PACKAGE_NAME + ".action.APP_START";
    public static final String ACTION_PACKAGE_CHANGE = ACTION_PACKAGE_NAME + ".action.PKG_CHANGE";
    public static final String ACTION_MOVE_TO_SD = ACTION_PACKAGE_NAME + ".action.MOVE_TO_SD";
    public static final String ACTION_PENDING_INTENT_BASE = ACTION_PACKAGE_NAME + ".action.PENDING_INTENT_";
    public static final String ACTION_SWITCH_WIDGET_TOGGLE = ACTION_PACKAGE_NAME + ".action.SW_TOGGLE";
    public static final String ACTION_SWITCH_DXFAST_WIDGET_TOGGLE = ACTION_PACKAGE_NAME + ".action.DXFAST_SW_TOGGLE";
    public static final String ACTION_SYSMSG_ACTIVITY = ACTION_PACKAGE_NAME + ".action.SYSMSG_ACT";
    public static final String ACTION_STATUSBAR_REFRESH = ACTION_PACKAGE_NAME + ".action.STATUSBAR_REFRESH";
    public static final String ACTION_AV_UNINSTALL_PACKAGE = ACTION_PACKAGE_NAME + ".action.AV_UNINSTALL_PACKAGE";
    public static final String ACTION_STATUSBAR_NO_ACTION = ACTION_PACKAGE_NAME + ".action.STATUSBAR_NOACTION";
    public static final String ACTION_APK_INSTALL_BG_START = ACTION_PACKAGE_NAME + ".action.APK_INSTALL_BG_S";
    public static final String ACTION_APK_INSTALL_BG_FINISH = ACTION_PACKAGE_NAME + ".action.APK_INSTALL_BG_F";
    public static final String ACTION_ADDETECT_CONFIG_ALLLOGSTOTALCOUNT = ACTION_PACKAGE_NAME + ".action.CONFIG_ALLLOGSTOTALCOUNT";
    public static final String ACTION_APP_INFO_CHANGED = ACTION_PACKAGE_NAME + ".action.GENUINE_CHANGED";
    public static final String ACTION_SPAMSMS_DATA_UPDATE= ACTION_PACKAGE_NAME + ".action.SPAMSMSDATA_UPDATE";
    public static final String ACTION_REPORT_PHONE_LABEL_DATA_UPDATE= ACTION_PACKAGE_NAME + ".action.REPORT_PHONE_LABEL_UPDATE";
    /**
     * used for {@link HomeMonitorService} detect it's home topActivity
     */
    public static final String ACTION_ENTER_HOME = ACTION_PACKAGE_NAME + ".action.ENTER_HOME";
    /**
     * used for {@link HomeMonitorService} detect out of home topActivity or screen off or in phone
     */
    public static final String ACTION_EXIT_HOME = ACTION_PACKAGE_NAME + ".action.EXIT_HOME";
    /**
     * used for {@link HomeMonitorService} force exit home
     */
    public static final String ACTION_FORCE_EXIT_HOME = ACTION_PACKAGE_NAME + ".action.FORCE_EXIT_HOME";
    /**
     * action for netflow window closed event
     */
    public static final String ACTION_ADD_FLOAT_AFTER_LAUNCH = ACTION_PACKAGE_NAME + ".action.ADD_FLOAT_AFTER_LAUNCH";
    public static final String ACTION_NETFLOW_WINDOW_CLOSED = ACTION_PACKAGE_NAME + ".action.NETFLOW_WINDOW_CLOSE";
    public static final String ACTION_NETFLOW_WINDOW_CHANGED = ACTION_PACKAGE_NAME + ".action.NETFLOW_WINDOW_CHANGED";
    // Actions for alarm events
    private static final String ACTION_ALARM_EVENT_BASE = ACTION_PACKAGE_NAME + ".action.ALARM_EVENT";
    public static final String ACTION_ALARM_EVENT_DASHI_DIAGNOSIS_WEEK = ACTION_ALARM_EVENT_BASE + "_DASHI_DIAGNOSIS_WEEK";
    public static final String ACTION_ALARM_EVENT_DASHI_DIAGNOSIS_MONTH = ACTION_ALARM_EVENT_BASE + "_DASHI_DIAGNOSIS_MONTH";
    public static final String ACTION_ALARM_EVENT_SYSMSG = ACTION_ALARM_EVENT_BASE + "_SYSMSG";
    public static final String ACTION_ALARM_EVENT_SDCARD_STORAGE_LOW = ACTION_ALARM_EVENT_BASE + "_SDCARD_STORAGE_LOW";
    public static final String ACTION_ALARM_EVENT_AUTO_APP_CACHE_CLEAN = ACTION_ALARM_EVENT_BASE  + "_APP_CACHE_CLEAN";
    public static final String ACTION_ALARM_EVENT_AUTO_THUMBNAIL_CLEAN = ACTION_ALARM_EVENT_BASE  + "_THUMBNAIL_CLEAN";

    // Action for quick helper
    public static final String ACTION_UPDATE_TRACKER_STATE = ACTION_PACKAGE_NAME + ".action.UPDATE_TRACKER_STATE";

    // Action for dxfast widget
    public static final String ACTION_UPDATE_BRIGHTNESS_TRACKER_STATE = ACTION_PACKAGE_NAME + ".action.UPDATE_BRIGHT_TRACKER_STATE";
    public static final String ACTION_UPDATE_LANGUAGE_CHANGE = ACTION_PACKAGE_NAME + ".action.LANGUAGE_CHANGE_UPDATE_WIDGET";
    public static final String ACTION_REFERSH_WIDGET = ACTION_PACKAGE_NAME + ".action.REFERSH_WIDGET";
    public static final String ACTION_UPDATE_DXHOME_WIDGET_DETAIL = ACTION_PACKAGE_NAME + ".action.UPDATE_DXHOME_WIDGET_DETAIL";
    public static final String ACTION_DXAPPWIDGET_UPDATE = "android.dxwidget.action.DXAPPWIDGET_UPDATE";
    public static final String ACTION_DXFAST_WIDGET_REFERSH_SERVICE_DO_REFERSH = ACTION_PACKAGE_NAME + ".action.DX_WIDGET_SERVICE_DO_REFERSH";
    public static final String ACTION_DXFAST_WIDGET_REFERSH_SERVICE_DO_ACC = ACTION_PACKAGE_NAME + ".action.DX_WIDGET_SERVICE_DO_ACC";
    public static final String ACTION_DXFAST_WIDGET_UPDATE_ACC_STAT = ACTION_PACKAGE_NAME + ".action.DX_WIDGET_UPDATE_ACC_STAT";

    // Action for auto correct
    public static final String ACTION_AC_SEND = ACTION_PACKAGE_NAME + ".action.AC_SEND";
    public static final String ACTION_AC_CHANGED = ACTION_PACKAGE_NAME + ".action.AC_CHANGED";
    public static final String ACTION_AC_RESEND = ACTION_PACKAGE_NAME + ".action.AC_RESEND";
    public static final String ACTION_AC_REPORT = ACTION_PACKAGE_NAME + ".action.AC_REPORT";
    public static final String ACTION_AC_WRONG = ACTION_PACKAGE_NAME + ".action.AC_WRONG";

    // Action for anti spam
    public static final String ACTION_ANTISPAM = ACTION_PACKAGE_NAME + ".action.ANTISPAM";
    public static final String ACTION_ANTISPAM_EVENT = ACTION_PACKAGE_NAME + ".action.ANTISPAM_EVENT";
    public static final String ACTION_SPAM_STRANGER_EVENT = ACTION_PACKAGE_NAME + ".action.SPAM_STRANGER_EVENT";
    public static final String ACTION_ANTISPAM_REPORT = ACTION_PACKAGE_NAME + ".action.ANTISPAM_REPORT";
    public static final String ACTION_ANTISPAM_ACHIEVE_REFRESH = ACTION_PACKAGE_NAME + ".action.ANTISPAM_ACHIEVE_REFRESH";
    public static final String ACTION_ANTISPAM_LABEL_UPDATE_SUCCESS = ACTION_PACKAGE_NAME + ".action.ANTISPAM_LABEL_UPDATE_SUCCESS";

    public static final String ACTION_ANTISPAM_SPAMSMS = ACTION_PACKAGE_NAME + ".action.ANTISPAM_SPAMSMS";
    public static final String ACTION_ANTISPAM_SPAMCALL = ACTION_PACKAGE_NAME + ".action.ANTISPAM_SPAMCALL";

    // Action extras
    public static final String EXTRA_DATA = "extra.data";
    public static final String EXTRA_ACTION = "extra.action";
    public static final String EXTRA_WIZARD = "extra.wizard";
    public static final String EXTRA_MESSAGE = "extra.message";
    public static final String EXTRA_RETRY = "extra.retry";
    public static final String EXTRA_ALLOW_SPLASH = "extra.allow_splash";
    public static final String EXTRA_INTENT = "extra.intent";
    public static final String EXTRA_PROJECT = "extra.project";
    public static final String EXTRA_PKG_NAME = "extra.pkg";
    public static final String EXTRA_APP_NAME = "extra.appname";
    public static final String EXTRA_FILE_PATH = "extra.filepath";
    public static final String EXTRA_SUCCESS = "extra.success";
    public static final String EXTRA_ID = "extra.id";
    public static final String EXTRA_IS_HIGHT_RISK = "extra.is_hight_risk";
    public static final String EXTRA_SHOWPERMISSION = "extra.showpermission";
    public static final String EXTRA_DXFAST_WIDGET = "dxfast.widget";
    public static final String EXTRA_ACHIEVE_UPGRADE="extra.isupgrade";
    public static final String EXTRA_NAVIGATE_FROM_NOTIFICATION = "extra.navigate_from_notification";

    public static final String EXTRA_FROM_KEY = "extra.from";
    public static final int EXTRA_FROM_VALUE_INVALID = -1;
    public static final int EXTRA_FROM_VALUE_STATUSBAR = 0;
    public static final int EXTRA_FROM_VALUE_DXPOWER = 1;
    public static final int EXTRA_FROM_VALUE_SHORTCUT = 2;
    public static final int EXTRA_FROM_VALUE_STATUSBAR_HOLD = 3;
    public static final int EXTRA_FROM_VALUE_FLOAT_WINDOW = 4;
    public static final int EXTRA_FROM_VALUE_DXFAST_WIDGET = 5;
    public static final int EXTRA_FROM_VALUE_WELCOM_SPLASH = 6;
    public static final int EXTRA_FROM_VALUE_MAIN_PAGE = 7;

    public static final String APP_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/AndroidOptimizer/";
    public static final String BACKUP_PATH = APP_PATH + "backup/";
    public static final String NETWORK_BACKUP_PATH = APP_PATH + "networkbackup/";
    public static final String CALL_BACKUP_PATH = APP_PATH + "callbackup/";
    public static final String LINK_BACKUP_PATH = APP_PATH + "linmanbackup/";
    public static final String SMS_BACKUP_PATH = APP_PATH + "smsbackup/";
    public static final String SHORTCUT_BACKUP_PATH = APP_PATH + "shortcut_themepack/";
    public static final String SYSTEM_BACKUP_PATH = APP_PATH + "systembackup/";
    public static final String DOWNLOAD_IMAGE_PATH = APP_PATH + "img_download/";
    public static final String DOWNLOAD_PLUGIN_PATH = APP_PATH + "toolbox_dex/";
    public static final String DOWNLOAD_APK_PATH = APP_PATH + "apkdownloader/";
    public static final String EXTERNAL_PATH_LOGS = APP_PATH + "du_logs/";
    public static final String EXTERNAL_PATH_CONFIG = APP_PATH + "du_config/";

    public static final String DOWNLOAD_TAG_PATCH = APP_PATH + "tag_patch/";

    public static final String DOWNLOAD_REPORT_PHONE_LABEL_PATCH_FOLDER = "phone_label";
    public static final String DOWNLOAD_REPORT_PHONE_LABEL_PATCH_PATH = APP_PATH
            + DOWNLOAD_REPORT_PHONE_LABEL_PATCH_FOLDER + "/";

    // Folders in /data
    public static final String DATA_PLUGINS_EXTRACT_FOLDER = "plugins";

    // Status bar notifications' IDs
    // !!! Please note that value of other notification IDs should be
    //     less than #STATUSBAR_DOWNLOADMGR_START_ID!
    public static final int STATUSBAR_UPDATE_DOWNLOAD_ID = 1;
    public static final int STATUSBAR_UPDATE_ONGOING_ID = 2;
    public static final int STATUSBAR_UPDATE_DONWLOAD_FAILED_ID = 3;
    public static final int STATUSBAR_UPDATE_NOTIFY_ID = 4;
    public static final int STATUSBAR_APPSMGR_MOVE2SD_ID = 5;
    public static final int STATUSBAR_BATTERY_CYCLE_CHARGE_ID = 6;
    public static final int STATUSBAR_BATTERY_TRICKLE_CHARGE_ID = 7;
    public static final int STATUSBAR_QS_AIRPLANE_ID = 8;
    public static final int STATUSBAR_QS_WIFI_ID = 10;
    public static final int STATUSBAR_QS_RING_ID = 11;
    public static final int STATUSBAR_STARTUPMGR_BOOT_TIME_ID = 12;
    public static final int STATUSBAR_DASHI_DIAGNOSIS = 13;
    public static final int STATUSBAR_PERFORMANCE_MONITOR_ID = 14;
    public static final int STATUSBAR_SYSMSG_ID = 15;
    public static final int STATUSBAR_AV_SAFETY_TIP_ID = 16;
    public static final int STATUSBAR_AUTO_CORRECT_ID = 17;
    public static final int STATUSBAR_FEEDBACK_ID = 18;
    public static final int STATUSBAR_APK_DOWNLOADER_PROGRESS_ID = 19;
    public static final int STATUSBAR_APK_DOWNLOADER_FAILED_ID = 20;
    public static final int STATUSBAR_NETFLOW_DISABLE_NETWORK = 21;
    public static final int STATUSBAR_APPS_ISSUE_ID = 22;
    public static final int STATUSBAR_BLOCK_ADS_NOTIFICATION = 23;
    public static final int STATUSBAR_SHOWUNUSEDAPPS = 24;
    public static final int STATUSBAR_ANTISPAM_LABEL_DATA_UPDATE_NOTIFICATION = 25;
    public static final int STATUSBAR_ANTISPAM_ACHIEVE_UPDATE_NOTIFICATION = 26;
    public static final int STATUSBAR_GAME_BOOSTER_OPEN_FLOATWINDOW = 27;
    public static final int STATUSBAR_NOTIFICATION_BLOCK_CALL_AND_SMS = 28;
    public static final int STATUSBAR_NOTIFICATION_NEW_INSTALL_APP_SCAN_SAFE = 29;
    public static final int STATUSBAR_NOTIFICATION_BACKGROUND_SCAN = 30;
    public static final int STATUSBAR_NOTIFICATION_FLOAT_WINDOW = 31;
    public static final int STATUSBAR_DEVICE_STORAGE_LOW = 32;
    public static final int STATUSBAR_SDCARD_STORAGE_LOW = 33;


    // Special IDs for ApkDownloader installed notifications
    public static final int STATUSBAR_APK_DOWNLOADER_INSTALLED_BASE_ID = 100000;
    // !!! Please note that DX AD module is using the ID "11298"
    //     and NotificationManager#cancelAll() may be called!

    public static final int FLAG_EXTERNAL_STORAGE = 1<<18;

    public static final int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
    public static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;

    public static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";

    public static final String ACTION_APPLICATION_DETAILS_SETTINGS = "android.settings.APPLICATION_DETAILS_SETTINGS";

    public static final int MSG_INIT_MAIN = 100;

    public static final long SECOND_MS = 1000l;
    public static final long MINUTE_MS = 1000l * 60;
    public static final long HOUR_MS = MINUTE_MS * 60;
    public static final long DAY_MS = HOUR_MS * 24;
    public static final long WEEK_MS = DAY_MS * 7;
    public static final long TEN_MINUTES_MS = MINUTE_MS * 10;
    public static final long HALF_HOUR_MS = MINUTE_MS * 30;

    public static final long INTERNAL_STORAGE_WARNING_THRESHOLD = 30 * 1024 * 1024l; // 30MB

    // SharedPreferences file names
    public static final String PREFS_FILE_NETSWITCH = "nettimes";
    public static final String PREFS_FILE_MEMORY_MGR = "optimaze_memory";
    public static final String PREFS_FILE_STARTUP_MGR = "DiagnosticSystem";
    public static final String PREFS_FILE_TOOLBOX = "toolbox_set";
    public static final String PREFS_FILE_TOOLBOX2 = "toolbox_set2";
    public static final String PREFS_FILE_NEW_FUNCTION = "new_function";
    public static final String PREFS_FILE_SETTINGS = "settings";
    public static final String PREFS_FILE_NETFLOWMGR = "netflow_config";
    public static final String PREFS_FILE_ANTIVIRUS = "antivirus";
    public static final String PREFS_FILE_PHONE_SPACE = "space";
    public static final String PREFS_FILE_DOWNLOAD = "download";
    public static final String PREFS_FILE_FEEDBACK = "feedback";
    public static final String PREFS_FILE_APPMGR = "appmgr";
    public static final String PREFS_FILE_APPS_MGR = "appmgr_updates";
    public static final String PREFS_FILE_TAPAS_RECOMMEND = "dxrecomm";
    public static final String PREFS_FILE_ADDETECT = "addetect";
    public static final String PREFS_FILE_DIAGNOSTIC = "diagnostic";
    public static final String PREFS_FILE_APPS_ANALYSIS = "analysis";
    public static final String PREFS_FILE_FLOAT_WINDOW = "floatwindow";
    public static final String PREFS_FILE_CONFIG_UPDATE = "config_update";

    // Database file names
    public static final String DB_FILE_TOOLBOX = "toolbox_configs.db";
    public static final String DB_FILE_STARTUP_MANAGER = "startup.db";
    public static final String DB_FILE_ALARM_EVENTS = "al_events.db";
    public static final String DB_FILE_SWITCH_WIDGET_ID = "WidgetIds";
    public static final String DB_FILE_STAT_REPORT_DB = "statstorage.db";
    public static final String DB_FILE_APPS_ANALYSIS = "apps_diagnosis.db";

    // Devices
    public static final String DEVICE_STRING_SAMSUNG = "Samsung";

    public static final String PLAY_PACKAGE_NAME = "com.android.vending";

    // increase this value when update version.
    public static final int CURR_UPDATE_STAMP = 4;

    public static final int EXTRA_FROM_VALUE_INTERNAL_STORAGE_LOW = 6;
    public static final int EXTRA_FROM_VALUE_SDCARD_STORAGE_LOW = 7;
}
