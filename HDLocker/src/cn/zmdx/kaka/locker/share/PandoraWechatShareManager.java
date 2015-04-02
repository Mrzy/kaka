
package cn.zmdx.kaka.locker.share;

import android.content.Context;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;

public class PandoraWechatShareManager {

    private static PandoraWechatShareManager mInstance;

    public static PandoraWechatShareManager getInstance() {
        if (null == mInstance) {
            mInstance = new PandoraWechatShareManager();
        }
        return mInstance;
    }

    public void shareToWechat(final Context mContext, boolean isToWechatCircle,
            ServerImageData date, PlatformActionListener mPlatformActionListener) {
        ShareParams wechat = new ShareParams();
        wechat.setTitle(date.getTitle());
        wechat.setText("↓精彩继续↓");
        wechat.setImageUrl(date.getUrl());
        wechat.setUrl(date.getImageDesc());
        wechat.setShareType(Platform.SHARE_WEBPAGE);

        Platform weixin = ShareSDK.getPlatform(mContext, isToWechatCircle ? WechatMoments.NAME
                : Wechat.NAME);

        weixin.setPlatformActionListener(mPlatformActionListener);
        weixin.share(wechat);
    }
}
