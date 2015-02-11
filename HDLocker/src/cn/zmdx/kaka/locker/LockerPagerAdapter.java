
package cn.zmdx.kaka.locker;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class LockerPagerAdapter extends PagerAdapter {

    private Context mContext;

    private ViewPager mPager;

    private List<View> mPages;

    public LockerPagerAdapter(Context context, ViewPager pager, List<View> pages) {
        mContext = context;
        mPager = pager;
        mPages = pages;
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mPages.get(position));
        return mPages.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeViewAt(position);
    }
}
