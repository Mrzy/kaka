
package cn.zmdx.kaka.locker.notification.adapter;

import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.database.CustomNotificationModel;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.notification.Constants;
import cn.zmdx.kaka.locker.notification.NotificationInfo;
import cn.zmdx.kaka.locker.notification.PandoraNotificationFactory;
import cn.zmdx.kaka.locker.notification.PandoraNotificationService;
import cn.zmdx.kaka.locker.notification.guide.NotificationGuideHelper;
import cn.zmdx.kaka.locker.notification.view.SwipeLayout;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.TimeUtils;

public class NotificationAdapter extends Adapter<NotificationAdapter.ViewHolder> {

    private static final int ITEM_HEIGHT = BaseInfoHelper.dip2px(HDApplication.getContext(), 66);

    private Context mContext;

    private List<NotificationInfo> mData;

    public class ViewHolder extends RecyclerView.ViewHolder implements
            SwipeLayout.OnSwipeLayoutListener {

        private SwipeLayout swipeLayout;

        private ImageView largeIconIv, smallIconIv;

        private TextView titleTv, contentTv, dateTv;

        public ViewHolder(View view) {
            super(view);
            swipeLayout = (SwipeLayout) view;
            swipeLayout.setOnSwipeLayoutListener(this);
            largeIconIv = (ImageView) swipeLayout.getUpperView().findViewById(R.id.largeIcon);
            smallIconIv = (ImageView) swipeLayout.getUpperView().findViewById(R.id.smallIcon);
            titleTv = (TextView) swipeLayout.getUpperView().findViewById(R.id.title);
            contentTv = (TextView) swipeLayout.getUpperView().findViewById(R.id.content);
            dateTv = (TextView) swipeLayout.getUpperView().findViewById(R.id.date);
        }

        @Override
        public void onOpened(SwipeLayout swipeLayout, int direction) {
            NotificationInfo info = (NotificationInfo) swipeLayout.getTag();
            if (direction == SwipeLayout.OPEN_DIRECTION_LEFT) {
                openNotification(info);
                remove(info);
            } else if (direction == SwipeLayout.OPEN_DIRECTION_RIGHT) {
                remove(info);
            }
            swipeLayout.setAlpha(0);
            swipeLayout.reset();
        }

        @Override
        public void onClosed() {
        }

        @Override
        public void onSlide(SwipeLayout layout,int position, float offset) {
        }
    }

    public NotificationAdapter(Context context, List<NotificationInfo> data) {
        mContext = context;
        mData = data;
    }

    public void replace(NotificationInfo oldInfo, NotificationInfo info) {
        final int position = mData.indexOf(oldInfo);
        if (position != -1) {
            mData.remove(position);
            HDBThreadUtils.runOnUi(new Runnable() {

                @Override
                public void run() {
                    notifyItemRemoved(position);
                }
            });

            add(info, 0);
        }
    }

    public void add(NotificationInfo info, final int position) {
        mData.add(position, info);
        HDBThreadUtils.runOnUi(new Runnable() {

            @Override
            public void run() {
                notifyItemInserted(position);
            }
        });
    }

    public void remove(NotificationInfo info) {
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
        }
        final int position = mData.indexOf(info);
        if (position != -1) {
            mData.remove(position);
            HDBThreadUtils.runOnUi(new Runnable() {

                @Override
                public void run() {
                    notifyItemRemoved(position);
                }
            });
        }
    }

    private void openNotification(final NotificationInfo info) {
        LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {

            @Override
            public void run() {
                try {
                    PendingIntent pi = info.getPendingIntent();
                    if (pi != null) {
                        pi.send();
                    }
                } catch (Exception e) {
                }
                int id = Integer.valueOf(info.getId());
                if (Integer.valueOf(id) == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_GUIDE_HIDE_MESSAGE) {
                    NotificationGuideHelper.markAlreadyPromptHideNotificationMsg(mContext);
                } else if (id == PandoraNotificationFactory.ID_CUSTOM_NOTIFICATION_OPEN_PERMISSION) {
                    // 启动设置通知权限引导界面
                    HDBThreadUtils.postOnUiDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(
                                    mContext,
                                    mContext.getResources().getString(
                                            R.string.tip_open_notification_permission),
                                    Toast.LENGTH_LONG).show();
                        }
                    }, 200);
                }
                UmengCustomEventManager.statisticalOpenNotification(info.getId(), info.getPkg(),
                        info.getType());
            }
        });
        LockScreenManager.getInstance().unLock();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        SwipeLayout itemView = new SwipeLayout(context);
        itemView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ITEM_HEIGHT));
        View leftView = View.inflate(context, R.layout.notification_item_leftview_layout, null);
        View rightView = View.inflate(context, R.layout.notification_item_rightview_layout, null);
        View upperView = View.inflate(context, R.layout.notification_item_layout, null);

        itemView.addLeftView(leftView);
        itemView.addRightView(rightView);
        itemView.addUpperView(upperView);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    private boolean isWinxinOrQQ(NotificationInfo ni) {
        return Constants.PKGNAME_QQ.equals(ni.getPkg())
                || Constants.PKGNAME_WEIXIN.equals(ni.getPkg());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NotificationInfo info = mData.get(position);
        holder.dateTv.setText(TimeUtils.getInterval(mContext, info.getPostTime()));
        boolean showMsg = PandoraConfig.newInstance(mContext).isShowNotificationMessage();
        Bitmap largeBmp = info.getLargeIcon();
        Drawable smallDrawable = info.getSmallIcon();
        holder.titleTv.setText(info.getTitle());
        holder.swipeLayout.setTag(info);
        if (!showMsg && isWinxinOrQQ(info)) {
            holder.largeIconIv.setImageDrawable(smallDrawable);
            holder.contentTv.setText(mContext.getString(R.string.hide_message_tip));
            holder.smallIconIv.setVisibility(View.GONE);
        } else {
            if (largeBmp != null) {
                holder.largeIconIv.setImageBitmap(largeBmp);
                if (smallDrawable != null) {
                    holder.smallIconIv.setImageDrawable(smallDrawable);
                }
            } else {
                if (smallDrawable != null) {
                    holder.largeIconIv.setImageDrawable(smallDrawable);
                }
                holder.smallIconIv.setVisibility(View.GONE);
            }
            holder.contentTv.setText(info.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
