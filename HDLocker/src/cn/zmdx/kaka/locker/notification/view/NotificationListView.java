
package cn.zmdx.kaka.locker.notification.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.notification.NotificationInfo;
import cn.zmdx.kaka.locker.notification.NotificationInterceptor;
import cn.zmdx.kaka.locker.notification.adapter.NotificationListViewAdapter;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class NotificationListView extends ListView {

    private NotificationInterceptor mInterceptor;

    private List<NotificationInfo> mActiveNotification = new ArrayList<NotificationInfo>();

    private NotificationListViewAdapter mAdapter;

    public NotificationListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NotificationListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationListView(Context context) {
        this(context, null);
    }

    private void init() {
        mAdapter = new NotificationListViewAdapter(getContext(), mActiveNotification);
        setAdapter(mAdapter);
        mInterceptor = NotificationInterceptor.getInstance(getContext());
        mInterceptor.setNotificationListener(mNotificationListener);
    }

    private NotificationInterceptor.INotificationListener mNotificationListener = new NotificationInterceptor.INotificationListener() {
        @Override
        public void onRemoved(int notifyId) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("notification onRemoved, notifyId:" + notifyId);
            }
            NotificationInfo info = null;
            for (int i = 0; i < mActiveNotification.size(); i++) {
                NotificationInfo ni = mActiveNotification.get(i);
                if (ni.getId() == notifyId) {
                    info = ni;
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onPosted(final NotificationInfo info) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("notification onPosted, info:" + info.toString());
            }
            
            boolean has = false;
            final int id = info.getId();
            int index = -1;
            for (int i = mActiveNotification.size() - 1; i >= 0; i--) {
                NotificationInfo ni = mActiveNotification.get(i);
                if (ni.getId() == id) {
                    has = true;
                    index = i;
                    break;
                }
            }

            if (has) {
                mActiveNotification.set(index, info);
            } else {
                mActiveNotification.add(0, info);
            }
            mAdapter.notifyDataSetChanged();
        }
    };
}