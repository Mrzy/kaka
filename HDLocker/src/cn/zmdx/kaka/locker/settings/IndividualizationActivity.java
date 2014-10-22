
package cn.zmdx.kaka.locker.settings;

import java.io.File;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.custom.wallpaper.CustomWallpaperManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.widget.SwitchButton;

import com.umeng.analytics.MobclickAgent;

public class IndividualizationActivity extends Activity implements OnClickListener,
        OnCheckedChangeListener {

    private View mRootView;

    private SwitchButton mNoticeSButton;

    private LinearLayout mLockerDefaultImage;

    private ImageView mLockerDefaultImageThumb;

    public static String LOCK_DEFAULT_SDCARD_LOCATION = Environment.getExternalStorageDirectory()
            .getPath() + "/Pandora/lockDefault/";

    private static final int MSG_SAVE_LOCK_DEFAULT = 11;

    private static final int MSG_SAVE_LOCK_DEFAULT_DELAY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_individualization);
        initView();
        initBackground();
        initLockDefaultBitmap();
    }

    private void initView() {
        mRootView = findViewById(R.id.individualization_background);
        mNoticeSButton = (SwitchButton) findViewById(R.id.individualization_notice_switch_button);
        mNoticeSButton.setOnCheckedChangeListener(this);
        mNoticeSButton.setChecked(isNeedNotice());
        mLockerDefaultImage = (LinearLayout) findViewById(R.id.individualization_locker_default_image);
        mLockerDefaultImage.setOnClickListener(this);
        mLockerDefaultImageThumb = (ImageView) findViewById(R.id.individualization_locker_default_thumb_image);

        LayoutParams params = mLockerDefaultImageThumb.getLayoutParams();
        int height = (int) getResources().getDimension(R.dimen.setting_item_height);
        int width = (int) getResources().getDimension(R.dimen.setting_item_height);
        params.width = width;
        params.height = height;
        mLockerDefaultImageThumb.setLayoutParams(params);

    }

    private void initBackground() {
        int themeId = PandoraConfig.newInstance(this).getCurrentThemeId();
        if (themeId == -1) {
            setCustomBackground();
        } else {
            setBackground(themeId);
        }

    }

    @SuppressWarnings("deprecation")
    private void setCustomBackground() {
        String fileName = PandoraConfig.newInstance(this).getCustomWallpaperFileName();
        String path = CustomWallpaperManager.getCustomWallpaperFilePath(fileName);
        Bitmap bitmap = PandoraUtils.getBitmap(path);
        if (null == bitmap) {
            mRootView.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.setting_background_blue));
        } else {
            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
            mRootView.setBackgroundDrawable(drawable);
        }
    }

    protected void setBackground(int themeId) {
        Theme theme = ThemeManager.getThemeById(themeId);
        mRootView.setBackgroundResource(theme.getmBackgroundResId());
    }

    private void initLockDefaultBitmap() {
        BitmapDrawable drawable = PandoraUtils.getLockDefaultBitmap(this);
        if (drawable == null) {
            mLockerDefaultImageThumb.setImageResource(R.drawable.ic_launcher);
        } else {
            mLockerDefaultImageThumb.setImageDrawable(drawable);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.individualization_notice_switch_button:
                if (isChecked) {
                    openNoticeBar();
                } else {
                    closeNoticeBar();
                }
                break;

            default:
                break;
        }

    }

    private void closeNoticeBar() {
        PandoraConfig.newInstance(this).saveNeedNotice(false);
    }

    private void openNoticeBar() {
        PandoraConfig.newInstance(this).saveNeedNotice(true);
    }

    private boolean isNeedNotice() {
        return PandoraConfig.newInstance(this).isNeedNotice();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.individualization_locker_default_image:
                PandoraUtils.gotoGalleryActivity(IndividualizationActivity.this,
                        PandoraUtils.REQUEST_CODE_GALLERY);
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PandoraUtils.REQUEST_CODE_CROP_IMAGE:
                String fileName = PandoraUtils.getRandomString();
                setBitmap();
                saveWallpaperFile(fileName);
                break;
            case PandoraUtils.REQUEST_CODE_GALLERY: {
                gotoCropActivity(data.getData());
                break;
            }
            default: {
                break;
            }
        }
    }

    private void gotoCropActivity(Uri uri) {
        Intent intent = new Intent();
        intent.setClass(this, CropImageActivity.class);
        intent.setData(uri);
        int mAspectRatioX = 0;
        int mAspectRatioY = 0;
        float rate = LockScreenManager.getInstance().getBoxWidthHeightRate();
        if (rate >= 1) {
            mAspectRatioX = 100;
            mAspectRatioY = (int) (mAspectRatioX / rate);
        }
        if (rate <= 1) {
            mAspectRatioY = 100;
            mAspectRatioX = (int) (mAspectRatioY * rate);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(CropImageActivity.KEY_BUNDLE_ASPECTRATIO_X, mAspectRatioX);
        bundle.putInt(CropImageActivity.KEY_BUNDLE_ASPECTRATIO_Y, mAspectRatioY);
        bundle.putBoolean(CropImageActivity.KEY_BUNDLE_IS_WALLPAPER, false);
        intent.putExtras(bundle);
        startActivityForResult(intent, PandoraUtils.REQUEST_CODE_CROP_IMAGE);
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private void setBitmap() {
        mLockerDefaultImageThumb.setImageBitmap(PandoraUtils.sLockDefaultThumbBitmap);
    }

    private void saveWallpaperFile(final String fileName) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                PandoraUtils.deleteFile(new File(LOCK_DEFAULT_SDCARD_LOCATION));
                PandoraUtils.saveBitmap(PandoraUtils.sLockDefaultThumbBitmap,
                        LOCK_DEFAULT_SDCARD_LOCATION, fileName);
                saveLockDefaultSP(fileName);
            }
        }).start();
    }

    private void saveLockDefaultSP(String fileName) {
        if (mHandler.hasMessages(MSG_SAVE_LOCK_DEFAULT)) {
            mHandler.removeMessages(MSG_SAVE_LOCK_DEFAULT);
        }
        Message message = Message.obtain();
        message.what = MSG_SAVE_LOCK_DEFAULT;
        message.obj = fileName;
        mHandler.sendMessageDelayed(message, MSG_SAVE_LOCK_DEFAULT_DELAY);
    }

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<Activity> mActicity;

        public MyHandler(Activity activity) {
            mActicity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActicity.get();
            switch (msg.what) {
                case MSG_SAVE_LOCK_DEFAULT:
                    String fileName = (String) msg.obj;
                    ((IndividualizationActivity) activity).saveLockDefaultFileName(fileName);
                    break;

            }
            super.handleMessage(msg);
        }
    }

    public void saveLockDefaultFileName(String fileName) {
        PandoraConfig.newInstance(this).saveLockDefaultFileName(fileName);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("IndividualizationActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("IndividualizationActivity"); // 保证 onPageEnd
                                                              // 在onPause
        // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
