
package cn.zmdx.kaka.locker.policy;

import java.util.Locale;

import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class PandoraPolicy {

    /**
     * 百度图片的本地库中数据总数的最小阀值(包括已下载图片的和未下载图片的)。当小于这个值时，会启动下载百度数据的逻辑
     */
    public static final int MIN_COUNT_LOCAL_DB = 20;

    public static final int MIN_COUNT_PANDORA_IMAGE = 3;

    /**
     * 本地的百度图片数据库中，已经下载图片的数据条数的最小阀值。当小于这个值时，会调度下载图片的逻辑
     */
    public static final int MIN_COUNT_LOCAL_DB_HAS_IMAGE = 5;

    public static final int COUNT_DOWNLOAD_IMAGE_WIFI = 15;

    public static final int COUNT_DOWNLOAD_IMAGE_NON_WIFI = 0;

    /**
     * 请求百度图片时，每页的数据条数
     */
    public static final int REQUEST_COUNT_PER_PAGE = 30;

    /**
     * 请求百度图片数据的总页数
     */
    public static final int REQUEST_PAGE_COUNT_DEFAULT = 10;

    // public static final int[] BAIDU_IMAGE_MODULE = {
    // BaiduTagMapping.INT_TAG1_BIZHI, BaiduTagMapping.INT_TAG1_GAOXIAO,
    // BaiduTagMapping.INT_TAG1_MEINV, BaiduTagMapping.INT_TAG1_MINGXING,
    // BaiduTagMapping.INT_TAG1_SHEYING
    // };

    public static final long MIN_INTERVAL_SAME_BOX = 1000;// 1 min

    public static final long PULL_BAIDU_INTERVAL_TIME = 2 * 24 * 60 * 60 * 1000; // 2
                                                                                 // days

    public static final long MIN_CHECK_WEATHER_DURAION = 1 * 60 * 1000;

    public static final int MIN_DURATION_SYNC_DATA_TIME = 60 * 1000;

    public static final long MIN_PULL_ORIGINAL_TIME = 2 * 60 * 60 * 1000;

    /**
     * 锁屏页下滑速率，值越下，越灵敏，即越容易下滑解锁
     */
    public static final float DEFAULT_MAX_YVEL = 1500.0f * BaseInfoHelper.getDensity(HDApplication.getContext());

    public static final int MIN_COUNT_FOLDABLE_BOX = 5;

    public static boolean verifyImageLegal(String url, int w, int h) {
        try {
            if (w / h > 2.5 || h / w > 2.5 || w > 1500 || h > 2000) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("图片宽高比不符合显示条件，w:" + w + ",h:" + h);
                }
                return false;
            }
            if (TextUtils.isEmpty(url) || url.trim().toUpperCase(Locale.getDefault()).endsWith(".GIF")) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("图片url为null或者以.gif结尾，不符合展示条件，忽略");
                }
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
