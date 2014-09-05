
package cn.zmdx.kaka.locker.content;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.content.BaiduDataManager.BaiduData;
import cn.zmdx.kaka.locker.database.DatabaseModel;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
                    BaiduDataManager bdm = new BaiduDataManager(HDApplication.getInstannce());
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
        int count = HDBNetworkState.isWifiNetwork() ? 5 : 1;
        List<BaiduData> list1 = DatabaseModel.getInstance().queryNonImageData(
                BaiduTagMapping.INT_TAG1_BIZHI, count);

        // 根据网络情况，从本地数据库读出部分条数据（例如20条),
        // 然后依次请求其图片，保存到本地的磁盘上，保存的文件名用url做hash处理，去掉扩展名，保存到sdcard/.Android/<package
        // name>中
        // TODO
    }
}
