
package cn.zmdx.kaka.locker.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Window;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.guide.CloseSystemLockGuideFragment;
import cn.zmdx.kaka.locker.guide.CloseSystemLockGuideFragment.ICloseSystemLockListener;
import cn.zmdx.kaka.locker.guide.InitSettingFragment;
import cn.zmdx.kaka.locker.guide.InitSettingFragment.ISettingFragmentListener;
import cn.zmdx.kaka.locker.guide.ReadNotificationGuideFragment;
import cn.zmdx.kaka.locker.guide.ReadNotificationGuideFragment.IReadNotificationListener;
import cn.zmdx.kaka.locker.notification.NotificationInterceptor;
import cn.zmdx.kaka.locker.service.PandoraService;
import cn.zmdx.kaka.locker.settings.MainSettingFragment.IMainSettingListener;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.splash.SplashFragment;
import cn.zmdx.kaka.locker.splash.SplashFragment.ISplashFragmentListener;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils;

import com.umeng.analytics.MobclickAgent;

public class MainSettingActivity extends ActionBarActivity implements IMainSettingListener,
        ISettingFragmentListener, ISplashFragmentListener, IReadNotificationListener,
        ICloseSystemLockListener {

    private static boolean isSplash = true;

    private Intent mServiceIntent = null;

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

    private CloseSystemLockGuideFragment mCloseSystemLockGuideFragment;

    private ReadNotificationGuideFragment mReadNotificationGuideFragment;

    private InitSettingFragment mInitSettingFragment;

    private SplashFragment mSplashFragment;

    private boolean isMIUI = false;

    private static final String TAG_CLOSE_SYSTEM_LOCK_GUIDE = "closeSystenLockGuideFragment";

    private static final String TAG_READ_NOTIFICATION_GUIDE = "readNotificationGuideFragment";

    private static final String TAG_MAIN_SETTING_FRAGMENT = "mainSettingFragment";

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        // AppUninstall.openUrlWhenUninstall(this, "http://www.hdlocker.com");
        MobclickAgent.openActivityDurationTrack(false);
        // UmengUpdateAgent.silentUpdate(this);
        setContentView(R.layout.main_setting_activity);

        isMIUI = PandoraUtils.isMIUI(this);

        setBackground(getResources().getDrawable(R.drawable.action_bar_bg_blue));
        getSupportActionBar().setTitle(getResources().getString(R.string.pandora_setting_general));
        getSupportActionBar().hide();
        // getWindow().getAttributes().flags |=
        // LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        mMainSettingFragment = new MainSettingFragment();
        mCloseSystemLockGuideFragment = new CloseSystemLockGuideFragment();
        mReadNotificationGuideFragment = new ReadNotificationGuideFragment();
        mInitSettingFragment = new InitSettingFragment();
        mSplashFragment = new SplashFragment();

        initFragment();
        UmengCustomEventManager.statisticalNeedInterceptApp();
        WallpaperUtils.autoChangeWallpaper();
    }

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

    private void initFragment() {
        if (isMIUI && !PandoraUtils.isMiuiFloatWindowOpAllowed(this)) {
            addFragment(mInitSettingFragment, "", false, true);
        } else {
            if (isSplash) {
                addFragment(mSplashFragment, "", false, true);
                isSplash = false;
                return;
            }
            if (NotificationInterceptor.isDeviceAvailable()
                    && !PandoraConfig.newInstance(this).isReadNotifitionGuided()
                    && !NotificationInterceptor.isGrantedNotifyPermission(this)) {
                addFragment(mReadNotificationGuideFragment, TAG_READ_NOTIFICATION_GUIDE, true, true);
                return;
            }
            addFragment(mMainSettingFragment, TAG_MAIN_SETTING_FRAGMENT, false, false);
        }

    }

    @Override
    public void onInitSettingSkip() {
        if (NotificationInterceptor.isDeviceAvailable()
                && !PandoraConfig.newInstance(this).isReadNotifitionGuided()
                && !NotificationInterceptor.isGrantedNotifyPermission(this)) {
            addFragment(mReadNotificationGuideFragment, TAG_READ_NOTIFICATION_GUIDE, true, true);
        } else {
            removeFragment(mInitSettingFragment, false);
            addFragment(mMainSettingFragment, TAG_MAIN_SETTING_FRAGMENT, false, false);
        }
    }

    @Override
    public void onReadNotificationBack() {
        removeFragment(mReadNotificationGuideFragment, true);
        getSupportFragmentManager().popBackStack(TAG_READ_NOTIFICATION_GUIDE,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (isMIUI) {
            if (PandoraUtils.isMiuiFloatWindowOpAllowed(this)) {
                removeFragment(mInitSettingFragment, false);
                addFragment(mMainSettingFragment, TAG_MAIN_SETTING_FRAGMENT, false, false);
            }
        } else {
            if (!PandoraConfig.newInstance(this).isHasGuided() && !PandoraUtils.isMeizu(this)
                    && !PandoraConfig.newInstance(this).isCloseSystemLockGuided()) {
                addFragment(mCloseSystemLockGuideFragment, TAG_CLOSE_SYSTEM_LOCK_GUIDE, false, true);
            } else {
                addFragment(mMainSettingFragment, TAG_MAIN_SETTING_FRAGMENT, false, false);
            }
        }
    }

    @Override
    public void onCloseSystemLockBack() {
        removeFragment(mCloseSystemLockGuideFragment, false);
        getSupportFragmentManager().popBackStack(TAG_CLOSE_SYSTEM_LOCK_GUIDE,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        addFragment(mMainSettingFragment, TAG_MAIN_SETTING_FRAGMENT, false, false);
    }

    @Override
    public void onSplashEnd() {
        if (isDestroy) {
            return;
        }
        removeFragment(mSplashFragment, false);

        if (NotificationInterceptor.isDeviceAvailable()
                && !PandoraConfig.newInstance(this).isReadNotifitionGuided()
                && !NotificationInterceptor.isGrantedNotifyPermission(this)) {
            addFragment(mReadNotificationGuideFragment, TAG_READ_NOTIFICATION_GUIDE, true, true);
            return;
        }

        if (!PandoraConfig.newInstance(this).isHasGuided() && !isMIUI
                && !PandoraUtils.isMeizu(this)
                && !PandoraConfig.newInstance(this).isCloseSystemLockGuided()) {
            addFragment(mCloseSystemLockGuideFragment, TAG_CLOSE_SYSTEM_LOCK_GUIDE, false, true);
            return;
        }
        addFragment(mMainSettingFragment, TAG_MAIN_SETTING_FRAGMENT, false, false);
    }

    private void addFragment(Fragment fragment, String tag, boolean isAddToBackStack,
            boolean isNeedAnimator) {
        if (!fragment.isAdded()) {
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            if (isNeedAnimator) {
                beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            }
            beginTransaction.add(R.id.content, fragment, tag);
            if (isAddToBackStack) {
                beginTransaction.addToBackStack(tag);
            }
            beginTransaction.commitAllowingStateLoss();

            if (fragment instanceof MainSettingFragment) {
                getSupportActionBar().show();
            } else {
                getSupportActionBar().hide();
            }
        }
    }

    private void removeFragment(Fragment fragment, boolean isNeedAnimator) {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        if (isNeedAnimator) {
            beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        }
        beginTransaction.remove(fragment).commitAllowingStateLoss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Fragment fragment_byTag = getSupportFragmentManager().findFragmentByTag(
                    TAG_READ_NOTIFICATION_GUIDE);
            Fragment close = getSupportFragmentManager().findFragmentByTag(
                    TAG_CLOSE_SYSTEM_LOCK_GUIDE);
            if (fragment_byTag != null) {
                if (fragment_byTag.isVisible()) {
                    onReadNotificationBack();
                    return false;
                }
            } else {
                if (close != null) {
                    if (close.isVisible()) {
                        onCloseSystemLockBack();
                    }
                    return false;
                }
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isDestroy = false;

    @Override
    protected void onDestroy() {
        isDestroy = true;
        super.onDestroy();
    }
}
