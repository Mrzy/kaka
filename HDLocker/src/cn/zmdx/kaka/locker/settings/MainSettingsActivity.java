
package cn.zmdx.kaka.locker.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.service.PandoraService;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainSettingsActivity extends FragmentActivity {

    private Intent mServiceIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mServiceIntent = new Intent(getApplicationContext(), PandoraService.class);
        startService(mServiceIntent);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        MobclickAgent.openActivityDurationTrack(false);
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.silentUpdate(this);
        setContentView(R.layout.main_setting_activity);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, new MainSettingsFragment()).commit();
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
