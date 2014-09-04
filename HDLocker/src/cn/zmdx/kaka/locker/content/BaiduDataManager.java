
package cn.zmdx.kaka.locker.content;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.content.Context;
import android.os.Message;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class BaiduDataManager {

    private String mBaseUrl = "http://image.baidu.com/channel/listjson?";

    private static final int REQUEST_PAGE_COUNT_DEFAULT = 5;

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
            request = new JsonObjectRequest(getUrl(tag1, REQUEST_PAGE_COUNT_DEFAULT), null,
                    new Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            final BaiduData bd = parse(response);
                            Message msg = new Message();
                            msg.what = PandoraBoxDispatcher.MSG_BAIDU_DATA_ARRIVED;
                            msg.obj = bd;
                            PandoraBoxDispatcher.getInstance().sendMessage(msg);
                        }

                    }, new ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
        }
        mQueue.add(request);
    }

    //将从百度服务器拉取的json解析为我们自己的实体对象
    public BaiduData parse(JSONObject jsonObj) {
        // TODO
        return null;
    }

    /**
     * 创建url，默认每页的数据条数为31条，且tag2即二级分类为"所有"
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
        StringBuilder sb = new StringBuilder(getBaseUrl());
        sb.append("pn=" + pageNum);
        sb.append("rn=" + pageCount);
        sb.append("&tag1=" + BaiduTagMapping.getStringTag1(tag1));
        sb.append("&tag2=" + BaiduTagMapping.getStringTag2(tag2));
        sb.append("&ie=utf8");
        try {
            return URLEncoder.encode(sb.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            // never execute
            return null;
        }
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public static class BaiduData implements IData{

        //TODO
        //id
        //baidu_id
        //url
        //...
        //get,set方法
        @Override
        public boolean saveToDatabase() {
            // TODO Auto-generated method stub
            return false;
        }
    }
}
