
package cn.zmdx.kaka.locker.meiwen.settings;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.meiwen.widget.TypefaceTextView;

import com.umeng.analytics.MobclickAgent;

public class InitSettingActivity extends BaseActivity implements OnClickListener {

    private Button mCloseSystemLockBtn;

    private Button mFolatfingWindowBtn;

    private Button mTrustBtn;

    private Button mReadNotificationBtn;

    private View mRootView;

    private TypefaceTextView mCompleteBtn;

    private static boolean isMIUI = false;

    private static boolean isMeizu = false;

    private static String mMIUIVersion;

    private static final int MSG_CLOSE_SYSTEM_LOCKER = 0;

    private static final int MSG_ALLOW_FOLAT_WINDOW = 1;

    private static final int MSG_TRUST = 2;

    private static final int MSG_READ_NOTIFICATION = 3;

    private static final int MSG_SETTING_DELAY = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mMIUIVersion = PandoraUtils.getSystemProperty();
        isMIUI = PandoraUtils.isMIUI(this);
        isMeizu = PandoraUtils.isMeizu(this);
        setContentView(Res.layout.init_setting_fragment);
        initView();
    }

    private void initView() {
        if (isMIUI) {
            findViewById(Res.id.init_setting_MIUI_allow_floating_window_guide).setVisibility(
                    View.VISIBLE);
            findViewById(Res.id.init_setting_MIUI_trust_guide).setVisibility(View.VISIBLE);
        }
        if (isMeizu) {
            findViewById(Res.id.init_setting_MIUI_close_systemlocker).setVisibility(View.GONE);
        }
        mRootView = findViewById(Res.id.init_setting_background);
        LinearLayout titleView = (LinearLayout) findViewById(Res.id.init_setting_title);
        initBackground(mRootView);
        initTitleHeight(titleView);
        mCloseSystemLockBtn = (Button) findViewById(Res.id.init_setting_close_systemlocker_to_set);
        mCloseSystemLockBtn.setOnClickListener(this);
        mFolatfingWindowBtn = (Button) findViewById(Res.id.init_setting_MIUI_allow_floating_window_to_set);
        mFolatfingWindowBtn.setOnClickListener(this);
        mTrustBtn = (Button) findViewById(Res.id.init_setting_MIUI_trust_to_set);
        mTrustBtn.setOnClickListener(this);
        mCompleteBtn = (TypefaceTextView) findViewById(Res.id.init_setting_miui_complete);
        mCompleteBtn.setOnClickListener(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mReadNotificationBtn = (Button) findViewById(Res.id.init_setting_read_notification_bar_to_set);
            mReadNotificationBtn.setOnClickListener(this);
            mReadNotificationBtn.setVisibility(View.VISIBLE);
        }
    }

    private void showPromptActicity(boolean isMIUI, String mMIUIVersion, int type) {
        Intent in = new Intent();
        in.setClass(this, InitPromptActivity.class);
        in.putExtra("isMIUI", isMIUI);
        in.putExtra("mMIUIVersion", mMIUIVersion);
        in.putExtra("type", type);
        startActivity(in);
        overridePendingTransition(Res.anim.umeng_fb_slide_in_from_right,
                Res.anim.umeng_fb_slide_out_from_left);
    }

    @Override
    public void onClick(View view) {
        if (view == mCloseSystemLockBtn) {
            PandoraUtils.closeSystemLocker(InitSettingActivity.this, isMIUI);
            mCloseSystemLockBtn.setBackgroundResource(Res.drawable.base_button_pressed);

            if (mHandler.hasMessages(MSG_CLOSE_SYSTEM_LOCKER)) {
                mHandler.removeMessages(MSG_CLOSE_SYSTEM_LOCKER);
            }
            Message closeSystemLocker = Message.obtain();
            closeSystemLocker.what = MSG_CLOSE_SYSTEM_LOCKER;
            mHandler.sendMessageDelayed(closeSystemLocker, MSG_SETTING_DELAY);
        } else if (view == mFolatfingWindowBtn) {
            PandoraUtils.setAllowFolatWindow(InitSettingActivity.this, mMIUIVersion);
            mFolatfingWindowBtn.setBackgroundResource(Res.drawable.base_button_pressed);

            if (mHandler.hasMessages(MSG_ALLOW_FOLAT_WINDOW)) {
                mHandler.removeMessages(MSG_ALLOW_FOLAT_WINDOW);
            }
            Message allowFloatWindow = Message.obtain();
            allowFloatWindow.what = MSG_ALLOW_FOLAT_WINDOW;
            mHandler.sendMessageDelayed(allowFloatWindow, MSG_SETTING_DELAY);
        } else if (view == mTrustBtn) {
            PandoraUtils.setTrust(InitSettingActivity.this, mMIUIVersion);
            mTrustBtn.setBackgroundResource(Res.drawable.base_button_pressed);
            if (mHandler.hasMessages(MSG_TRUST)) {
                mHandler.removeMessages(MSG_TRUST);
            }
            Message setTrust = Message.obtain();
            setTrust.what = MSG_TRUST;
            mHandler.sendMessageDelayed(setTrust, MSG_SETTING_DELAY);
        } else if (view == mCompleteBtn) {
            onBackPressed();
        } else if (view == mReadNotificationBtn) {
            if (isMIUI) {
                PandoraUtils.setMIUIAllowReadNotification(InitSettingActivity.this, mMIUIVersion);
            } else if (isMeizu) {
                PandoraUtils.setMeizuAllowReadNotification(InitSettingActivity.this);
            } else {
                PandoraUtils.setRegularAllowReadNotification(InitSettingActivity.this);
            }

            mReadNotificationBtn.setBackgroundResource(Res.drawable.base_button_pressed);
            if (mHandler.hasMessages(MSG_READ_NOTIFICATION)) {
                mHandler.removeMessages(MSG_READ_NOTIFICATION);
            }
            Message readNotification = Message.obtain();
            readNotification.what = MSG_READ_NOTIFICATION;
            mHandler.sendMessageDelayed(readNotification, MSG_SETTING_DELAY);
        }
    }

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<Activity> mActicity;

        public MyHandler(Activity activity) {
            mActicity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActicity.get();
            switch (msg.what) {
                case MSG_CLOSE_SYSTEM_LOCKER:
                    ((InitSettingActivity) activity).showPromptActicity(isMIUI, mMIUIVersion,
                            InitPromptActivity.PROMPT_CLOSE_SYSTEM_LOCKER);
                    break;
                case MSG_ALLOW_FOLAT_WINDOW:
                    ((InitSettingActivity) activity).showPromptActicity(isMIUI, mMIUIVersion,
                            InitPromptActivity.PROMPT_ALLOW_FLOAT_WINDOW);
                    break;
                case MSG_TRUST:
                    ((InitSettingActivity) activity).showPromptActicity(isMIUI, mMIUIVersion,
                            InitPromptActivity.PROMPT_TRRST);
                    break;
                case MSG_READ_NOTIFICATION:
                    ((InitSettingActivity) activity).showPromptActicity(isMIUI, mMIUIVersion,
                            InitPromptActivity.PROMPT_READ_NOTIFICATION);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(Res.anim.umeng_fb_slide_in_from_left,
                Res.anim.umeng_fb_slide_out_from_right);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("InitSettingActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("InitSettingActivity"); // 保证 onPageEnd 在onPause
        // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}