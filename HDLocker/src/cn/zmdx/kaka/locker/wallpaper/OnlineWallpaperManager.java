
package cn.zmdx.kaka.locker.wallpaper;

import java.io.File;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.network.DownloadRequest;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.request.JsonObjectRequest;

public class OnlineWallpaperManager {
    public static String ONLINE_WALLPAPER_SDCARD_LOCATION = Environment
            .getExternalStorageDirectory().getPath() + "/.Pandora/onlineWallpaper/background/";

    public String getOnlineWallpaperFilePath(String fileName) {
        return ONLINE_WALLPAPER_SDCARD_LOCATION + fileName + ".jpg";
    }

    private static OnlineWallpaperManager mInstance;

    public static OnlineWallpaperManager getInstance() {
        if (null == mInstance) {
            mInstance = new OnlineWallpaperManager();
        }
        return mInstance;
    }

    private DownloadRequest mRequest;

    public void saveOnlineWallpaperFileName(Context mContext, String fileName) {
        PandoraConfig.newInstance(mContext).saveOnlineWallpaperFileName(fileName);
    }

    public String getOnlineWallpaperFileName(Context mContext) {
        return PandoraConfig.newInstance(mContext).getOnlineWallpaperFileName();
    }

    public void saveThemeId(Context mContext, int themeId) {
        PandoraConfig.newInstance(mContext).saveThemeId(themeId);
    }

    public void downloadImage(String url, String fileName, Listener<String> listener,
            ErrorListener errorListener) {
        if (null != mRequest && !mRequest.isCanceled()) {
            mRequest.cancel();
        }
        mRequest = new DownloadRequest(url, getOnlineWallpaperFilePath(fileName), listener,
                errorListener);
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

    public void deleteFile(String fileName) {
        File file = new File(getOnlineWallpaperFilePath(fileName));
        if (!file.exists()) {
            file.mkdirs();
        }
        PandoraUtils.deleteFile(file);
    }

    public void pullWallpaperFromServer(Listener<JSONObject> listener, ErrorListener errorListener) {
        JsonObjectRequest request = null;
        request = new JsonObjectRequest("", null, listener, errorListener);
        RequestManager.getRequestQueue().add(request);
    }

    public static final class OnlineWallpaper {
        private int mId;

        private Bitmap mBitmap;

        private View mSelectView;

        private String mUrl;

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

    }
}
