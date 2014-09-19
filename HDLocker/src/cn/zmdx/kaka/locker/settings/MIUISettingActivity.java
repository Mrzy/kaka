
package cn.zmdx.kaka.locker.settings;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;

public class MIUISettingActivity extends Activity implements OnClickListener {

    private String mMIUIVersion;

    private Button mCloseSystemLockBtn;

    private Button mFolatfingWindowBtn;

    private Button mTrustBtn;

    private Button mCompleteBtn;

    private PandoraConfig mPandoraConfig;

    private int mThemeId;

    private boolean isMIUI = false;

    private Toast mToast;

    private TextView mFirstLineToast;

    private TextView mSecondLineToast;

    private TextView mThirdLineToast;

    private static final int TOAST_GRAVITY_MAIGIN_TOP = 500;

    private static final int TOAST_SHOW_DURATION = 5000;

    private boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        isMIUI = PandoraUtils.isMIUI(this);
        mMIUIVersion = PandoraUtils.getSystemProperty();
        mPandoraConfig = PandoraConfig.newInstance(this);
        mThemeId = mPandoraConfig.getCurrentThemeId();
        isFirst = getIntent().getBooleanExtra("isFirst", false);
        setContentView(R.layout.setting_miui_fragment);
        initView();
        initToastView();
    }

    private void initView() {
        int resId = ThemeManager.getThemeById(mThemeId).getmBackgroundResId();
        findViewById(R.id.setting_miui_background).setBackgroundResource(resId);

        if (isFirst) {
            findViewById(R.id.setting_close_systemlocker_guide).setVisibility(View.VISIBLE);
        }
        mCloseSystemLockBtn = (Button) findViewById(R.id.setting_close_systemlocker_to_set);
        mCloseSystemLockBtn.setOnClickListener(this);
        mFolatfingWindowBtn = (Button) findViewById(R.id.setting_MIUI_allow_floating_window_to_set);
        mFolatfingWindowBtn.setOnClickListener(this);
        mTrustBtn = (Button) findViewById(R.id.setting_MIUI_trust_to_set);
        mTrustBtn.setOnClickListener(this);
        mCompleteBtn = (Button) findViewById(R.id.setting_miui_complete);
        mCompleteBtn.setOnClickListener(this);

    }

    private void initToastView() {
        mToast = new Toast(getApplication());
        View view = LayoutInflater.from(this).inflate(R.layout.toast_view, null);
        mFirstLineToast = (TextView) view.findViewById(R.id.toast_first_line);
        mSecondLineToast = (TextView) view.findViewById(R.id.toast_second_line);
        mThirdLineToast = (TextView) view.findViewById(R.id.toast_third_line);
        mToast.setGravity(Gravity.TOP, 0, TOAST_GRAVITY_MAIGIN_TOP);
        mToast.setDuration(TOAST_SHOW_DURATION);
        mToast.setView(view);
    }

    private void showToast(boolean isShowThird, Spanned firstPrompr, Spanned secondPrompt,
            String thirdrompt) {
        if (null != firstPrompr) {
            mFirstLineToast.setText(firstPrompr);
        }
        if (null != secondPrompt) {
            mSecondLineToast.setText(secondPrompt);
            mSecondLineToast.setVisibility(View.VISIBLE);
        } else {
            mSecondLineToast.setVisibility(View.GONE);
        }
        if (isShowThird) {
            mThirdLineToast.setVisibility(View.VISIBLE);
            mThirdLineToast.setText(thirdrompt);
        } else {
            mThirdLineToast.setVisibility(View.GONE);
        }
        mToast.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_close_systemlocker_to_set:
                PandoraUtils.closeSystemLocker(MIUISettingActivity.this, isMIUI);
                mCloseSystemLockBtn.setBackgroundResource(R.drawable.setting_miui_button_complete);
                Spanned kFirstLine = Html.fromHtml("请先开启<font color='#0c73f6'>开发者选项</font>开关");
                Spanned kSecondLine = Html.fromHtml("在开启<font color='#0c73f6'>直接进入系统</font>开关");
                String kThirdLine = "*需要先关闭系统屏幕密码";
                showToast(true, kFirstLine, kSecondLine, kThirdLine);
                break;
            case R.id.setting_MIUI_allow_floating_window_to_set:
                PandoraUtils.setAllowFolatWindow(MIUISettingActivity.this, mMIUIVersion);
                mFolatfingWindowBtn.setBackgroundResource(R.drawable.setting_miui_button_complete);
                Spanned xFirstLine = Html.fromHtml("请开启<font color='#0c73f6'>显示悬浮窗</font>开关");
                Spanned xSecondLine = null;
                showToast(false, xFirstLine, xSecondLine, "");
                break;
            case R.id.setting_MIUI_trust_to_set:
                PandoraUtils.setTrust(MIUISettingActivity.this, mMIUIVersion);
                mTrustBtn.setBackgroundResource(R.drawable.setting_miui_button_complete);
                Spanned firstLine = Html.fromHtml("请开启<font color='#0c73f6'>我信任该程序</font>开关");
                Spanned secondLine = Html.fromHtml("在开启<font color='#0c73f6'>自动启动</font>开关");
                showToast(false, firstLine, secondLine, "");
                break;
            case R.id.setting_miui_complete:
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
