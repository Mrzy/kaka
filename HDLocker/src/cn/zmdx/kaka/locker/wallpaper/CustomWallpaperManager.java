
package cn.zmdx.kaka.locker.wallpaper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.wallpaper.PandoraWallpaperManager.IWallpaperClickListener;
import cn.zmdx.kaka.locker.wallpaper.PandoraWallpaperManager.PandoraWallpaper;

public class CustomWallpaperManager {
    private static CustomWallpaperManager mInstance;

    public static CustomWallpaperManager getInstance() {
        if (null == mInstance) {
            mInstance = new CustomWallpaperManager();
        }
        return mInstance;
    }

    public static final int THEME_INT_KEY_NO_NEED = -100;

    public static String WALLPAPER_SDCARD_LOCATION = Environment.getExternalStorageDirectory()
            .getPath() + "/.Pandora/wallpaper/background/";

//    public static String WALLPAPER_THUMB_SDCARD_LOCATION = Environment
//            .getExternalStorageDirectory().getPath() + "/.Pandora/wallpaper/thumb/";

    public boolean isHaveCustomWallpaper() {
        return isHaveFile(WALLPAPER_SDCARD_LOCATION);
    }

//    public boolean isHaveCustomThumbWallpaper() {
//        return isHaveFile(WALLPAPER_THUMB_SDCARD_LOCATION);
//    }

    private boolean isHaveFile(String path) {
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

    public List<CustomWallpaper> getCustomWallpaper() {
        List<CustomWallpaper> list = new ArrayList<CustomWallpaper>();
        File file = new File(WALLPAPER_SDCARD_LOCATION);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            CustomWallpaper customWallpaper = new CustomWallpaper();
            customWallpaper.setFilePath(files[i].getPath());
            customWallpaper.setFileName(files[i].getName().substring(0,
                    files[i].getName().indexOf(".")));
            list.add(customWallpaper);
        }
        return list;
    }

//    public List<CustomWallpaper> getCustomThumbWallpaper() {
//        List<CustomWallpaper> list = new ArrayList<CustomWallpaper>();
//        File file = new File(WALLPAPER_THUMB_SDCARD_LOCATION);
//        File[] files = file.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            if (isHaveFileWithFileName(WALLPAPER_SDCARD_LOCATION, files[i].getName())) {
//                CustomWallpaper customWallpaper = new CustomWallpaper();
//                customWallpaper.setFilePath(files[i].getPath());
//                customWallpaper.setFileName(files[i].getName().substring(0,
//                        files[i].getName().indexOf(".")));
//                list.add(customWallpaper);
//            }
//        }
//        return list;
//
//    }

    public void setCustomWallpaperList(Context mContext, ViewGroup mCustomContainer,
            final IWallpaperClickListener listener, List<PandoraWallpaper> pWallpaperList) {
        if (isHaveCustomWallpaper()) {
            final String currentFileName = PandoraConfig.newInstance(mContext)
                    .getCustomWallpaperFileName();
            int thumbWidth = (int) mContext.getResources().getDimension(
                    R.dimen.pandora_wallpaper_width);
            int thumbHeight = (int) mContext.getResources().getDimension(
                    R.dimen.pandora_wallpaper_height);
            List<CustomWallpaper> wallpaperList = getCustomWallpaper();
            int currentThemeId = ThemeManager.getCurrentTheme().getmThemeId();
            Log.d("syc", "one 1  "+System.currentTimeMillis());
            for (int i = 0; i < wallpaperList.size(); i++) {
                Log.d("syc", "one 2  "+System.currentTimeMillis());
//                final Bitmap bitmap = PandoraUtils.getBitmap(wallpaperList.get(i).getFilePath());
                final String fileName = wallpaperList.get(i).getFileName();
                String path = wallpaperList.get(i).getFilePath();
                Bitmap bitmap = null;
                try {
                    bitmap = PandoraUtils.getAdaptBitmap(((Activity) mContext), path, thumbWidth,
                            thumbHeight);
                } catch (FileNotFoundException e) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error),
                            Toast.LENGTH_LONG).show();
                    PandoraUtils.sCropBitmap = null;
                    ((Activity) mContext).finish();
                }
                Log.d("syc", "one 3  "+System.currentTimeMillis());
                setCustomWallpaperItem(mContext, mCustomContainer, bitmap, fileName,
                        currentFileName, currentThemeId, listener, pWallpaperList);
            }
            Log.d("syc", "one 4  "+System.currentTimeMillis());
        }
    }

    public void setCustomWallpaperItem(Context mContext, ViewGroup mCustomContainer,
            final Bitmap bitmap, final String fileName, final String currentFileName,
            int currentThemeId, final IWallpaperClickListener listener,
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
        mWallpaperIv.setImageBitmap(bitmap);
        mWallpaperIvRl.setBackgroundResource(R.drawable.setting_wallpaper_border_default);
        mWallpaperIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onCustomClickListener(fileName, bitmap);
                }
            }
        });
        mWallpaperDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (null != listener) {
                    listener.onDelClickListener(mWallpaperRl, fileName);
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
        pWallpaper.setDefaultWallpaper(false);
        pWallpaper.setCurrentWallpaper(currentThemeId == ThemeManager.THEME_ID_CUSTOM
                && currentFileName.equals(fileName));
        pWallpaper.setImageView(mWallpaperSelect);
        pWallpaper.setImageStringKeyName(fileName);
        pWallpaper.setImageIntKey(THEME_INT_KEY_NO_NEED);
        pWallpaperList.add(pWallpaper);

    }

    public String getCustomWallpaperFilePath(String fileName) {
        return WALLPAPER_SDCARD_LOCATION + fileName + ".jpg";
    }

//    public String getThumbCustomWallpaperFilePath(String fileName) {
//        return WALLPAPER_THUMB_SDCARD_LOCATION + fileName + ".jpg";
//    }

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
