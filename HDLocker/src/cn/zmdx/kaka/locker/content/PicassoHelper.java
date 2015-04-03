
package cn.zmdx.kaka.locker.content;

import android.content.Context;

import cn.zmdx.kaka.locker.BuildConfig;

import com.squareup.picasso.Picasso;

public class PicassoHelper {

    public static Picasso getPicasso(Context context) {
        Picasso picasso = Picasso.with(context);
        picasso.setLoggingEnabled(BuildConfig.DEBUG);
        picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
        return picasso;
    }
}
