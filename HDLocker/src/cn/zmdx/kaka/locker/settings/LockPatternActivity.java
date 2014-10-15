
package cn.zmdx.kaka.locker.settings;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.LockPatternUtils;
import cn.zmdx.kaka.locker.widget.LockPatternView;
import cn.zmdx.kaka.locker.widget.LockPatternView.Cell;
import cn.zmdx.kaka.locker.widget.LockPatternView.DisplayMode;
import cn.zmdx.kaka.locker.widget.LockPatternView.OnPatternListener;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.umeng.analytics.MobclickAgent;

public class LockPatternActivity extends Activity implements OnClickListener, OnPatternListener {

    private Context mContext;

    public static final int LOCK_PATTERN_TYPE_CLOSE = 0;

    public static final int LOCK_PATTERN_TYPE_OPEN = 1;

    public static final int LOCK_PATTERN_TYPE_VERIFY = 2;

    private int mLockPatternType = 1;

    private TextView mGusturePrompt;

    private TypefaceTextView mResetBtn;

    private TypefaceTextView mDetermineBtn;

    private LockPatternView mLockPatternView;

    private static final int TIMES_DRAW_GUSTURE = 1;

    private static final int TIMES_DRAW_GUSTURE_AGAIN = 2;

    private static final int GUSTURE_LEAST_POINT_COUNT = 4;

    private static final int THREAD_SLEPPING_DELAY = 300;

    private static final int ERROT_MAX_TIMES = 5;

    private PandoraConfig mPandoraConfig;

    private int onPatternDetectedTimes = 0;

    private int mErrorTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.gusture_view);
        mLockPatternType = getIntent().getBundleExtra("bundle").getInt("type");
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mPandoraConfig = PandoraConfig.newInstance(mContext);
        initView();
    }

    private void initView() {
        mGusturePrompt = (TextView) findViewById(R.id.gusture_prompt);
        mResetBtn = (TypefaceTextView) findViewById(R.id.gusture_reset);
        mResetBtn.setOnClickListener(this);
        mDetermineBtn = (TypefaceTextView) findViewById(R.id.gusture_sure);
        mDetermineBtn.setOnClickListener(this);
        mLockPatternView = (LockPatternView) findViewById(R.id.gusture);
        mLockPatternView.setOnPatternListener(this);
    }

    @Override
    public void onPatternStart() {
        setGuseturePromptString(mContext.getResources().getString(R.string.gusture_complete));
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
            case LOCK_PATTERN_TYPE_CLOSE:
                closeGustureLock(pattern);
                break;
            case LOCK_PATTERN_TYPE_OPEN:
                openGustureLock(pattern);
                break;
            case LOCK_PATTERN_TYPE_VERIFY:
                verifyGustureLock(pattern);
                break;

            default:
                break;
        }

    }

    private void closeGustureLock(List<Cell> pattern) {
        if (checkPattern(pattern)) {
            clearSaveLockPattern();
            setUnLockType(PandoraConfig.UNLOCKER_TYPE_DEFAULT);
            finishWithNoResult();
        } else {
            mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            setGuseturePromptString(mContext.getResources().getString(R.string.gusture_error));
            HDBThreadUtils.postOnUiDelayed(new Runnable() {

                @Override
                public void run() {
                    mLockPatternView.clearPattern();
                }
            }, THREAD_SLEPPING_DELAY);
        }

    }

    private void openGustureLock(final List<Cell> pattern) {
        if (isLeastPointCount(pattern.size())) {
            setGuseturePromptString(mContext.getResources()
                    .getString(R.string.gusture_limit_prompt));
            mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            return;
        }
        if (isPatternDetectedOnce(onPatternDetectedTimes)) {
            if (checkPattern(pattern)) {
                setGuseturePromptString(mContext.getResources().getString(
                        R.string.gusture_new_pattern));
            } else {
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                setGuseturePromptString(mContext.getResources().getString(R.string.gusture_error));
                return;
            }
        }
        onPatternDetectedTimes = onPatternDetectedTimes + 1;
        if (isPatternDetectedForConfirmation(onPatternDetectedTimes)) {
            setDetermineBtnClickable(true);
            setLockPatternViewTouchState(true);
            return;
        }
        // success to detected once
        setGuseturePromptString(mContext.getResources().getString(R.string.gusture_save_prompt));
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
                setBtnVisibility(View.VISIBLE);
                setDetermineBtnClickable(false);
                saveLockPattern(LockPatternUtils.patternToString(pattern));
                setGuseturePromptString(mContext.getResources().getString(
                        R.string.gusture_confirmation_prompt));
                mLockPatternView.clearPattern();
            }
        }, THREAD_SLEPPING_DELAY);

    }

    protected void setBtnVisibility(int visible) {
        mResetBtn.setVisibility(visible);
        mDetermineBtn.setVisibility(visible);

    }

    private void setDetermineBtnClickable(boolean isClickable) {
        mDetermineBtn.setClickable(isClickable);
        if (isClickable) {
            mDetermineBtn.setTextColor(getResources().getColor(R.color.setting_textview_color));
        } else {
            mDetermineBtn.setTextColor(getResources()
                    .getColor(R.color.gusture_button_unpress_color));
        }
    }

    private void setLockPatternViewTouchState(final boolean isTouched) {
        mLockPatternView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isTouched;
            }
        });
    }

    private void verifyGustureLock(List<Cell> pattern) {
        if (checkPattern(pattern)) {
            verifySuccess();
            finishWithNoResult();
        } else {
            verifyFails();
        }
    }

    private void verifyFails() {
        if (mErrorTimes == ERROT_MAX_TIMES) {
            // TODO
        }
        setGuseturePromptString(mContext.getResources().getString(R.string.gusture_verify_fail));
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
                mLockPatternView.clearPattern();
            }
        }, THREAD_SLEPPING_DELAY);
        mErrorTimes++;
    }

    private void verifySuccess() {

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

    private boolean checkPattern(List<Cell> pattern) {
        String stored = getLockPaternString();
        if (!stored.equals(null)) {
            return stored.equals(LockPatternUtils.patternToString(pattern)) ? true : false;
        }
        return false;
    }

    private String getLockPaternString() {
        return mPandoraConfig.getLockPaternString();
    }

    private void clearSaveLockPattern() {
        mPandoraConfig.saveLockPattern("");
    }

    protected void saveLockPattern(String pattern) {
        mPandoraConfig.saveLockPattern(pattern);
    }

    private void setUnLockType(int type) {
        mPandoraConfig.saveUnlockType(type);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gusture_reset:
                setLockPatternViewTouchState(false);
                mGusturePrompt.setText(mContext.getResources().getString(R.string.gustrue_prompt));
                setBtnVisibility(View.INVISIBLE);
                onPatternDetectedTimes = 0;
                mLockPatternView.clearPattern();
                clearSaveLockPattern();
                break;
            case R.id.gusture_sure:
                setUnLockType(PandoraConfig.UNLOCKER_TYPE_GUSTURE);
                finishWithNoResult();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent();
        in.putExtra("type", mLockPatternType);
        setResult(MainSettingsFragment.GUSTURE_REQUEST_CODE_FAIL, in);
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }

    private void finishWithNoResult() {
        Intent in = new Intent();
        setResult(MainSettingsFragment.GUSTURE_REQUEST_CODE_SUCCESS, in);
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LockPatternActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LockPatternActivity"); // 保证 onPageEnd 在onPause
        // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
