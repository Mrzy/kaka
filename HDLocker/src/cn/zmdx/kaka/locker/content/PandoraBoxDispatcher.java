
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

public class PandoraBoxDispatcher extends Handler {

    public static final int MSG_PULL_ORIGINAL_DATA = 12;

    public static final int MSG_ORIGINAL_DATA_ARRIVED = 13;

    public static final int MSG_DOWNLOAD_IMAGES = 14;

    private static PandoraBoxDispatcher INSTANCE;

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
                //标记最后一次拉取原始数据的时间
                mConfig.saveLastPullOriginalDataTime(System.currentTimeMillis());

                //如果这是今天第一次拉取，则删除当前库中的旧数据
                if (checkFirstPullToday()) {
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("今天第一次拉取到原始数据，删除以前的旧数据及sd卡上的图片缓存");
                    }
                    ServerImageDataModel.getInstance().deleteAll();
                    DiskImageHelper.clear();
                }
                //标记今天已经拉取过原始数据
                mConfig.saveTodayPullOriginalDataTime(BaseInfoHelper.getCurrentDate());

                if (oriDataList.size() <= 0) {
                    return;
                }
                //将今天的数据保存到本地数据库
                ServerImageData.saveToDatabase(oriDataList);
                break;
        }

        super.handleMessage(msg);
    }

    private boolean checkFirstPullToday() {
        String date = mConfig.getTodayPullOriginalData();
        String currentDate = BaseInfoHelper.getCurrentDate();
        return !date.equals(currentDate);
    }

    private boolean checkOriginalDataPullable() {
        long lastTime = mConfig.getLastTimePullOriginalData();
        return System.currentTimeMillis() - lastTime > PandoraPolicy.MIN_PULL_ORIGINAL_TIME;
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

            int hasImageCount = ServerImageDataModel.getInstance().queryCountHasImageAndUnRead(null);
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
        }
    }

    private void downloadServerImages() {
        // 根据不同网络情况查询出不同数量的数据，准备下载其图片
        // 规则说明：若wifi,则每个频道取5条数据，共5*5=25条数据；若非wifi，则每个频道取1条，共1 * 5 = 5条数据
        int count = HDBNetworkState.isWifiNetwork() ? PandoraPolicy.COUNT_DOWNLOAD_IMAGE_WIFI
                : PandoraPolicy.COUNT_DOWNLOAD_IMAGE_NON_WIFI;
        List<ServerImageData> list = new ArrayList<ServerImageData>();
        List<ServerImageData> tmpList = ServerImageDataModel.getInstance().queryWithoutImg(count);
        list.addAll(tmpList);
        ServerImageDataManager.getInstance().batchDownloadServerImage(list);
    }

    private void processPullOriginalData() {
        boolean isFirstPull = checkFirstPullToday();
        long lastModified = 0;
        if (!isFirstPull) {
            lastModified = ServerImageDataModel.getInstance().queryLastModifiedOfToday();
        }
        ServerImageDataManager.getInstance().pullTodayData(lastModified);
    }
}
