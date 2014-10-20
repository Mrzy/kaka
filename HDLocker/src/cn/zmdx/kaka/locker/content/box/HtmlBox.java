
package cn.zmdx.kaka.locker.content.box;

import cn.zmdx.kaka.locker.HDApplication;
import android.content.Context;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HtmlBox extends BaseBox {

    private Context mContext;

    private PandoraData mData;

    private WebView mWebView;

    public HtmlBox(PandoraData data) {
        mData = data;
        mContext = HDApplication.getInstannce();
        mWebView = new WebView(mContext);
        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
    }

    @Override
    public int getCategory() {
        return CATEGORY_HTML;
    }

    @Override
    public PandoraData getData() {
        return mData;
    }

    @Override
    public View getContainer() {
        return mWebView;
    }

    @Override
    public View getRenderedView() {
        mWebView.loadUrl(mData.getmContentUrl());
        return mWebView;
    }

}
