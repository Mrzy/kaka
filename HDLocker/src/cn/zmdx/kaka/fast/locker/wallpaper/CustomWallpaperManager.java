
package cn.zmdx.kaka.fast.locker.wallpaper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.zmdx.kaka.fast.locker.HDApplication;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.fast.locker.theme.ThemeManager;
import cn.zmdx.kaka.fast.locker.wallpaper.PandoraWallpaperManager.IWallpaperClickListener;
import cn.zmdx.kaka.fast.locker.wallpaper.PandoraWallpaperManager.PandoraWallpaper;
import cn.zmdx.kaka.fast.locker.wallpaper.WallpaperUtils.ILoadBitmapCallback;
import cn.zmdx.kaka.fast.locker.R;

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

    public List<CustomWallpaper> getCustomWallpaper(Context context) {
        int currentThemeId = ThemeManager.getCurrentThemeId();
        String currentThemeFileName = null;
        if (currentThemeId == ThemeManager.THEME_ID_CUSTOM) {
            currentThemeFileName = PandoraConfig.newInstance(context).getCurrentWallpaperFileName();
        }
        List<CustomWallpaper> list = new ArrayList<CustomWallpaper>();
        File file = new File(WALLPAPER_SDCARD_LOCATION);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File filePos = files[i];
            String fileName = filePos.getName().substring(0, filePos.getName().indexOf("."));
            CustomWallpaper customWallpaper = new CustomWallpaper();
            customWallpaper.setFilePath(filePos.getPath());
            customWallpaper.setFileName(fileName);
            customWallpaper.setLastModified(filePos.lastModified());
            if (currentThemeId == ThemeManager.THEME_ID_CUSTOM) {
                if (currentThemeFileName.equals(fileName)) {
                    customWallpaper.setCurrentTheme(true);
                }
            }
            list.add(customWallpaper);
        }
        Collections.sort(list, comparator);
        return list;
    }

    public static Comparator<CustomWallpaper> comparator = new Comparator<CustomWallpaper>() {
        @Override
        public int compare(CustomWallpaper object1, CustomWallpaper object2) {
            return (object1.getLastModified() - object2.getLastModified()) > 0 ? 1 : -1;
        }
    };

    public void setCustomWallpaperList(Context mContext, ViewGroup mCustomContainer,
            final IWallpaperClickListener listener, List<PandoraWallpaper> pWallpaperList) {
        if (isHaveCustomWallpaper()) {
            List<CustomWallpaper> wallpaperList = getCustomWallpaper(mContext);
            for (CustomWallpaper list : wallpaperList) {
                String fileName = list.getFileName();
                boolean isCurrentTheme = list.isCurrentTheme();
                setCustomWallpaperItem(mContext, mCustomContainer, fileName, isCurrentTheme,
                        listener, pWallpaperList);
            }
        }
    }

    public void setCustomWallpaperItem(Context mContext, final ViewGroup mCustomContainer,
            final String fileName, boolean isCurrentTheme, final IWallpaperClickListener listener,
            List<PandoraWallpaper> pWallpaperList) {
        final RelativeLayout mWallpaperRl = (RelativeLayout) LayoutInflater.from(
                HDApplication.getContext()).inflate(R.layout.pandora_wallpaper_item, null);
        RelativeLayout mWallpaperIvRl = (RelativeLayout) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_iamge_rl);
        final ImageView mWallpaperIv = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_iamge);
        ImageView mWallpaperSelect = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_select);
        final ImageView mWallpaperDel = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_delete);
        RelativeLayout mWallpaperDelLayout = (RelativeLayout) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_delete_layout);
        WallpaperUtils.loadBitmap(mContext, getFilePath(fileName), new ILoadBitmapCallback() {

            @Override
            public void imageLoaded(Bitmap bitmap, String filePath) {
                mWallpaperIv.setImageBitmap(bitmap);
            }
        });
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
                    mCustomContainer.removeView(mWallpaperRl);
                    listener.onDelClickListener(fileName, getFilePath(fileName));
                }
            }
        });
        mCustomContainer.addView(mWallpaperRl, Math.min(1, mCustomContainer.getChildCount()));

        SparseIntArray sparseIntArray = WallpaperUtils.initWallpaperSize(mContext);
        int layoutWidth = sparseIntArray.get(WallpaperUtils.KEY_LAYOUT_WIDTH);
        int layoutHeight = sparseIntArray.get(WallpaperUtils.KEY_LAYOUT_HEIGHT);
        int imageWidth = sparseIntArray.get(WallpaperUtils.KEY_IMAGE_WIDTH);
        int imageHeight = sparseIntArray.get(WallpaperUtils.KEY_IMAGE_HEIGHT);

        LayoutParams params = mWallpaperIvRl.getLayoutParams();
        params.width = imageWidth;
        params.height = imageHeight;
        mWallpaperIvRl.setLayoutParams(params);

        LayoutParams layoutParams = mWallpaperRl.getLayoutParams();
        layoutParams.width = layoutWidth;
        layoutParams.height = layoutHeight;
        mWallpaperRl.setLayoutParams(layoutParams);

        PandoraWallpaper pWallpaper = new PandoraWallpaper();
        pWallpaper.setCurrentWallpaper(isCurrentTheme);
        pWallpaper.setSelectView(mWallpaperSelect);
        pWallpaper.setDeleteView(mWallpaperDelLayout);
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
