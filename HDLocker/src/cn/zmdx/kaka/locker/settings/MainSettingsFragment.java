
package cn.zmdx.kaka.locker.settings;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
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
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.LockPatternUtils;
import cn.zmdx.kaka.locker.widget.LockPatternView;
import cn.zmdx.kaka.locker.widget.LockPatternView.Cell;
import cn.zmdx.kaka.locker.widget.LockPatternView.DisplayMode;
import cn.zmdx.kaka.locker.widget.LockPatternView.OnPatternListener;
import cn.zmdx.kaka.locker.widget.SwitchButton;

public class MainSettingsFragment extends BaseSettingsFragment implements OnCheckedChangeListener,
        OnClickListener {
    private View mRootView;

    private TextView mSystemLockerPrompt;

    private TextView mConcernTeam;

    private TextView mChangeBackground;

    private TextView mFeedback;

    private TextView mCheckNewVersion;

    private SwitchButton mPandoraLockerSButton;

    private SwitchButton mLockerTypeSButton;

    private HorizontalScrollView mPicScrollView;

    private LinearLayout mPicView;

    private SparseArray<ImageView> mImageViewItems = new SparseArray<ImageView>();

    private boolean mIsWallpaperShow = false;

    private static final int MSG_SAVE_WALLPAPER = 11;

    private static final int MSG_SAVE_WALLPAPER_DELAY = 100;

    private static final int TIMES_DRAW_GUSTURE = 1;

    private static final int TIMES_DRAW_GUSTURE_AGAIN = 2;

    private boolean mForbidGustureViewShowWhenCreate = false;

    private static final int GUSTURE_LEAST_POINT_COUNT = 4;

    private static final int THREAD_GUSTURE_PROMPT_DELAY = 300;

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
        mRootView = inflater.inflate(R.layout.setting_activity, container, false);
        initView();
        initSwitchButtonState();
        return mRootView;
    }

    private void initView() {

        mSystemLockerPrompt = (TextView) mRootView.findViewById(R.id.setting_systemlocker_prompt);
        mSystemLockerPrompt.setOnClickListener(this);

        mPandoraLockerSButton = (SwitchButton) mRootView
                .findViewById(R.id.setting_pandoralocker_switch_button);
        mPandoraLockerSButton.setOnCheckedChangeListener(this);

        mLockerTypeSButton = (SwitchButton) mRootView
                .findViewById(R.id.setting_pandoralocker_password);
        mLockerTypeSButton.setOnCheckedChangeListener(this);
        // mLockerTypeSButton.setOnClickListener(this);

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
            for (int i = 0; i < PandoraConfig.sWallpapers.length; i++) {
                RelativeLayout artistPiclay = (RelativeLayout) LayoutInflater.from(
                        HDApplication.getInstannce())
                        .inflate(R.layout.setting_wallpaper_item, null);
                ImageView mWallpaper = (ImageView) artistPiclay
                        .findViewById(R.id.setting_wallpaper_image);
                mWallpaper.setImageResource(PandoraConfig.sWallpapers[i]);
                ImageView mWallpaperCircle = (ImageView) artistPiclay
                        .findViewById(R.id.setting_wallpaper_image_border);
                mImageViewItems.put(i, mWallpaperCircle);
                mWallpaper.setTag(i);
                mWallpaper.setOnClickListener(mPicClickListener);
                mPicView.addView(artistPiclay);
            }
            checkWhichWallpaper();
        }
    }

    private void checkWhichWallpaper() {
        int which = getWhichWallpaper();
        if (null != mImageViewItems) {
            mImageViewItems.get(which).setVisibility(View.VISIBLE);
        }
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
        mForbidGustureViewShowWhenCreate = true;
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
                    if (mForbidGustureViewShowWhenCreate) {
                        showGustureView();
                    }
                    setUnLockType(PandoraConfig.UNLOCKER_TYPE_GUSTURE);
                } else {
                    clearLockPatern();
                    setUnLockType(PandoraConfig.UNLOCKER_TYPE_DEFAULT);
                }
                break;

            default:
                break;
        }
    }

    private int onPatternDetectedTimes = 0;

    private void showGustureView() {
        onPatternDetectedTimes = 0;
        final Dialog builder = new Dialog(getActivity(), R.style.gusture_dialog);
        builder.show();
        builder.getWindow().setContentView(R.layout.gusture_view);
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(false);
        final TextView mGusturePrompt = (TextView) builder.findViewById(R.id.gusture_prompt);
        final Button mResetBtn = (Button) builder.findViewById(R.id.gusture_reset);
        final Button mSureBtn = (Button) builder.findViewById(R.id.gusture_sure);
        final LockPatternView lockPatternView = (LockPatternView) builder
                .findViewById(R.id.gusture);
        lockPatternView.setOnPatternListener(new OnPatternListener() {

            @Override
            public void onPatternStart() {
                setGuseturePromptString(getActivity().getResources().getString(
                        R.string.gusture_complete));
            }

            @Override
            public void onPatternDetected(final List<Cell> pattern) {
                if (isLeastPointCount(pattern.size())) {
                    setGuseturePromptString(getActivity().getResources().getString(
                            R.string.gusture_limit_prompt));
                    lockPatternView.setDisplayMode(DisplayMode.Wrong);
                    return;
                }
                if (isPatternDetectedOnce(onPatternDetectedTimes)) {
                    if (checkPattern(pattern)) {
                        setGuseturePromptString(getActivity().getResources().getString(
                                R.string.gusture_new_pattern));
                    } else {
                        lockPatternView.setDisplayMode(DisplayMode.Wrong);
                        setGuseturePromptString(getActivity().getResources().getString(
                                R.string.gusture_error));
                        return;
                    }
                }
                onPatternDetectedTimes = onPatternDetectedTimes + 1;
                if (isPatternDetectedForConfirmation(onPatternDetectedTimes)) {

                    mResetBtn.setClickable(false);
                    lockPatternView.setOnTouchListener(new OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                    return;
                }
                // success to detected once
                setGuseturePromptString(getActivity().getResources().getString(
                        R.string.gusture_save_prompt));
                HDBThreadUtils.postOnUiDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mResetBtn.setVisibility(View.VISIBLE);
                        mSureBtn.setVisibility(View.VISIBLE);
                        saveLockPattern(LockPatternUtils.patternToString(pattern));
                        setGuseturePromptString(getActivity().getResources().getString(
                                R.string.gusture_confirmation_prompt));
                        lockPatternView.clearPattern();
                    }
                }, THREAD_GUSTURE_PROMPT_DELAY);

            }

            private void setGuseturePromptString(String prompt) {
                mGusturePrompt.setText(prompt);
            }

            private boolean isPatternDetectedForConfirmation(int onPatternDetectedTimes) {
                return onPatternDetectedTimes == TIMES_DRAW_GUSTURE_AGAIN;
            }

            private boolean isLeastPointCount(int size) {
                return size < GUSTURE_LEAST_POINT_COUNT;
            }

            private boolean isPatternDetectedOnce(int onPatternDetectedTimes) {
                return onPatternDetectedTimes == TIMES_DRAW_GUSTURE;
            }

            @Override
            public void onPatternCleared() {
                clearLockPatern();
            }

            @Override
            public void onPatternCellAdded(List<Cell> pattern) {

            }
        });
        mResetBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mGusturePrompt.setText(getActivity().getResources().getString(
                        R.string.gustrue_prompt));
                mResetBtn.setVisibility(View.INVISIBLE);
                mSureBtn.setVisibility(View.INVISIBLE);
                onPatternDetectedTimes = 0;
                lockPatternView.clearPattern();
                clearLockPatern();
            }

        });
        mSureBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mLockerTypeSButton.setChecked(false);
                }
                return false;
            }
        });

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
            // case R.id.setting_pandoralocker_password:
            // Log.d("syc", "LockerTypeSButton.isChecked()=" +
            // mLockerTypeSButton.isChecked());
            // if (mLockerTypeSButton.isChecked()) {
            // showGustureView();
            // } else {
            // clearLockPatern();
            // }
            // break;
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
