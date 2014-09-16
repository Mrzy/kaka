
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Message;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.content.ServerDataManager.ServerData;
import cn.zmdx.kaka.locker.database.DatabaseModel;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

public class ServerImageDataManager {

    private String mBaseUrl = "http://192.168.1.103:8080/pandora/locker!queryDataImgTable.action?";

    public void pullServerImageData(int limit, String dataType, String webSite) {
        JsonObjectRequest request = null;
        request = new JsonObjectRequest(getUrl(limit, dataType, webSite), null,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        final List<ServerImageData> sdList = ServerImageData.parseJson(response);
                        Message msg = Message.obtain();
                        msg.what = PandoraBoxDispatcher.MSG_SERVER_IMAGE_DATA_ARRIVED;
                        msg.obj = sdList;
                        PandoraBoxDispatcher.getInstance().sendMessage(msg);
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO
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
        return mBaseUrl;
    }

    public static class ServerImageData extends BaseDataManager {

        private String mUrl;

        private String mImageUrl;

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String mUrl) {
            this.mUrl = mUrl;
        }

        public String getImageUrl() {
            return mImageUrl;
        }

        public void setImageUrl(String mImageUrl) {
            this.mImageUrl = mImageUrl;
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
                    serverImageData.setImageUrl(imgUrl);
                    sdList.add(serverImageData);
                }
            }
            return sdList;
        }

        public static void saveToDatabase(List<ServerImageData> sidList) {
            DatabaseModel.getInstance().saveServerImageData(sidList);
        }

    }

}
