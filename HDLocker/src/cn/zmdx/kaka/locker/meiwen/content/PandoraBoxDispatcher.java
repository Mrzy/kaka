
package cn.zmdx.kaka.locker.meiwen.content;

import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.zmdx.kaka.locker.meiwen.BuildConfig;
import cn.zmdx.kaka.locker.meiwen.HDApplication;
import cn.zmdx.kaka.locker.meiwen.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.meiwen.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.meiwen.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.meiwen.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.meiwen.utils.HDBLOG;
import cn.zmdx.kaka.locker.meiwen.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.meiwen.utils.HDBThreadUtils;

public class PandoraBoxDispatcher extends Handler {

    public static final int MSG_PULL_ORIGINAL_DATA = 12;

    public static final int MSG_ORIGINAL_DATA_ARRIVED = 13;

    public static final int MSG_DOWNLOAD_IMAGES = 14;

    private static PandoraBoxDispatcher INSTANCE;

    private long mLastSyncDataTime = 0;

    private PandoraConfig mConfig;

    private PandoraBoxDispatcher(Looper looper) {
        super(looper);
        mConfig = PandoraConfig.newInstance(HDApplication.getContext());
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
            case MSG_PULL_ORIGINAL_DATA:
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("收到拉取原始数据消息");
                }
                if (!HDBNetworkState.isNetworkAvailable()) {
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("无网络，中断拉取原始数据");
                    }
                    return;
                }
                if (!checkOriginalDataPullable()) {
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("拉取原始数据条件不满足，中断拉取");
                    }
                    return;
                }
                processPullOriginalData();
                break;
            case MSG_DOWNLOAD_IMAGES:
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("收到下载图片的消息");
                }
                loadPandoraServerImage();
                break;
            case MSG_ORIGINAL_DATA_ARRIVED:
                @SuppressWarnings("unchecked")
                final List<ServerImageData> oriDataList = (List<ServerImageData>) msg.obj;
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("原始数据已经下载完成，开始入库，条数:" + oriDataList.size());
                }
                // 标记最后一次拉取原始数据的时间
                mConfig.saveLastPullOriginalDataTime(System.currentTimeMillis());

                if (oriDataList.size() <= 0) {
                    return;
                }

                //删除本地数据库中除了已经收藏的新闻的数据
                List<String> delUrls = ServerImageDataModel.getInstance().deleteOldDataExceptFavorited();
                // 将今天的数据保存到本地数据库
                ServerImageData.saveToDatabase(oriDataList);
                deleteLocalImage(delUrls);
                break;
        }

        super.handleMessage(msg);
    }

    private void deleteLocalImage(List<String> delUrls) {
        DiskImageHelper.deleteByUrls(delUrls);
    }

    /**
     * 尝试拉取原始数据及图片的预下载，此方法仅是尝试拉取，如果判断条件都满足才会真正做拉取动作
     */
    public void pullData() {
        long curTime = System.currentTimeMillis();
        long delta = curTime - mLastSyncDataTime;
        if (delta > PandoraPolicy.MIN_DURATION_SYNC_DATA_TIME) {
            sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_ORIGINAL_DATA);
            mLastSyncDataTime = curTime;
        }
        if (!hasMessages(PandoraBoxDispatcher.MSG_DOWNLOAD_IMAGES)) {
            sendEmptyMessageDelayed(PandoraBoxDispatcher.MSG_DOWNLOAD_IMAGES, 3000);
        }
    }

    private boolean checkOriginalDataPullable() {
        long lastTime = mConfig.getLastTimePullOriginalData();
        int count = ServerImageDataModel.getInstance().getCountUnRead();
        return System.currentTimeMillis() - lastTime > PandoraPolicy.MIN_PULL_ORIGINAL_TIME && count < 20;
    }

    private void loadPandoraServerImage() {
        if (HDBNetworkState.isNetworkAvailable()) {
            // 如果磁盘缓存区图片数为0，则更新数据库中的是否下载字段为否
            if (DiskImageHelper.getFileCountOnDisk() <= 1) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("图片的本地磁盘存储的数量为0，清空数据库中的已下载标记，并开启下载图片程序");
                }
                ServerImageDataModel.getInstance().markAllNonDownloadExceptHtml();
                downloadServerImages();
                return;
            }

            int hasImageCount = ServerImageDataModel.getInstance()
                    .queryCountHasImageAndUnRead(null);
            if (hasImageCount < PandoraPolicy.MIN_COUNT_LOCAL_DB_HAS_IMAGE) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("ServerImage数据库中标记为已下载的数据总数:" + hasImageCount + "已小于阀值:"
                            + PandoraPolicy.MIN_COUNT_LOCAL_DB_HAS_IMAGE + ",立即开启下载图片程序");
                }
                downloadServerImages();
            } else {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("ServerImage数据库中标记为已下载的数据总数为:" + hasImageCount + "大于最小阀值"
                            + PandoraPolicy.MIN_COUNT_LOCAL_DB_HAS_IMAGE + ",无需启动下载图片程序");
                }
            }
        } else {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("下载图片条件不允许，无网络");
            }
        }
    }

    private void downloadServerImages() {
        // 根据不同网络情况查询出不同数量的数据，准备下载其图片
        // 规则说明：若wifi,则每个频道取5条数据，共5*5=25条数据；若非wifi，则每个频道取1条，共1 * 5 = 5条数据
        int count = 0;
        if (HDBNetworkState.isWifiNetwork()) {
            count = PandoraPolicy.COUNT_DOWNLOAD_IMAGE_WIFI;
        } else {
            if (mConfig.isMobileNetwork()) {
                count = PandoraPolicy.COUNT_DOWNLOAD_IMAGE_NON_WIFI;
            }
        }
        if (count <= 0) {
            return;
        }
        List<ServerImageData> tmpList = ServerImageDataModel.getInstance().queryWithoutImg(count);
        ServerImageDataManager.getInstance().batchDownloadServerImage(tmpList);
    }

    private void processPullOriginalData() {
        long lastModified = ServerImageDataModel.getInstance().queryLastModified();
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("lastModified:" + lastModified);
        }
        ServerImageDataManager.getInstance().pullTodayData(lastModified);
    }
}
