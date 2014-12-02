
package cn.zmdx.kaka.locker.settings;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils.ILoadBitmapCallback;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.umeng.analytics.MobclickAgent;

public class InitSettingActivity extends Activity implements OnClickListener {

    private Button mCloseSystemLockBtn;

    private Button mFolatfingWindowBtn;

    private Button mTrustBtn;

    private View mRootView;

    private TypefaceTextView mCompleteBtn;

    private static boolean isMIUI = false;

    private static String mMIUIVersion;

    private static final int MSG_CLOSE_SYSTEM_LOCKER = 0;

    private static final int MSG_ALLOW_FOLAT_WINDOW = 1;

    private static final int MSG_TRUST = 2;

    private static final int MSG_SETTING_DELAY = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mMIUIVersion = PandoraUtils.getSystemProperty();
        boolean miui = PandoraUtils.isMIUI(this);
        isMIUI = miui || !TextUtils.isEmpty(mMIUIVersion);
        setContentView(R.layout.init_setting_fragment);
        initView();
        initWallpaper();
    }

    private void initView() {
        if (isMIUI) {
            findViewById(R.id.init_setting_MIUI_allow_floating_window_guide).setVisibility(
                    View.VISIBLE);
            findViewById(R.id.init_setting_MIUI_trust_guide).setVisibility(View.VISIBLE);
        }
        mRootView = findViewById(R.id.init_setting_background);
        mCloseSystemLockBtn = (Button) findViewById(R.id.init_setting_close_systemlocker_to_set);
        mCloseSystemLockBtn.setOnClickListener(this);
        mFolatfingWindowBtn = (Button) findViewById(R.id.init_setting_MIUI_allow_floating_window_to_set);
        mFolatfingWindowBtn.setOnClickListener(this);
        mTrustBtn = (Button) findViewById(R.id.init_setting_MIUI_trust_to_set);
        mTrustBtn.setOnClickListener(this);
        mCompleteBtn = (TypefaceTextView) findViewById(R.id.init_setting_miui_complete);
        mCompleteBtn.setOnClickListener(this);

    }

    private void initWallpaper() {
        Theme theme = ThemeManager.getCurrentTheme();
        if (theme.isDefaultTheme()) {
            mRootView.setBackgroundResource(theme.getmBackgroundResId());
        } else {
            WallpaperUtils.loadBackgroundBitmap(this, theme.getFilePath(), new ILoadBitmapCallback() {

                @SuppressWarnings("deprecation")
                @Override
                public void imageLoaded(Bitmap bitmap, String filePath) {
                    mRootView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                }
            });
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
        switch (view.getId()) {
            case R.id.init_setting_close_systemlocker_to_set:
                PandoraUtils.closeSystemLocker(InitSettingActivity.this, isMIUI);
                mCloseSystemLockBtn.setBackgroundResource(R.drawable.base_button_pressed);

                if (mHandler.hasMessages(MSG_CLOSE_SYSTEM_LOCKER)) {
                    mHandler.removeMessages(MSG_CLOSE_SYSTEM_LOCKER);
                }
                Message closeSystemLocker = Message.obtain();
                closeSystemLocker.what = MSG_CLOSE_SYSTEM_LOCKER;
                mHandler.sendMessageDelayed(closeSystemLocker, MSG_SETTING_DELAY);
                break;
            case R.id.init_setting_MIUI_allow_floating_window_to_set:
                PandoraUtils.setAllowFolatWindow(InitSettingActivity.this, mMIUIVersion);
                mFolatfingWindowBtn.setBackgroundResource(R.drawable.base_button_pressed);

                if (mHandler.hasMessages(MSG_ALLOW_FOLAT_WINDOW)) {
                    mHandler.removeMessages(MSG_ALLOW_FOLAT_WINDOW);
                }
                Message allowFloatWindow = Message.obtain();
                allowFloatWindow.what = MSG_ALLOW_FOLAT_WINDOW;
                mHandler.sendMessageDelayed(allowFloatWindow, MSG_SETTING_DELAY);

                break;
            case R.id.init_setting_MIUI_trust_to_set:
                PandoraUtils.setTrust(InitSettingActivity.this, mMIUIVersion);
                mTrustBtn.setBackgroundResource(R.drawable.base_button_pressed);
                if (mHandler.hasMessages(MSG_TRUST)) {
                    mHandler.removeMessages(MSG_TRUST);
                }
                Message setTrust = Message.obtain();
                setTrust.what = MSG_TRUST;
                mHandler.sendMessageDelayed(setTrust, MSG_SETTING_DELAY);
                break;
            case R.id.init_setting_miui_complete:
                onBackPressed();
                break;

            default:
                break;
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

            }
            super.handleMessage(msg);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
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
