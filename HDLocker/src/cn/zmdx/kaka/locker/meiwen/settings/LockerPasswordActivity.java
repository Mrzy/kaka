
package cn.zmdx.kaka.locker.meiwen.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.security.KeyguardLockerManager;
import cn.zmdx.kaka.locker.meiwen.settings.config.PandoraConfig;

public class LockerPasswordActivity extends BaseActivity implements OnClickListener {

    public static final int REQUEST_LOCKER_PASSWORD_TYPE_CODE = 999;

    private View mRootView;

    private LinearLayout mNoneTypeLayout;

    private LinearLayout mNumberTypeLayout;

    private LinearLayout mPatternTypeLayout;

    private ImageView mNoneType;

    private ImageView mNumberType;

    private ImageView mPatternType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(Res.layout.pandora_lock_password);
        initView();
        initLockType();
    }

    private void initView() {
        mRootView = findViewById(Res.id.pandora_lock_password_background);
        LinearLayout titleLayout = (LinearLayout) mRootView
                .findViewById(Res.id.pandora_lock_password_title);
        initBackground(mRootView);
        initTitleHeight(titleLayout);
        mNoneTypeLayout = (LinearLayout) findViewById(Res.id.pandora_lock_type_none_layout);
        mNoneType = (ImageView) findViewById(Res.id.pandora_lock_type_none);
        mNoneTypeLayout.setOnClickListener(this);

        mPatternTypeLayout = (LinearLayout) findViewById(Res.id.pandora_lock_type_pattern_layout);
        mPatternType = (ImageView) findViewById(Res.id.pandora_lock_type_pattern);
        mPatternTypeLayout.setOnClickListener(this);

        mNumberTypeLayout = (LinearLayout) findViewById(Res.id.pandora_lock_type_number_layout);
        mNumberType = (ImageView) findViewById(Res.id.pandora_lock_type_number);
        mNumberTypeLayout.setOnClickListener(this);
    }

    private void initLockType() {
        int type = PandoraConfig.newInstance(this).getUnLockType();
        setTypeViewState(type);
    }

    private void setTypeViewState(int type) {
        switch (type) {
            case KeyguardLockerManager.UNLOCKER_TYPE_NONE:
                mNoneType.setImageResource(Res.drawable.pandora_lock_password_radio_checked);
                mPatternType.setImageResource(Res.drawable.pandora_lock_password_radio_normal);
                mNumberType.setImageResource(Res.drawable.pandora_lock_password_radio_normal);
                break;
            case KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN:
                mNoneType.setImageResource(Res.drawable.pandora_lock_password_radio_normal);
                mPatternType.setImageResource(Res.drawable.pandora_lock_password_radio_checked);
                mNumberType.setImageResource(Res.drawable.pandora_lock_password_radio_normal);
                break;
            case KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK:
                mNoneType.setImageResource(Res.drawable.pandora_lock_password_radio_normal);
                mPatternType.setImageResource(Res.drawable.pandora_lock_password_radio_normal);
                mNumberType.setImageResource(Res.drawable.pandora_lock_password_radio_checked);
                break;

            default:
                break;
        }
    }

    private void setLockTypePattern() {
        int currentType = PandoraConfig.newInstance(this).getUnLockType();
        if (currentType != KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN) {
            gotoLockerPasswordTypeActivity(KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN);
        }
    }

    private void setLockTypeNumber() {
        int currentType = PandoraConfig.newInstance(this).getUnLockType();
        if (currentType != KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK) {
            gotoLockerPasswordTypeActivity(KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK);
        }
    }

    private void setLockTypeNone() {
        int currentType = PandoraConfig.newInstance(this).getUnLockType();
        if (currentType != KeyguardLockerManager.UNLOCKER_TYPE_NONE) {
            gotoLockerPasswordTypeActivity(KeyguardLockerManager.UNLOCKER_TYPE_NONE);
        }
    }

    private void gotoLockerPasswordTypeActivity(int targetType) {
        Intent in = new Intent();
        in.setClass(this, LockerPasswordTypeActivity.class);
        in.putExtra("targetType", targetType);
        startActivityForResult(in, REQUEST_LOCKER_PASSWORD_TYPE_CODE);
        overridePendingTransition(Res.anim.umeng_fb_slide_in_from_right,
                Res.anim.umeng_fb_slide_out_from_left);
    }

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
        if (view == mNoneTypeLayout) {
            setLockTypeNone();
            setTypeViewState(KeyguardLockerManager.UNLOCKER_TYPE_NONE);
        } else if (view == mPatternTypeLayout) {
            setLockTypePattern();
            setTypeViewState(KeyguardLockerManager.UNLOCKER_TYPE_LOCK_PATTERN);
        } else if (view == mNumberTypeLayout) {
            setLockTypeNumber();
            setTypeViewState(KeyguardLockerManager.UNLOCKER_TYPE_NUMBER_LOCK);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(Res.anim.umeng_fb_slide_in_from_left,
                Res.anim.umeng_fb_slide_out_from_right);
    }

}