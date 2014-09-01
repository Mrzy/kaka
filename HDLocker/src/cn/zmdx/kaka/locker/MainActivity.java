
package cn.zmdx.kaka.locker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import cn.zmdx.kaka.locker.settings.MainSettingsActivity;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        MobclickAgent.openActivityDurationTrack(false);
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.silentUpdate(this);
        TextView tv = new TextView(this);
        tv.setText("test");
        setContentView(tv);
        Intent in = new Intent();
        in.setClass(MainActivity.this, MainSettingsActivity.class);
        startActivity(in);
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
