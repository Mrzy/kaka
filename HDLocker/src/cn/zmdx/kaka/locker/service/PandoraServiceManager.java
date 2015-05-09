
package cn.zmdx.kaka.locker.service;

import android.content.Context;
import android.content.Intent;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

public class PandoraServiceManager {

    public static void startServiceIfNeeded(Context context) {
        if (PandoraConfig.newInstance(context).isPandolaLockerOn()) {
            context.startService(new Intent(context, PandoraService.class));
        }
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, PandoraService.class));
    }
}
