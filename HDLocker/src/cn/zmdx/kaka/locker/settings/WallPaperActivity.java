
package cn.zmdx.kaka.locker.settings;

import java.lang.ref.WeakReference;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.custom.wallpaper.CustomWallpaperManager;
import cn.zmdx.kaka.locker.custom.wallpaper.PandoraWallpaperManager;
import cn.zmdx.kaka.locker.custom.wallpaper.PandoraWallpaperManager.IWallpaperClickListener;
import cn.zmdx.kaka.locker.custom.wallpaper.PandoraWallpaperManager.PandoraWallpaper;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

import com.umeng.analytics.MobclickAgent;

@SuppressWarnings("deprecation")
public class WallPaperActivity extends Activity implements IWallpaperClickListener {

    private View mRootView;

    private ViewGroup mCustomContainer = null;

    private ViewGroup mDefaultContainer = null;

    private Animator customAppearingAnim, customDisappearingAnim;

    private Animator customChangingAppearingAnim, customChangingDisappearingAnim;

    private static final int MSG_SAVE_CUSTOM_WALLPAPER = 11;

    private static final int MSG_SAVE_DEFAULT_WALLPAPER = 12;

    private List<PandoraWallpaper> mPandoraWallpaperList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_wallpaper);
        initView();
        initWallpaper();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mPandoraWallpaperList = PandoraWallpaperManager.getWallpaperList(
                        WallPaperActivity.this, mDefaultContainer, mCustomContainer,
                        WallPaperActivity.this);
                checkoutCurrentWallpaper();
            }
        }, 50);
    }

    private void initView() {
        mRootView = findViewById(R.id.pandora_wallpaper);

        initCustomContainer();
        initDefaultContainer();

        initTransition();

        initAddCustomButton();
    }

    private void initCustomContainer() {
        mCustomContainer = new FixedGridLayout(this);
        mCustomContainer.setClipChildren(false);
        int width = (int) getResources().getDimension(R.dimen.pandora_wallpaper_layout_width);
        int height = (int) getResources().getDimension(R.dimen.pandora_wallpaper_layout_height);
        ((FixedGridLayout) mCustomContainer).setCellHeight(height);
        ((FixedGridLayout) mCustomContainer).setCellWidth(width);
        ViewGroup parent = (ViewGroup) findViewById(R.id.parent);
        parent.addView(mCustomContainer);
        parent.setClipChildren(false);
    }

    private void initDefaultContainer() {
        mDefaultContainer = new FixedGridLayout(this);
        mDefaultContainer.setClipChildren(false);
        int adviceWidth = (int) getResources().getDimension(R.dimen.pandora_wallpaper_layout_width);
        int adviceHeight = (int) getResources().getDimension(
                R.dimen.pandora_wallpaper_layout_height);
        ((FixedGridLayout) mDefaultContainer).setCellHeight(adviceHeight);
        ((FixedGridLayout) mDefaultContainer).setCellWidth(adviceWidth);
        ViewGroup adviceParent = (ViewGroup) findViewById(R.id.advice_parent);
        adviceParent.addView(mDefaultContainer);
        adviceParent.setClipChildren(false);
    }

    private void initWallpaper() {
        Theme theme = ThemeManager.getCurrentTheme();
        if (theme.isCustomWallpaper()) {
            BitmapDrawable drawable = theme.getmCustomBitmap();
            if (null == drawable) {
                mRootView.setBackgroundResource(theme.getmBackgroundResId());
            } else {
                mRootView.setBackgroundDrawable(drawable);
            }
        } else {
            mRootView.setBackgroundResource(theme.getmBackgroundResId());
        }
    }

    protected void setSettingBackground(int themeId) {
        Theme theme = ThemeManager.getThemeById(themeId);
        mRootView.setBackgroundResource(theme.getmBackgroundResId());
    }

    private void initAddCustomButton() {
        final RelativeLayout mWallpaperRl = (RelativeLayout) LayoutInflater.from(
                HDApplication.getInstannce()).inflate(R.layout.pandora_wallpaper_item, null);
        RelativeLayout mWallpaperIvRl = (RelativeLayout) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_iamge_rl);
        ImageView mWallpaperIv = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_iamge);
        mWallpaperRl.findViewById(R.id.pandora_wallpaper_item_select).setVisibility(View.GONE);
        mWallpaperRl.findViewById(R.id.pandora_wallpaper_item_delete).setVisibility(View.GONE);
        mWallpaperIv.setImageDrawable(getResources().getDrawable(R.drawable.pandora_wallpaper_add));
        mWallpaperIv.setBackgroundResource(R.drawable.setting_wallpaper_add_button_selector);
        mWallpaperIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCustomContainer.getChildCount() >= 6) {
                    // TODO toast
                    Toast.makeText(WallPaperActivity.this, "壁纸数目已经到达上限，请删除部分不需要的壁纸!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                UmengCustomEventManager.statisticalClickCustomButtonTimes();
                showSelectDialog();
            }
        });
        mCustomContainer.addView(mWallpaperRl, Math.min(1, mCustomContainer.getChildCount()));
        LayoutParams params = mWallpaperIvRl.getLayoutParams();
        int width = (int) getResources().getDimension(R.dimen.pandora_wallpaper_width);
        int height = (int) getResources().getDimension(R.dimen.pandora_wallpaper_height);
        params.width = width;
        params.height = height;
        mWallpaperIvRl.setLayoutParams(params);

        LayoutParams layoutParams = mWallpaperRl.getLayoutParams();
        int layoutWidth = (int) getResources().getDimension(R.dimen.pandora_wallpaper_layout_width);
        int layoutHeight = (int) getResources().getDimension(
                R.dimen.pandora_wallpaper_layout_height);
        layoutParams.width = layoutWidth;
        layoutParams.height = layoutHeight;
        mWallpaperRl.setLayoutParams(layoutParams);

        LayoutParams layoutParamIV = mWallpaperIv.getLayoutParams();
        int widthIv = (int) getResources().getDimension(R.dimen.pandora_wallpaper_width);
        int heightIv = (int) getResources().getDimension(R.dimen.pandora_wallpaper_height);
        layoutParamIV.width = widthIv;
        layoutParamIV.height = heightIv;
        mWallpaperIv.setLayoutParams(layoutParamIV);

    }

    private void showSelectDialog() {
        PandoraUtils.gotoGalleryActivity(WallPaperActivity.this, PandoraUtils.REQUEST_CODE_GALLERY);
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
                saveWallpaperSP(fileName);
                PandoraWallpaperManager.setCustomWallpaperItem(this, mCustomContainer,
                        PandoraUtils.sCropThumbBitmap, fileName, fileName, this,
                        mPandoraWallpaperList);
                setCurrentWallpaperBoolean(true, fileName,
                        PandoraWallpaperManager.THEME_INT_KEY_NO_NEED);
                setBackground(PandoraUtils.sCropBitmap, -1);
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

    private void setBackground(Bitmap bitmap, int resId) {
        if (null == bitmap) {
            mRootView.setBackgroundDrawable(getResources().getDrawable(resId));
        } else {
            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
            mRootView.setBackgroundDrawable(drawable);
        }
    }

    private void saveWallpaperFile(final String fileName) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                PandoraUtils.saveBitmap(PandoraUtils.sCropBitmap,
                        CustomWallpaperManager.WALLPAPER_SDCARD_LOCATION, fileName);
                PandoraUtils.saveBitmap(PandoraUtils.sCropThumbBitmap,
                        CustomWallpaperManager.WALLPAPER_THUMB_SDCARD_LOCATION, fileName);
            }
        }).start();
    }

    private void gotoCropActivity(Uri uri) {
        Intent intent = new Intent();
        intent.setClass(this, CropImageActivity.class);
        intent.setData(uri);
        int mAspectRatioX = 0;
        int mAspectRatioY = 0;
        int width = BaseInfoHelper.getWidth(this);
        int height = Integer.parseInt(BaseInfoHelper.getHeight(this));
        if (width >= height) {
            mAspectRatioX = 100;
            mAspectRatioY = (mAspectRatioX * height) / width;
        }
        if (height >= width) {
            mAspectRatioY = 100;
            mAspectRatioX = (mAspectRatioY * width) / height;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(CropImageActivity.KEY_BUNDLE_ASPECTRATIO_X, mAspectRatioX);
        bundle.putInt(CropImageActivity.KEY_BUNDLE_ASPECTRATIO_Y, mAspectRatioY);
        bundle.putBoolean(CropImageActivity.KEY_BUNDLE_IS_WALLPAPER, true);
        intent.putExtras(bundle);
        startActivityForResult(intent, PandoraUtils.REQUEST_CODE_CROP_IMAGE);
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private void saveWallpaperSP(String fileName) {
        if (mHandler.hasMessages(MSG_SAVE_CUSTOM_WALLPAPER)) {
            mHandler.removeMessages(MSG_SAVE_CUSTOM_WALLPAPER);
        }
        Message message = Message.obtain();
        message.what = MSG_SAVE_CUSTOM_WALLPAPER;
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
                case MSG_SAVE_CUSTOM_WALLPAPER:
                    String fileName = (String) msg.obj;
                    ((WallPaperActivity) activity).saveCustomWallpaperFileName(fileName);
                    ((WallPaperActivity) activity).saveThemeId(ThemeManager.THEME_ID_CUSTOM);
                    break;
                case MSG_SAVE_DEFAULT_WALLPAPER:
                    int themeId = msg.arg1;
                    ((WallPaperActivity) activity).saveThemeId(themeId);
                    ((WallPaperActivity) activity).saveCustomWallpaperFileName("");
                    break;

            }
            super.handleMessage(msg);
        }
    }

    private void saveCustomWallpaperFileName(String fileName) {
        PandoraConfig.newInstance(this).saveCustomWallpaperFileName(fileName);
    }

    public void saveThemeId(int themeId) {
        ThemeManager.saveTheme(themeId);
    }

    protected void showDelDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.setContentView(R.layout.pandora_dialog);
        dialog.show();

    }

    private void initTransition() {
        final LayoutTransition transitioner = new LayoutTransition();
        mCustomContainer.setLayoutTransition(transitioner);
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

    @Override
    protected void onDestroy() {
        mPandoraWallpaperList = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WallPaperActivity"); // 统计页面
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WallPaperActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onCustomClickListener(String fileName, Bitmap bitmap) {
        setCurrentWallpaperBoolean(true, fileName, -1);
        UmengCustomEventManager.statisticalSelectTheme(ThemeManager.THEME_ID_CUSTOM);
        Bitmap backgroundBitmap = PandoraUtils.getBitmap(CustomWallpaperManager
                .getCustomWallpaperFilePath(fileName));
        PandoraUtils.sCropBitmap = backgroundBitmap;
        PandoraUtils.sCropThumbBitmap = bitmap;
        setBackground(backgroundBitmap, -1);
        saveWallpaperSP(fileName);

    }

    @Override
    public void onDefaultClickListener(int themeId) {
        setCurrentWallpaperBoolean(false, "", themeId);
        setSettingBackground(themeId);
        UmengCustomEventManager.statisticalSelectTheme(themeId);

        PandoraUtils.sCropBitmap = null;
        PandoraUtils.sCropThumbBitmap = null;
        if (mHandler.hasMessages(MSG_SAVE_DEFAULT_WALLPAPER)) {
            mHandler.removeMessages(MSG_SAVE_DEFAULT_WALLPAPER);
        }
        Message message = Message.obtain();
        message.what = MSG_SAVE_DEFAULT_WALLPAPER;
        message.arg1 = themeId;
        mHandler.sendMessage(message);
    }

    @Override
    public void onDelClickListener(RelativeLayout mWallpaperRl, final String fileName) {
        mCustomContainer.removeView(mWallpaperRl);
        delPandoraWallpaperListItem(fileName);
        String currentFileName = PandoraConfig.newInstance(this).getCustomWallpaperFileName();
        if (fileName.equals(currentFileName)) {
            PandoraConfig.newInstance(this).saveCustomWallpaperFileName("");
            PandoraConfig.newInstance(this).saveThemeId(ThemeManager.THEME_ID_DEFAULT);
            PandoraUtils.sCropBitmap = null;
            PandoraUtils.sCropThumbBitmap = null;
            setDefaultWallPaperBoolean();
            Theme theme = ThemeManager.getThemeById(ThemeManager.THEME_ID_DEFAULT);
            setBackground(null, theme.getmBackgroundResId());
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                PandoraUtils.deleteFile(CustomWallpaperManager.WALLPAPER_SDCARD_LOCATION, fileName);
                PandoraUtils.deleteFile(CustomWallpaperManager.WALLPAPER_THUMB_SDCARD_LOCATION,
                        fileName);
            }
        }).start();

    }

    private void setDefaultWallPaperBoolean() {
        for (int i = 0; i < mPandoraWallpaperList.size(); i++) {
            if (mPandoraWallpaperList.get(i).getImageIntKey() == ThemeManager.THEME_ID_DEFAULT) {
                mPandoraWallpaperList.get(i).setCurrentWallpaper(true);
            } else {
                mPandoraWallpaperList.get(i).setCurrentWallpaper(false);
            }
        }
        checkoutCurrentWallpaper();
    }

    private void setCurrentWallpaperBoolean(boolean isCustom, String mImageStringKeyName,
            int mImageIntKey) {
        for (int i = 0; i < mPandoraWallpaperList.size(); i++) {
            if (isCustom) {
                if (mImageStringKeyName
                        .equals(mPandoraWallpaperList.get(i).getImageStringKeyName())) {
                    mPandoraWallpaperList.get(i).setCurrentWallpaper(true);
                } else {
                    mPandoraWallpaperList.get(i).setCurrentWallpaper(false);
                }
            } else {
                if (mImageIntKey == mPandoraWallpaperList.get(i).getImageIntKey()) {
                    mPandoraWallpaperList.get(i).setCurrentWallpaper(true);
                } else {
                    mPandoraWallpaperList.get(i).setCurrentWallpaper(false);
                }
            }
        }

        checkoutCurrentWallpaper();
    }

    private void delPandoraWallpaperListItem(String mImageStringKeyName) {
        PandoraWallpaper pandoraWallpaper = new PandoraWallpaper();
        for (int i = 0; i < mPandoraWallpaperList.size(); i++) {
            if (mImageStringKeyName.equals(mPandoraWallpaperList.get(i).getImageStringKeyName())) {
                pandoraWallpaper = mPandoraWallpaperList.get(i);
            }
        }
        mPandoraWallpaperList.remove(pandoraWallpaper);
    }

    private void checkoutCurrentWallpaper() {
        for (int i = 0; i < mPandoraWallpaperList.size(); i++) {
            if (mPandoraWallpaperList.get(i).isCurrentWallpaper()) {
                mPandoraWallpaperList.get(i).getImageView().setVisibility(View.VISIBLE);
            } else {
                mPandoraWallpaperList.get(i).getImageView().setVisibility(View.INVISIBLE);
            }
        }
    }
}
