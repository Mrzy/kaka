
package cn.zmdx.kaka.locker.settings;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

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
            mCropBitmap = PandoraUtils.zoomBitmap(this, getIntent().getData());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mAspectRatioX = getIntent().getExtras().getInt(KEY_BUNDLE_ASPECTRATIO_X);
        mAspectRatioY = getIntent().getExtras().getInt(KEY_BUNDLE_ASPECTRATIO_Y);
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
                PandoraUtils.sCropBitmap = mCropImageView.getCroppedImage();
                PandoraUtils.sCropThumbBitmap = PandoraUtils.zoomThumbBitmap(
                        CropImageActivity.this, PandoraUtils.sCropBitmap);
                setResult(Activity.RESULT_OK);
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }
}
