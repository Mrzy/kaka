
package cn.zmdx.kaka.fast.locker.notification.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.zmdx.kaka.fast.locker.HDApplication;
import cn.zmdx.kaka.fast.locker.LockScreenManager;
import cn.zmdx.kaka.fast.locker.LockScreenManager.IMainPanelListener;
import cn.zmdx.kaka.fast.locker.LockScreenManager.IPullDownListener;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.database.CustomNotificationModel;
import cn.zmdx.kaka.fast.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.fast.locker.notification.Constants;
import cn.zmdx.kaka.fast.locker.notification.NotificationInfo;
import cn.zmdx.kaka.fast.locker.notification.NotificationInterceptor;
import cn.zmdx.kaka.fast.locker.notification.NotificationPreferences;
import cn.zmdx.kaka.fast.locker.notification.PandoraNotificationFactory;
import cn.zmdx.kaka.fast.locker.notification.PandoraNotificationService;
import cn.zmdx.kaka.fast.locker.notification.guide.NotificationGuideHelper;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.fast.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.fast.locker.widget.CircleImageView;

public class NotificationLayout extends LinearLayout {

    private NotificationInterceptor mInterceptor;

    private Map<String, NotificationInfo> mActiveNotification = new HashMap<String, NotificationInfo>();

    private View mCurrentTouchView;

    private static final long ITEM_DOUBLE_TAP_DURATION = 200;

    protected static final int GAP_BETWEEN_NOTIFICATIONS = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 5);

    protected static final int GAP_ITEM_LEFT_MARGIN = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 35);

    protected static final int GAP_ITEM_RIGHT_MARGIN = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 15);

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat sSdf = new SimpleDateFormat("HH:mm");

    protected static final float ITEM_RIGHT_ANIMATOR_DISTANCE = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 50);

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
        setGravity(Gravity.CENTER_HORIZONTAL);
        final LayoutTransition transitioner = new LayoutTransition();
        setLayoutTransition(transitioner);
        initLayoutAnimation(transitioner);
    }

    private void initLayoutAnimation(LayoutTransition transition) {
        Animator addAnimator = null;
        Animator removeAnimator = null;
        // Animator changingAddAnimator = null;
        // Animator changingRemoveAnimator = null;

        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f);
        addAnimator = ObjectAnimator.ofPropertyValuesHolder(this, pvhScaleX, pvhScaleY)
                .setDuration(transition.getDuration(LayoutTransition.APPEARING));
        addAnimator.setInterpolator(new OvershootInterpolator());
        transition.setAnimator(LayoutTransition.APPEARING, addAnimator);

        PropertyValuesHolder rmScaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0);
        PropertyValuesHolder rmScaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0);
        removeAnimator = ObjectAnimator.ofPropertyValuesHolder(this, rmScaleX, rmScaleY)
                .setDuration(transition.getDuration(LayoutTransition.DISAPPEARING));
        removeAnimator.setInterpolator(new AccelerateInterpolator());
        transition.setAnimator(LayoutTransition.DISAPPEARING, removeAnimator);
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
                updateNotificationItem(((View) view.getParent()), info);
            } else {
                final View itemView = createNotificationItemView(info);
                View leftIcon = itemView.findViewById(R.id.leftIcon);
                leftIcon.setOnTouchListener(new ItemViewTouchListener(itemView));
                leftIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - mItemClickStartTime < ITEM_DOUBLE_TAP_DURATION) {// 响应双击事件
                            final String id = String.valueOf(v.getTag());
                            final NotificationInfo ni = mActiveNotification.get(id);
                            if (ni != null) {
                                if (ni.getType() == NotificationInfo.NOTIFICATION_TYPE_CUSTOM) {
                                    final int intId = Integer.valueOf(id);
                                    if (intId == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_GUIDE_HIDE_MESSAGE) {
                                        NotificationGuideHelper
                                                .markAlreadyPromptHideNotificationMsg(getContext());
                                    } else if (Integer.valueOf(id) == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_GUIDE_REMOVE) {
                                        NotificationGuideHelper.recordGuideProgress(getContext());
                                        NotificationInfo info = NotificationGuideHelper
                                                .getNextGuide(getContext());
                                        if (info != null) {
                                            NotificationInterceptor.getInstance(getContext())
                                            .sendCustomNotification(info);
                                        }
                                    }
                                }
                                NotificationPreferences prefer = NotificationPreferences
                                        .getInstance(getContext());
                                if (ni.getType() == NotificationInfo.NOTIFICATION_TYPE_SYSTEM
                                        && !prefer.isAlreadyRemovedNotification()) {
                                    NotificationPreferences.getInstance(getContext())
                                            .markAlreadyRemovedNotification();
                                }
                            }
                            removeNotification(id);
                            UmengCustomEventManager.statisticalRemoveNotification(info.getId(),
                                    info.getPkg(), info.getType());
                        } else {
                            mItemClickStartTime = currentTime;
                        }
                    }
                });
                addNotificationItem(itemView);
                if (isWinxinOrQQ(info) && !hasAlreadyPromptHideNotificationMsg()
                        && PandoraConfig.newInstance(getContext()).isShowNotificationMessage()
                        && !TextUtils.isEmpty(info.getTitle())
                        && !TextUtils.isEmpty(info.getContent())) {
                    NotificationInfo ni = PandoraNotificationFactory
                            .createGuideHideNotificationInfo();
                    NotificationInterceptor.getInstance(getContext()).sendCustomNotification(ni);
                    NotificationGuideHelper.markAlreadyPromptHideNotificationMsg(getContext());
                }
                UmengCustomEventManager.statisticalPostNotification(info.getId(), info.getPkg(),
                        info.getType());
            }
        }
    };

    private boolean isWinxinOrQQ(NotificationInfo ni) {
        return Constants.PKGNAME_QQ.equals(ni.getPkg())
                || Constants.PKGNAME_WEIXIN.equals(ni.getPkg());
    }

    private boolean hasAlreadyPromptHideNotificationMsg() {
        return NotificationGuideHelper.hasAlreadyPromptHideNotificationMsg(getContext());
    }

    /**
     * 创建ItemView，并添加到本容器的顶部
     * 
     * @param itemView
     */
    private void addNotificationItem(View itemView) {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = GAP_BETWEEN_NOTIFICATIONS;
        lp.leftMargin = GAP_ITEM_LEFT_MARGIN;
        lp.rightMargin = GAP_ITEM_RIGHT_MARGIN;
        addView(itemView, 0, lp);
    }

    @SuppressLint("NewApi")
    private void removeNotification(String notifyId) {
        // 从view容器中将这个通知view移除
        View targetView = findViewWithTag(notifyId);
        if (targetView != null) {
            removeView((View) targetView.getParent());
        }

        NotificationInfo info = mActiveNotification.get(notifyId);
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
                    mCurrentTouchView = mItemView;
                    v.performClick();
                    showContentWithAnimator(mItemView.findViewById(R.id.rightArea));
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
                            if (Integer.valueOf(id) == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_GUIDE_HIDE_MESSAGE) {
                                NotificationGuideHelper
                                        .markAlreadyPromptHideNotificationMsg(getContext());
                            } else if (Integer.valueOf(id) == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_GUIDE_OPENDETAIL) {
                                NotificationGuideHelper.recordGuideProgress(getContext());
                            } else if (Integer.parseInt(id) == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_OPEN_PERMISSION) {
                                // 启动设置通知权限引导界面
                                HDBThreadUtils.postOnUiDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast.makeText(
                                                getContext(),
                                                getResources().getString(
                                                        R.string.tip_open_notification_permission),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }, 200);
                            }
                            NotificationPreferences prefer = NotificationPreferences
                                    .getInstance(getContext());
                            if (info.getType() == NotificationInfo.NOTIFICATION_TYPE_SYSTEM
                                    && !prefer.isAlreadyOpenedNotification()) {
                                prefer.markAlreadyOpenedNotification();
                            }
                            removeNotification(id);
                            UmengCustomEventManager.statisticalOpenNotification(info.getId(),
                                    info.getPkg(), info.getType());
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

    private void showContentWithAnimator(View rightView) {
        rightView.setAlpha(0);
        rightView.setVisibility(View.VISIBLE);
        rightView.animate().alpha(1).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // rightView.setVisibility(V)
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        }).start();
    }

    public void clearAll() {
        // TODO
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    };

    private void updateNotificationItem(View itemView, NotificationInfo info) {
        // final RippleView largeIcon = (RippleView)
        // itemView.findViewById(R.id.largeIcon);
        final ImageView smallIcon = (ImageView) itemView.findViewById(R.id.smallIcon);
        final TextView title = (TextView) itemView.findViewById(R.id.title);
        final TextView content = (TextView) itemView.findViewById(R.id.content);
        final TextView date = (TextView) itemView.findViewById(R.id.date);
        final CircleImageView circleIv = (CircleImageView) itemView.findViewById(R.id.circleIv);
        final View handleTip = itemView.findViewById(R.id.handleTip);

        long postTime = info.getPostTime() == 0 ? new Date().getTime() : info.getPostTime();
        date.setText(sSdf.format(postTime));
        boolean showMsg = PandoraConfig.newInstance(getContext()).isShowNotificationMessage();
        Bitmap largeBmp = info.getLargeIcon();
        Drawable smallDrawable = info.getSmallIcon();
        title.setText(info.getTitle());
        itemView.findViewById(R.id.leftIcon).setTag(String.valueOf(info.getId()));
        if (!showMsg && isWinxinOrQQ(info)) {
            circleIv.setImageDrawable(smallDrawable);
            content.setText(getContext().getString(R.string.hide_message_tip));
            smallIcon.setVisibility(View.GONE);
        } else {
            if (largeBmp != null) {
                circleIv.setImageBitmap(largeBmp);
                if (smallDrawable != null) {
                    smallIcon.setImageDrawable(smallDrawable);
                }
            } else {
                if (smallDrawable != null) {
                    circleIv.setImageDrawable(smallDrawable);
                }
                smallIcon.setVisibility(View.GONE);
            }
            content.setText(info.getContent());
        }
        final NotificationPreferences prefer = NotificationPreferences.getInstance(getContext());
        if (prefer.isAlreadyOpenedNotification()&& prefer.isAlreadyRemovedNotification()) {
            handleTip.setVisibility(View.GONE);
        }
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
        sendGuideNotificationIfNeeded();
        super.onAttachedToWindow();
    }

    private void sendGuideNotificationIfNeeded() {
        final NotificationInfo guideNi = NotificationGuideHelper.getNextGuide(getContext());
        if (guideNi != null) {
            NotificationInterceptor.getInstance(getContext()).sendCustomNotification(guideNi);
        }
    }

    private boolean mIsRunRightArrowAnimator = false;

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

        @Override
        public void onMainPanelSlide(View panel, float slideOffset) {
            // 为通知view执行一个向右的偏移动画
            if (!mIsRunRightArrowAnimator) {
                if (mCurrentTouchView != null) {
                    mIsRunRightArrowAnimator = true;
                    mCurrentTouchView.animate().translationX(ITEM_RIGHT_ANIMATOR_DISTANCE)
                            .setDuration(300).start();
                }
            }
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
            final View rightView = mCurrentTouchView.findViewById(R.id.rightArea);
            dismissContentViewWithAnimator(rightView);

            // 如果通知view执行了向右的偏移动画，将其恢复原位
            if (mIsRunRightArrowAnimator) {
                mCurrentTouchView.animate().translationX(0).setDuration(300).start();
                mIsRunRightArrowAnimator = false;
            }
            mCurrentTouchView = null;
        }
        LockScreenManager.getInstance().setRunnableAfterUnLock(null);
    }

    private void dismissContentViewWithAnimator(final View rightView) {
        rightView.animate().alpha(0).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rightView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                rightView.setVisibility(View.GONE);
            }
        }).start();
    }
}
