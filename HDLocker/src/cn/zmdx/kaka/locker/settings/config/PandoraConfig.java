
package cn.zmdx.kaka.locker.settings.config;

import android.content.Context;
import android.content.SharedPreferences;

public class PandoraConfig {

    private static final String SP_NAME_SETTINGS = "sp_name_config";

    private static PandoraConfig sConfig;
    private Context mContext;
    private SharedPreferences mSp;

    private static final int UNLOCKER_TYPE_DEFAULT = 0;

    private static final int UNLOCKER_TYPE_GUSTURE = 1;

    private static final int UNLOCKER_TYPE_NUMBER = 2;

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
        //TODO
        return UNLOCKER_TYPE_DEFAULT;
    }
}
