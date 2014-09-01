
package cn.zmdx.kaka.locker.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public abstract class BaseSettingsFragment extends Fragment {

    private PandoraConfig mPandoraConfig;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mPandoraConfig = PandoraConfig.newInstance(mContext);
    }

    protected void checkNewVersion() {
        UmengUpdateAgent.forceUpdate(mContext);
    }

    protected void startFeedback() {
        FeedbackAgent agent = new FeedbackAgent(mContext);
        agent.startFeedbackActivity();
    }

    protected void enablePandoraLocker() {
        mPandoraConfig.savePandolaLockerState(true);
    }

    protected void disablePandoraLocker() {
        mPandoraConfig.savePandolaLockerState(false);
    }

    protected boolean isPandoraLockerOn() {
        return mPandoraConfig.isPandolaLockerOn();
    }

    protected void openSystemLocker() {
        mPandoraConfig.saveSystemLockerState(true);
    }

    protected void closeSystemLocker() {
        mPandoraConfig.saveSystemLockerState(false);
    }

    protected boolean isSystemLockerOn() {
        return mPandoraConfig.isSystemLockerOn();
    }

    protected void setUnLockType(int type) {
        // TODO
    }

    protected int getUnLockType() {
        // TODO
        return mPandoraConfig.getUnLockType();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }
}
