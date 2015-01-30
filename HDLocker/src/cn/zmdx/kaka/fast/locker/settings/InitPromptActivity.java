
package cn.zmdx.kaka.fast.locker.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.fast.locker.widget.PandoraInitSettingPromptView;
import cn.zmdx.kaka.fast.locker.widget.PandoraInitSettingPromptView.IPromptViewListener;
import cn.zmdx.kaka.fast.locker.widget.RippleView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.umeng.analytics.MobclickAgent;

public class InitPromptActivity extends Activity implements IPromptViewListener {

    private boolean isMIUI;

    private String mMIUIVersion;

    public static final int PROMPT_CLOSE_SYSTEM_LOCKER = 1;

    public static final int PROMPT_ALLOW_FLOAT_WINDOW = 2;

    public static final int PROMPT_TRRST = 3;

    public static final int PROMPT_READ_NOTIFICATION = 4;

    private int mPromptType;

    private MaterialDialog mPromptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        isMIUI = getIntent().getBooleanExtra("isMIUI", false);
        mMIUIVersion = getIntent().getStringExtra("mMIUIVersion");
        mPromptType = getIntent().getIntExtra("type", PROMPT_CLOSE_SYSTEM_LOCKER);
        if (isMIUI && PandoraUtils.MUIU_V5.equals(mMIUIVersion)) {
            setContentView(R.layout.init_prompt_view);
            getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
            getWindow().getAttributes().gravity = Gravity.TOP;
            initView();
            showView();
        } else {
            PandoraInitSettingPromptView customView = new PandoraInitSettingPromptView(
                    InitPromptActivity.this);
            customView.initType(isMIUI, mMIUIVersion, mPromptType, this);
            mPromptDialog = new MaterialDialog.Builder(this).customView(customView, true)
                    .dismissListener(mOnDismissListener).build();
            mPromptDialog.show();
        }
    }

    private LinearLayout mV5CloseSystemLockerView;

    private LinearLayout mV5AllowFloatWindowView;

    private LinearLayout mV5TrustView;

    private LinearLayout mReadNotificationView;

    private RippleView mReadNotificationButton;

    private void initView() {
        if (isMIUI) {
            findViewById(R.id.init_setting_close_systemlocker_prompt).setVisibility(View.GONE);
            findViewById(R.id.init_setting_MIUI_V5).setVisibility(View.VISIBLE);
            findViewById(R.id.init_setting_MIUI_V6).setVisibility(View.GONE);
            mV5CloseSystemLockerView = (LinearLayout) findViewById(R.id.init_setting_V5_close_systemlocker_prompt_miui);
            mV5AllowFloatWindowView = (LinearLayout) findViewById(R.id.init_setting_V5_allow_floating_window_prompt);
            mV5TrustView = (LinearLayout) findViewById(R.id.init_setting_V5_trust_prompt);
            mReadNotificationView = (LinearLayout) findViewById(R.id.init_setting_read_notification_prompt);
            mReadNotificationButton = (RippleView) findViewById(R.id.init_setting_V6_read_notification_prompt_button);
            mReadNotificationButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

    }

    private void showView() {
        if (isMIUI) {
            switch (mPromptType) {
                case PROMPT_CLOSE_SYSTEM_LOCKER:
                    mV5CloseSystemLockerView.setVisibility(View.VISIBLE);
                    break;
                case PROMPT_ALLOW_FLOAT_WINDOW:
                    mV5AllowFloatWindowView.setVisibility(View.VISIBLE);
                    break;
                case PROMPT_TRRST:
                    mV5TrustView.setVisibility(View.VISIBLE);
                    break;
                case PROMPT_READ_NOTIFICATION:
                    mReadNotificationView.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }
    }

    private OnDismissListener mOnDismissListener = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            mPromptDialog.dismiss();
            mPromptDialog.cancel();
            onBackPressed();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        onBackPressed();
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (null != mPromptDialog && mPromptDialog.isShowing()) {
            mPromptDialog.dismiss();
        }
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("InitPromptActivity");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("InitPromptActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onButtonClickListener() {
        onBackPressed();
    }
}
