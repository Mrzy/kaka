
package cn.zmdx.kaka.locker.content;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Message;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.cache.ImageCacheManager;
import cn.zmdx.kaka.locker.database.DatabaseModel;
import cn.zmdx.kaka.locker.network.DownloadRequest;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBLOG;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;

public class BaiduDataManager {

    private String mBaseUrl = "http://image.baidu.com/channel/listjson?";

    public BaiduDataManager() {
    }

    /**
     * 从百度服务器拉取这5个频道的数据，每个频道请求5次(每页一次，一页31条数据)，执行此方法后，一共获得数据31*5*5=775条
     */
    public void pullAllFunnyData() {
        int length = PandoraPolicy.BAIDU_IMAGE_MODULE.length;
        for (int i = 0; i < length; i++) {
            pullFunnyDataByTag1(PandoraPolicy.BAIDU_IMAGE_MODULE[i]);
        }
    }

    public void pullFunnyDataByTag1(final int tag1) {
        JsonObjectRequest request = null;

        for (int i = 0; i < PandoraPolicy.REQUEST_PAGE_COUNT_DEFAULT; i++) {
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
                    // TODO
                }
            });
            RequestManager.getRequestQueue().add(request);
        }
    }

    public void downloadImage(final BaiduData bd) {
        Request<String> request = new DownloadRequest(bd.mImageUrl, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("download image finished,path=" + response);
                }
                DatabaseModel.getInstance().markAlreadyDownload(bd.mId);
            }

        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode >= 500) {
                    // invalidate url
                    DiskImageHelper.remove(bd.mImageUrl);
                }
            }
        });
        RequestManager.getRequestQueue().add(request);
    }

    //暂废弃
    private void downloadBaiduImage(final BaiduData bd) {
        if (bd == null) {
            return;
        }

        String url = bd.mImageUrl;
        if (TextUtils.isEmpty(url)) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logE("url is empty, id:" + bd.mId);
            }
            return;
        }
        ImageCacheManager.getInstance().getImage(url, new ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("下载图片失败，" + error.toString());
                }
                // 若请求失败，此处认为
                if (error.networkResponse.statusCode >= 500) {
                    DatabaseModel.getInstance().deleteById(bd.mId);
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("下载图片时，服务器异常，认为图片已不能正常下载，所以删除本地库中的这条数据");
                    }
                }
            }

            @Override
            public void onResponse(ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    // update local db isDownload flag
                    DatabaseModel.getInstance().markAlreadyDownload(bd.mId);
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("下载图片请求的onResponse被调用，response.getBitmap()="
                                + response.getBitmap() + "更新数据库标记为已下载");
                    }
                }
            }
        });
    }

    public void batchDownloadBaiduImage(List<BaiduData> list) {
        int size = list.size();
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("开始批量下载百度图片程序，总数：" + size);
        }
        for (int i = 0; i < size; i++) {
            BaiduData bd = list.get(i);
            downloadImage(bd);
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
        return getUrl(tag1, BaiduTagMapping.INT_TAG2_ALL, pageNum, PandoraPolicy.REQUEST_COUNT_PER_PAGE);
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

//            if (BuildConfig.DEBUG) {
//                HDBLOG.logD("请求url：" + sb.toString());
//            }
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

        public int mId;

        public String mBaiduId;

        public String mDescribe;

        public String mImageUrl;

        public int mImageWidth;

        public int mImageHeight;

        public int mIsImageDownloaded;

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
                    bd.setIsImageDownloaded(DatabaseModel.DOWNLOAD_FALSE);
                    bd.setTthumbLargeUrl(thumb_large_url);
                    bd.setThumbLargeWidth(thumb_large_width);
                    bd.setThumbLargeHeight(thumb_large_height);
                    bdList.add(bd);
//                    if (PandoraConfig.sDebug) {
//                        HDBLOG.logD("tag1=" + tag1 + " tag2=" + tag2 + "baiduId=" + baiduId
//                                + " describe=" + describe + " image_url=" + image_url
//                                + " image_width=" + image_width + " image_height=" + image_height
//                                + " thumb_large_url=" + thumb_large_url + " thumb_large_width="
//                                + thumb_large_width + " thumb_large_height=" + thumb_large_height);
//                    }
                }
            }

            return bdList;
        }

        public static void saveToDatabase(List<BaiduData> bdList) {
            DatabaseModel.getInstance().saveBaiduData(bdList);
        }

        public int getId() {
            return mId;
        }

        public void setId(int mId) {
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

        /*
         * return: 0:未被下载。1：已经下载
         */
        public int isImageDownloaded() {
            return mIsImageDownloaded;
        }

        public void setIsImageDownloaded(int mIsImageDownloaded) {
            this.mIsImageDownloaded = mIsImageDownloaded;
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
