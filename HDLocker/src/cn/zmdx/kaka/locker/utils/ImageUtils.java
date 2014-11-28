
package cn.zmdx.kaka.locker.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    /**
     * null may be returned if the image file not found
     */
    public static Bitmap getBitmapFromFile(String filepath, BitmapFactory.Options opts) {
        try {
            return BitmapFactory.decodeFile(filepath, opts);
        } catch (OutOfMemoryError e) {
        }
        return null;
    }

    public static Bitmap getBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = computeSampleSize(filePath, reqWidth, reqHeight);
        return getBitmapFromFile(filePath, opts);
    }
    
    /**
     * Create a bitmap object from a drawable object.
     * 
     * @return null may be returned if the drawable object has no intrinsic
     *         width/height.
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        return drawable2Bitmap(drawable, false);
    }

    public static Bitmap drawable2Bitmap(Drawable drawable, boolean directReturn) {
        if (drawable == null) {
            return null;
        }
        ;

        /**
         * In that case, we cannot release the returned Bitmap anymore.
         */
        if (directReturn && drawable instanceof BitmapDrawable) {
            Bitmap tmp = ((BitmapDrawable) drawable).getBitmap();
            if (tmp != null) {
                return tmp;
            }
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width <= 0 || height <= 0) {
            // No intrinsic width/height, such as a solid color
            return null;
        }

        Drawable clone = drawable.getConstantState().newDrawable();
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                clone.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        clone.setBounds(0, 0, width, height);
        clone.draw(canvas);
        return bitmap;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int reqWidth) {
        try {
            int widRate = Math.round((float) options.outWidth / (float) reqWidth);
            // int heightRate = Math.round((float) options.outHeight / (float)
            // reqHeight);
            return widRate;
        } catch (Exception e) {
            return 1;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth <= 0 || reqHeight <= 0) {
            return 1;
        }
        try {
            int widRate = Math.round((float) options.outWidth / (float) reqWidth);
            int heightRate = Math.round((float) options.outHeight / (float) reqHeight);
            return Math.min(widRate, heightRate);
        } catch (Exception e) {
            return 1;
        }
    }

    public static int computeSampleSize(String bmpFile, int reqWidth, int reqHeight) {
        if (reqWidth <= 0 || reqHeight <= 0) {
            return 1;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(bmpFile, options);
            int widRate = Math.round((float) options.outWidth / (float) reqWidth);
            int heightRate = Math.round((float) options.outHeight / (float) reqHeight);
            return Math.min(widRate, heightRate);
        } catch (Exception e) {
        }
        return 1;
    }

    /**
     * Create a BitmapDrawable object from the specified Bitmap object.
     */
    public static BitmapDrawable bitmap2Drawable(Context cxt, Bitmap bmp) {
        if (bmp == null) {
            return null;
        }
        return new BitmapDrawable(cxt.getResources(), bmp);
    }

    public static byte[] bitmap2Bytes(Bitmap bmp) {
        if (bmp == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    /**
     * @return null may be returned
     */
    public static Bitmap bytes2Bitmap(byte[] bmpBytes) {
        if (bmpBytes == null || bmpBytes.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bmpBytes, 0, bmpBytes.length);
    }

    /**
     * @deprecated Use
     *             {@link #scaleTo(Bitmap src, int newWidth, int newHeight, boolean recycle)}
     *             instead
     */
    public static Bitmap scaleTo(Bitmap src, int newWidth, int newHeight) {
        return scaleTo(src, newWidth, newHeight, false);
    }

    /**
     * Create a new bitmap by scaling the source bitmap to new width and height.
     */
    public static Bitmap scaleTo(Bitmap src, int newWidth, int newHeight, boolean recycle) {
        if (src == null) {
            return null;
        }

        int width = src.getWidth();
        int height = src.getHeight();
        if (width == newHeight && height == newHeight) {
            return src;
        }
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap target = Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);

        if (recycle && src != target) {
            src.recycle();
            src = null;
        }

        return target;
    }

    /**
     * 根据resize图片后的宽和高，在保证宽高比的情况下，缩放或放大到最合适的尺寸
     * 
     * @param srcBmp
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     * @return
     */
    public static Bitmap getResizedBitmap(Bitmap srcBmp, int targetWidth, int targetHeight) {
        if (srcBmp == null || targetWidth <= 0 || targetHeight <= 0) {
            return null;
        }

        try {
            float hRate = (float) targetHeight / (float) srcBmp.getHeight();
            float wRate = (float) targetWidth / (float) srcBmp.getWidth();
            float rate = Math.max(hRate, wRate);
            Matrix matrix = new Matrix();
            // resize the Bitmap
            matrix.postScale(rate, rate);
            return Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), srcBmp.getHeight(), matrix,
                    true);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * @deprecated Use
     *             {@link #getRoundBitmap(Bitmap src, float roundPx, boolean recycle)}
     *             instead
     */
    public static Bitmap getRoundBitmap(Bitmap src, float roundPx) {
        return getRoundBitmap(src, roundPx, false);
    }

    public static Bitmap getRoundBitmap(Bitmap src, float roundPx, boolean recycle) {
        if (src == null) {
            return null;
        }

        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap target = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(target);

        Rect rect = new Rect(0, 0, width, height);
        RectF rectF = new RectF(rect);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xff424242); // dark grey
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(src, rect, rect, paint);

        if (recycle) {
            src.recycle();
        }

        return target;
    }

    public static Bitmap getReflectionBitmap(Bitmap src, int scaleLevel) {
        if (scaleLevel == 0) {
            return src;
        }

        final int reflectionGap = 0;
        int width = src.getWidth();
        int height = src.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionBmp = Bitmap.createBitmap(src, 0, height - (height / scaleLevel), width,
                height / scaleLevel, matrix, false);

        Bitmap target = Bitmap
                .createBitmap(width, (height + height / scaleLevel), Config.ARGB_8888);
        Canvas canvas = new Canvas(target);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvas.drawBitmap(src, 0, 0, paint);
        canvas.drawRect(0, height, width, height + reflectionGap, paint); // TODO:
                                                                          // why
                                                                          // need
                                                                          // this
                                                                          // rect
        canvas.drawBitmap(reflectionBmp, 0, height + reflectionGap, paint);

        LinearGradient shader = new LinearGradient(0, src.getHeight(), 0, target.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, height, width, target.getHeight() + reflectionGap, paint);

        return target;
    }

    public static Bitmap getReflectionBitmap(Bitmap src) {
        return getReflectionBitmap(src, 2);
    }

    /**
     * Create a new Bitmap object to add a background color.
     * 
     * @param src
     * @param bkgColor
     * @param recycle Indicate if recycle the <b>src</b> Bitmap object
     * @return
     */
    public static Bitmap setBitmapBkg(Bitmap src, int bkgColor, boolean recycle) {
        if (src == null) {
            return null;
        }

        Bitmap target = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Canvas canvas = new Canvas(target);
        canvas.drawColor(bkgColor);
        canvas.drawBitmap(src, 0, 0, paint);

        if (recycle) {
            src.recycle();
        }

        return target;
    }

    /**
     * Scale the source Bitmap
     * 
     * @param bmp the source Bitmap
     * @param scale scale factor
     * @return result Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap bmp, float scale) {
        Bitmap ret = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale),
                (int) (bmp.getHeight() * scale), true);
        return ret;
    }

    public static boolean saveImageToFile(Bitmap image, String filepath) {
        try {
            FileOutputStream fos = new FileOutputStream(filepath);
            return image.compress(CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            HDBLOG.logE("Failed to save image file: " + e);
        }
        return false;
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     * 
     * @param actualWidth Actual width of the bitmap
     * @param actualHeight Actual height of the bitmap
     * @param desiredWidth Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    // Visible for testing.
    public static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth,
            int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }
}
