
package cn.zmdx.kaka.locker.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.widget.PagerSlidingTabStrip;
import cn.zmdx.kaka.locker.widget.ViewPagerCompat;

public class MainSettingFragment extends Fragment {

    private View mEntireView;

    public interface IMainSettingListener {
        void onItemClick(String title);
    }

    private IMainSettingListener mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (IMainSettingListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.main_setting_fragment, container, false);
        final ViewPagerCompat viewPager = (ViewPagerCompat) mEntireView
                .findViewById(R.id.setting_viewpaper);
        List<Fragment> pages = new ArrayList<Fragment>();
        initNewsPages(pages);
        List<String> titles = new ArrayList<String>();
        initTitles(titles);
        PageFragmentAdapter pagerAdapter = new PageFragmentAdapter(getFragmentManager(), pages,
                titles);
        viewPager.setAdapter(pagerAdapter);
        final PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) mEntireView
                .findViewById(R.id.setting_pats);
        tabStrip.setViewPager(viewPager);
        tabStrip.setOnPageChangeListener(pagerAdapter);
        tabStrip.setShouldExpand(true);
        return mEntireView;
    }

    private void initTitles(List<String> titles) {
        titles.add("通用");
        titles.add("通知");
        titles.add("密码");
        titles.add("壁纸");
    }

    private void initNewsPages(List<Fragment> pages) {
        GeneralFragment mainSettingsFragment = new GeneralFragment();
        NotifyFragment notifyFragment = new NotifyFragment();
        PasswordFragment passwordFragment = new PasswordFragment();
        WallpaperFragment wallpaperFragment = new WallpaperFragment();
        pages.add(mainSettingsFragment);
        pages.add(notifyFragment);
        pages.add(passwordFragment);
        pages.add(wallpaperFragment);
    }

    public class PageFragmentAdapter extends FragmentPagerAdapter implements OnPageChangeListener {

        private List<Fragment> mPages;

        private List<String> mTitles;

        public PageFragmentAdapter(FragmentManager fm, List<Fragment> pages, List<String> titles) {
            super(fm);
            mPages = pages;
            mTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return mPages.get(position);
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (null != mCallback) {
                mCallback.onItemClick(getPageTitle(position).toString());
            }
        }
    }

}
