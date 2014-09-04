
package cn.zmdx.kaka.locker.content;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.zmdx.kaka.locker.content.BaiduDataManager.BaiduData;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

public class PandoraBoxDispatcher extends Handler {

    public static final int MSG_BAIDU_DATA_ARRIVED = 0;
    
    
    private static PandoraBoxDispatcher INSTANCE;
    private Context mContext;

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
        switch(msg.what) {
            case MSG_BAIDU_DATA_ARRIVED:
                final BaiduData bd = (BaiduData)msg.obj;
                bd.saveToDatabase();
                break;
        }

        super.handleMessage(msg);
    }
}
