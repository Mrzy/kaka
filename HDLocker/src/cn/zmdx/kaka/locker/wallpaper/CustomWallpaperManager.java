
package cn.zmdx.kaka.locker.wallpaper;

import java.io.File;

import android.annotation.SuppressLint;
import android.os.Environment;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;

@SuppressLint("InflateParams")
public class CustomWallpaperManager {
    private static CustomWallpaperManager mInstance;

    public static CustomWallpaperManager getInstance() {
        if (null == mInstance) {
            mInstance = new CustomWallpaperManager();
        }
        return mInstance;
    }

    public static String WALLPAPER_SDCARD_LOCATION = Environment.getExternalStorageDirectory()
            .getPath() + "/.Pandora/wallpaper/background/";

    public boolean isHaveCustomWallpaper() {
        return PandoraUtils.isHaveFile(WALLPAPER_SDCARD_LOCATION);
    }

    public void mkDirs() {
        File dir = new File(WALLPAPER_SDCARD_LOCATION);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public String getFilePath(String fileName) {
        return WALLPAPER_SDCARD_LOCATION + fileName + ".jpg";
    }

    public static final class CustomWallpaper {
        private String mFilePath;

        private String mFileName;

        private boolean isCurrentTheme = false;

        private long mLastModified;

        public String getFilePath() {
            return mFilePath;
        }

        public void setFilePath(String mFilePath) {
            this.mFilePath = mFilePath;
        }

        public String getFileName() {
            return mFileName;
        }

        public void setFileName(String mFileName) {
            this.mFileName = mFileName;
        }

        public boolean isCurrentTheme() {
            return isCurrentTheme;
        }

        public void setCurrentTheme(boolean isCurrentTheme) {
            this.isCurrentTheme = isCurrentTheme;
        }

        public long getLastModified() {
            return mLastModified;
        }

        public void setLastModified(long mLastModified) {
            this.mLastModified = mLastModified;
        }

    }
}
