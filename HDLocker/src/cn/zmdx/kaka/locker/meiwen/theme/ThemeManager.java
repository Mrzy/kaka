
package cn.zmdx.kaka.locker.meiwen.theme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.meiwen.HDApplication;
import cn.zmdx.kaka.locker.meiwen.ImageLoaderManager;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.meiwen.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.meiwen.utils.ImageUtils;
import cn.zmdx.kaka.locker.meiwen.wallpaper.CustomWallpaperManager;
import cn.zmdx.kaka.locker.meiwen.wallpaper.OnlineWallpaperManager;
import cn.zmdx.kaka.locker.meiwen.wallpaper.WallpaperUtils;

public class ThemeManager {
    public static final int THEME_ID_CUSTOM = -1;

    public static final int THEME_ID_ONLINE = -2;

    public static final int THEME_ID_DEFAULT = 4;

    public static final int DEFAULT_BACKGROUND_RES_ID = Res.drawable.pandora_locker_default_wallpaper_new;

    private static final String CURRENT_THEME_CACHE_KEY = "curThemeCacheKey";

    public static Theme getCurrentTheme() {
        int themeId = PandoraConfig.newInstance(HDApplication.getContext()).getCurrentThemeId();
        if (themeId == THEME_ID_CUSTOM) {
            return getCustomTheme(HDApplication.getContext());
        } else if (themeId == THEME_ID_ONLINE) {
            return getOnlineTheme(HDApplication.getContext());
        } else {
            return getDefauleTheme(HDApplication.getContext());
        }
    }

    /**
     * 获取当前网络壁纸主题
     * 
     * @param context
     * @return
     */
    private static Theme getOnlineTheme(Context context) {
        return getThemeByThemeId(context, THEME_ID_ONLINE);
    }

    /**
     * 获取当前自定义壁纸主题
     * 
     * @param context
     * @return
     */
    private static Theme getCustomTheme(Context context) {
        return getThemeByThemeId(context, THEME_ID_CUSTOM);
    }

    private static Theme getThemeByThemeId(Context context, int themeId) {
        Theme theme = null;
        Bitmap cacheBmp = ImageLoaderManager.getImageMemCache().getBitmap(CURRENT_THEME_CACHE_KEY);
        if (null == cacheBmp) {
            String fileName = PandoraConfig.newInstance(context).getCurrentWallpaperFileName();
            if (TextUtils.isEmpty(fileName)) {
                theme = getDefauleTheme(context);
            } else {
                theme = new Theme();
                int screenWidth = BaseInfoHelper.getRealWidth(context);
                int screenHeight = BaseInfoHelper.getRealHeight(context);
                String filePath = getFilePathByThemeId(themeId, fileName);
                Bitmap bitmap = ImageUtils.getBitmapFromFile(filePath, screenWidth, screenHeight);
                if (null == bitmap) {
                    theme = getDefauleTheme(context);
                } else {
                    addBitmapToCache(bitmap);
                    BitmapDrawable drawable = ImageUtils.bitmap2Drawable(context, bitmap);
                    theme.setCurDrawable(drawable);
                }
            }
        } else {
            theme = new Theme();
            BitmapDrawable drawable = ImageUtils.bitmap2Drawable(context, cacheBmp);
            theme.setCurDrawable(drawable);
        }
        return theme;

    }

    /**
     * 获取当前默认主题
     * 
     * @param context
     * @return
     */
    private static Theme getDefauleTheme(Context context) {
        Theme theme = new Theme();
        Bitmap cacheBmp = ImageLoaderManager.getImageMemCache().getBitmap(CURRENT_THEME_CACHE_KEY);
        if (null == cacheBmp) {
            Drawable drawable = context.getResources().getDrawable(DEFAULT_BACKGROUND_RES_ID);
            Bitmap defaultBitmap = ImageUtils.drawable2Bitmap(drawable);
            ThemeManager.addBitmapToCache(defaultBitmap);
            theme.setCurDrawable(drawable);
        } else {
            theme = new Theme();
            BitmapDrawable drawable = ImageUtils.bitmap2Drawable(context, cacheBmp);
            theme.setCurDrawable(drawable);
        }
        return theme;
    }

    private static String getFilePathByThemeId(int themeId, String fileName) {
        String filePath = "";
        switch (themeId) {
            case THEME_ID_CUSTOM:
                filePath = CustomWallpaperManager.getInstance().getFilePath(fileName);
                break;
            case THEME_ID_ONLINE:
                filePath = OnlineWallpaperManager.getInstance().getFilePath(fileName);
                break;

            default:
                break;
        }
        return filePath;
    }

    public static void saveTheme(int themeId) {
        PandoraConfig.newInstance(HDApplication.getContext()).saveThemeId(themeId);
    }

    public static int getCurrentThemeId() {
        return PandoraConfig.newInstance(HDApplication.getContext()).getCurrentThemeId();
    }

    public static void addBitmapToCache(Bitmap bitmap) {
        invalidateBitmapCache();
        ImageLoaderManager.getImageMemCache().putBitmap(CURRENT_THEME_CACHE_KEY, bitmap);
    }

    public static void invalidateBitmapCache() {
        ImageLoaderManager.getImageMemCache().invalidateBitmap(CURRENT_THEME_CACHE_KEY);
    }

    // public static Theme getThemeById(int themeId) {
    // Theme theme = new Theme();
    // switch (themeId) {
    // case THEME_ID_DEFAULT:
    // String path = WallpaperUtils.getDefaultWallpaperPath();
    // if (TextUtils.isEmpty(path)) {
    // path = WallpaperUtils.initDefaultWallpaper();
    // }
    // theme.setDefaultTheme(false);
    // theme.setFilePath(path);
    // theme.setmThemeId(THEME_ID_DEFAULT);
    // break;
    // default:
    // String path1 = WallpaperUtils.getDefaultWallpaperPath();
    // if (TextUtils.isEmpty(path1)) {
    // path1 = WallpaperUtils.initDefaultWallpaper();
    // }
    // theme.setDefaultTheme(false);
    // theme.setFilePath(path1);
    // theme.setmThemeId(THEME_ID_DEFAULT);
    // break;
    // }
    // theme.setDefaultTheme(true);
    // return theme;
    // }

    public static final class Theme {

        private Drawable mCurDrawable;

        public Drawable getCurDrawable() {
            return mCurDrawable;
        }

        public void setCurDrawable(Drawable mCurDrawable) {
            this.mCurDrawable = mCurDrawable;
        }

    }
}
