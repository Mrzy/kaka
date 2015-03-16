
package cn.zmdx.kaka.locker.notification.view;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import cn.zmdx.kaka.locker.database.CustomNotificationModel;
import cn.zmdx.kaka.locker.notification.NotificationInfo;
import cn.zmdx.kaka.locker.notification.NotificationInterceptor;
import cn.zmdx.kaka.locker.notification.PandoraNotificationService;
import cn.zmdx.kaka.locker.notification.adapter.NotificationAdapter;
import cn.zmdx.kaka.locker.notification.guide.NotificationGuideHelper;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

public class NotificationLayout extends FrameLayout {

    private NotificationInterceptor mInterceptor;

    private List<NotificationInfo> mActiveNotification = new LinkedList<NotificationInfo>();

    private NotificationAdapter mAdapter;

    private RecyclerView mRecyclerView;

    public NotificationLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NotificationLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationLayout(Context context) {
        this(context, null);
    }

    private void init() {
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setVerticalFadingEdgeEnabled(true);
        mRecyclerView.setFadingEdgeLength(BaseInfoHelper.dip2px(getContext(), 3));
        mAdapter = new NotificationAdapter(getContext(), this, mActiveNotification);
        mRecyclerView.setAdapter(mAdapter);
        addView(mRecyclerView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mInterceptor = NotificationInterceptor.getInstance(getContext());
        mInterceptor.setNotificationListener(mNotificationListener);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void addNotificationItem(NotificationInfo info) {
        final int id = info.getId();
        boolean has = false;
        for (NotificationInfo ni : mActiveNotification) {
            if (ni.getId() == id) {
                ni = info;
                has = true;
                break;
            }
        }

        if (!has) {
            mActiveNotification.add(0, info);
        }

        mAdapter.notifyDataSetChanged();
    }

    private NotificationInterceptor.INotificationListener mNotificationListener = new NotificationInterceptor.INotificationListener() {
        @Override
        public void onRemoved(int notifyId) {
            NotificationInfo info = null;
            for (NotificationInfo ni : mActiveNotification) {
                if (String.valueOf(ni.getId()).equals(notifyId)) {
                    info = ni;
                    break;
                }
            }
            removeNotification(info);
        }

        @Override
        public void onPosted(final NotificationInfo info) {
            addNotificationItem(info);
        }
    };

    // 通知item会恢复为初始化的位置
    public void restoreItemsPosition() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View childView = getChildAt(i);
            if (childView instanceof SwipeLayout) {
                SwipeLayout sl = (SwipeLayout) childView;
                sl.close(false);
            }
        }
    }

    @SuppressLint("NewApi")
    public void removeNotification(NotificationInfo info) {
        if (info != null) {
            // 如果是自定义通知，要从本地数据库删除通知
            if (info.getType() == NotificationInfo.NOTIFICATION_TYPE_CUSTOM) {
                CustomNotificationModel.getInstance().deleteById(info.getId());
            } else if (info.getType() == NotificationInfo.NOTIFICATION_TYPE_SYSTEM) {
                // 如果为系统通知，清除通知栏中的这个通知
                Intent intent = new Intent();
                intent.setAction(PandoraNotificationService.ACTION_CANCEL_NOTIFICATION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.putExtra("key", info.getKey());
                } else {
                    intent.putExtra("pkgName", info.getPkg());
                    intent.putExtra("tag", info.getTag());
                    intent.putExtra("id", info.getId());
                }
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            }

            // 从内存中的通知集合中移除这个通知
            mActiveNotification.remove(info);
            mAdapter.notifyDataSetChanged();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        sendGuideNotificationIfNeeded();
        super.onAttachedToWindow();
    }

    private void sendGuideNotificationIfNeeded() {
        final NotificationInfo guideNi = NotificationGuideHelper.getNextGuide(getContext());
        if (guideNi != null) {
            NotificationInterceptor.getInstance(getContext()).sendCustomNotification(guideNi);
        }
    }
}
