
package cn.zmdx.kaka.fast.locker;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.fast.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.widget.SwitchButton;

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

            convertView = mInflater.inflate(R.layout.activity_mainsetting_listview_item, parent,
                    false);

            holder.settingItemIcon = (ImageView) convertView
                    .findViewById(R.id.fastlocker_setting_item_icon);
            holder.settingItemText = (TextView) convertView
                    .findViewById(R.id.fastlocker_setting_item_text);
            holder.lockScreenSwitch = (SwitchButton) convertView
                    .findViewById(R.id.fastlocker_lockscreen_switch);
            holder.lockScreenSwitch.setChecked(isPandoraLockerOn());
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.settingItemText.setText(list.get(position));
        switch (position) {
            case 0:
                holder.lockScreenSwitch.setVisibility(View.VISIBLE);
                holder.lockScreenSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        enablePandoraLocker();
                        if (isChecked) {
                            UmengCustomEventManager.statisticalPandoraSwitchOpenTimes();
                        } else {
                            disablePandoraLocker();
                            UmengCustomEventManager.statisticalPandoraSwitchCloseTimes();
                        }
                    }
                });
                holder.settingItemIcon.setImageResource(R.drawable.fast_setting_secure);
                break;
            case 1:
                holder.settingItemIcon.setImageResource(R.drawable.fast_setting_password);
                break;
            case 2:
                holder.settingItemIcon.setImageResource(R.drawable.fast_setting_manage_wallpaper);
                break;
            case 3:
                holder.settingItemIcon
                        .setImageResource(R.drawable.fast_setting_notification_center);
                break;
            case 4:
                holder.settingItemIcon.setImageResource(R.drawable.fast_setting_shortcut);
                break;
            case 5:
                holder.settingItemIcon.setImageResource(R.drawable.fast_setting_init);
                break;
            case 6:
                holder.settingItemIcon.setImageResource(R.drawable.fast_setting_individual);
                break;
            case 7:
                holder.settingItemIcon.setImageResource(R.drawable.fast_setting_about);
                break;
            default:
                break;
        }
        return convertView;
    }

    protected void enablePandoraLocker() {
        PandoraConfig.newInstance(context).savePandolaLockerState(true);
    }

    protected void disablePandoraLocker() {
        PandoraConfig.newInstance(context).savePandolaLockerState(false);
    }

    protected boolean isPandoraLockerOn() {
        return PandoraConfig.newInstance(context).isPandolaLockerOn();
    }

    public static class ViewHolder {
        // 设置项的名称
        public TextView settingItemText;

        // 设置项的图标
        public ImageView settingItemIcon;

        // 设置项的SwitchView
        public SwitchButton lockScreenSwitch;

    }
}
