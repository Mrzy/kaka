
package cn.zmdx.kaka.locker.share;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import cn.zmdx.kaka.locker.share.PandoraShareManager.PandoraShareData;

import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

public class PandoraQQShareManager {

    private static final String QQ_APPID = "1103193086";

    private static PandoraQQShareManager mInstance;

    public static PandoraQQShareManager getInstance() {
        if (null == mInstance) {
            mInstance = new PandoraQQShareManager();
        }
        return mInstance;
    }

    public void shareToQzone(Activity activity, PandoraShareData shareData, IUiListener listener) {
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareData.mTitle);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareData.mDesc);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareData.mWebUrl);
        ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add(shareData.mImageUrl);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        Tencent mTencent = Tencent.createInstance(QQ_APPID, activity);
        mTencent.shareToQzone(activity, params, listener);
    }
}
