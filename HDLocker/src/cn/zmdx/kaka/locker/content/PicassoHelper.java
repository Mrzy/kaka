
package cn.zmdx.kaka.locker.content;

import android.content.Context;

import cn.zmdx.kaka.locker.BuildConfig;

import com.squareup.picasso.Cache;
import com.squareup.picasso.Picasso;

public class PicassoHelper {

    private static Picasso sPicasso;

    public static Picasso getPicasso(Context context) {
        if (sPicasso == null) {
            sPicasso = new Picasso.Builder(context).memoryCache(Cache.NONE)
                    .loggingEnabled(BuildConfig.DEBUG).indicatorsEnabled(BuildConfig.DEBUG).build();
        }
        return sPicasso;
    }

    public void shutdown() {
        if (sPicasso != null) {
            sPicasso.shutdown();
            sPicasso = null;
        }
    }
}
