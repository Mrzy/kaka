
package cn.zmdx.kaka.locker.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;

public class InitSettingActivity extends Activity implements OnClickListener {

    private String mMIUIVersion;

    private Button mCloseSystemLockBtn;

    private Button mFolatfingWindowBtn;

    private Button mTrustBtn;

    private Button mCompleteBtn;

    private PandoraConfig mPandoraConfig;

    private int mThemeId;

    private boolean isMIUI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        isMIUI = PandoraUtils.isMIUI(this);
        mMIUIVersion = PandoraUtils.getSystemProperty();
        mPandoraConfig = PandoraConfig.newInstance(this);
        mThemeId = mPandoraConfig.getCurrentThemeId();
        setContentView(R.layout.init_setting_fragment);
        initView();
    }

    private void initView() {
        int resId = ThemeManager.getThemeById(mThemeId).getmBackgroundResId();
        findViewById(R.id.init_setting_background).setBackgroundResource(resId);
        if (isMIUI) {
            findViewById(R.id.init_setting_MIUI_allow_floating_window_guide).setVisibility(View.VISIBLE);
            findViewById(R.id.init_setting_MIUI_trust_guide).setVisibility(View.VISIBLE);
        }
        mCloseSystemLockBtn = (Button) findViewById(R.id.init_setting_close_systemlocker_to_set);
        mCloseSystemLockBtn.setOnClickListener(this);
        mFolatfingWindowBtn = (Button) findViewById(R.id.init_setting_MIUI_allow_floating_window_to_set);
        mFolatfingWindowBtn.setOnClickListener(this);
        mTrustBtn = (Button) findViewById(R.id.init_setting_MIUI_trust_to_set);
        mTrustBtn.setOnClickListener(this);
        mCompleteBtn = (Button) findViewById(R.id.init_setting_miui_complete);
        mCompleteBtn.setOnClickListener(this);

    }

    private void showPromptActicity(boolean isMIUI, int type) {
        Intent in = new Intent();
        in.setClass(this, InitPromptActivity.class);
        in.putExtra("isMIUI", isMIUI);
        in.putExtra("type", type);
        startActivity(in);
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.init_setting_close_systemlocker_to_set:
                PandoraUtils.closeSystemLocker(InitSettingActivity.this, isMIUI);
                mCloseSystemLockBtn.setBackgroundResource(R.drawable.setting_miui_button_complete);
                showPromptActicity(isMIUI, InitPromptActivity.PROMPT_CLOSE_SYSTEM_LOCKER);
                break;
            case R.id.init_setting_MIUI_allow_floating_window_to_set:
                PandoraUtils.setAllowFolatWindow(InitSettingActivity.this, mMIUIVersion);
                mFolatfingWindowBtn.setBackgroundResource(R.drawable.setting_miui_button_complete);
                showPromptActicity(isMIUI, InitPromptActivity.PROMPT_ALLOW_FLOAT_WINDOW);
                break;
            case R.id.init_setting_MIUI_trust_to_set:
                PandoraUtils.setTrust(InitSettingActivity.this, mMIUIVersion);
                mTrustBtn.setBackgroundResource(R.drawable.setting_miui_button_complete);
                showPromptActicity(isMIUI, InitPromptActivity.PROMPT_TRRST);
                break;
            case R.id.init_setting_miui_complete:
                onBackPressed();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }
}
