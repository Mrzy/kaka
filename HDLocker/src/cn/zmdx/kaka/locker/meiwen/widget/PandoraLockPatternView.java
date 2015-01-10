
package cn.zmdx.kaka.locker.meiwen.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.security.KeyguardLockerManager;
import cn.zmdx.kaka.locker.meiwen.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.meiwen.utils.HDBHashUtils;
import cn.zmdx.kaka.locker.meiwen.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.meiwen.utils.LockPatternUtils;
import cn.zmdx.kaka.locker.meiwen.widget.LockPatternView.Cell;
import cn.zmdx.kaka.locker.meiwen.widget.LockPatternView.DisplayMode;
import cn.zmdx.kaka.locker.meiwen.widget.LockPatternView.OnPatternListener;

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
        mRootView = LayoutInflater.from(mContext).inflate(Res.layout.pandora_lock_pattern, null);
        addView(mRootView);

        mPromptTextView = (TextView) findViewById(Res.id.pandora_lock_pattern_prompt);
        mLockPatternView = (LockPatternView) findViewById(Res.id.pandora_lock_pattern);
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
            setPromptString(mContext.getResources().getString(Res.string.lock_pattern_limit_prompt));
            mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            return;
        }
        if (isPatternDetectedOnce(onPatternDetectedTimes)) {
            if (checkPattern(pattern)) {
                setPromptString(mContext.getResources()
                        .getString(Res.string.lock_pattern_new_pattern));
            } else {
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                setPromptString(mContext.getResources().getString(Res.string.lock_pattern_error));
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
        setPromptString(mContext.getResources().getString(Res.string.lock_pattern_save_prompt));
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
                saveLockPattern(LockPatternUtils.patternToString(pattern));
                setPromptString(mContext.getResources().getString(
                        Res.string.lock_pattern_confirmation_prompt));
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
            setPromptString(mContext.getResources().getString(Res.string.lock_pattern_error));
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
        setPromptString(mContext.getResources().getString(Res.string.lock_pattern_verify_fail));
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
    public LockPatternView getLockPatternView(){
        return mLockPatternView;
        
    }

}
