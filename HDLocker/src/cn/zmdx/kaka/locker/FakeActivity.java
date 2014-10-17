
package cn.zmdx.kaka.locker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import cn.zmdx.kaka.locker.LockScreenManager.ILockScreenListener;
import cn.zmdx.kaka.locker.share.PandoraShareManager;
import cn.zmdx.kaka.locker.utils.HDBLOG;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;

public class FakeActivity extends Activity {

    public static final String ACTION_PANDORA_SHARE = "actionPandoraShare";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 19) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        LockScreenManager.getInstance().setOnLockScreenListener(new ILockScreenListener() {
            @Override
            public void onLock() {

            }

            @Override
            public void onUnLock() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PANDORA_SHARE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mShareReceiver, filter);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    // | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            // | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            // getWindow().getDecorView().setSystemUiVisibility(
            // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            // | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        MobclickAgent.onPageStart("FakeActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FakeActivity"); // 保证 onPageEnd 在onPause
        // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mShareReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private void sinaShare(String imagePath) {
        UMSocialService sinaShare = UMServiceFactory.getUMSocialService("cn.zmdx.kaka.locker");
        SinaShareContent sina = new SinaShareContent();
        sina.setShareContent(PandoraShareManager.ShareContent);
        sina.setTargetUrl(PandoraShareManager.TargetUrl);
        sina.setTitle(PandoraShareManager.Title);
        sina.setShareImage(new UMImage(this, imagePath));
        sinaShare.setShareMedia(sina);
        sinaShare.setShareMedia(new UMImage(FakeActivity.this, R.drawable.ic_launcher));
        sinaShare.getConfig().setSsoHandler(new SinaSsoHandler());
        sinaShare.directShare(FakeActivity.this, SHARE_MEDIA.SINA, new SnsPostListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
                FakeActivity.this.finish();
            }
        });
    }

    private void renrenShare(String imagePath) {
        UMSocialService renrenShare = UMServiceFactory.getUMSocialService("cn.zmdx.kaka.locker");
        RenrenShareContent renren = new RenrenShareContent();
        renren.setShareContent(PandoraShareManager.ShareContent);
        renren.setTargetUrl(PandoraShareManager.TargetUrl);
        renren.setTitle(PandoraShareManager.Title);
        renren.setShareImage(new UMImage(this, imagePath));
        renrenShare.setShareMedia(renren);
        renrenShare.getConfig().setSsoHandler(
                new RenrenSsoHandler(FakeActivity.this, "272417",
                        "f56d084e27f14efda76788f31045a542", "27e373b49cad4fd6b4f78bdae9129758"));
        renrenShare.directShare(FakeActivity.this, SHARE_MEDIA.RENREN, new SnsPostListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
                FakeActivity.this.finish();
            }
        });
    }

    private void qzoneShare(String imagePath) {
        UMSocialService qzoneShare = UMServiceFactory.getUMSocialService("cn.zmdx.kaka.locker");
        QZoneShareContent qzone = new QZoneShareContent();
        // 设置分享文字
        qzone.setShareContent(PandoraShareManager.ShareContent);
        // 设置点击消息的跳转URL
        qzone.setTargetUrl(PandoraShareManager.TargetUrl);
        // 设置分享内容的标题
        qzone.setTitle(PandoraShareManager.Title);
        // 设置分享图片
        qzone.setShareImage(new UMImage(this, imagePath));
        qzoneShare.setShareMedia(qzone);
        qzoneShare.getConfig().setSsoHandler(
                new QZoneSsoHandler(FakeActivity.this, "1103193086", "XOgkKrK9tZOcawOF"));
        qzoneShare.directShare(FakeActivity.this, SHARE_MEDIA.QZONE, new SnsPostListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
                FakeActivity.this.finish();
            }
        });
    }

    // private void weixinShare(String imagePath) {
    // mWeixinShare.setShareContent("潘多拉锁屏----下拉有料。www.hdlocker.com");
    // mWeixinShare.setShareImage(new UMImage(this, imagePath));
    // mWeixinShare.getConfig().setSsoHandler(
    // new RenrenSsoHandler(FakeActivity.this, "272417",
    // "f56d084e27f14efda76788f31045a542", "27e373b49cad4fd6b4f78bdae9129758"));
    // mWeixinShare.directShare(FakeActivity.this, SHARE_MEDIA.RENREN, new
    // SnsPostListener() {
    // @Override
    // public void onStart() {
    // }
    //
    // @Override
    // public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2)
    // {
    // FakeActivity.this.finish();
    // }
    // });
    // }

    private final BroadcastReceiver mShareReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_PANDORA_SHARE)) {
                int platform = intent.getIntExtra("platform", -1);
                String imagePath = intent.getStringExtra("imagePath");
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("收到分享事件，platform=" + platform);
                }
                if (platform == -1) {
                    return;
                }
                switch (platform) {
                // TODO 调用各对应平台分享接口
                    case PandoraShareManager.Sina:
                        sinaShare(imagePath);
                        break;
                    case PandoraShareManager.Renren:
                        renrenShare(imagePath);
                        break;
                    case PandoraShareManager.Tencent:
                        qzoneShare(imagePath);
                        break;
                    case PandoraShareManager.Weixin:

                        break;
                    default:
                }
            }
        }
    };
}
