
package cn.zmdx.kaka.locker.notification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.database.CustomNotificationModel;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

public class NotificationInterceptor extends Handler {

    static final int MSG_NOTIFICATION_POST = 0;

    static final int MSG_NOTIFICATION_REMOVED = 1;

    private static final int MSG_CUSTOM_NOTIFICATION_POST = 2;

    private static final int MSG_CUSTOM_NOTIFICATION_REMOVED = 3;

    private static final int MSG_PULL_CUSTOM_NOTIFICATION_DATA = 4;

    static final int MSG_ACTIVE_NOTIFICATIONS_ARRIVED = 5;

    private static final int DURATION_PULL_CUSTOM_NOTIFICATION_DATA = BuildConfig.DEBUG ? 30 * 1000
            : 2 * 60 * 60 * 1000;

    private static final String BASE_URL = "http://nb.hdlocker.com/pandora/notify!queryNotifyList.action";

    private static final int MSG_DISPATCH_CUSTOM_NOTIFICATION = 6;

    private static NotificationInterceptor INSTANCE;

    private Context mContext;

    private Set<StatusBarNotification> mNotificationSet = new HashSet<StatusBarNotification>();

    private INotificationListener mListener;

    private NotificationPreferences mPreference;

    public interface INotificationListener {
        void onPosted(NotificationInfo info);

        void onRemoved(int notifyId);
    }

    private NotificationInterceptor(Context context, Looper looper) {
        super(looper);
        mContext = context;
        mPreference = NotificationPreferences.getInstance(context);
    }

    public static NotificationInterceptor getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NotificationInterceptor(context, HDBThreadUtils.getWorkerLooper());
        }
        return INSTANCE;
    }

    public void setNotificationListener(INotificationListener listener) {
        mListener = listener;
    }

    @SuppressLint("NewApi")
    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case MSG_NOTIFICATION_POST:
                if (!(msg.obj instanceof StatusBarNotification)) {
                    return;
                }
                final StatusBarNotification sbn = (StatusBarNotification) msg.obj;
                if (sbn == null || !checkIntercept(sbn.getPackageName())) {
                    return;
                }

                if (mListener != null) {
                    Bundle bundle = sbn.getNotification().extras;
                    String title = bundle.getString("android.title");
                    String content = bundle.getString("android.text");
                    Bitmap largeIcon = (Bitmap) bundle.getParcelable("android.largeIcon");
                    long postTime = sbn.getPostTime();

                    if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
                        return;
                    }

                    final NotificationInfo ni = new NotificationInfo();
                    ni.setLargeIcon(largeIcon);
                    ni.setTitle(title);
                    ni.setContent(content);
                    try {
                        Drawable smallIcon = mContext.getPackageManager().getApplicationIcon(
                                sbn.getPackageName());
                        if (smallIcon != null) {
                            ni.setSmallIcon(smallIcon);
                        }
                    } catch (NameNotFoundException e) {
                    }
                    ni.setPostTime(postTime);
                    ni.setId(sbn.getId());
                    ni.setPkg(sbn.getPackageName());
                    ni.setTag(sbn.getTag());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ni.setKey(sbn.getKey());
                    }
                    ni.setType(NotificationInfo.NOTIFICATION_TYPE_SYSTEM);
                    ni.setPendingIntent(sbn.getNotification().contentIntent);
                    dispatchNotificationPostedEvent(ni);
                }
                break;
            case MSG_NOTIFICATION_REMOVED:
                if (!(msg.obj instanceof StatusBarNotification)) {
                    return;
                }
                final StatusBarNotification sbn1 = (StatusBarNotification) msg.obj;
                if (sbn1 == null || !checkIntercept(sbn1.getPackageName())) {
                    return;
                }

                dispatchNotificationRemovedEvent(sbn1);
                break;
            case MSG_CUSTOM_NOTIFICATION_POST:
                if (!(msg.obj instanceof NotificationInfo)) {
                    return;
                }
                final NotificationInfo ni = (NotificationInfo) msg.obj;
                dispatchNotificationPostedEvent(ni);
                break;
            case MSG_CUSTOM_NOTIFICATION_REMOVED:

                break;
            case MSG_PULL_CUSTOM_NOTIFICATION_DATA:
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("----拉取自定义通知原始数据：开始拉取");
                }
                handlePullCustomNotificationData();
                break;
            case MSG_ACTIVE_NOTIFICATIONS_ARRIVED:
                if (!(msg.obj instanceof StatusBarNotification[])) {
                    return;
                }

                final StatusBarNotification[] sbns = (StatusBarNotification[]) msg.obj;
                for (StatusBarNotification notify : sbns) {
                    if (checkIntercept(notify.getPackageName())) {
                        Message message = Message.obtain();
                        message.what = MSG_NOTIFICATION_POST;
                        message.obj = notify;
                        sendMessageDelayed(message, 300);
                    }
                }
                break;
            case MSG_DISPATCH_CUSTOM_NOTIFICATION:
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("----调度自定义通知:开始调度");
                }
                handleDispatchCustomNotification();
                break;
            default:

        }
    };

    private void handleDispatchCustomNotification() {
        // 删除过期自定义通知
        deleteExpiredNotificationDataFromDb();

        List<NotificationEntity> data = CustomNotificationModel.getInstance()
                .queryValidNotification();
        if (data != null && data.size() > 0) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("----调度自定义通知：从本地数据库查出有效通知数据" + data.size() + "条");
            }
            for (NotificationEntity entity : data) {
                final NotificationInfo ni = PandoraNotificationFactory
                        .createCustomNotification(entity);
                if (ni != null) {
                    // 如果此自定义通知类型是打开浏览器，则判断如果当前没有网络，则跳过
                    if (!TextUtils.isEmpty(entity.getTargetUrl())) {
                        if (!HDBNetworkState.isNetworkAvailable()) {
                            if (BuildConfig.DEBUG) {
                                HDBLOG.logD("----调度自定义通知：当前无网络，忽略本通知，通知数据体：" + ni.toString());
                            }
                            continue;
                        }
                    }
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("----调度自定义通知：发送显示自定义通知消息，通知数据体：" + ni.toString());
                    }
                    sendCustomNotification(ni);
                }
            }
        } else {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("----调度自定义通知: 没有在本地数据库查询到有效通知数据，停止调度");
            }
        }
    }

    /**
     * 删除本地数据库中过期的通知数据
     */
    private void deleteExpiredNotificationDataFromDb() {
        int result = CustomNotificationModel.getInstance().deleteExpiredData();
        if (result > 0 && BuildConfig.DEBUG) {
            HDBLOG.logD("----调度自定义通知: 删除过期数据共" + result + "条");
        }
    }

    /**
     * 用于获取拉取自定义通知的url
     * 
     * @param lastModified
     * @return
     */
    private String getUrl(long lastModified) {
        return BASE_URL + "?lastModified=" + lastModified;
    }

    private void handlePullCustomNotificationData() {
        if (!HDBNetworkState.isNetworkAvailable()) {
            return;
        }

        final long lastModified = mPreference.getPullCustomNotificationLastModified();
        final String url = getUrl(lastModified);
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("----拉取自定义通知原始数据：拉取url：" + url);
        }
        JsonObjectRequest request = null;
        request = new JsonObjectRequest(url, null, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response == null) {
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("----拉取自定义通知原始数据：返回数据位null");
                    }
                    return;
                }
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("----拉取自定义通知原始数据：请求返回：" + response.toString());
                }
                final String status = response.optString("state");
                final long newLastModified = response.optLong("lastModified");
                if (!TextUtils.isEmpty(status) && status.equals("success")) {
                    List<NotificationEntity> list = new ArrayList<NotificationEntity>();
                    JSONArray data = response.optJSONArray("data");
                    int size = data.length();
                    for (int i = 0; i < size; i++) {
                        final NotificationEntity entity = new NotificationEntity();
                        JSONObject item = data.optJSONObject(i);
                        entity.setCloudId(item.optInt("id"));
                        entity.setTitle(item.optString("title"));
                        entity.setContent(item.optString("content"));
                        entity.setType(NotificationInfo.NOTIFICATION_TYPE_CUSTOM);
                        entity.setStartTime(item.optLong("start_time"));
                        entity.setEndTime(item.optLong("end_time"));
                        entity.setIcon(item.optString("icon"));
                        entity.setLevel(item.optInt("level"));
                        entity.setTargetApp(item.optString("application"));
                        entity.setTargetUrl(item.optString("url"));
                        entity.setTimes(item.optInt("times"));
                        list.add(entity);
                    }

                    int result = CustomNotificationModel.getInstance().batchInsert(list);
                    if (BuildConfig.DEBUG) {
                        HDBLOG.logD("----拉取自定义通知原始数据：将数据存储到本地数据库，成功插入" + result + "条");
                    }
                    mPreference.savePullCustomNotificationLastModified(newLastModified);
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (BuildConfig.DEBUG) {
                    error.printStackTrace();
                }
            }
        });
        request.setShouldCache(false);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 增量拉取自定义通知的原始数据，如果返回有效数据，会保存到本地数据库
     */
    public void tryPullCustomNotificationData() {
        final long lastPullTime = mPreference.getLastPullCustomNotificationTime();
        final long current = System.currentTimeMillis();
        if (current - lastPullTime > DURATION_PULL_CUSTOM_NOTIFICATION_DATA) {
            sendEmptyMessage(MSG_PULL_CUSTOM_NOTIFICATION_DATA);
            mPreference.saveLastPullCustomNotificationTime(current);
        }
    }

    /**
     * 尝试调度自定义通知显示。从本地数据库查找出有效的通知数据并调度显示在锁屏上
     */
    public void tryDispatchCustomNotification() {
        sendEmptyMessage(MSG_DISPATCH_CUSTOM_NOTIFICATION);
    }

    private void dispatchNotificationPostedEvent(final NotificationInfo ni) {
        if (mListener != null) {
            HDBThreadUtils.runOnUi(new Runnable() {

                @Override
                public void run() {
                    mListener.onPosted(ni);
                }
            });
        }
    }

    private void dispatchNotificationRemovedEvent(final StatusBarNotification sbn) {
        if (mListener != null) {
            HDBThreadUtils.runOnUi(new Runnable() {

                @SuppressLint("NewApi")
                @Override
                public void run() {
                    mListener.onRemoved(sbn.getId());
                }
            });
        }
    }

    public void sendCustomNotification(NotificationInfo sbn) {
        if (sbn == null || sbn.getType() != NotificationInfo.NOTIFICATION_TYPE_CUSTOM) {
            if (BuildConfig.DEBUG) {
                throw new IllegalStateException(
                        "sbn must not be null, and sbn's type must be NOTIFICATION_TYPE_CUSTOM");
            }
            return;
        }
        if (sbn.getId() == 0) {
            if (BuildConfig.DEBUG) {
                throw new IllegalStateException("sbn's id must not be zero");
            }
            return;
        }
        Message msg = obtainMessage();
        msg.what = MSG_CUSTOM_NOTIFICATION_POST;
        msg.obj = sbn;
        sendMessageDelayed(msg, 300);
    }

    /**
     * 检查是否拦截指定包名的应用
     * 
     * @param pkgName
     * @return 如果pkgName是拦截的应用，返回true，否则返回false
     */
    private boolean checkIntercept(String pkgName) {
        return mPreference.isIntercepted(pkgName);
    }

    /**
     * 检查拦截通知功能是否可用。只有android4.3及以上设备才可用
     * 
     * @return
     */
    public boolean isDeviceAvailable() {
        // TODO
        return false;
    }

    public Set<StatusBarNotification> getActiveNotifications() {
        return mNotificationSet;
    }

    public Set<String> getInterceptPackages() {
        // TODO
        return null;
    }

    public void cancelIntercept(String pkgName) {
        // TODO
    }

    public void addIntercept(String pkgName) {

    }
}
