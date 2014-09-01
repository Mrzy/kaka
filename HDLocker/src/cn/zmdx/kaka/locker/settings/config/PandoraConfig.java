
package cn.zmdx.kaka.locker.settings.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PandoraConfig {

    private static final String SP_NAME_SETTINGS = "sp_name_config";

    private static PandoraConfig sConfig;

    private Context mContext;

    private SharedPreferences mSp;

    private static final int UNLOCKER_TYPE_DEFAULT = 0;

    private static final int UNLOCKER_TYPE_GUSTURE = 1;

    private static final int UNLOCKER_TYPE_NUMBER = 2;

    private static final String PANDOLA_LOCKER_SP_NAME = "pandolaLockerName";

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
        // TODO
        return UNLOCKER_TYPE_DEFAULT;
    }

    public boolean isPandolaLockerOn() {
        return mSp.getBoolean(PANDOLA_LOCKER_SP_NAME, true);
    }

    public void savePandolaLockerState(boolean isOn) {
        Editor editor = mSp.edit();
        editor.putBoolean(PANDOLA_LOCKER_SP_NAME, isOn);
        editor.commit();
    }

}
