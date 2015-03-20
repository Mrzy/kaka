
package cn.zmdx.kaka.locker.notification.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
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

    public static final String ACTION_NOTIFICATION_POSTED = "action_notification_posted";

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
        setLayoutAnimation(getAnimationController());
        mInterceptor = NotificationInterceptor.getInstance(getContext());
        mInterceptor.setNotificationListener(mNotificationListener);
    }

    /**
     * Layout动画
     * 
     * @return
     */
    private LayoutAnimationController getAnimationController() {
        int duration=300;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return controller;
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

            // 将收到新通知的事件以广播形式派发出去
            Intent in = new Intent(ACTION_NOTIFICATION_POSTED);
            in.putExtra("icon", info.getLargeIcon());
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(in);
        }
    };
}
