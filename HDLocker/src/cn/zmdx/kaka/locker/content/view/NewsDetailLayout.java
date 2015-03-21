
package cn.zmdx.kaka.locker.content.view;

import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.AttributeSet;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.sharesdk.framework.ShareSDK;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.network.UrlBuilder;
import cn.zmdx.kaka.locker.share.PandoraShareManager;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;

public class NewsDetailLayout extends FrameLayout implements View.OnClickListener, OnTouchListener,
        OnGestureListener {

    private static String URL = UrlBuilder.getBaseUrl() + "locker!addDataImgTableTop.action?";

    private WebView mWebView;

    private ImageView mBackImageView, mLikeImageView, mShareImageView;

    private LinearLayout mShareLayout;

    private TypefaceTextView mLikeNumber;

    private ImageView mWecharShareIcon, mWecharCircleShareIcon, mQQShareIcon, mSinaShareIcon;

    private GestureDetector mGestureDetector;// 实例化手势对象

    private PandoraBoxManager mPbManager;

    private ContentLoadingProgressBar mProgressBar;

    private ServerImageData mData;

    private boolean isLoadError = false;

    private static final int SWIPE_MIN_DISTANCE = BaseInfoHelper.dip2px(HDApplication.getContext(),
            50);

    private static final int SWIPE_THRESHOLD_VELOCITY = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 500);

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

    public NewsDetailLayout(PandoraBoxManager pbManager, ServerImageData sid) {
        this(HDApplication.getContext());
        mPbManager = pbManager;
        mData = sid;
        load(sid.getImageDesc());
        ShareSDK.initSDK(HDApplication.getContext());
        if (mData.isLiked()) {
            mLikeImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.news_detail_like_icon));
        }
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.news_detail_layout, this);
        mProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.progress);
        mWebView = (WebView) view.findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mWebView.getSettings().setLoadsImagesAutomatically(false);
        }
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
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!mWebView.getSettings().getLoadsImagesAutomatically()) {
                    mWebView.getSettings().setLoadsImagesAutomatically(true);
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                    String failingUrl) {
                isLoadError = true;
                if (HDBNetworkState.isNetworkAvailable()) {
                    view.loadData(getContext().getString(R.string.newsdetail_tip_press_try_again),
                            "text/html; charset=UTF-8", null);
                } else {
                    view.loadData(
                            getContext().getString(R.string.newsdetail_tip_please_check_network),
                            "text/html; charset=UTF-8", null);
                }
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        if (PandoraConfig.newInstance(getContext()).isOnlyWifiLoadImage()
                && !HDBNetworkState.isWifiNetwork()) {
            mWebView.getSettings().setBlockNetworkImage(true);
        }
        mWebView.setOnTouchListener(this);

        mGestureDetector = new GestureDetector(getContext(), this);

        mBackImageView = (ImageView) view.findViewById(R.id.back);
        mLikeImageView = (ImageView) view.findViewById(R.id.like);
        mShareImageView = (ImageView) view.findViewById(R.id.share);

        mBackImageView.setOnClickListener(this);
        mLikeImageView.setOnClickListener(this);
        mShareImageView.setOnClickListener(this);

        mLikeNumber = (TypefaceTextView) view.findViewById(R.id.like_number);

        initShareLayout(view);
    }

    private void initShareLayout(View view) {

        if (PandoraShareManager.isAvilible(getContext(), PandoraShareManager.PACKAGE_WECHAR_STRING)) {
            view.findViewById(R.id.share_wechat_icon_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.share_wechat_circle_icon_layout).setVisibility(View.VISIBLE);
        }
        if (PandoraShareManager.isAvilible(getContext(), PandoraShareManager.PACKAGE_QQ_STRING)) {
            view.findViewById(R.id.share_wechat_qq_icon_layout).setVisibility(View.VISIBLE);
        }
        if (PandoraShareManager.isAvilible(getContext(), PandoraShareManager.PACKAGE_SINA_STRING)) {
            view.findViewById(R.id.share_wechat_sina_icon_layout).setVisibility(View.VISIBLE);
        }
        mShareLayout = (LinearLayout) view.findViewById(R.id.share_detail_layout);
        mWecharShareIcon = (ImageView) view.findViewById(R.id.share_wechat_icon);
        mWecharCircleShareIcon = (ImageView) view.findViewById(R.id.share_wechat_circle_icon);
        mQQShareIcon = (ImageView) view.findViewById(R.id.share_wechat_qq_icon);
        mSinaShareIcon = (ImageView) view.findViewById(R.id.share_wechat_sina_icon);

        mShareLayout.setOnClickListener(this);
        mWecharShareIcon.setOnClickListener(this);
        mWecharCircleShareIcon.setOnClickListener(this);
        mQQShareIcon.setOnClickListener(this);
        mSinaShareIcon.setOnClickListener(this);
    }

    private void load(String url) {
        mWebView.loadUrl(url);
    }

    private void back() {
        if (isLoadError) {
            exit(true);
        }
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            exit(true);
        }
    }

    private void exit(boolean withAnimator) {
        mPbManager.closeDetailPage(withAnimator);
    }

    @Override
    public void onClick(View v) {
        if (v == mBackImageView) {
            back();
        } else if (v == mLikeImageView) {
            if (!mData.isLiked()) {
                mData.setLiked(true);
                mLikeImageView.setImageDrawable(getResources().getDrawable(
                        R.drawable.news_detail_like_icon));
                showLikeNumber();
                toLikeNews();
            }
        } else if (v == mShareImageView) {
            mShareLayout.setVisibility(View.VISIBLE);
        } else if (v == mShareLayout) {
            mShareLayout.setVisibility(View.GONE);
        } else if (v == mWecharShareIcon) {
            PandoraShareManager.shareContent(getContext(), mData,
                    PandoraShareManager.TYPE_SHARE_WECHAT);
        } else if (v == mWecharCircleShareIcon) {
            PandoraShareManager.shareContent(getContext(), mData,
                    PandoraShareManager.TYPE_SHARE_WECHAT_CIRCLE);
        } else if (v == mQQShareIcon) {
            PandoraShareManager
                    .shareContent(getContext(), mData, PandoraShareManager.TYPE_SHARE_QQ);
        } else if (v == mSinaShareIcon) {
            PandoraShareManager.shareContent(getContext(), mData,
                    PandoraShareManager.TYPE_SHARE_SINA);
        }
    }

    private void showLikeNumber() {
        mLikeNumber.setText("+1");
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mLikeNumber, "scaleX", 0, 1f);
        scaleXAnimator.setDuration(300);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mLikeNumber, "scaleY", 0, 1f);
        scaleYAnimator.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mLikeNumber, "alpha", 1, 0);
                scaleXAnimator.setDuration(200);
                scaleXAnimator.start();
            }
        });
        animatorSet.start();
    }

    private void toLikeNews() {
        JsonObjectRequest request = new JsonObjectRequest(URL + "?id=" + mData.getCloudId(), null,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestManager.getRequestQueue().add(request);
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
