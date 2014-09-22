
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.content.BaiduDataManager.BaiduData;
import cn.zmdx.kaka.locker.content.ServerDataManager.ServerData;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.database.BaiduDataModel;
import cn.zmdx.kaka.locker.database.ServerDataModel;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

public class PandoraBoxDispatcher extends Handler {

    public static final int MSG_BAIDU_DATA_ARRIVED = 0;

    public static final int MSG_PULL_BAIDU_DATA = 1;

    public static final int MSG_LOAD_BAIDU_IMG = 2;

    public static final int MSG_SERVER_DATA_ARRIVED = 3;

    public static final int MSG_SERVER_IMAGE_DATA_ARRIVED = 4;

    public static final int MSG_PULL_SERVER_TEXT_DATA = 5;

    public static final int MSG_PULL_SERVER_IMAGE_DATA = 6;

    public static final int MSG_LOAD_SERVER_IMAGE = 7;


    private static PandoraBoxDispatcher INSTANCE;

    private PandoraBoxDispatcher(Looper looper) {
        super(looper);
    }

    public static synchronized PandoraBoxDispatcher getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PandoraBoxDispatcher(HDBThreadUtils.getWorkerLooper());
        }
        return INSTANCE;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_BAIDU_DATA_ARRIVED:
                @SuppressWarnings("unchecked")
                final List<BaiduData> bdList = (List<BaiduData>) msg.obj;
                BaiduData.saveToDatabase(bdList);
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("收到百度图片的数据，准备入库，条数:" + bdList.size());
                }
                break;
            case MSG_PULL_BAIDU_DATA:
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("收到抓取百度数据的消息");
                }
                processPullBaiduData();
                break;
            case MSG_PULL_SERVER_TEXT_DATA:
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("收到拉取文本数据的消息");
                }
                processPullServerTextData();
                break;
            case MSG_PULL_SERVER_IMAGE_DATA:
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("收到拉取搞笑图片数据的消息");
                }
                processPullServerImageData();
                break;
            case MSG_LOAD_BAIDU_IMG:
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("收到下载百度图片的消息");
                }
                loadBaiduImage();
                break;
            case MSG_LOAD_SERVER_IMAGE:
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("收到下载阿里云图片的消息");
                }
//                loadServerImage();
                break;
            case MSG_SERVER_DATA_ARRIVED:
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("server文本数据已经下载完成，准备入库");
                }
                @SuppressWarnings("unchecked")
                final List<ServerData> sdList = (List<ServerData>) msg.obj;
                ServerData.saveToDatabase(sdList);
                break;
            case MSG_SERVER_IMAGE_DATA_ARRIVED:
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("server Image数据已经下载完成，准备入库");
                }
                @SuppressWarnings("unchecked")
                final List<ServerImageData> sidList = (List<ServerImageData>) msg.obj;
                ServerImageData.saveToDatabase(sidList);
                break;
        }

        super.handleMessage(msg);
    }

    private void loadServerImage() {
        if (HDBNetworkState.isNetworkAvailable()) {
            // 如果磁盘缓存区图片数为0，则更新数据库中的是否下载字段为否
            if (DiskImageHelper.getFileCountOnDisk() <= 1) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("百度图片的本地磁盘存储的数量为0，清空数据库中的已下载标记，并开启下载图片程序");
                }
                BaiduDataModel.getInstance().markAllNonDownload();
                downloadPartImages();
                return;
            }

            int hasImageCount = BaiduDataModel.getInstance().queryCountHasImage();
            if (hasImageCount < PandoraPolicy.MIN_COUNT_LOCAL_DB_HAS_IMAGE) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("数据库中标记为已下载的数据总数:" + hasImageCount + "已小于阀值:"
                            + PandoraPolicy.MIN_COUNT_LOCAL_DB_HAS_IMAGE + ",立即开启下载图片程序");
                }
                downloadPartImages();
            } else {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("数据库中标记为已下载的数据总数为:" + hasImageCount + "大于最小阀值"
                            + PandoraPolicy.MIN_COUNT_LOCAL_DB_HAS_IMAGE + ",无需启动下载图片程序");
                }
            }
        }
    }

    private void loadBaiduImage() {
        if (HDBNetworkState.isNetworkAvailable()) {
            // 如果磁盘缓存区图片数为0，则更新数据库中的是否下载字段为否
            if (DiskImageHelper.getFileCountOnDisk() <= 1) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("百度图片的本地磁盘存储的数量为0，清空数据库中的已下载标记，并开启下载图片程序");
                }
                BaiduDataModel.getInstance().markAllNonDownload();
                downloadPartImages();
                return;
            }

            int hasImageCount = BaiduDataModel.getInstance().queryCountHasImage();
            if (hasImageCount < PandoraPolicy.MIN_COUNT_LOCAL_DB_HAS_IMAGE) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("数据库中标记为已下载的数据总数:" + hasImageCount + "已小于阀值:"
                            + PandoraPolicy.MIN_COUNT_LOCAL_DB_HAS_IMAGE + ",立即开启下载图片程序");
                }
                downloadPartImages();
            } else {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("数据库中标记为已下载的数据总数为:" + hasImageCount + "大于最小阀值"
                            + PandoraPolicy.MIN_COUNT_LOCAL_DB_HAS_IMAGE + ",无需启动下载图片程序");
                }
            }
        }
    }

    private void processPullBaiduData() {
        int totalCount = BaiduDataModel.getInstance().queryTotalCount();
        // 如果本地的百度图片库中的数据条数已经少于一定的值，则启动拉取程序
        if (totalCount < PandoraPolicy.MIN_COUNT_LOCAL_DB) {
            if (HDBNetworkState.isNetworkAvailable()) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("本地数据库的数据还有" + totalCount + "条数据，少于最小阀值，马上启动抓取程序");
                }

                BaiduDataManager.getInstance().pullAllFunnyData();
            } else {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("当前无网络，停止启动抓取程序");
                }
            }
        } else {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("本地数据库的数据还有" + totalCount + "条数据，无需启动抓取程序");
            }
        }
    }

    private void processPullServerTextData() {
        int totalCount = ServerDataModel.getInstance().queryTotalCount();
        if (!HDBNetworkState.isNetworkAvailable()) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("无可用网络，停止拉取");
            }
            return;
        }

        if (totalCount < PandoraPolicy.MIN_COUNT_LOCAL_DB) {
            ServerDataManager.getInstance().pullServerData(20, ServerDataMapping.S_DATATYPE_ALL,
                    ServerDataMapping.S_WEBSITE_ALL);
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("满足拉取文本数据条件，开始拉取");
            }
        } else {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("不满足拉取文本数据条件，无需拉取");
            }
        }
    }

    private void processPullServerImageData() {
        int totalCount = ServerImageDataModel.getInstance().queryTotalCount();
        if (!HDBNetworkState.isNetworkAvailable()) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("无可用网络，停止拉取");
            }
            return;
        }
        if (totalCount < PandoraPolicy.MIN_COUNT_LOCAL_DB) {
            ServerImageDataManager.getInstance().pullServerImageData(10, ServerDataMapping.S_DATATYPE_ALL,
                    ServerDataMapping.S_WEBSITE_ALL);
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("满足拉取文本数据条件，开始拉取");
            }
        } else {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("不满足拉取文本数据条件，无需拉取");
            }
        }
    }

    private void downloadPartImages() {
        // 根据不同网络情况查询出不同数量的数据，准备下载其图片
        // 规则说明：若wifi,则每个频道取5条数据，共5*5=25条数据；若非wifi，则每个频道取1条，共1 * 5 = 5条数据
        int count = HDBNetworkState.isWifiNetwork() ? PandoraPolicy.COUNT_DOWNLOAD_IMAGE_WIFI
                : PandoraPolicy.COUNT_DOWNLOAD_IMAGE_NON_WIFI;
        List<BaiduData> list = new ArrayList<BaiduData>();
        int length = PandoraPolicy.BAIDU_IMAGE_MODULE.length;
        for (int i = 0; i < length; i++) {
            list.addAll(BaiduDataModel.getInstance().queryNonImageData(
                    PandoraPolicy.BAIDU_IMAGE_MODULE[i], count));
        }
        BaiduDataManager.getInstance().batchDownloadBaiduImage(list);
    }
}
