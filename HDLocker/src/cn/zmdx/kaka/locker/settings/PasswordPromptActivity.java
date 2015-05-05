
package cn.zmdx.kaka.locker.settings;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.pattern.LockPatternManager;
import cn.zmdx.kaka.locker.security.KeyguardLockerManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.widget.PandoraLockPatternView;
import cn.zmdx.kaka.locker.widget.PandoraLockPatternView.ILockPatternListener;
import cn.zmdx.kaka.locker.widget.PandoraNumberLockView;

public class PasswordPromptActivity extends BaseActivity {

    private LinearLayout mLockPatternLayout;

    private LinearLayout mNumberLockLayout;

    private int targetType;

    private int curType;

    private int mLockPatternStyle;

    private PandoraLockPatternView mLockPatternView;

    private PandoraNumberLockView mNumberLockView;

    public static final int REQUEST_LOCKER_PASSWORD_TYPE_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.password_prompt_activity);
        targetType = getIntent()
                .getIntExtra("targetType", KeyguardLockerManager.UNLOCKER_TYPE_NONE);
        curType = PandoraConfig.newInstance(this).getUnLockType();
        mLockPatternStyle = getIntent().getIntExtra("lockPatternStyle",
                LockPatternManager.LOCK_PATTERN_STYLE_PURE);
        initView();
    }

    private void initView() {
        mLockPatternLayout = (LinearLayout) findViewById(R.id.pandora_lock_pattern_layout);
        mNumberLockLayout = (LinearLayout) findViewById(R.id.pandora_number_lock_layout);
        switch (curType) {
            case KeyguardLockerManager.UNLOCKER_TYPE_NONE:
                if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
                    setLockPatternViewVisibleWithLockPatternListener(
                            PandoraLockPatternView.TYPE_LOCK_PATTERN_OPEN, mLockPatternStyle);
                } else if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK) {
                    setNumberLockViewVisibleWithNumberLockListener(PandoraNumberLockView.LOCK_NUMBER_TYPE_OPEN);
                }
                break;
            case KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN:
                if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_NONE) {
                    setLockPatternViewVisibleWithLockPatternListener(
                            PandoraLockPatternView.TYPE_LOCK_PATTERN_CLOSE, mLockPatternStyle);
                } else if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK) {
                    setLockPatternViewVisibleWithVerifyListener(true, mLockPatternStyle,
                            PandoraLockPatternView.TYPE_LOCK_PATTERN_VERIFY);
                } else {
                    setLockPatternViewVisibleWithVerifyListener(false, mLockPatternStyle,
                            PandoraLockPatternView.TYPE_LOCK_PATTERN_RESET);
                }
                break;
            case KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK:
                if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_NONE) {
                    setNumberLockViewVisibleWithNumberLockListener(PandoraNumberLockView.LOCK_NUMBER_TYPE_CLOSE);
                } else if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
                    setNumberLockViewVisibleWithVerifyListener(true, mLockPatternStyle,
                            PandoraNumberLockView.LOCK_NUMBER_TYPE_VERIFY);
                } else {
                    setNumberLockViewVisibleWithVerifyListener(false, mLockPatternStyle,
                            PandoraNumberLockView.LOCK_NUMBER_TYPE_RESET);
                }
                break;

            default:
                break;
        }

    }

    private void setLockPatternViewVisibleWithVerifyListener(final boolean isNeedNumberLockView,
            int lockPatternStyle, int lockPatternType) {
        mLockPatternView = new PandoraLockPatternView(this, lockPatternType, lockPatternStyle,
                new PandoraLockPatternView.ILockPatternListener() {

                    @Override
                    public void onComplete(int type, boolean success) {
                        // TODO
                        if (isNeedNumberLockView) {
                            setNumberLockViewVisibleWithNumberLockListener(PandoraNumberLockView.LOCK_NUMBER_TYPE_OPEN);
                        } else {
                            finishWithResult();
                        }
                    }
                }, false);
        mLockPatternView.setGravity(Gravity.CENTER);
        if (mLockPatternLayout.getChildCount() != 0) {
            mLockPatternLayout.removeAllViews();
        }
        mLockPatternLayout.addView(mLockPatternView);
        setLockPatternLayoutVisible();
    }

    private void setLockPatternViewVisibleWithLockPatternListener(int type, int lockPatternStyle) {
        mLockPatternView = new PandoraLockPatternView(this, type, lockPatternStyle,
                new ILockPatternListener() {

                    @Override
                    public void onComplete(int type, boolean success) {
                        // TODO
                        finishWithResult();
                    }
                });

        mLockPatternView.setGravity(Gravity.CENTER);
        if (mLockPatternLayout.getChildCount() != 0) {
            mLockPatternLayout.removeAllViews();
        }
        mLockPatternLayout.addView(mLockPatternView);
        setLockPatternLayoutVisible();
    }

    private void setLockPatternLayoutVisible() {
        mLockPatternLayout.setVisibility(View.VISIBLE);
        mNumberLockLayout.setVisibility(View.GONE);
    }

    private void setNumberLockViewVisibleWithVerifyListener(final boolean isNeedLockPatternView,
            final int lockPatternStyle, int numberLockType) {
        mNumberLockView = new PandoraNumberLockView(this, numberLockType,
                new PandoraNumberLockView.INumberLockListener() {

                    @Override
                    public void onComplete(int type, boolean success) {
                        // TODO
                        if (isNeedLockPatternView) {
                            setLockPatternViewVisibleWithLockPatternListener(
                                    PandoraLockPatternView.TYPE_LOCK_PATTERN_OPEN, lockPatternStyle);
                        } else {
                            finishWithResult();
                        }
                    }

                }, false);
        mNumberLockView.setGravity(Gravity.CENTER);
        if (mNumberLockLayout.getChildCount() != 0) {
            mNumberLockLayout.removeAllViews();
        }
        mNumberLockLayout.addView(mNumberLockView);
        setNumberLockLayoutVisible();
    }

    private void setNumberLockViewVisibleWithNumberLockListener(int type) {
        mNumberLockView = new PandoraNumberLockView(this, type,
                new PandoraNumberLockView.INumberLockListener() {

                    @Override
                    public void onComplete(int type, boolean success) {
                        // TODO
                        finishWithResult();
                    }
                }, false);
        mNumberLockView.setGravity(Gravity.CENTER);
        if (mNumberLockLayout.getChildCount() != 0) {
            mNumberLockLayout.removeAllViews();
        }
        mNumberLockLayout.addView(mNumberLockView);
        setNumberLockLayoutVisible();
    }

    private void setNumberLockLayoutVisible() {
        mLockPatternLayout.setVisibility(View.GONE);
        mNumberLockLayout.setVisibility(View.VISIBLE);
    }

    private void finishWithResult() {
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finishWithResult();
        return false;
    }

    @Override
    public void onBackPressed() {
        finishWithResult();
    }
}
