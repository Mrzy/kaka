
package cn.zmdx.kaka.locker.share;

import android.content.Context;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.zmdx.kaka.locker.share.PandoraShareManager.PandoraShareData;

public class PandoraSinaShareManager {

    private static PandoraSinaShareManager mInstance;

    public static PandoraSinaShareManager getInstance() {
        if (null == mInstance) {
            mInstance = new PandoraSinaShareManager();
        }
        return mInstance;
    }

    public void shareToSina(final Context context, final PandoraShareData shareData,
            PlatformActionListener mPlatformActionListener) {
        ShareParams sina = new ShareParams();
        sina.setText(getSinaShareContent(shareData));
        sina.setImageUrl(shareData.mImageUrl);
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(mPlatformActionListener);
        weibo.share(sina);
    }

    private String getSinaShareContent(PandoraShareData shareData) {
        return "【" + shareData.mTitle + "】 " + shareData.mWebUrl + " " + "(分享自@潘多拉锁屏)";
    }

}
