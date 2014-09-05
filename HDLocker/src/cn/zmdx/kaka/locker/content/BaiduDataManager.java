
package cn.zmdx.kaka.locker.content;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.database.DatabaseModel;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBLOG;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class BaiduDataManager {

    private String mBaseUrl = "http://image.baidu.com/channel/listjson?";

    private static final int REQUEST_PAGE_COUNT_DEFAULT = 10;

    private static final int REQUEST_COUNT_PER_PAGE = 30;

    private RequestQueue mQueue;

    public BaiduDataManager(Context context) {
        mQueue = Volley.newRequestQueue(context);
    }

    /**
     * 从百度服务器拉取这5个频道的数据，每个频道请求5次(每页一次，一页31条数据)，执行此方法后，一共获得数据31*5*5=775条
     */
    public void pullAllFunnyData() {
        pullFunnyDataByTag1(BaiduTagMapping.INT_TAG1_BIZHI);
        pullFunnyDataByTag1(BaiduTagMapping.INT_TAG1_GAOXIAO);
        pullFunnyDataByTag1(BaiduTagMapping.INT_TAG1_MEINV);
        pullFunnyDataByTag1(BaiduTagMapping.INT_TAG1_MINGXING);
        pullFunnyDataByTag1(BaiduTagMapping.INT_TAG1_SHEYING);
    }

    public void pullFunnyDataByTag1(final int tag1) {
        JsonObjectRequest request = null;

        for (int i = 0; i < REQUEST_PAGE_COUNT_DEFAULT; i++) {
            request = new JsonObjectRequest(getUrl(tag1, i), null, new Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    final List<BaiduData> bdList = BaiduData.parseJson(response);
                    Message msg = Message.obtain();
                    msg.what = PandoraBoxDispatcher.MSG_BAIDU_DATA_ARRIVED;
                    msg.obj = bdList;
                    PandoraBoxDispatcher.getInstance().sendMessage(msg);
                }

            }, new ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            mQueue.add(request);
        }

    }

    /**
     * 创建url，默认每页的数据条数为31条，且tag2即二级分类为"所有"
     * 
     * @param tag1 大分类
     * @param pageNum 页号
     * @return
     */
    public String getUrl(int tag1, int pageNum) {
        return getUrl(tag1, BaiduTagMapping.INT_TAG2_ALL, pageNum, REQUEST_COUNT_PER_PAGE);
    }

    /*
     * pn是请求的页号 rn是每页的数据条数
     */
    public String getUrl(int tag1, int tag2, int pageNum, int pageCount) {
        try {
            StringBuilder sb = new StringBuilder(getBaseUrl());
            sb.append("pn=" + pageNum);
            sb.append("&rn=" + pageCount);
            sb.append("&tag1=" + URLEncoder.encode(BaiduTagMapping.getStringTag1(tag1), "utf-8"));
            sb.append("&tag2=" + URLEncoder.encode(BaiduTagMapping.getStringTag2(tag2), "utf-8"));
            sb.append("&ie=utf8");
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            // never execute
            return null;
        }
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public static class BaiduData implements IData {

        public String mId;

        public String mBaiduId;

        public String mDescribe;

        public String mImageUrl;

        public int mImageWidth;

        public int mImageHeight;

        public String mTthumbLargeUrl;

        public int mThumbLargeWidth;

        public int mThumbLargeHeight;

        public String mTag1;

        public String mTag2;

        // 将从百度服务器拉取的json解析为我们自己的实体对象
        public static List<BaiduData> parseJson(JSONObject jsonObj) {
            List<BaiduData> bdList = new ArrayList<BaiduData>();
            String tag1 = jsonObj.optString("tag1");
            String tag2 = jsonObj.optString("tag2");
            JSONArray jsonArray = jsonObj.optJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                String baiduId = jsonObject.optString("id");
                String describe = jsonObject.optString("desc");
                String image_url = jsonObject.optString("image_url");
                int image_width = jsonObject.optInt("image_width");
                int image_height = jsonObject.optInt("image_height");
                String thumb_large_url = jsonObject.optString("thumb_large_url");
                int thumb_large_width = jsonObject.optInt("thumb_large_width");
                int thumb_large_height = jsonObject.optInt("thumb_large_height");
                if (!TextUtils.isEmpty(image_url)) {
                    BaiduData bd = new BaiduData();
                    bd.setTag1(tag1);
                    bd.setTag2(tag2);
                    bd.setBaiduId(baiduId);
                    bd.setDescribe(describe);
                    bd.setImageUrl(image_url);
                    bd.setImageWidth(image_width);
                    bd.setImageHeight(image_height);
                    bd.setTthumbLargeUrl(thumb_large_url);
                    bd.setThumbLargeWidth(thumb_large_width);
                    bd.setThumbLargeHeight(thumb_large_height);
                    bdList.add(bd);
                    if (PandoraConfig.sDebug) {
                        HDBLOG.logD("tag1=" + "tag1" + " tag2=" + tag2 + "baiduId=" + baiduId
                                + " describe=" + describe + " image_url=" + image_url
                                + " image_width=" + image_width + " image_height=" + image_height
                                + " thumb_large_url=" + thumb_large_url + " thumb_large_width="
                                + thumb_large_width + " thumb_large_height=" + thumb_large_height);
                    }
                }
            }

            return bdList;
        }

        public static void saveToDatabase(List<BaiduData> bdList) {
            DatabaseModel.getInstance().saveBaiduData(bdList);
        }

        public String getId() {
            return mId;
        }

        public void setId(String mId) {
            this.mId = mId;
        }

        public String getBaiduId() {
            return mBaiduId;
        }

        public void setBaiduId(String mBaiduId) {
            this.mBaiduId = mBaiduId;
        }

        public String getDescribe() {
            return mDescribe;
        }

        public void setDescribe(String mDescribe) {
            this.mDescribe = mDescribe;
        }

        public String getImageUrl() {
            return mImageUrl;
        }

        public void setImageUrl(String mImageUrl) {
            this.mImageUrl = mImageUrl;
        }

        public int getImageWidth() {
            return mImageWidth;
        }

        public void setImageWidth(int mImageWidth) {
            this.mImageWidth = mImageWidth;
        }

        public int getImageHeight() {
            return mImageHeight;
        }

        public void setImageHeight(int mImageHeight) {
            this.mImageHeight = mImageHeight;
        }

        public String getTthumbLargeUrl() {
            return mTthumbLargeUrl;
        }

        public void setTthumbLargeUrl(String mTthumbLargeUrl) {
            this.mTthumbLargeUrl = mTthumbLargeUrl;
        }

        public int getThumbLargeWidth() {
            return mThumbLargeWidth;
        }

        public void setThumbLargeWidth(int mThumbLargeWidth) {
            this.mThumbLargeWidth = mThumbLargeWidth;
        }

        public int getThumbLargeHeight() {
            return mThumbLargeHeight;
        }

        public void setThumbLargeHeight(int mThumbLargeHeight) {
            this.mThumbLargeHeight = mThumbLargeHeight;
        }

        public String getTag1() {
            return mTag1;
        }

        public void setTag1(String mTag1) {
            this.mTag1 = mTag1;
        }

        public String getTag2() {
            return mTag2;
        }

        public void setTag2(String mTag2) {
            this.mTag2 = mTag2;
        }

    }

}
