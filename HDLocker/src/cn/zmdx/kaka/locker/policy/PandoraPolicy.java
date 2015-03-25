
package cn.zmdx.kaka.locker.policy;


public class PandoraPolicy {

    /**
     * 本地的百度图片数据库中，已经下载图片的数据条数的最小阀值。当小于这个值时，会调度下载图片的逻辑
     */
    public static final int MIN_COUNT_LOCAL_DB_HAS_IMAGE = 15;

    public static final int COUNT_DOWNLOAD_IMAGE_WIFI = 40;

    public static final int COUNT_DOWNLOAD_IMAGE_NON_WIFI = 10;

    public static final long MIN_CHECK_WEATHER_DURAION = 10 * 60 * 1000;

    public static final int MIN_DURATION_SYNC_DATA_TIME = 60 * 1000;

    public static final long MIN_PULL_ORIGINAL_TIME = 15 * 60 * 1000;// 15min

    public static final long MIN_PULL_WALLPAPER_ORIGINAL_TIME = 3 * 60 * 60 * 1000;// 3h

    public static final long MIN_UPDATE_LOCATION_TIME = 2 * 60 * 60 * 1000;// 2h

    public static final long MIN_SHOW_UNREAD_NEWS_TIME = 1 * 60 * 60 * 1000;// 1h

}
