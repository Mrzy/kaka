
package cn.zmdx.kaka.fast.locker;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import cn.zmdx.kaka.fast.locker.service.PandoraService;
import cn.zmdx.kaka.fast.locker.sound.LockSoundManager;
import cn.zmdx.kaka.fast.locker.utils.HDBEventSource;
import cn.zmdx.kaka.fast.locker.utils.HDBThreadUtils;

public class HDApplication extends Application {

    private static Context instance = null;

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = getApplicationContext();
        HDBEventSource.startup(getApplicationContext(), null);
        RequestManager.init(this);
        HDBThreadUtils.runOnWorker(new Runnable() {

            @Override
            public void run() {
                ImageLoaderManager.init(instance);
                LockSoundManager.initSoundPool();
            }
        });
        super.onCreate();
        startPandoraService();
    }

    private void startPandoraService() {
        Intent in = new Intent(getApplicationContext(), PandoraService.class);
        startService(in);
    }
}
