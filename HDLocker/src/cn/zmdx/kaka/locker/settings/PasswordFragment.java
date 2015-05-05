
package cn.zmdx.kaka.locker.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.pattern.LockPatternManager;
import cn.zmdx.kaka.locker.security.KeyguardLockerManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.widget.BaseLinearLayout;
import cn.zmdx.kaka.locker.widget.SwitchButton;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.umeng.analytics.MobclickAgent;

public class PasswordFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {

    private View mEntireView;

    private BaseLinearLayout mNoneItem;

    private ImageView mNoneItemSelect;

    private ImageView mLockPatternItemSelect;

    private BaseLinearLayout mNumberLockItem;

    private ImageView mNumberLockItemSelect;

    private LinearLayout mLockPatternLayout;

    private FrameLayout mPureStyle;

    private ImageView mPureStyleSelect;

    private FrameLayout mNeonStyle;

    private ImageView mNeonStyleSelect;

    private FrameLayout mFlorescenceStyle;

    private ImageView mFlorescenceStyleSelect;

    private FrameLayout mTouchStyle;

    private ImageView mTouchStyleSelect;

    private FrameLayout mDeepSeaStyle;

    private ImageView mDeepSeaStyleSelect;

    private FrameLayout mMidsummerStyle;

    private ImageView mMidsummerStyleSelect;

    private TypefaceTextView mPureStylePrompt;

    private TypefaceTextView mNeonStylePrompt;

    private TypefaceTextView mFlorescenceStylePrompt;

    private TypefaceTextView mTouchStylePrompt;

    private TypefaceTextView mDeepSeaStylePrompt;

    private TypefaceTextView mMidsummerStylePrompt;

    private SwitchButton mDelayLockScreen, mHiddenLine;

    private ImageView mResetLine;

    private LinearLayout mResetLayout;

    private PandoraConfig mPandoraConfig;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.password_fragment, container, false);
        mPandoraConfig = PandoraConfig.newInstance(getActivity());
        initView();
        return mEntireView;
    }

    private void initView() {
        mDelayLockScreen = (SwitchButton) mEntireView
                .findViewById(R.id.setting_delay_lockscreen_switch_button);
        mDelayLockScreen.setOnCheckedChangeListener(this);

        mHiddenLine = (SwitchButton) mEntireView
                .findViewById(R.id.setting_hidden_lock_pattern_line_switch_button);
        mHiddenLine.setOnCheckedChangeListener(this);

        mDelayLockScreen.setChecked(isDelayLockScreenOn());
        mHiddenLine.setChecked(isHiddenLineOn());

        mResetLine = (ImageView) mEntireView
                .findViewById(R.id.pandora_setting_password_reset_item_line);

        mResetLayout = (LinearLayout) mEntireView.findViewById(R.id.pandora_setting_password_reset);
        mResetLayout.setOnClickListener(this);
        int curType = mPandoraConfig.getUnLockType();
        checkResetLayoutVisibility(curType);

        mNoneItem = (BaseLinearLayout) mEntireView.findViewById(R.id.setting_password_none_item);
        mNoneItem.setOnClickListener(this);
        mNoneItemSelect = (ImageView) mEntireView.findViewById(R.id.setting_password_none_select);
        mLockPatternItemSelect = (ImageView) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_select);
        mNumberLockItem = (BaseLinearLayout) mEntireView
                .findViewById(R.id.setting_password_number_lock_item);
        mNumberLockItem.setOnClickListener(this);
        mNumberLockItemSelect = (ImageView) mEntireView
                .findViewById(R.id.setting_password_number_lock_select);
        initLockPatternLayout();
        initPasswordTypeState();
    }

    private void initLockPatternLayout() {
        mLockPatternLayout = (LinearLayout) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_layout);

        mPureStyle = (FrameLayout) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_pure_style);
        mPureStyleSelect = (ImageView) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_pure_style_select);

        mNeonStyle = (FrameLayout) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_neon_style);
        mNeonStyleSelect = (ImageView) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_neon_style_select);

        mFlorescenceStyle = (FrameLayout) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_florescence_style);
        mFlorescenceStyleSelect = (ImageView) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_florescence_style_select);

        mTouchStyle = (FrameLayout) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_touch_style);
        mTouchStyleSelect = (ImageView) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_touch_style_select);

        mDeepSeaStyle = (FrameLayout) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_deep_sea_style);
        mDeepSeaStyleSelect = (ImageView) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_deep_sea_style_select);

        mMidsummerStyle = (FrameLayout) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_midsummer_style);
        mMidsummerStyleSelect = (ImageView) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_midsummer_style_select);

        initLayoutSize(mPureStyle, mNeonStyle, mFlorescenceStyle, mTouchStyle, mDeepSeaStyle,
                mMidsummerStyle);

        mPureStylePrompt = (TypefaceTextView) mEntireView
                .findViewById(R.id.setting_password_style_pure_prompt);
        mNeonStylePrompt = (TypefaceTextView) mEntireView
                .findViewById(R.id.setting_password_style_neon_prompt);
        mFlorescenceStylePrompt = (TypefaceTextView) mEntireView
                .findViewById(R.id.setting_password_style_florescence_prompt);
        mTouchStylePrompt = (TypefaceTextView) mEntireView
                .findViewById(R.id.setting_password_style_touch_prompt);
        mDeepSeaStylePrompt = (TypefaceTextView) mEntireView
                .findViewById(R.id.setting_password_style_deep_sea_prompt);
        mMidsummerStylePrompt = (TypefaceTextView) mEntireView
                .findViewById(R.id.setting_password_style_midsummer_prompt);

    }

    private void initLayoutSize(View... views) {
        int screenWidth = BaseInfoHelper.getRealWidth(getActivity());
        int imageViewPadding = (int) getResources().getDimension(
                R.dimen.setting_password_lock_pattern_image_padding);
        int paddingTop = BaseInfoHelper.dip2px(getActivity(), 20);
        int paddingBottom = BaseInfoHelper.dip2px(getActivity(), 40);
        int paddingLeftRight = BaseInfoHelper.dip2px(getActivity(), 20);
        mLockPatternLayout
                .setPadding(paddingLeftRight, paddingTop, paddingLeftRight, paddingBottom);
        int imageWidth = (int) (screenWidth - imageViewPadding * 2 - paddingLeftRight * 2 - 9) / 3;
        int imageHeight = imageWidth;

        for (View view : views) {
            LayoutParams params = view.getLayoutParams();
            params.width = imageWidth;
            params.height = imageHeight;
            view.setLayoutParams(params);
            view.setOnClickListener(this);
        }

    }

    private void initPasswordTypeState() {
        int type = mPandoraConfig.getUnLockType();

        int noneItemVisibility = type == KeyguardLockerManager.UNLOCKER_TYPE_NONE ? View.VISIBLE
                : View.GONE;
        alphaAnimator(mNoneItemSelect, mNoneItemSelect.getVisibility(), noneItemVisibility);

        int lockPatternItemVisibility = type == KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN ? View.VISIBLE
                : View.GONE;
        alphaAnimator(mLockPatternItemSelect, mLockPatternItemSelect.getVisibility(),
                lockPatternItemVisibility);

        int numberLocknItemVisibility = type == KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK ? View.VISIBLE
                : View.GONE;
        alphaAnimator(mNumberLockItemSelect, mNumberLockItemSelect.getVisibility(),
                numberLocknItemVisibility);

        if (type == KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
            initLockPatternStyle(true);
        } else {
            initLockPatternStyle(false);
        }

        checkResetLayoutVisibility(type);
    }

    private void initLockPatternStyle(boolean isLockPatternType) {
        if (!isLockPatternType) {
            setStyleViewSelectState(null);
            return;
        }
        int style = mPandoraConfig.getLockPatternStyle(LockPatternManager.LOCK_PATTERN_STYLE_PURE);
        if (style == LockPatternManager.LOCK_PATTERN_STYLE_PURE) {
            setStyleViewSelectState(mPureStyle);
        } else if (style == LockPatternManager.LOCK_PATTERN_STYLE_NEON) {
            setStyleViewSelectState(mNeonStyle);
        } else if (style == LockPatternManager.LOCK_PATTERN_STYLE_FLORESCENCE) {
            setStyleViewSelectState(mFlorescenceStyle);
        } else if (style == LockPatternManager.LOCK_PATTERN_STYLE_TOUCH) {
            setStyleViewSelectState(mTouchStyle);
        } else if (style == LockPatternManager.LOCK_PATTERN_STYLE_DEEPSEA) {
            setStyleViewSelectState(mDeepSeaStyle);
        } else if (style == LockPatternManager.LOCK_PATTERN_STYLE_MIDSUMMER) {
            setStyleViewSelectState(mMidsummerStyle);
        }
    }

    @Override
    public void onClick(View view) {
        setStyleViewSelectState(view);

        if (view == mNoneItem) {
            int lockPatternStyle = mPandoraConfig
                    .getLockPatternStyle(LockPatternManager.LOCK_PATTERN_STYLE_PURE);
            setLockTypeNone(lockPatternStyle);
            setLockPatternViewSelectState(mNoneItemSelect);
            setStyleViewSelectState(null);
        } else if (view == mNumberLockItem) {
            int lockPatternStyle = mPandoraConfig
                    .getLockPatternStyle(LockPatternManager.LOCK_PATTERN_STYLE_PURE);
            setLockTypeNumber(lockPatternStyle);
            setLockPatternViewSelectState(mNumberLockItemSelect);
            setStyleViewSelectState(null);
        } else if (view == mPureStyle) {
            setLockPatternWithStyle(LockPatternManager.LOCK_PATTERN_STYLE_PURE);
        } else if (view == mNeonStyle) {
            setLockPatternWithStyle(LockPatternManager.LOCK_PATTERN_STYLE_NEON);
        } else if (view == mFlorescenceStyle) {
            setLockPatternWithStyle(LockPatternManager.LOCK_PATTERN_STYLE_FLORESCENCE);
        } else if (view == mTouchStyle) {
            setLockPatternWithStyle(LockPatternManager.LOCK_PATTERN_STYLE_TOUCH);
        } else if (view == mDeepSeaStyle) {
            setLockPatternWithStyle(LockPatternManager.LOCK_PATTERN_STYLE_DEEPSEA);
        } else if (view == mMidsummerStyle) {
            setLockPatternWithStyle(LockPatternManager.LOCK_PATTERN_STYLE_MIDSUMMER);
        } else if (view == mResetLayout) {
            int curType = mPandoraConfig.getUnLockType();
            int lockPatternStyle = mPandoraConfig
                    .getLockPatternStyle(LockPatternManager.LOCK_PATTERN_STYLE_PURE);
            gotoLockerPasswordTypeActivity(curType, curType, lockPatternStyle);
        }
    }

    private void setLockPatternWithStyle(int style) {
        int type = mPandoraConfig.getUnLockType();
        if (type == KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
            mPandoraConfig.saveLockPatternStyle(style);
        } else {
            setLockTypePattern(style);
            setLockPatternViewSelectState(mLockPatternItemSelect);
        }
    }

    private void setLockTypeNone(int lockPatternStyle) {
        int curType = mPandoraConfig.getUnLockType();
        if (curType != KeyguardLockerManager.UNLOCKER_TYPE_NONE) {
            gotoLockerPasswordTypeActivity(curType, KeyguardLockerManager.UNLOCKER_TYPE_NONE,
                    lockPatternStyle);
        }
    }

    private void setLockTypePattern(int lockPatternStyle) {
        int curType = mPandoraConfig.getUnLockType();
        if (curType != KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
            gotoLockerPasswordTypeActivity(curType,
                    KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN, lockPatternStyle);
        }
    }

    private void setLockTypeNumber(int lockPatternStyle) {
        int curType = mPandoraConfig.getUnLockType();
        if (curType != KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK) {
            gotoLockerPasswordTypeActivity(curType,
                    KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK, lockPatternStyle);
        }
    }

    private void gotoLockerPasswordTypeActivity(int curType, int targetType, int lockPatternStyle) {
        Intent in = new Intent();
        in.setClass(getActivity(), PasswordPromptActivity.class);
        in.putExtra("targetType", targetType);
        in.putExtra("lockPatternStyle", lockPatternStyle);
        getActivity().startActivityForResult(in,
                PasswordPromptActivity.REQUEST_LOCKER_PASSWORD_TYPE_CODE);
        getActivity().overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    public void reset() {
        initPasswordTypeState();
    }

    private void setLockPatternViewSelectState(final View view) {
        int noneItemVisibility = view == mNoneItemSelect ? View.VISIBLE : View.GONE;
        alphaAnimator(mNoneItemSelect, mNoneItemSelect.getVisibility(), noneItemVisibility);

        int lockPatternItemVisibility = view == mLockPatternItemSelect ? View.VISIBLE : View.GONE;
        alphaAnimator(mLockPatternItemSelect, mLockPatternItemSelect.getVisibility(),
                lockPatternItemVisibility);

        int numberLockItemVisibility = view == mNumberLockItemSelect ? View.VISIBLE : View.GONE;
        alphaAnimator(mNumberLockItemSelect, mNumberLockItemSelect.getVisibility(),
                numberLockItemVisibility);

    }

    private void alphaAnimator(final View view, final int oldState, final int targetState) {
        if (oldState == targetState) {
            return;
        }

        if (targetState == View.VISIBLE) {
            view.setVisibility(targetState);
        }
        float star = targetState == View.GONE ? 1f : 0f;
        float end = targetState == View.GONE ? 0f : 1f;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", star, end);
        animator.setDuration(400);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(targetState);
            }
        });
        animator.start();
    }

    private void setStyleViewSelectState(View view) {
        int normalColor = getResources().getColor(R.color.setting_text_normal_color);
        int greyColor = getResources().getColor(R.color.setting_text_grey_color);

        mPureStyleSelect.setVisibility(view == mPureStyle ? View.VISIBLE : View.GONE);
        mPureStylePrompt.setTextColor(view == mPureStyle ? greyColor : normalColor);

        mNeonStyleSelect.setVisibility(view == mNeonStyle ? View.VISIBLE : View.GONE);
        mNeonStylePrompt.setTextColor(view == mNeonStyle ? greyColor : normalColor);

        mFlorescenceStyleSelect.setVisibility(view == mFlorescenceStyle ? View.VISIBLE : View.GONE);
        mFlorescenceStylePrompt.setTextColor(view == mFlorescenceStyle ? greyColor : normalColor);

        mTouchStyleSelect.setVisibility(view == mTouchStyle ? View.VISIBLE : View.GONE);
        mTouchStylePrompt.setTextColor(view == mTouchStyle ? greyColor : normalColor);

        mDeepSeaStyleSelect.setVisibility(view == mDeepSeaStyle ? View.VISIBLE : View.GONE);
        mDeepSeaStylePrompt.setTextColor(view == mDeepSeaStyle ? greyColor : normalColor);

        mMidsummerStyleSelect.setVisibility(view == mMidsummerStyle ? View.VISIBLE : View.GONE);
        mMidsummerStylePrompt.setTextColor(view == mMidsummerStyle ? greyColor : normalColor);
    }

    private void checkResetLayoutVisibility(int type) {
        if (type != KeyguardLockerManager.UNLOCKER_TYPE_NONE) {
            mResetLine.setVisibility(View.VISIBLE);
            mResetLayout.setVisibility(View.VISIBLE);
        } else {
            mResetLine.setVisibility(View.GONE);
            mResetLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PasswordFragment");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PasswordFragment");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mDelayLockScreen) {
            if (isChecked) {
                if (!isDelayLockScreenOn()) {
                    Toast.makeText(getActivity(), R.string.toast_delay_locksrceen,
                            Toast.LENGTH_SHORT).show();
                }
                enableDelayLockScreen();
                UmengCustomEventManager.statisticalOpenDelayLockScreen();
            } else {
                disableDelayLockScreen();
                UmengCustomEventManager.statisticalCloseDelayLockScreen();
            }
        }
        if (buttonView == mHiddenLine) {
            if (isChecked) {
                enableHiddenLine();
                UmengCustomEventManager.statisticalOpenHiddenLine();
            } else {
                disableHiddenLine();
                UmengCustomEventManager.statisticalCloseHiddenLine();
            }
        }
    }

    private boolean isDelayLockScreenOn() {
        return mPandoraConfig.isDelayLockScreenOn();
    }

    private void enableDelayLockScreen() {
        mPandoraConfig.saveDelayLockScreenState(true);
    }

    private void disableDelayLockScreen() {
        mPandoraConfig.saveDelayLockScreenState(false);
    }

    private boolean isHiddenLineOn() {
        return mPandoraConfig.isHiddenLineOn();
    }

    private void enableHiddenLine() {
        mPandoraConfig.saveHiddenLineState(true);
    }

    private void disableHiddenLine() {
        mPandoraConfig.saveHiddenLineState(false);
    }
}
