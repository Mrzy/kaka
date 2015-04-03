
package cn.zmdx.kaka.locker.content;

import android.content.Context;

import cn.zmdx.kaka.locker.BuildConfig;

import com.squareup.picasso.Picasso;

public class PicassoHelper {

    private static Picasso sPicasso;

    public static Picasso getPicasso(Context context) {
        if (sPicasso == null) {
            sPicasso = Picasso.with(context);
            sPicasso.setLoggingEnabled(BuildConfig.DEBUG);
            sPicasso.setIndicatorsEnabled(BuildConfig.DEBUG);
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
