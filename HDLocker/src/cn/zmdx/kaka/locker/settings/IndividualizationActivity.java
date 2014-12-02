
package cn.zmdx.kaka.locker.settings;

import java.io.File;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.FileHelper;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils.ILoadBitmapCallback;
import cn.zmdx.kaka.locker.widget.BaseEditText;
import cn.zmdx.kaka.locker.widget.SwitchButton;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.umeng.analytics.MobclickAgent;

public class IndividualizationActivity extends Activity implements OnClickListener,
        OnCheckedChangeListener {

    private View mRootView;

    private SwitchButton mNoticeSButton;

    private SwitchButton mNoticeMobileNetworkSButton;

    private LinearLayout mLockerDefaultImage;

    private ImageView mLockerDefaultImageThumb;

    public static String LOCK_DEFAULT_SDCARD_LOCATION = Environment.getExternalStorageDirectory()
            .getPath() + "/.Pandora/lockDefault/";

    private static final int MSG_SAVE_LOCK_DEFAULT = 11;

    public static boolean sIsDirect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_individualization);
        initView();
        initWallpaper();
        initLockDefaultBitmap();
    }

    private void initView() {
        mRootView = findViewById(R.id.individualization_background);
        mNoticeSButton = (SwitchButton) findViewById(R.id.individualization_notice_switch_button);
        mNoticeSButton.setOnCheckedChangeListener(this);
        mNoticeSButton.setChecked(isNeedNotice());
        mNoticeMobileNetworkSButton = (SwitchButton) findViewById(R.id.individualization_3G_4G_switch_button);
        mNoticeMobileNetworkSButton.setOnCheckedChangeListener(this);
        mNoticeMobileNetworkSButton.setChecked(isMobileNetwork());
        mLockerDefaultImage = (LinearLayout) findViewById(R.id.individualization_locker_default_image);
        mLockerDefaultImage.setOnClickListener(this);
        mLockerDefaultImageThumb = (ImageView) findViewById(R.id.individualization_locker_default_thumb_image);

        LayoutParams params = mLockerDefaultImageThumb.getLayoutParams();
        int height = (int) getResources().getDimension(R.dimen.individualization_image_height);
        int width = (int) getResources().getDimension(R.dimen.individualization_image_height);
        params.width = width;
        params.height = height;
        mLockerDefaultImageThumb.setLayoutParams(params);

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
            case R.id.individualization_3G_4G_switch_button:
                if (isChecked) {
                    openMobileNetwork();
                } else {
                    closeMobileNetwork();
                }
                break;
            default:
                break;
        }

    }

    private void showInputDialog() {
        final Dialog dialog = new Dialog(this, R.style.pandora_dialog_style);
        dialog.getWindow().setContentView(R.layout.pandora_dialog);
        dialog.show();
        dialog.setCancelable(false);

        TypefaceTextView mTitle = (TypefaceTextView) dialog.findViewById(R.id.pandora_dialog_title);
        mTitle.setText(getResources().getString(R.string.individualization_welcome_text));
        dialog.findViewById(R.id.pandora_dialog_individualization).setVisibility(View.VISIBLE);
        final BaseEditText mEditText = (BaseEditText) dialog
                .findViewById(R.id.pandora_dialog_individualization_edit_text);

        String welcomeString = PandoraConfig.newInstance(IndividualizationActivity.this)
                .getWelcomeString();
        if (!TextUtils.isEmpty(welcomeString)) {
            mEditText.setText(welcomeString);
            mEditText.setSelection(welcomeString.length());
        }

        TypefaceTextView mCancle = (TypefaceTextView) dialog
                .findViewById(R.id.pandora_dialog_individualization_button_cancle);
        mCancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TypefaceTextView mSure = (TypefaceTextView) dialog
                .findViewById(R.id.pandora_dialog_individualization_button_sure);
        mSure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String welcomeString = mEditText.getText().toString();
                UmengCustomEventManager.statisticalSetWelcomeString(welcomeString, true);
                saveWelcomeString(welcomeString);
                dialog.dismiss();
            }
        });

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

    private void closeMobileNetwork() {
        PandoraConfig.newInstance(this).saveMobileNetwork(false);
    }

    private void openMobileNetwork() {
        PandoraConfig.newInstance(this).saveMobileNetwork(true);
    }

    private boolean isMobileNetwork() {
        return PandoraConfig.newInstance(this).isMobileNetwork();
    }

    private void saveWelcomeString(String welcomeString) {
        PandoraConfig.newInstance(this).saveWelcomeString(welcomeString);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.individualization_locker_default_image:
                PandoraUtils.gotoGalleryActivity(IndividualizationActivity.this,
                        PandoraUtils.REQUEST_CODE_GALLERY);
                UmengCustomEventManager.statisticalSetDefaultImage(false);
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
        PandoraUtils.gotoCropActivity(this, uri, mAspectRatioX, mAspectRatioY, false);
    }

    private void setBitmap() {
        mLockerDefaultImageThumb.setImageBitmap(PandoraUtils.sLockDefaultThumbBitmap);
    }

    private void saveWallpaperFile(final String fileName) {
        if (null != PandoraUtils.sLockDefaultThumbBitmap) {
            mkDirs();
            FileHelper.clearFolderFiles(new File(LOCK_DEFAULT_SDCARD_LOCATION));
            ImageUtils.saveImageToFile(PandoraUtils.sLockDefaultThumbBitmap,
                    getLockDefaultFilePath(fileName));
            saveLockDefaultSP(fileName);
        }
    }

    private String getLockDefaultFilePath(String fileName) {
        return LOCK_DEFAULT_SDCARD_LOCATION + fileName + ".jpg";
    }

    public void mkDirs() {
        File tmpDir = new File(LOCK_DEFAULT_SDCARD_LOCATION);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
    }

    private void saveLockDefaultSP(String fileName) {
        if (mHandler.hasMessages(MSG_SAVE_LOCK_DEFAULT)) {
            mHandler.removeMessages(MSG_SAVE_LOCK_DEFAULT);
        }
        Message message = Message.obtain();
        message.what = MSG_SAVE_LOCK_DEFAULT;
        message.obj = fileName;
        mHandler.sendMessage(message);
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
        Intent in = new Intent();
        in.setClass(IndividualizationActivity.this, MainSettingsActivity.class);
        startActivity(in);
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("IndividualizationActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
        if (sIsDirect) {
            sIsDirect = false;
            PandoraUtils.gotoGalleryActivity(IndividualizationActivity.this,
                    PandoraUtils.REQUEST_CODE_GALLERY);
            UmengCustomEventManager.statisticalSetDefaultImage(false);
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("IndividualizationActivity"); // 保证 onPageEnd
                                                              // 在onPause
        // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
