
package cn.zmdx.kaka.locker.settings;

import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class BaseSettingsFragment extends Fragment {

    

    private PandoraConfig mPandoraConfig;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mPandoraConfig = PandoraConfig.newInstance(mContext);
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
        return mPandoraConfig.getUnLockType();
    }
}
