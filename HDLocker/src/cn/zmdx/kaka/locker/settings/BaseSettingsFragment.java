
package cn.zmdx.kaka.locker.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;

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

    protected void gotoAbout() {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.umeng_fb_slide_in_from_right,
                        R.anim.umeng_fb_slide_out_from_left, R.anim.umeng_fb_slide_in_from_left,
                        R.anim.umeng_fb_slide_out_from_right).add(R.id.content, mMIUIFragment)
                .add(R.id.content, mAboutFragment).commit();
    }

    protected void gotoMIUI() {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.umeng_fb_slide_in_from_right,
                        R.anim.umeng_fb_slide_out_from_left, R.anim.umeng_fb_slide_in_from_left,
                        R.anim.umeng_fb_slide_out_from_right).add(R.id.content, mMIUIFragment)
                .commit();
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
