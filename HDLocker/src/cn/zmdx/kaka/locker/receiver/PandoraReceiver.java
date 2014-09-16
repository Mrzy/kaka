package cn.zmdx.kaka.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.zmdx.kaka.locker.service.PandoraService;

public class PandoraReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent service = new Intent(context, PandoraService.class);
            context.startService(service);
        }
    }
}
