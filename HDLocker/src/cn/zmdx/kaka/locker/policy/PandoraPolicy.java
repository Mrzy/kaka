
package cn.zmdx.kaka.locker.policy;

import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

public class PandoraPolicy {

    /**
     * 本地的百度图片数据库中，已经下载图片的数据条数的最小阀值。当小于这个值时，会调度下载图片的逻辑
     */
    public static final int MIN_COUNT_LOCAL_DB_HAS_IMAGE = 5;

    public static final int COUNT_DOWNLOAD_IMAGE_WIFI = 20;

    public static final int COUNT_DOWNLOAD_IMAGE_NON_WIFI = 10;

    public static final long MIN_CHECK_WEATHER_DURAION = 1 * 60 * 1000;

    public static final int MIN_DURATION_SYNC_DATA_TIME = 60 * 1000;

    public static final long MIN_PULL_ORIGINAL_TIME = 30 * 60 * 1000;//30min
    
    public static final long MIN_PULL_WALLPAPER_ORIGINAL_TIME = 3 * 60 * 60 * 1000;// 3h

    /**
     * 锁屏页下滑速率，值越小，越灵敏，即越容易下滑解锁
     */
    public static final float DEFAULT_MAX_YVEL = 1500.0f * BaseInfoHelper.getDensity(HDApplication.getContext());

    public static final int MIN_COUNT_FOLDABLE_BOX = 5;
}
