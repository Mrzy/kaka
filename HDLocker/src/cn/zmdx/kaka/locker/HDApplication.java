
package cn.zmdx.kaka.locker;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap.CompressFormat;
import cn.zmdx.kaka.locker.cache.ImageCacheManager;
import cn.zmdx.kaka.locker.cache.ImageCacheManager.CacheType;
import cn.zmdx.kaka.locker.content.PandoraBoxDispatcher;
import cn.zmdx.kaka.locker.settings.config.CrashHandler;
import cn.zmdx.kaka.locker.utils.HDBEventSource;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class HDApplication extends Application {

    private static HDApplication instance = null;

    private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 100;// 100Mb磁盘缓存区

    private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;

    private static int DISK_IMAGECACHE_QUALITY = 100; // PNG is lossless so
                                                      // quality is ignored but
                                                      // must be provided

    public HDApplication() {
        instance = this;
    }

    public static HDApplication getInstannce() {
        return instance;
    }

    @Override
    public void onCreate() {
        HDBEventSource.startup(getApplicationContext(), null);
        // Intialize the request manager and the image cache
        RequestManager.init(this);
        // createImageCache();
        // Pull baidu image data to local db
//        PandoraBoxDispatcher.getInstance().sendEmptyMessage(
//                PandoraBoxDispatcher.MSG_PULL_BAIDU_DATA);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        super.onCreate();
        registerBroadcastReceiver();
    }

    /**
     * Create the image cache. Uses Memory Cache by default. Change to Disk for
     * a Disk based LRU implementation.
     */
    private void createImageCache() {
        ImageCacheManager.getInstance().init(this, "PandoraLocker", DISK_IMAGECACHE_SIZE,
                DISK_IMAGECACHE_COMPRESS_FORMAT, DISK_IMAGECACHE_QUALITY, CacheType.DISK);
    }
    
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mReceiver, filter);
    }

    private void unRegisterBroadcastReceiver() {
        unregisterReceiver(mReceiver);
    }

    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("receive broadcast,action=" + action);
            }
            LockScreenManager.getInstance().lock();
        }
    };
}
