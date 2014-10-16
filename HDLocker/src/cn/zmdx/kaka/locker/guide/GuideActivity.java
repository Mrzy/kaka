
package cn.zmdx.kaka.locker.guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import cn.zmdx.kaka.locker.settings.InitSettingActivity;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

import com.umeng.analytics.MobclickAgent;

/**
 * 引导界面
 */
public class GuideActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Button btn = new Button(this);
        btn.setText("开始体验");
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setGuided();
                goHome();
                PandoraConfig.newInstance(GuideActivity.this).savePandolaLockerState(true);
            }
        });
        setContentView(btn);
    }

    private void goHome() {
        // 跳转
        Intent intent = new Intent(this, InitSettingActivity.class);
        intent.putExtra("isFirst", true);
        startActivity(intent);
        finish();
    }

    /**
     * 设置已经引导过了，下次启动不用再次引导
     */
    private void setGuided() {
        PandoraConfig.newInstance(this).saveHasGuided();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SplashScreen"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SplashScreen"); // 保证 onPageEnd 在onPause
                                                 // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
