
package cn.zmdx.kaka.locker.share;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import cn.zmdx.kaka.locker.content.DiskImageHelper;
import cn.zmdx.kaka.locker.share.PandoraShareManager.PandoraShareData;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.ImageUtils;

import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

public class PandoraSinaShareManager {

    public static final String SINA_APPID = "1331388942";

    public static final String REDIRECT_URL = "https://www.hdlocker.com";

    private static final String HTTPMETHOD_POST = "POST";

    private static final String SINA_UPLOAD_URL = "https://api.weibo.com/2/statuses/upload.json";

    private static final String KEY_ACCESS_TOKEN = "access_token";

    private static PandoraSinaShareManager mInstance;

    public static PandoraSinaShareManager getInstance() {
        if (null == mInstance) {
            mInstance = new PandoraSinaShareManager();
        }
        return mInstance;
    }

    public void shareToSina(final Context context, final PandoraShareData shareData,
            final String comment, final RequestListener listener) {
        Bitmap bmp = getBitmap(context, shareData.mImageUrl);
        String accessToken = SinaAccessTokenKeeper.getToken(context);
        sendWeibo(context, accessToken, getSinaShareContent(comment, shareData), bmp, listener);
    }

    private void sendWeibo(Context context, String token, String content, Bitmap bitmap,
            RequestListener listener) {
        WeiboParameters params = new WeiboParameters(SINA_APPID);
        params.put("status", content);
        params.put("pic", bitmap);
        requestAsync(context, token, SINA_UPLOAD_URL, params, HTTPMETHOD_POST, listener);
    }

    private String getSinaShareContent(String comment, PandoraShareData shareData) {
        return comment + "// 【" + shareData.mTitle + "】 " + shareData.mWebUrl + " " + "(分享自@潘多拉锁屏)";
    }

    private void requestAsync(Context context, String token, String url, WeiboParameters params,
            String httpMethod, RequestListener listener) {
        params.put(KEY_ACCESS_TOKEN, token);
        new AsyncWeiboRunner(context).requestAsync(url, params, httpMethod, listener);
    }

    private Bitmap getBitmap(Context context, String imageUrl) {
        File file = DiskImageHelper.getFileByUrl(imageUrl);
        Options option = new Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), option);
        BitmapFactory.Options realOpts = new Options();
        int screenHeight = BaseInfoHelper.getRealHeight(context);
        int screenWidth = BaseInfoHelper.getRealWidth(context);
        realOpts.inSampleSize = ImageUtils.computeSampleSize(option, screenHeight, screenWidth);
        realOpts.inJustDecodeBounds = false;
        realOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        realOpts.inPurgeable = true;
        realOpts.inInputShareable = true;
        return DiskImageHelper.getBitmapByUrl(imageUrl, realOpts);
    }

    public void sinaAuth(SsoHandler ssoHandler, WeiboAuthListener listener) {
        ssoHandler.authorizeClientSso(listener);
    }

}
