
package cn.zmdx.kaka.locker.notification.adapter;

import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
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
import cn.zmdx.kaka.locker.notification.view.SwipeLayout.OnSwipeLayoutListener;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.TimeUtils;

public class NotificationListViewAdapter extends BaseAdapter {

    private static final int ITEM_HEIGHT = BaseInfoHelper.dip2px(HDApplication.getContext(), 66);

    private Context mContext;

    private List<NotificationInfo> mData;

    public NotificationListViewAdapter(Context context, List<NotificationInfo> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public NotificationInfo getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            SwipeLayout itemView = new SwipeLayout(mContext);
            itemView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, ITEM_HEIGHT));
            View leftView = View
                    .inflate(mContext, R.layout.notification_item_leftview_layout, null);
            View rightView = View.inflate(mContext, R.layout.notification_item_rightview_layout,
                    null);
            View upperView = View.inflate(mContext, R.layout.notification_item_layout, null);
            itemView.addLeftView(leftView);
            itemView.addRightView(rightView);
            itemView.addUpperView(upperView);

            convertView = itemView;
            holder.swipeLayout = itemView;
            // holder.swipeLayout.setOnSwipeLayoutListener(this);
            holder.largeIconIv = (ImageView) itemView.getUpperView().findViewById(R.id.largeIcon);
            holder.smallIconIv = (ImageView) itemView.getUpperView().findViewById(R.id.smallIcon);
            holder.titleTv = (TextView) itemView.getUpperView().findViewById(R.id.title);
            holder.contentTv = (TextView) itemView.getUpperView().findViewById(R.id.content);
            holder.dateTv = (TextView) itemView.getUpperView().findViewById(R.id.date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final NotificationInfo info = mData.get(position);
        holder.dateTv.setText(TimeUtils.getInterval(mContext, info.getPostTime()));
        boolean hideMsg = PandoraConfig.newInstance(mContext).isHideNotifyContent();
        Bitmap largeBmp = info.getLargeIcon();
        Drawable smallDrawable = info.getSmallIcon();
        holder.titleTv.setText(info.getTitle());
        holder.titleTv.setTag(info);
        if (hideMsg) {
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

        holder.swipeLayout.reset();
        holder.swipeLayout.setOnSwipeLayoutListener(new OnSwipeLayoutListener() {

            @Override
            public void onOpened(SwipeLayout swipeLayout, int direction) {
                // ViewHolder holder = (ViewHolder) swipeLayout.getTag();
                // NotificationInfo info = (NotificationInfo)
                // holder.titleTv.getTag();
                NotificationInfo info = mData.get(pos);
                if (direction == SwipeLayout.OPEN_DIRECTION_LEFT) {
                    openNotification(info);
                    // remove(info);
                } else if (direction == SwipeLayout.OPEN_DIRECTION_RIGHT) {
                    remove(info);
                }
            }

            @Override
            public void onClosed() {
            }

            @Override
            public void onSlide(SwipeLayout layout, int position, float offset) {
                if (position == 0) {
                    if (offset < 0.2) {
                        layout.getLeftView().setAlpha(offset);
                    }
                    layout.getLeftView().setPivotX(layout.getLeftView().getWidth());
                    layout.getLeftView().setPivotY(layout.getLeftView().getHeight() / 2);
                    layout.getLeftView().setScaleX(1 + Math.min((1.0f - offset), 0.45f));
                    layout.getLeftView().setScaleY(1 + Math.min((1.0f - offset), 0.45f));
                } else if (position == 1) {
                    if (offset > 0.8) {
                        layout.getRightView().setAlpha(1.0f - offset);
                    }
                    layout.getRightView().setPivotX(0);
                    layout.getRightView().setPivotY(layout.getRightView().getHeight() / 2);
                    layout.getRightView().setScaleX(1 + Math.min(offset, 0.45f));
                    layout.getRightView().setScaleY(1 + Math.min(offset, 0.45f));
                }
            }
        });
        return convertView;
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

                remove(info);

                UmengCustomEventManager.statisticalOpenNotification(info.getId(), info.getPkg(),
                        info.getType());
            }
        });
        LockScreenManager.getInstance().unLock();
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
        if (mData.remove(info)) {
            notifyDataSetChanged();
        }
    }

    private boolean isWinxinOrQQ(NotificationInfo ni) {
        return Constants.PKGNAME_QQ.equals(ni.getPkg())
                || Constants.PKGNAME_WEIXIN.equals(ni.getPkg());
    }

    static final class ViewHolder {
        private SwipeLayout swipeLayout;

        private ImageView largeIconIv, smallIconIv;

        private TextView titleTv, contentTv, dateTv;
    }
}
