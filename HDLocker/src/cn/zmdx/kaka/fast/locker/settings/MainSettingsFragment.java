
package cn.zmdx.kaka.fast.locker.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.fast.locker.theme.ThemeManager;
import cn.zmdx.kaka.fast.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.fast.locker.widget.SwitchButton;

import com.umeng.analytics.MobclickAgent;

public class MainSettingsFragment extends BaseSettingsFragment implements OnCheckedChangeListener,
        OnClickListener {
    private View mRootView;

    private LinearLayout mInitSetting;

    private LinearLayout mConcernTeam;

    private LinearLayout mChangeBackground;

    private LinearLayout mSettingIndividualization;

    private LinearLayout mFeedback;

    private LinearLayout mCheckNewVersion;

    private SwitchButton mPandoraLockerSButton;

    private LinearLayout mLockType;

    private View mSettingBackground;

    public static final int GUSTURE_REQUEST_CODE_SUCCESS = 37;

    public static final int GUSTURE_REQUEST_CODE_FAIL = 38;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.pandora_setting, container, false);
        initView();
//        initTitleHeight();
        initSwitchButtonState();
        return mRootView;
    }

    private void initView() {
        mInitSetting = (LinearLayout) mRootView.findViewById(R.id.setting_init);
        mInitSetting.setOnClickListener(this);
        mInitSetting.setVisibility(View.VISIBLE);

        mSettingBackground = mRootView.findViewById(R.id.setting_background);

        mSettingIndividualization = (LinearLayout) mRootView
                .findViewById(R.id.setting_individualization);
        mSettingIndividualization.setOnClickListener(this);

        mPandoraLockerSButton = (SwitchButton) mRootView
                .findViewById(R.id.setting_pandoralocker_switch_button);
        mPandoraLockerSButton.setOnCheckedChangeListener(this);

        mLockType = (LinearLayout) mRootView.findViewById(R.id.setting_lock_type_prompt);
        mLockType.setOnClickListener(this);

        mFeedback = (LinearLayout) mRootView.findViewById(R.id.setting_feedback_prompt);
        mFeedback.setOnClickListener(this);
        mCheckNewVersion = (LinearLayout) mRootView
                .findViewById(R.id.setting_checkout_new_version_prompt);
        mCheckNewVersion.setOnClickListener(this);

        mConcernTeam = (LinearLayout) mRootView.findViewById(R.id.setting_concern_team);
        mConcernTeam.setOnClickListener(this);
        mChangeBackground = (LinearLayout) mRootView.findViewById(R.id.setting_change_background);
        mChangeBackground.setOnClickListener(this);
    }

    private void initTitleHeight() {
        int statusBarHeight = PandoraUtils.getStatusBarHeight(getActivity());
        LinearLayout titleLayout = (LinearLayout) mRootView
                .findViewById(R.id.pandora_setting_title);
        titleLayout.setPadding(0, statusBarHeight, 0, 0);
    }

    @SuppressWarnings("deprecation")
    private void initBackground() {
        Theme theme = ThemeManager.getCurrentTheme();
        mSettingBackground.setBackgroundDrawable(theme.getCurDrawable());
    }

    private void initSwitchButtonState() {
        mPandoraLockerSButton.setChecked(isPandoraLockerOn());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.setting_pandoralocker_switch_button:
                enablePandoraLocker();
                if (isChecked) {
                    UmengCustomEventManager.statisticalPandoraSwitchOpenTimes();
                } else {
                    disablePandoraLocker();
                    UmengCustomEventManager.statisticalPandoraSwitchCloseTimes();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onResume() {
        mPandoraLockerSButton.setChecked(isPandoraLockerOn());
        super.onResume();
        MobclickAgent.onPageStart("MainSettingsFragment"); // 统计页面
//        initBackground();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainSettingsFragment");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_feedback_prompt:
                startFeedback();
                break;
            case R.id.setting_concern_team:
                gotoAbout();
                break;
            case R.id.setting_checkout_new_version_prompt:
                checkNewVersion();
                break;
            case R.id.setting_change_background:
                gotoWallpaper();
                break;
            case R.id.setting_init:
                gotoInit();
                break;
            case R.id.setting_individualization:
                gotoIndividualization();
                break;
            case R.id.setting_lock_type_prompt:
                gotoLockerPassword();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        PandoraUtils.sLockDefaultThumbBitmap = null;
        super.onDestroyView();
    }
}
