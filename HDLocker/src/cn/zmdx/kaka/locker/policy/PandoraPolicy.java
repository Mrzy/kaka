
package cn.zmdx.kaka.locker.policy;

import cn.zmdx.kaka.locker.content.BaiduTagMapping;

public class PandoraPolicy {

    /**
     * 百度图片的本地库中数据总数的最小阀值(包括已下载图片的和未下载图片的)。当小于这个值时，会启动下载百度数据的逻辑
     */
    public static final int MIN_COUNT_LOCAL_DB = 20;

    /**
     * 本地的百度图片数据库中，已经下载图片的数据条数的最小阀值。当小于这个值时，会调度下载图片的逻辑
     */
    public static final int MIN_COUNT_LOCAL_DB_HAS_IMAGE = 5;

    public static final int COUNT_DOWNLOAD_IMAGE_WIFI = 15;

    public static final int COUNT_DOWNLOAD_IMAGE_NON_WIFI = 1;

    /**
     * 请求百度图片时，每页的数据条数
     */
    public static final int REQUEST_COUNT_PER_PAGE = 30;

    /**
     * 请求百度图片数据的总页数
     */
    public static final int REQUEST_PAGE_COUNT_DEFAULT = 10;

    public static final int[] BAIDU_IMAGE_MODULE = {
        BaiduTagMapping.INT_TAG1_GAOXIAO
    };
//     public static final int[] BAIDU_IMAGE_MODULE = {
//     BaiduTagMapping.INT_TAG1_BIZHI, BaiduTagMapping.INT_TAG1_GAOXIAO,
//     BaiduTagMapping.INT_TAG1_MEINV, BaiduTagMapping.INT_TAG1_MINGXING,
//     BaiduTagMapping.INT_TAG1_SHEYING
//     };

    public static final long MIN_INTERVAL_SAME_BOX = 1000 * 60;// 1 min

}
