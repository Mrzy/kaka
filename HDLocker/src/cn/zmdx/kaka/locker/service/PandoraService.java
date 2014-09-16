
package cn.zmdx.kaka.locker.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class PandoraService extends Service {

    @Override
    public void onCreate() {
        registerBroadcastReceiver();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("PandoraService onDestroy()");
        }
        unRegisterBroadcastReceiver();
        super.onDestroy();
        startService(new Intent(this, PandoraService.class));
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mReceiver, filter);
    }

    private void unRegisterBroadcastReceiver() {
        unregisterReceiver(mReceiver);
    }

    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("receive broadcast,action=" + action);
            }
            LockScreenManager.getInstance().lock();
        }
    };
}
