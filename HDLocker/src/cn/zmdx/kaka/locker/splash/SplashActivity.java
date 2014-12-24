
package cn.zmdx.kaka.locker.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.MainSettingsActivity;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_splash);
        HDBThreadUtils.postOnWorkerDelayed(new Runnable() {
            
            @Override
            public void run() {
                boolean isForceTheme = PandoraConfig.newInstance(SplashActivity.this).getChristmasForceTheme();
                if (!isForceTheme) {
                    PandoraConfig.newInstance(SplashActivity.this).saveThemeId(ThemeManager.THEME_ID_DEFAULT);
                    PandoraConfig.newInstance(SplashActivity.this).saveChristmasForceTheme(true);
                }
                
                Intent in =new Intent();
                in.setClass(SplashActivity.this, MainSettingsActivity.class);
                startActivity(in);
                finish();
            }
        }, 1500);
    }
}