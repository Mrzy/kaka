
package cn.zmdx.kaka.locker.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class BaseSettingsFragment extends Fragment {

    private static final String SP_NAME_SETTINGS = "sp_name_settings";

    private static final int UNLOCKER_TYPE_DEFAULT = 0;

    private static final int UNLOCKER_TYPE_GUSTURE = 1;

    private static final int UNLOCKER_TYPE_NUMBER = 2;

    private SharedPreferences mSp;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mSp = mContext.getSharedPreferences(SP_NAME_SETTINGS, Context.MODE_PRIVATE);
    }

    protected boolean checkNewVersion() {
        // TODO
        return false;
    }

    protected void startFeedback() {
        // TODO
    }

    protected void enablePandoraLocker() {
        // TODO
    }

    protected void disablePandoraLocker() {
        // TODO
    }

    protected boolean isPandoraLockerOn() {
        // TODO
        return true;
    }

    protected void closeSystemLocker() {
        // TODO
    }

    protected void setUnLockType(int type) {
        // TODO
    }

    protected int getUnLockType() {
        // TODO
        return UNLOCKER_TYPE_DEFAULT;
    }
}
