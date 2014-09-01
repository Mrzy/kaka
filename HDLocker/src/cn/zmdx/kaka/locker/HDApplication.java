
package cn.zmdx.kaka.locker;

import android.app.Application;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HDApplication extends Application {

    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        mRequestQueue = Volley.newRequestQueue(this);
        super.onCreate();
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
