
package cn.zmdx.kaka.locker.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.initialization.InitializationManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.widget.BaseLinearLayout;
import cn.zmdx.kaka.locker.widget.SwitchButton;

import com.umeng.analytics.MobclickAgent;

public class NotifyFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {

    private View mEntireView;

    private SwitchButton mOpenNotificationRemindSButton;

    private SwitchButton mHideNotifyContentSButton;

    private BaseLinearLayout mNotifyPermission;

    private BaseLinearLayout mNotifyManager;

    private Context mContext;

    private PandoraConfig mPandoraConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mPandoraConfig = PandoraConfig.newInstance(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.notify_fragment, container, false);
        initView();
        initSwitchButtonState();
        return mEntireView;
    }

    private void initView() {
        mOpenNotificationRemindSButton = (SwitchButton) mEntireView
                .findViewById(R.id.setting_open_notification_switch_button);
        mOpenNotificationRemindSButton.setOnCheckedChangeListener(this);

        mHideNotifyContentSButton = (SwitchButton) mEntireView
                .findViewById(R.id.setting_hide_notice_content_switch_button);
        mHideNotifyContentSButton.setOnCheckedChangeListener(this);

        mNotifyPermission = (BaseLinearLayout) mEntireView
                .findViewById(R.id.setting_open_notification_permissions_item);
        mNotifyPermission.setOnClickListener(this);

        mNotifyManager = (BaseLinearLayout) mEntireView
                .findViewById(R.id.setting_notify_manage_item);
        mNotifyManager.setOnClickListener(this);
    }

    private void initSwitchButtonState() {
        mOpenNotificationRemindSButton.setChecked(isNotificationRemindOn());
        mHideNotifyContentSButton.setChecked(isHideNotifyContent());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainSettingsFragment");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainSettingsFragment");
    }

    @Override
    public void onClick(View view) {
        if (view == mNotifyPermission) {
            InitializationManager.getInstance(NotifyFragment.this).initializationLockScreen(
                    InitializationManager.TYPE_READ_NOTIFICATION);
        } else if (view == mNotifyManager) {
            Intent in = new Intent();
            in.setClass(getActivity(), NotifyManagerActivity.class);
            startActivity(in);
            getActivity().overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                    R.anim.umeng_fb_slide_out_from_left);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mOpenNotificationRemindSButton) {
            if (isChecked) {
                enableNotificationRemind();
            } else {
                disableNotificationRemind();
            }
        } else if (buttonView == mHideNotifyContentSButton) {
            if (isChecked) {
                enableHideNotifyContent();
                UmengCustomEventManager.statisticalShowNotifyTimes();
            } else {
                disableHideNotifyContent();
                UmengCustomEventManager.statisticalCloseNotifyTimes();
            }
        }
    }

    private boolean isNotificationRemindOn() {
        return mPandoraConfig.isNotificationRemindOn();
    }

    private void enableNotificationRemind() {
        mPandoraConfig.saveNotificationRemindState(true);
    }

    private void disableNotificationRemind() {
        mPandoraConfig.saveNotificationRemindState(false);
    }

    private boolean isHideNotifyContent() {
        return mPandoraConfig.isHideNotifyContent();
    }

    private void enableHideNotifyContent() {
        mPandoraConfig.saveHideNotifyContentState(true);
    }

    private void disableHideNotifyContent() {
        mPandoraConfig.saveHideNotifyContentState(false);
    }
}
