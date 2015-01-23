
package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import java.util.List;

import cn.zmdx.kaka.fast.locker.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ToolbarAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private Context mContext;

    private List<QuickHelperItem> mQuickHelperItemsAll;

    public class ViewHolder {
        public ImageView icon;

        public TextView itemName;
    }

    public ToolbarAdapter(Context context, List<QuickHelperItem> helperItems) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mQuickHelperItemsAll = helperItems;
    }

    @Override
    public int getCount() {
        return mQuickHelperItemsAll.size();
    }

    @Override
    public Object getItem(int position) {
        return mQuickHelperItemsAll.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.sevenkey_item_layout, null);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.sevenkey_icon);
            // viewHolder.itemName = (TextView)
            // convertView.findViewById(Res.id.quick_item_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final QuickHelperItem item = (QuickHelperItem) getItem(position);
        switch (item.type) {
            case QuickHelperItem.TYPE_SWITCH:
                item.findTrackerBySwitchType(item.switchType);
                if (item.switchType != WidgetConfig.SWITCH_ID_BLUETOOTH) {
                    item.tracker.onActualStateChange(mContext, null);
                }
                item.tracker.refreshActualState(mContext);
                // viewHolder.itemName.setText(WidgetConfig.getSwitchName(item.switchType));
                viewHolder.icon.setBackgroundDrawable(null);
                viewHolder.icon.setImageResource(item.tracker.getIconResId(mContext, 1));
                break;
            default:
                break;
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                switch (item.type) {
                    case QuickHelperItem.TYPE_SWITCH:
                        updateQuickSwitch(item);
                        break;
                }
            }
        });
        return convertView;
    }

    private void updateQuickSwitch(QuickHelperItem item) {
        item.tracker.toggleState(mContext, null, null);
        notifyDataSetChanged();
    }
}
