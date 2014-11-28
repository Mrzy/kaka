
package cn.zmdx.kaka.locker;

import android.content.Context;
import android.widget.Toast;
import cn.zmdx.kaka.locker.cache.DiskImageCache;

import com.android.volley.toolbox.ImageLoader;

public class ImageLoaderManager {

    private static ImageLoader sImageLoader;

    public static DiskImageCache sOnlineImageCache;

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
}
