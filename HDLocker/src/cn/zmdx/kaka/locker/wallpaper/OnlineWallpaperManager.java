
package cn.zmdx.kaka.locker.wallpaper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.network.DownloadRequest;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.wallpaper.PandoraWallpaperManager.IWallpaperClickListener;
import cn.zmdx.kaka.locker.wallpaper.PandoraWallpaperManager.PandoraWallpaper;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.request.JsonObjectRequest;

@SuppressLint("InflateParams")
public class OnlineWallpaperManager {
    public static String ONLINE_WALLPAPER_SDCARD_LOCATION = Environment
            .getExternalStorageDirectory().getPath() + "/.Pandora/onlineWallpaper/background/";

    public static String ONLINE_WALLPAPER_TMP_SDCARD_LOCATION = Environment
            .getExternalStorageDirectory().getPath() + "/.Pandora/onlineWallpaper/tmp/";

    public String getFilePath(String fileName) {
        return ONLINE_WALLPAPER_SDCARD_LOCATION + fileName + ".jpg";
    }

    public String getTmpFilePath(String fileName) {
        return ONLINE_WALLPAPER_TMP_SDCARD_LOCATION + fileName + ".jpg";
    }

    private static OnlineWallpaperManager mInstance;

    public static OnlineWallpaperManager getInstance() {
        if (null == mInstance) {
            mInstance = new OnlineWallpaperManager();
        }
        return mInstance;
    }

    private DownloadRequest mRequest;

    public void saveCurrentWallpaperFileName(Context mContext, String fileName) {
        PandoraConfig.newInstance(mContext).saveCurrentWallpaperFileName(fileName);
    }

    public String getCurrentWallpaperFileName(Context mContext) {
        return PandoraConfig.newInstance(mContext).getCurrentWallpaperFileName();
    }

    public void saveThemeId(Context mContext, int themeId) {
        PandoraConfig.newInstance(mContext).saveThemeId(themeId);
    }

    public void downloadImage(String url, String fileName, Listener<String> listener,
            ErrorListener errorListener) {
        if (null != mRequest && !mRequest.isCanceled()) {
            mRequest.cancel();
        }
        mRequest = new DownloadRequest(url, getTmpFilePath(fileName), listener, errorListener);
        RequestManager.getRequestQueue().add(mRequest);
    }

    public void saveOnlineWallpaperFile(final String fileName, final Bitmap bitmap) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                PandoraUtils.saveBitmap(bitmap, ONLINE_WALLPAPER_SDCARD_LOCATION, fileName);
            }
        }).start();
    }

    // public void deleteFile(String fileName) {
    // File file = new File(getFilePath(fileName));
    // if (!file.exists()) {
    // file.mkdirs();
    // }
    // PandoraUtils.deleteFile(file);
    // }
    public void mkDirs() {
        File tmpDir = new File(ONLINE_WALLPAPER_TMP_SDCARD_LOCATION);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        File dir = new File(ONLINE_WALLPAPER_SDCARD_LOCATION);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void clearTmpFolderFile() {
        File dir = new File(ONLINE_WALLPAPER_TMP_SDCARD_LOCATION);
        PandoraUtils.clearFolderFiles(dir);
    }

    public boolean renameFile(String fileName) {
        File oleFile = new File(getTmpFilePath(fileName));
        File newFile = new File(getFilePath(fileName));
        return oleFile.renameTo(newFile);
    }

    public void pullWallpaperFromServer(Listener<JSONObject> listener, ErrorListener errorListener) {
        JsonObjectRequest request = null;
        request = new JsonObjectRequest("", null, listener, errorListener);
        RequestManager.getRequestQueue().add(request);
    }

    public boolean isHaveCustomWallpaper() {
        return PandoraUtils.isHaveFile(ONLINE_WALLPAPER_SDCARD_LOCATION);
    }

    public void setOnlineWallpaperList(Context mContext, ViewGroup mOnlineContainer,
            final IWallpaperClickListener listener, List<PandoraWallpaper> pWallpaperList) {
        if (isHaveCustomWallpaper()) {
            Log.d("syc", "one 0  " + System.currentTimeMillis());
            List<OnlineWallpaper> wallpaperList = getOnlineWallpaper(mContext);
            Log.d("syc", "one 1  " + System.currentTimeMillis());
            for (int i = 0; i < wallpaperList.size(); i++) {
                Log.d("syc", "one 2  " + System.currentTimeMillis());
                String fileName = wallpaperList.get(i).getFileName();
                boolean isCurrentTheme = wallpaperList.get(i).isCurrentTheme();
                Log.d("syc", "one 3  " + System.currentTimeMillis());
                setOnlineWallpaperItem(mContext, mOnlineContainer, fileName, isCurrentTheme,
                        listener, pWallpaperList);
            }
            Log.d("syc", "one 4  " + System.currentTimeMillis());
        }
    }

    public void setOnlineWallpaperItem(Context mContext, final ViewGroup mOnlineContainer,
            final String fileName, final boolean isCurrentTheme,
            final IWallpaperClickListener listener, List<PandoraWallpaper> pWallpaperList) {
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
        PandoraUtils.loadBitmap(mContext, fileName, mWallpaperIv, false);
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
                    UmengCustomEventManager.statisticalSelectTheme(ThemeManager.THEME_ID_ONLINE);
                    mOnlineContainer.removeView(mWallpaperRl);
                    listener.onDelClickListener(fileName, getFilePath(fileName));
                }
            }
        });
        mOnlineContainer.addView(mWallpaperRl, Math.min(1, mOnlineContainer.getChildCount()));
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

    /**
     * 获取本地存储的网路壁纸数据
     * 
     * @return
     */
    private List<OnlineWallpaper> getOnlineWallpaper(Context context) {
        int currentThemeId = ThemeManager.getCurrentTheme().getmThemeId();
        String currentThemeFileName = null;
        if (currentThemeId == ThemeManager.THEME_ID_ONLINE) {
            currentThemeFileName = PandoraConfig.newInstance(context).getCurrentWallpaperFileName();
        }
        List<OnlineWallpaper> list = new ArrayList<OnlineWallpaper>();
        File file = new File(ONLINE_WALLPAPER_SDCARD_LOCATION);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName().substring(0, files[i].getName().indexOf("."));
            Log.d("syc", "fileName=" + fileName);
            OnlineWallpaper onlineWallpaper = new OnlineWallpaper();
            onlineWallpaper.setFilePath(files[i].getPath());
            onlineWallpaper.setFileName(fileName);
            if (currentThemeId == ThemeManager.THEME_ID_ONLINE) {
                if (currentThemeFileName.equals(fileName)) {
                    onlineWallpaper.setCurrentTheme(true);
                }
            }
            list.add(onlineWallpaper);
        }
        return list;
    }

    public static final class OnlineWallpaper {
        private int mId;

        private Bitmap mBitmap;

        private View mSelectView;

        private String mUrl;

        private String mFilePath;

        private String mFileName;

        private String mExt;

        private String mFullName;

        private boolean isCurrentTheme = false;

        public int getId() {
            return mId;
        }

        public void setId(int mId) {
            this.mId = mId;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public void setBitmap(Bitmap mBitmap) {
            this.mBitmap = mBitmap;
        }

        public View getSelectView() {
            return mSelectView;
        }

        public void setSelectView(View mSelectView) {
            this.mSelectView = mSelectView;
        }

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String mUrl) {
            this.mUrl = mUrl;
        }

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

        public String getExt() {
            return mExt;
        }

        public void setExt(String ext) {
            this.mExt = ext;
        }

        public String getFullName() {
            return mFullName;
        }

        public void setFullName(String mFullName) {
            this.mFullName = mFullName;
        }

    }

}
