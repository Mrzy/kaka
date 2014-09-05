
package cn.zmdx.kaka.locker.settings;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import cn.zmdx.kaka.locker.R;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainSettingsActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        MobclickAgent.openActivityDurationTrack(false);
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.silentUpdate(this);
        String manufacturer = android.os.Build.MANUFACTURER;// 获取制造商名字
        setContentView(R.layout.main_setting_activity);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, new MainSettingsFragment()).commit();

        // DisplayMetrics metric = new DisplayMetrics();
        // getWindowManager().getDefaultDisplay().getMetrics(metric);
        // int width = metric.widthPixels; // 屏幕宽度（像素）
        // int height = metric.heightPixels; // 屏幕高度（像素）
        // float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
        // int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
        // Log.d("syc",
        // "width="+width+" height="+height+" densityDpi="+densityDpi+" density="+density);
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
