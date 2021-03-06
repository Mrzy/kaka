
package cn.zmdx.kaka.fast.locker.wallpaper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONObject;

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
import cn.zmdx.kaka.fast.locker.RequestManager;
import cn.zmdx.kaka.fast.locker.network.UrlBuilder;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.fast.locker.theme.ThemeManager;
import cn.zmdx.kaka.fast.locker.utils.FileHelper;
import cn.zmdx.kaka.fast.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.fast.locker.utils.ImageUtils;
import cn.zmdx.kaka.fast.locker.wallpaper.PandoraWallpaperManager.IWallpaperClickListener;
import cn.zmdx.kaka.fast.locker.wallpaper.PandoraWallpaperManager.PandoraWallpaper;
import cn.zmdx.kaka.fast.locker.wallpaper.WallpaperUtils.ILoadBitmapCallback;
import cn.zmdx.kaka.fast.locker.R;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.request.JsonObjectRequest;

@SuppressLint("InflateParams")
public class OnlineWallpaperManager {

    private static String URL = UrlBuilder.getBaseUrl() + "queryWallPaper.action";

    public static String ONLINE_WALLPAPER_SDCARD_LOCATION = Environment
            .getExternalStorageDirectory().getPath() + "/.Fast/onlineWallpaper/background/";

    public String getFilePath(String fileName) {
        return ONLINE_WALLPAPER_SDCARD_LOCATION + fileName + ".jpg";
    }

    public static int MAX_ONLINE_PAPER_COUNT_LIMIT = 12;

    private static OnlineWallpaperManager mInstance;

    public static OnlineWallpaperManager getInstance() {
        if (null == mInstance) {
            mInstance = new OnlineWallpaperManager();
        }
        return mInstance;
    }

    public void saveCurrentWallpaperFileName(Context mContext, String fileName) {
        PandoraConfig.newInstance(mContext).saveCurrentWallpaperFileName(fileName);
    }

    public String getCurrentWallpaperFileName(Context mContext) {
        return PandoraConfig.newInstance(mContext).getCurrentWallpaperFileName();
    }

    public void saveThemeId(Context mContext, int themeId) {
        PandoraConfig.newInstance(mContext).saveThemeId(themeId);
    }

    public void saveOnlineWallpaperFile(final String fileName, final Bitmap bitmap) {
        HDBThreadUtils.runOnWorker(new Runnable() {

            @Override
            public void run() {
                ImageUtils.saveImageToFile(bitmap, getFilePath(fileName));
            }
        });
    }

    public void mkDirs() {
        File dir = new File(ONLINE_WALLPAPER_SDCARD_LOCATION);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void pullWallpaperFromServer(Listener<JSONObject> listener, ErrorListener errorListener) {
        JsonObjectRequest request = null;
        request = new JsonObjectRequest(URL, null, listener, errorListener);
        request.setShouldCache(false);
        RequestManager.getRequestQueue().add(request);
    }

    public boolean isHaveOnlineWallpaper() {
        return PandoraUtils.isHaveFile(ONLINE_WALLPAPER_SDCARD_LOCATION);
    }

    public void setOnlineWallpaperList(Context mContext, ViewGroup mOnlineContainer,
            final IWallpaperClickListener listener, List<PandoraWallpaper> pWallpaperList) {
        if (isHaveOnlineWallpaper()) {
            List<OnlineWallpaper> wallpaperList = getOnlineWallpaper(mContext);
            for (OnlineWallpaper list : wallpaperList) {
                String fileName = list.getFileName();
                boolean isCurrentTheme = list.isCurrentTheme();
                setOnlineWallpaperItem(mContext, mOnlineContainer, fileName, isCurrentTheme,
                        listener, pWallpaperList);
            }
        }
    }

    public void setOnlineWallpaperItem(Context mContext, final ViewGroup mOnlineContainer,
            final String fileName, final boolean isCurrentTheme,
            final IWallpaperClickListener listener, List<PandoraWallpaper> pWallpaperList) {
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
                    listener.onClickListener(fileName, getFilePath(fileName), false);
                }
            }
        });
        mWallpaperDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (null != listener) {
                    mOnlineContainer.removeView(mWallpaperRl);
                    listener.onDelClickListener(fileName, getFilePath(fileName));
                }
            }
        });
        mOnlineContainer.addView(mWallpaperRl, Math.min(0, mOnlineContainer.getChildCount()));

        SparseIntArray sparseIntArray = WallpaperUtils.initWallpaperSize(mContext);
        int layoutWidth = sparseIntArray.get(WallpaperUtils.KEY_LAYOUT_WIDTH);
        int layoutHeight = sparseIntArray.get(WallpaperUtils.KEY_LAYOUT_HEIGHT);
        int imageWidth = sparseIntArray.get(WallpaperUtils.KEY_IMAGE_WIDTH);
        int imageHeight = sparseIntArray.get(WallpaperUtils.KEY_IMAGE_HEIGHT);
        
        int delPadding = (layoutWidth - imageWidth) / 2 - 10;
        mWallpaperDel.setPadding(0, delPadding, delPadding, 0);
        mWallpaperSelect.setPadding(0, delPadding, delPadding, 0);

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
        pWallpaper.setDeleteView(mWallpaperDel);
        pWallpaper.setDeleteLayout(mWallpaperDelLayout);
        pWallpaper.setFileName(fileName);
        pWallpaperList.add(pWallpaper);

    }

    /**
     * 获取本地存储的网路壁纸数据
     * 
     * @return
     */
    private List<OnlineWallpaper> getOnlineWallpaper(Context context) {
        int currentThemeId = ThemeManager.getCurrentThemeId();
        String currentThemeFileName = null;
        if (currentThemeId == ThemeManager.THEME_ID_ONLINE) {
            currentThemeFileName = PandoraConfig.newInstance(context).getCurrentWallpaperFileName();
        }
        List<OnlineWallpaper> list = new ArrayList<OnlineWallpaper>();
        File file = new File(ONLINE_WALLPAPER_SDCARD_LOCATION);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File filePos = files[i];
            String fileName = filePos.getName().substring(0, filePos.getName().indexOf("."));
            OnlineWallpaper onlineWallpaper = new OnlineWallpaper();
            onlineWallpaper.setFilePath(filePos.getPath());
            onlineWallpaper.setFileName(fileName);
            onlineWallpaper.setLastModified(filePos.lastModified());
            if (currentThemeId == ThemeManager.THEME_ID_ONLINE) {
                if (currentThemeFileName.equals(fileName)) {
                    onlineWallpaper.setCurrentTheme(true);
                }
            }
            list.add(onlineWallpaper);
        }
        Collections.sort(list, comparator);
        if (list.size() > MAX_ONLINE_PAPER_COUNT_LIMIT) {
            List<OnlineWallpaper> needDelList = new ArrayList<OnlineWallpaper>();
            for (int i = 0; i < list.size() - MAX_ONLINE_PAPER_COUNT_LIMIT; i++) {
                OnlineWallpaper onlineWallpaper = list.get(i);
                needDelList.add(onlineWallpaper);
            }
            list.removeAll(needDelList);
            deleteFile(needDelList);
        }
        return list;
    }

    private void deleteFile(final List<OnlineWallpaper> needDelList) {
        HDBThreadUtils.runOnWorker(new Runnable() {

            @Override
            public void run() {
                for (OnlineWallpaper wallpaper : needDelList) {
                    FileHelper.deleteFile(new File(wallpaper.mFilePath));
                }
            }
        });
    }

    public static Comparator<OnlineWallpaper> comparator = new Comparator<OnlineWallpaper>() {
        @Override
        public int compare(OnlineWallpaper object1, OnlineWallpaper object2) {
            return (object1.getLastModified() - object2.getLastModified()) > 0 ? 1 : -1;
        }
    };

    public static final class OnlineWallpaper {

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
