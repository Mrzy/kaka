
package cn.zmdx.kaka.locker.settings;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.service.PandoraService;
import cn.zmdx.kaka.locker.settings.MainSettingFragment.IMainSettingListener;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.splash.SplashActivity;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils;

import com.umeng.analytics.MobclickAgent;

public class MainSettingActivity extends ActionBarActivity implements IMainSettingListener {

    private Intent mServiceIntent = null;

    boolean isFirstIn = false;

    private static final int GO_INIT_SETTING = 1001;

    private int[] mBackgroundDrawable = {
            R.drawable.action_bar_bg_blue, R.drawable.action_bar_bg_purple,
            R.drawable.action_bar_bg_orange, R.drawable.action_bar_bg_red
    };

    private int[] mBackgroundColor = {
            Color.parseColor("#3db7ff"), Color.parseColor("#ab47bc"), Color.parseColor("#ea861c"),
            Color.parseColor("#e84e40")
    };

    public int[] getBackgroundColor() {
        return mBackgroundColor;
    }

    private int mLastPosition;

    private MainSettingFragment mMainSettingFragment;

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<MainSettingActivity> mActivity;

        public MyHandler(MainSettingActivity activity) {
            mActivity = new WeakReference<MainSettingActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainSettingActivity activity = mActivity.get();
            switch (msg.what) {
                case GO_INIT_SETTING:
                    activity.gotoSplash();
//                    activity.goInitSetting();
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // if (Build.VERSION.SDK_INT >= 19) {
        // Window window = getWindow();
        // window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
        // WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
        // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        // }
        mServiceIntent = new Intent(getApplicationContext(), PandoraService.class);
        startService(mServiceIntent);
        init();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        // AppUninstall.openUrlWhenUninstall(this, "http://www.hdlocker.com");
        MobclickAgent.openActivityDurationTrack(false);
        // UmengUpdateAgent.silentUpdate(this);
        setContentView(R.layout.main_setting_activity);

        setBackground(getResources().getDrawable(R.drawable.action_bar_bg_blue));
        getSupportActionBar().setTitle(getResources().getString(R.string.pandora_setting_general));
        // getWindow().getAttributes().flags |=
        // LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mMainSettingFragment = new MainSettingFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content, mMainSettingFragment)
                .commit();
        UmengCustomEventManager.statisticalNeedInterceptApp();
        WallpaperUtils.autoChangeWallpaper();
    }

    private void init() {
//        isFirstIn = !PandoraConfig.newInstance(this).isHasGuided();
//        if (isFirstIn) {
            mHandler.sendEmptyMessage(GO_INIT_SETTING);
//        }
    }

    private void gotoSplash() {
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
    }

//    private void goInitSetting() {
//        boolean isMeizu = PandoraUtils.isMeizu(this);
//        if (isMeizu && !NotificationInterceptor.getInstance(this).isDeviceAvailable()) {
//            return;
//        }
//        Intent intent = new Intent(this, InitSettingActivity.class);
//        startActivity(intent);
//    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onItemClick(String title, int position) {
        getSupportActionBar().setTitle(title);
        setActionBarBackground(position);
        mLastPosition = position;
    }

    private void setActionBarBackground(int position) {
        if (mLastPosition != position) {
            Drawable[] layers = new Drawable[] {
                    new ColorDrawable(mBackgroundColor[mLastPosition]),
                    new ColorDrawable(mBackgroundColor[position])
            };
            TransitionDrawable td = new TransitionDrawable(layers);
            setBackground(td);
            td.startTransition(300);
        } else {
            setBackground(getResources().getDrawable(mBackgroundDrawable[position]));
        }

    }

    private void setBackground(Drawable drawable) {
        getSupportActionBar().setBackgroundDrawable(drawable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PandoraUtils.REQUEST_CODE_GALLERY:
                Intent intent = new Intent();
                intent.setClass(MainSettingActivity.this, CropImageActivity.class);
                intent.setData(data.getData());
                startActivity(intent);
                overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                        R.anim.umeng_fb_slide_out_from_left);
                break;

            case PasswordPromptActivity.REQUEST_LOCKER_PASSWORD_TYPE_CODE:
                if (null != mMainSettingFragment) {
                    mMainSettingFragment.resetPasswordState();
                }
                break;

            case GeneralFragment.REQUEST_CITY_CHOSEN_CODE:
                if (null != mMainSettingFragment) {
                    String cityName = data.getStringExtra("cityNameChosen");
                    mMainSettingFragment.sendChosenCityName(cityName);
                }
                break;
            default: {
                break;
            }
        }
    }
}
