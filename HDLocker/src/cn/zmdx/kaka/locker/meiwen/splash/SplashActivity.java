
package cn.zmdx.kaka.locker.meiwen.splash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.meiwen.HDApplication;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.settings.MainSettingsActivity;
import cn.zmdx.kaka.locker.meiwen.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.meiwen.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.meiwen.wallpaper.WallpaperUtils;
import cn.zmdx.kaka.locker.meiwen.widget.TypefaceTextView;

public class SplashActivity extends Activity {

    private TypefaceTextView mVersion;

    private ImageView mIcon;

    private TypefaceTextView mAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(Res.layout.pandora_splash);
        // initDesktopDrawable();
        mIcon = (ImageView) findViewById(Res.id.pandora_splash_icon);
        mAppName = (TypefaceTextView) findViewById(Res.id.pandora_splash_app_name);
        mVersion = (TypefaceTextView) findViewById(Res.id.pandora_splash_version);
        // invisiableViews(mIcon, mAppName, mVersion);
        initVersion();
        // processAnimations();
        goToMainSettingsActivity();
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
                overridePendingTransition(Res.anim.umeng_fb_slide_in_from_right,
                        Res.anim.umeng_fb_slide_out_from_left);
                finish();
            }
        }, 1000);
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
        mVersion.setText(version+"版本");
    }

}
