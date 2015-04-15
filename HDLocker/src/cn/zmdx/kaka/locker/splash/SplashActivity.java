
package cn.zmdx.kaka.locker.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.notification.NotificationInterceptor;
import cn.zmdx.kaka.locker.settings.InitSettingActivity;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

public class SplashActivity extends Activity {

    private boolean isFirstIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_splash);
        isFirstIn = !PandoraConfig.newInstance(this).isHasGuided();
        goToMainSettingsActivity();
    }

    private void goToMainSettingsActivity() {
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
//                boolean isMeizu = PandoraUtils.isMeizu(SplashActivity.this);
//                boolean isDeviceAvailable = !NotificationInterceptor.getInstance(
//                        SplashActivity.this).isDeviceAvailable();
//                if (isFirstIn && !(isMeizu && isDeviceAvailable)) {
//                    Intent intent = new Intent(SplashActivity.this, InitSettingActivity.class);
//                    startActivity(intent);
//                }
//                finish();
//                overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
//                        R.anim.umeng_fb_slide_out_from_left);
            }
        }, 2000);
    }

}
