
package cn.zmdx.kaka.locker.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.security.KeyguardLockerManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBHashUtils;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

public class PandoraNumberLockView extends LinearLayout {

    public interface IVerifyListener {
        void onVerifySuccess();
    }

    public interface INumberLockListener {
        void onSetNumberLock(int type, boolean success);
    }

    public PandoraNumberLockView(Context context, int type, INumberLockListener numberLockListener) {
        super(context);
        mContext = context;
        mNumberLockListener = numberLockListener;
        mNumberLockType = type;
        init();
    }

    public PandoraNumberLockView(Context context, int type, IVerifyListener verifyListener) {
        super(context);
        mContext = context;
        mVerifyListener = verifyListener;
        mNumberLockType = type;
        init();
    }

    private INumberLockListener mNumberLockListener;

    private IVerifyListener mVerifyListener;

    private Context mContext;

    private View mRootView;

    private KeyboardView mKeyboardView;

    private TypefaceTextView mPromptTextView;

    private LinearLayout mNumberLayout;

    private ImageView mNumberOne;

    private ImageView mNumberTwo;

    private ImageView mNumberThree;

    private ImageView mNumberFour;

    public static final int LOCK_NUMBER_TYPE_OPEN = 99;

    public static final int LOCK_NUMBER_TYPE_CLOSE = 98;

    public static final int LOCK_NUMBER_TYPE_VERIFY = 97;

    private int mNumberLockType = 99;

    private static final int NUMBER_LOCK_MAX_COUNT = 4;

    private static final int TIMES_SET_NUMBER_LOCK = 1;

    private static final int TIMES_SET_NUMBER_LOCK_AGAIN = 2;

    private static final int THREAD_SLEPPING_DELAY = 300;

    private static final int VERIFY_FAIL_ANIMATION_DURATION = 60;

    private static final int VERIFY_FAIL_ANIMATION_REPEAT_COUNT = 4;

    private int onPatternDetectedTimes = 0;

    private StringBuffer mPassword = new StringBuffer();

    private void init() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.pandora_number_lock, null);
        addView(mRootView);
        mKeyboardView = (KeyboardView) mRootView.findViewById(R.id.pandora_keyboard_view);
        mKeyboardView.setKeyboard(new Keyboard(mContext, R.layout.pandora_number_lock_content));
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(mKeyboardListener);
        mPromptTextView = (TypefaceTextView) mRootView.findViewById(R.id.number_lock_prompt);
        mNumberLayout = (LinearLayout) findViewById(R.id.pandora_number_layout);
        mNumberOne = (ImageView) mRootView.findViewById(R.id.pandora_number_one);
        mNumberTwo = (ImageView) mRootView.findViewById(R.id.pandora_number_two);
        mNumberThree = (ImageView) mRootView.findViewById(R.id.pandora_number_three);
        mNumberFour = (ImageView) mRootView.findViewById(R.id.pandora_number_four);
    }

    private void clearPasswordStringBuffer(boolean isNeedAnimation) {
        if (isNeedAnimation) {
            createVerifyFailAnimations();
        } else {
            mPassword.delete(0, mPassword.length());
            showPasswordStringBuffer(false);
        }
    }

    private void createVerifyFailAnimations() {
        int displacementPX = (int) mContext.getResources().getDimension(
                R.dimen.pandora_number_lock_translation_x_displacement);
        ObjectAnimator numberOneX = ObjectAnimator.ofFloat(mNumberLayout, "translationX",
                displacementPX);
        numberOneX.setRepeatCount(VERIFY_FAIL_ANIMATION_REPEAT_COUNT);
        numberOneX.setInterpolator(new CycleInterpolator(5));
        numberOneX.setDuration(VERIFY_FAIL_ANIMATION_DURATION);
        numberOneX.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                mPassword.delete(0, mPassword.length());
                showPasswordStringBuffer(false);
            }
        });
        numberOneX.start();
    }

    protected void appendPasswordStringBuffer(String password) {
        mPassword.append(password);
    }

    protected void showPasswordStringBuffer(boolean isNeedAnimation) {
        switch (mPassword.toString().length()) {
            case 0:
                mNumberOne.setImageResource(R.drawable.pandora_number_lock_line);
                mNumberTwo.setImageResource(R.drawable.pandora_number_lock_line);
                mNumberThree.setImageResource(R.drawable.pandora_number_lock_line);
                mNumberFour.setImageResource(R.drawable.pandora_number_lock_line);
                break;
            case 1:
                mNumberOne.setImageResource(R.drawable.pandora_number_lock_point);
                mNumberTwo.setImageResource(R.drawable.pandora_number_lock_line);
                mNumberThree.setImageResource(R.drawable.pandora_number_lock_line);
                mNumberFour.setImageResource(R.drawable.pandora_number_lock_line);
                if (isNeedAnimation) {
                    createNumberStateAnimations(mNumberOne);
                }
                break;
            case 2:
                mNumberOne.setImageResource(R.drawable.pandora_number_lock_point);
                mNumberTwo.setImageResource(R.drawable.pandora_number_lock_point);
                mNumberThree.setImageResource(R.drawable.pandora_number_lock_line);
                mNumberFour.setImageResource(R.drawable.pandora_number_lock_line);
                if (isNeedAnimation) {
                    createNumberStateAnimations(mNumberTwo);
                }
                break;
            case 3:
                mNumberOne.setImageResource(R.drawable.pandora_number_lock_point);
                mNumberTwo.setImageResource(R.drawable.pandora_number_lock_point);
                mNumberThree.setImageResource(R.drawable.pandora_number_lock_point);
                mNumberFour.setImageResource(R.drawable.pandora_number_lock_line);
                if (isNeedAnimation) {
                    createNumberStateAnimations(mNumberThree);
                }
                break;
            case 4:
                mNumberOne.setImageResource(R.drawable.pandora_number_lock_point);
                mNumberTwo.setImageResource(R.drawable.pandora_number_lock_point);
                mNumberThree.setImageResource(R.drawable.pandora_number_lock_point);
                mNumberFour.setImageResource(R.drawable.pandora_number_lock_point);
                if (isNeedAnimation) {
                    createNumberStateAnimations(mNumberFour);
                }
                break;

            default:
                break;
        }

    }

    private void createNumberStateAnimations(final ImageView views) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(views, "scaleX", 1.3f);
        scaleXAnimator.setDuration(100);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(views, "scaleY", 1.3f);
        scaleYAnimator.setDuration(100);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                ObjectAnimator resetScaleXAnimator = ObjectAnimator.ofFloat(views, "scaleX", 1f);
                resetScaleXAnimator.setDuration(100);

                ObjectAnimator resetScaleYAnimator = ObjectAnimator.ofFloat(views, "scaleY", 1f);
                resetScaleYAnimator.setDuration(100);
                AnimatorSet resetAnimatorSet = new AnimatorSet();
                resetAnimatorSet.playTogether(resetScaleXAnimator, resetScaleYAnimator);
                resetAnimatorSet.start();
            }
        });
        animatorSet.start();

    }

    private OnKeyboardActionListener mKeyboardListener = new OnKeyboardActionListener() {
        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if (primaryCode == Keyboard.KEYCODE_DELETE) {
                if (mPassword.length() > 0) {
                    mPassword.delete(mPassword.length() - 1, mPassword.length());
                    showPasswordStringBuffer(false);
                }
            } else if (mPassword.length() < NUMBER_LOCK_MAX_COUNT - 1) {
                String password = Character.toString((char) primaryCode);
                appendPasswordStringBuffer(password);
                showPasswordStringBuffer(true);
            } else if (mPassword.length() == NUMBER_LOCK_MAX_COUNT - 1) {
                // 密码位数超过
                String password = Character.toString((char) primaryCode);
                appendPasswordStringBuffer(password);
                showPasswordStringBuffer(true);
                HDBThreadUtils.postOnUiDelayed(new Runnable() {

                    @Override
                    public void run() {
                        switch (mNumberLockType) {
                            case LOCK_NUMBER_TYPE_OPEN:
                                openNumberLock();
                                break;
                            case LOCK_NUMBER_TYPE_CLOSE:
                                closeNumberLock();
                                break;
                            case LOCK_NUMBER_TYPE_VERIFY:
                                verifyNumberLock();
                                break;

                            default:
                                break;
                        }
                    }
                }, 100);
            }
        }

        @Override
        public void swipeUp() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onPress(int primaryCode) {

        }

    };

    protected void openNumberLock() {
        if (isDetectedOnce(onPatternDetectedTimes)) {
            if (!checkNumberLock()) {
                setPromptString(mContext.getResources().getString(
                        R.string.number_lock_confirmation_fail_prompt));
                clearPasswordStringBuffer(true);
                return;
            }
        }
        onPatternDetectedTimes = onPatternDetectedTimes + 1;
        if (isPatternDetectedForConfirmation(onPatternDetectedTimes)) {
            // TODO   success to set munber lock
            HDBThreadUtils.postOnUiDelayed(new Runnable() {

                @Override
                public void run() {
                    setUnLockType(KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK);
                    if (null != mNumberLockListener) {
                        mNumberLockListener.onSetNumberLock(LOCK_NUMBER_TYPE_OPEN, true);
                    }
                }
            }, THREAD_SLEPPING_DELAY);
            return;
        }
        setPromptString(mContext.getResources().getString(R.string.number_lock_confirmation_prompt));
        saveNumberLockString();
        clearPasswordStringBuffer(false);
    }

    protected void closeNumberLock() {
        if (checkNumberLock()) {
            clearSaveNumberLockString();
            setUnLockType(KeyguardLockerManager.UNLOCKER_TYPE_NONE);
            if (null != mNumberLockListener) {
                mNumberLockListener.onSetNumberLock(LOCK_NUMBER_TYPE_CLOSE, true);
            }
        } else {
            setPromptString(mContext.getResources().getString(R.string.number_lock_verify_fail));
            clearPasswordStringBuffer(true);
        }

    }

    protected void verifyNumberLock() {
        if (checkNumberLock()) {
            if (null != mVerifyListener) {
                mVerifyListener.onVerifySuccess();
            }
        } else {
            setPromptString(mContext.getResources().getString(R.string.number_lock_verify_fail));
            clearPasswordStringBuffer(true);
        }

    }

    private boolean isDetectedOnce(int onPatternDetectedTimes) {
        return onPatternDetectedTimes == TIMES_SET_NUMBER_LOCK;
    }

    private boolean isPatternDetectedForConfirmation(int onPatternDetectedTimes) {
        return onPatternDetectedTimes == TIMES_SET_NUMBER_LOCK_AGAIN;
    }

    private boolean checkNumberLock() {
        String stored = getNumberLockString();
        if (!stored.equals(null)) {
            String md5Password = HDBHashUtils.getStringMD5(mPassword.toString());
            return stored.equals(md5Password) ? true : false;
        }
        return false;
    }

    private void saveNumberLockString() {
        String md5Password = HDBHashUtils.getStringMD5(mPassword.toString());
        PandoraConfig.newInstance(mContext).saveNumberLockString(md5Password);
    }

    private String getNumberLockString() {
        return PandoraConfig.newInstance(mContext).getNumberLockString();
    }

    private void clearSaveNumberLockString() {
        PandoraConfig.newInstance(mContext).saveLockPattern("");
    }

    private void setUnLockType(int type) {
        PandoraConfig.newInstance(mContext).saveUnlockType(type);
    }

    private void setPromptString(String promptString) {
        mPromptTextView.setVisibility(View.GONE);
        mPromptTextView.setText(promptString);
        mPromptTextView.setVisibility(View.VISIBLE);
        createPromptTextViewAnimations();
    }

    private void createPromptTextViewAnimations() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mPromptTextView, "alpha", 0, 1f);
        animator.setDuration(500);
        animator.start();
    }

}
