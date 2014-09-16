
package cn.zmdx.kaka.locker.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public abstract class BaseSettingsFragment extends Fragment {

    private PandoraConfig mPandoraConfig;

    private Context mContext;

    public boolean isMIUI = false;

    private MAboutFragment mAboutFragment;

    private MIUISettingFragment mMIUIFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mPandoraConfig = PandoraConfig.newInstance(mContext);
        isMIUI = PandoraUtils.isMIUI(mContext);
        mAboutFragment = new MAboutFragment();
        mMIUIFragment = new MIUISettingFragment();
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

    protected void aboutUs() {
        gotoAbout();
    }

    public void gotoAbout() {
        getFragmentManager().beginTransaction().addToBackStack(null)
                .replace(R.id.content, mAboutFragment).commit();
    }

    public void gotoMIUI() {
        getFragmentManager().beginTransaction().addToBackStack(null)
                .replace(R.id.content, mMIUIFragment).commit();
    }

    protected void closeSystemLocker() {
        PandoraUtils.closeSystemLocker(getActivity(), isMIUI);
    }

    protected int getUnLockType() {
        return mPandoraConfig.getUnLockType();
    }

    protected void saveThemeId(int themeId) {
        mPandoraConfig.saveThemeId(themeId);
    }

    protected int getCurrentThemeId() {
        return mPandoraConfig.getCurrentThemeId();
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
