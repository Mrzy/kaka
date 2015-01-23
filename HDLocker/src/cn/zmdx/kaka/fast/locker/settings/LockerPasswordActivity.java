
package cn.zmdx.kaka.fast.locker.settings;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.security.KeyguardLockerManager;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.widget.PandoraLockPatternView;
import cn.zmdx.kaka.fast.locker.widget.PandoraLockPatternView.ILockPatternListener;
import cn.zmdx.kaka.fast.locker.widget.PandoraNumberLockView;

import com.afollestad.materialdialogs.MaterialDialog;

public class LockerPasswordActivity extends BaseActivity implements OnClickListener {

    public static final int REQUEST_LOCKER_PASSWORD_TYPE_CODE = 999;

    private LinearLayout mNoneTypeLayout;

    private LinearLayout mNumberTypeLayout;

    private LinearLayout mPatternTypeLayout;

    private ImageView mNoneType;

    private ImageView mNumberType;

    private ImageView mPatternType;

    private MaterialDialog mNumberLockDialog;

    private MaterialDialog mLockPatternDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_lock_password);
        initView();
        initLockType();
    }

    private void initView() {
        mNoneTypeLayout = (LinearLayout) findViewById(R.id.pandora_lock_type_none_layout);
        mNoneType = (ImageView) findViewById(R.id.pandora_lock_type_none);
        mNoneTypeLayout.setOnClickListener(this);

        mPatternTypeLayout = (LinearLayout) findViewById(R.id.pandora_lock_type_pattern_layout);
        mPatternType = (ImageView) findViewById(R.id.pandora_lock_type_pattern);
        mPatternTypeLayout.setOnClickListener(this);

        mNumberTypeLayout = (LinearLayout) findViewById(R.id.pandora_lock_type_number_layout);
        mNumberType = (ImageView) findViewById(R.id.pandora_lock_type_number);
        mNumberTypeLayout.setOnClickListener(this);
    }

    private void initLockType() {
        int type = PandoraConfig.newInstance(this).getUnLockType();
        setTypeViewState(type);
    }

    private void setTypeViewState(int type) {
        switch (type) {
            case KeyguardLockerManager.UNLOCKER_TYPE_NONE:
                mNoneType.setImageResource(R.drawable.pandora_lock_password_radio_checked);
                mPatternType.setImageResource(R.drawable.pandora_lock_password_radio_normal);
                mNumberType.setImageResource(R.drawable.pandora_lock_password_radio_normal);
                break;
            case KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN:
                mNoneType.setImageResource(R.drawable.pandora_lock_password_radio_normal);
                mPatternType.setImageResource(R.drawable.pandora_lock_password_radio_checked);
                mNumberType.setImageResource(R.drawable.pandora_lock_password_radio_normal);
                break;
            case KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK:
                mNoneType.setImageResource(R.drawable.pandora_lock_password_radio_normal);
                mPatternType.setImageResource(R.drawable.pandora_lock_password_radio_normal);
                mNumberType.setImageResource(R.drawable.pandora_lock_password_radio_checked);
                break;

            default:
                break;
        }
    }

    private void setLockTypePattern() {
        int curType = PandoraConfig.newInstance(this).getUnLockType();
        if (curType != KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
            gotoLockerPasswordTypeActivity(curType,
                    KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN);
        }
    }

    private void setLockTypeNumber() {
        int curType = PandoraConfig.newInstance(this).getUnLockType();
        if (curType != KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK) {
            gotoLockerPasswordTypeActivity(curType, KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK);
        }
    }

    private void setLockTypeNone() {
        int curType = PandoraConfig.newInstance(this).getUnLockType();
        if (curType != KeyguardLockerManager.UNLOCKER_TYPE_NONE) {
            gotoLockerPasswordTypeActivity(curType, KeyguardLockerManager.UNLOCKER_TYPE_NONE);
        }
    }

    private void gotoLockerPasswordTypeActivity(int curType, int targetType) {
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
        PandoraLockPatternView mLockPatternView = new PandoraLockPatternView(this,
                PandoraLockPatternView.TYPE_LOCK_PATTERN_VERIFY,
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
                });
        createLockPatternDialog(mLockPatternView);
    }

    private void setLockPatternViewVisibleWithLockPatternListener(int type) {
        PandoraLockPatternView mLockPatternView = new PandoraLockPatternView(this, type,
                new ILockPatternListener() {

                    @Override
                    public void onPatternDetected(int type, boolean success) {
                        // TODO
                        dismissLockPatternDialog();
                    }
                });
        createLockPatternDialog(mLockPatternView);
    }

    private void setNumberLockViewVisibleWithVerifyListener(final boolean isNeedLockPatternView) {
        PandoraNumberLockView mNumberLockView = new PandoraNumberLockView(this,
                PandoraNumberLockView.LOCK_NUMBER_TYPE_VERIFY,
                new PandoraNumberLockView.IVerifyListener() {

                    @Override
                    public void onVerifySuccess() {
                        // TODO
                        if (isNeedLockPatternView) {
                            setLockPatternViewVisibleWithLockPatternListener(PandoraLockPatternView.TYPE_LOCK_PATTERN_OPEN);
                        } else {
                            dismissNumberLockDialog();
                        }
                    }

                });
        createNumberLockDialog(mNumberLockView);
    }

    private void setNumberLockViewVisibleWithNumberLockListener(int type) {
        PandoraNumberLockView mNumberLockView = new PandoraNumberLockView(this, type,
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
        mLockPatternDialog = new MaterialDialog.Builder(this).customView(customView, true)
                .dismissListener(mOnDismissListener).build();
        mLockPatternDialog.show();
    }

    private void createNumberLockDialog(View customView) {
        dismissLockPatternDialog();
        mNumberLockDialog = new MaterialDialog.Builder(this).customView(customView, true)
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
        initLockType();
    }

    private OnDismissListener mOnDismissListener = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            reset();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        initLockType();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pandora_lock_type_none_layout:
                setLockTypeNone();
                setTypeViewState(KeyguardLockerManager.UNLOCKER_TYPE_NONE);
                break;
            case R.id.pandora_lock_type_pattern_layout:
                setLockTypePattern();
                setTypeViewState(KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN);
                break;
            case R.id.pandora_lock_type_number_layout:
                setLockTypeNumber();
                setTypeViewState(KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }

}
