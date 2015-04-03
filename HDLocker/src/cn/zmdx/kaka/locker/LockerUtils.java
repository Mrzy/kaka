
package cn.zmdx.kaka.locker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.BlurUtils;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.ImageUtils;

public class LockerUtils {

    private static Context sContext;
    private static Bitmap sBlurBmp;
    static {
        sContext = HDApplication.getContext();
    }

    static Bitmap renderScreenLockerWallpaper(ImageView view, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            if (BuildConfig.DEBUG) {
                throw new NullPointerException("fileName must not be null");
            }
            HDBLOG.logE("fileName must not be null when getScreenLockerBackground");
        }

        // 获得手机屏幕的物理宽高，包括通知栏和虚拟按键栏（如果存在的话）
        int screenWidth = BaseInfoHelper.getRealWidth(sContext);
        int screenHeight = BaseInfoHelper.getRealHeight(sContext);

        Bitmap bitmap = ImageUtils.getBitmapFromFile(fileName, screenWidth, screenHeight);
        if (bitmap == null) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logE("根据图片文件名获得壁纸时，可能出现OOM异常，导致壁纸没有正常返回");
            }
            return null;
        }
        return renderScreenLockerWallpaper(view, bitmap);
    }

    static Bitmap renderScreenLockerWallpaper(ImageView view, Bitmap resBmp) {
        ImageView iv = (ImageView) view;
        iv.setImageBitmap(resBmp);
        return resBmp;
    }

    public static Bitmap getViewBitmap(View v) {
        if (v.getWidth() == 0 || v.getHeight() == 0)
            return null;
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    static void renderScreenLockerBlurEffect(ImageView mBlurImageView, Bitmap bmp) {
        sBlurBmp = BlurUtils.doFastBlur(sContext, bmp, mBlurImageView, 15);
    }

    static void recycleBlurBitmap() {
        if (sBlurBmp != null && !sBlurBmp.isRecycled()) {
            sBlurBmp.recycle();
            sBlurBmp = null;
        }
    }
}
