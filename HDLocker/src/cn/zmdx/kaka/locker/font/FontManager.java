
package cn.zmdx.kaka.locker.font;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

public class FontManager {
    public static Typeface sTypeface = null;

    public static Typeface getChineseTypeface(Context context) {
        AssetManager mgr = context.getResources().getAssets();
        if (null == sTypeface) {
            sTypeface = Typeface.createFromAsset(mgr, "fonts/ltxh_GBK_Mobil.TTF");
        }
        return sTypeface;
    }
}
