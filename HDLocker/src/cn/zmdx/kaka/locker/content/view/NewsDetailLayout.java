
package cn.zmdx.kaka.locker.content.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;

public class NewsDetailLayout extends FrameLayout implements View.OnClickListener{

    private WebView mWebView;

    private View mBackBtn, mForwardBtn, mLikeBtn, mShareBtn;

    private PandoraBoxManager mPbManager;
    public NewsDetailLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NewsDetailLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsDetailLayout(Context context) {
        this(context, null);
    }

    public NewsDetailLayout(PandoraBoxManager pbManager) {
        this(HDApplication.getContext());
        mPbManager = pbManager;
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.news_detail_layout, this);
        mWebView = (WebView) view.findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());

        mBackBtn = view.findViewById(R.id.back);
        mForwardBtn = view.findViewById(R.id.forward);
        mLikeBtn = view.findViewById(R.id.like);
        mShareBtn = view.findViewById(R.id.share);

        mBackBtn.setOnClickListener(this);
        mForwardBtn.setOnClickListener(this);
        mLikeBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                mPbManager.closeDetailPage();
            }
        } else if (v == mForwardBtn) {
            mWebView.goForward();
        } else if (v == mLikeBtn) {
            
        } else if (v == mShareBtn) {
            
        }
    }
}
