
package cn.zmdx.kaka.locker.custom.wallpaper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class CustomWallpaperManager {
    public static String WALLPAPER_SDCARD_LOCATION = Environment.getExternalStorageDirectory()
            .getPath() + "/Pandora/wallpaper/background/";

    public static String WALLPAPER_THUMB_SDCARD_LOCATION = Environment
            .getExternalStorageDirectory().getPath() + "/Pandora/wallpaper/thumb/";

    public static boolean isHaveCustomWallpaper() {
        return isHaveFile(WALLPAPER_SDCARD_LOCATION);
    }

    public static boolean isHaveCustomThumbWallpaper() {
        return isHaveFile(WALLPAPER_THUMB_SDCARD_LOCATION);
    }

    private static boolean isHaveFile(String path) {
        boolean isHave = false;
        try {
            File file = new File(path);
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                isHave = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isHave;
    }

    private static boolean isHaveFileWithFileName(String path, String name) {
        boolean isHave = false;
        try {
            File file = new File(path);
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().equals(name)) {
                        isHave = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isHave;
    }

    public static List<CustomWallpaper> getCustomWallpaper() {
        List<CustomWallpaper> list = new ArrayList<CustomWallpaper>();
        File file = new File(WALLPAPER_SDCARD_LOCATION);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            CustomWallpaper customWallpaper = new CustomWallpaper();
            customWallpaper.setFilePath(files[i].getPath());
            customWallpaper.setFileName(files[i].getName());
            list.add(customWallpaper);
        }
        return list;
    }

    public static List<CustomWallpaper> getCustomThumbWallpaper() {
        List<CustomWallpaper> list = new ArrayList<CustomWallpaper>();
        File file = new File(WALLPAPER_THUMB_SDCARD_LOCATION);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (isHaveFileWithFileName(WALLPAPER_SDCARD_LOCATION, files[i].getName())) {
                CustomWallpaper customWallpaper = new CustomWallpaper();
                customWallpaper.setFilePath(files[i].getPath());
                customWallpaper.setFileName(files[i].getName().substring(0,
                        files[i].getName().indexOf(".")));
                list.add(customWallpaper);
            }
        }
        return list;

    }

    public static String getCustomWallpaperFilePath(String fileName) {
        return WALLPAPER_SDCARD_LOCATION + fileName + ".jpg";
    }

    public static final class CustomWallpaper {
        private String mFilePath;

        private String mFileName;

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

    }
}
