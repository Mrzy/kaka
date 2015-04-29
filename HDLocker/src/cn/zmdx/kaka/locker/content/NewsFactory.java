
package cn.zmdx.kaka.locker.content;

import java.util.List;

import org.json.JSONObject;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.network.UrlBuilder;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.widget.PandoraSwipeRefreshLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

public class NewsFactory {

    public interface IOnLoadingListener {
        void onLoaded(List<ServerImageData> stickData);
    }

    /**
     * @param type 新闻类型
     * @param adapter
     * @param data 已经加载的数据集
     * @param srl 下拉刷新的控件，方便控制显示状态
     * @param older 是要显示更老的数据还是更新的数据
     */
    static void updateNews(int type, final RecyclerView.Adapter adapter,
            final List<ServerImageData> data, final PandoraSwipeRefreshLayout srl, final boolean older,
            boolean showRefresh, final IOnLoadingListener listener) {
        if (adapter == null || data == null) {
            return;
        }

        boolean isLoading = false;
        if (srl.getTag() != null) {
            isLoading = (Boolean) srl.getTag();
        }

        if (isLoading) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("正在加载数据，中断此次请求");
            }
            return;
        }
        isLoading = true;
        srl.setTag(isLoading);
        if (!HDBNetworkState.isNetworkAvailable()) {
            srl.setRefreshing(false);

            isLoading = false;
            srl.setTag(isLoading);
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("无网络，中断请求新闻数据");
            }
            return;
        }

        if (showRefresh) {
            srl.setRefreshing(true);
        }

        JsonObjectRequest request = null;
        final String url = getUrl(type, data, older);
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("加载新闻url:" + url);
        }
        request = new JsonObjectRequest(url, null, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                final List<ServerImageData> newData = ServerImageData.parseJson(response);
                List<ServerImageData> stickData = ServerImageData.parseStickJson(response);
                if (older) {
                    data.addAll(newData);
                } else {
                    data.addAll(0, newData);
                }

                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("请求新闻数据成功，本次返回：" + newData.size() + ",条，共" + data.size() + "条新闻"+"  "+stickData.size()+"条Stick新闻");
                }

                if (null != listener) {
                    if (!older) {
                        listener.onLoaded(stickData);
                    }
                }
                adapter.notifyDataSetChanged();
                if (srl != null) {
                    srl.setRefreshing(false);
                    // 标记已加载完数据
                    srl.setTag(false);
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (BuildConfig.DEBUG) {
                    error.printStackTrace();
                }
                if (srl != null) {
                    srl.setRefreshing(false);
                    // 标记已加载完数据
                    srl.setTag(false);
                }
            }
        });
        RequestManager.getRequestQueue().add(request);
    }

    private static String getUrl(int type, List<ServerImageData> data, boolean older) {
        long time = -1;
        if (data != null && data.size() == 0) {
            time = System.currentTimeMillis();
        } else if (data != null && data.size() > 0) {
            for (ServerImageData sid : data) {
                final String modifyTime = sid.getCollectTime();
                if (!TextUtils.isEmpty(modifyTime)) {
                    long lm = Long.valueOf(modifyTime);
                    if (time == -1) {
                        time = lm;
                    } else {
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
            }
        }

        String flag = null;
        if (data.size() == 0) {
            flag = "1"; // 第一次加载新闻，flag为1
        } else {
            flag = older ? "1" : "0";
        }

        int limit = 20;
        if (!HDBNetworkState.isWifiNetwork()) {
            limit = 10;
        }
        return UrlBuilder.getBaseUrl() + "locker!queryDataImgTableNew.action?type=" + type
                + "&lastModified=" + time + "&flag=" + flag + "&limit=" + limit;
    }
}
