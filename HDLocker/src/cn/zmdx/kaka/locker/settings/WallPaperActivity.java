
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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.custom.wallpaper.CustomWallpaperManager;
import cn.zmdx.kaka.locker.custom.wallpaper.CustomWallpaperManager.CustomWallpaper;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

public class WallPaperActivity extends Activity {

    public static final int REQUEST_CODE_CROP_IMAGE = 0;

    private static final int REQUEST_CODE_GALLERY = 1;

    private PandoraConfig mPandoraConfig;

    private View mRootView;

    private ViewGroup container = null;

    private Animator customAppearingAnim, customDisappearingAnim;

    private Animator customChangingAppearingAnim, customChangingDisappearingAnim;

    private SparseArray<ImageView> mBorderArray = new SparseArray<ImageView>();

    private SparseArray<String> mThumbNameArray = new SparseArray<String>();

    private static final int MSG_SAVE_WALLPAPER = 11;

    private static final int MSG_SAVE_WALLPAPER_DELAY = 100;

    private static final int MSG_SAVE_ADVICE_WALLPAPER = 12;

    private static final int MSG_SAVE_ADVICE_WALLPAPER_DELAY = 100;

    private SparseArray<ImageView> mAdviceBorderArray = new SparseArray<ImageView>();

    private SparseIntArray mAdviceThumbIdArray = new SparseIntArray();

    private ViewGroup mAdviceContainer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandora_wallpaper);
        mPandoraConfig = PandoraConfig.newInstance(this);
        initView();
        initWallpaper();
        // initCustomWallpaper();
        // initAdviceWallpaper();
    }

    private void initView() {
        mRootView = findViewById(R.id.pandora_wallpaper);

        initContainer();
        initAdviceContainer();

        initTransition();

        initAddCustomButton();
    }

    private void initContainer() {
        container = new FixedGridLayout(this);
        container.setClipChildren(false);
        // int width = PandoraUtils.getWallpapaerWidth(this);
        int width = (int) getResources().getDimension(R.dimen.pandora_wallpaper_layout_width);
        int height = (int) getResources().getDimension(R.dimen.pandora_wallpaper_layout_height);
        // int height = PandoraUtils.getWallpaperHeight(this, width);
        ((FixedGridLayout) container).setCellHeight(height);
        ((FixedGridLayout) container).setCellWidth(width);
        ViewGroup parent = (ViewGroup) findViewById(R.id.parent);
        parent.addView(container);
        parent.setClipChildren(false);
    }

    private void initAdviceContainer() {
        mAdviceContainer = new FixedGridLayout(this);
        mAdviceContainer.setClipChildren(false);
        // int adviceWidth = PandoraUtils.getWallpapaerWidth(this);
        int adviceWidth = (int) getResources().getDimension(R.dimen.pandora_wallpaper_layout_width);
        int adviceHeight = (int) getResources().getDimension(
                R.dimen.pandora_wallpaper_layout_height);
        // int adviceHeight = PandoraUtils.getWallpaperHeight(this,
        // adviceWidth);
        ((FixedGridLayout) mAdviceContainer).setCellHeight(adviceHeight);
        ((FixedGridLayout) mAdviceContainer).setCellWidth(adviceWidth);
        ViewGroup adviceParent = (ViewGroup) findViewById(R.id.advice_parent);
        adviceParent.addView(mAdviceContainer);
        adviceParent.setClipChildren(false);
    }

    private void initWallpaper() {
        int themeId = mPandoraConfig.getCurrentThemeId();
        if (themeId == -1) {
            setCustomBackground();
        } else {
            setSettingBackground(themeId);
        }
        initCustomWallpaper();
        initAdviceWallpaper();
    }

    private void initCustomWallpaper() {
        if (CustomWallpaperManager.isHaveCustomThumbWallpaper()) {
            List<CustomWallpaper> wallpaperList = CustomWallpaperManager.getCustomThumbWallpaper();
            addCustomWallpaperItem(wallpaperList, null, null);
        }

    }

    private void initAdviceWallpaper() {
        List<Theme> mThemeList = ThemeManager.getAllTheme();
        for (int i = 0; i < mThemeList.size(); i++) {
            final RelativeLayout mWallpaperRl = (RelativeLayout) LayoutInflater.from(
                    HDApplication.getInstannce()).inflate(R.layout.pandora_wallpaper_item, null);
            int margin = (int) getResources().getDimension(R.dimen.pandora_wallpaper_margin);
            mWallpaperRl.setPadding(margin, margin, margin, margin);
            ImageView mWallpaperIv = (ImageView) mWallpaperRl
                    .findViewById(R.id.pandora_wallpaper_item_iamge);
            ImageView mWallpaperSelect = (ImageView) mWallpaperRl
                    .findViewById(R.id.pandora_wallpaper_item_select);
            mWallpaperRl.findViewById(R.id.pandora_wallpaper_item_delete).setVisibility(View.GONE);
            int themeId = mThemeList.get(i).getmThemeId();
            mAdviceThumbIdArray.put(i, themeId);
            mAdviceBorderArray.put(i, mWallpaperSelect);
            mWallpaperIv.setTag(i);
            mWallpaperIv.setImageResource(mThemeList.get(i).getmThumbnailResId());
            mWallpaperIv.setOnClickListener(mPicClickListener);
            mAdviceContainer.addView(mWallpaperRl, mAdviceContainer.getChildCount());
            LayoutParams params = mWallpaperIv.getLayoutParams();
            int width = (int) getResources().getDimension(R.dimen.pandora_wallpaper_width);
            int height = (int) getResources().getDimension(R.dimen.pandora_wallpaper_height);
            params.width = width;
            params.height = height;

            LayoutParams layoutParams = mWallpaperRl.getLayoutParams();
            int layoutWidth = (int) getResources().getDimension(
                    R.dimen.pandora_wallpaper_layout_width);
            int layoutHeight = (int) getResources().getDimension(
                    R.dimen.pandora_wallpaper_layout_height);
            layoutParams.width = layoutWidth;
            layoutParams.height = layoutHeight;
        }
        setCurrentAdviceWallpaper();
    }

    private void setCurrentAdviceWallpaper() {
        int themeId = PandoraConfig.newInstance(this).getCurrentThemeId();
        for (int i = 0; i < mAdviceThumbIdArray.size(); i++) {
            if (themeId == mAdviceThumbIdArray.get(i)) {
                if (null != mAdviceBorderArray) {
                    mAdviceBorderArray.get(i).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected void setSettingBackground(int themeId) {
        Theme theme = ThemeManager.getThemeById(themeId);
        mRootView.setBackgroundResource(theme.getmBackgroundResId());
    }

    private View.OnClickListener mPicClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if (null != view) {
                int position = (Integer) view.getTag();
                for (int pos = 0; pos < mAdviceThumbIdArray.size(); pos++) {
                    if (pos == position) {
                        mAdviceBorderArray.get(pos).setVisibility(View.VISIBLE);
                    } else {
                        mAdviceBorderArray.get(pos).setVisibility(View.GONE);
                    }
                }
                int themeId = mAdviceThumbIdArray.get(position);
                setSettingBackground(themeId);
                UmengCustomEventManager.statisticalSelectTheme(themeId);

                PandoraUtils.sCropBitmap = null;
                PandoraUtils.sCropThumbBitmap = null;
                if (mHandler.hasMessages(MSG_SAVE_ADVICE_WALLPAPER)) {
                    mHandler.removeMessages(MSG_SAVE_ADVICE_WALLPAPER);
                }
                Message message = Message.obtain();
                message.what = MSG_SAVE_ADVICE_WALLPAPER;
                message.arg1 = themeId;
                mHandler.sendMessageDelayed(message, MSG_SAVE_ADVICE_WALLPAPER_DELAY);
            }
        }
    };

    private void setCustomBackground() {
        String fileName = PandoraConfig.newInstance(this).getCustomWallpaperFileName();
        String path = CustomWallpaperManager.getCustomWallpaperFilePath(fileName);
        Bitmap bitmap = PandoraUtils.getBitmap(path);
        if (null == bitmap) {
            mRootView.setBackground(getResources().getDrawable(R.drawable.setting_background_blue));
        } else {
            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
            mRootView.setBackground(drawable);
        }
    }

    private void initAddCustomButton() {
        ImageView mAddCustom = new ImageView(this);
        mAddCustom.setScaleType(ScaleType.FIT_XY);
        int margin = (int) getResources().getDimension(R.dimen.pandora_wallpaper_margin);
        mAddCustom.setPadding(margin, margin, margin, margin);
        mAddCustom.setImageDrawable(getResources().getDrawable(R.drawable.pandora_wallpaper_add));
        mAddCustom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSelectDialog();
            }
        });
        container.addView(mAddCustom);
        int width = (int) getResources().getDimension(R.dimen.pandora_wallpaper_width);
        int height = (int) getResources().getDimension(R.dimen.pandora_wallpaper_height);
        LayoutParams params = mAddCustom.getLayoutParams();
        params.width = width;
        params.height = height;
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
                String fileName = PandoraUtils.getRandomString();
                addCustomWallpaperItem(null, PandoraUtils.sCropThumbBitmap, fileName);
                setBackground(PandoraUtils.sCropBitmap, -1);
                saveWallpaperFile(fileName);
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

    private void setBackground(Bitmap bitmap, int resId) {
        if (null == bitmap) {
            mRootView.setBackground(getResources().getDrawable(resId));
        } else {
            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
            mRootView.setBackground(drawable);
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
                PandoraConfig.newInstance(WallPaperActivity.this).saveCustomWallpaperFileName(
                        fileName);
            }
        }).start();
    }

    private void gotoCropActivity(Uri uri) {
        Intent intent = new Intent();
        intent.setClass(this, CropImageActivity.class);
        intent.setData(uri);
        int mAspectRatioX = 0;
        int mAspectRatioY = 0;
        int width = Integer.parseInt(BaseInfoHelper.getWidth(this));
        int height = Integer.parseInt(BaseInfoHelper.getHeight(this));
        // int width = (int)
        // getResources().getDimension(R.dimen.pandora_wallpaper_width);
        // int height = (int)
        // getResources().getDimension(R.dimen.pandora_wallpaper_height);
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
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private void addCustomWallpaperItem(List<CustomWallpaper> wallpaperList, Bitmap bitmap,
            String fileName) {
        if (null == wallpaperList) {
            initThumbWallpaperLayout(bitmap, mThumbNameArray.size(), fileName);
        } else {
            for (int i = 0; i < wallpaperList.size(); i++) {
                Bitmap thumbBitmap = PandoraUtils.getBitmap(wallpaperList.get(i).getFilePath());
                initThumbWallpaperLayout(thumbBitmap, i, wallpaperList.get(i).getFileName());
            }
        }
        if (null == fileName) {
            checkCurrentCustomWallPaper(PandoraConfig.newInstance(this)
                    .getCustomWallpaperFileName());
        } else {
            checkCurrentCustomWallPaper(fileName);
        }
    }

    private void checkCurrentCustomWallPaper(String fileName) {

        for (int i = 0; i < mThumbNameArray.size(); i++) {
            if (fileName.equals(mThumbNameArray.get(mThumbNameArray.keyAt(i)))) {
                if (null != mBorderArray) {
                    mBorderArray.get(mBorderArray.keyAt(i)).setVisibility(View.VISIBLE);
                }
            } else {
                if (null != mBorderArray) {
                    mBorderArray.get(mBorderArray.keyAt(i)).setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void saveWallpaperSP(String fileName) {
        if (mHandler.hasMessages(MSG_SAVE_WALLPAPER)) {
            mHandler.removeMessages(MSG_SAVE_WALLPAPER);
        }
        Message message = Message.obtain();
        message.what = MSG_SAVE_WALLPAPER;
        message.obj = fileName;
        mHandler.sendMessageDelayed(message, MSG_SAVE_WALLPAPER_DELAY);
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
                case MSG_SAVE_WALLPAPER:
                    String fileName = (String) msg.obj;
                    ((WallPaperActivity) activity).saveCustomWallpaperFileName(fileName);
                    ((WallPaperActivity) activity).saveThemeId(ThemeManager.THEME_ID_CUSTOM);
                    break;
                case MSG_SAVE_ADVICE_WALLPAPER:
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

    private void initThumbWallpaperLayout(final Bitmap bitmap, final int key, final String fileName) {
        final RelativeLayout mWallpaperRl = (RelativeLayout) LayoutInflater.from(
                HDApplication.getInstannce()).inflate(R.layout.pandora_wallpaper_item, null);
        int margin = (int) getResources().getDimension(R.dimen.pandora_wallpaper_margin);
        mWallpaperRl.setPadding(margin, margin, margin, margin);
        ImageView mWallpaperIv = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_iamge);
        ImageView mWallpaperSelect = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_select);
        final ImageView mWallpaperDel = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_delete);
        mThumbNameArray.put(key, fileName);
        mBorderArray.put(key, mWallpaperSelect);
        mWallpaperIv.setImageBitmap(bitmap);
        mWallpaperIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bitmap bacground = PandoraUtils.getBitmap(CustomWallpaperManager
                        .getCustomWallpaperFilePath(fileName));
                PandoraUtils.sCropBitmap = bacground;
                PandoraUtils.sCropThumbBitmap = bitmap;
                setBackground(bacground, -1);
                checkCurrentCustomWallPaper(fileName);
                saveWallpaperSP(fileName);
            }
        });
        mWallpaperDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                container.removeView(mWallpaperRl);
                mThumbNameArray.remove(key);
                mBorderArray.remove(key);
                if (fileName.equals(PandoraConfig.newInstance(WallPaperActivity.this)
                        .getCustomWallpaperFileName())) {
                    PandoraConfig.newInstance(WallPaperActivity.this).saveCustomWallpaperFileName(
                            "");
                    Theme theme = ThemeManager.getThemeById(ThemeManager.THEME_ID_BLUE);
                    setBackground(null, theme.getmBackgroundResId());
                }
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        PandoraUtils.deleteFile(CustomWallpaperManager.WALLPAPER_SDCARD_LOCATION,
                                fileName);
                        PandoraUtils.deleteFile(
                                CustomWallpaperManager.WALLPAPER_THUMB_SDCARD_LOCATION, fileName);
                    }
                }).start();
            }
        });
        container.addView(mWallpaperRl, Math.min(1, container.getChildCount()));
        LayoutParams params = mWallpaperIv.getLayoutParams();
        int width = (int) getResources().getDimension(R.dimen.pandora_wallpaper_width);
        int height = (int) getResources().getDimension(R.dimen.pandora_wallpaper_height);
        params.width = width;
        params.height = height;

        LayoutParams layoutParams = mWallpaperRl.getLayoutParams();
        int layoutWidth = (int) getResources().getDimension(R.dimen.pandora_wallpaper_layout_width);
        int layoutHeight = (int) getResources().getDimension(
                R.dimen.pandora_wallpaper_layout_height);
        layoutParams.width = layoutWidth;
        layoutParams.height = layoutHeight;

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

    @Override
    protected void onDestroy() {
        if (null != mBorderArray) {
            mBorderArray = null;
        }
        if (null != mAdviceBorderArray) {
            mAdviceBorderArray = null;
        }
        super.onDestroy();
    }
}
