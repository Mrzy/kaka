
package cn.zmdx.kaka.locker.wallpaper;

import java.io.File;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;

public class WallpaperUtils {

    private static final String DESKTOP_WALLPAPER_FILE_PATH = Environment
            .getExternalStorageDirectory().getPath() + "/.Pandora/wallpaper/desktop/";

    private static final String DESKTOP_WALLPAPER_FILE_NAME = "desktop.jpg";

    public interface ILoadBitmapCallback {
        void imageLoaded(Bitmap bitmap, String filePath);
    }

    public static void loadBitmap(final Context context, final String filePath,
            final ILoadBitmapCallback callback) {
        HDBThreadUtils.runOnWorker(new Runnable() {

            @Override
            public void run() {
                int thumbWidth = (int) context.getResources().getDimension(
                        R.dimen.pandora_wallpaper_width);
                int thumbHeight = (int) context.getResources().getDimension(
                        R.dimen.pandora_wallpaper_height);
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
     * 
     * @return 返回裁剪后并保存到磁盘上的文件完整路径
     */
    public static String initDefaultWallpaper() {
        String path = getDefaultWallpaperPath();
        if (TextUtils.isEmpty(path)) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(HDApplication
                    .getContext());
            Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
            int screenWidth = BaseInfoHelper.getRealWidth(HDApplication.getContext());
            int screenHeight = BaseInfoHelper.getRealHeight(HDApplication.getContext());
            int X = (bm.getWidth() - screenWidth) / 2;
            Bitmap aa = Bitmap.createBitmap(bm, X, 0, screenWidth, screenHeight);
            mkDirs();
            ImageUtils.saveImageToFile(aa, DESKTOP_WALLPAPER_FILE_PATH
                    + DESKTOP_WALLPAPER_FILE_NAME);
        }
        return path;
    }

    private static void mkDirs() {
        File dir = new File(DESKTOP_WALLPAPER_FILE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 获得默认壁纸的文件路径
     * 
     * @return 如果文件不存在，返回null,否则返回文件完整路径
     */
    public static String getDefaultWallpaperPath() {
        File file = new File(DESKTOP_WALLPAPER_FILE_PATH + DESKTOP_WALLPAPER_FILE_NAME);
        if (file.exists()) {
            return DESKTOP_WALLPAPER_FILE_PATH + DESKTOP_WALLPAPER_FILE_NAME;
        }
        return null;
    }

    public static Bitmap getDefaultWallpaperBitmap() {
        final String path = getDefaultWallpaperPath();
        if (!TextUtils.isEmpty(path)) {
            return BitmapFactory.decodeFile(path, null);
        }
        return null;
    }
}
