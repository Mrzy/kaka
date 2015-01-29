
package cn.zmdx.kaka.fast.locker.notify.filter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseIntArray;
import cn.zmdx.kaka.fast.locker.BuildConfig;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.fast.locker.utils.HDBLOG;

public class NotifyFilterUtil {
    private NotifyFilterUtil() {

    }

    public static String getChinesePinyinStr(String chineseStr) {
        return getPinyinStr(chineseStr);
    }

    private static String getPinyinStr(String srcString) {
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(srcString);
        if (tokens != null && tokens.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (HanziToPinyin.Token token : tokens) {
                if (HanziToPinyin.Token.PINYIN == token.getType()) {
                    if (!TextUtils.isEmpty(token.getTarget())) {
                        sb.append(token.getTarget());
                    }
                } else {
                    sb.append(token.getSource());
                }
            }
            return sb.toString();
        }
        return srcString;
    }

    private static SparseIntArray mCacheSparseArray = null;

    public static int KEY_LAYOUT_WIDTH = 0;

    public static int KEY_IMAGE_WIDTH = 1;

    public static int KEY_IMAGE_HEIGHT = 2;

    public static int KEY_GRIDVIEW_HEIGHT = 3;

    public static int KEY_GRIDVIEW_PADDING = 4;

    public static int KEY_HEAD_PADDING_LEFT = 5;

    // 140x140
    public static SparseIntArray initAppSize(Context context) {
        if (null == mCacheSparseArray || mCacheSparseArray.size() == 0) {
            int screenWidth = BaseInfoHelper.getRealWidth(context);
            int padding = Math.round(Math.round((screenWidth * 0.05)));
            int screenHeight = BaseInfoHelper.getRealHeight(context);
            int itemLayoutWidth = Math.round(((screenWidth - padding * 2) / 4));
            int itemImageWidth = Math.round(Math.round((screenWidth * 0.15)));
            int itemImageHeight = itemImageWidth;
            int gridViewHeight = Math.round(Math.round((screenHeight * 0.25)));
            int headPaddingLeft = padding + (itemLayoutWidth - itemImageWidth) / 2;
            mCacheSparseArray = new SparseIntArray();
            mCacheSparseArray.put(KEY_LAYOUT_WIDTH, itemLayoutWidth);
            mCacheSparseArray.put(KEY_IMAGE_WIDTH, itemImageWidth);
            mCacheSparseArray.put(KEY_IMAGE_HEIGHT, itemImageHeight);
            mCacheSparseArray.put(KEY_GRIDVIEW_HEIGHT, gridViewHeight);
            mCacheSparseArray.put(KEY_GRIDVIEW_PADDING, padding);
            mCacheSparseArray.put(KEY_HEAD_PADDING_LEFT, headPaddingLeft);

            if (BuildConfig.DEBUG) {
                HDBLOG.logD("screenWidth=" + screenWidth + " itemLayoutWidth=" + itemLayoutWidth
                        + " itemImageWidth=" + itemImageWidth + " itemImageHeight="
                        + itemImageHeight);
            }
        }
        return mCacheSparseArray;
    }

}
