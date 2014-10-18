
package cn.zmdx.kaka.locker.share;

import android.app.Activity;

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
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class PandoraShareManager {
    public static final int Sina = 1;

    public static final int Renren = 2;

    public static final int Tencent = 3;

    public static final int Weixin = 4;

    public static final int WeixinCircle = 5;

    public static final String ShareContent = "潘多拉锁屏----下拉有料";

    public static final String TargetUrl = "http://android.myapp.com/myapp/detail.htm?apkName=cn.zmdx.kaka.locker";

    public static final String Title = "潘多拉锁屏";

    public static void sinaShare(final Activity activity, String imagePath) {
        UMSocialService sinaShare = UMServiceFactory.getUMSocialService("cn.zmdx.kaka.locker");
        SinaShareContent sina = new SinaShareContent();
        sina.setShareContent(PandoraShareManager.ShareContent);
        sina.setTargetUrl(PandoraShareManager.TargetUrl);
        sina.setTitle(PandoraShareManager.Title);
        sina.setShareImage(new UMImage(activity, imagePath));
        sinaShare.setShareMedia(sina);
        sinaShare.getConfig().setSsoHandler(new SinaSsoHandler());
        sinaShare.postShare(activity, SHARE_MEDIA.SINA, new SnsPostListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
                activity.finish();
            }
        });
    }

    public static void renrenShare(final Activity activity, String imagePath) {
        UMSocialService renrenShare = UMServiceFactory.getUMSocialService("cn.zmdx.kaka.locker");
        RenrenShareContent renren = new RenrenShareContent();
        renren.setShareContent(PandoraShareManager.ShareContent);
        renren.setTargetUrl(PandoraShareManager.TargetUrl);
        renren.setTitle(PandoraShareManager.Title);
        renren.setShareImage(new UMImage(activity, imagePath));
        renrenShare.setShareMedia(renren);
        renrenShare.getConfig().setSsoHandler(
                new RenrenSsoHandler(activity, "272417",
                        "f56d084e27f14efda76788f31045a542", "27e373b49cad4fd6b4f78bdae9129758"));
        renrenShare.postShare(activity, SHARE_MEDIA.RENREN, new SnsPostListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
                activity.finish();
            }
        });
    }

    public static void qzoneShare(final Activity activity, String imagePath) {
        UMSocialService qzoneShare = UMServiceFactory.getUMSocialService("cn.zmdx.kaka.locker");
        QZoneShareContent qzone = new QZoneShareContent();
        // 设置分享文字
        qzone.setShareContent(PandoraShareManager.ShareContent);
        // 设置点击消息的跳转URL
        qzone.setTargetUrl(PandoraShareManager.TargetUrl);
        // 设置分享内容的标题
        qzone.setTitle(PandoraShareManager.Title);
        // 设置分享图片
        qzone.setShareImage(new UMImage(activity, imagePath));
        qzoneShare.setShareMedia(qzone);
        qzoneShare.getConfig().setSsoHandler(
                new QZoneSsoHandler(activity, "1103193086", "XOgkKrK9tZOcawOF"));
        qzoneShare.postShare(activity, SHARE_MEDIA.QZONE, new SnsPostListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
                activity.finish();
            }
        });
    }

    public static void weixinCircleShare(final Activity activity, String imagePath) {
        UMSocialService weixinCircleShare = UMServiceFactory
                .getUMSocialService("cn.zmdx.kaka.locker");
        String appID = "wx5fa094ca2b1994ba";
        String appSecret = "5f6abd06e3804079eb95ce0de0464161";
        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(activity, appID, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(PandoraShareManager.ShareContent);
        // 设置朋友圈title
        circleMedia.setTitle(PandoraShareManager.Title);
        circleMedia.setTargetUrl(PandoraShareManager.TargetUrl);
        circleMedia.setShareImage(new UMImage(activity, imagePath));
        weixinCircleShare.setShareMedia(circleMedia);
        weixinCircleShare.postShare(activity, SHARE_MEDIA.WEIXIN_CIRCLE,
                new SnsPostListener() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {

                    }
                });
    }

    public static void weixinShare(final Activity activity, String imagePath) {
        UMSocialService weixinShare = UMServiceFactory.getUMSocialService("cn.zmdx.kaka.locker");
        // 微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appID = "wx5fa094ca2b1994ba";
        String appSecret = "5f6abd06e3804079eb95ce0de0464161";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(activity, appID, appSecret);
        wxHandler.addToSocialSDK();

        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        // 设置分享文字
        weixinContent.setShareContent(PandoraShareManager.ShareContent);
        // 设置title
        weixinContent.setTitle(PandoraShareManager.Title);
        // 设置分享内容跳转URL
        weixinContent.setTargetUrl(PandoraShareManager.TargetUrl);
        // 设置分享图片
        weixinContent.setShareImage(new UMImage(activity, imagePath));
        weixinShare.setShareMedia(weixinContent);

        weixinShare.postShare(activity, SHARE_MEDIA.WEIXIN, new SnsPostListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
            }
        });
    }
}
