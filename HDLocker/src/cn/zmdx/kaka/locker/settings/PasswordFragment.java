
package cn.zmdx.kaka.locker.settings;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.pattern.LockPatternManager;
import cn.zmdx.kaka.locker.security.KeyguardLockerManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.widget.BaseLinearLayout;
import cn.zmdx.kaka.locker.widget.PandoraLockPatternView;
import cn.zmdx.kaka.locker.widget.PandoraLockPatternView.ILockPatternListener;
import cn.zmdx.kaka.locker.widget.PandoraNumberLockView;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.afollestad.materialdialogs.MaterialDialog;

public class PasswordFragment extends Fragment implements OnClickListener {

    private View mEntireView;

    private BaseLinearLayout mNoneItem;

    private ImageView mNoneItemSelect;

    private BaseLinearLayout mLockPatternItem;

    private ImageView mLockPatternItemSelect;

    private BaseLinearLayout mNumberLockItem;

    private ImageView mNumberLockItemSelect;

    private LinearLayout mLockPatternLayout;

    private LinearLayout mLockPatternStyleLineOne;

    private LinearLayout mLockPatternStyleLineTwo;

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

    private MaterialDialog mNumberLockDialog;

    private MaterialDialog mLockPatternDialog;

    private TypefaceTextView mPureStylePrompt;

    private TypefaceTextView mNeonStylePrompt;

    private TypefaceTextView mFlorescenceStylePrompt;

    private TypefaceTextView mTouchStylePrompt;

    private TypefaceTextView mDeepSeaStylePrompt;

    private TypefaceTextView mMidsummerStylePrompt;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.password_fragment, container, false);
        initView();
        return mEntireView;
    }

    private void initView() {
        mNoneItem = (BaseLinearLayout) mEntireView.findViewById(R.id.setting_password_none_item);
        mNoneItem.setOnClickListener(this);
        mNoneItemSelect = (ImageView) mEntireView.findViewById(R.id.setting_password_none_select);
        mLockPatternItem = (BaseLinearLayout) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_item);
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
        mLockPatternStyleLineOne = (LinearLayout) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_style_line_one);
        mLockPatternStyleLineTwo = (LinearLayout) mEntireView
                .findViewById(R.id.setting_password_lock_pattern_style_line_two);

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
        }
        // else if (view == mLockPatternItem) {
        // setLockTypePattern();
        // initPasswordType();
        // }
        else if (view == mNumberLockItem) {
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
        switch (curType) {
            case KeyguardLockerManager.UNLOCKER_TYPE_NONE:
                if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
                    setLockPatternViewVisibleWithLockPatternListener(
                            PandoraLockPatternView.TYPE_LOCK_PATTERN_OPEN, lockPatternStyle);
                } else if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK) {
                    setNumberLockViewVisibleWithNumberLockListener(PandoraNumberLockView.LOCK_NUMBER_TYPE_OPEN);
                }
                break;
            case KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN:
                if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_NONE) {
                    setLockPatternViewVisibleWithLockPatternListener(
                            PandoraLockPatternView.TYPE_LOCK_PATTERN_CLOSE, lockPatternStyle);
                } else if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK) {
                    setLockPatternViewVisibleWithVerifyListener(true, lockPatternStyle);
                }
                break;
            case KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK:
                if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_NONE) {
                    setNumberLockViewVisibleWithNumberLockListener(PandoraNumberLockView.LOCK_NUMBER_TYPE_CLOSE);
                } else if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
                    setNumberLockViewVisibleWithVerifyListener(true, lockPatternStyle);
                }
                break;

            default:
                break;
        }

    }

    private void setLockPatternViewVisibleWithVerifyListener(final boolean isNeedNumberLockView,
            int lockPatternStyle) {
        PandoraLockPatternView mLockPatternView = new PandoraLockPatternView(getActivity(),
                PandoraLockPatternView.TYPE_LOCK_PATTERN_VERIFY, lockPatternStyle,
                new PandoraLockPatternView.IVerifyListener() {

                    @Override
                    public void onVerifySuccess() {
                        // TODO
                        if (isNeedNumberLockView) {
                            setNumberLockViewVisibleWithNumberLockListener(PandoraNumberLockView.LOCK_NUMBER_TYPE_OPEN);
                        } else {
                            dismissLockPatternDialog();
                        }
                    }
                }, false);
        createLockPatternDialog(mLockPatternView);
    }

    private void setLockPatternViewVisibleWithLockPatternListener(int type, int lockPatternStyle) {
        PandoraLockPatternView mLockPatternView = new PandoraLockPatternView(getActivity(), type,
                lockPatternStyle, new ILockPatternListener() {

                    @Override
                    public void onPatternDetected(int type, boolean success) {
                        // TODO
                        dismissLockPatternDialog();
                    }
                });
        createLockPatternDialog(mLockPatternView);
    }

    private void setNumberLockViewVisibleWithVerifyListener(final boolean isNeedLockPatternView,
            final int lockPatternStyle) {
        PandoraNumberLockView mNumberLockView = new PandoraNumberLockView(getActivity(),
                PandoraNumberLockView.LOCK_NUMBER_TYPE_VERIFY,
                new PandoraNumberLockView.IVerifyListener() {

                    @Override
                    public void onVerifySuccess() {
                        // TODO
                        if (isNeedLockPatternView) {
                            setLockPatternViewVisibleWithLockPatternListener(
                                    PandoraLockPatternView.TYPE_LOCK_PATTERN_OPEN, lockPatternStyle);
                        } else {
                            dismissNumberLockDialog();
                        }
                    }

                }, false);
        createNumberLockDialog(mNumberLockView);
    }

    private void setNumberLockViewVisibleWithNumberLockListener(int type) {
        PandoraNumberLockView mNumberLockView = new PandoraNumberLockView(getActivity(), type,
                new PandoraNumberLockView.INumberLockListener() {

                    @Override
                    public void onSetNumberLock(int type, boolean success) {
                        // TODO
                        dismissNumberLockDialog();
                    }
                });
        createNumberLockDialog(mNumberLockView);
    }

    private void createLockPatternDialog(View customView) {
        dismissNumberLockDialog();
        mLockPatternDialog = new MaterialDialog.Builder(getActivity()).customView(customView, true)
                .dismissListener(mOnDismissListener).build();
        mLockPatternDialog.show();
        int screenWidth = BaseInfoHelper.getRealWidth(getActivity());
        int lockWidth = (int) (screenWidth * PandoraLockPatternView.SCALE_LOCK_PATTERN_WIDTH);
        int padding = (int) (screenWidth * PandoraLockPatternView.SCALE_LOCK_PATTERN_PADDING * 2);
        int width = lockWidth + padding;
        mLockPatternDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void createNumberLockDialog(View customView) {
        dismissLockPatternDialog();
        mNumberLockDialog = new MaterialDialog.Builder(getActivity()).customView(customView, true)
                .dismissListener(mOnDismissListener).build();
        mNumberLockDialog.show();
    }

    private void dismissLockPatternDialog() {
        if (null != mLockPatternDialog) {
            mLockPatternDialog.cancel();
        }
    }

    private void dismissNumberLockDialog() {
        if (null != mNumberLockDialog) {
            mNumberLockDialog.cancel();
        }
    }

    private void reset() {
        initPasswordTypeState();
    }

    private OnDismissListener mOnDismissListener = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            reset();
        }
    };

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
}
