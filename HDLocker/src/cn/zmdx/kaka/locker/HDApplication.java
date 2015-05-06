
package cn.zmdx.kaka.locker;

import android.app.Application;
import android.content.Context;
import cn.zmdx.kaka.locker.crash.CrashHandler;
import cn.zmdx.kaka.locker.daemon.DaemonLoader;
import cn.zmdx.kaka.locker.daemon.Utilities;
import cn.zmdx.kaka.locker.sound.LockSoundManager;
import cn.zmdx.kaka.locker.utils.HDBEventSource;
import cn.zmdx.kaka.locker.weather.PandoraLocationManager;

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

        // PandoraBoxDispatcher.getInstance().pullData();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        crashHandler.setCollectDeviceInfo(false);
        crashHandler.setWrite2File(false);
        super.onCreate();

        PandoraLocationManager.getInstance(instance).requestLocation();
        LockSoundManager.init();

        Utilities.initEnvironment(instance);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DaemonLoader.startDaemonIfNeeded(true);
            }
        }).start();
//        if (!BuildConfig.DEBUG) {
//            AppUninstall.openUrlWhenUninstall(this, "http://www.mikecrm.com/f.php?t=rlfi3n");
//        }
    }
}
