
package cn.zmdx.kaka.locker.settings.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

    private static final String UNLOCK_TYPE = "unlocktype";

    private static final String LOCKPATTERN = "lockPattern";

    private static final String THEME_ID = "theme_id";

    private static final String GUIDE_TIMES = "guideTimes";

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

    public void saveEventGuestureLockTime(String time) {
        Editor editor = mSp.edit();
        editor.putString(UmengCustomEvent.EVENT_GUESTURE_LOCK, time);
        editor.commit();
    }

    public String getEventGuestureLockTimeString() {
        return mSp.getString(UmengCustomEvent.EVENT_GUESTURE_LOCK, "");
    }

    public void saveEventUseThemeTime(String time) {
        Editor editor = mSp.edit();
        editor.putString(UmengCustomEvent.EVENT_USE_THEME_TIMES, time);
        editor.commit();
    }

    public String getEventUseThemeTimeString() {
        return mSp.getString(UmengCustomEvent.EVENT_USE_THEME_TIMES, "");
    }

    public void saveEventEnterLockTime(String time) {
        Editor editor = mSp.edit();
        editor.putString(UmengCustomEvent.EVENT_ENTER_LOCK_TIME, time);
        editor.commit();
    }

    public String getEventEnterLockTimeString() {
        return mSp.getString(UmengCustomEvent.EVENT_ENTER_LOCK_TIME, "");
    }
}
