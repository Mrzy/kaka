
package cn.zmdx.kaka.locker.settings;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.animation.ViewAnimation;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout;
import cn.zmdx.kaka.locker.widget.SwitchButton;

import com.umeng.analytics.MobclickAgent;

public class MainSettingsFragment extends BaseSettingsFragment implements OnCheckedChangeListener,
        OnClickListener {
    private View mRootView;

    private SlidingUpPanelLayout mSettingForeView;

    private LinearLayout mInitSetting;

    private LinearLayout mConcernTeam;

    private TextView mChangeBackground;

    private LinearLayout mFeedback;

    private LinearLayout mCheckNewVersion;

    private SwitchButton mPandoraLockerSButton;

    private SwitchButton mLockerTypeSButton;

    private HorizontalScrollView mPicScrollView;

    private LinearLayout mPicView;

    private ImageView mSettingIcon;

    private View mSettingBackground;

    private SparseArray<ImageView> mBorderArray = new SparseArray<ImageView>();

    private SparseIntArray mThumbIdArray = new SparseIntArray();

    private boolean mIsWallpaperShow = false;

    private static final int MSG_SAVE_WALLPAPER = 11;

    private static final int MSG_SAVE_WALLPAPER_DELAY = 100;

    private boolean mIsCurrentlyPressed = false;

    private static final int TIME_COLLAPSE_PANEL_DELAY = 500;

    private static final int TIME_COLLAPSE_PANEL_DURATION = 1000;

    public static final int GUSTURE_REQUEST_CODE_SUCCESS = 37;

    public static final int GUSTURE_REQUEST_CODE_FAIL = 38;

    private static final int VIEW_ANIMATION_DURATION = 200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.pandora_setting, container, false);
        initView();
        initSwitchButtonState();
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
                mSettingForeView.collapsePanel(TIME_COLLAPSE_PANEL_DURATION);
            }
        }, TIME_COLLAPSE_PANEL_DELAY);
        return mRootView;
    }

    private void initView() {
        mInitSetting = (LinearLayout) mRootView.findViewById(R.id.setting_init);
        mInitSetting.setOnClickListener(this);

        mSettingForeView = (SlidingUpPanelLayout) mRootView.findViewById(R.id.setting_fore_view);
        mSettingIcon = (ImageView) mRootView.findViewById(R.id.setting_icon);

        mSettingBackground = mRootView.findViewById(R.id.setting_background);

        mSettingIcon.setOnClickListener(this);

        mPandoraLockerSButton = (SwitchButton) mRootView
                .findViewById(R.id.setting_pandoralocker_switch_button);
        mPandoraLockerSButton.setOnCheckedChangeListener(this);

        mLockerTypeSButton = (SwitchButton) mRootView
                .findViewById(R.id.setting_pandoralocker_password);
        mLockerTypeSButton.setOnCheckedChangeListener(this);

        mFeedback = (LinearLayout) mRootView.findViewById(R.id.setting_feedback_prompt);
        mFeedback.setOnClickListener(this);
        mCheckNewVersion = (LinearLayout) mRootView
                .findViewById(R.id.setting_checkout_new_version_prompt);
        mCheckNewVersion.setOnClickListener(this);

        mConcernTeam = (LinearLayout) mRootView.findViewById(R.id.setting_concern_team);
        mConcernTeam.setOnClickListener(this);
        mChangeBackground = (TextView) mRootView.findViewById(R.id.setting_change_background);
        mChangeBackground.setOnClickListener(this);

        mPicView = (LinearLayout) mRootView.findViewById(R.id.setting_bg_image);
        mPicScrollView = (HorizontalScrollView) mRootView.findViewById(R.id.setting_bg_hs);

        bindPicData();
    }

    private void bindPicData() {
        if (mPicView != null) {
            List<Theme> mThemeList = ThemeManager.getAllTheme();
            for (int i = 0; i < mThemeList.size(); i++) {
                RelativeLayout mWallpaperRl = (RelativeLayout) LayoutInflater.from(
                        HDApplication.getInstannce())
                        .inflate(R.layout.setting_wallpaper_item, null);
                ImageView mWallpaperIv = (ImageView) mWallpaperRl
                        .findViewById(R.id.setting_wallpaper_image);
                mWallpaperIv.setScaleType(ScaleType.FIT_XY);
                mWallpaperIv.setImageResource(mThemeList.get(i).getmThumbnailResId());
                ImageView mWallpaperBorder = (ImageView) mWallpaperRl
                        .findViewById(R.id.setting_wallpaper_image_border);
                int themeId = mThemeList.get(i).getmThemeId();
                mThumbIdArray.put(i, themeId);
                mBorderArray.put(i, mWallpaperBorder);
                mWallpaperIv.setTag(i);
                mWallpaperIv.setOnClickListener(mPicClickListener);
                mPicView.addView(mWallpaperRl);
            }
            checkCurrentThemeId();
        }
    }

    private void checkCurrentThemeId() {
        int themeId = getCurrentThemeId();
        for (int i = 0; i < mThumbIdArray.size(); i++) {
            if (themeId == mThumbIdArray.get(i)) {
                if (null != mBorderArray) {
                    mBorderArray.get(i).setVisibility(View.VISIBLE);
                }
            }
        }
        setSettingBackground(themeId);
    }

    private View.OnClickListener mPicClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if (null != view) {
                int position = (Integer) view.getTag();
                for (int pos = 0; pos < mBorderArray.size(); pos++) {
                    if (pos == position) {
                        mBorderArray.get(pos).setVisibility(View.VISIBLE);
                    } else {
                        mBorderArray.get(pos).setVisibility(View.GONE);
                    }
                }
                int themeId = mThumbIdArray.get(position);
                setSettingBackground(themeId);

                if (mHandler.hasMessages(MSG_SAVE_WALLPAPER)) {
                    mHandler.removeMessages(MSG_SAVE_WALLPAPER);
                }
                Message message = Message.obtain();
                message.what = MSG_SAVE_WALLPAPER;
                message.arg1 = themeId;
                mHandler.sendMessageDelayed(message, MSG_SAVE_WALLPAPER_DELAY);
            }
        }
    };

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<MainSettingsFragment> mFragment;

        public MyHandler(MainSettingsFragment fragment) {
            mFragment = new WeakReference<MainSettingsFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            MainSettingsFragment fragment = mFragment.get();
            switch (msg.what) {
                case MSG_SAVE_WALLPAPER:
                    int themeId = msg.arg1;
                    fragment.saveThemeId(themeId);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private void initSwitchButtonState() {
        mPandoraLockerSButton.setChecked(isPandoraLockerOn());
        mLockerTypeSButton.setChecked(getUnLockType() == PandoraConfig.UNLOCKER_TYPE_GUSTURE);
        mIsCurrentlyPressed = true;
    }

    protected void setSettingBackground(int themeId) {
        Theme theme = ThemeManager.getThemeById(themeId);
        mSettingBackground.setBackgroundResource(theme.getmBackgroundResId());
        mSettingForeView.setForegroundDrawable(getActivity().getResources().getDrawable(
                theme.getmForegroundResId()));
        mSettingIcon.setBackgroundResource(theme.getmSettingsIconResId());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.setting_pandoralocker_switch_button:
                if (isChecked) {
                    enablePandoraLocker();
                } else {
                    disablePandoraLocker();
                }
                break;
            case R.id.setting_pandoralocker_password:
                if (isChecked) {
                    if (mIsCurrentlyPressed) {
                        showGustureView(LockPatternActivity.LOCK_PATTERN_TYPE_OPEN);
                    }
                } else {
                    if (mIsCurrentlyPressed) {
                        showGustureView(LockPatternActivity.LOCK_PATTERN_TYPE_CLOSE);
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onResume() {
        mPandoraLockerSButton.setChecked(isPandoraLockerOn());
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); // 统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }

    private void showGustureView(final int type) {
        View decorView = getActivity().getWindow().getDecorView();
        Bitmap blurBitmap = PandoraUtils.fastBlur(decorView);
        Intent in = new Intent();
        in.setClass(getActivity(), LockPatternActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putParcelable("bitmap", blurBitmap);
        in.putExtra("bundle", bundle);
        startActivityForResult(in, GUSTURE_REQUEST_CODE_SUCCESS);
        getActivity().overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case GUSTURE_REQUEST_CODE_FAIL:
                int type = data.getExtras().getInt("type");
                mIsCurrentlyPressed = false;
                switch (type) {
                    case LockPatternActivity.LOCK_PATTERN_TYPE_CLOSE:
                        mLockerTypeSButton.setChecked(true);
                        break;
                    case LockPatternActivity.LOCK_PATTERN_TYPE_OPEN:
                        mLockerTypeSButton.setChecked(false);
                        break;

                    default:
                        break;
                }
                mIsCurrentlyPressed = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_feedback_prompt:
                startFeedback();
                break;
            case R.id.setting_concern_team:
                gotoAbout();
                break;
            case R.id.setting_checkout_new_version_prompt:
                checkNewVersion();
                break;
            case R.id.setting_change_background:
                mIsWallpaperShow = !mIsWallpaperShow;
                int height = (int) getActivity().getResources().getDimension(
                        R.dimen.setting_wallpaper_bg_height);
                if (mIsWallpaperShow) {
                    ViewAnimation viewAnimation = new ViewAnimation(mPicScrollView, height,
                            mIsWallpaperShow);
                    viewAnimation.setDuration(VIEW_ANIMATION_DURATION);
                    mPicScrollView.startAnimation(viewAnimation);
                } else {
                    ViewAnimation viewAnimation = new ViewAnimation(mPicScrollView, height,
                            mIsWallpaperShow);
                    viewAnimation.setDuration(VIEW_ANIMATION_DURATION);
                    mPicScrollView.startAnimation(viewAnimation);
                }
                break;
            case R.id.setting_icon:
                if (mSettingForeView.isPanelExpanded()) {
                    mSettingForeView.collapsePanel(TIME_COLLAPSE_PANEL_DURATION);
                } else {
                    mSettingForeView.expandPanel();
                }
                break;

            case R.id.setting_init:
                gotoInit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        if (null != mBorderArray) {
            mBorderArray.clear();
        }
        super.onDestroyView();
    }
}
