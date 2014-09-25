
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Message;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.database.ServerDataModel;
import cn.zmdx.kaka.locker.utils.HDBLOG;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class ServerDataManager {

    private static ServerDataManager INSTANCE;
    
    private ServerDataManager() {
    }

    public static ServerDataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerDataManager();
        }
        return INSTANCE;
    }
    public void pullServerData(int limit, String dataType, String webSite) {
        JsonObjectRequest request = null;
        request = new JsonObjectRequest(getUrl(limit, dataType, webSite), null,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        final List<ServerData> sdList = ServerData.parseJson(response);
                        if (sdList.size() <= 0) {
                            if (BuildConfig.DEBUG) {
                                HDBLOG.logD("拉取文本数据正常返回，但无数据");
                            }
                            return;
                        }
                        Message msg = Message.obtain();
                        msg.what = PandoraBoxDispatcher.MSG_SERVER_DATA_ARRIVED;
                        msg.obj = sdList;
                        PandoraBoxDispatcher.getInstance().sendMessage(msg);
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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
        if (BuildConfig.DEBUG) {
            return "http://182.254.159.63:8080/pandora/locker!queryDataTable.action?";
        } else {
            return "http://182.254.214.26:8080/pandora/locker!queryDataTable.action?";
        }
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
                }
            }
            return sdList;
        }

        public static void saveToDatabase(List<ServerData> sdList) {
            ServerDataModel.getInstance().saveServerData(sdList);
        }
    }
}
