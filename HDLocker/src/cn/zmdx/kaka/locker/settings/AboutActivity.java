
package cn.zmdx.kaka.locker.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.Window;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.umeng.analytics.MobclickAgent;

public class AboutActivity extends ActionBarActivity {

    private TypefaceTextView mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_about_us);
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.action_bar_bg_blue));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        mVersion = (TypefaceTextView) findViewById(R.id.setting_about_version);
        String version = PandoraUtils.getVersionCode(this);
        mVersion.setText(version);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MAboutActivity");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MAboutActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }
}
