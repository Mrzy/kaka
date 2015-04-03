
package cn.zmdx.kaka.locker;

import android.content.Context;
import cn.zmdx.kaka.locker.cache.DiskImageCache;

import com.android.volley.cache.BitmapCache;

public class ImageLoaderManager {

    public static DiskImageCache sOnlineImageCache;

    public static BitmapCache sBmpCache = null;

    public static DiskImageCache getOnlineImageCache(Context context) {
        if (null == sOnlineImageCache) {
            sOnlineImageCache = new DiskImageCache(context, "onlineImageCache", 1024 * 50 * 1024);
        }
        return sOnlineImageCache;
    }

//    public static BitmapCache getImageMemCache() {
//        if (sBmpCache == null) {
//            // 使用可用RAM的15%作为图片的内存缓存
//            sBmpCache = BitmapCache.getInstance(null, 0.10f);
//        }
//        return sBmpCache;
//    }
}
