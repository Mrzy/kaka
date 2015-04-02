
package cn.zmdx.kaka.locker.wallpaper;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.network.UrlBuilder;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

@SuppressLint("InflateParams")
public class OnlineWallpaperManager {

    private static String URL = UrlBuilder.getBaseUrl() + "locker!queryWallPaperNew.action";

    public static String ONLINE_WALLPAPER_SDCARD_LOCATION = Environment
            .getExternalStorageDirectory().getPath() + "/.Pandora/onlineWallpaper/background/";

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

    public interface IPullWallpaperListener {
        void onSuccecc(List<ServerOnlineWallpaper> list);

        void onFail();
    }

    /**
     * 获取壁纸信息
     * 
     * @param context
     * @param listener
     */
    public void pullWallpaperData(Context context, IPullWallpaperListener listener, long flag,
            long lastModified) {
        final String lastPullJson = PandoraConfig.newInstance(context)
                .getLastOnlineServerJsonData();
        if (HDBNetworkState.isWifiNetwork()
                || ((HDBNetworkState.isNetworkAvailable() && !HDBNetworkState.isWifiNetwork()) && !PandoraConfig
                        .newInstance(context).isOnlyWifiLoadImage())) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("满足获取数据条件，获取网路壁纸数据中...");
            }
            getWallpaperFromServer(listener, lastPullJson, flag, lastModified);
        } else {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("不满足获取数据条件，获取缓存壁纸数据中...");
            }
            parseWallpaperJson(lastPullJson, listener);
        }
    }

    /**
     * 解析壁纸数据Json
     * 
     * @param lastPullJson
     * @param listener
     */
    private void parseWallpaperJson(String lastPullJson, IPullWallpaperListener listener) {
        try {
            List<ServerOnlineWallpaper> list = ServerOnlineWallpaperManager
                    .parseJson(new JSONObject(lastPullJson));
            listener.onSuccecc(list);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFail();
        }
    }

    /**
     * 从服务器获取壁纸数据
     * 
     * @param listener
     * @param lastPullJson
     */
    public void getWallpaperFromServer(final IPullWallpaperListener listener,
            final String lastPullJson, long flag, long lastModified) {
        JsonObjectRequest request = null;
        request = new JsonObjectRequest(URL + getParam(flag, lastModified), null,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        parseWallpaperJson(response.toString(), listener);
                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (!TextUtils.isEmpty(lastPullJson)) {
                            parseWallpaperJson(lastPullJson, listener);
                        } else {
                            listener.onFail();
                        }
                    }
                });
        request.setShouldCache(!BuildConfig.DEBUG);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * @param flag 0代表刷新，即获取最新的 1代表加载更多，即获取老的
     * @param lastModified
     * @return
     */
    private String getParam(long flag, long lastModified) {
        boolean isDebug = BuildConfig.DEBUG ? true : false;
        return "?flag=" + flag + "&lastModified=" + lastModified + "&isDebug=" + isDebug;
    }

    public boolean isHaveOnlineWallpaper() {
        return PandoraUtils.isHaveFile(ONLINE_WALLPAPER_SDCARD_LOCATION);
    }

}
