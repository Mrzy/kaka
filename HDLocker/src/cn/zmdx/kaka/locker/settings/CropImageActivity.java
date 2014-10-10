
package cn.zmdx.kaka.locker.settings;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;

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

    private Button mRotate;

    private Button mOK;

    private Button mCancle;

    private boolean isRotate = false;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_crop_image);
        try {
            mCropBitmap = PandoraUtils.zoomBitmap(this, getIntent().getData());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mAspectRatioX = getIntent().getExtras().getInt(KEY_BUNDLE_ASPECTRATIO_X);
        mAspectRatioY = getIntent().getExtras().getInt(KEY_BUNDLE_ASPECTRATIO_Y);
        // try {
        // mCropBitmap = PandoraUtils.zoomBitmap(this, getIntent().getData());
        // int width = mCropBitmap.getWidth();
        // int height = mCropBitmap.getHeight();
        // if (width >= height) {
        // mAspectRatioX = 100;
        // mAspectRatioY = (mAspectRatioX * mCropBitmap.getHeight()) /
        // mCropBitmap.getWidth();
        // }
        // if (height >= width) {
        // mAspectRatioY = 100;
        // mAspectRatioX = (mAspectRatioY * mCropBitmap.getWidth()) /
        // mCropBitmap.getHeight();
        // }
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        initView();
    }

    private void initView() {
        mCropImageView = (CropImageView) findViewById(R.id.pandora_crop_image);
        mCropImageView.setFixedAspectRatio(true);
        // mCropImageView.setImageScaleType(ScaleType.FIT_XY);
        mCropImageView.setImageBitmap(mCropBitmap);
        mCropImageView.setAspectRatio(mAspectRatioX, mAspectRatioY);

        mRotate = (Button) findViewById(R.id.pandora_rotate_btn);
        mRotate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isRotate = !isRotate;
                if (isRotate) {
                    mCropImageView.setAspectRatio(mAspectRatioY, mAspectRatioX);
                } else {
                    mCropImageView.setAspectRatio(mAspectRatioX, mAspectRatioY);
                }
                mCropImageView.rotateImage(ROTATE_NINETY_DEGREES);
            }
        });
        mOK = (Button) findViewById(R.id.pandora_crop_sure);
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
        mCancle = (Button) findViewById(R.id.pandora_crop_cancle);
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
