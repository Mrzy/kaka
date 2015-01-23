
package cn.zmdx.kaka.fast.locker.settings;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.guide.GuideActivity;
import cn.zmdx.kaka.fast.locker.service.PandoraService;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;

import com.umeng.analytics.MobclickAgent;

public class MainSettingsActivityOld extends BaseActivity {

    private Intent mServiceIntent = null;

    boolean isFirstIn = false;

    private static final int GO_GUIDE = 1001;

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<MainSettingsActivityOld> mActivity;

        public MyHandler(MainSettingsActivityOld activity) {
            mActivity = new WeakReference<MainSettingsActivityOld>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainSettingsActivityOld activity = mActivity.get();
            switch (msg.what) {
                case GO_GUIDE:
                    activity.goGuide();
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 19) {
            Window window = getWindow();
            // window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            // WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
            // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        mServiceIntent = new Intent(getApplicationContext(), PandoraService.class);
        startService(mServiceIntent);
        init();
        super.onCreate(savedInstanceState);
        // AppUninstall.openUrlWhenUninstall(this, "http://www.hdlocker.com");
        MobclickAgent.openActivityDurationTrack(false);
        // UmengUpdateAgent.silentUpdate(this);
        setContentView(R.layout.main_setting_activity);
        // getWindow().getAttributes().flags |=
        // LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, new MainSettingsFragment()).commit();
    }

    private void init() {
        // 读取SharedPreferences中需要的数据
        // 使用SharedPreferences来记录程序的使用次数

        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = !PandoraConfig.newInstance(this).isHasGuided();

        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
        if (isFirstIn) {
            mHandler.sendEmptyMessage(GO_GUIDE);
        }
    }

    private void goGuide() {
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainSettingsActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainSettingsActivity"); // 保证 onPageEnd
                                                         // 在onPause
        // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

}
