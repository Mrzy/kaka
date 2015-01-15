
package cn.zmdx.kaka.locker.meiwen.notification.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.zmdx.kaka.locker.meiwen.HDApplication;
import cn.zmdx.kaka.locker.meiwen.LockScreenManager;
import cn.zmdx.kaka.locker.meiwen.LockScreenManager.IMainPanelListener;
import cn.zmdx.kaka.locker.meiwen.LockScreenManager.IPullDownListener;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.database.CustomNotificationModel;
import cn.zmdx.kaka.locker.meiwen.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.meiwen.notification.Constants;
import cn.zmdx.kaka.locker.meiwen.notification.NotificationInfo;
import cn.zmdx.kaka.locker.meiwen.notification.NotificationInterceptor;
import cn.zmdx.kaka.locker.meiwen.notification.PandoraNotificationFactory;
import cn.zmdx.kaka.locker.meiwen.notification.PandoraNotificationService;
import cn.zmdx.kaka.locker.meiwen.notification.guide.NotificationGuideHelper;
import cn.zmdx.kaka.locker.meiwen.settings.InitPromptActivity;
import cn.zmdx.kaka.locker.meiwen.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.meiwen.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.meiwen.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.meiwen.utils.HDBThreadUtils;

public class NotificationLayout extends LinearLayout {

    private NotificationInterceptor mInterceptor;

    private Map<String, NotificationInfo> mActiveNotification = new HashMap<String, NotificationInfo>();

    private View mCurrentTouchView;

    private static final long ITEM_DOUBLE_TAP_DURATION = 200;

    protected static final int GAP_BETWEEN_NOTIFICATIONS = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 5);

    // protected static final int NOTIFICATION_ITEM_HEIGHT =
    // BaseInfoHelper.dip2px(
    // HDApplication.mContext, 64);

    private static final SimpleDateFormat sSdf = new SimpleDateFormat("dd日 HH:mm");

    private Context mContext;

    public NotificationLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public NotificationLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationLayout(Context context) {
        this(context, null);
    }

    private void init() {
        mInterceptor = NotificationInterceptor.getInstance(mContext);
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
                        if (currentTime - mItemClickStartTime < ITEM_DOUBLE_TAP_DURATION) {// 响应双击事件
                            final String id = String.valueOf(v.getTag());
                            final NotificationInfo ni = mActiveNotification.get(id);
                            if (ni.getType() == NotificationInfo.NOTIFICATION_TYPE_CUSTOM) {
                                final int intId = Integer.valueOf(id);
                                if (intId == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_GUIDE_HIDE_MESSAGE) {
                                    NotificationGuideHelper
                                            .markAlreadyPromptHideNotificationMsg(mContext);
                                } else if (Integer.valueOf(id) == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_GUIDE_REMOVE) {
                                    NotificationGuideHelper.recordGuideProgress(mContext);
                                    NotificationInfo info = NotificationGuideHelper
                                            .getNextGuide(mContext);
                                    NotificationInterceptor.getInstance(mContext)
                                            .sendCustomNotification(info);
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
                        && PandoraConfig.newInstance(mContext).isShowNotificationMessage()) {
                    NotificationInfo ni = PandoraNotificationFactory
                            .createGuideHideNotificationInfo();
                    NotificationInterceptor.getInstance(mContext).sendCustomNotification(ni);
                    NotificationGuideHelper.markAlreadyPromptHideNotificationMsg(mContext);
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
        return NotificationGuideHelper.hasAlreadyPromptHideNotificationMsg(mContext);
    }

    /**
     * 移除通知教学完成，记录状态
     * 
     * @param id 通知id
     */
    private void recordGuideStateIfNeeded(String id) {
        final NotificationInfo ni = mActiveNotification.get(id);
        int intId = Integer.valueOf(id);
        if (intId == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_GUIDE_REMOVE
                && ni.getType() == NotificationInfo.NOTIFICATION_TYPE_CUSTOM) {
            NotificationGuideHelper.recordGuideProgress(mContext);
        } else if (intId == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_GUIDE_OPENDETAIL) {
            NotificationGuideHelper.recordGuideProgress(mContext);
        }
    }

    /**
     * 创建ItemView，并添加到本容器的顶部
     * 
     * @param itemView
     */
    private void addNotificationItem(View itemView) {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = GAP_BETWEEN_NOTIFICATIONS;
        addView(itemView, 0, lp);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void removeNotification(String notifyId) {
        // 从view容器中将这个通知view移除
        View targetView = findViewWithTag(notifyId);
        if (targetView != null) {
            removeView(targetView);
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
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
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
                    v.performClick();
                    mCurrentTouchView = mItemView;
                    View contentLayout = mItemView.findViewById(Res.id.pandora_notification_hint);
                    contentLayout
                            .setBackgroundResource(Res.drawable.pandora_notification_click_shape);
                    mItemView.findViewById(Res.id.handleTip).setVisibility(View.VISIBLE);
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
                                        .markAlreadyPromptHideNotificationMsg(mContext);
                            } else if (Integer.valueOf(id) == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_GUIDE_OPENDETAIL) {
                                NotificationGuideHelper.recordGuideProgress(mContext);
                            } else if (Integer.parseInt(id) == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_OPEN_PERMISSION) {
                                // 启动设置通知权限引导界面
                                Toast.makeText(
                                        mContext,
                                        getResources().getString(
                                                Res.string.open_notification_permission_toast),
                                        Toast.LENGTH_LONG).show();
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

    public void clearAll() {
        // TODO
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    };

    private void updateNotificationItem(View itemView, NotificationInfo info) {
        final ImageView largeIcon = (ImageView) itemView.findViewById(Res.id.largeIcon);
        final ImageView smallIcon = (ImageView) itemView.findViewById(Res.id.smallIcon);
        final TextView title = (TextView) itemView.findViewById(Res.id.title);
        final TextView content = (TextView) itemView.findViewById(Res.id.content);
        final TextView date = (TextView) itemView.findViewById(Res.id.date);
        long postTime = info.getPostTime() == 0 ? new Date().getTime() : info.getPostTime();
        date.setText(sSdf.format(postTime));
        boolean showMsg = PandoraConfig.newInstance(mContext).isShowNotificationMessage();
        // Bitmap largeBmp = info.getLargeIcon();
        // Drawable smallDrawable = info.getSmallIcon();
        title.setText(info.getTitle());
        itemView.setTag(String.valueOf(info.getId()));
        smallIcon.setVisibility(View.GONE);
        final Resources res = getResources();
        final String pkgName = info.getPkg();
        if (info.getType() == NotificationInfo.NOTIFICATION_TYPE_SYSTEM) {
            if (Constants.PKGNAME_QQ.equals(pkgName)) {
                largeIcon.setImageResource(Res.drawable.notification_qq);
                String msg = showMsg ? info.getContent() : "QQ:"
                        + res.getString(Res.string.hide_message_tip);
                content.setText(msg);
            } else if (Constants.PKGNAME_WEIXIN.equals(pkgName)) {
                largeIcon.setImageResource(Res.drawable.notification_wechat);
                String msg = showMsg ? info.getContent() : "微信:"
                        + res.getString(Res.string.hide_message_tip);
                content.setText(msg);
            } else if (isCallNotification(mContext, pkgName)) {
                largeIcon.setImageResource(Res.drawable.notification_missed_call);
                content.setText(info.getContent());
            } else if (isSmsNotification(mContext, pkgName)) {
                largeIcon.setImageResource(Res.drawable.notification_message);
                content.setText(info.getContent());
            }
        } else {
            content.setText(info.getContent());
            largeIcon.setImageBitmap(info.getLargeIcon());
        }
    }

    private boolean isCallNotification(Context mContext, String pkgName) {
        PackageManager sPackageManager = mContext.getPackageManager();
        Intent dialerIntent = new Intent(Intent.ACTION_DIAL);
        List<ResolveInfo> intentResolveInfos = sPackageManager.queryIntentActivities(dialerIntent,
                PackageManager.GET_RECEIVERS);
        if (intentResolveInfos.size() < 1) {
            return false;
        }
        for (ResolveInfo ri : intentResolveInfos) {
            if (ri.activityInfo.packageName.equals(pkgName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSmsNotification(Context context, String pkgName) {
        PackageManager sPackageManager = context.getPackageManager();
        Intent smsIntent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            smsIntent.setAction("android.provider.Telephony.SMS_DELIVER");// 4.4以上
        } else {
            smsIntent.setAction("android.provider.Telephony.SMS_RECEIVED");
        }
        List<ResolveInfo> resolveInfos = sPackageManager.queryBroadcastReceivers(smsIntent,
                PackageManager.GET_RECEIVERS);
        for (ResolveInfo ri : resolveInfos) {
            if (ri.activityInfo.packageName.equals(pkgName)) {
                return true;
            }
        }
        return false;
    }

    private View createNotificationItemView(NotificationInfo info) {
        final View view = View.inflate(mContext, Res.layout.notification_item_layout, null);
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
        sendOpenNotificationService();
        super.onAttachedToWindow();
    }

    private void sendOpenNotificationService() {
        final NotificationInfo guideNi = NotificationGuideHelper.getNextGuide(mContext);
        if (guideNi != null) {
            NotificationInterceptor.getInstance(mContext).sendCustomNotification(guideNi);
        }
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
            View contentLayout = mCurrentTouchView.findViewById(Res.id.pandora_notification_hint);
            contentLayout.setBackgroundResource(Res.drawable.pandora_notification_shape);
            mCurrentTouchView.findViewById(Res.id.handleTip).setVisibility(View.GONE);
        }
        LockScreenManager.getInstance().setRunnableAfterUnLock(null);
    }
}
