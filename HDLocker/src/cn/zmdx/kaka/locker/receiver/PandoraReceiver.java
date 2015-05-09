
package cn.zmdx.kaka.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.service.PandoraServiceManager;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class PandoraReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("receive broadcast,action:" + intent.getAction());
        }
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                || intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            PandoraServiceManager.startServiceIfNeeded(context);
        }
    }
}
