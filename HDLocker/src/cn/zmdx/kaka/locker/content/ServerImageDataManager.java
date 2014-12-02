
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Message;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.database.MySqlitDatabase;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.network.ImageDownloadRequest;
import cn.zmdx.kaka.locker.network.UrlBuilder;
import cn.zmdx.kaka.locker.utils.HDBLOG;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

public class ServerImageDataManager {

    private static ServerImageDataManager INSTANCE;

    private ServerImageDataManager() {
    }

    public static ServerImageDataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerImageDataManager();
        }
        return INSTANCE;
    }

    public void downloadImage(final ServerImageData bd) {
        Request<String> request = new ImageDownloadRequest(bd.mUrl, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("download image finished,path=" + response);
                }
                ServerImageDataModel.getInstance().markAlreadyDownload(bd.mId);
            }

        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // invalidate url
                DiskImageHelper.remove(bd.mUrl);
            }
        });
        RequestManager.getRequestQueue().add(request);
    }

    public void batchDownloadServerImage(List<ServerImageData> list) {
        int size = list.size();
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("开始批量下载pandora server图片程序，总数：" + size);
        }
        for (int i = 0; i < size; i++) {
            ServerImageData bd = list.get(i);
            downloadImage(bd);
        }
    }

    /**
     * 拉取今日数据，不加参数，数据数量和类型有服务端决定
     */
    public void pullTodayData(long lastModified) {
        JsonObjectRequest request = null;
        request = new JsonObjectRequest(getUrl(lastModified), null, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                final List<ServerImageData> sdList = ServerImageData.parseJson(response);
                if (sdList.size() <= 0) {
                    return;
                }
                Message msg = Message.obtain();
                msg.what = PandoraBoxDispatcher.MSG_ORIGINAL_DATA_ARRIVED;
                msg.obj = sdList;
                PandoraBoxDispatcher.getInstance().sendMessage(msg);
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (BuildConfig.DEBUG) {
                    error.printStackTrace();
                }
            }
        });
        RequestManager.getRequestQueue().add(request);
    }

    public String getUrl(long lastModified) {
        StringBuilder sb = new StringBuilder(UrlBuilder.getBaseUrl());
        sb.append("queryDataImgTable.action?");
        sb.append("lastModified=" + lastModified);
        return sb.toString();
    }

    public static class ServerImageData extends BaseDataManager {

        /**
         * 图片的原地址
         */
        private String mUrl;

        /**
         * 图片的自己服务器地址
         */
        private String mImageDesc;

        public int mIsImageDownloaded;

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String mUrl) {
            this.mUrl = mUrl;
        }

        public String getImageDesc() {
            return mImageDesc;
        }

        public void setImageDesc(String mImageDesc) {
            this.mImageDesc = mImageDesc;
        }

        public int isImageDownloaded() {
            return mIsImageDownloaded;
        }

        public void setIsImageDownloaded(int mIsImageDownloaded) {
            this.mIsImageDownloaded = mIsImageDownloaded;
        }

        public static List<ServerImageData> parseJson(JSONObject jsonObj) {
            List<ServerImageData> sdList = new ArrayList<ServerImageData>();
            String state = jsonObj.optString("state");
            if (state.equals("success")) {
                JSONArray jsonArray = jsonObj.optJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    ServerImageData serverImageData = new ServerImageData();
                    serverImageData.parseBaseJson(jsonObject);
                    String url = jsonObject.optString("url");
                    String imgUrl = jsonObject.optString("imgUrl");
                    serverImageData.setUrl(url);
                    serverImageData.setImageDesc(imgUrl);
                    String dataType = serverImageData.getDataType();
                    if (!TextUtils.isEmpty(dataType)
                            && dataType.equals(ServerDataMapping.S_DATATYPE_HTML)) {
                        serverImageData.setIsImageDownloaded(MySqlitDatabase.DOWNLOAD_TRUE);
                    } else {
                        serverImageData.setIsImageDownloaded(MySqlitDatabase.DOWNLOAD_FALSE);
                    }
                    sdList.add(serverImageData);
                }
            }
            return sdList;
        }

        public static void saveToDatabase(List<ServerImageData> sidList) {
            ServerImageDataModel.getInstance().saveServerImageData(sidList);
        }

    }
}
