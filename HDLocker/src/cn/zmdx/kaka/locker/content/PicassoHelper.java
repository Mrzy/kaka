
package cn.zmdx.kaka.locker.content;

import android.content.Context;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

import com.squareup.picasso.Cache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

public class PicassoHelper {

    private static Cache sCache;

    private static Picasso sPicasso;

    static {
        int maxSize = BaseInfoHelper.calculateMemoryCacheSize(HDApplication.getContext());
        sCache = null;
        sCache = new LruCache(maxSize);
    }

    public static Picasso getPicasso(Context context) {
        if (sPicasso == null) {
            synchronized (PicassoHelper.class) {
                if (sPicasso == null) {
                    Picasso.Builder builder = new Picasso.Builder(context);
                    builder.memoryCache(sCache);
                    sPicasso = builder.build();
                    sPicasso.setLoggingEnabled(BuildConfig.DEBUG);
                    sPicasso.setIndicatorsEnabled(BuildConfig.DEBUG);
                }
            }
        }

        return sPicasso;
    }

    public static Cache getPicassoCache() {
        return sCache;
    }

    public static void clearMemoryCache() {
        if (sCache != null) {
            sCache.clear();
        }
    }

    public static void shutdown() {
        if (sPicasso != null) {
            sPicasso.shutdown();
            sPicasso = null;
        }
    }
}
