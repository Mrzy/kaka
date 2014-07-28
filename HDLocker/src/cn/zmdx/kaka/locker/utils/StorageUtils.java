package cn.zmdx.kaka.locker.utils;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;


/**
 * Utility to obtain the internal and external storage info.
 */
public class StorageUtils {
    private static final String TAG = "StorageUtils";
    private static final String[] CACHE_SKIP_PACKAGES = {"com.sec.knox.containeragent"};

    /**
     * Get the internal storage directory (ROM)
     */
    public static String getInternalStorageDirectory() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    /**
     * Get the available internal storage size (ROM).
     */
    public static long getInternalStorageAvailableSize() {
        StatFs stat = new StatFs(getInternalStorageDirectory());
        return (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
    }

    /**
     * Get the total internal storage size (ROM).
     */
    public static long getInternalStorageTotalSize() {
        StatFs stat = new StatFs(getInternalStorageDirectory());
        return (long) stat.getBlockSize() * (long) stat.getBlockCount();
    }

    public static int getInertalStorageUsedPercent() {
        long total = getInternalStorageTotalSize();
        long free = getInternalStorageAvailableSize();
        return (int) ((total - free) * 100l / total);
    }

    public static int getInertalStorageFreedPercent() {
        long total = getInternalStorageTotalSize();
        long free = getInternalStorageAvailableSize();
        return (int) (free * 100l / total);
    }

    public static int getExternalStorageUsedPercent() {
        if (StorageUtils.externalStorageAvailable()) {
            long total = getExternalStorageTotalSize();
            long free = getExternalStorageAvailableSize();
            return (int) ((total - free) * 100l / total);
        } else {
            return 0;
        }
    }

    public static int getExternalStorageFreedPercent() {
        if (StorageUtils.externalStorageAvailable()) {
            long total = getExternalStorageTotalSize();
            long free = getExternalStorageAvailableSize();
            return (int) (free * 100l / total);
        } else {
            return 0;
        }
    }

    /**
     * Check if the external storage exists (SD Card)
     * @return true if the external storage exists, otherwise false
     */
    public static boolean externalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Get the external storage directory (SD Card)
     */
    public static String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * Get the absolute path of a relative path on the external storage (SD card)
     */
    public static String getExternalStorageSubDirectory(String relativePath) {
        return new File(Environment.getExternalStorageDirectory(), relativePath).getAbsolutePath();
    }

    /**
     * Get the available external storage size (SD Card)
     * @return Return the available external storage size in bytes if possible, otherwise -1
     */
    public static long getExternalStorageAvailableSize() {
        try {
            if (externalStorageAvailable()) {
                StatFs stat = new StatFs(getExternalStorageDirectory());
                return (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
            }
        } catch (Exception e) {
            // ignore
        }
        return -1;
    }

    /**
     * Get the total external storage size  (SD Card)
     * @return Return the total external storage size in bytes if possible, otherwise -1
     */
    public static long getExternalStorageTotalSize() {
        try {
            if (externalStorageAvailable()) {
                StatFs stat = new StatFs(getExternalStorageDirectory());
                long temp = (long) stat.getBlockSize() * (long) stat.getBlockCount();
                return temp == 0 ? -1 : temp;
            }
        } catch (Exception e) {
            // ignore
        }
        return -1;
    }

    // TODO: The following path is not standard
    private static final String EXTRA_SDCARD_PATH = getExternalStorageDirectory() + "/external_sd";

    /**
     * Check if the extra SD card available
     */
    public static boolean extraSDCardAvailable() {
        return new File(EXTRA_SDCARD_PATH).exists();
    }

    /**
     * Get the available extra SD card size.
     * @return Return the available extra SD card in bytes if possible, otherwise -1
     */
    public static long getExtraSDCardAvailableSize() {
        if (externalStorageAvailable()) {
            StatFs stat = new StatFs(EXTRA_SDCARD_PATH);
            return (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        } else {
            return -1;
        }
    }

    /**
     * Get the total extra SD card size.
     * @return Return the total extra SD card size in bytes if possible, otherwise -1
     */
    public static long getExtraSDCardTotalSize() {
        if (externalStorageAvailable()) {
            StatFs stat = new StatFs(EXTRA_SDCARD_PATH);
            return (long) stat.getBlockSize() * (long) stat.getBlockCount();
        } else {
            return -1;
        }
    }

    /**
     * Get total size (code size + data size + cache size) of the specified package.
     * Note: Cannot be called in UI thread.
     */
    public static final long getAppTotalSize(Context cxt, String pkgName) {
        PackageManager pm = cxt.getPackageManager();
        final CountDownLatch latch = new CountDownLatch(1);
        final long[] totalSize = new long[] {0};

        // NOTE: To call a hidden method from PackageManager
        boolean invokeSuccess = PackageCompat.packageManager_getPackageSizeInfo(pm, pkgName, new IPackageStatsObserver.Stub() {
            @Override
            public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
                if (succeeded && stats != null) {
                    totalSize[0] = stats.codeSize + stats.dataSize + stats.cacheSize;
                }
                latch.countDown();
            }
        });

        if (invokeSuccess) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                HDBLOG.logE("Unexpected interruption", e);
                // ignore the exception
            }
        }

        return totalSize[0];
    }

    /**
     * Get cleanable size (data size + cache size) of the specified package.
     * Note: Cannot be called in UI thread.
     */
    public static final long getAppCleanableSize(Context cxt, String pkgName) {
        PackageManager pm = cxt.getPackageManager();
        final CountDownLatch latch = new CountDownLatch(1);
        final long[] totalSize = new long[] {0};

        // NOTE: To call a hidden method from PackageManager
        boolean invokeSuccess = PackageCompat.packageManager_getPackageSizeInfo(pm, pkgName, new IPackageStatsObserver.Stub() {
            @Override
            public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
                if (succeeded && stats != null) {
                    totalSize[0] = stats.dataSize + stats.cacheSize;
                }
                latch.countDown();
            }
        });

        if (invokeSuccess) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                HDBLOG.logE("Unexpected interruption", e);
                // ignore the exception
            }
        }

        return totalSize[0];
    }

    /**
     * Get code size of the specified package.
     * Note: Cannot be called in UI thread.
     */
    public static final long getAppCodeSize(Context cxt, String pkgName) {
        PackageManager pm = cxt.getPackageManager();
        final CountDownLatch latch = new CountDownLatch(1);
        final long[] totalSize = new long[] {0};

        // NOTE: To call a hidden method from PackageManager
        boolean invokeSuccess = PackageCompat.packageManager_getPackageSizeInfo(pm, pkgName, new IPackageStatsObserver.Stub() {
            @Override
            public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
                if (succeeded && stats != null) {
                    totalSize[0] = stats.codeSize;
                }
                latch.countDown();
            }
        });

        if (invokeSuccess) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                HDBLOG.logE("Unexpected interruption", e);
                // ignore the exception
            }
        }

        return totalSize[0];
    }

    /**
     * Get cache size of the specified package.
     * Note: Cannot be called in UI thread
     */
    public static final long getAppCacheSize(Context cxt, String pkgName, final boolean hasSystemPerm) {
        PackageManager pm = cxt.getPackageManager();
        final CountDownLatch latch = new CountDownLatch(1);
        final long[] cacheSize = new long[] {0};

        // skip these pkgs that can not get or clear cache
        for (String skipPkg : CACHE_SKIP_PACKAGES) {
            if (skipPkg.equals(pkgName)) {
                return cacheSize[0];
            }
        }

        // NOTE: To call a hidden method from PackageManager
        boolean invokeSuccess = PackageCompat.packageManager_getPackageSizeInfo(pm, pkgName, new IPackageStatsObserver.Stub() {
            @SuppressLint("NewApi")
            @Override
            public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
                if (succeeded && stats != null) {
                    cacheSize[0] = stats.cacheSize;
                    if (Build.VERSION.SDK_INT >= 11) {
                        if (hasSystemPerm) {
                            // Only show "external cache" when we can delete it
                            // which need "system" or "root" permission.
                            cacheSize[0] += stats.externalCacheSize;
                        }
                    }
                    if (Build.VERSION.SDK_INT >= 17) {
                        if (cacheSize[0] == 12 * 1024) {
                            // Workaround for the "12KB" issue from Android 4.2
                            cacheSize[0] = 0;
                        } else if (cacheSize[0] == 4 * 1024) {
                            // workaround for the "4KB" issue on Huawei Emotion UI 4.2
                            cacheSize[0] = 0;
                        } else if (cacheSize[0] == 8 * 1024) {
                            // workaround for the "8KB" issue on Samsung Note3
                            cacheSize[0] = 0;
                        }
                    }
                }
                latch.countDown();
            }
        });

        if (invokeSuccess) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                HDBLOG.logE("Unexpected interruption", e);
                // ignore the exception
            }
        }

        return cacheSize[0];
    }

    public static void clearAllAppCachesOneKey(Context cxt) {
        PackageManager pm = cxt.getPackageManager();
        long freeStorageSize = StorageUtils.getInternalStorageTotalSize() - 1;
        final CountDownLatch latch = new CountDownLatch(1);
        boolean invokeSuccess = PackageCompat.packageManager_freeStorageAndNotify(pm, freeStorageSize, new IPackageDataObserver.Stub() {
            @Override
            public void onRemoveCompleted(String packageName, boolean succeeded) {
                latch.countDown();
            }
        });
        if (invokeSuccess) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                HDBLOG.logE("Unexpected interruption", e);
                // ignore the exception
            }
        }
    }
}
