
package cn.zmdx.kaka.locker.guide;

import java.util.ArrayList;
import java.util.List;

import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.adapter.ViewPagerAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pandora_guide);

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

}
