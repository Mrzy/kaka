
package cn.zmdx.kaka.fast.locker.splash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import cn.zmdx.kaka.fast.locker.MainSettingsActivity;
import cn.zmdx.kaka.fast.locker.HDApplication;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.settings.MainSettingsActivityOld;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.fast.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.fast.locker.wallpaper.WallpaperUtils;
import cn.zmdx.kaka.fast.locker.widget.TypefaceTextView;

public class SplashActivity extends Activity {

    private TypefaceTextView mVersion;

    private ImageView mIcon;

    private TypefaceTextView mAppName;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 19) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.pandora_splash);
        initDesktopDrawable();
        mIcon = (ImageView) findViewById(R.id.pandora_splash_icon);
        mAppName = (TypefaceTextView) findViewById(R.id.pandora_splash_app_name);
        mVersion = (TypefaceTextView) findViewById(R.id.pandora_splash_version);
        invisiableViews(mIcon, mAppName, mVersion);
        initVersion();
        processAnimations();
    }

    private void processAnimations() {
        ObjectAnimator iconAlpha = ObjectAnimator.ofFloat(mIcon, "alpha", 0, 1);
        ObjectAnimator iconTrans = ObjectAnimator.ofFloat(mIcon, "translationY",
                DATE_WIDGET_TRANSLATIONY_DISTANCE, 0);
        AnimatorSet iconSet = new AnimatorSet();
        iconSet.setStartDelay(100);
        iconSet.playTogether(iconAlpha, iconTrans);

        ObjectAnimator appNameAlpha = ObjectAnimator.ofFloat(mAppName, "alpha", 0, 1);
        ObjectAnimator appNameTrans = ObjectAnimator.ofFloat(mAppName, "translationY",
                DATE_WIDGET_TRANSLATIONY_DISTANCE_1, 0);
        AnimatorSet appNameSet = new AnimatorSet();
        appNameSet.setStartDelay(300);
        appNameSet.playTogether(appNameAlpha, appNameTrans);

        ObjectAnimator versionAlpha = ObjectAnimator.ofFloat(mVersion, "alpha", 0, 1);
        ObjectAnimator versionTrans = ObjectAnimator.ofFloat(mVersion, "translationY",
                DATE_WIDGET_TRANSLATIONY_DISTANCE_2, 0);
        AnimatorSet versionSet = new AnimatorSet();
        versionSet.setStartDelay(500);
        versionSet.playTogether(versionAlpha, versionTrans);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playTogether(iconSet, appNameSet, versionSet);
        finalSet.setDuration(800);
        finalSet.setInterpolator(new DecelerateInterpolator());
        finalSet.start();
        finalSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                goToMainSettingsActivity();
            }
        });
    }

    private void goToMainSettingsActivity() {
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
                Intent in = new Intent();
                in.setClass(SplashActivity.this, MainSettingsActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                        R.anim.umeng_fb_slide_out_from_left);
                finish();
            }
        }, 500);
    }

    private void invisiableViews(View... views) {
        for (View view : views) {
            if (view != null)
                view.setAlpha(0);
        }
    }

    private static final int DATE_WIDGET_TRANSLATIONY_DISTANCE = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 100);

    private static final int DATE_WIDGET_TRANSLATIONY_DISTANCE_1 = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 90);

    private static final int DATE_WIDGET_TRANSLATIONY_DISTANCE_2 = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 80);

    private void initVersion() {
        String version = BaseInfoHelper.getPkgVersionName(this);
        mVersion.setText("V " + version);
    }

    private void initDesktopDrawable() {
        HDBThreadUtils.runOnWorker(new Runnable() {

            @Override
            public void run() {
                WallpaperUtils.initDefaultWallpaper();
            }
        });
    }
}
