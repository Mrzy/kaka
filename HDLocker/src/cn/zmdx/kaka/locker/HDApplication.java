
package cn.zmdx.kaka.locker;

import android.app.Application;

import cn.zmdx.kaka.locker.database.DatabaseModel;

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

    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        mRequestQueue = Volley.newRequestQueue(this);
        String manufacturer = android.os.Build.MANUFACTURER;// 获取制造商名字
        DatabaseModel.getInstance().createTable();
        super.onCreate();
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
