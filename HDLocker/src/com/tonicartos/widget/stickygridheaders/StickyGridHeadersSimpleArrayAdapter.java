
package com.tonicartos.widget.stickygridheaders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.notify.filter.NotifyFilterManager;
import cn.zmdx.kaka.locker.notify.filter.NotifyFilterManager.NotifyFilterEntity;
import cn.zmdx.kaka.locker.notify.filter.NotifyFilterUtil;

public class StickyGridHeadersSimpleArrayAdapter extends BaseAdapter implements
        StickyGridHeadersSimpleAdapter {

    private LayoutInflater mInflater;

    private ArrayList<NotifyFilterEntity> mItems;

    // private NotifySectionIndexer mIndexer;

    public ArrayList<NotifyFilterEntity> getAdapterData() {
        return mItems;
    }

    private Context mContext;

    public StickyGridHeadersSimpleArrayAdapter(Context context, ArrayList<NotifyFilterEntity> items) {
        init(context, items);
        // if (mItems != null) {
        // mIndexer = new NotifySectionIndexer(listCompare.getAlphaIndexer(),
        // listCompare.getSections());
        // } else {
        // mIndexer = null;
        // }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public long getHeaderId(int position) {
        NotifyFilterEntity item = getItem(position);
        String value = item.getNotifyUSName();
        return ((String) value).substring(0, 1).toUpperCase(Locale.getDefault()).charAt(0);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_notify_manager_item_header, parent,
                    false);
            holder = new HeaderViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.item_header);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        SparseIntArray sparseIntArray = NotifyFilterUtil.initAppSize(mContext);
        int headPaddingLeft = sparseIntArray.get(NotifyFilterUtil.KEY_HEAD_PADDING_LEFT);
        holder.textView.setPadding(headPaddingLeft, 0, 0, 0);

        NotifyFilterEntity item = getItem(position);
        String usName = item.getNotifyUSName();
        if (usName.contains(NotifyFilterManager.RECENT_TASK_MARK)) {
            holder.textView.setText(NotifyFilterManager.RECENT_TASK_MARK_PROMPT);
        } else if (usName.contains(NotifyFilterManager.APP_NUMBER_TASK_MARK)) {
            holder.textView.setText(NotifyFilterManager.RECENT_TASK_MARK);
        } else {
            holder.textView.setText(item.getNotifyUSName().substring(0, 1)
                    .toUpperCase(Locale.getDefault()));
        }

        return convertView;
    }

    @Override
    public NotifyFilterEntity getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private ViewHolder viewHolder = null;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_notify_manager_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mNotifyAppLayout = (RelativeLayout) convertView
                    .findViewById(R.id.notify_app_layout);
            viewHolder.mNotifyAppIcon = (ImageView) convertView.findViewById(R.id.notify_app_icon);
            viewHolder.mNotifyAppName = (TextView) convertView.findViewById(R.id.notify_app_name);
            viewHolder.mNotifySelect = (ImageView) convertView.findViewById(R.id.notify_app_select);
            convertView.setTag(viewHolder);

            SparseIntArray sparseIntArray = NotifyFilterUtil.initAppSize(mContext);
            int layoutWidth = sparseIntArray.get(NotifyFilterUtil.KEY_LAYOUT_WIDTH);
            int imageWidth = sparseIntArray.get(NotifyFilterUtil.KEY_IMAGE_WIDTH);
            int imageHeight = sparseIntArray.get(NotifyFilterUtil.KEY_IMAGE_HEIGHT);

            viewHolder.mNotifySelect.setPadding(0, 0, (layoutWidth - imageWidth) / 2, 0);

            LayoutParams layoutParams = viewHolder.mNotifyAppLayout.getLayoutParams();
            layoutParams.width = layoutWidth;
            layoutParams.height = LayoutParams.WRAP_CONTENT;
            viewHolder.mNotifyAppLayout.setLayoutParams(layoutParams);

            LayoutParams params = viewHolder.mNotifyAppIcon.getLayoutParams();
            params.width = imageWidth;
            params.height = imageHeight;
            viewHolder.mNotifyAppIcon.setLayoutParams(params);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        NotifyFilterEntity item = mItems.get(position);
        viewHolder.mNotifyAppIcon.setImageDrawable(item.getNotifyIcon());
        viewHolder.mNotifyAppName.setText(item.getNotifyCHName());

        if (item.isSelect()) {
            viewHolder.mNotifySelect.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mNotifySelect.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void init(Context context, ArrayList<NotifyFilterEntity> items) {
        this.mItems = items;
        this.mContext = context;
        Collections.sort(mItems, new SortIgnoreCase());
        mInflater = LayoutInflater.from(context);
    }

    public class SortIgnoreCase implements Comparator<NotifyFilterEntity> {
        public int compare(NotifyFilterEntity s1, NotifyFilterEntity s2) {
            return s1.getNotifyUSName().compareToIgnoreCase(s2.getNotifyUSName());
        }
    }

    protected class HeaderViewHolder {
        public TextView textView;
    }

    private class ViewHolder {

        private RelativeLayout mNotifyAppLayout;

        private ImageView mNotifyAppIcon;

        private ImageView mNotifySelect;

        private TextView mNotifyAppName;
    }

    // @Override
    // public Object[] getSections() {
    // if (mIndexer == null) {
    // return new String[] {
    // " "
    // };
    // }
    // synchronized (mIndexer) {
    // return mIndexer.getSections();
    // }
    // }
    //
    // @Override
    // public int getPositionForSection(int section) {
    // if (mIndexer == null) {
    // return -1;
    // }
    //
    // synchronized (mIndexer) {
    // return mIndexer.getPositionForSection(section);
    // }
    // }
    //
    // @Override
    // public int getSectionForPosition(int position) {
    // if (mIndexer == null) {
    // return -1;
    // }
    //
    // synchronized (mIndexer) {
    // return mIndexer.getSectionForPosition(position);
    // }
    // }
}
