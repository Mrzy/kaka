
package cn.zmdx.kaka.locker.settings;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.guide.GuideActivity;
import cn.zmdx.kaka.locker.service.PandoraService;
import cn.zmdx.kaka.locker.settings.MainSettingFragment.IMainSettingListener;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.umeng.analytics.MobclickAgent;

public class MainSettingsActivity extends FragmentActivity implements IMainSettingListener {

    private Intent mServiceIntent = null;

    boolean isFirstIn = false;

    private static final int GO_GUIDE = 1001;

    private TypefaceTextView mTitle;

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<MainSettingsActivity> mActivity;

        public MyHandler(MainSettingsActivity activity) {
            mActivity = new WeakReference<MainSettingsActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainSettingsActivity activity = mActivity.get();
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
        super.onCreate(savedInstanceState);
        // AppUninstall.openUrlWhenUninstall(this, "http://www.hdlocker.com");
        MobclickAgent.openActivityDurationTrack(false);
        // UmengUpdateAgent.silentUpdate(this);
        setContentView(R.layout.main_setting_activity);

        mTitle = (TypefaceTextView) findViewById(R.id.title);
        mTitle.setVisibility(View.VISIBLE);

        // getWindow().getAttributes().flags |=
        // LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, new MainSettingFragment()).commit();
    }

    private void init() {
        isFirstIn = !PandoraConfig.newInstance(this).isHasGuided();
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
        MobclickAgent.onPageStart("MainSettingsActivity");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainSettingsActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onItemClick(String title) {
        mTitle.setText(title);
    }

}
