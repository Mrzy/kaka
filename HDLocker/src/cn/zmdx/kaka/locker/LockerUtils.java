
package cn.zmdx.kaka.locker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.ImageUtils;

public class LockerUtils {

    private static Context sContext;
    static {
        sContext = HDApplication.getContext();
    }

    static Drawable renderScreenLockerBackground(View view, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            if (BuildConfig.DEBUG) {
                throw new NullPointerException("fileName must not be null");
            }
            HDBLOG.logE("fileName must not be null when getScreenLockerBackground");
        }

        //获得手机屏幕的物理宽高，包括通知栏和虚拟按键栏（如果存在的话）
        int screenWidth = BaseInfoHelper.getRealWidth(sContext);
        int screenHeight = BaseInfoHelper.getRealHeight(sContext);

        Bitmap bitmap = ImageUtils.getBitmapFromFile(fileName, screenWidth, screenHeight);
        if (bitmap == null) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logE("根据图片文件名获得壁纸时，可能出现OOM异常，导致壁纸没有正常返回");
            }
            return null;
        }
        return renderScreenLockerBackground(view, bitmap);
    }

    static Drawable renderScreenLockerBackground(View view, Drawable resDrawable) {
        final Bitmap bmp = ImageUtils.drawable2Bitmap(resDrawable);
        return renderScreenLockerBackground(view, bmp);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    static Drawable renderScreenLockerBackground(View view, Bitmap resBmp) {
        int width = BaseInfoHelper.getRealWidth(sContext);
        int realHeight = BaseInfoHelper.getRealHeight(sContext);
        Bitmap desBmp = ImageUtils.getResizedBitmap(resBmp, width, realHeight);
        if (desBmp == null) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logE("occured exception when getResizedBitmap, so return null");
            }
            return null;
        }
        int x = Math.max(0, (desBmp.getWidth() - width) / 2);
        Bitmap finalBmp = Bitmap.createBitmap(desBmp, x, 0, width, realHeight);
        if (!finalBmp.equals(desBmp)) {
            desBmp.recycle();
        }

        Drawable finalDrawable = ImageUtils.bitmap2Drawable(sContext, finalBmp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(finalDrawable);
        } else {
            view.setBackgroundDrawable(finalDrawable);
        }
        return finalDrawable;
    }
}
