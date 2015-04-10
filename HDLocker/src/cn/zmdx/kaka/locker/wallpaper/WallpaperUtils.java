
package cn.zmdx.kaka.locker.wallpaper;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseIntArray;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.network.ByteArrayRequest;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBHashUtils;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperManager.IPullWallpaperListener;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;

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
                SparseIntArray sparseIntArray = WallpaperUtils.initWallpaperSize(context);
                int imageWidth = sparseIntArray.get(WallpaperUtils.KEY_IMAGE_WIDTH);
                int imageHeight = sparseIntArray.get(WallpaperUtils.KEY_IMAGE_HEIGHT);
                final Bitmap bitmap = ImageUtils.getBitmapFromFile(filePath, imageWidth,
                        imageHeight);
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

    private static SparseIntArray mCacheSparseArray = null;

    public static int KEY_LAYOUT_WIDTH = 0;

    public static int KEY_LAYOUT_HEIGHT = 1;

    public static int KEY_IMAGE_WIDTH = 2;

    public static int KEY_IMAGE_HEIGHT = 3;

    public static SparseIntArray initWallpaperSize(Context context) {
        if (null == mCacheSparseArray || mCacheSparseArray.size() == 0) {
            int screenWidth = BaseInfoHelper.getRealWidth(context);
            int screenHeight = BaseInfoHelper.getRealHeight(context);
            int layoutWidth = (int) (screenWidth / 3);
            int imageWidth = layoutWidth - BaseInfoHelper.dip2px(context, 32);
            int imageHeight = (imageWidth * screenHeight) / screenWidth;
            int layoutHeight = imageHeight + (layoutWidth - imageWidth);
            mCacheSparseArray = new SparseIntArray();
            mCacheSparseArray.put(KEY_LAYOUT_WIDTH, layoutWidth);
            mCacheSparseArray.put(KEY_LAYOUT_HEIGHT, layoutHeight);
            mCacheSparseArray.put(KEY_IMAGE_WIDTH, imageWidth);
            mCacheSparseArray.put(KEY_IMAGE_HEIGHT, imageHeight);
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("screenWidth=" + screenWidth + " padding=" + " layoutWidth="
                        + layoutWidth + " imageWidth=" + imageWidth + " layoutHeight="
                        + layoutHeight + " imageHeight=" + imageHeight);
            }
        }
        return mCacheSparseArray;
    }

    public interface IDownLoadWallpaper {
        void onSuccess(Bitmap bitmap);

        void onFail();
    }

    public static void downloadWallpaper(final Context context, String imageUrl,
            final IDownLoadWallpaper listener) {
        ByteArrayRequest mRequest = new ByteArrayRequest(imageUrl, new Listener<byte[]>() {

            @Override
            public void onResponse(byte[] data) {
                Bitmap bitmap = doParse(data, BaseInfoHelper.getRealWidth(context),
                        BaseInfoHelper.getRealHeight(context));
                listener.onSuccess(bitmap);
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFail();
            }
        });
        mRequest.setShouldCache(false);
        RequestManager.getRequestQueue().add(mRequest);
    }

    private static Bitmap doParse(byte[] data, int mMaxWidth, int mMaxHeight) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inInputShareable = true;
        decodeOptions.inPurgeable = true;
        decodeOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        return bitmap;
    }

    public static void autoChangeWallpaper() {
        HDBThreadUtils.runOnWorker(new Runnable() {

            @Override
            public void run() {
                handleToAuto();
            }
        });
    }

    private static void handleToAuto() {
        final Context context = HDApplication.getContext();
        if (!PandoraConfig.newInstance(context).isAutoChangeOn()
                || !isShouldDownloadWallpaper(context)) {
            return;
        }
        String saveDate = PandoraConfig.newInstance(context).getAutoChangeWallapperDate();
        final String curDate = BaseInfoHelper.getCurrentDate();
        if (!curDate.equals(saveDate)) {
            OnlineWallpaperManager.getInstance().pullWallpaperData(context,
                    new IPullWallpaperListener() {

                        @Override
                        public void onSuccecc(List<ServerOnlineWallpaper> list) {
                            Collections.sort(list, WallpaperUtils.comparator);
                            final ServerOnlineWallpaper item = list.get(0);
                            if (isNeedToChangeWallpaper(curDate, item.getPublishDATE())) {
                                downloadAutoWallpaper(context, item.getImageURL(), curDate,
                                        item.getDesc());
                            }
                        }

                        @Override
                        public void onFail() {

                        }
                    }, 1, System.currentTimeMillis());
        }

    }

    private static boolean isNeedToChangeWallpaper(String curDate, long date) {
        int day = getDayByTime(date, Calendar.DAY_OF_MONTH);
        int month = getDayByTime(date, Calendar.MONTH) + 1;
        int year = getDayByTime(date, Calendar.YEAR);
        String newWallpaperDate = "" + year + month + day;
        return newWallpaperDate.equals(curDate);
    }

    private static void downloadAutoWallpaper(Context context, final String imageUrl,
            final String curDate, final String desc) {
        downloadWallpaper(context, imageUrl, new IDownLoadWallpaper() {

            @Override
            public void onSuccess(final Bitmap bitmap) {
                changeWallpaper(bitmap, imageUrl, curDate, desc);
            }

            @Override
            public void onFail() {

            }
        });
    }

    private static void changeWallpaper(Bitmap bitmap, String imageUrl, String curDate, String desc) {
        OnlineWallpaperManager.getInstance().mkDirs();
        final String md5ImageUrl = HDBHashUtils.getStringMD5(imageUrl);
        ImageUtils.saveImageToFile(bitmap,
                OnlineWallpaperManager.getInstance().getFilePath(md5ImageUrl));
        OnlineWallpaperManager.getInstance().saveThemeId(HDApplication.getContext(),
                ThemeManager.THEME_ID_ONLINE);
        ThemeManager.addBitmapToCache(bitmap);
        OnlineWallpaperManager.getInstance().saveCurrentWallpaperFileName(
                HDApplication.getContext(), md5ImageUrl);
        PandoraConfig.newInstance(HDApplication.getContext()).saveOnlineWallPaperDesc(md5ImageUrl,
                desc);
        PandoraConfig.newInstance(HDApplication.getContext()).saveAutoChangeWallapperDate(curDate);
    }

    private static int getDayByTime(long time, int field) {
        Calendar cal = Calendar.getInstance();
        if (time > 0) {
            cal.setTimeInMillis(time);
        }
        return cal.get(field);
    }

    public static Comparator<ServerOnlineWallpaper> comparator = new Comparator<ServerOnlineWallpaper>() {

        @Override
        public int compare(ServerOnlineWallpaper lhs, ServerOnlineWallpaper rhs) {
            return (lhs.getPublishDATE() - rhs.getPublishDATE()) > 0 ? -1 : 1;
        }
    };

    public static boolean isShouldDownloadWallpaper(Context context) {
        return HDBNetworkState.isWifiNetwork()
                || ((HDBNetworkState.isNetworkAvailable() && !HDBNetworkState.isWifiNetwork()) && !PandoraConfig
                        .newInstance(context).isOnlyWifiLoadImage());
    }
}
