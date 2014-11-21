
package cn.zmdx.kaka.locker.theme;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.wallpaper.CustomWallpaperManager;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperManager;

public class ThemeManager {
    public static final int THEME_ID_CUSTOM = -1;

    public static final int THEME_ID_ONLINE = -2;

    public static final int THEME_ID_BLUE = 1;

    public static final int THEME_ID_JEAN = 3;

    public static final int THEME_ID_ROAD = 4;

    public static final int THEME_ID_DEFAULT = THEME_ID_ROAD;

    public static final int THEME_ID_DEFAULT_BACKGROUND_RESID = R.drawable.setting_background_road_fore;

    public static final int THEME_ID_DEFAULT_FOREGROUND_RESID = R.drawable.setting_background_road_fore;

    public static final int THEME_ID_DEFAULT_THUMBNAIL_RESID = R.drawable.setting_wallpaper_road;

    public static final int THEME_ID_DEFAULT_SETTINGICON_RESID = R.drawable.ic_setting_common;

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
        String fileName = PandoraConfig.newInstance(context).getOnlineWallpaperFileName();
        Bitmap bitmap = PandoraUtils.getBitmap(OnlineWallpaperManager.getInstance()
                .getOnlineWallpaperFilePath(fileName));
        Theme theme = new Theme();
        if (null == bitmap) {
            theme.setDefaultTheme(true);
            theme.setmBitmap(null);
            theme.setmBackgroundResId(THEME_ID_DEFAULT_BACKGROUND_RESID);
            theme.setmForegroundResId(THEME_ID_DEFAULT_FOREGROUND_RESID);
            theme.setmSettingsIconResId(THEME_ID_DEFAULT_SETTINGICON_RESID);
            theme.setmThumbnailResId(THEME_ID_DEFAULT_THUMBNAIL_RESID);
            theme.setmThemeId(THEME_ID_DEFAULT);
        } else {
            theme.setDefaultTheme(false);
            theme.setmBitmap(ImageUtils.bitmap2Drawable(context, bitmap));
            theme.setThumbBitmap(ImageUtils.bitmap2Drawable(context, bitmap));
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
        String fileName = PandoraConfig.newInstance(context).getCustomWallpaperFileName();
        Bitmap bitmap = PandoraUtils.getBitmap(CustomWallpaperManager.getInstance()
                .getCustomWallpaperFilePath(fileName));
        Bitmap thumbBitmap = PandoraUtils.getBitmap(CustomWallpaperManager.getInstance()
                .getCustomWallpaperFilePath(fileName));
        Theme theme = new Theme();
        if (null == bitmap) {
            theme.setDefaultTheme(true);
            theme.setmBitmap(null);
            theme.setmBackgroundResId(THEME_ID_DEFAULT_BACKGROUND_RESID);
            theme.setmForegroundResId(THEME_ID_DEFAULT_FOREGROUND_RESID);
            theme.setmSettingsIconResId(THEME_ID_DEFAULT_SETTINGICON_RESID);
            theme.setmThumbnailResId(THEME_ID_DEFAULT_THUMBNAIL_RESID);
            theme.setmThemeId(THEME_ID_DEFAULT);
        } else {
            theme.setDefaultTheme(false);
            theme.setmBitmap(ImageUtils.bitmap2Drawable(context, bitmap));
            theme.setThumbBitmap(ImageUtils.bitmap2Drawable(context, thumbBitmap));
            theme.setmThemeId(ThemeManager.THEME_ID_CUSTOM);
        }
        return theme;
    }

    public static Theme getThemeById(int themeId) {
        Theme theme = new Theme();
        switch (themeId) {
            case THEME_ID_BLUE:
                theme.setmBackgroundResId(R.drawable.setting_background_blue_fore);
                theme.setmForegroundResId(R.drawable.setting_background_blue_fore);
                theme.setmSettingsIconResId(R.drawable.ic_setting_common);
                theme.setmThumbnailResId(R.drawable.setting_wallpaper_blue);
                theme.setmThemeId(THEME_ID_BLUE);
                break;
            case THEME_ID_JEAN:
                theme.setmBackgroundResId(R.drawable.setting_background_jean_fore);
                theme.setmForegroundResId(R.drawable.setting_background_jean_fore);
                theme.setmSettingsIconResId(R.drawable.ic_setting_common);
                theme.setmThumbnailResId(R.drawable.setting_wallpaper_jean);
                theme.setmThemeId(THEME_ID_JEAN);
                break;
            case THEME_ID_ROAD:
                theme.setmBackgroundResId(R.drawable.setting_background_road_fore);
                theme.setmForegroundResId(R.drawable.setting_background_road_fore);
                theme.setmSettingsIconResId(R.drawable.ic_setting_common);
                theme.setmThumbnailResId(R.drawable.setting_wallpaper_road);
                theme.setmThemeId(THEME_ID_ROAD);
                break;
            default:
                theme.setmBackgroundResId(THEME_ID_DEFAULT_BACKGROUND_RESID);
                theme.setmForegroundResId(THEME_ID_DEFAULT_FOREGROUND_RESID);
                theme.setmSettingsIconResId(THEME_ID_DEFAULT_SETTINGICON_RESID);
                theme.setmThumbnailResId(THEME_ID_DEFAULT_THUMBNAIL_RESID);
                theme.setmThemeId(THEME_ID_DEFAULT);
                break;
        }
        theme.setDefaultTheme(true);
        return theme;
    }

    public static List<Theme> getAllTheme() {
        List<Theme> list = new ArrayList<Theme>();
        Theme theme = new Theme();
        theme.setmBackgroundResId(R.drawable.setting_background_blue_fore);
        theme.setmForegroundResId(R.drawable.setting_background_blue_fore);
        theme.setmSettingsIconResId(R.drawable.ic_setting_common);
        theme.setmThumbnailResId(R.drawable.setting_wallpaper_blue);
        theme.setmThemeId(THEME_ID_BLUE);
        list.add(theme);

        theme = new Theme();
        theme.setmBackgroundResId(R.drawable.setting_background_jean_fore);
        theme.setmForegroundResId(R.drawable.setting_background_jean_fore);
        theme.setmSettingsIconResId(R.drawable.ic_setting_common);
        theme.setmThumbnailResId(R.drawable.setting_wallpaper_jean);
        theme.setmThemeId(THEME_ID_JEAN);
        list.add(theme);

        theme = new Theme();
        theme.setmBackgroundResId(R.drawable.setting_background_road_fore);
        theme.setmForegroundResId(R.drawable.setting_background_road_fore);
        theme.setmSettingsIconResId(R.drawable.ic_setting_common);
        theme.setmThumbnailResId(R.drawable.setting_wallpaper_road);
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
         * 自定义或者网络主题壁纸bitmap
         */
        private BitmapDrawable mBitmap;

        /**
         * 自定义或者网络主题壁纸缩略图bitmap
         */
        private BitmapDrawable mThumbBitmap;

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

        public BitmapDrawable getmBitmap() {
            return mBitmap;
        }

        public void setmBitmap(BitmapDrawable mBitmap) {
            this.mBitmap = mBitmap;
        }

        public BitmapDrawable getThumbBitmap() {
            return mThumbBitmap;
        }

        public void setThumbBitmap(BitmapDrawable mThumbBitmap) {
            this.mThumbBitmap = mThumbBitmap;
        }

    }
}
