
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.content.BaiduDataManager.BaiduData;
import cn.zmdx.kaka.locker.database.DatabaseModel;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

public class PandoraBoxDispatcher extends Handler {

    public static final int MSG_BAIDU_DATA_ARRIVED = 0;

    public static final int MSG_PULL_BAIDU_DATA = 1;

    public static final int MSG_LOAD_BAIDU_IMG = 2;

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

                break;
            case MSG_PULL_BAIDU_DATA:
                if (HDBNetworkState.isNetworkAvailable()) {
                    BaiduDataManager bdm = new BaiduDataManager();
                    bdm.pullAllFunnyData();
                }
            case MSG_LOAD_BAIDU_IMG:
                if (HDBNetworkState.isNetworkAvailable()) {
                    downloadBaiduImage();

                }
                break;
        }

        super.handleMessage(msg);
    }

    private void downloadBaiduImage() {
        //根据不同网络情况查询出不同数量的数据，准备下载其图片
        //规则说明：若wifi,则每个频道取5条数据，共5*5=25条数据；若非wifi，则每个频道取1条，共1 * 5 = 5条数据
        int count = HDBNetworkState.isWifiNetwork() ? 5 : 1;
        List<BaiduData> list = new ArrayList<BaiduData>();
        list.addAll(DatabaseModel.getInstance().queryNonImageData(BaiduTagMapping.INT_TAG1_BIZHI,
                count));
        list.addAll(DatabaseModel.getInstance().queryNonImageData(BaiduTagMapping.INT_TAG1_GAOXIAO,
                count));
        list.addAll(DatabaseModel.getInstance().queryNonImageData(BaiduTagMapping.INT_TAG1_MEINV,
                count));
        list.addAll(DatabaseModel.getInstance().queryNonImageData(
                BaiduTagMapping.INT_TAG1_MINGXING, count));
        list.addAll(DatabaseModel.getInstance().queryNonImageData(BaiduTagMapping.INT_TAG1_SHEYING,
                count));

        new BaiduDataManager().batchDownloadBaiduImage(list);
    }
}
