
package cn.zmdx.kaka.locker;

import android.content.Context;
import android.widget.Toast;
import cn.zmdx.kaka.locker.cache.DiskImageCache;

import com.android.volley.cache.BitmapCache;
import com.android.volley.toolbox.ImageLoader;

public class ImageLoaderManager {

    private static ImageLoader sImageLoader;

    public static DiskImageCache sOnlineImageCache;

    public static BitmapCache sBmpCache = null;

    public static void init(Context context) {
        try {
            DiskImageCache cache = new DiskImageCache(context, "onlineCache", 1024 * 30 * 1024);
            sImageLoader = new ImageLoader(RequestManager.getRequestQueue(), cache);
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.sdcard_error),
                    Toast.LENGTH_LONG).show();
        }
    }

    public static ImageLoader getImageLoader() {
        if (sImageLoader != null) {
            return sImageLoader;
        } else {
            throw new IllegalStateException("Not initialized");
        }
    }

    public static DiskImageCache getOnlineImageCache(Context context) {
        if (null == sOnlineImageCache) {
            sOnlineImageCache = new DiskImageCache(context, "onlineImageCache", 1024 * 50 * 1024);
        }
        return sOnlineImageCache;
    }

    public static BitmapCache getImageMemCache() {
        if (sBmpCache == null) {
            // 使用可用RAM的15%作为图片的内存缓存
            sBmpCache = BitmapCache.getInstance(null, 0.15f);
        }
        return sBmpCache;
    }

}
