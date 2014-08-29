
package cn.zmdx.kaka.locker.settings;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

public class MainSettingsActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().add(new MainSettingsFragment(), null).commit();
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
