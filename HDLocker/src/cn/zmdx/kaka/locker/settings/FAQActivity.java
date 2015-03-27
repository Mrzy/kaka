
package cn.zmdx.kaka.locker.settings;

import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
        setContentView(R.layout.activity_faq);
        WebView mWebView = (WebView) findViewById(R.id.faq_webview);
        if (HDBNetworkState.isWifiNetwork()) {
            mWebView.loadUrl(UrlBuilder.getBaseUrl() + "commonQuestions.html");
        } else {
            mWebView.loadUrl("file:///android_asset/commonQuestions.html");
        }
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
