
package cn.zmdx.kaka.locker.settings.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.theme.ThemeManager;

public class PandoraConfig {

    private static final String SP_NAME_SETTINGS = "sp_name_config";

    private static PandoraConfig sConfig;

    private Context mContext;

    private SharedPreferences mSp;

    public static final int UNLOCKER_TYPE_DEFAULT = 0;

    public static final int UNLOCKER_TYPE_GUSTURE = 1;

    public static final int UNLOCKER_TYPE_NUMBER = 2;

    private static final String PANDORA_LOCKER_SP_NAME = "pandoraLockerName";

    public static final String DATABASE_NAME = "PandoraLocker.db";

    private static final String UNLOCK_TYPE = "a";

    private static final String LOCKPATTERN = "b";

    private static final String THEME_ID = "c";

    private static final String GUIDE_TIMES = "d";

    // 最后一次拉取百度图片的时间
    private static final String KEY_LAST_PULL_BAIDU_TIME = "e";

    private static final String KEY_NEW_VERSION_CHECKED = "f";

    private static final String KEY_CUSTOM_WALLPAPER = "g";

    private static final String KEY_LAST_CHECK_WEATHER = "h";

    private static final String KEY_LAST_WEATHER_INFO = "i";

    public static final int DEFAULT_NO_THRME_INT = -999;

    private static final String KEY_HAS_GUIDED = "j";

    private static final String KEY_TODAY_PULL_ORIGINAL_DATA = "k";

    private static final String KEY_NEED_NOTICE = "keyNeedNotice";

    private static final String KEY_LOCK_DEFAULT_ = "keyLockDefault";

    private static final String KEY_LAST_TIME_PULL_ORIGINAL_DATA = "l";

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
        return mSp.getBoolean(PANDORA_LOCKER_SP_NAME, false);
    }

    public void savePandolaLockerState(boolean isOn) {
        Editor editor = mSp.edit();
        editor.putBoolean(PANDORA_LOCKER_SP_NAME, isOn);
        editor.commit();
    }

    public void saveUnlockType(int type) {
        Editor editor = mSp.edit();
        editor.putInt(UNLOCK_TYPE, type);
        editor.commit();
    }

    public int getUnLockType() {
        return mSp.getInt(UNLOCK_TYPE, UNLOCKER_TYPE_DEFAULT);
    }

    public int getCurrentThemeId() {
        return mSp.getInt(THEME_ID, ThemeManager.THEME_ID_BLUE);
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

    public void saveEventCurrentThemeDaily(String time) {
        Editor editor = mSp.edit();
        editor.putString(UmengCustomEventManager.EVENT_CURRENT_THEME_DAILY, time);
        editor.commit();
    }

    public String getEventCurrentThemeDailyString() {
        return mSp.getString(UmengCustomEventManager.EVENT_CURRENT_THEME_DAILY, "");
    }

    public void saveEventActiveDaily(String time) {
        Editor editor = mSp.edit();
        editor.putString(UmengCustomEventManager.EVENT_ACTIVE_DAILY, time);
        editor.commit();
    }

    public String getEventActiveDailyString() {
        return mSp.getString(UmengCustomEventManager.EVENT_ACTIVE_DAILY, "");
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

    public void saveCustomWallpaperFileName(String fileName) {
        Editor editor = mSp.edit();
        editor.putString(KEY_CUSTOM_WALLPAPER, fileName);
        editor.commit();
    }

    public String getCustomWallpaperFileName() {
        return mSp.getString(KEY_CUSTOM_WALLPAPER, "");
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

    public String getLastWeatherInfo() {
        return mSp.getString(KEY_LAST_WEATHER_INFO, null);
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

    public void saveNeedNotice(boolean isNeed) {
        Editor editor = mSp.edit();
        editor.putBoolean(KEY_NEED_NOTICE, isNeed);
        editor.commit();
    }

    public boolean isNeedNotice() {
        return mSp.getBoolean(KEY_NEED_NOTICE, true);
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
}
