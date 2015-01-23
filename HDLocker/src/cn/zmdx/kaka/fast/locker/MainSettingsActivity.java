
package cn.zmdx.kaka.fast.locker;

import java.util.ArrayList;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.zmdx.kaka.fast.locker.settings.IndividualizationActivity;
import cn.zmdx.kaka.fast.locker.settings.LockerPasswordActivity;
import cn.zmdx.kaka.fast.locker.settings.MAboutActivity;
import cn.zmdx.kaka.fast.locker.settings.WallPaperActivity;
import cn.zmdx.kaka.fast.locker.widget.AlphaForegroundColorSpan;
import cn.zmdx.kaka.fast.locker.widget.KenBurnsView;

public class MainSettingsActivity extends Activity {

    private int mActionBarTitleColor;

    private int mActionBarHeight;

    private int mHeaderHeight;

    private int mMinHeaderTranslation;

    private ListView mListView;

    private KenBurnsView mHeaderPicture;

    private ImageView mHeaderLogo;

    private View mHeader;

    private View mPlaceHolderView;

    private View mSpaceView;

    private TextView mLockScreenName;

    private FrameLayout mRootViewFrameLayout;// unused

    private AccelerateDecelerateInterpolator mSmoothInterpolator;

    private RectF mRect1 = new RectF();

    private RectF mRect2 = new RectF();

    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;

    // private SpannableString mSpannableString;

    private TypedValue mTypedValue = new TypedValue();

    private String[] settingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        mHeaderHeight = getResources().getDimensionPixelSize(
                R.dimen.pandora_actionbar_header_height);
        mMinHeaderTranslation = -mHeaderHeight + getActionBarHeight();

        setContentView(R.layout.activity_actionbar);

        settingItems = new String[] {
                getString(R.string.fast_setting_item_open_fastlocker),
                getString(R.string.fast_setting_item_set_password),
                getString(R.string.fast_setting_item_manage_wallpaper),
                getString(R.string.fast_setting_item_notification_center),
                getString(R.string.fast_setting_item_shortcut),
                getString(R.string.fast_setting_item_individual),
                getString(R.string.fast_setting_item_about)
        };
        initView();
        setupActionBar();
        setupListView();
    }

    private void initView() {
        mRootViewFrameLayout = (FrameLayout) findViewById(R.id.actionbar_rootview);
        mLockScreenName = (TextView) findViewById(R.id.fastlocker_name);
        mListView = (ListView) findViewById(R.id.listview);
        mHeader = this.findViewById(R.id.header);
        mHeaderPicture = (KenBurnsView) findViewById(R.id.header_picture);
        mHeaderPicture.setResourceIds(R.drawable.picture0, R.drawable.picture1);
        mHeaderLogo = (ImageView) findViewById(R.id.header_logo);
        mActionBarTitleColor = getResources().getColor(R.color.pandora_actionbar_title_color);

        // mSpannableString = new
        // SpannableString(getString(R.string.pandora_actionbar_title));
        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(mActionBarTitleColor);
    }

    private void setupListView() {
        ArrayList<String> FAKES = new ArrayList<String>();
        for (int i = 0; i < settingItems.length; i++) {
            FAKES.add(settingItems[i]);
        }
        SettingItemsAdapter settingItemsAdapter = new SettingItemsAdapter(this, FAKES);
        mPlaceHolderView = getLayoutInflater().inflate(R.layout.view_header_placeholder, mListView,
                false);
        // 增加ListView下面的空白，使上方能够折叠。
        mSpaceView = getLayoutInflater().inflate(R.layout.activity_actionbar_listview_space,
                mListView, false);
        mListView.addHeaderView(mPlaceHolderView);
        mListView.addFooterView(mSpaceView);

        mListView.setAdapter(settingItemsAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            /**
             * 通过计算ListView滑动时的距离来动态改变
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                int scrollY = getScrollY();
                // sticky actionbar
                mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
                // header_logo --> actionbar icon
                float ratio = clamp(mHeader.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
                interpolate(mHeaderLogo, getActionBarIconView(),
                        mSmoothInterpolator.getInterpolation(ratio));
                setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
            }
        });
        /**
         * 界面跳转
         */
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        break;
                    // 设置锁屏密码
                    case 2:
                        gotoLockerPasswordActivity();
                        break;
                    // 管理壁纸
                    case 3:
                        gotoWallpaperActivity();
                        break;
                    // 通知中心
                    case 4:
                        gotoNotificationCenter();
                        break;
                    // 添加快捷应用
                    case 5:

                        break;
                    // 个性化设置
                    case 6:
                        gotoIndividualizationActivity();
                        break;
                    // 关于
                    case 7:
                        gotoAbout();
                        break;
                }
            }
        });
    }

    private void setTitleAlpha(float alpha) {
        mAlphaForegroundColorSpan.setAlpha(alpha);
        // mSpannableString.setSpan(mAlphaForegroundColorSpan, 0,
        // mSpannableString.length(),
        // Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // getActionBar().setTitle(mSpannableString);
        getActionBar().setTitle("");
    }

    private void gotoLockerPasswordActivity() {
        Intent intentLocker = new Intent(MainSettingsActivity.this, LockerPasswordActivity.class);
        startActivity(intentLocker);
    }

    private void gotoWallpaperActivity() {
        Intent intentWallpaper = new Intent(MainSettingsActivity.this, WallPaperActivity.class);
        startActivity(intentWallpaper);
    }

    private void gotoIndividualizationActivity() {
        Intent intentIndividual = new Intent(MainSettingsActivity.this,
                IndividualizationActivity.class);
        startActivity(intentIndividual);
    }

    private void gotoNotificationCenter() {

    }

    private void gotoAbout() {
        Intent in = new Intent();
        in.setClass(MainSettingsActivity.this, MAboutActivity.class);
        startActivity(in);
    }

    /**
     * 做LockerName的动画，暂未使用
     */
    private void setLockerNameAnimator() {
        ObjectAnimator lockerNameAnimator = ObjectAnimator.ofFloat(mLockScreenName, "alpha", 1.0f,
                0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.play(lockerNameAnimator);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    private void interpolate(View view1, View view2, float interpolation) {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

        view1.setTranslationX(translationX);
        view1.setTranslationY(translationY - mHeader.getTranslationY());
        view1.setScaleX(scaleX);
        view1.setScaleY(scaleY);
    }

    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    public int getScrollY() {
        View c = mListView.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mPlaceHolderView.getHeight();
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    /**
     * 简单设置ActionBar
     */
    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.drawable.ic_transparent);
    }

    private ImageView getActionBarIconView() {
        return (ImageView) findViewById(android.R.id.home);
    }

    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }
        getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources()
                .getDisplayMetrics());
        return mActionBarHeight;
    }

}
