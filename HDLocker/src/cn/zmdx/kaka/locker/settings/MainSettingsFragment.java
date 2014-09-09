
package cn.zmdx.kaka.locker.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.PandoraBoxDispatcher;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.ui.SettingSwitchButton;

public class MainSettingsFragment extends BaseSettingsFragment implements OnCheckedChangeListener,
        OnClickListener {
    private View mRootView;

    private TextView mPandoraLockerPrompt;

    private TextView mSystemLockerPrompt;

    private TextView mConcernTeam;

    private TextView mChangeBackground;

    private TextView mLockTypePrompt;

    private TextView mFeedback;

    private TextView mCheckNewVersion;

    private SettingSwitchButton mPandoraLockerSButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        PandoraBoxDispatcher.getInstance().sendEmptyMessageDelayed(PandoraBoxDispatcher.MSG_LOAD_BAIDU_IMG, 10000);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.setting_activity, container, false);
        initView();
        initSwitchButtonState();
        return mRootView;
    }

    private void initView() {
        mPandoraLockerPrompt = (TextView) mRootView.findViewById(R.id.setting_pandoralocker_prompt);

        mSystemLockerPrompt = (TextView) mRootView.findViewById(R.id.setting_systemlocker_prompt);
        mSystemLockerPrompt.setOnClickListener(this);

        mLockTypePrompt = (TextView) mRootView.findViewById(R.id.setting_lock_type_prompt);
        mLockTypePrompt.setOnClickListener(this);

        mPandoraLockerSButton = (SettingSwitchButton) mRootView
                .findViewById(R.id.setting_pandoralocker_switch_button);
        mPandoraLockerSButton.setOnCheckedChangeListener(this);

        mFeedback = (TextView) mRootView.findViewById(R.id.setting_feedback_prompt);
        mFeedback.setOnClickListener(this);
        mCheckNewVersion = (TextView) mRootView
                .findViewById(R.id.setting_checkout_new_version_prompt);
        mCheckNewVersion.setOnClickListener(this);

        mConcernTeam = (TextView) mRootView.findViewById(R.id.setting_concern_team);
        mConcernTeam.setOnClickListener(this);
        mChangeBackground = (TextView) mRootView.findViewById(R.id.setting_change_background);
        mChangeBackground.setOnClickListener(this);

    }

    private void initSwitchButtonState() {
        mPandoraLockerSButton.setChecked(isPandoraLockerOn());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.setting_pandoralocker_switch_button:
                if (isChecked) {
                    mPandoraLockerPrompt.setText(getResources().getString(
                            R.string.setting_open_pandoralocker));
                    enablePandoraLocker();
                } else {
                    mPandoraLockerPrompt.setText(getResources().getString(
                            R.string.setting_close_pandoralocker));
                    disablePandoraLocker();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_systemlocker_prompt:
                closeSystemLocker();
                break;
            case R.id.setting_lock_type_prompt:
                setUnLockType(PandoraConfig.UNLOCKER_TYPE_DEFAULT);
                break;
            case R.id.setting_feedback_prompt:
                startFeedback();
                break;
            case R.id.setting_concern_team:
                aboutUs();
                break;
            case R.id.setting_checkout_new_version_prompt:
                checkNewVersion();
                break;
            default:
                break;
        }
    }

}
