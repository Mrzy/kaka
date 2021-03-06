
package cn.zmdx.kaka.fast.locker;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import cn.zmdx.kaka.fast.locker.LockScreenManager.ILockScreenListener;
import cn.zmdx.kaka.fast.locker.settings.MainSettingsActivity;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.sound.LockSoundManager;
import cn.zmdx.kaka.fast.locker.weather.PandoraLocationManager;

import com.umeng.analytics.MobclickAgent;

public class FakeActivity extends Activity {

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LockScreenManager.getInstance().isLocked()) {
            finish();
        }

        if (Build.VERSION.SDK_INT >= 19) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        LockScreenManager.getInstance().setOnLockScreenListener(new ILockScreenListener() {
            PandoraConfig config = PandoraConfig.newInstance(getApplicationContext());

            @Override
            public void onLock() {
                boolean lockScreenVoice = config.isLockScreenVoice();
                if (lockScreenVoice) {
                    LockSoundManager.play(LockSoundManager.SOUND_ID_LOCK);
                }
            }

            @Override
            public void onUnLock() {
                finish();
                overridePendingTransition(0, 0);
                boolean lockScreenVoice = config.isLockScreenVoice();
                if (lockScreenVoice) {
                    LockSoundManager.play(LockSoundManager.SOUND_ID_UNLOCK);
                }
            }

            @Override
            public void onInitDefaultImage() {
                Intent intent = new Intent();
                intent.setClass(FakeActivity.this, MainSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                        R.anim.umeng_fb_slide_out_from_left);
                finish();
            }
        });
        PandoraLocationManager.getInstance().registLocationUpdates();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        int systemUI = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (!ViewConfiguration.get(this).hasPermanentMenuKey()) {
            systemUI |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        if (!PandoraConfig.newInstance(this).isNeedNotice(this)) {
            // systemUI |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            systemUI |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        getWindow().getDecorView().setSystemUiVisibility(systemUI);
        MobclickAgent.onPageStart("FakeActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FakeActivity"); // 保证 onPageEnd 在onPause
        // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        PandoraLocationManager.getInstance().unRegistLocationUpdates();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        LockScreenManager.getInstance().onBackPressed();
    }
}
