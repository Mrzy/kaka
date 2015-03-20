
package cn.zmdx.kaka.locker.share;

import android.content.Context;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qzone.QZone;
import cn.zmdx.kaka.locker.share.PandoraShareManager.PandoraShareData;

public class PandoraQQShareManager {

    private static PandoraQQShareManager mInstance;

    public static PandoraQQShareManager getInstance() {
        if (null == mInstance) {
            mInstance = new PandoraQQShareManager();
        }
        return mInstance;
    }

    public void shareToQzone(final Context context, PandoraShareData shareData,
            PlatformActionListener mPlatformActionListener) {
        ShareParams sp = new ShareParams();
        sp.setTitle(shareData.mTitle);
        sp.setTitleUrl(shareData.mWebUrl); // 标题的超链接
        sp.setText(shareData.mDesc);
        sp.setImageUrl(shareData.mImageUrl);
        sp.setSite("潘多拉锁屏");
        sp.setSiteUrl("http://www.hdlocker.com");

        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
        qzone.setPlatformActionListener(mPlatformActionListener);
        qzone.share(sp);
    }
}
