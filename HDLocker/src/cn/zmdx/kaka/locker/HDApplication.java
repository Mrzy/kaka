
package cn.zmdx.kaka.locker;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import cn.zmdx.kaka.locker.cache.ImageCacheManager;
import cn.zmdx.kaka.locker.cache.ImageCacheManager.CacheType;
import cn.zmdx.kaka.locker.utils.HDBEventSource;

public class HDApplication extends Application {

    private static Context mContext = null;

    private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 100;// 100Mb磁盘缓存区

    private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;

    private static int DISK_IMAGECACHE_QUALITY = 100; // PNG is lossless so
                                                      // quality is ignored but
                                                      // must be provided

    public static Context getInstannce() {
        return mContext;
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();

        HDBEventSource.startup(getApplicationContext(), null);
        // Intialize the request manager and the image cache
        RequestManager.init(this);
        // createImageCache();
        // Pull baidu image data to local db
        // PandoraBoxDispatcher.getInstance().sendEmptyMessage(
        // PandoraBoxDispatcher.MSG_PULL_BAIDU_DATA);
        // CrashHandler crashHandler = CrashHandler.getInstance();
        // crashHandler.init(getApplicationContext());
        super.onCreate();
    }

    /**
     * Create the image cache. Uses Memory Cache by default. Change to Disk for
     * a Disk based LRU implementation.
     */
    private void createImageCache() {
        ImageCacheManager.getInstance().init(this, "PandoraLocker", DISK_IMAGECACHE_SIZE,
                DISK_IMAGECACHE_COMPRESS_FORMAT, DISK_IMAGECACHE_QUALITY, CacheType.DISK);
    }
}
