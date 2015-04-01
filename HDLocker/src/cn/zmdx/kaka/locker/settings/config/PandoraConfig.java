
package cn.zmdx.kaka.locker.settings.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.security.KeyguardLockerManager;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

public class PandoraConfig {

    private static final String SP_NAME_SETTINGS = "sp_name_config";

    private static PandoraConfig sConfig;

    private Context mContext;

    private SharedPreferences mSp;

    private static final String PANDORA_LOCKER_SP_NAME = "pandoraLockerName";

    public static final String DATABASE_NAME = "PandoraLocker.db";

    private static final String UNLOCK_TYPE = "a";

    private static final String LOCKPATTERN = "b";

    private static final String THEME_ID = "c";

    private static final String GUIDE_TIMES = "d";

    // 最后一次拉取百度图片的时间
    private static final String KEY_LAST_PULL_BAIDU_TIME = "e";

    private static final String KEY_NEW_VERSION_CHECKED = "f";

    private static final String KEY_CURRENT_WALLPAPER = "g";

    private static final String KEY_LAST_CHECK_WEATHER = "klcw";

    private static final String KEY_LAST_WEATHER_INFO = "i";

    public static final int DEFAULT_NO_THRME_INT = -999;

    private static final String KEY_HAS_GUIDED = "j";

    private static final String KEY_TODAY_PULL_ORIGINAL_DATA = "k";

    private static final String KEY_NEED_NOTICE = "keyNeedNotice";

    private static final String KEY_LOCK_DEFAULT_ = "keyLockDefault";

    private static final String KEY_LAST_TIME_PULL_ORIGINAL_DATA = "l";

    private static final String KEY_CURRENT_FONT = "m";

    private static final String KEY_DISPLAY_BOX_GUIDE = "o";

    private static final String KEY_NEED_MOBILE_NETWORK = "p";

    private static final String KEY_ONLINE_PULL_TIME = "q";

    private static final String KEY_ONLINE_SERVER_JSON_DATA = "r";

    private static final String OPEN_LOCK_SCREEN_VOCIE = "s";

    private static final String KEY_NUMBER_LOCK = "t";

    private static final String KEY_LOCK_SCREEN_GUIDE_TIMES = "u";

    private static final String KEY_NOTIFICATION_GUIDE_PROGRESS = "w";

    private static final String KEY_HAS_ALREADY_HIDE_NOTIFY_MSG = "x";

    private static final String OPEN_MESSAGE_NOTIFICATION = "y";

    private static final String ONLINE_WALLPAPER_DESC = "awd";

    private static final String KEY_OPEN_NOTIFICATION_REMIND = "konr";

    private static final String KEY_HIDE_NOTIFY_CONTENT = "khnc";

    private static final String KEY_LOCK_PATTERN_STYLE = "klps";

    private static final String KEY_GRAVITY_SENOR = "kgs";

    private static final String KEY_RANDOM_REPLACEMENT = "krp";

    private static final String KEY_LAST_CITY_NAME = "klcn";

    private static final String KEY_LAST_CITY_PROVINCE_NAME = "klcpn";

    private static final String KEY_LAST_CHECK_LOCATION = "klcl";

    private static final String KEY_LAST_SHOW_UNREAD_NEWS = "klsun";

    private static final String KEY_EVENT_NEED_INTERCEPT_APP_DAILY = "kenipd";

    private PandoraConfig(Context context) {
        mContext = context;
        mSp = context.getSharedPreferences(SP_NAME_SETTINGS, Context.MODE_PRIVATE);
    }

    public synchronized static PandoraConfig newInstance(Context context) {
        if (sConfig == null) {
            sConfig = new PandoraConfig(context);
        }
        return sConfig;
    }

    public boolean isPandolaLockerOn() {
        return mSp.getBoolean(PANDORA_LOCKER_SP_NAME, true);
    }

    public void saveLastShowUnreadNews(long curTime) {
        Editor editor = mSp.edit();
        editor.putLong(KEY_LAST_SHOW_UNREAD_NEWS, curTime);
        editor.commit();
    }

    public long getLastShowUnreadNews() {
        return mSp.getLong(KEY_LAST_SHOW_UNREAD_NEWS, 0);
    }

    public void savePandolaLockerState(boolean isEnable) {
        Editor editor = mSp.edit();
        editor.putBoolean(PANDORA_LOCKER_SP_NAME, isEnable);
        editor.commit();
    }

    public void saveUnlockType(int type) {
        Editor editor = mSp.edit();
        editor.putInt(UNLOCK_TYPE, type);
        editor.commit();
    }

    public int getUnLockType() {
        return mSp.getInt(UNLOCK_TYPE, KeyguardLockerManager.UNLOCKER_TYPE_NONE);
    }

    public int getCurrentThemeId() {
        return mSp.getInt(THEME_ID, ThemeManager.THEME_ID_DEFAULT);
    }

    public int getCurrentThemeIdForStatistical() {
        return mSp.getInt(THEME_ID, DEFAULT_NO_THRME_INT);
    }

    public void saveThemeId(int themeId) {
        Editor editor = mSp.edit();
        editor.putInt(THEME_ID, themeId);
        editor.commit();
    }

    public void saveLockPattern(String pattern) {
        Editor editor = mSp.edit();
        editor.putString(LOCKPATTERN, pattern);
        editor.commit();
    }

    public String getLockPaternString() {
        return mSp.getString(LOCKPATTERN, "");
    }

    public void saveGuideTimes(int times) {
        Editor editor = mSp.edit();
        editor.putInt(GUIDE_TIMES, times);
        editor.commit();
    }

    public int getGuideTimesInt() {
        return mSp.getInt(GUIDE_TIMES, 0);
    }

    public void saveEventGuestureLockEnabledDaily(String time) {
        Editor editor = mSp.edit();
        editor.putString(UmengCustomEventManager.EVENT_GUESTURE_LOCK_ENABLED_DAILY, time);
        editor.commit();
    }

    public String getEventGuestureLockEnabledDailyString() {
        return mSp.getString(UmengCustomEventManager.EVENT_GUESTURE_LOCK_ENABLED_DAILY, "");
    }

    public long getLastPullBaiduTime() {
        return mSp.getLong(KEY_LAST_PULL_BAIDU_TIME, 0);
    }

    public void saveLastPullBaiduTime(long time) {
        Editor editor = mSp.edit();
        editor.putLong(KEY_LAST_PULL_BAIDU_TIME, time);
        editor.commit();
    }

    public String getFlagCheckNewVersion() {
        return mSp.getString(KEY_NEW_VERSION_CHECKED, "");
    }

    public void setFlagCheckNewVersionTime(String today) {
        Editor editor = mSp.edit();
        editor.putString(KEY_NEW_VERSION_CHECKED, today);
        editor.commit();
    }

    public void saveCurrentWallpaperFileName(String fileName) {
        Editor editor = mSp.edit();
        editor.putString(KEY_CURRENT_WALLPAPER, fileName);
        editor.commit();
    }

    public String getCurrentWallpaperFileName() {
        return mSp.getString(KEY_CURRENT_WALLPAPER, "");
    }

    public long getLastCheckWeatherTime() {
        return mSp.getLong(KEY_LAST_CHECK_WEATHER, 0);
    }

    public void saveLastCheckWeatherTime(long time) {
        Editor editor = mSp.edit();
        editor.putLong(KEY_LAST_CHECK_WEATHER, time);
        editor.commit();
    }

    public void saveLastWeatherInfo(String info) {
        Editor editor = mSp.edit();
        editor.putString(KEY_LAST_WEATHER_INFO, info);
        editor.commit();
    }

    public void saveLastCityName(String cityName) {
        Editor editor = mSp.edit();
        editor.putString(KEY_LAST_CITY_NAME, cityName);
        editor.commit();
    }

    public String getLastCityName() {
        return mSp.getString(KEY_LAST_CITY_NAME, "");
    }

    public void saveLastCityProvinceName(String cityProvinceName) {
        Editor editor = mSp.edit();
        editor.putString(KEY_LAST_CITY_PROVINCE_NAME, cityProvinceName);
        editor.commit();
    }

    public String getLastCityProvinceName() {
        return mSp.getString(KEY_LAST_CITY_PROVINCE_NAME, "");
    }

    public String getLastWeatherInfo() {
        return mSp.getString(KEY_LAST_WEATHER_INFO, null);
    }

    public void saveLastCheckLocationTime(long lastCheckTime) {
        Editor editor = mSp.edit();
        editor.putLong(KEY_LAST_CHECK_LOCATION, lastCheckTime);
        editor.commit();
    }

    public long getLastCheckLocationTime() {
        return mSp.getLong(KEY_LAST_CHECK_LOCATION, 0l);
    }

    public void saveHasGuided() {
        Editor editor = mSp.edit();
        // 存入数据
        editor.putBoolean(KEY_HAS_GUIDED, true);
        // 提交修改
        editor.commit();
    }

    public boolean isHasGuided() {
        return mSp.getBoolean(KEY_HAS_GUIDED, false);
    }

    public void saveOnlyWifiLoadImage(boolean isEnable) {
        Editor editor = mSp.edit();
        editor.putBoolean(KEY_NEED_MOBILE_NETWORK, isEnable);
        editor.commit();
    }

    public boolean isOnlyWifiLoadImage() {
        return mSp.getBoolean(KEY_NEED_MOBILE_NETWORK, false);
    }

    public void saveLockSoundState(boolean isEnable) {
        Editor editor = mSp.edit();
        editor.putBoolean(OPEN_LOCK_SCREEN_VOCIE, isEnable);
        editor.commit();
    }

    public boolean isLockSoundOn() {
        return mSp.getBoolean(OPEN_LOCK_SCREEN_VOCIE, true);
    }

    public void saveNotifyFunctionState(boolean isEnable) {
        Editor editor = mSp.edit();
        editor.putBoolean(KEY_NEED_NOTICE, isEnable);
        editor.commit();
    }

    public boolean isNotifyFunctionOn() {
        boolean defaultValue = false;
        if (PandoraUtils.isMIUI(mContext) || (BaseInfoHelper.isSupportTranslucentStatus())) {
            defaultValue = true;
        }
        return mSp.getBoolean(KEY_NEED_NOTICE, defaultValue);
    }

    public void saveMessageNotification(boolean isMessage) {
        Editor editor = mSp.edit();
        editor.putBoolean(OPEN_MESSAGE_NOTIFICATION, isMessage);
        editor.commit();
    }

    public boolean isShowNotificationMessage() {
        return mSp.getBoolean(OPEN_MESSAGE_NOTIFICATION, true);
    }

    public void saveLockDefaultFileName(String fileName) {
        Editor editor = mSp.edit();
        editor.putString(KEY_LOCK_DEFAULT_, fileName);
        editor.commit();
    }

    public String getLockDefaultFileName() {
        return mSp.getString(KEY_LOCK_DEFAULT_, "");
    }

    public String getTodayPullOriginalData() {
        return mSp.getString(KEY_TODAY_PULL_ORIGINAL_DATA, "");
    }

    public void saveTodayPullOriginalDataTime(String date) {
        Editor editor = mSp.edit();
        editor.putString(KEY_TODAY_PULL_ORIGINAL_DATA, date);
        editor.commit();
    }

    public long getLastTimePullOriginalData() {
        return mSp.getLong(KEY_LAST_TIME_PULL_ORIGINAL_DATA, 0);
    }

    public void saveLastPullOriginalDataTime(long time) {
        Editor editor = mSp.edit();
        editor.putLong(KEY_LAST_TIME_PULL_ORIGINAL_DATA, time);
        editor.commit();
    }

    /**
     * 如果没有设置第三方字体，会返回null
     * 
     * @return
     */
    public String getCurrentFont() {
        return mSp.getString(KEY_CURRENT_FONT, null);
    }

    public void saveCurrentFont(String fontFilePath) {
        Editor editor = mSp.edit();
        editor.putString(KEY_CURRENT_FONT, fontFilePath);
        editor.commit();
    }

    public boolean getFlagDisplayBoxGuide() {
        return mSp.getBoolean(KEY_DISPLAY_BOX_GUIDE, false);
    }

    public void saveHasAlreadyDisplayBoxGuide() {
        Editor editor = mSp.edit();
        editor.putBoolean(KEY_DISPLAY_BOX_GUIDE, true);
        editor.commit();
    }

    public long getLastOnlinePullTime() {
        return mSp.getLong(KEY_ONLINE_PULL_TIME, 0);
    }

    public void saveLastOnlinePullTime(long lastTime) {
        Editor editor = mSp.edit();
        editor.putLong(KEY_ONLINE_PULL_TIME, lastTime);
        editor.commit();
    }

    public String getLastOnlineServerJsonData() {
        return mSp.getString(KEY_ONLINE_SERVER_JSON_DATA, "");
    }

    public void saveLastOnlineServerJsonData(String lastJsonString) {
        Editor editor = mSp.edit();
        editor.putString(KEY_ONLINE_SERVER_JSON_DATA, lastJsonString);
        editor.commit();
    }

    public String getNumberLockString() {
        return mSp.getString(KEY_NUMBER_LOCK, "");
    }

    public void saveNumberLockString(String md5Password) {
        Editor editor = mSp.edit();
        editor.putString(KEY_NUMBER_LOCK, md5Password);
        editor.commit();
    }

    public int getLockScreenTimes() {
        return mSp.getInt(KEY_LOCK_SCREEN_GUIDE_TIMES, 0);
    }

    public void saveLockScreenTimes(int times) {
        Editor editor = mSp.edit();
        editor.putInt(KEY_LOCK_SCREEN_GUIDE_TIMES, times);
        editor.commit();
    }

    public int getNotificationGuideProgress() {
        return mSp.getInt(KEY_NOTIFICATION_GUIDE_PROGRESS, 0);
    }

    public void saveNotificationGuideProgress(int progress) {
        mSp.edit().putInt(KEY_NOTIFICATION_GUIDE_PROGRESS, progress).commit();
    }

    public boolean hasAlreadyPromptHideNotificationMsg() {
        return mSp.getBoolean(KEY_HAS_ALREADY_HIDE_NOTIFY_MSG, false);
    }

    public void markAlreadyPromptHideNotificationMsg() {
        mSp.edit().putBoolean(KEY_HAS_ALREADY_HIDE_NOTIFY_MSG, true).commit();
    }

    public String getOnlineWallPaperDesc(String fileName) {
        return mSp.getString(ONLINE_WALLPAPER_DESC + fileName, "");
    }

    public void saveOnlineWallPaperDesc(String fileName, String desc) {
        mSp.edit().putString(ONLINE_WALLPAPER_DESC + fileName, desc).commit();
    }

    //
    public boolean isNotificationRemindOn() {
        return mSp.getBoolean(KEY_OPEN_NOTIFICATION_REMIND, true);
    }

    public void saveNotificationRemindState(boolean isEnable) {
        mSp.edit().putBoolean(KEY_OPEN_NOTIFICATION_REMIND, isEnable).commit();
    }

    public boolean isHideNotifyContent() {
        return mSp.getBoolean(KEY_HIDE_NOTIFY_CONTENT, false);
    }

    public void saveHideNotifyContentState(boolean isEnable) {
        mSp.edit().putBoolean(KEY_HIDE_NOTIFY_CONTENT, isEnable).commit();
    }

    public int getLockPatternStyle(int defaultStyle) {
        return mSp.getInt(KEY_LOCK_PATTERN_STYLE, defaultStyle);
    }

    public void saveLockPatternStyle(int type) {
        mSp.edit().putInt(KEY_LOCK_PATTERN_STYLE, type).commit();
    }

    public boolean isGravitySenorOn() {
        return mSp.getBoolean(KEY_GRAVITY_SENOR, true);
    }

    public void saveGravitySenorState(boolean isEnable) {
        mSp.edit().putBoolean(KEY_GRAVITY_SENOR, isEnable).commit();
    }

    public boolean isRandomReplacementOn() {
        return mSp.getBoolean(KEY_RANDOM_REPLACEMENT, false);
    }

    public void saveRandomReplacementState(boolean isEnable) {
        mSp.edit().putBoolean(KEY_RANDOM_REPLACEMENT, isEnable).commit();
    }

    /**
     * TODO
     * 
     * @return
     */
    public boolean isOpenForegroundService() {
        return BuildConfig.DEBUG;
    }

    public String getEventNeedInterceptAppDailyString() {
        return mSp.getString(KEY_EVENT_NEED_INTERCEPT_APP_DAILY, BaseInfoHelper.getCurrentDate());
    }

    public void saveEventNeedInterceptAppDailyData(String time) {
        mSp.edit().putString(KEY_EVENT_NEED_INTERCEPT_APP_DAILY, time).commit();
    }

}
