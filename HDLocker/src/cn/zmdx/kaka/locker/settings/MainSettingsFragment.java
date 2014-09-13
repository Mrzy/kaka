
package cn.zmdx.kaka.locker.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.PandoraBoxDispatcher;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.widget.LockPatternDialog;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout;
import cn.zmdx.kaka.locker.widget.SwitchButton;

public class MainSettingsFragment extends BaseSettingsFragment implements OnCheckedChangeListener,
        OnClickListener {
    private View mRootView;

    private SlidingUpPanelLayout mSettingView;

    private TextView mSystemLockerPrompt;

    private TextView mConcernTeam;

    private TextView mChangeBackground;

    private TextView mFeedback;

    private TextView mCheckNewVersion;

    private SwitchButton mPandoraLockerSButton;

    private SwitchButton mLockerTypeSButton;

    private HorizontalScrollView mPicScrollView;

    private LinearLayout mPicView;

    private ImageView mSettingImageView;

    private View mSettingMainView;

    private SparseArray<ImageView> mImageViewItems = new SparseArray<ImageView>();

    private int[] mWallpapers = {
            R.drawable.setting_background_blue, R.drawable.setting_background_green,
            R.drawable.setting_background_purple, R.drawable.setting_background_yellow
    };

    private int[] mForeWallpapers = {
            R.drawable.setting_background_blue_fore, R.drawable.setting_background_green_fore,
            R.drawable.setting_background_purple_fore, R.drawable.setting_background_yellow_fore
    };

    private boolean mIsWallpaperShow = false;

    private static final int MSG_SAVE_WALLPAPER = 11;

    private static final int MSG_SAVE_WALLPAPER_DELAY = 100;

    private boolean mIsCurrentlyPressed = false;

    private static final int TIME_COLLAPSE_PANEL_DELAY = 500;

    private static final int TIME_COLLAPSE_PANEL_DURATION = 1000;

    public static final int GUSTURE_REQUEST_CODE_SUCCESS = 37;

    public static final int GUSTURE_REQUEST_CODE_FAIL = 38;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        PandoraBoxDispatcher.getInstance().sendEmptyMessageDelayed(
                PandoraBoxDispatcher.MSG_LOAD_BAIDU_IMG, 10000);
        super.onCreate(savedInstanceState);
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
                mSettingView.collapsePanel(TIME_COLLAPSE_PANEL_DURATION);
            }
        }, TIME_COLLAPSE_PANEL_DELAY);
        return mRootView;
    }

    private void initView() {

        mSettingView = (SlidingUpPanelLayout) mRootView.findViewById(R.id.setting_view);
        mSettingImageView = (ImageView) mRootView.findViewById(R.id.setting_icon);

        mSettingMainView = mRootView.findViewById(R.id.setting_main);

        mSettingImageView.setOnClickListener(this);

        mSystemLockerPrompt = (TextView) mRootView.findViewById(R.id.setting_systemlocker_prompt);
        mSystemLockerPrompt.setOnClickListener(this);

        mPandoraLockerSButton = (SwitchButton) mRootView
                .findViewById(R.id.setting_pandoralocker_switch_button);
        mPandoraLockerSButton.setOnCheckedChangeListener(this);

        mLockerTypeSButton = (SwitchButton) mRootView
                .findViewById(R.id.setting_pandoralocker_password);
        mLockerTypeSButton.setOnCheckedChangeListener(this);

        mFeedback = (TextView) mRootView.findViewById(R.id.setting_feedback_prompt);
        mFeedback.setOnClickListener(this);
        mCheckNewVersion = (TextView) mRootView
                .findViewById(R.id.setting_checkout_new_version_prompt);
        mCheckNewVersion.setOnClickListener(this);

        mConcernTeam = (TextView) mRootView.findViewById(R.id.setting_concern_team);
        mConcernTeam.setOnClickListener(this);
        mChangeBackground = (TextView) mRootView.findViewById(R.id.setting_change_background);
        mChangeBackground.setOnClickListener(this);

        mPicView = (LinearLayout) mRootView.findViewById(R.id.setting_bg_image);
        mPicScrollView = (HorizontalScrollView) mRootView.findViewById(R.id.setting_bg_hs);

        bindPicData();
    }

    private void bindPicData() {
        if (mPicView != null) {
            for (int i = 0; i < PandoraConfig.sThumbWallpapers.length; i++) {
                RelativeLayout mWallpaperRl = (RelativeLayout) LayoutInflater.from(
                        HDApplication.getInstannce())
                        .inflate(R.layout.setting_wallpaper_item, null);
                ImageView mWallpaper = (ImageView) mWallpaperRl
                        .findViewById(R.id.setting_wallpaper_image);
                mWallpaper.setImageResource(PandoraConfig.sThumbWallpapers[i]);
                ImageView mWallpaperCircle = (ImageView) mWallpaperRl
                        .findViewById(R.id.setting_wallpaper_image_border);
                mImageViewItems.put(i, mWallpaperCircle);
                PandoraConfig.sBackgroundArray.put(PandoraConfig.sThumbWallpapers[i],
                        mWallpapers[i]);
                PandoraConfig.sForeBackgroundArray.put(mWallpapers[i], mForeWallpapers[i]);
                mWallpaper.setTag(i);
                mWallpaper.setOnClickListener(mPicClickListener);
                mPicView.addView(mWallpaperRl);
            }
            checkWhichWallpaper();
        }
    }

    private void checkWhichWallpaper() {
        int which = getWhichWallpaper();
        if (null != mImageViewItems) {
            mImageViewItems.get(which).setVisibility(View.VISIBLE);
        }

        setSettingBackground(getWhichWallpaperResId());
    }

    private View.OnClickListener mPicClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if (null != view) {
                int position = (Integer) view.getTag();
                for (int pos = 0; pos < mImageViewItems.size(); pos++) {
                    if (pos == position) {
                        mImageViewItems.get(pos).setVisibility(View.VISIBLE);
                    } else {
                        mImageViewItems.get(pos).setVisibility(View.GONE);
                    }
                }

                setSettingBackground(PandoraConfig.sBackgroundArray
                        .get(PandoraConfig.sThumbWallpapers[position]));

                if (mHandler.hasMessages(MSG_SAVE_WALLPAPER)) {
                    mHandler.removeMessages(MSG_SAVE_WALLPAPER);
                }
                Message message = Message.obtain();
                message.what = MSG_SAVE_WALLPAPER;
                message.arg1 = position;
                mHandler.sendMessageDelayed(message, MSG_SAVE_WALLPAPER_DELAY);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAVE_WALLPAPER:
                    int which = msg.arg1;
                    setWhichWallpaper(which);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void initSwitchButtonState() {
        mPandoraLockerSButton.setChecked(isPandoraLockerOn());
        mLockerTypeSButton.setChecked(getUnLockType() == PandoraConfig.UNLOCKER_TYPE_GUSTURE);
        mIsCurrentlyPressed = true;
    }

    protected void setSettingBackground(int resid) {
        mSettingMainView.setBackgroundResource(resid);
        mSettingView.setForegroundDrawable(getActivity().getResources().getDrawable(resid));
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
                        showGustureView(LockPatternDialog.LOCK_PATTERN_TYPE_OPEN);
                    }
                } else {
                    if (mIsCurrentlyPressed) {
                        showGustureView(LockPatternDialog.LOCK_PATTERN_TYPE_CLOSE);
                    }
                }
                break;

            default:
                break;
        }
    }

    private void showGustureView(final int type) {
        View decorView = getActivity().getWindow().getDecorView();
        Bitmap blurBitmap = PandoraUtils.fastBlur(decorView);
        Intent in = new Intent();
        in.setClass(getActivity(), LockPatternDialog.class);
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putParcelable("bitmap", blurBitmap);
        in.putExtra("bundle", bundle);
        startActivityForResult(in, GUSTURE_REQUEST_CODE_SUCCESS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case GUSTURE_REQUEST_CODE_FAIL:
                int type = data.getExtras().getInt("type");
                mIsCurrentlyPressed = false;
                switch (type) {
                    case LockPatternDialog.LOCK_PATTERN_TYPE_CLOSE:
                        mLockerTypeSButton.setChecked(true);
                        break;
                    case LockPatternDialog.LOCK_PATTERN_TYPE_OPEN:
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
            case R.id.setting_systemlocker_prompt:
                closeSystemLocker();
                break;
            case R.id.setting_feedback_prompt:
                startFeedback();
                break;
            case R.id.setting_concern_team:
                aboutUs();
                break;
            case R.id.setting_checkout_new_version_prompt:
                checkNewVersion();
                break;
            case R.id.setting_change_background:
                mIsWallpaperShow = !mIsWallpaperShow;
                if (mIsWallpaperShow) {
                    mPicScrollView.setVisibility(View.VISIBLE);
                } else {
                    mPicScrollView.setVisibility(View.GONE);
                }
                break;
            case R.id.setting_icon:
                if (mSettingView.isPanelExpanded()) {
                    mSettingView.collapsePanel(TIME_COLLAPSE_PANEL_DURATION);
                } else {
                    mSettingView.expandPanel();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        if (null != mImageViewItems) {
            mImageViewItems.clear();
        }
        super.onDestroyView();
    }
}
