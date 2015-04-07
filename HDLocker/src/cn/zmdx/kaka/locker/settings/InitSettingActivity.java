
package cn.zmdx.kaka.locker.settings;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.umeng.analytics.MobclickAgent;

public class InitSettingActivity extends ActionBarActivity implements OnClickListener {

    private TypefaceTextView mCompleteBtn;

    private TypefaceTextView mCompleteProgress;

    private static boolean isMIUI = false;

    private static String mMIUIVersion;

    private static boolean isMeizu = false;

    private static final int MSG_CLOSE_SYSTEM_LOCKER = 0;

    private static final int MSG_ALLOW_FOLAT_WINDOW = 1;

    private static final int MSG_TRUST = 2;

    private static final int MSG_READ_NOTIFICATION = 3;

    private static final int MSG_SETTING_DELAY = 500;

    private boolean isCanCancled = false;

    private RelativeLayout mFolatfingWindowLayout;

    private RelativeLayout mTrustLayout;

    private RelativeLayout mCloseSystemLockLayout;

    private RelativeLayout mReadNotificationLayout;

    private ImageView mFolatfingWindowArrow;

    private ImageView mTrustArrow;

    private ImageView mCloseSystemLockArrow;

    private ImageView mReadNotificationArrow;

    private float mSettingCount;

    private float mSettingComCount;

    private boolean isFolatfingWindow;

    private boolean isTrust;

    private boolean isCloseSystemLock;

    private boolean isReadNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.action_bar_bg_blue));
        mMIUIVersion = PandoraUtils.getSystemProperty();
        isMIUI = PandoraUtils.isMIUI(this);
        isMeizu = PandoraUtils.isMeizu(this);
        setContentView(R.layout.init_setting_fragment);
        initView();
    }

    private void initView() {
        mCompleteBtn = (TypefaceTextView) findViewById(R.id.init_setting_complete);
        mCompleteBtn.setOnClickListener(this);
        mCompleteProgress = (TypefaceTextView) findViewById(R.id.init_setting_progress);

        mFolatfingWindowLayout = (RelativeLayout) findViewById(R.id.init_setting_MIUI_allow_floating_window_guide);
        mFolatfingWindowLayout.setOnClickListener(this);
        mFolatfingWindowArrow = (ImageView) findViewById(R.id.init_setting_MIUI_allow_floating_window_arrow);
        mTrustLayout = (RelativeLayout) findViewById(R.id.init_setting_MIUI_trust_guide);
        mTrustLayout.setOnClickListener(this);
        mTrustArrow = (ImageView) findViewById(R.id.init_setting_MIUI_trust_arrow);
        mCloseSystemLockLayout = (RelativeLayout) findViewById(R.id.init_setting_close_systemlocker_guide);
        mCloseSystemLockLayout.setOnClickListener(this);
        mCloseSystemLockArrow = (ImageView) findViewById(R.id.init_setting_close_systemlocker_arrow);
        mReadNotificationLayout = (RelativeLayout) findViewById(R.id.init_setting_read_notification_bar_guide);
        mReadNotificationLayout.setOnClickListener(this);
        mReadNotificationArrow = (ImageView) findViewById(R.id.init_setting_read_notification_bar_arrow);

        mCloseSystemLockLayout.setVisibility(View.VISIBLE);
        mSettingCount = mSettingCount + 1;
        if (isMIUI) {
            mFolatfingWindowLayout.setVisibility(View.VISIBLE);
            mTrustLayout.setVisibility(View.VISIBLE);
            mSettingCount = mSettingCount + 2;
        }
        if (isMeizu) {
            mCloseSystemLockLayout.setVisibility(View.GONE);
            mSettingCount = mSettingCount - 1;
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mReadNotificationLayout.setVisibility(View.VISIBLE);
            mSettingCount = mSettingCount + 1;
        }
    }

    private void showPromptActicity(boolean isMIUI, String mMIUIVersion, int type) {
        Intent in = new Intent();
        in.setClass(this, InitPromptActivity.class);
        in.putExtra("isMIUI", isMIUI);
        in.putExtra("mMIUIVersion", mMIUIVersion);
        in.putExtra("type", type);
        startActivity(in);
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    @Override
    public void onClick(View view) {
        if (view == mCompleteBtn) {
            if (isCanCancled) {
                LockScreenManager.getInstance().lock();
                PandoraConfig.newInstance(this).saveHasGuided();
                finish();
                overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                        R.anim.umeng_fb_slide_out_from_right);
            }
        } else if (view == mFolatfingWindowLayout) {
            PandoraUtils.setAllowFolatWindow(InitSettingActivity.this, mMIUIVersion);
            if (!isFolatfingWindow) {
                isFolatfingWindow = !isFolatfingWindow;
                mSettingComCount++;
            }
            setViewPressed(mFolatfingWindowLayout, mFolatfingWindowArrow);
            if (mHandler.hasMessages(MSG_ALLOW_FOLAT_WINDOW)) {
                mHandler.removeMessages(MSG_ALLOW_FOLAT_WINDOW);
            }
            Message allowFloatWindow = Message.obtain();
            allowFloatWindow.what = MSG_ALLOW_FOLAT_WINDOW;
            mHandler.sendMessageDelayed(allowFloatWindow, MSG_SETTING_DELAY);
        } else if (view == mTrustLayout) {
            PandoraUtils.setTrust(InitSettingActivity.this, mMIUIVersion);
            if (!isTrust) {
                isTrust = !isTrust;
                mSettingComCount++;
            }
            setViewPressed(mTrustLayout, mTrustArrow);
            if (mHandler.hasMessages(MSG_TRUST)) {
                mHandler.removeMessages(MSG_TRUST);
            }
            Message setTrust = Message.obtain();
            setTrust.what = MSG_TRUST;
            mHandler.sendMessageDelayed(setTrust, MSG_SETTING_DELAY);
        } else if (view == mCloseSystemLockLayout) {
            PandoraUtils.closeSystemLocker(InitSettingActivity.this, isMIUI);
            if (!isCloseSystemLock) {
                isCloseSystemLock = !isCloseSystemLock;
                mSettingComCount++;
            }
            setViewPressed(mCloseSystemLockLayout, mCloseSystemLockArrow);
            if (mHandler.hasMessages(MSG_CLOSE_SYSTEM_LOCKER)) {
                mHandler.removeMessages(MSG_CLOSE_SYSTEM_LOCKER);
            }
            Message closeSystemLocker = Message.obtain();
            closeSystemLocker.what = MSG_CLOSE_SYSTEM_LOCKER;
            mHandler.sendMessageDelayed(closeSystemLocker, MSG_SETTING_DELAY);
        } else if (view == mReadNotificationLayout) {
            PandoraUtils.setAllowReadNotification(InitSettingActivity.this, isMIUI, mMIUIVersion,
                    isMeizu);
            if (!isReadNotification) {
                isReadNotification = !isReadNotification;
                mSettingComCount++;
            }
            setViewPressed(mReadNotificationLayout, mReadNotificationArrow);
            if (mHandler.hasMessages(MSG_READ_NOTIFICATION)) {
                mHandler.removeMessages(MSG_READ_NOTIFICATION);
            }
            Message readNotification = Message.obtain();
            readNotification.what = MSG_READ_NOTIFICATION;
            mHandler.sendMessageDelayed(readNotification, MSG_SETTING_DELAY);
        }

    }

    private void setViewPressed(final View bgView, final ImageView view) {
        isCanCancled = true;
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                bgView.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.init_setting_item_bg_press));
                view.setImageDrawable(getResources().getDrawable(R.drawable.init_setting_select));
                mCompleteBtn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.init_setting_com_selector));
                mCompleteProgress.setTextColor(getResources().getColor(
                        R.color.init_setting_progress));
                float result = mSettingComCount / mSettingCount;
                DecimalFormat fmt = new DecimalFormat("##%");
                mCompleteProgress.setText("进度" + fmt.format(result));
            }
        }, 500);
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

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("InitSettingActivity");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("InitSettingActivity");
        MobclickAgent.onPause(this);
    }
}
