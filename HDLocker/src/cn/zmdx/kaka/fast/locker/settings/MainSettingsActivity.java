
package cn.zmdx.kaka.fast.locker.settings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.SettingItemsAdapter;
import cn.zmdx.kaka.fast.locker.guide.GuideActivity;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.fast.locker.widget.AlphaForegroundColorSpan;
import cn.zmdx.kaka.fast.locker.widget.KenBurnsView;
import cn.zmdx.kaka.fast.locker.widget.material.design.ButtonFloat;

import com.umeng.analytics.MobclickAgent;

public class MainSettingsActivity extends Activity implements OnClickListener {

    private int mActionBarTitleColor;

    private int mActionBarHeight;

    private int mHeaderHeight;

    private int mMinHeaderTranslation;

    private ListView mListView;

    private KenBurnsView mHeaderPicture;

    private ImageView mHeaderLogo;

    private ButtonFloat mCommentImageView;

    private ViewGroup mHeader;

    private View mPlaceHolderView;

    private View mKenBurnsFooterView;

    private View mSpaceView;

    private TextView mLockScreenName;

    private FrameLayout mRootViewFrameLayout;// unused

    private AccelerateDecelerateInterpolator mSmoothInterpolator;

    private RectF mRect1 = new RectF();

    private RectF mRect2 = new RectF();

    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;

    private SpannableString mSpannableString;

    private TypedValue mTypedValue = new TypedValue();

    private String[] settingItems;

    boolean isFirstIn = false;

    private static final int GO_GUIDE = 1001;

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<MainSettingsActivity> mActivity;

        public MyHandler(MainSettingsActivity activity) {
            mActivity = new WeakReference<MainSettingsActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainSettingsActivity activity = mActivity.get();
            switch (msg.what) {
                case GO_GUIDE:
                    activity.goGuide();
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.openActivityDurationTrack(false);
        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        mHeaderHeight = getResources()
                .getDimensionPixelSize(R.dimen.fast_mainsetting_header_height);
        mMinHeaderTranslation = -mHeaderHeight + getActionBarHeight();

        setContentView(R.layout.activity_mainsetting);

        init();
        settingItems = new String[] {
                getString(R.string.fast_setting_item_open_fastlocker),
                getString(R.string.fast_setting_item_set_password),
                getString(R.string.fast_setting_item_manage_wallpaper),
                getString(R.string.fast_setting_item_notification_center),
                getString(R.string.fast_setting_item_shortcut),
                getString(R.string.fast_setting_item_init),
                getString(R.string.fast_setting_item_individual),
                getString(R.string.fast_setting_item_about),
        };
        initView();
        setupActionBar();
        setupListView();
    }

    private void init() {
        isFirstIn = !PandoraConfig.newInstance(this).isHasGuided();

        if (isFirstIn) {
            mHandler.sendEmptyMessage(GO_GUIDE);
        }
    }

    private void goGuide() {
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
    }

    private void initView() {
        mRootViewFrameLayout = (FrameLayout) findViewById(R.id.actionbar_rootview);
        mLockScreenName = (TextView) findViewById(R.id.fastlocker_name);
        mListView = (ListView) findViewById(R.id.listview);
        mHeader = (ViewGroup) this.findViewById(R.id.header);
        // 增加阴影
        mKenBurnsFooterView = getLayoutInflater().inflate(
                R.layout.activity_mainsetting_kenburns_footer, mHeader, false);
        mHeader.addView(mKenBurnsFooterView);
        mHeaderPicture = (KenBurnsView) findViewById(R.id.header_picture);
        mHeaderPicture.setResourceIds(R.drawable.fastbg1, R.drawable.fastbg2);
        mHeaderLogo = (ImageView) findViewById(R.id.header_logo);
        mCommentImageView = (ButtonFloat) findViewById(R.id.fast_comment);
        mCommentImageView.setOnClickListener(this);
        mActionBarTitleColor = getResources().getColor(R.color.fast_actionbar_title_color);

        mSpannableString = new SpannableString("  "+getString(R.string.app_name));
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
        mSpaceView = getLayoutInflater().inflate(R.layout.activity_mainsetting_listview_space,
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
                if (scrollY < 0) {
                    return;
                }
                // sticky actionbar
                mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
                // header_logo --> actionbar icon
                float ratio = clamp(mHeader.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
                interpolate(mHeaderLogo, getActionBarIconView(),
                        mSmoothInterpolator.getInterpolation(0.8F * ratio));
                setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
                showLockerNameAnimator(mLockScreenName, 1 - ratio);
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
                        gotoNotificationCenterActivity();
                        break;
                    // 添加快捷应用
                    case 5:
                        gotoShortcutActivity();
                        break;
                    // 初始设置
                    case 6:
                        gotoInitSettingActivity();
                        break;
                    // 个性化设置
                    case 7:
                        gotoIndividualizationActivity();
                        break;
                    // 关于
                    case 8:
                        gotoMainSettingAboutActivity();
                        break;

                }
            }
        });
    }

    private void setTitleAlpha(float alpha) {
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(new AbsoluteSizeSpan(20, true), 0, mSpannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getActionBar().setTitle(mSpannableString);
    }

    private void gotoLockerPasswordActivity() {
        Intent intentLocker = new Intent(MainSettingsActivity.this, LockerPasswordActivity.class);
        startActivity(intentLocker);
        this.overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private void gotoWallpaperActivity() {
        Intent intentWallpaper = new Intent(MainSettingsActivity.this, WallPaperActivity.class);
        startActivity(intentWallpaper);
        this.overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private void gotoIndividualizationActivity() {
        Intent intentIndividual = new Intent(MainSettingsActivity.this,
                IndividualizationActivity.class);
        startActivity(intentIndividual);
        this.overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private void gotoNotificationCenterActivity() {
        Intent in = new Intent();
        in.setClass(MainSettingsActivity.this, NotificationCenterActivity.class);
        startActivity(in);
        this.overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private void gotoShortcutActivity() {
        Intent in = new Intent();
        in.setClass(MainSettingsActivity.this, ShortcutSettingsActivity.class);
        startActivity(in);
        this.overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private void gotoMainSettingAboutActivity() {
        Intent in = new Intent();
        in.setClass(MainSettingsActivity.this, MainSettingAboutActivity.class);
        startActivity(in);
        this.overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private void gotoInitSettingActivity() {
        Intent in = new Intent();
        in.setClass(MainSettingsActivity.this, InitSettingActivity.class);
        startActivity(in);
        this.overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    /**
     * 做LockerName的动画
     */
    private void showLockerNameAnimator(View view, float toAlpha) {
        view.setAlpha(toAlpha);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    private void interpolate(View view1, View view2, float interpolation) {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);
        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.52F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.55F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));
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
        // 隐藏ActionBar图标
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    private ImageView getActionBarIconView() {
        return (ImageView) findViewById(android.R.id.home);
    }

    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }
        getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        mActionBarHeight = BaseInfoHelper.dip2px(this, 56);
        return mActionBarHeight;
    }

    /**
     * 处理评价按钮点击事件
     */
    @Override
    public void onClick(View v) {
        String locale = BaseInfoHelper.getLocale(this);
        if (locale.equals(Locale.CHINA.toString())) {
            Uri uri = Uri.parse("market://details?id=" + BaseInfoHelper.getPkgName(this));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="
                                + BaseInfoHelper.getPkgName(this)));
                browserIntent.setClassName("com.android.vending",
                        "com.android.vending.AssetBrowserActivity");
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(browserIntent);
            } catch (Exception e) {
                Uri uri = Uri.parse("market://details?id=" + BaseInfoHelper.getPkgName(this));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        Toast.makeText(MainSettingsActivity.this, "如果您喜欢我们的锁屏，请给个五星好评哦！", Toast.LENGTH_LONG).show();
    }
}
