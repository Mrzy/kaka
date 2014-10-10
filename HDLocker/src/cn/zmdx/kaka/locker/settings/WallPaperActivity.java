
package cn.zmdx.kaka.locker.settings;

import java.io.FileNotFoundException;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.custom.wallpaper.CustomWallpaperManager;
import cn.zmdx.kaka.locker.custom.wallpaper.CustomWallpaperManager.CustomWallpaper;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;

public class WallPaperActivity extends Activity {

    public static final int REQUEST_CODE_CROP_IMAGE = 0;

    private static final int REQUEST_CODE_GALLERY = 1;

    private ViewGroup container = null;

    private Animator customAppearingAnim, customDisappearingAnim;

    private Animator customChangingAppearingAnim, customChangingDisappearingAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_wallpaper);
        initView();
        initWallpaper();
    }

    private void initView() {
        initTitleHeight();

        container = new FixedGridLayout(this);
        container.setClipChildren(false);
        int height = (int) getResources().getDimension(R.dimen.pandora_wallpaper_height);
        int width = (int) getResources().getDimension(R.dimen.pandora_wallpaper_width);
        ((FixedGridLayout) container).setCellHeight(height);
        ((FixedGridLayout) container).setCellWidth(width);

        initTransition();

        initAddCustomButton();

        ViewGroup parent = (ViewGroup) findViewById(R.id.parent);
        parent.addView(container);
        parent.setClipChildren(false);
    }

    private void initWallpaper() {
        if (CustomWallpaperManager.isHaveCustomThumbWallpaper()) {
            // if
            // (!"".equals(PandoraConfig.newInstance(WallPaperActivity.this).getCustomWallpaper()))
            // {
            List<CustomWallpaper> wallpaperList = CustomWallpaperManager.getCustomThumbWallpaper();
            for (int i = 0; i < wallpaperList.size(); i++) {
                Bitmap bitmap = PandoraUtils.getBitmap(wallpaperList.get(i).getFilePath());
                addCustomWallpaper(bitmap);
            }
            // }
        }
    }

    private void initAddCustomButton() {
        ImageView mAddCustom = new ImageView(this);
        mAddCustom.setScaleType(ScaleType.FIT_XY);
        mAddCustom.setPadding(15, 15, 15, 15);
        mAddCustom.setImageDrawable(getResources().getDrawable(R.drawable.pandora_wallpaper_add));
        mAddCustom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSelectDialog();
            }
        });
        container.addView(mAddCustom);
    }

    private void showSelectDialog() {
        PandoraUtils.gotoGalleryActivity(WallPaperActivity.this, REQUEST_CODE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_CROP_IMAGE:
                addCustomWallpaper(PandoraUtils.sCropThumbBitmap);
                saveWallpaper();
                break;
            case REQUEST_CODE_GALLERY: {
                gotoCropActivity(data.getData());
                break;
            }
            default: {
                break;
            }
        }
    }

    private void saveWallpaper() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String fileName = PandoraUtils.getRandomString();
                PandoraUtils.saveBitmap(PandoraUtils.sCropBitmap,
                        CustomWallpaperManager.WALLPAPER_SDCARD_LOCATION, fileName);
                PandoraUtils.saveBitmap(PandoraUtils.sCropThumbBitmap,
                        CustomWallpaperManager.WALLPAPER_THUMB_SDCARD_LOCATION, fileName);
                PandoraConfig.newInstance(WallPaperActivity.this).saveCustomWallpaper(fileName);
            }
        }).start();
    }

    private void gotoCropActivity(Uri uri) {
        Intent intent = new Intent();
        intent.setClass(this, CropImageActivity.class);
         intent.setData(uri);
        Bitmap mCropBitmap = null;
        int mAspectRatioX = 0;
        int mAspectRatioY = 0;
        try {
            mCropBitmap = PandoraUtils.zoomBitmap(this, uri);
            int width = mCropBitmap.getWidth();
            int height = mCropBitmap.getHeight();
            if (width >= height) {
                mAspectRatioX = 100;
                mAspectRatioY = (mAspectRatioX * mCropBitmap.getHeight()) / mCropBitmap.getWidth();
            }
            if (height >= width) {
                mAspectRatioY = 100;
                mAspectRatioX = (mAspectRatioY * mCropBitmap.getWidth()) / mCropBitmap.getHeight();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(CropImageActivity.KEY_BUNDLE_ASPECTRATIO_X, mAspectRatioX);
        bundle.putInt(CropImageActivity.KEY_BUNDLE_ASPECTRATIO_Y, mAspectRatioY);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private void addCustomWallpaper(Bitmap bitmap) {
        final RelativeLayout mWallpaperRl = (RelativeLayout) LayoutInflater.from(
                HDApplication.getInstannce()).inflate(R.layout.pandora_wallpaper_item, null);
        ImageView mWallpaperIv = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_iamge);
        mWallpaperIv.setImageBitmap(bitmap);
        ImageView mWallpaperDel = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_delete);
        mWallpaperDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                container.removeView(mWallpaperRl);
            }
        });
        container.addView(mWallpaperRl, Math.min(1, container.getChildCount()));
    }

    private void initTitleHeight() {
        int height = (int) getResources().getDimension(R.dimen.setting_about_us_height);
        int statusBarHeight = PandoraUtils.getStatusBarHeight(this);
        LinearLayout titleLayout = (LinearLayout) findViewById(R.id.pandora_wallpaper_title);
        LayoutParams params = titleLayout.getLayoutParams();
        params.height = height + PandoraUtils.getStatusBarHeight(this);
        titleLayout.setLayoutParams(params);
        titleLayout.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initTransition() {
        final LayoutTransition transitioner = new LayoutTransition();
        container.setLayoutTransition(transitioner);
        createCustomAnimations(transitioner);
    }

    private void createCustomAnimations(LayoutTransition transition) {
        // Changing while Adding
        PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 1);
        PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 1);
        PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", 0, 1);
        PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 1);
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f, 1f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f, 1f);
        customChangingAppearingAnim = ObjectAnimator.ofPropertyValuesHolder(this, pvhLeft, pvhTop,
                pvhRight, pvhBottom, pvhScaleX, pvhScaleY).setDuration(
                transition.getDuration(LayoutTransition.CHANGE_APPEARING));
        customChangingAppearingAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setScaleX(1f);
                view.setScaleY(1f);
            }
        });

        // Changing while Removing
        Keyframe kf0 = Keyframe.ofFloat(0f, 0f);
        Keyframe kf1 = Keyframe.ofFloat(.9999f, 360f);
        Keyframe kf2 = Keyframe.ofFloat(1f, 0f);
        PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("rotation", kf0, kf1,
                kf2);
        customChangingDisappearingAnim = ObjectAnimator.ofPropertyValuesHolder(this, pvhLeft,
                pvhTop, pvhRight, pvhBottom, pvhRotation).setDuration(
                transition.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
        customChangingDisappearingAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setRotation(0f);
            }
        });

        // Adding
        customAppearingAnim = ObjectAnimator.ofFloat(null, "rotationY", 90f, 0f).setDuration(
                transition.getDuration(LayoutTransition.APPEARING));
        customAppearingAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setRotationY(0f);
            }
        });

        // Removing
        customDisappearingAnim = ObjectAnimator.ofFloat(null, "rotationX", 0f, 90f).setDuration(
                transition.getDuration(LayoutTransition.DISAPPEARING));
        customDisappearingAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setRotationX(0f);
            }
        });

    }
}
