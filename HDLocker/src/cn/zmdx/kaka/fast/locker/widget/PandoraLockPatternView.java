
package cn.zmdx.kaka.fast.locker.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zmdx.kaka.fast.locker.HDApplication;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.security.KeyguardLockerManager;
import cn.zmdx.kaka.fast.locker.settings.LockerPasswordActivity;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.fast.locker.utils.HDBHashUtils;
import cn.zmdx.kaka.fast.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.fast.locker.utils.LockPatternUtils;
import cn.zmdx.kaka.fast.locker.widget.LockPatternView.Cell;
import cn.zmdx.kaka.fast.locker.widget.LockPatternView.DisplayMode;
import cn.zmdx.kaka.fast.locker.widget.LockPatternView.OnPatternListener;

public class PandoraLockPatternView extends LinearLayout {

    private Context mContext;

    public static final int TYPE_LOCK_PATTERN_CLOSE = 110;

    public static final int TYPE_LOCK_PATTERN_OPEN = 120;

    public static final int TYPE_LOCK_PATTERN_VERIFY = 119;

    private int mLockPatternType = 120;

    private View mRootView;

    private TextView mPromptTextView;

    private LockPatternView mLockPatternView;

    private static final int TIMES_DRAW_LOCK_PATTERN = 1;

    private static final int TIMES_DRAW_LOCK_PATTERN_AGAIN = 2;

    private static final int LOCK_PATTERN_LEAST_POINT_COUNT = 4;

    private static final int THREAD_SLEPPING_DELAY = 300;

    private static final int ERROT_MAX_TIMES = 5;

    private int onPatternDetectedTimes = 0;

    private int mErrorTimes = 0;

    private IVerifyListener mVerifyListener;

    private ILockPatternListener mLockPatternListener;

    public static double SCALE_LOCK_PATTERN_WIDTH = 0.8;

    public static double SCALE_LOCK_PATTERN_PADDING = 0.05;

    public interface IVerifyListener {
        void onVerifySuccess();
    }

    public interface ILockPatternListener {
        void onPatternDetected(int type, boolean success);
    }

    /**
     * @param context
     * @param type
     * @param lockPatternListener
     */
    public PandoraLockPatternView(Context context, int type,
            ILockPatternListener lockPatternListener) {
        super(context);
        mContext = context;
        mLockPatternListener = lockPatternListener;
        mLockPatternType = type;
        init();
    }

    public PandoraLockPatternView(Context context, int type, IVerifyListener verifyListener) {
        super(context);
        mContext = context;
        mVerifyListener = verifyListener;
        mLockPatternType = type;
        init();
    }

    private void init() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.pandora_lock_pattern, null);
        int padding = (int) (SCALE_LOCK_PATTERN_PADDING * BaseInfoHelper.getRealWidth(mContext));
        mRootView.setPadding(padding, 0, padding, 0);
        addView(mRootView);
        mPromptTextView = (TextView) findViewById(R.id.pandora_lock_pattern_prompt);
        if (mContext instanceof HDApplication) {
            mLockPatternView = (LockPatternView) findViewById(R.id.pandora_lock_pattern_lockscreen);
            mPromptTextView.setTextColor(mContext.getResources().getColor(
                    R.color.lock_pattern_screen));
        } else if (mContext instanceof LockerPasswordActivity) {
            mLockPatternView = (LockPatternView) findViewById(R.id.pandora_lock_pattern);
            mPromptTextView.setTextColor(mContext.getResources().getColor(
                    R.color.fast_setting_text_color));
        }
        mLockPatternView.setVisibility(View.VISIBLE);

        int screenWidth = BaseInfoHelper.getRealWidth(mContext);
        int lockPatternWidth = (int) (screenWidth * SCALE_LOCK_PATTERN_WIDTH);
        ViewGroup.LayoutParams params = mLockPatternView.getLayoutParams();
        params.width = lockPatternWidth;
        params.height = lockPatternWidth;
        mLockPatternView.setLayoutParams(params);
        mLockPatternView.setOnPatternListener(mOnPatternListener);

    }

    private OnPatternListener mOnPatternListener = new OnPatternListener() {

        @Override
        public void onPatternStart() {

        }

        @Override
        public void onPatternCleared() {

        }

        @Override
        public void onPatternCellAdded(List<Cell> pattern) {

        }

        @Override
        public void onPatternDetected(List<Cell> pattern) {
            switch (mLockPatternType) {
                case TYPE_LOCK_PATTERN_OPEN:
                    openLockPattern(pattern);
                    break;
                case TYPE_LOCK_PATTERN_CLOSE:
                    closeLockPattern(pattern);
                    break;
                case TYPE_LOCK_PATTERN_VERIFY:
                    verifyLockPattern(pattern);
                    break;

                default:
                    break;
            }

        }

    };

    private void openLockPattern(final List<Cell> pattern) {
        if (isLeastPointCount(pattern.size())) {
            setPromptString(mContext.getResources().getString(R.string.lock_pattern_limit_prompt));
            mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            return;
        }
        if (isPatternDetectedOnce(onPatternDetectedTimes)) {
            if (checkPattern(pattern)) {
                setPromptString(mContext.getResources()
                        .getString(R.string.lock_pattern_new_pattern));
            } else {
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                setPromptString(mContext.getResources().getString(R.string.lock_pattern_error));
                return;
            }
        }
        onPatternDetectedTimes = onPatternDetectedTimes + 1;
        if (isPatternDetectedForConfirmation(onPatternDetectedTimes)) {
            // TODO success to set lock pattern
            String md5Pattern = HDBHashUtils
                    .getStringMD5(LockPatternUtils.patternToString(pattern));
            saveLockPattern(md5Pattern);
            setUnLockType(KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN);
            if (null != mLockPatternListener) {
                mLockPatternListener.onPatternDetected(TYPE_LOCK_PATTERN_OPEN, true);
            }
            return;
        }
        // success to detected once
        setPromptString(mContext.getResources().getString(R.string.lock_pattern_save_prompt));
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
                saveLockPattern(LockPatternUtils.patternToString(pattern));
                setPromptString(mContext.getResources().getString(
                        R.string.lock_pattern_confirmation_prompt));
                mLockPatternView.clearPattern();
            }
        }, THREAD_SLEPPING_DELAY);

    }

    private boolean isLeastPointCount(int size) {
        return size < LOCK_PATTERN_LEAST_POINT_COUNT;
    }

    private boolean isPatternDetectedOnce(int onPatternDetectedTimes) {
        return onPatternDetectedTimes == TIMES_DRAW_LOCK_PATTERN;
    }

    private boolean isPatternDetectedForConfirmation(int onPatternDetectedTimes) {
        return onPatternDetectedTimes == TIMES_DRAW_LOCK_PATTERN_AGAIN;
    }

    private boolean checkPattern(List<Cell> pattern) {
        String stored = getLockPaternString();
        String patternString = LockPatternUtils.patternToString(pattern);
        switch (mLockPatternType) {
            case TYPE_LOCK_PATTERN_OPEN:
                if (!stored.equals(null)) {
                    return stored.equals(patternString) ? true : false;
                }
                break;
            case TYPE_LOCK_PATTERN_CLOSE:
                if (!stored.equals(null)) {
                    return (stored.equals(patternString) ? true : false)
                            || (stored.equals(HDBHashUtils.getStringMD5(patternString)) ? true
                                    : false);
                }
                break;
            case TYPE_LOCK_PATTERN_VERIFY:
                if (!stored.equals(null)) {
                    return (stored.equals(patternString) ? true : false)
                            || (stored.equals(HDBHashUtils.getStringMD5(patternString)) ? true
                                    : false);
                }
                break;

            default:
                break;
        }
        return false;
    }

    private String getLockPaternString() {
        return PandoraConfig.newInstance(mContext).getLockPaternString();
    }

    protected void saveLockPattern(String pattern) {
        PandoraConfig.newInstance(mContext).saveLockPattern(pattern);
    }

    private void closeLockPattern(List<Cell> pattern) {
        if (checkPattern(pattern)) {
            clearSaveLockPattern();
            setUnLockType(KeyguardLockerManager.UNLOCKER_TYPE_NONE);
            if (null != mLockPatternListener) {
                mLockPatternListener.onPatternDetected(TYPE_LOCK_PATTERN_CLOSE, true);
            }
        } else {
            mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            setPromptString(mContext.getResources().getString(R.string.lock_pattern_error));
            HDBThreadUtils.postOnUiDelayed(new Runnable() {

                @Override
                public void run() {
                    mLockPatternView.clearPattern();
                }
            }, THREAD_SLEPPING_DELAY);
        }

    }

    private void clearSaveLockPattern() {
        PandoraConfig.newInstance(mContext).saveLockPattern("");
    }

    private void setUnLockType(int type) {
        PandoraConfig.newInstance(mContext).saveUnlockType(type);
    }

    private void verifyLockPattern(List<Cell> pattern) {
        if (checkPattern(pattern)) {
            verifySuccess();
        } else {
            verifyFails();
        }
    }

    private void verifySuccess() {
        if (null != mVerifyListener) {
            mVerifyListener.onVerifySuccess();
        }
    }

    private void verifyFails() {
        if (mErrorTimes == ERROT_MAX_TIMES) {
            // TODO
        }
        setPromptString(mContext.getResources().getString(R.string.lock_pattern_verify_fail));
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
                mLockPatternView.clearPattern();
            }
        }, THREAD_SLEPPING_DELAY);
        mErrorTimes++;
    }

    private void setPromptString(String prompt) {
        mPromptTextView.setText(prompt);
    }

    public TextView getPromptTextView() {
        // TODO Auto-generated method stub
        return mPromptTextView;
    }

    public LockPatternView getLockPatternView() {
        return mLockPatternView;

    }

}
