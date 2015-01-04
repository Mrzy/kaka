
package cn.zmdx.kaka.locker.share;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import cn.zmdx.kaka.locker.content.DiskImageHelper;
import cn.zmdx.kaka.locker.share.PandoraShareManager.PandoraShareData;
import cn.zmdx.kaka.locker.utils.ImageUtils;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class PandoraWechatShareManager {

    private static final String WECHAT_APPID = "wx5fa094ca2b1994ba";

    private static PandoraWechatShareManager mInstance;

    public static PandoraWechatShareManager getInstance() {
        if (null == mInstance) {
            mInstance = new PandoraWechatShareManager();
        }
        return mInstance;
    }

    public void shareToWechat(Context mContext, boolean isToWechatCircle,
            PandoraShareData pandoraShare) {
        IWXAPI mWechatAPI = WXAPIFactory.createWXAPI(mContext, WECHAT_APPID, false);
        mWechatAPI.registerApp(WECHAT_APPID);

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = pandoraShare.mWebUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = pandoraShare.mTitle;
        msg.description = pandoraShare.mDesc;
        msg.thumbData = bmpToByteArray(getBitmap(pandoraShare.mImageUrl), true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = isToWechatCircle ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;

        mWechatAPI.sendReq(req);
    }

    private Bitmap getBitmap(String imageUrl) {
        File file = DiskImageHelper.getFileByUrl(imageUrl);
        Options option = new Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), option);
        BitmapFactory.Options realOpts = new Options();
        realOpts.inSampleSize = ImageUtils.computeSampleSize(option, 80, 120);
        realOpts.inJustDecodeBounds = false;
        realOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        realOpts.inPurgeable = true;
        realOpts.inInputShareable = true;
        return DiskImageHelper.getBitmapByUrl(imageUrl, realOpts);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
