
package cn.zmdx.kaka.locker.meiwen.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.meiwen.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.meiwen.sound.LockSoundManager;
import cn.zmdx.kaka.locker.meiwen.widget.SwitchButton;

import com.umeng.analytics.MobclickAgent;

public class IndividualizationActivity extends BaseActivity implements OnClickListener,
        OnCheckedChangeListener {
    public static String LOCK_DEFAULT_SDCARD_LOCATION = Environment.getExternalStorageDirectory()
            .getPath() + "/.Pandora/lockDefault/";

    private View mRootView;

    private SwitchButton mNoticeSButton;

    private SwitchButton mNoticeMobileNetworkSButton;

    private SwitchButton mLockScreenVoiceSButton;

    private SwitchButton mNotificationSButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(Res.layout.pandora_individualization);
        initView();
    }

    private void initView() {
        mRootView = findViewById(Res.id.individualization_background);
        LinearLayout titleView = (LinearLayout) findViewById(Res.id.pandora_individualization_title);
        initBackground(mRootView);
        initTitleHeight(titleView);
        mNoticeSButton = (SwitchButton) findViewById(Res.id.individualization_notice_switch_button);
        mNoticeSButton.setOnCheckedChangeListener(this);
        mNoticeSButton.setChecked(isNeedNotice());
        mNoticeMobileNetworkSButton = (SwitchButton) findViewById(Res.id.individualization_3G_4G_switch_button);
        mNoticeMobileNetworkSButton.setOnCheckedChangeListener(this);
        mNoticeMobileNetworkSButton.setChecked(isMobileNetwork());
        mLockScreenVoiceSButton = (SwitchButton) findViewById(Res.id.individualization_open_lockscreen_voice_switch_button);
        mLockScreenVoiceSButton.setOnCheckedChangeListener(this);
        mLockScreenVoiceSButton.setChecked(isLockScreenVoice());
        mNotificationSButton = (SwitchButton) findViewById(Res.id.individualization_open_message_notification_switch_button);
        mNotificationSButton.setOnCheckedChangeListener(this);
        mNotificationSButton.setChecked(isMessageNotification());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView == mNoticeSButton) {
            if (isChecked) {
                openNoticeBar();
                UmengCustomEventManager.statisticalShowNotifyTimes();
            } else {
                closeNoticeBar();
                UmengCustomEventManager.statisticalCloseNotifyTimes();
            }
        } else if (buttonView == mNoticeMobileNetworkSButton) {
            if (isChecked) {
                openMobileNetwork();
                UmengCustomEventManager.statisticalAllowAutoDownload();
            } else {
                closeMobileNetwork();
                UmengCustomEventManager.statisticalDisallowAutoDownload();
            }
        } else if (buttonView == mLockScreenVoiceSButton) {
            if (isChecked) {
                openLockScreenVoice();
                UmengCustomEventManager.statisticalEnableLockScreenSound();
            } else {
                closeLocksScreenVoice();
                UmengCustomEventManager.statisticalDisableLockScreenSound();
            }
        } else if (buttonView == mNotificationSButton) {
            if (isChecked) {
                openMessageNotification();
            } else {
                closeMessageNotification();
            }
        }
    }

    // private void showInputDialog() {
    // final Dialog dialog = new Dialog(this, Res.style.pandora_dialog_style);
    // dialog.getWindow().setContentView(Res.layout.pandora_dialog);
    // dialog.show();
    // dialog.setCancelable(false);
    //
    // TypefaceTextView mTitle = (TypefaceTextView)
    // dialog.findViewById(Res.id.pandora_dialog_title);
    // mTitle.setText(getResources().getString(Res.string.individualization_welcome_text));
    // dialog.findViewById(Res.id.pandora_dialog_individualization).setVisibility(View.VISIBLE);
    // final BaseEditText mEditText = (BaseEditText) dialog
    // .findViewById(Res.id.pandora_dialog_individualization_edit_text);
    //
    // String welcomeString =
    // PandoraConfig.newInstance(IndividualizationActivity.this)
    // .getWelcomeString();
    // if (!TextUtils.isEmpty(welcomeString)) {
    // mEditText.setText(welcomeString);
    // mEditText.setSelection(welcomeString.length());
    // }
    //
    // TypefaceTextView mCancle = (TypefaceTextView) dialog
    // .findViewById(Res.id.pandora_dialog_individualization_button_cancle);
    // mCancle.setOnClickListener(new OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // dialog.dismiss();
    // }
    // });
    // TypefaceTextView mSure = (TypefaceTextView) dialog
    // .findViewById(Res.id.pandora_dialog_individualization_button_sure);
    // mSure.setOnClickListener(new OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // String welcomeString = mEditText.getText().toString();
    // UmengCustomEventManager.statisticalSetWelcomeString(welcomeString, true);
    // saveWelcomeString(welcomeString);
    // dialog.dismiss();
    // }
    // });
    //
    // }

    private void closeNoticeBar() {
        PandoraConfig.newInstance(this).saveNeedNotice(false);
    }

    private void openNoticeBar() {
        PandoraConfig.newInstance(this).saveNeedNotice(true);
    }

    private boolean isNeedNotice() {
        return PandoraConfig.newInstance(this).isNeedNotice(this);
    }

    private void closeMobileNetwork() {
        PandoraConfig.newInstance(this).saveMobileNetwork(false);
    }

    private void openMobileNetwork() {
        PandoraConfig.newInstance(this).saveMobileNetwork(true);
    }

    private boolean isMobileNetwork() {
        return PandoraConfig.newInstance(this).isMobileNetwork();
    }

    private void closeLocksScreenVoice() {
        PandoraConfig.newInstance(this).saveLockScreenVoice(false);
        LockSoundManager.release();
    }

    private void openLockScreenVoice() {
        PandoraConfig.newInstance(this).saveLockScreenVoice(true);
        LockSoundManager.initSoundPool();
    }

    private boolean isLockScreenVoice() {
        return PandoraConfig.newInstance(this).isLockScreenVoice();
    }

    private void closeMessageNotification() {
        PandoraConfig.newInstance(this).saveMessageNotification(false);
    }

    private void openMessageNotification() {
        PandoraConfig.newInstance(this).saveMessageNotification(true);
    }

    private boolean isMessageNotification() {
        return PandoraConfig.newInstance(this).isShowNotificationMessage();
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent();
        in.setClass(IndividualizationActivity.this, MainSettingsActivity.class);
        startActivity(in);
        finish();
        overridePendingTransition(Res.anim.umeng_fb_slide_in_from_left,
                Res.anim.umeng_fb_slide_out_from_right);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("IndividualizationActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("IndividualizationActivity"); // 保证 onPageEnd
                                                              // 在onPause
        // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}