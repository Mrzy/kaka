
package cn.zmdx.kaka.locker.notification.view;

import java.util.ArrayList;
import java.util.List;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ListView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.notification.NotificationInfo;
import cn.zmdx.kaka.locker.notification.NotificationInterceptor;
import cn.zmdx.kaka.locker.notification.adapter.NotificationListViewAdapter;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

public class NotificationListView extends FrameLayout {

    private NotificationInterceptor mInterceptor;

    private List<NotificationInfo> mActiveNotification = new ArrayList<NotificationInfo>();

    private NotificationListViewAdapter mAdapter;

    public static final String ACTION_NOTIFICATION_POSTED = "action_notification_posted";

    private View mFooterView;

    private ListView mListView;

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
        View view = View.inflate(getContext(), R.layout.notification_layout, this);
        mListView = (ListView) view.findViewById(R.id.notificationListView);
        mFooterView = view.findViewById(R.id.notificationFooterView);
        mFooterView.setVisibility(View.GONE);
        mFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        mAdapter = new NotificationListViewAdapter(getContext(), mActiveNotification, this);
        mListView.setAdapter(mAdapter);
        mListView.setLayoutAnimation(getAnimationController());
        mListView.setVerticalFadingEdgeEnabled(true);
        mListView.setVerticalScrollBarEnabled(true);
        mListView.setFadingEdgeLength(BaseInfoHelper.dip2px(getContext(), 3));
        mListView.setLayoutTransition(new LayoutTransition());
        mInterceptor = NotificationInterceptor.getInstance(getContext());
        mInterceptor.setNotificationListener(mNotificationListener);
    }

    private void clear() {
        int first = mListView.getFirstVisiblePosition();
        int count = mAdapter.getCount();
        int delay = 0;
        for (int i = count - 1; i >= 0; i--) {
            View itemView = mListView.getChildAt(i);
            if (itemView != null) {
                itemView.animate().translationX(-itemView.getWidth()).setDuration(300)
                        .setStartDelay(delay).start();
                delay += 200;
            }
        }

        HDBThreadUtils.postOnUiDelayed(new Runnable() {
            @Override
            public void run() {
                synchronized (mActiveNotification) {
                    for (int i = mActiveNotification.size() - 1; i >= 0; i--) {
                        mAdapter.remove(mActiveNotification.get(i), false);
                    }
                    mFooterView.setVisibility(View.GONE);
                }
            }
        }, delay);
    }

    /**
     * Layout动画
     * 
     * @return
     */
    private LayoutAnimationController getAnimationController() {
        int duration = 300;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
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

            UmengCustomEventManager.statisticalPostNotification(info.getId(), info.getPkg(),
                    info.getType());

            wakeLockIfNeeded();
        }
    };

    private void wakeLockIfNeeded() {
        PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        if (!isScreenOn(pm)) {
            final WakeLock powerWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, "notification");
            powerWakeLock.acquire();
            HDBThreadUtils.postOnUiDelayed(new Runnable() {
                @Override
                public void run() {
                    powerWakeLock.release();
                }
            }, 3000);
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private boolean isScreenOn(PowerManager pm) {
        if (Build.VERSION.SDK_INT < 20) {
            return pm.isScreenOn();
        } else {
            return pm.isInteractive();
        }
    }

    public ListView getListView() {
        return mListView;
    }

    public void setClearButtonVisibility(int visible) {
        mFooterView.setVisibility(visible);
    }
}
