
package cn.zmdx.kaka.locker;

import android.content.Context;
import cn.zmdx.kaka.locker.cache.DiskImageCache;

import com.android.volley.toolbox.ImageLoader;

public class ImageLoaderManager {

    private static ImageLoader mImageLoader;

    public static void init(Context context) {
        DiskImageCache cache = new DiskImageCache(context, "onlineCache", 1024 * 30 * 1024);
        mImageLoader = new ImageLoader(RequestManager.getRequestQueue(), cache);
    }

    public static ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("Not initialized");
        }
    }
}
