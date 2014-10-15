
package cn.zmdx.kaka.locker.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;

import com.umeng.analytics.MobclickAgent;

public class MAboutActivity extends Activity {

    private TextView mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_about_us);
        initView();
    }

    private void initView() {
        mVersion = (TextView) findViewById(R.id.setting_about_version);
        String version = PandoraUtils.getVersionCode(this);
        mVersion.setText(version);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MAboutFragment"); // 统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MAboutFragment");
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }
}
