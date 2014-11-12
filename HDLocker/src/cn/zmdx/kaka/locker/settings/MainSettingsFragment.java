
package cn.zmdx.kaka.locker.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.widget.PandoraPanelLayout;
import cn.zmdx.kaka.locker.widget.SwitchButton;

import com.umeng.analytics.MobclickAgent;

public class MainSettingsFragment extends BaseSettingsFragment implements OnCheckedChangeListener,
        OnClickListener {
    private View mRootView;

    private PandoraPanelLayout mSettingForeView;

    private LinearLayout mInitSetting;

    private LinearLayout mConcernTeam;

    private LinearLayout mChangeBackground;

    private LinearLayout mSettingIndividualization;

    private LinearLayout mFeedback;

    private LinearLayout mCheckNewVersion;

    private SwitchButton mPandoraLockerSButton;

    private SwitchButton mLockerTypeSButton;

    private ImageView mSettingIcon;

    private View mSettingBackground;

    private boolean mIsCurrentlyPressed = false;

    private static final int TIME_COLLAPSE_PANEL_DELAY = 1500;

    private static final int TIME_COLLAPSE_PANEL_DURATION = 1000;

    public static final int GUSTURE_REQUEST_CODE_SUCCESS = 37;

    public static final int GUSTURE_REQUEST_CODE_FAIL = 38;

    private boolean isMeizu = false;

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
        isMeizu = PandoraUtils.isMeizu(getActivity());
        initView();
        initTitleHeight();
        initSwitchButtonState();
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
                mSettingForeView.collapsePanel(TIME_COLLAPSE_PANEL_DURATION);
            }
        }, TIME_COLLAPSE_PANEL_DELAY);
        return mRootView;
    }

    private void initView() {
        mInitSetting = (LinearLayout) mRootView.findViewById(R.id.setting_init);
        mInitSetting.setOnClickListener(this);
        if (isMeizu) {
            enablePandoraLocker();
            mInitSetting.setVisibility(View.GONE);
        } else {
            mInitSetting.setVisibility(View.VISIBLE);
        }

        mSettingForeView = (PandoraPanelLayout) mRootView.findViewById(R.id.setting_fore_view);
        mSettingForeView.setForegroundResource(R.drawable.splash_bg);
        mSettingIcon = (ImageView) mRootView.findViewById(R.id.setting_icon);
        mSettingIcon.setVisibility(View.GONE);
        mSettingBackground = mRootView.findViewById(R.id.setting_background);

        mSettingIcon.setOnClickListener(this);

        mSettingIndividualization = (LinearLayout) mRootView
                .findViewById(R.id.setting_individualization);
        mSettingIndividualization.setOnClickListener(this);

        mPandoraLockerSButton = (SwitchButton) mRootView
                .findViewById(R.id.setting_pandoralocker_switch_button);
        mPandoraLockerSButton.setOnCheckedChangeListener(this);

        mLockerTypeSButton = (SwitchButton) mRootView
                .findViewById(R.id.setting_pandoralocker_password);
        mLockerTypeSButton.setOnCheckedChangeListener(this);

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
        if (null != PandoraUtils.sCropBitmap) {
            BitmapDrawable drawable = new BitmapDrawable(getResources(), PandoraUtils.sCropBitmap);
            mSettingBackground.setBackgroundDrawable(drawable);
            // mSettingForeView.setForegroundDrawable(getActivity().getResources().getDrawable(
            // ThemeManager.THEME_ID_DEFAULT_FOREGROUND_RESID));
            // mSettingIcon.setBackgroundResource(ThemeManager.THEME_ID_DEFAULT_SETTINGICON_RESID);
        } else {
            initWallpaper();
        }
    }

    @SuppressWarnings("deprecation")
    private void initWallpaper() {
        Theme theme = ThemeManager.getCurrentTheme();
        if (theme.isCustomWallpaper()) {
            BitmapDrawable drawable = theme.getmCustomBitmap();
            if (null == drawable) {
                mSettingBackground.setBackgroundResource(theme.getmBackgroundResId());
            } else {
                mSettingBackground.setBackgroundDrawable(drawable);
            }
        } else {
            mSettingBackground.setBackgroundResource(theme.getmBackgroundResId());
        }
    }

    private void initSwitchButtonState() {
        mPandoraLockerSButton.setChecked(isPandoraLockerOn());
        mLockerTypeSButton.setChecked(getUnLockType() == PandoraConfig.UNLOCKER_TYPE_GUSTURE);
        mIsCurrentlyPressed = true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.setting_pandoralocker_switch_button:
                if (isChecked) {
                    enablePandoraLocker();
                } else {
                    disablePandoraLocker();
                }
                break;
            case R.id.setting_pandoralocker_password:
                if (isChecked) {
                    if (mIsCurrentlyPressed) {
                        UmengCustomEventManager.statisticalPandoraSwitchOpenTimes();
                        showGustureView(LockPatternActivity.LOCK_PATTERN_TYPE_OPEN);
                    }
                } else {
                    if (mIsCurrentlyPressed) {
                        UmengCustomEventManager.statisticalPandoraSwitchCloseTimes();
                        showGustureView(LockPatternActivity.LOCK_PATTERN_TYPE_CLOSE);
                    }
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
        initBackground();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainSettingsFragment");
    }

    private void showGustureView(final int type) {
        mLockerTypeSButton.setEnabled(false);
        gotoGustureActivity(type);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLockerTypeSButton.setEnabled(true);
        switch (resultCode) {
            case GUSTURE_REQUEST_CODE_FAIL:
                int type = data.getExtras().getInt("type");
                mIsCurrentlyPressed = false;
                switch (type) {
                    case LockPatternActivity.LOCK_PATTERN_TYPE_CLOSE:
                        mLockerTypeSButton.setChecked(true);
                        break;
                    case LockPatternActivity.LOCK_PATTERN_TYPE_OPEN:
                        mLockerTypeSButton.setChecked(false);
                        break;

                    default:
                        break;
                }
                mIsCurrentlyPressed = true;
                break;
            default:
                break;
        }
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
                // mIsWallpaperShow = !mIsWallpaperShow;
                // int height = (int) getActivity().getResources().getDimension(
                // R.dimen.setting_wallpaper_bg_height);
                // if (mIsWallpaperShow) {
                // ViewAnimation viewAnimation = new
                // ViewAnimation(mPicScrollView, height,
                // mIsWallpaperShow);
                // viewAnimation.setDuration(VIEW_ANIMATION_DURATION);
                // mPicScrollView.startAnimation(viewAnimation);
                // } else {
                // ViewAnimation viewAnimation = new
                // ViewAnimation(mPicScrollView, height,
                // mIsWallpaperShow);
                // viewAnimation.setDuration(VIEW_ANIMATION_DURATION);
                // mPicScrollView.startAnimation(viewAnimation);
                // }
                break;
            case R.id.setting_icon:
                if (mSettingForeView.isPanelExpanded()) {
                    mSettingForeView.collapsePanel(TIME_COLLAPSE_PANEL_DURATION);
                } else {
                    mSettingForeView.expandPanel();
                }
                break;

            case R.id.setting_init:
                gotoInit();
                break;
            case R.id.setting_individualization:
                gotoIndividualization();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        PandoraUtils.sCropBitmap = null;
//        PandoraUtils.sCropThumbBitmap = null;
        PandoraUtils.sLockDefaultThumbBitmap = null;
        super.onDestroyView();
    }
}
