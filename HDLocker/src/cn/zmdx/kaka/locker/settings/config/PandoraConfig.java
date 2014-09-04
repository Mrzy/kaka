
package cn.zmdx.kaka.locker.settings.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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

    public int getUnLockType() {
        return mSp.getInt(UNLOCK_TYPE, UNLOCKER_TYPE_DEFAULT);
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

}
