
package cn.zmdx.kaka.locker.content.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

public class NewsDetailLayout extends FrameLayout implements View.OnClickListener, OnTouchListener,
        OnGestureListener {

    private WebView mWebView;

    private View mBackBtn, mForwardBtn, mLikeBtn, mShareBtn;

    private GestureDetector mGestureDetector;// 实例化手势对象

    private PandoraBoxManager mPbManager;

    private ContentLoadingProgressBar mProgressBar;

    private static final int SWIPE_MIN_DISTANCE = BaseInfoHelper.dip2px(HDApplication.getContext(), 50);

    private static final int SWIPE_THRESHOLD_VELOCITY = BaseInfoHelper.dip2px(HDApplication.getContext(), 500);

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
        mProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.progress);
        mWebView = (WebView) view.findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.show();
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.hide();
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.setOnTouchListener(this);

        mGestureDetector = new GestureDetector(getContext(), this);

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

    private void back() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            animate().translationX(getWidth()).setDuration(300).setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    mPbManager.closeDetailPage();
                }
            }).start();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            back();
        } else if (v == mForwardBtn) {
            mWebView.goForward();
        } else if (v == mLikeBtn) {

        } else if (v == mShareBtn) {

        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float xDistance = e2.getX() - e1.getX();
        float yDistance = Math.abs(e1.getY() - e2.getY());
        if (xDistance > SWIPE_MIN_DISTANCE && xDistance > yDistance
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            back();
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}
