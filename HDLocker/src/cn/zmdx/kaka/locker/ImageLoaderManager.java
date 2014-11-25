
package cn.zmdx.kaka.locker;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

import com.android.volley.cache.DiskLruImageCache;
import com.android.volley.toolbox.ImageLoader;

public class ImageLoaderManager {

    private static ImageLoader mImageLoader;

    public static void init(Context mContext) {
        DiskLruImageCache mCache = new DiskLruImageCache(mContext, "onlineCache",
                1024 * 100 * 1024, CompressFormat.JPEG, 80);
        mImageLoader = new ImageLoader(RequestManager.getRequestQueue(), mCache);
    }

    public static ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("Not initialized");
        }
    }
}
