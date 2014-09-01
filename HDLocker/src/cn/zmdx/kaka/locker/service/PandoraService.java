
package cn.zmdx.kaka.locker.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PandoraService extends Service {

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        //regist needed broadcast receiver
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
        // TODO Auto-generated method stub
        //unregist broadcast receiver
        super.onDestroy();
    }
}
