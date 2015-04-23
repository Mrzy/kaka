
package cn.zmdx.kaka.locker.content.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.squareup.picasso.Picasso;

public class StickPageAdapter extends PagerAdapter implements OnPageChangeListener {

    public static final int MAX_COUNT = 100 * 1000;

    private List<ImageView> mPageViews;

    private ImageView[] mPointViews;

    private TypefaceTextView mTitleView;

    private List<ServerImageData> mStickData;

    public StickPageAdapter(List<ImageView> pages, ImageView[] pointViews,
            TypefaceTextView titleView, List<ServerImageData> stickData) {
        mPageViews = pages;
        mPointViews = pointViews;
        mTitleView = titleView;
        mStickData = stickData;
    }

    @Override
    public int getCount() {
        return mStickData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(View view, int position, Object object) {
        ((ViewPager) view).removeView(mPageViews.get(position));
    }

    @Override
    public Object instantiateItem(View view, int position) {
        // int realPos = position % mStickData.size();
        int realPos = position;
        int targetWidth = BaseInfoHelper.getRealWidth(HDApplication.getContext())
                - BaseInfoHelper.dip2px(HDApplication.getContext(), 8);
        int targetHeight = BaseInfoHelper.dip2px(HDApplication.getContext(), 240);
        ImageView imageView = mPageViews.get(realPos);
        Picasso.with(HDApplication.getContext()).load(mStickData.get(realPos).getUrl())
                .placeholder(R.drawable.icon_newsimage_loading).resize(targetWidth, targetHeight)
                .centerCrop().into(imageView);
        // ((ViewPager) view).removeView(imageView);
        ((ViewPager) view).addView(imageView);
        return mPageViews.get(realPos);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        // int realPos = position % mStickData.size();
        int realPos = position;
        mTitleView.setText(getPageTitle(realPos));
        for (int i = 0; i < mPointViews.length; i++) {
            if (i == realPos) {
                mPointViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                mPointViews[i].setBackgroundResource(R.drawable.page_indicator_normal);
            }
        }
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("viewpaper自动滚动中：" + getPageTitle(realPos));
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mStickData.get(position).getTitle();
    }

}
