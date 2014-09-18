
package cn.zmdx.kaka.locker.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.guide.GuideActivity;
import cn.zmdx.kaka.locker.service.PandoraService;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainSettingsActivity extends FragmentActivity {

    private Intent mServiceIntent = null;

    boolean isFirstIn = false;

    private static final int GO_HOME = 1000;

    private static final int GO_GUIDE = 1001;

    private static final String SHAREDPREFERENCES_NAME = "first_pref";

    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mServiceIntent = new Intent(getApplicationContext(), PandoraService.class);
        startService(mServiceIntent);
        init();
        super.onCreate(savedInstanceState);
        MobclickAgent.openActivityDurationTrack(false);
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.silentUpdate(this);
        setContentView(R.layout.main_setting_activity);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, new MainSettingsFragment()).commit();

    }

    private void init() {
        // 读取SharedPreferences中需要的数据
        // 使用SharedPreferences来记录程序的使用次数
        SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);

        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = preferences.getBoolean("isFirstIn", true);

        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
        if (!isFirstIn) {
            // mHandler.sendEmptyMessage(GO_HOME);
        } else {
            mHandler.sendEmptyMessage(GO_GUIDE);
        }

    }

    private void goHome() {
        Intent intent = new Intent(this, MainSettingsActivity.class);
    }

    private void goGuide() {
        Intent intent = new Intent(this, GuideActivity.class);
        this.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
