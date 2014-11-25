
package cn.zmdx.kaka.locker.wallpaper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.wallpaper.PandoraWallpaperManager.IWallpaperClickListener;
import cn.zmdx.kaka.locker.wallpaper.PandoraWallpaperManager.PandoraWallpaper;

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

    @SuppressWarnings("unused")
    private boolean isHaveFileWithFileName(String path, String name) {
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

    public List<CustomWallpaper> getCustomWallpaper(Context context) {
        int currentThemeId = ThemeManager.getCurrentTheme().getmThemeId();
        String currentThemeFileName = null;
        if (currentThemeId == ThemeManager.THEME_ID_CUSTOM) {
            currentThemeFileName = PandoraConfig.newInstance(context).getCurrentWallpaperFileName();
        }
        List<CustomWallpaper> list = new ArrayList<CustomWallpaper>();
        File file = new File(WALLPAPER_SDCARD_LOCATION);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName().substring(0, files[i].getName().indexOf("."));
            CustomWallpaper customWallpaper = new CustomWallpaper();
            customWallpaper.setFilePath(files[i].getPath());
            customWallpaper.setFileName(fileName);
            if (currentThemeId == ThemeManager.THEME_ID_CUSTOM) {
                if (currentThemeFileName.equals(fileName)) {
                    customWallpaper.setCurrentTheme(true);
                }
            }
            list.add(customWallpaper);
        }
        return list;
    }

    public void setCustomWallpaperList(Context mContext, ViewGroup mCustomContainer,
            final IWallpaperClickListener listener, List<PandoraWallpaper> pWallpaperList) {
        if (isHaveCustomWallpaper()) {
            Log.d("syc", "one 0  " + System.currentTimeMillis());
            List<CustomWallpaper> wallpaperList = getCustomWallpaper(mContext);
            Log.d("syc", "one 1  " + System.currentTimeMillis());
            for (int i = 0; i < wallpaperList.size(); i++) {
                String fileName = wallpaperList.get(i).getFileName();
                boolean isCurrentTheme = wallpaperList.get(i).isCurrentTheme();
                setCustomWallpaperItem(mContext, mCustomContainer, fileName, isCurrentTheme,
                        listener, pWallpaperList);
            }
            Log.d("syc", "one 2  " + System.currentTimeMillis());
        }
    }

    public void setCustomWallpaperItem(Context mContext, final ViewGroup mCustomContainer,
            final String fileName, boolean isCurrentTheme, final IWallpaperClickListener listener,
            List<PandoraWallpaper> pWallpaperList) {
        final RelativeLayout mWallpaperRl = (RelativeLayout) LayoutInflater.from(
                HDApplication.getContext()).inflate(R.layout.pandora_wallpaper_item, null);
        RelativeLayout mWallpaperIvRl = (RelativeLayout) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_iamge_rl);
        ImageView mWallpaperIv = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_iamge);
        ImageView mWallpaperSelect = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_select);
        final ImageView mWallpaperDel = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_delete);
        PandoraUtils.loadBitmap(mContext, fileName, mWallpaperIv, true);
        mWallpaperIvRl.setBackgroundResource(R.drawable.setting_wallpaper_border_default);
        mWallpaperIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onClickListener(fileName, getFilePath(fileName), true);
                }
            }
        });
        mWallpaperDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (null != listener) {
                    UmengCustomEventManager.statisticalSelectTheme(ThemeManager.THEME_ID_CUSTOM);
                    mCustomContainer.removeView(mWallpaperRl);
                    listener.onDelClickListener(fileName, getFilePath(fileName));
                }
            }
        });
        mCustomContainer.addView(mWallpaperRl, Math.min(1, mCustomContainer.getChildCount()));
        LayoutParams params = mWallpaperIvRl.getLayoutParams();
        int width = (int) mContext.getResources().getDimension(R.dimen.pandora_wallpaper_width);
        int height = (int) mContext.getResources().getDimension(R.dimen.pandora_wallpaper_height);
        params.width = width;
        params.height = height;
        mWallpaperIvRl.setLayoutParams(params);

        LayoutParams layoutParams = mWallpaperRl.getLayoutParams();
        int layoutWidth = (int) mContext.getResources().getDimension(
                R.dimen.pandora_wallpaper_layout_width);
        int layoutHeight = (int) mContext.getResources().getDimension(
                R.dimen.pandora_wallpaper_layout_height);
        layoutParams.width = layoutWidth;
        layoutParams.height = layoutHeight;
        mWallpaperRl.setLayoutParams(layoutParams);

        PandoraWallpaper pWallpaper = new PandoraWallpaper();
        pWallpaper.setCurrentWallpaper(isCurrentTheme);
        pWallpaper.setImageView(mWallpaperSelect);
        pWallpaper.setFileName(fileName);
        pWallpaperList.add(pWallpaper);

    }

    public String getFilePath(String fileName) {
        return WALLPAPER_SDCARD_LOCATION + fileName + ".jpg";
    }

    public static final class CustomWallpaper {
        private String mFilePath;

        private String mFileName;

        private boolean isCurrentTheme = false;

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
    }
}
