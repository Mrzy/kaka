
package cn.zmdx.kaka.locker.notification.view;

import java.util.HashMap;
import java.util.Map;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.LockScreenManager.IMainPanelListener;
import cn.zmdx.kaka.locker.LockScreenManager.IPullDownListener;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.notification.NotificationInfo;
import cn.zmdx.kaka.locker.notification.NotificationInterceptor;
import cn.zmdx.kaka.locker.notification.PandoraNotificationService;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

//import android.util.Log;

public class NotificationLayout extends LinearLayout {

    private NotificationInterceptor mInterceptor;

    private Map<String, NotificationInfo> mActiveNotification = new HashMap<String, NotificationInfo>();

    private View mCurrentTouchView;

    private static final long ITEM_DOUBLE_TAP_DURATION = 200;

    protected static final int GAP_BETWEEN_NOTIFICATIONS = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 5);

    protected static final int NOTIFICATION_ITEM_HEIGHT = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 60);

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
        mInterceptor = NotificationInterceptor.getInstance(getContext());
        mInterceptor.setNotificationListener(mNotificationListener);
        setOrientation(LinearLayout.VERTICAL);
        final LayoutTransition transitioner = new LayoutTransition();
        setLayoutTransition(transitioner);
    }

    private long mItemClickStartTime = 0;

    private NotificationInterceptor.INotificationListener mNotificationListener = new NotificationInterceptor.INotificationListener() {
        @Override
        public void onRemoved(int notifyId) {
            removeNotification(String.valueOf(notifyId));
        }

        @Override
        public void onPosted(final NotificationInfo info) {
            final String notificationid = String.valueOf(info.getId());
            mActiveNotification.put(notificationid, info);

            View view = findViewWithTag(notificationid);
            if (view != null) {
                updateNotificationItem(view, info);
            } else {
                final View itemView = createNotificationItemView(info);
                itemView.setOnTouchListener(new ItemViewTouchListener(itemView));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - mItemClickStartTime < ITEM_DOUBLE_TAP_DURATION) {
                            removeNotification(String.valueOf(v.getTag()));
                        } else {
                            mItemClickStartTime = currentTime;
                        }
                    }
                });
                addNotificationItem(itemView);
            }
        }
    };

    /**
     * 创建ItemView，并添加到本容器的顶部
     * 
     * @param itemView
     */
    private void addNotificationItem(View itemView) {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, NOTIFICATION_ITEM_HEIGHT);
        lp.bottomMargin = GAP_BETWEEN_NOTIFICATIONS;
        addView(itemView, 0, lp);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void removeNotification(String notifyId) {
        View targetView = findViewWithTag(notifyId);
        if (targetView != null) {
            removeView(targetView);
        }
        NotificationInfo info = mActiveNotification.get(notifyId);
        if (info != null) {
            Intent intent = new Intent();
            intent.setAction(PandoraNotificationService.ACTION_CANCEL_NOTIFICATION);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                intent.putExtra("key", info.getKey());
//            } else {
                intent.putExtra("pkgName", info.getPkg());
                intent.putExtra("tag", info.getTag());
                intent.putExtra("id", info.getId());
//            }
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            mActiveNotification.remove(notifyId);
        }
    }

    private class ItemViewTouchListener implements OnTouchListener {

        private View mItemView;

        public ItemViewTouchListener(View itemView) {
            mItemView = itemView;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.performClick();
                    mCurrentTouchView = mItemView;
                    startTapItemAnimation(mItemView);
                    // TODO 1. 变化view的背景色 2. 开始右划打开的提示动画
                    final String id = String.valueOf(v.getTag());
                    LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {

                        @Override
                        public void run() {
                            final NotificationInfo info = mActiveNotification.get(id);
                            try {
                                PendingIntent pi = info.getPendingIntent();
                                if (pi != null) {
                                    pi.send();
                                }
                            } catch (Exception e) {
                            }
                            removeNotification(id);
                        }
                    });
                    return true;
                case MotionEvent.ACTION_MOVE:
                    return true;
                case MotionEvent.ACTION_UP:
                    resetState();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
            return true;
        }

    };

    private ObjectAnimator mRightArrowAnimator;

    private void startTapItemAnimation(View view) {
        View rightArrowView = view.findViewById(R.id.rightArrow);
        View contentLayout = view.findViewById(R.id.blackOverlay);
        contentLayout.setBackgroundColor(Color.parseColor("#bb000000"));
        view.findViewById(R.id.rightArrow).setVisibility(View.VISIBLE);
        view.findViewById(R.id.doubleTapDeleteTip).setVisibility(View.VISIBLE);
        mRightArrowAnimator = ObjectAnimator.ofFloat(rightArrowView, "translationX",
                BaseInfoHelper.dip2px(getContext(), 40));
        mRightArrowAnimator.setDuration(1500);
        mRightArrowAnimator.setRepeatCount(-1);
        mRightArrowAnimator.setRepeatMode(Animation.RESTART);
        mRightArrowAnimator.start();
    }

    public void clearAll() {
        // TODO
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    };

    private void updateNotificationItem(View itemView, NotificationInfo info) {
        final ImageView largeIcon = (ImageView) itemView.findViewById(R.id.largeIcon);
        final ImageView smallIcon = (ImageView) itemView.findViewById(R.id.smallIcon);
        final TextView title = (TextView) itemView.findViewById(R.id.title);
        final TextView content = (TextView) itemView.findViewById(R.id.content);
        Bitmap largeBmp = info.getLargeIcon();
        Drawable smallDrawable = info.getSmallIcon();
        if (largeBmp != null) {
            largeIcon.setImageBitmap(largeBmp);
            if (smallDrawable != null) {
                smallIcon.setImageDrawable(smallDrawable);
            }
        } else {
            if (smallDrawable != null) {
                largeIcon.setImageDrawable(smallDrawable);
            }
            smallIcon.setVisibility(View.GONE);
        }
        title.setText(info.getTitle());
        content.setText(info.getContent());
        itemView.setTag(String.valueOf(info.getId()));
    }

    private View createNotificationItemView(NotificationInfo info) {
        final View view = View.inflate(getContext(), R.layout.notification_item_layout, null);
        updateNotificationItem(view, info);
        return view;
    }

    @Override
    protected void onDetachedFromWindow() {
        LockScreenManager.getInstance().unRegistMainPanelListener(mMainPanelListener);
        LockScreenManager.getInstance().unRegistPullDownListener(mPullDownListener);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        LockScreenManager.getInstance().registMainPanelListener(mMainPanelListener);
        LockScreenManager.getInstance().registPullDownListener(mPullDownListener);
        super.onAttachedToWindow();
    }

    /**
     * 锁屏页右划解锁的监听器
     */
    private IMainPanelListener mMainPanelListener = new IMainPanelListener() {

        @Override
        public void onMainPanelOpened() {

        }

        @Override
        public void onMainPanelClosed() {
            resetState();
        }
    };

    /**
     * 锁屏页下拉抽屉的监听器
     */
    private IPullDownListener mPullDownListener = new IPullDownListener() {

        @Override
        public void onStartPullDown() {
            resetState();
        }
    };

    private void resetState() {
        if (mCurrentTouchView != null) {
            View contentLayout = mCurrentTouchView.findViewById(R.id.blackOverlay);
            contentLayout.setBackgroundColor(Color.TRANSPARENT);
            mCurrentTouchView.findViewById(R.id.doubleTapDeleteTip).setVisibility(View.INVISIBLE);
            mCurrentTouchView.findViewById(R.id.rightArrow).setTranslationX(0);
            mCurrentTouchView.findViewById(R.id.rightArrow).setVisibility(View.INVISIBLE);
        }
        if (mRightArrowAnimator != null) {
            mRightArrowAnimator.cancel();
            mRightArrowAnimator = null;
        }
        LockScreenManager.getInstance().setRunnableAfterUnLock(null);
    }
}
