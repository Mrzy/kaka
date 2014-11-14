
package cn.zmdx.kaka.locker.content.box;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

public class HtmlBox implements IPandoraBox {
    protected static final int MAX_TIMES_SHOW_GUIDE = 10;

    private int mTextHtmlTimes;

    private Context mContext;

    private PandoraData mData;

    private ImageButton mImageButton;

    private View mEntireView;

    private WebView mWebView;

    private TextView mTextView;

    private FoldablePage mPage;

    private RelativeLayout mRelativeLayout;

    @SuppressLint("SetJavaScriptEnabled")
    public HtmlBox(Context context, FoldablePage page, PandoraData data) {
        mData = data;
        mPage = page;
        mContext = context;
        mEntireView = LayoutInflater.from(context).inflate(R.layout.pandora_box_html_layout, null);
        mWebView = (WebView) mEntireView.findViewById(R.id.pandora_box_html_context_show);
        mRelativeLayout = (RelativeLayout) mEntireView
                .findViewById(R.id.pandora_box_html_single_back_btn);
        mTextHtmlTimes = PandoraConfig.newInstance(mContext).getGuideHtmlTimesInt();
        mTextView = (TextView) mEntireView.findViewById(R.id.pandora_box_html_back_text);
        if (mTextHtmlTimes < MAX_TIMES_SHOW_GUIDE) {
            if (null != mTextView) {
                mTextView.setText(mContext.getResources().getString(
                        R.string.pandora_box_html_back_text));
            }
        }
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
        mRelativeLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPage.foldBack();
                if (mTextHtmlTimes < MAX_TIMES_SHOW_GUIDE) {
                    PandoraConfig.newInstance(mContext).saveHtmlTimes(mTextHtmlTimes + 1);
                }
            }
        });
        return mEntireView;
    }
}
