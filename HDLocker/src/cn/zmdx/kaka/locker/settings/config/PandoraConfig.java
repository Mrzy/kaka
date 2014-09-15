
package cn.zmdx.kaka.locker.settings.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.SparseIntArray;
import cn.zmdx.kaka.locker.R;
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

    public static boolean sDebug = true;

    public static final String DATABASE_NAME = "PandoraLocker.db";

    private static final String UNLOCK_TYPE = "unlocktype";

    private static final String WHICH_WALLPAPER = "whichWallpaper";

    private static final String LOCKPATTERN = "lockPattern";

    private static final String THEME_ID = "theme_id";

    public static final int[] sThumbWallpapers = {
            R.drawable.setting_wallpaper_blue, R.drawable.setting_wallpaper_green,
            R.drawable.setting_wallpaper_purple, R.drawable.setting_wallpaper_yellow
    };

    public static SparseIntArray sBackgroundArray = new SparseIntArray();

    public static SparseIntArray sForeBackgroundArray = new SparseIntArray();

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

    public void saveWhichWallpaper(int which) {
        Editor editor = mSp.edit();
        editor.putInt(WHICH_WALLPAPER, which);
        editor.commit();

    }

    public int getWhichWallpaper() {
        return mSp.getInt(WHICH_WALLPAPER, 0);
    }

    public int getWhichWallpaperResId() {
        return sBackgroundArray.get(sThumbWallpapers[getWhichWallpaper()]);
    }

    /*
     * 返回前景颜色对应resId
     */
    public int getWhichForeWallpaperResId() {
        return sForeBackgroundArray.get(getWhichWallpaperResId());
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
}
