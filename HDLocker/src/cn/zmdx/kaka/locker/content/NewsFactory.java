
package cn.zmdx.kaka.locker.content;

import java.util.List;

import org.json.JSONObject;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.network.UrlBuilder;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

public class NewsFactory {

    public static final int NEWS_TYPE_HEADLINE = 1;

    public static final int NEWS_TYPE_GOSSIP = 2;

    public static final int NEWS_TYPE_MICRO_CHOICE = 3;

    public static final int NEWS_TYPE_BEAUTY = 4;

    public static final int NEWS_TYPE_JOKE = 5;

    // public static final String TMPURL =
    // "http://192.168.1.111:8080/pandora/locker!queryDataImgTableNew.action?";

    private static boolean mLoadingOlder = false;

    /**
     * @param type 新闻类型
     * @param adapter
     * @param data 已经加载的数据集
     * @param srl 下拉刷新的控件，方便控制显示状态
     * @param older 是要显示更老的数据还是更新的数据
     */
    static void updateNews(int type, final RecyclerView.Adapter adapter,
            final List<ServerImageData> data, final SwipeRefreshLayout srl, final boolean older) {
        if (adapter == null || data == null) {
            throw new NullPointerException();
        }

        if (srl != null && !older) {
            srl.setRefreshing(true);
        }

        if (mLoadingOlder) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("正在加载更早的数据，中断此次请求");
            }
            return;
        }
        if (older) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("开始加载更早的数据");
            }
            mLoadingOlder = true;
        }
        if (!HDBNetworkState.isNetworkAvailable()) {
            srl.setRefreshing(false);
            mLoadingOlder = false;
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("无网络，中断请求新闻数据");
            }
            return;
        }

        String lastModified = getLastModified(data, older);
        JsonObjectRequest request = null;
        final String url = getUrl(type, lastModified, older);
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("加载新闻url:" + url);
        }
        request = new JsonObjectRequest(url, null, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                final List<ServerImageData> newData = ServerImageData.parseJson(response);
                if (older) {
                    data.addAll(newData);
                } else {
                    data.addAll(0, newData);
                }

                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("请求新闻数据成功，本次返回：" + newData.size() + ",条，共" + data.size() + "条新闻");
                }

                adapter.notifyDataSetChanged();
                if (srl != null) {
                    srl.setRefreshing(false);
                }
                mLoadingOlder = false;
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (BuildConfig.DEBUG) {
                    error.printStackTrace();
                }
                if (srl != null) {
                    srl.setRefreshing(false);
                }
                mLoadingOlder = false;
            }
        });
        RequestManager.getRequestQueue().add(request);
    }

    private static String getUrl(int type, String lastModified, boolean older) {
        final String flag = older ? "1" : "0";
        return UrlBuilder.getBaseUrl() + "locker!queryDataImgTableNew.action?type=" + type
                + "&lastModified=" + lastModified + "&flag=" + flag;
    }

    private static String getLastModified(List<ServerImageData> data, boolean older) {
        long time = 0;
        if (older) {
            time = System.currentTimeMillis();
        }

        for (ServerImageData sid : data) {
            final String modifyTime = sid.getCollectTime();
            if (!TextUtils.isEmpty(modifyTime)) {
                long lm = Long.valueOf(modifyTime);
                if (older) {
                    if (lm < time) {
                        time = lm;
                    }
                } else {
                    if (lm > time) {
                        time = lm;
                    }
                }
            }
        }
        return String.valueOf(time);
    }
}
