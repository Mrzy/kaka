
package cn.zmdx.kaka.locker.settings;

import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.network.UrlBuilder;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;

public class FAQActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.action_bar_bg_blue));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_faq);
        WebView mWebView = (WebView) findViewById(R.id.faq_webview);
        if (HDBNetworkState.isWifiNetwork()) {
            mWebView.loadUrl("http://pandora.hdlocker.com/pandora/commonQuestions.html");
        } else {
            mWebView.loadUrl("file:///android_asset/commonQuestions.html");
        }
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

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FAQActivity");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FAQActivity");
        MobclickAgent.onPause(this);
    }
}
