
package cn.zmdx.kaka.locker.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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

    protected void closeSystemLocker() {
        try {
            Intent intent = new Intent("/");
            ComponentName cm = new ComponentName("com.android.settings",
                    "com.android.settings.ChooseLockGeneric");
            intent.setComponent(cm);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            // 打开开发者选项
            // Intent intent = new
            // Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
            // startActivity(intent);
            
            // 根据包名跳转到系统自带的应用程序信息界面
            // Uri packageURI = Uri.parse("package:" + "cn.zmdx.kaka.locker");
            // Intent intent = new
            // Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
            // startActivity(intent);

        }

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
