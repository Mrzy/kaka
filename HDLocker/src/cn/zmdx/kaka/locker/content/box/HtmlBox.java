
package cn.zmdx.kaka.locker.content.box;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;

public class HtmlBox implements IPandoraBox {
    protected static final int MAX_TIMES_SHOW_GUIDE = 10;


    private Context mContext;

    private PandoraData mData;

    private View mEntireView;

    private WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    public HtmlBox(Context context, PandoraData data) {
        mData = data;
        mContext = context;
        mEntireView = LayoutInflater.from(context).inflate(R.layout.pandora_box_html_layout, null);
        mWebView = (WebView) mEntireView.findViewById(R.id.pandora_box_html_context_show);
        mWebView.getSettings().setJavaScriptEnabled(true);
        // mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
    }

    public static PandoraData convertFormServerImageData(ServerImageData data) {
        PandoraData pd = new PandoraData();
        pd.setmId(data.getId());
        pd.setmContentUrl(data.getUrl());
        return pd;
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
        return mEntireView;
    }
}
