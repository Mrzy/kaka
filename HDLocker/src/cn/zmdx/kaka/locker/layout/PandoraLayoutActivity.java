
package cn.zmdx.kaka.locker.layout;

import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.BlurUtils;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;

import com.umeng.analytics.MobclickAgent;

public class PandoraLayoutActivity extends ActionBarActivity implements OnItemClickListener {

    private GridView mGrid;

    private List<LayoutInfo> mData;

    private TimeLayoutAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_layoutpage_layout);
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.action_bar_bg_blue));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        mGrid = (GridView) findViewById(R.id.layoutGridView);
        mGrid.setVerticalFadingEdgeEnabled(true);
        mGrid.setFadingEdgeLength(BaseInfoHelper.dip2px(this, 5));
        mData = TimeLayoutManager.getInstance(this).getAllLayout();
        mAdapter = new TimeLayoutAdapter(this, mData);
        mGrid.setAdapter(mAdapter);
        mGrid.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PandoraLayoutActivity");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PandoraLayoutActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        HDBThreadUtils.runOnWorker(new Runnable() {

            @Override
            public void run() {
                int layoutId = TimeLayoutManager.getInstance(PandoraLayoutActivity.this)
                        .getCurrentLayout();
                UmengCustomEventManager.statisticalPandoraLayout(layoutId);
            }
        });

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
            Bitmap scaledBmp = ImageUtils.scaleTo(bmp, bmp.getWidth() / 8, bmp.getHeight() / 8);
            return BlurUtils.getBlurBitmap(mContext, scaledBmp, 5f, true);
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
                alphaAnimator(holder.selectedImg, View.VISIBLE);
            } else {
                alphaAnimator(holder.selectedImg, View.INVISIBLE);
                // holder.selectedImg.setVisibility(View.INVISIBLE);
            }

            holder.timeImg.setImageResource(info.getCoverResId());
            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            mCurLayoutId = PandoraConfig.newInstance(mContext).getCurrentLayout();
            super.notifyDataSetChanged();
        }

        private void alphaAnimator(final View view, final int visibility) {
            float star = visibility == View.INVISIBLE ? 1f : 0f;
            float end = visibility == View.INVISIBLE ? 0f : 1f;
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", star, end);
            animator.setDuration(300);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(visibility);
                }
            });
            animator.start();
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
        final TimeLayoutManager tm = TimeLayoutManager.getInstance(this);
        tm.saveCurrentLayout(info.getLayoutId());
        mAdapter.notifyDataSetChanged();
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
                LockScreenManager.getInstance().lock();
                LockScreenManager.getInstance().startShimmer();
            }
        }, 300);
    }
}
