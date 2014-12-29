
package cn.zmdx.kaka.locker.theme;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.wallpaper.CustomWallpaperManager;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperManager;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils;

public class ThemeManager {
    public static final int THEME_ID_CUSTOM = -1;

    public static final int THEME_ID_ONLINE = -2;

    public static final int THEME_ID_ROAD = 4;

    public static final int THEME_ID_DEFAULT = THEME_ID_ROAD;

    public static final int THEME_ID_DEFAULT_BACKGROUND_RESID = R.drawable.setting_background_road_fore;

    public static final int THEME_ID_DEFAULT_FOREGROUND_RESID = R.drawable.setting_background_road_fore;

    public static Theme getCurrentTheme() {
        int themeId = PandoraConfig.newInstance(HDApplication.getContext()).getCurrentThemeId();
        if (themeId == THEME_ID_CUSTOM) {
            return getCustomTheme(HDApplication.getContext());
        } else if (themeId == THEME_ID_ONLINE) {
            return getOnlineTheme(HDApplication.getContext());
        } else {
            return getThemeById(themeId);
        }
    }

    private static Theme getOnlineTheme(Context context) {
        String fileName = PandoraConfig.newInstance(context).getCurrentWallpaperFileName();
        String filePath = OnlineWallpaperManager.getInstance().getFilePath(fileName);
        Theme theme = new Theme();
        if (TextUtils.isEmpty(fileName)) {
            theme.setDefaultTheme(true);
            theme.setmBackgroundResId(THEME_ID_DEFAULT_BACKGROUND_RESID);
            theme.setmForegroundResId(THEME_ID_DEFAULT_FOREGROUND_RESID);
            theme.setmThemeId(THEME_ID_DEFAULT);
        } else {
            theme.setDefaultTheme(false);
            theme.setFilePath(filePath);
            theme.setThumbFilePath(filePath);
            theme.setmThemeId(ThemeManager.THEME_ID_ONLINE);
        }
        return theme;
    }

    public static int getCurrentThemeIdForStatistical() {
        return PandoraConfig.newInstance(HDApplication.getContext())
                .getCurrentThemeIdForStatistical();
    }

    public static void saveTheme(int themeId) {
        PandoraConfig.newInstance(HDApplication.getContext()).saveThemeId(themeId);
    }

    /**
     * @param context
     * @return
     */
    private static Theme getCustomTheme(Context context) {
        String fileName = PandoraConfig.newInstance(context).getCurrentWallpaperFileName();
        String filePath = CustomWallpaperManager.getInstance().getFilePath(fileName);
        Theme theme = new Theme();
        if (TextUtils.isEmpty(fileName)) {
            theme.setDefaultTheme(true);
            theme.setmBackgroundResId(THEME_ID_DEFAULT_BACKGROUND_RESID);
            theme.setmForegroundResId(THEME_ID_DEFAULT_FOREGROUND_RESID);
            theme.setmThemeId(THEME_ID_DEFAULT);
        } else {
            theme.setDefaultTheme(false);
            theme.setFilePath(filePath);
            theme.setmThemeId(ThemeManager.THEME_ID_CUSTOM);
        }
        return theme;
    }

    public static Theme getThemeById(int themeId) {
        Theme theme = new Theme();
        switch (themeId) {
            case THEME_ID_DEFAULT:
                String path = WallpaperUtils.getDefaultWallpaperPath();
                if (TextUtils.isEmpty(path)) {
                    path = WallpaperUtils.initDefaultWallpaper();
                }
                theme.setDefaultTheme(false);
                theme.setFilePath(path);
                theme.setmThemeId(THEME_ID_DEFAULT);
                break;
            default:
                String path1 = WallpaperUtils.getDefaultWallpaperPath();
                if (TextUtils.isEmpty(path1)) {
                    path1 = WallpaperUtils.initDefaultWallpaper();
                }
                theme.setDefaultTheme(false);
                theme.setFilePath(path1);
                theme.setmThemeId(THEME_ID_DEFAULT);
                break;
        }
        theme.setDefaultTheme(true);
        return theme;
    }

    public static List<Theme> getAllTheme() {
        List<Theme> list = new ArrayList<Theme>();
        Theme theme = new Theme();
        theme.setmBackgroundResId(R.drawable.setting_background_road_fore);
        theme.setmForegroundResId(R.drawable.setting_background_road_fore);
        theme.setmThemeId(THEME_ID_ROAD);
        list.add(theme);
        return list;
    }

    public static final class Theme {
        /**
         * 前景图resource id
         */
        private int mForegroundResId;

        /**
         * theme id
         */
        private int mThemeId;

        /**
         * 设置页缩略图resource id
         */
        private int mThumbnailResId;

        /**
         * 下层背景图resource id
         */
        private int mBackgroundResId;

        /**
         * 设置页icon resource id
         */
        private int mSettingsIconResId;

        /**
         * 锁屏页钥匙图 resource id
         */
        // private int mDragViewIconResId;

        /**
         * 锁屏页孔图resource id
         */
        // private int mHoleIconResId;

        /**
         * 锁屏页钥匙插入孔图resource id
         */
        // private int mKeyholeIconResId;

        /**
         * true代表当前主题为默认主题，false代表当前主题为自定义或者网络主题
         */
        private boolean isDefaultTheme = true;

        /**
         * 自定义或者网络主题壁纸本地路径
         */
        private String mFilePath;

        /**
         * 自定义或者网络主题壁纸缩略图本地路径
         */
        private String mThumbFilePath;

        public int getmThumbnailResId() {
            return mThumbnailResId;
        }

        public void setmThumbnailResId(int mThumbnailResId) {
            this.mThumbnailResId = mThumbnailResId;
        }

        public int getmThemeId() {
            return mThemeId;
        }

        public void setmThemeId(int mThemeId) {
            this.mThemeId = mThemeId;
        }

        public int getmForegroundResId() {
            return mForegroundResId;
        }

        public void setmForegroundResId(int mForegroundResId) {
            this.mForegroundResId = mForegroundResId;
        }

        public int getmBackgroundResId() {
            return mBackgroundResId;
        }

        public void setmBackgroundResId(int mBackgroundResId) {
            this.mBackgroundResId = mBackgroundResId;
        }

        public int getmSettingsIconResId() {
            return mSettingsIconResId;
        }

        public void setmSettingsIconResId(int mSettingsIconResId) {
            this.mSettingsIconResId = mSettingsIconResId;
        }

        public boolean isDefaultTheme() {
            return isDefaultTheme;
        }

        public void setDefaultTheme(boolean isDefaultTheme) {
            this.isDefaultTheme = isDefaultTheme;
        }

        public String getFilePath() {
            return mFilePath;
        }

        public void setFilePath(String mFilePath) {
            this.mFilePath = mFilePath;
        }

        public String getThumbFilePath() {
            return mThumbFilePath;
        }

        public void setThumbFilePath(String mThumbFilePath) {
            this.mThumbFilePath = mThumbFilePath;
        }

    }
}
