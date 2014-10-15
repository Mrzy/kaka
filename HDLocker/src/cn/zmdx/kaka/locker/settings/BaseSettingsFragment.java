
package cn.zmdx.kaka.locker.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;

import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

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
        isUpdate();
    }

    private void isUpdate() {
        UmengUpdateAgent.forceUpdate(mContext);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case 0: // has update
                        UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
                        break;
                    case 1: // has no update
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.update_prompt_no_update),
                                Toast.LENGTH_LONG).show();
                        break;
                    case 2: // none wifi
                        Toast.makeText(mContext,
                                mContext.getResources().getString(R.string.update_prompt_no_wify),
                                Toast.LENGTH_LONG).show();
                        break;
                    case 3: // time out
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(
                                        R.string.update_prompt_no_internet), Toast.LENGTH_LONG)
                                .show();
                        break;
                }
            }
        });
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
        Intent in = new Intent();
        in.setClass(getActivity(), MAboutActivity.class);
        startActivity(in);
        getActivity().overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    protected void gotoGustureActivity(int type) {
        Intent in = new Intent();
        in.setClass(getActivity(), LockPatternActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        in.putExtra("bundle", bundle);
        startActivityForResult(in, MainSettingsFragment.GUSTURE_REQUEST_CODE_SUCCESS);
        getActivity().overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    protected void gotoInit() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), InitSettingActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    protected int getUnLockType() {
        return mPandoraConfig.getUnLockType();
    }

    protected void saveThemeId(int themeId) {
        ThemeManager.saveTheme(themeId);
    }

    protected int getCurrentThemeId() {
        return ThemeManager.getCurrentTheme().getmThemeId();
    }

    protected void gotoWallpaper() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), WallPaperActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);

    }

    protected boolean isHaveCustomWallpaper() {
        return PandoraUtils.isHaveCustomWallpaper(getActivity());
    }

    protected String getCustomWallpaperFileName() {
        return mPandoraConfig.getCustomWallpaperFileName();
    }
}
