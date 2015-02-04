
package cn.zmdx.kaka.fast.locker.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.sound.LockSoundManager;
import cn.zmdx.kaka.fast.locker.widget.SwitchButton;

import com.umeng.analytics.MobclickAgent;

public class IndividualizationActivity extends BaseActivity implements OnClickListener,
        OnCheckedChangeListener {
    public static String LOCK_DEFAULT_SDCARD_LOCATION = Environment.getExternalStorageDirectory()
            .getPath() + "/.Pandora/lockDefault/";

    private View mRootView;

    private SwitchButton mNoticeSButton;

    private SwitchButton mLockScreenVoiceSButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_individualization);
        initView();
        // initLockDefaultBitmap();
    }

    private void initView() {
        mRootView = findViewById(R.id.individualization_background);
        mNoticeSButton = (SwitchButton) findViewById(R.id.individualization_notice_switch_button);
        mNoticeSButton.setOnCheckedChangeListener(this);
        mNoticeSButton.setChecked(isNeedNotice());
        mLockScreenVoiceSButton = (SwitchButton) findViewById(R.id.individualization_open_lockscreen_voice_switch_button);
        mLockScreenVoiceSButton.setOnCheckedChangeListener(this);
        mLockScreenVoiceSButton.setChecked(isLockScreenVoice());

        // LayoutParams params = mLockerDefaultImageThumb.getLayoutParams();
        // int height = (int)
        // getResources().getDimension(R.dimen.individualization_image_height);
        // int width = (int)
        // getResources().getDimension(R.dimen.individualization_image_height);
        // params.width = width;
        // params.height = height;
        // mLockerDefaultImageThumb.setLayoutParams(params);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.individualization_notice_switch_button:
                if (isChecked) {
                    openNoticeBar();
                    UmengCustomEventManager.statisticalShowNotifyTimes();
                } else {
                    closeNoticeBar();
                    UmengCustomEventManager.statisticalCloseNotifyTimes();
                }
                break;
            case R.id.individualization_open_lockscreen_voice_switch_button:
                if (isChecked) {
                    openLockScreenVoice();
                    UmengCustomEventManager.statisticalEnableLockScreenSound();
                } else {
                    closeLocksScreenVoice();
                    UmengCustomEventManager.statisticalDisableLockScreenSound();
                }
                break;
            default:
                break;
        }

    }

    // private void showInputDialog() {
    // final Dialog dialog = new Dialog(this, R.style.pandora_dialog_style);
    // dialog.getWindow().setContentView(R.layout.pandora_dialog);
    // dialog.show();
    // dialog.setCancelable(false);
    //
    // TypefaceTextView mTitle = (TypefaceTextView)
    // dialog.findViewById(R.id.pandora_dialog_title);
    // mTitle.setText(getResources().getString(R.string.individualization_welcome_text));
    // dialog.findViewById(R.id.pandora_dialog_individualization).setVisibility(View.VISIBLE);
    // final BaseEditText mEditText = (BaseEditText) dialog
    // .findViewById(R.id.pandora_dialog_individualization_edit_text);
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
    // .findViewById(R.id.pandora_dialog_individualization_button_cancle);
    // mCancle.setOnClickListener(new OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // dialog.dismiss();
    // }
    // });
    // TypefaceTextView mSure = (TypefaceTextView) dialog
    // .findViewById(R.id.pandora_dialog_individualization_button_sure);
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

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent();
        in.setClass(IndividualizationActivity.this, MainSettingsActivity.class);
        startActivity(in);
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
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
