
package cn.zmdx.kaka.fast.locker.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.widget.SwitchButton;

public class NotificationCenterActivity extends BaseActivity implements OnCheckedChangeListener,
        OnClickListener {

    private SwitchButton mRemindSButton;

    private SwitchButton mPrivacySButton;

    private LinearLayout mNotifyFilterLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);
        initView();
    }

    private void initView() {
        mRemindSButton = (SwitchButton) findViewById(R.id.activity_notification_center_start_remind_switch_button);
        mRemindSButton.setOnCheckedChangeListener(this);
        if (mRemindSButton.isChecked()) {
            UmengCustomEventManager.statisticalEnableAllowNotification();
        }
        mRemindSButton.setChecked(PandoraConfig.newInstance(this).isNotificationActive());
        mPrivacySButton = (SwitchButton) findViewById(R.id.activity_notification_center_start_privacy_switch_button);
        mPrivacySButton.setOnCheckedChangeListener(this);
        if (mPrivacySButton.isChecked()) {
            UmengCustomEventManager.statisticalShowNotification();
        }
        mPrivacySButton.setChecked(!PandoraConfig.newInstance(this).isShowNotificationContent());
        mNotifyFilterLayout = (LinearLayout) findViewById(R.id.activity_notification_center_filter);
        mNotifyFilterLayout.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mPrivacySButton) {
            PandoraConfig.newInstance(this).saveShowNotificationContent(!isChecked);
            UmengCustomEventManager.statisticalHideNotification();
        } else if (buttonView == mRemindSButton) {
            PandoraConfig.newInstance(this).saveNotificationFunctionEnabled(isChecked);
            UmengCustomEventManager.statisticalDisallowNotification();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mNotifyFilterLayout) {
            Intent in = new Intent();
            in.setClass(this, NotifyFilterActivity.class);
            in.putExtra("type", NotifyFilterActivity.TYPE_FILTER);
            startActivity(in);
            overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                    R.anim.umeng_fb_slide_out_from_left);

        }
    }
}
