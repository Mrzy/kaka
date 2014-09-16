
package cn.zmdx.kaka.locker.content;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.cache.ImageCacheManager;
import cn.zmdx.kaka.locker.content.BaiduDataManager.BaiduData;
import cn.zmdx.kaka.locker.database.DatabaseModel;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.utils.HDBLOG;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;

public class ServerDataManager {

    private String mBaseUrl = "http://192.168.1.103:8080/pandora/locker!queryDataTable.action?";

    public void pullServerData(int limit, String dataType, String webSite) {
        JsonObjectRequest request = null;
        request = new JsonObjectRequest(getUrl(limit, dataType, webSite), null,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        final List<ServerData> bdList = ServerData.parseJson(response);
                        // Message msg = Message.obtain();
                        // msg.what =
                        // PandoraBoxDispatcher.MSG_BAIDU_DATA_ARRIVED;
                        // msg.obj = bdList;
                        // PandoraBoxDispatcher.getInstance().sendMessage(msg);
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO
                        error.printStackTrace();
                        Log.d("syc", "error=" + error.getMessage());
                    }
                });
        RequestManager.getRequestQueue().add(request);

    }

    /**
     * 
     */
    public String getUrl(int limit, String dataType, String webSite) {
        StringBuilder sb = new StringBuilder(getBaseUrl());
        sb.append("limit=" + limit);
        sb.append("&dataType=" + dataType);
        sb.append("&webSite=" + webSite);
        return sb.toString();
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public static class ServerData extends BaseDataManager {

        private String mContent;

        public String getContent() {
            return mContent;
        }

        public void setContent(String mContent) {
            this.mContent = mContent;
        }

        public static List<ServerData> parseJson(JSONObject jsonObj) {
            List<ServerData> sdList = new ArrayList<ServerData>();
            String state = jsonObj.optString("state");
            if (state.equals("success")) {
                JSONArray jsonArray = jsonObj.optJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    ServerData serverData = new ServerData();
                    serverData.parseBaseJson(jsonObject);
                    String content = jsonObject.optString("content");
                    serverData.setContent(content);
                    sdList.add(serverData);
                    Log.d("syc", "id" + serverData.getCloudId() + "content=" + content);
                }
            }
            return sdList;
        }

        public static void saveToDatabase(List<ServerData> sdList) {
            // DatabaseModel.getInstance().saveServerData(sdList);
        }

    }

}
