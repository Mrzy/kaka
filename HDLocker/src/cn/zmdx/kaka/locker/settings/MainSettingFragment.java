
package cn.zmdx.kaka.locker.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Build;
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

    private PagerSlidingTabStrip mTabStrip;

    private PasswordFragment mPasswordFragment;

    public interface IMainSettingListener {
        void onItemClick(String title, int position);
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
        mTabStrip = (PagerSlidingTabStrip) mEntireView.findViewById(R.id.setting_pats);
        mTabStrip.setShouldExpand(true);
        mTabStrip.setShouldSizeBigger(true);
        mTabStrip.setShouldChangeTextColor(true);
        mTabStrip.setViewPager(viewPager);
        mTabStrip.setOnPageChangeListener(pagerAdapter);
        return mEntireView;
    }

    private List<String> initFragmentTitleList() {
        List<String> titleList = new ArrayList<String>();
        titleList.add(getResources().getString(R.string.pandora_setting_general));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            titleList.add(getResources().getString(R.string.pandora_setting_notify));
        }
        titleList.add(getResources().getString(R.string.pandora_setting_password));
        titleList.add(getResources().getString(R.string.pandora_setting_wallpaper));
        return titleList;
    }

    private List<Fragment> initFragmentList() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        GeneralFragment mainSettingsFragment = new GeneralFragment();
        mPasswordFragment = new PasswordFragment();
        WallpaperFragment wallpaperFragment = new WallpaperFragment();
        fragmentList.add(mainSettingsFragment);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotifyFragment notifyFragment = new NotifyFragment();
            fragmentList.add(notifyFragment);
        }
        fragmentList.add(mPasswordFragment);
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
                mCallback.onItemClick(getPageTitle(position).toString(), position);
                mTabStrip.setIndicatorColor(((MainSettingActivity) (getActivity()))
                        .getBackgroundColor()[position]);
            }
        }
    }

    public void resetPasswordState() {
        if (null != mPasswordFragment) {
            mPasswordFragment.reset();
        }
    }

}
