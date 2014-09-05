
package cn.zmdx.kaka.locker;

import android.app.Application;
import android.content.Context;

import cn.zmdx.kaka.locker.content.PandoraBoxDispatcher;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HDApplication extends Application {

    private static HDApplication instance = null;

    public HDApplication() {
        instance = this;
    }

    public static HDApplication getInstannce() {
        return instance;
    }

    @Override
    public void onCreate() {
        PandoraBoxDispatcher.getInstance().sendEmptyMessage(PandoraBoxDispatcher.MSG_PULL_BAIDU_DATA);
        super.onCreate();
    }

}
