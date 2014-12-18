
package cn.zmdx.kaka.locker.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.security.KeyguardLockerManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils.ILoadBitmapCallback;

public class LockerPasswordActivity extends Activity implements OnClickListener {

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
        setContentView(R.layout.pandora_lock_password);
        getWindow().getAttributes().flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        initView();
        initTitleHeight();
        initWallpaper();
        initLockType();
    }

    private void initView() {
        mRootView = findViewById(R.id.pandora_lock_password_background);

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

    private void initTitleHeight() {
        int statusBarHeight = PandoraUtils.getStatusBarHeight(this);
        LinearLayout titleLayout = (LinearLayout) mRootView
                .findViewById(R.id.pandora_lock_password_title);
        titleLayout.setPadding(0, statusBarHeight, 0, 0);
    }

    @SuppressWarnings("deprecation")
    private void initWallpaper() {
        Theme theme = ThemeManager.getCurrentTheme();
        if (theme.isDefaultTheme()) {
            mRootView.setBackgroundResource(theme.getmBackgroundResId());
        } else {
            WallpaperUtils.loadBackgroundBitmap(this, theme.getFilePath(),
                    new ILoadBitmapCallback() {

                        @Override
                        public void imageLoaded(Bitmap bitmap, String filePath) {
                            mRootView.setBackgroundDrawable(new BitmapDrawable(getResources(),
                                    bitmap));
                        }
                    });
        }
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
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
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
