
package cn.zmdx.kaka.locker.theme;

import java.util.ArrayList;
import java.util.List;

import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

public class ThemeManager {

    public static final int THEME_ID_BLUE = 1; // default

    public static final int THEME_ID_WOOD_GRAIN = 2;

    public static final int THEME_ID_PURPLE = 3;

    public static final int THEME_ID_YELLOW = 4;

    public static Theme getCurrentTheme() {
        int themeId = PandoraConfig.newInstance(HDApplication.getInstannce()).getCurrentThemeId();
        return getThemeById(themeId);
    }

    public void saveTheme(int themeId) {
        PandoraConfig.newInstance(HDApplication.getInstannce()).saveThemeId(themeId);
    }

    public static Theme getThemeById(int themeId) {
        Theme theme = new Theme();
        switch (themeId) {
            case THEME_ID_BLUE:
                theme.setmBackgroundResId(R.drawable.setting_background_blue);
                theme.setmForegroundResId(R.drawable.setting_background_blue_fore);
                // theme.setmDragViewIconResId(0);
                theme.setmSettingsIconResId(R.drawable.ic_setting_blue);
                theme.setmThumbnailResId(R.drawable.setting_wallpaper_blue);
                theme.setmThemeId(THEME_ID_BLUE);
                break;
            case THEME_ID_WOOD_GRAIN:
                theme.setmBackgroundResId(R.drawable.setting_background_wood_grain);
                theme.setmForegroundResId(R.drawable.setting_background_wood_grain_fore);
                // theme.setmDragViewIconResId(0);
                theme.setmSettingsIconResId(R.drawable.ic_setting_wood_grain);
                theme.setmThumbnailResId(R.drawable.setting_wallpaper_green);
                theme.setmThemeId(THEME_ID_WOOD_GRAIN);
                break;
            case THEME_ID_PURPLE:
                theme.setmBackgroundResId(R.drawable.setting_background_purple);
                theme.setmForegroundResId(R.drawable.setting_background_purple_fore);
                // theme.setmDragViewIconResId(0);
                theme.setmSettingsIconResId(R.drawable.ic_setting_blue);
                theme.setmThumbnailResId(R.drawable.setting_wallpaper_purple);
                theme.setmThemeId(THEME_ID_PURPLE);
                break;
            case THEME_ID_YELLOW:
                theme.setmBackgroundResId(R.drawable.setting_background_yellow);
                theme.setmForegroundResId(R.drawable.setting_background_yellow_fore);
                // theme.setmDragViewIconResId(0);
                theme.setmSettingsIconResId(R.drawable.ic_setting_blue);
                theme.setmThumbnailResId(R.drawable.setting_wallpaper_yellow);
                theme.setmThemeId(THEME_ID_YELLOW);
                break;
            default:
                theme.setmBackgroundResId(R.drawable.setting_background_blue);
                theme.setmForegroundResId(R.drawable.setting_background_blue_fore);
                // theme.setmDragViewIconResId(0);
                theme.setmSettingsIconResId(R.drawable.ic_setting_blue);
                theme.setmThumbnailResId(R.drawable.setting_wallpaper_blue);
                theme.setmThemeId(THEME_ID_BLUE);
                break;
        }
        return theme;
    }

    public static List<Theme> getAllTheme() {
        List<Theme> list = new ArrayList<Theme>();
        Theme theme = new Theme();
        theme.setmBackgroundResId(R.drawable.setting_background_blue);
        theme.setmForegroundResId(R.drawable.setting_background_blue_fore);
        // theme.setmDragViewIconResId(0);
        theme.setmSettingsIconResId(R.drawable.ic_setting_blue);
        theme.setmThumbnailResId(R.drawable.setting_wallpaper_blue);
        theme.setmThemeId(THEME_ID_BLUE);
        list.add(theme);

        theme = new Theme();
        theme.setmBackgroundResId(R.drawable.setting_background_wood_grain);
        theme.setmForegroundResId(R.drawable.setting_background_wood_grain_fore);
        // theme.setmDragViewIconResId(0);
        theme.setmSettingsIconResId(R.drawable.ic_setting_wood_grain);
        theme.setmThumbnailResId(R.drawable.setting_wallpaper_green);
        theme.setmThemeId(THEME_ID_WOOD_GRAIN);
        list.add(theme);

        theme = new Theme();
        theme.setmBackgroundResId(R.drawable.setting_background_purple);
        theme.setmForegroundResId(R.drawable.setting_background_purple_fore);
        // theme.setmDragViewIconResId(0);
        theme.setmSettingsIconResId(R.drawable.ic_setting_blue);
        theme.setmThumbnailResId(R.drawable.setting_wallpaper_purple);
        theme.setmThemeId(THEME_ID_PURPLE);
        list.add(theme);

        theme = new Theme();
        theme.setmBackgroundResId(R.drawable.setting_background_yellow);
        theme.setmForegroundResId(R.drawable.setting_background_yellow_fore);
        // theme.setmDragViewIconResId(0);
        theme.setmSettingsIconResId(R.drawable.ic_setting_blue);
        theme.setmThumbnailResId(R.drawable.setting_wallpaper_yellow);
        theme.setmThemeId(THEME_ID_YELLOW);
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
        private int mDragViewIconResId;

        /**
         * 锁屏页钥匙孔图resource id
         */
        private int mKeyholeIconResId;

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

        public int getmDragViewIconResId() {
            return mDragViewIconResId;
        }

        public void setmDragViewIconResId(int mDragViewIconResId) {
            this.mDragViewIconResId = mDragViewIconResId;
        }

        public int getmKeyholeIconResId() {
            return mKeyholeIconResId;
        }

        public void setmKeyholeIconResId(int mKeyholeIconResId) {
            this.mKeyholeIconResId = mKeyholeIconResId;
        }

    }
}