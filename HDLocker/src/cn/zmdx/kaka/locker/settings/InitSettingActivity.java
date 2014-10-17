
package cn.zmdx.kaka.locker.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.custom.wallpaper.CustomWallpaperManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.umeng.analytics.MobclickAgent;

public class InitSettingActivity extends Activity implements OnClickListener {

    private String mMIUIVersion;

    private Button mCloseSystemLockBtn;

    private Button mFolatfingWindowBtn;

    private Button mTrustBtn;

    private TypefaceTextView mCompleteBtn;

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
        initWallpaper();
    }

    private void initView() {
        if (isMIUI) {
            findViewById(R.id.init_setting_MIUI_allow_floating_window_guide).setVisibility(
                    View.VISIBLE);
            findViewById(R.id.init_setting_MIUI_trust_guide).setVisibility(View.VISIBLE);
        }
        mCloseSystemLockBtn = (Button) findViewById(R.id.init_setting_close_systemlocker_to_set);
        mCloseSystemLockBtn.setOnClickListener(this);
        mFolatfingWindowBtn = (Button) findViewById(R.id.init_setting_MIUI_allow_floating_window_to_set);
        mFolatfingWindowBtn.setOnClickListener(this);
        mTrustBtn = (Button) findViewById(R.id.init_setting_MIUI_trust_to_set);
        mTrustBtn.setOnClickListener(this);
        mCompleteBtn = (TypefaceTextView) findViewById(R.id.init_setting_miui_complete);
        mCompleteBtn.setOnClickListener(this);

    }

    @SuppressWarnings("deprecation")
    private void initWallpaper() {
        View view = findViewById(R.id.init_setting_background);
        int themeId = mPandoraConfig.getCurrentThemeId();
        if (themeId == -1) {
            String fileName = PandoraConfig.newInstance(this).getCustomWallpaperFileName();
            String path = CustomWallpaperManager.getCustomWallpaperFilePath(fileName);
            Bitmap bitmap = PandoraUtils.getBitmap(path);
            if (null == bitmap) {
                view.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.setting_background_blue));
            } else {
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                view.setBackgroundDrawable(drawable);
            }
        } else {
            int resId = ThemeManager.getThemeById(mThemeId).getmBackgroundResId();
            view.setBackgroundResource(resId);
        }

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
                mCloseSystemLockBtn.setBackgroundResource(R.drawable.base_button_pressed);
                showPromptActicity(isMIUI, InitPromptActivity.PROMPT_CLOSE_SYSTEM_LOCKER);
                break;
            case R.id.init_setting_MIUI_allow_floating_window_to_set:
                PandoraUtils.setAllowFolatWindow(InitSettingActivity.this, mMIUIVersion);
                mFolatfingWindowBtn.setBackgroundResource(R.drawable.base_button_pressed);
                showPromptActicity(isMIUI, InitPromptActivity.PROMPT_ALLOW_FLOAT_WINDOW);
                break;
            case R.id.init_setting_MIUI_trust_to_set:
                PandoraUtils.setTrust(InitSettingActivity.this, mMIUIVersion);
                mTrustBtn.setBackgroundResource(R.drawable.base_button_pressed);
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

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("InitSettingActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("InitSettingActivity"); // 保证 onPageEnd 在onPause
        // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
