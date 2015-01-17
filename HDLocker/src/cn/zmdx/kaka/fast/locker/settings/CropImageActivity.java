
package cn.zmdx.kaka.fast.locker.settings;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;
import cn.zmdx.kaka.fast.locker.LockScreenManager;
import cn.zmdx.kaka.fast.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.fast.locker.theme.ThemeManager;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.fast.locker.utils.ImageUtils;
import cn.zmdx.kaka.fast.locker.widget.TypefaceTextView;
import cn.zmdx.kaka.fast.locker.BuildConfig;
import cn.zmdx.kaka.fast.locker.R;

import com.edmodo.cropper.CropImageView;

public class CropImageActivity extends Activity {

    private CropImageView mCropImageView;

    private Bitmap mCropBitmap;

    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;

    private static final int ROTATE_NINETY_DEGREES = 90;

    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";

    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";

    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;

    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

    private TypefaceTextView mRotate;

    private TypefaceTextView mOK;

    private TypefaceTextView mCancle;

    public static final String KEY_BUNDLE_BITMAP = "cropBitmap";

    public static final String KEY_BUNDLE_ASPECTRATIO_X = "aspectRatioX";

    public static final String KEY_BUNDLE_ASPECTRATIO_Y = "aspectRatioY";

    public static final String KEY_BUNDLE_IS_WALLPAPER = "isWallpaper";

    private boolean isWallpaper = true;

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_crop_image);
        try {
            mCropBitmap = getBitmap(getIntent().getData());
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            Toast.makeText(CropImageActivity.this, getResources().getString(R.string.error),
                    Toast.LENGTH_LONG).show();
            setResult(Activity.RESULT_CANCELED);
            onBackPressed();
        }
        mAspectRatioX = getIntent().getExtras().getInt(KEY_BUNDLE_ASPECTRATIO_X);
        mAspectRatioY = getIntent().getExtras().getInt(KEY_BUNDLE_ASPECTRATIO_Y);
        isWallpaper = getIntent().getExtras().getBoolean(KEY_BUNDLE_IS_WALLPAPER);
        initView();
    }

    private void initView() {
        mCropImageView = (CropImageView) findViewById(R.id.pandora_crop_image);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setImageBitmap(mCropBitmap);
        mCropImageView.setAspectRatio(mAspectRatioX, mAspectRatioY);

        mRotate = (TypefaceTextView) findViewById(R.id.pandora_rotate_btn);
        mRotate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCropImageView.rotateImage(ROTATE_NINETY_DEGREES);
            }
        });
        mOK = (TypefaceTextView) findViewById(R.id.pandora_crop_sure);
        mOK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isWallpaper) {
                    UmengCustomEventManager.statisticalSuccessSetCustomTimes();
                    ThemeManager.saveTheme(ThemeManager.THEME_ID_CUSTOM);
                    ThemeManager.addBitmapToCache(mCropImageView.getCroppedImage());
                    setResult(Activity.RESULT_OK);
                } else {
                    try {
                        PandoraUtils.sLockDefaultThumbBitmap = zoomBitmap();
                        setResult(Activity.RESULT_OK);
                    } catch (Exception e) {
                        Toast.makeText(CropImageActivity.this,
                                getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
                        PandoraUtils.sLockDefaultThumbBitmap = null;
                        setResult(Activity.RESULT_CANCELED);
                    }
                }
                onBackPressed();
            }
        });
        mCancle = (TypefaceTextView) findViewById(R.id.pandora_crop_cancle);
        mCancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                onBackPressed();
            }
        });
    }

    /**
     * 获取适应当前屏幕宽高比的图片
     * 
     * @param activity
     * @param uri
     * @return
     * @throws FileNotFoundException
     */
    public Bitmap getBitmap(Uri uri) throws FileNotFoundException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BitmapFactory.Options opts = new Options();
        opts.inJustDecodeBounds = true;// 设置为true时，BitmapFactory只会解析要加载的图片的边框的信息，但是不会为该图片分配内存
        BitmapFactory.decodeStream(inputStream, new Rect(), opts);
        int screenHeight = BaseInfoHelper.getRealHeight(this);
        int screenWidth = BaseInfoHelper.getRealWidth(this);
        BitmapFactory.Options realOpts = new Options();
        realOpts.inSampleSize = ImageUtils.computeSampleSize(opts, screenWidth, screenHeight);
        realOpts.inJustDecodeBounds = false;
        realOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        realOpts.inPurgeable = true;
        realOpts.inInputShareable = true;
        InputStream realInputStream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(realInputStream, new Rect(), realOpts);
        return bitmap;
    }

    /**
     * 将图片缩放到适应锁屏页面默认图片比例的图片
     * 
     * @return
     */
    public Bitmap zoomBitmap() {
        int thumbWidth = BaseInfoHelper.getRealWidth(this);
        int thumbHeight = (int) (thumbWidth / (LockScreenManager.getInstance()
                .getBoxWidthHeightRate()));
        return ImageUtils.scaleTo(mCropImageView.getCroppedImage(), thumbWidth, thumbHeight, false);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }
}
