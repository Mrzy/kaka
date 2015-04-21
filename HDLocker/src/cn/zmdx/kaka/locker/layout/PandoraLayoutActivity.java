
package cn.zmdx.kaka.locker.layout;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.ImageUtils;

import com.umeng.analytics.MobclickAgent;

public class PandoraLayoutActivity extends ActionBarActivity implements OnItemClickListener {

    private GridView mGrid;

    private List<LayoutInfo> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_layoutpage_layout);
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.action_bar_bg_blue));
        initView();
    }

    private void initView() {
        mGrid = (GridView) findViewById(R.id.layoutGridView);
        mData = TimeLayoutManager.getInstance(this).getAllLayout();
        TimeLayoutAdapter adapter = new TimeLayoutAdapter(this, mData);
        mGrid.setAdapter(adapter);
        mGrid.setOnItemClickListener(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MAboutActivity");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MAboutActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }

    private static class TimeLayoutAdapter extends BaseAdapter {

        private Context mContext;

        private List<LayoutInfo> mData;

        private LayoutInflater mInflater;

        private Bitmap mLayoutBg;

        private int mCurLayoutId;

        public TimeLayoutAdapter(Context context, List<LayoutInfo> data) {
            mContext = context;
            mData = data;
            mInflater = LayoutInflater.from(context);
            mLayoutBg = createLayoutBg();
            mCurLayoutId = PandoraConfig.newInstance(mContext).getCurrentLayout();
        }

        private Bitmap createLayoutBg() {
            Drawable drawable = ThemeManager.getCurrentTheme().getCurDrawable();
            Bitmap bmp = ImageUtils.drawable2Bitmap(drawable, true);
            return ImageUtils.scaleTo(bmp, bmp.getWidth() / 2, bmp.getHeight() / 2);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mData.get(position).getLayoutId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.layoutpage_cell_layout, null);
                holder = new ViewHolder();
                holder.bg = (ImageView) convertView.findViewById(R.id.layoutPageBg);
                holder.selectedImg = (ImageView) convertView.findViewById(R.id.layoutPageSelected);
                holder.timeImg = (ImageView) convertView.findViewById(R.id.layoutPageTime);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final LayoutInfo info = mData.get(position);
            int layoutId = info.getLayoutId();
            holder.bg.setImageBitmap(mLayoutBg);
            if (mCurLayoutId == layoutId) {
                holder.selectedImg.setVisibility(View.VISIBLE);
            } else {
                holder.selectedImg.setVisibility(View.INVISIBLE);
            }
            holder.timeImg.setImageResource(info.getCoverResId());
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView bg;

        ImageView selectedImg;

        ImageView timeImg;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final LayoutInfo info = mData.get(position);
    }
}
