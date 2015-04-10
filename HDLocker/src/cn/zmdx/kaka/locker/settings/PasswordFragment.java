
package cn.zmdx.kaka.locker.settings;

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

    private SwitchButton mDelayLockScreen;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.password_fragment, container, false);
        initView();
        return mEntireView;
    }

    private void initView() {
        mDelayLockScreen = (SwitchButton) mEntireView
                .findViewById(R.id.setting_delay_lockscreen_switch_button);
        mDelayLockScreen.setOnCheckedChangeListener(this);

        mDelayLockScreen.setChecked(isDelayLockScreenOn());

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
        int type = PandoraConfig.newInstance(getActivity()).getUnLockType();
        mNoneItemSelect
                .setVisibility(type == KeyguardLockerManager.UNLOCKER_TYPE_NONE ? View.VISIBLE
                        : View.GONE);
        mLockPatternItemSelect
                .setVisibility(type == KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN ? View.VISIBLE
                        : View.GONE);
        mNumberLockItemSelect
                .setVisibility(type == KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK ? View.VISIBLE
                        : View.GONE);
        if (type == KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
            initLockPatternStyle(true);
        } else {
            initLockPatternStyle(false);
        }
    }

    private void initLockPatternStyle(boolean isLockPatternType) {
        if (!isLockPatternType) {
            setStyleViewSelectState(null);
            return;
        }
        int style = PandoraConfig.newInstance(getActivity()).getLockPatternStyle(
                LockPatternManager.LOCK_PATTERN_STYLE_PURE);
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
            int lockPatternStyle = PandoraConfig.newInstance(getActivity()).getLockPatternStyle(
                    LockPatternManager.LOCK_PATTERN_STYLE_PURE);
            setLockTypeNone(lockPatternStyle);
            setLockPatternViewSelectState(mNoneItemSelect);
            setStyleViewSelectState(null);
        } else if (view == mNumberLockItem) {
            int lockPatternStyle = PandoraConfig.newInstance(getActivity()).getLockPatternStyle(
                    LockPatternManager.LOCK_PATTERN_STYLE_PURE);
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
        }
    }

    private void setLockPatternWithStyle(int style) {
        int type = PandoraConfig.newInstance(getActivity()).getUnLockType();
        if (type == KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
            PandoraConfig.newInstance(getActivity()).saveLockPatternStyle(style);
        } else {
            setLockTypePattern(style);
            setLockPatternViewSelectState(mLockPatternItemSelect);
        }
    }

    private void setLockTypeNone(int lockPatternStyle) {
        int curType = PandoraConfig.newInstance(getActivity()).getUnLockType();
        if (curType != KeyguardLockerManager.UNLOCKER_TYPE_NONE) {
            gotoLockerPasswordTypeActivity(curType, KeyguardLockerManager.UNLOCKER_TYPE_NONE,
                    lockPatternStyle);
        }
    }

    private void setLockTypePattern(int lockPatternStyle) {
        int curType = PandoraConfig.newInstance(getActivity()).getUnLockType();
        if (curType != KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
            gotoLockerPasswordTypeActivity(curType,
                    KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN, lockPatternStyle);
        }
    }

    private void setLockTypeNumber(int lockPatternStyle) {
        int curType = PandoraConfig.newInstance(getActivity()).getUnLockType();
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

    private void setLockPatternViewSelectState(View view) {
        mNoneItemSelect.setVisibility(view == mNoneItemSelect ? View.VISIBLE : View.GONE);
        mLockPatternItemSelect.setVisibility(view == mLockPatternItemSelect ? View.VISIBLE
                : View.GONE);
        mNumberLockItemSelect.setVisibility(view == mNumberLockItemSelect ? View.VISIBLE
                : View.GONE);
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
                enableDelayLockScreen();
                UmengCustomEventManager.statisticalPandoraSwitchOpenTimes();
            } else {
                disableDelayLockScreen();
                UmengCustomEventManager.statisticalPandoraSwitchCloseTimes();
            }
        }
    }

    private boolean isDelayLockScreenOn() {
        return PandoraConfig.newInstance(getActivity()).isDelayLockScreenOn();
    }

    private void enableDelayLockScreen() {
        PandoraConfig.newInstance(getActivity()).saveDelayLockScreenState(true);
    }

    private void disableDelayLockScreen() {
        PandoraConfig.newInstance(getActivity()).saveDelayLockScreenState(false);
    }
}
