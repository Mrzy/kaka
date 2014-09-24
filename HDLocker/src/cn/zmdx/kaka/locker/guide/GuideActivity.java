
package cn.zmdx.kaka.locker.guide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.InitSettingActivity;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.UmengCustomEvent;

import com.umeng.analytics.MobclickAgent;

/**
 * 引导界面
 */
public class GuideActivity extends Activity implements OnPageChangeListener {

    private ViewPager mViewPager;

    private ViewPagerAdapter mViewPagerAdapter;

    private List<View> mList;

    // 底部小点图片
    private ImageView[] mImageView;

    // 记录当前选中位置
    private int mCurrentIndex;

    private long mGuideTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pandora_guide);
        mGuideTime = System.currentTimeMillis();
        // 初始化页面
        initViews();

        // 初始化底部小点
        initDots();
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);

        mList = new ArrayList<View>();
        // 初始化引导图片列表
        mList.add(inflater.inflate(R.layout.pandora_guide_image_one, null));
        mList.add(inflater.inflate(R.layout.pandora_guide_image_two, null));
        mList.add(inflater.inflate(R.layout.pandora_guide_image_three, null));
        mList.add(inflater.inflate(R.layout.pandora_guide_image_four, null));

        // 初始化Adapter
        mViewPagerAdapter = new ViewPagerAdapter(mList, this);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mViewPagerAdapter);
        // 绑定回调
        mViewPager.setOnPageChangeListener(this);
    }

    class ViewPagerAdapter extends PagerAdapter {

        // 界面列表
        private List<View> mList;

        private Activity mActivity;

        private static final String SHAREDPREFERENCES_NAME = "first_pref";

        public ViewPagerAdapter(List<View> views, Activity activity) {
            this.mList = views;
            this.mActivity = activity;
        }

        // 销毁arg1位置的界面
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mList.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        // 获得当前界面数
        @Override
        public int getCount() {
            if (mList != null) {
                return mList.size();
            }
            return 0;
        }

        // 初始化arg1位置的界面
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mList.get(arg1), 0);
            if (arg1 == mList.size() - 1) {
                ImageView mStartWeiboImageButton = (ImageView) arg0
                        .findViewById(R.id.iv_start_locker);
                mStartWeiboImageButton.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 设置已经引导
                        statisticalGuideTime();
                        setGuided();
                        goHome();
                        PandoraConfig.newInstance(mActivity).savePandolaLockerState(true);
                    }

                });
            }
            return mList.get(arg1);
        }

        private void goHome() {
            // 跳转
            Intent intent = new Intent(mActivity, InitSettingActivity.class);
            intent.putExtra("isFirst", true);
            mActivity.startActivity(intent);
            mActivity.finish();
        }

        /**
         * 设置已经引导过了，下次启动不用再次引导
         */
        private void setGuided() {
            SharedPreferences preferences = mActivity.getSharedPreferences(SHAREDPREFERENCES_NAME,
                    Context.MODE_PRIVATE);
            Editor editor = preferences.edit();
            // 存入数据
            editor.putBoolean("isFirstIn", false);
            // 提交修改
            editor.commit();
        }

        // 判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

    }

    /**
     * 计算引导页的总展示时间
     */
    private void statisticalGuideTime() {
        int duration = (int) (System.currentTimeMillis() - mGuideTime);
        Map<String, String> map_value = new HashMap<String, String>();
        MobclickAgent.onEventValue(HDApplication.getInstannce(), UmengCustomEvent.EVENT_GUIDE_TIME,
                map_value, duration);
    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

        mImageView = new ImageView[mList.size()];

        // 循环取得小点图片
        for (int i = 0; i < mList.size(); i++) {
            mImageView[i] = (ImageView) ll.getChildAt(i);
            mImageView[i].setEnabled(true);// 都设为灰色
        }

        mCurrentIndex = 0;
        mImageView[mCurrentIndex].setEnabled(false);// 设置为白色，即选中状态
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > mList.size() - 1 || mCurrentIndex == position) {
            return;
        }

        mImageView[position].setEnabled(false);
        mImageView[mCurrentIndex].setEnabled(true);

        mCurrentIndex = position;
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // 当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        // 设置底部小点选中状态
        setCurrentDot(arg0);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SplashScreen"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SplashScreen"); // 保证 onPageEnd 在onPause
                                                 // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
