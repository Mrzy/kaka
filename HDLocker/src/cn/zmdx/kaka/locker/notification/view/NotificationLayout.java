
package cn.zmdx.kaka.locker.notification.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.notification.NotificationInfo;
import cn.zmdx.kaka.locker.notification.NotificationInterceptor;
import cn.zmdx.kaka.locker.notification.adapter.NotificationAdapter;
import cn.zmdx.kaka.locker.notification.guide.NotificationGuideHelper;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class NotificationLayout extends FrameLayout {

    private NotificationInterceptor mInterceptor;

    private List<NotificationInfo> mActiveNotification = new ArrayList<NotificationInfo>();

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
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setVerticalFadingEdgeEnabled(true);
        mRecyclerView.setFadingEdgeLength(BaseInfoHelper.dip2px(getContext(), 3));
        mAdapter = new NotificationAdapter(getContext(), mActiveNotification);
        addView(mRecyclerView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mRecyclerView.setAdapter(mAdapter);
        mInterceptor = NotificationInterceptor.getInstance(getContext());
        mInterceptor.setNotificationListener(mNotificationListener);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
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
            mAdapter.remove(info);
        }

        @Override
        public void onPosted(final NotificationInfo info) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("notification onPosted, info:" + info.toString());
            }
            final int id = info.getId();
            int index = -1;
            NotificationInfo oldInfo = null;
            for (int i = mActiveNotification.size() - 1; i >= 0; i--) {
                NotificationInfo ni = mActiveNotification.get(i);
                if (ni.getId() == id) {
                    index = i;
                    oldInfo = ni;
                    break;
                }
            }

            if (index != -1) {
                mAdapter.replace(oldInfo, info);
            } else {
                mAdapter.add(info, 0);
            }
        }
    };

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
