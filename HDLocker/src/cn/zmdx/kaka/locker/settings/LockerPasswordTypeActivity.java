
package cn.zmdx.kaka.locker.settings;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.security.KeyguardLockerManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.widget.PandoraLockPatternView;
import cn.zmdx.kaka.locker.widget.PandoraLockPatternView.ILockPatternListener;
import cn.zmdx.kaka.locker.widget.PandoraNumberLockView;

public class LockerPasswordTypeActivity extends BaseActivity {

    private LinearLayout mLockPatternLayout;

    private LinearLayout mNumberLockLayout;

    private int targetType;

    private int curType;

    private PandoraLockPatternView mLockPatternView;

    private PandoraNumberLockView mNumberLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_locker_password_type);
//        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        targetType = getIntent()
                .getIntExtra("targetType", KeyguardLockerManager.UNLOCKER_TYPE_NONE);
        curType = PandoraConfig.newInstance(this).getUnLockType();
        initView();
    }

    private void initView() {
        mLockPatternLayout = (LinearLayout) findViewById(R.id.pandora_lock_pattern_layout);
        mNumberLockLayout = (LinearLayout) findViewById(R.id.pandora_number_lock_layout);
        switch (curType) {
            case KeyguardLockerManager.UNLOCKER_TYPE_NONE:
                if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
                    setLockPatternViewVisibleWithLockPatternListener(PandoraLockPatternView.TYPE_LOCK_PATTERN_OPEN);
                } else if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK) {
                    setNumberLockViewVisibleWithNumberLockListener(PandoraNumberLockView.LOCK_NUMBER_TYPE_OPEN);
                }
                break;
            case KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN:
                if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_NONE) {
                    setLockPatternViewVisibleWithLockPatternListener(PandoraLockPatternView.TYPE_LOCK_PATTERN_CLOSE);
                } else if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK) {
                    setLockPatternViewVisibleWithVerifyListener(true);
                }
                break;
            case KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK:
                if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_NONE) {
                    setNumberLockViewVisibleWithNumberLockListener(PandoraNumberLockView.LOCK_NUMBER_TYPE_CLOSE);
                } else if (targetType == KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
                    setNumberLockViewVisibleWithVerifyListener(true);
                }
                break;

            default:
                break;
        }

    }

    private void setLockPatternViewVisibleWithVerifyListener(final boolean isNeedNumberLockView) {
        mLockPatternView = new PandoraLockPatternView(this,
                PandoraLockPatternView.TYPE_LOCK_PATTERN_VERIFY,
                new PandoraLockPatternView.IVerifyListener() {

                    @Override
                    public void onVerifySuccess() {
                        // TODO
                        if (isNeedNumberLockView) {
                            setNumberLockViewVisibleWithNumberLockListener(PandoraNumberLockView.LOCK_NUMBER_TYPE_OPEN);
                        } else {
                            finishWithResult();
                        }
                    }
                });
        mLockPatternView.setGravity(Gravity.CENTER);
        if (mLockPatternLayout.getChildCount() != 0) {
            mLockPatternLayout.removeAllViews();
        }
        mLockPatternLayout.addView(mLockPatternView);
        setLockPatternLayoutVisible();
    }

    private void setLockPatternViewVisibleWithLockPatternListener(int type) {
        mLockPatternView = new PandoraLockPatternView(this, type, new ILockPatternListener() {

            @Override
            public void onPatternDetected(int type, boolean success) {
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

    private void setNumberLockViewVisibleWithVerifyListener(final boolean isNeedLockPatternView) {
        mNumberLockView = new PandoraNumberLockView(this,
                PandoraNumberLockView.LOCK_NUMBER_TYPE_VERIFY,
                new PandoraNumberLockView.IVerifyListener() {

                    @Override
                    public void onVerifySuccess() {
                        // TODO
                        if (isNeedLockPatternView) {
                            setLockPatternViewVisibleWithLockPatternListener(PandoraLockPatternView.TYPE_LOCK_PATTERN_OPEN);
                        } else {
                            finishWithResult();
                        }
                    }

                });
        mNumberLockView.setGravity(Gravity.CENTER);
        if (mNumberLockLayout.getChildCount() != 0) {
            mNumberLockLayout.removeAllViews();
        }
        mNumberLockLayout.addView(mNumberLockView);
        setNumberLockLayoutVisible();
    }

    private void setNumberLockViewVisibleWithNumberLockListener(int type) {
        mNumberLockView = new PandoraNumberLockView(LockerPasswordTypeActivity.this, type,
                new PandoraNumberLockView.INumberLockListener() {

                    @Override
                    public void onSetNumberLock(int type, boolean success) {
                        // TODO
                        finishWithResult();
                    }
                });
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
    public void onBackPressed() {
        finishWithResult();
    }
}
