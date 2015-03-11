
package cn.zmdx.kaka.locker;

import android.app.Application;
import android.content.Context;
import cn.zmdx.kaka.locker.crash.CrashHandler;
import cn.zmdx.kaka.locker.sound.LockSoundManager;
import cn.zmdx.kaka.locker.utils.HDBEventSource;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

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
        // PandoraBoxDispatcher.getInstance().pullData();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        crashHandler.setCollectDeviceInfo(false);
        crashHandler.setWrite2File(false);
        super.onCreate();
    }
}
