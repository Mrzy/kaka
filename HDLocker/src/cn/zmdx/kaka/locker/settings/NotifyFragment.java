
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
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.initialization.InitializationManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.widget.SwitchButton;

import com.umeng.analytics.MobclickAgent;

public class NotifyFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {

    private View mEntireView;

    private SwitchButton mOpenNotificationRemindSButton;

    private SwitchButton mHideNotifyContentSButton;

    private SwitchButton mLightScreenSButton;

    private LinearLayout mNotifyPermission;

    private LinearLayout mNotifyManager;

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
                .findViewById(R.id.setting_hide_content_switch_button);
        mHideNotifyContentSButton.setOnCheckedChangeListener(this);

        mLightScreenSButton = (SwitchButton) mEntireView
                .findViewById(R.id.setting_light_screen_switch_button);
        mLightScreenSButton.setOnCheckedChangeListener(this);

        mNotifyPermission = (LinearLayout) mEntireView
                .findViewById(R.id.setting_open_permissions_item);
        mNotifyPermission.setOnClickListener(this);

        mNotifyManager = (LinearLayout) mEntireView.findViewById(R.id.setting_notify_manage_item);
        mNotifyManager.setOnClickListener(this);
    }

    private void initSwitchButtonState() {
        mOpenNotificationRemindSButton.setChecked(isNotificationRemindOn());
        mHideNotifyContentSButton.setChecked(isHideNotifyContent());
        mLightScreenSButton.setChecked(isLightScreenOn());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NotifyFragment");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NotifyFragment");
    }

    @Override
    public void onClick(View view) {
        if (view == mNotifyPermission) {
            InitializationManager.getInstance(getActivity()).initializationLockScreen(
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
                UmengCustomEventManager.statisticalOpenNotificationRemindTimes();
            } else {
                UmengCustomEventManager.statisticalCloseNotificationRemindTimes();
                disableNotificationRemind();
            }
        } else if (buttonView == mHideNotifyContentSButton) {
            if (isChecked) {
                enableHideNotifyContent();
                UmengCustomEventManager.statisticalshowNotifyContentTimes();
            } else {
                disableHideNotifyContent();
                UmengCustomEventManager.statisticalHideNotifyContentTimes();
            }
        } else if (buttonView == mLightScreenSButton) {
            if (isChecked) {
                enableLightScreen();
            } else {
                disableLightScreen();
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

    private boolean isLightScreenOn() {
        return mPandoraConfig.isLightScreenOn();
    }

    private void enableLightScreen() {
        mPandoraConfig.saveLightScreenState(true);
    }

    private void disableLightScreen() {
        mPandoraConfig.saveLightScreenState(false);
    }

}
