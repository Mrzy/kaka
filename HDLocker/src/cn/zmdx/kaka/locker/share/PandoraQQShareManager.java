
package cn.zmdx.kaka.locker.share;

import android.content.Context;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qzone.QZone;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;

public class PandoraQQShareManager {

    private static PandoraQQShareManager mInstance;

    public static PandoraQQShareManager getInstance() {
        if (null == mInstance) {
            mInstance = new PandoraQQShareManager();
        }
        return mInstance;
    }

    public void shareToQzone(final Context context, ServerImageData date,
            PlatformActionListener mPlatformActionListener) {
        ShareParams sp = new ShareParams();
        sp.setTitle(date.getTitle());
        sp.setTitleUrl(date.getImageDesc()); // 标题的超链接
        sp.setText("↓精彩继续↓");
        sp.setImageUrl(date.getUrl());
        sp.setSite("潘多拉锁屏");
        sp.setSiteUrl("http://www.hdlocker.com");

        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
        qzone.setPlatformActionListener(mPlatformActionListener);
        qzone.share(sp);
    }
}
