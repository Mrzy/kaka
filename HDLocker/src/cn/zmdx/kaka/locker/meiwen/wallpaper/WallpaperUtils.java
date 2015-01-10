
package cn.zmdx.kaka.locker.meiwen.wallpaper;

import java.io.File;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.meiwen.HDApplication;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.meiwen.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.meiwen.utils.ImageUtils;

public class WallpaperUtils {

    private static final String DESKTOP_WALLPAPER_FILE_NAME = "/desktop";

    public interface ILoadBitmapCallback {
        void imageLoaded(Bitmap bitmap, String filePath);
    }

    public static void loadBitmap(final Context context, final String filePath,
            final ILoadBitmapCallback callback) {
        HDBThreadUtils.runOnWorker(new Runnable() {

            @Override
            public void run() {
                int thumbWidth = (int) context.getResources().getDimension(
                        Res.dimen.pandora_wallpaper_width);
                int thumbHeight = (int) context.getResources().getDimension(
                        Res.dimen.pandora_wallpaper_height);
                final Bitmap bitmap = ImageUtils.getBitmapFromFile(filePath, thumbWidth,
                        thumbHeight);
                HDBThreadUtils.runOnUi(new Runnable() {

                    @Override
                    public void run() {
                        if (null != bitmap) {
                            callback.imageLoaded(bitmap, filePath);
                        }
                    }
                });
            }
        });

    }

    public static void loadBackgroundBitmap(final Context context, final String filePath,
            final ILoadBitmapCallback callback) {
        HDBThreadUtils.runOnWorker(new Runnable() {

            @Override
            public void run() {
                int width = BaseInfoHelper.getRealWidth(context);
                int realHeight = BaseInfoHelper.getRealHeight(context);
                Bitmap bitamp = ImageUtils.getBitmapFromFile(filePath, width, realHeight);
                if (null != bitamp) {
                    bitamp = ImageUtils.getResizedBitmap(bitamp, width, realHeight);
                    int x = Math.max(0, (bitamp.getWidth() - width));
                    final Bitmap finalBitmap = Bitmap.createBitmap(bitamp, x, 0, width, realHeight);
                    HDBThreadUtils.runOnUi(new Runnable() {

                        @Override
                        public void run() {
                            if (null != finalBitmap) {
                                callback.imageLoaded(finalBitmap, filePath);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 1. 读取系统桌面壁纸，处理为适合锁屏显示的图片 2. 存储到手机内部存储的指定位置下
     */
    public static void initDefaultWallpaper() {
        String path = getDefaultWallpaperPath();
        if (!TextUtils.isEmpty(path)) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(HDApplication
                    .getContext());
            Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            Bitmap bitmap = ImageUtils.drawable2Bitmap(wallpaperDrawable);
            int screenWidth = BaseInfoHelper.getRealWidth(HDApplication.getContext());
            int screenHeight = BaseInfoHelper.getRealHeight(HDApplication.getContext());
            Bitmap resizeBitmap = null;
            resizeBitmap = ImageUtils.getResizedBitmap(bitmap, screenWidth, screenHeight);

            int x = 0;
            if (resizeBitmap.getWidth() > screenWidth) {
                x = ((resizeBitmap.getWidth() - screenWidth) / 2);
            }
            if (bitmap != null) {
                bitmap = null;
            }
            try {
                Bitmap finalBitmap = null;
                finalBitmap = Bitmap.createBitmap(resizeBitmap, x, 0, screenWidth, screenHeight);
                if (resizeBitmap != null) {
                    resizeBitmap = null;
                }
                ImageUtils.saveImageToFile(finalBitmap, path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得默认壁纸的文件路径
     * 
     * @return 如果文件不存在，返回null,否则返回文件完整路径
     */
    private static String getDefaultWallpaperPath() {
        File file = HDApplication.getContext().getCacheDir();
        File cacheDir = new File(file.getPath() + File.separator + DESKTOP_WALLPAPER_FILE_NAME);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir.getAbsolutePath() + DESKTOP_WALLPAPER_FILE_NAME;
    }

    public static Bitmap getDefaultWallpaperBitmap() {
        final String path = getDefaultWallpaperPath();
        if (!TextUtils.isEmpty(path)) {
            return BitmapFactory.decodeFile(path, null);
        }
        return null;
    }

}
