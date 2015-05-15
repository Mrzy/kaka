
package cn.zmdx.kaka.locker.guide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.view.View;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.utils.BlurUtils;

public class GuideUtil {
    private static Bitmap sBlurBmp;

    @SuppressWarnings("deprecation")
    public static void renderScreenLockerBlurEffect(Context context, View view, Bitmap bmp) {
        if (null != sBlurBmp && !sBlurBmp.isRecycled()) {
            if (view instanceof ImageView) {
                ImageView iv = (ImageView) view;
                iv.setImageBitmap(sBlurBmp);
            } else {
                if (VERSION.SDK_INT >= 16) {
                    view.setBackground(new BitmapDrawable(context.getResources(), sBlurBmp));
                } else {
                    view.setBackgroundDrawable(new BitmapDrawable(sBlurBmp));
                }
            }
            return;
        }
        sBlurBmp = BlurUtils.doFastBlur(context, bmp, view, 30);
    }

    public static void recycleBitmap() {
        if (sBlurBmp != null && !sBlurBmp.isRecycled()) {
            sBlurBmp.recycle();
            sBlurBmp = null;
        }
    }
}
