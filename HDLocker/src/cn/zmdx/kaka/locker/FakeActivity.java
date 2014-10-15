
package cn.zmdx.kaka.locker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import cn.zmdx.kaka.locker.LockScreenManager.ILockScreenListener;
import cn.zmdx.kaka.locker.utils.HDBLOG;

import com.umeng.analytics.MobclickAgent;

public class FakeActivity extends Activity {

    public static final String ACTION_PANDORA_SHARE = "actionPandoraShare";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LockScreenManager.getInstance().setOnLockScreenListener(new ILockScreenListener() {

            @Override
            public void onLock() {

            }

            @Override
            public void onUnLock() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PANDORA_SHARE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mShareReceiver, filter);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    // | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            // | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            // getWindow().getDecorView().setSystemUiVisibility(
            // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            // | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mShareReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private final BroadcastReceiver mShareReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_PANDORA_SHARE)) {
                int platform = intent.getIntExtra("platform", -1);
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("收到分享事件，platform=" + platform);
                }
                if (platform == -1) {
                    return;
                }
                switch (platform) {
                    case 1:
                        // TODO 调用对应平台分享接口
                        break;
                    case 2:
                        // TODO 调用对应平台分享接口
                        break;
                    case 3:
                        // TODO 调用对应平台分享接口
                        break;
                    case 4:
                        // TODO 调用对应平台分享接口
                        break;
                    default:
                }
            }
        }
    };
}
