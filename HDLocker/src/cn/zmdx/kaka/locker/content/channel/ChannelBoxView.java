
package cn.zmdx.kaka.locker.content.channel;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

public class ChannelBoxView extends FrameLayout {

    private Context mContext;

    private ChannelBoxManager mChannelManager;

    private List<ChannelInfo> mSelectedChannels;

    private List<ChannelInfo> mUnSelectedChannels;

    private GridView mSelectedChannelGrid;

    private ListView mAllChannelListView;

    private ImageView mBackBtn;

    private BaseAdapter mGridAdapter, mListAdapter;

    public ChannelBoxView(Context context, ChannelBoxManager manager) {
        super(context);
        mContext = context;
        mChannelManager = manager;
        init();
    }

    private void init() {
        mSelectedChannels = mChannelManager.getSelectedChannels();
        //由于壁纸为必选，所以去掉壁纸
        mSelectedChannels.remove(0);

        mUnSelectedChannels = mChannelManager.getAllChannels();
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_channel_box_layout, null);
        mSelectedChannelGrid = (GridView) view.findViewById(R.id.selectedChannelGrid);
        mGridAdapter = new GridAdapter();
        mSelectedChannelGrid.setAdapter(mGridAdapter);

        mAllChannelListView = (ListView) view.findViewById(R.id.allChannelList);
        mAllChannelListView.setVerticalFadingEdgeEnabled(true);
        mAllChannelListView.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 5));
        mListAdapter = new AllChannelAdapter();
        mAllChannelListView.setAdapter(mListAdapter);

        mSelectedChannelGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedChannels.remove(position);
                for (ChannelInfo info : mUnSelectedChannels) {
                    if (info.getChannelId() == id) {
                        info.setSelected(false);
                        break;
                    }
                }
                mGridAdapter.notifyDataSetChanged();
                mListAdapter.notifyDataSetChanged();
            }
        });

        mBackBtn = (ImageView) view.findViewById(R.id.backBtn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PandoraBoxManager.newInstance(mContext).closeDetailPage(true);
            }
        });
        addView(view);
    }

    @Override
    protected void onDetachedFromWindow() {
        // 由于显示时去掉了壁纸项，保存时要再加上壁纸，确保新闻页显示壁纸项
        ChannelInfo ci = new ChannelInfo();
        ci.setChannelId(ChannelBoxManager.CHANNEL_WALLPAPER);
        mSelectedChannels.add(0, ci);
        mChannelManager.saveSelectedChannels(mSelectedChannels);
        super.onDetachedFromWindow();
    }

    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSelectedChannels.size();
        }

        @Override
        public Object getItem(int position) {
            return mSelectedChannels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mSelectedChannels.get(position).getChannelId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder1 holder = null;
            if (convertView == null) {
                holder = new ViewHolder1();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.channel_selected_grid_item_layout, null);
                holder.channelName = (TextView) convertView.findViewById(R.id.channelName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder1) convertView.getTag();
            }
            holder.channelName.setText(mSelectedChannels.get(position).getChannelName());
            return convertView;
        }

        class ViewHolder1 {
            private TextView channelName;
        }
    }

    private class AllChannelAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mUnSelectedChannels.size();
        }

        @Override
        public Object getItem(int position) {
            return mUnSelectedChannels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mUnSelectedChannels.get(position).getChannelId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.channel_unselected_item_layout, null);
                holder.chName = (TextView) convertView.findViewById(R.id.chName);
                holder.enName = (TextView) convertView.findViewById(R.id.enName);
                holder.bg = convertView.findViewById(R.id.bg);
                holder.checkBtn = (TextView) convertView.findViewById(R.id.checkChannelBtn);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ChannelInfo ci = mUnSelectedChannels.get(position);
            holder.chName.setText(ci.getChannelName());
            holder.enName.setText(ci.getChannelEnName());
            holder.bg.setBackgroundResource(ci.getChannelImgResId());
            if (ci.isSelected()) {
                holder.checkBtn.setBackgroundResource(R.drawable.channel_feed_btn_bg);
                holder.checkBtn.setText("取消");
            } else {
                holder.checkBtn.setBackgroundResource(R.drawable.channel_unfeed_btn_bg);
                holder.checkBtn.setText("订阅 +");
            }
            holder.checkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ci.isSelected()) {
                        ci.setSelected(false);
                        mSelectedChannels.remove(ci);
                    } else {
                        ci.setSelected(true);
                        mSelectedChannels.add(ci);
                    }
                    mGridAdapter.notifyDataSetChanged();
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        class ViewHolder {
            private View bg;

            private TextView chName;

            private TextView enName;

            private TextView checkBtn;
        }
    }
}
