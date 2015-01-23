
package cn.zmdx.kaka.fast.locker;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.fast.locker.widget.SwitchView;

public class SettingItemsAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater mInflater;

    private List<String> list;

    public SettingItemsAdapter(Context mContext, List<String> dataList) {
        this.context = mContext;
        mInflater = LayoutInflater.from(mContext);
        this.list = dataList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.activity_actionbar_listview_item, parent,
                    false);
            holder.settingItemIcon = (ImageView) convertView
                    .findViewById(R.id.fastlocker_setting_item_icon);
            holder.settingItemText = (TextView) convertView
                    .findViewById(R.id.fastlocker_setting_item_text);
            holder.lockScreenSwitch = (SwitchView) convertView
                    .findViewById(R.id.fastlocker_lockscreen_switch);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.settingItemIcon.setImageResource(R.drawable.fastlocker_setting_item_secure);
        holder.settingItemText.setText(list.get(position));
        if (position == 0) {
            holder.lockScreenSwitch.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public static class ViewHolder {
        // 设置项的名称
        public TextView settingItemText;

        // 设置项的图标
        public ImageView settingItemIcon;

        // 设置项的SwitchView
        public SwitchView lockScreenSwitch;

    }
}
