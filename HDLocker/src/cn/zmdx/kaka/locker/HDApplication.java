
package cn.zmdx.kaka.locker;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import cn.zmdx.kaka.locker.utils.HDBEventSource;

public class HDApplication extends Application {

    private static Context instance = null;

    private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 100;// 100Mb磁盘缓存区

    private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.JPEG;

    private static int DISK_IMAGECACHE_QUALITY = 80; // PNG is lossless so
                                                      // quality is ignored but
                                                      // must be provided

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = getApplicationContext();
        HDBEventSource.startup(getApplicationContext(), null);
        RequestManager.init(this);
        ImageLoaderManager.init(instance);
        super.onCreate();
    }
}
