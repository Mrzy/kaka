package cn.zmdx.kaka.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.service.PandoraService;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class PandoraReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("receive broadcast,action:" + intent.getAction());
        }
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent service = new Intent(context, PandoraService.class);
            context.startService(service);
        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
//            LockScreenManager.getInstance().lock();
            Intent service = new Intent(context, PandoraService.class);
            context.startService(service);
        }
    }
}
