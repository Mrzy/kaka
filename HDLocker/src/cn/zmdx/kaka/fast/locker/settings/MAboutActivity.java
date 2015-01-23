
package cn.zmdx.kaka.fast.locker.settings;

import android.os.Bundle;
import android.view.Window;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.fast.locker.widget.TypefaceTextView;

import com.umeng.analytics.MobclickAgent;

public class MAboutActivity extends BaseActivity {

    private TypefaceTextView mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_about_us);
        initView();
    }

    private void initView() {
        mVersion = (TypefaceTextView) findViewById(R.id.setting_about_version);
        String version = PandoraUtils.getVersionCode(this);
        mVersion.setText(version);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MAboutActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MAboutActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }
}
