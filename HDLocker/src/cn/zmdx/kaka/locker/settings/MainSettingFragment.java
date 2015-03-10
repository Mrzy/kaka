
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
        ViewPagerCompat viewPager = (ViewPagerCompat) mEntireView
                .findViewById(R.id.setting_viewpaper);
        PageFragmentAdapter pagerAdapter = new PageFragmentAdapter(getFragmentManager(),
                initFragmentList(), initFragmentTitleList());
        viewPager.setAdapter(pagerAdapter);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) mEntireView
                .findViewById(R.id.setting_pats);
        tabStrip.setViewPager(viewPager);
        tabStrip.setOnPageChangeListener(pagerAdapter);
        tabStrip.setShouldExpand(true);
        return mEntireView;
    }

    private List<String> initFragmentTitleList() {
        List<String> titleList = new ArrayList<String>();
        titleList.add("通用");
        titleList.add("通知");
        titleList.add("密码");
        titleList.add("壁纸");
        return titleList;
    }

    private List<Fragment> initFragmentList() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        GeneralFragment mainSettingsFragment = new GeneralFragment();
        NotifyFragment notifyFragment = new NotifyFragment();
        PasswordFragment passwordFragment = new PasswordFragment();
        WallpaperFragment wallpaperFragment = new WallpaperFragment();
        fragmentList.add(mainSettingsFragment);
        fragmentList.add(notifyFragment);
        fragmentList.add(passwordFragment);
        fragmentList.add(wallpaperFragment);
        return fragmentList;
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
