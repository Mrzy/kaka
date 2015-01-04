
package cn.zmdx.kaka.locker.share;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.share.PandoraShareManager.PandoraShareData;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils.ILoadBitmapCallback;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class ShareContentActivity extends Activity implements TextWatcher, OnClickListener {

    private View mRootView;

    private EditText mSinaEdit;

    private TypefaceTextView mSinaSubmit;
    
    private ProgressBar mSendPb;

    private TypefaceTextView mSinaContentCount;

    private TypefaceTextView mSinaContentTitle;

    private static final int MAX_CONTENT_COUNT = 80;

    private PandoraShareData mShareData;

    private AuthInfo mAuthInfo;

    private SsoHandler mSsoHandler;

    private boolean isSinaAuth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        int type = getIntent().getIntExtra("type", PandoraShareManager.TYPE_SHARE_QQ);
        mShareData = getIntent().getParcelableExtra("shareData");
        switch (type) {
            case PandoraShareManager.TYPE_SHARE_QQ:
                shareToQzone();
                break;
            case PandoraShareManager.TYPE_SHARE_SINA:
                shareToSina();
                break;

            default:
                break;
        }

    }

    private void shareToQzone() {
        setTheme(R.style.locker_password_dialog_theme);
        PandoraQQShareManager.getInstance().shareToQzone(this, mShareData, new IUiListener() {

            @Override
            public void onError(UiError e) {
                Toast.makeText(ShareContentActivity.this, "分享失败，请重试!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onComplete(Object response) {
                Toast.makeText(ShareContentActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onCancel() {
                Toast.makeText(ShareContentActivity.this, "已取消分享", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void shareToSina() {
        setContentView(R.layout.pandora_share_content);
        initSinaView();
    }

    private void initSinaView() {
        mRootView = findViewById(R.id.pandora_share_content);
        initWallpaper();
        mSinaContentTitle = (TypefaceTextView) findViewById(R.id.pandora_share_sina_content_title);
        mSinaContentTitle.setText(mShareData.mTitle);
        mSinaContentCount = (TypefaceTextView) findViewById(R.id.pandora_share_sina_edit_text_count);
        mSinaEdit = (EditText) findViewById(R.id.pandora_share_sina_edit);
        mSinaEdit.addTextChangedListener(this);
        mSinaSubmit = (TypefaceTextView) findViewById(R.id.pandora_share_sina_submit);
        mSinaSubmit.setOnClickListener(this);
        mSendPb = (ProgressBar) findViewById(R.id.pandora_share_send_progress);

        Oauth2AccessToken mAccessToken = SinaAccessTokenKeeper.readAccessToken(this);
        String token = mAccessToken.getToken();
        long expiresIn = mAccessToken.getExpiresTime();
        long ssoTime = SinaAccessTokenKeeper.getUserSSOTime(this);
        if (TextUtils.isEmpty(token) || (System.currentTimeMillis() - ssoTime) >= expiresIn) {
            isSinaAuth = false;
        } else {
            isSinaAuth = true;
        }

    }

    private void initWallpaper() {
        Theme theme = ThemeManager.getCurrentTheme();
        if (theme.isDefaultTheme()) {
            mRootView.setBackgroundResource(theme.getmBackgroundResId());
        } else {
            WallpaperUtils.loadBackgroundBitmap(ShareContentActivity.this, theme.getFilePath(),
                    new ILoadBitmapCallback() {

                        @SuppressWarnings("deprecation")
                        @Override
                        public void imageLoaded(Bitmap bitmap, String filePath) {
                            mRootView.setBackgroundDrawable(new BitmapDrawable(getResources(),
                                    bitmap));
                        }
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null != mSsoHandler) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mSinaContentCount.setText("" + (MAX_CONTENT_COUNT - mSinaEdit.getEditableText().length()));
        if (mSinaEdit.getEditableText().toString().length() > MAX_CONTENT_COUNT) {
            mSinaSubmit.setClickable(false);
        } else {
            mSinaSubmit.setClickable(true);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pandora_share_sina_submit:
                mSendPb.setVisibility(View.VISIBLE);
                if (isSinaAuth) {
                    PandoraSinaShareManager.getInstance().shareToSina(ShareContentActivity.this,
                            mShareData, mSinaEdit.getEditableText().toString(), mRequestListener);
                } else {
                    mAuthInfo = new AuthInfo(this, PandoraSinaShareManager.SINA_APPID,
                            PandoraSinaShareManager.REDIRECT_URL, "");
                    mSsoHandler = new SsoHandler(this, mAuthInfo);
                    PandoraSinaShareManager.getInstance().sinaAuth(mSsoHandler, mWeiboAuthListener);
                }
                break;

            default:
                break;
        }
    }

    
    private RequestListener mRequestListener = new RequestListener() {

        @Override
        public void onWeiboException(WeiboException arg0) {
            Toast.makeText(ShareContentActivity.this, "分享失败，请重试", Toast.LENGTH_LONG).show();
            mSendPb.setVisibility(View.GONE);
            finish();
        }

        @Override
        public void onComplete(String arg0) {
            Toast.makeText(ShareContentActivity.this, "分享成功", Toast.LENGTH_LONG).show();
            mSendPb.setVisibility(View.GONE);
            finish();
        }
    };

    private WeiboAuthListener mWeiboAuthListener = new WeiboAuthListener() {

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(ShareContentActivity.this, "授权失败，请重试", Toast.LENGTH_LONG).show();
            isSinaAuth = false;
            mSendPb.setVisibility(View.GONE);
        }

        @Override
        public void onComplete(Bundle values) {
            Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                SinaAccessTokenKeeper.writeAccessToken(ShareContentActivity.this, mAccessToken,
                        System.currentTimeMillis());
                isSinaAuth = true;
                PandoraSinaShareManager.getInstance().shareToSina(ShareContentActivity.this,
                        mShareData, mSinaEdit.getEditableText().toString(), mRequestListener);
            } else {
                Toast.makeText(ShareContentActivity.this, "授权失败，请重试", Toast.LENGTH_LONG).show();
                isSinaAuth = false;
                mSendPb.setVisibility(View.GONE);
            }
            
        }

        @Override
        public void onCancel() {
            Toast.makeText(ShareContentActivity.this, "用户取消授权，授权失败", Toast.LENGTH_LONG).show();
            isSinaAuth = false;
            mSendPb.setVisibility(View.GONE);
        }
    };
}
