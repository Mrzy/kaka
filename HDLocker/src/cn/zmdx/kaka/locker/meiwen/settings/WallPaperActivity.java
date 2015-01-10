
package cn.zmdx.kaka.locker.meiwen.settings;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.zmdx.kaka.locker.meiwen.HDApplication;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.meiwen.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.meiwen.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.meiwen.theme.ThemeManager;
import cn.zmdx.kaka.locker.meiwen.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.meiwen.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.meiwen.utils.FileHelper;
import cn.zmdx.kaka.locker.meiwen.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.meiwen.utils.ImageUtils;
import cn.zmdx.kaka.locker.meiwen.wallpaper.CustomWallpaperManager;
import cn.zmdx.kaka.locker.meiwen.wallpaper.PandoraWallpaperManager;
import cn.zmdx.kaka.locker.meiwen.wallpaper.PandoraWallpaperManager.IWallpaperClickListener;
import cn.zmdx.kaka.locker.meiwen.wallpaper.PandoraWallpaperManager.PandoraWallpaper;
import cn.zmdx.kaka.locker.meiwen.wallpaper.WallpaperUtils;
import cn.zmdx.kaka.locker.meiwen.wallpaper.WallpaperUtils.ILoadBitmapCallback;

import com.umeng.analytics.MobclickAgent;

@SuppressLint("InflateParams")
@SuppressWarnings("deprecation")
public class WallPaperActivity extends BaseActivity implements IWallpaperClickListener {

    private View mRootView;

    private ViewGroup mCustomContainer = null;

    private ViewGroup mOnlineContainer = null;

    private Animator customAppearingAnim, customDisappearingAnim;

    private Animator customChangingAppearingAnim, customChangingDisappearingAnim;

    private static final int MSG_SAVE_CURRENT_WALLPAPER_FILENAME = 12;

    private static final int MSG_INSERT_WALLPAPER_ITEM = 13;

    private List<PandoraWallpaper> mPandoraWallpaperList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(Res.layout.pandora_wallpaper);
        initView();
        mPandoraWallpaperList = PandoraWallpaperManager.getWallpaperList(WallPaperActivity.this,
                mOnlineContainer, mCustomContainer, WallPaperActivity.this);
        markSelectState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WallPaperActivity"); // 统计页面
        MobclickAgent.onResume(this);
    }

    private void initView() {
        mRootView = findViewById(Res.id.pandora_wallpaper);
        LinearLayout titleLayout = (LinearLayout) mRootView
                .findViewById(Res.id.pandora_wallpaper_title);
        initBackground(mRootView);
        initTitleHeight(titleLayout);
        initCustomContainer();
        initDefaultContainer();

        initTransition();

        initAddCustomButton();
    }

    private void initCustomContainer() {
        mCustomContainer = new FixedGridLayout(this);
        mCustomContainer.setClipChildren(false);
        int width = (int) getResources().getDimension(Res.dimen.pandora_wallpaper_layout_width);
        int height = (int) getResources().getDimension(Res.dimen.pandora_wallpaper_layout_height);
        ((FixedGridLayout) mCustomContainer).setCellHeight(height);
        ((FixedGridLayout) mCustomContainer).setCellWidth(width);
        ViewGroup parent = (ViewGroup) findViewById(Res.id.parent);
        parent.addView(mCustomContainer);
        parent.setClipChildren(false);
    }

    private void initDefaultContainer() {
        mOnlineContainer = new FixedGridLayout(this);
        mOnlineContainer.setClipChildren(false);
        int adviceWidth = (int) getResources().getDimension(Res.dimen.pandora_wallpaper_layout_width);
        int adviceHeight = (int) getResources().getDimension(
                Res.dimen.pandora_wallpaper_layout_height);
        ((FixedGridLayout) mOnlineContainer).setCellHeight(adviceHeight);
        ((FixedGridLayout) mOnlineContainer).setCellWidth(adviceWidth);
        ViewGroup adviceParent = (ViewGroup) findViewById(Res.id.advice_parent);
        adviceParent.addView(mOnlineContainer);
        adviceParent.setClipChildren(false);
    }

    private void initAddCustomButton() {
        final RelativeLayout mWallpaperRl = (RelativeLayout) LayoutInflater.from(
                HDApplication.getContext()).inflate(Res.layout.pandora_wallpaper_item, null);
        RelativeLayout mWallpaperIvRl = (RelativeLayout) mWallpaperRl
                .findViewById(Res.id.pandora_wallpaper_item_iamge_rl);
        ImageView mWallpaperIv = (ImageView) mWallpaperRl
                .findViewById(Res.id.pandora_wallpaper_item_iamge);
        mWallpaperRl.findViewById(Res.id.pandora_wallpaper_item_select).setVisibility(View.GONE);
        mWallpaperRl.findViewById(Res.id.pandora_wallpaper_item_delete).setVisibility(View.GONE);
        mWallpaperIv.setImageDrawable(getResources().getDrawable(Res.drawable.pandora_wallpaper_add));
        mWallpaperIv.setBackgroundResource(Res.drawable.setting_wallpaper_add_button_selector);
        mWallpaperIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCustomContainer.getChildCount() >= 6) {
                    // TODO toast
                    Toast.makeText(WallPaperActivity.this, "壁纸数目已经到达上限，请删除部分不需要的壁纸!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                showSelectDialog();
            }
        });
        mCustomContainer.addView(mWallpaperRl, Math.min(1, mCustomContainer.getChildCount()));
        LayoutParams params = mWallpaperIvRl.getLayoutParams();
        int width = (int) getResources().getDimension(Res.dimen.pandora_wallpaper_width);
        int height = (int) getResources().getDimension(Res.dimen.pandora_wallpaper_height);
        params.width = width;
        params.height = height;
        mWallpaperIvRl.setLayoutParams(params);

        LayoutParams layoutParams = mWallpaperRl.getLayoutParams();
        int layoutWidth = (int) getResources().getDimension(Res.dimen.pandora_wallpaper_layout_width);
        int layoutHeight = (int) getResources().getDimension(
                Res.dimen.pandora_wallpaper_layout_height);
        layoutParams.width = layoutWidth;
        layoutParams.height = layoutHeight;
        mWallpaperRl.setLayoutParams(layoutParams);

        LayoutParams layoutParamIV = mWallpaperIv.getLayoutParams();
        int widthIv = (int) getResources().getDimension(Res.dimen.pandora_wallpaper_width);
        int heightIv = (int) getResources().getDimension(Res.dimen.pandora_wallpaper_height);
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
                Drawable curDrawable = ThemeManager.getCurrentTheme().getCurDrawable();
                setBackground(curDrawable);
                String fileName = PandoraUtils.getRandomString();
                saveCurWallpaperFileName(fileName);
                saveCustomWallpaperFile(fileName);
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

    /**
     * @param bitmap
     * @param resId 资源文件ID
     */
    private void setBackground(Drawable drawable) {
        Drawable rootViewDrawable = mRootView.getBackground();
        if (null != rootViewDrawable) {
            setAnimator(drawable);
        } else {
            mRootView.setBackgroundDrawable(drawable);
        }
    }

    private void setAnimator(final Drawable drawable) {
        ObjectAnimator animatorAlphaInvisible = ObjectAnimator.ofInt(mRootView.getBackground(),
                "alpha", 255, 100);
        animatorAlphaInvisible.setDuration(250);
        animatorAlphaInvisible.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                mRootView.setBackgroundDrawable(drawable);
                ObjectAnimator animatorAlphaVisible = ObjectAnimator.ofInt(drawable, "alpha", 100,
                        255);
                animatorAlphaVisible.setDuration(250);
                animatorAlphaVisible.start();
            }
        });
        animatorAlphaInvisible.start();
    }

    private void saveCustomWallpaperFile(final String fileName) {
        HDBThreadUtils.runOnWorker(new Runnable() {

            @Override
            public void run() {
                CustomWallpaperManager.getInstance().mkDirs();
                Drawable curDrawable = ThemeManager.getCurrentTheme().getCurDrawable();
                ImageUtils.saveImageToFile(ImageUtils.drawable2Bitmap(curDrawable),
                        CustomWallpaperManager.getInstance().getFilePath(fileName));
                if (mHandler.hasMessages(MSG_INSERT_WALLPAPER_ITEM)) {
                    mHandler.removeMessages(MSG_INSERT_WALLPAPER_ITEM);
                }
                Message message = Message.obtain();
                message.what = MSG_INSERT_WALLPAPER_ITEM;
                message.obj = fileName;
                mHandler.sendMessage(message);
            }
        });
    }

    private void gotoCropActivity(Uri uri) {
        int mAspectRatioX = 0;
        int mAspectRatioY = 0;
        int width = BaseInfoHelper.getRealWidth(this);
        int height = Integer.parseInt(BaseInfoHelper.getHeight(this));
        if (width >= height) {
            mAspectRatioX = 100;
            mAspectRatioY = (mAspectRatioX * height) / width;
        }
        if (height >= width) {
            mAspectRatioY = 100;
            mAspectRatioX = (mAspectRatioY * width) / height;
        }
        PandoraUtils.gotoCropActivity(this, uri, mAspectRatioX, mAspectRatioY, true);
    }

    private void saveCurWallpaperFileName(String fileName) {
        if (mHandler.hasMessages(MSG_SAVE_CURRENT_WALLPAPER_FILENAME)) {
            mHandler.removeMessages(MSG_SAVE_CURRENT_WALLPAPER_FILENAME);
        }
        Message message = Message.obtain();
        message.what = MSG_SAVE_CURRENT_WALLPAPER_FILENAME;
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
                case MSG_SAVE_CURRENT_WALLPAPER_FILENAME:
                    ((WallPaperActivity) activity).saveCurrentWallpaperFileName((String) msg.obj);
                    break;
                case MSG_INSERT_WALLPAPER_ITEM:
                    ((WallPaperActivity) activity).setWallpaperItem((String) msg.obj);
                    ((WallPaperActivity) activity).markSelectState((String) msg.obj);
                    break;

            }
            super.handleMessage(msg);
        }
    }

    private void saveCurrentWallpaperFileName(String fileName) {
        PandoraConfig.newInstance(this).saveCurrentWallpaperFileName(fileName);
    }

    public void saveThemeId(int themeId) {
        ThemeManager.saveTheme(themeId);
    }

    public void setWallpaperItem(String fileName) {
        CustomWallpaperManager.getInstance().setCustomWallpaperItem(WallPaperActivity.this,
                mCustomContainer, fileName, true, this, mPandoraWallpaperList);
        UmengCustomEventManager.statisticalSelectLockScreenWallpaperCount(fileName);
    }

    protected void showDelDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.setContentView(Res.layout.pandora_dialog);
        dialog.show();

    }

    private void initTransition() {
        final LayoutTransition transitioner = new LayoutTransition();
        mCustomContainer.setLayoutTransition(transitioner);
        mOnlineContainer.setLayoutTransition(transitioner);
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
        overridePendingTransition(Res.anim.umeng_fb_slide_in_from_left,
                Res.anim.umeng_fb_slide_out_from_right);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WallPaperActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClickListener(final String fileName, final String filePath, final boolean isCustom) {
        markSelectState(fileName);
        WallpaperUtils.loadBackgroundBitmap(this, filePath, new ILoadBitmapCallback() {

            @Override
            public void imageLoaded(Bitmap bitmap, String filePath) {
                if (isCustom) {
                    saveThemeId(ThemeManager.THEME_ID_CUSTOM);
                } else {
                    saveThemeId(ThemeManager.THEME_ID_ONLINE);
                }
                ThemeManager.addBitmapToCache(bitmap);
                saveCurrentWallpaperFileName(fileName);
                setBackground(new BitmapDrawable(getResources(), bitmap));
            }
        });
    }

    @Override
    public void onDelClickListener(String fileName, final String filePath) {
        delPandoraWallpaperListItem(fileName);
        String currentFileName = PandoraConfig.newInstance(this).getCurrentWallpaperFileName();
        if (fileName.equals(currentFileName)) {
            PandoraConfig.newInstance(this).saveCurrentWallpaperFileName("");
            PandoraConfig.newInstance(this).saveThemeId(ThemeManager.THEME_ID_DEFAULT);
            ThemeManager.invalidateBitmapCache();
            markSelectState(fileName);
            Theme theme = ThemeManager.getCurrentTheme();
            setBackground(theme.getCurDrawable());
        }
        HDBThreadUtils.runOnWorker(new Runnable() {

            @Override
            public void run() {
                FileHelper.deleteFile(new File(filePath));
            }
        });
    }

    private void delPandoraWallpaperListItem(String fileName) {
        PandoraWallpaper pandoraWallpaper = new PandoraWallpaper();
        for (PandoraWallpaper wallpaper : mPandoraWallpaperList) {
            if (fileName.equals(wallpaper.getFileName())) {
                pandoraWallpaper = wallpaper;
            }
        }
        mPandoraWallpaperList.remove(pandoraWallpaper);
    }

    private void markSelectState(String fileName) {
        for (PandoraWallpaper wallpaper : mPandoraWallpaperList) {
            if (wallpaper.getFileName().equals(fileName)) {
                wallpaper.setCurrentWallpaper(true);
                wallpaper.getImageView().setVisibility(View.VISIBLE);
                createSelectStateAnimations(wallpaper.getImageView());
            } else {
                wallpaper.setCurrentWallpaper(false);
                wallpaper.getImageView().setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 标记当前选中壁纸状态
     */
    private void markSelectState() {
        for (PandoraWallpaper wallpaper : mPandoraWallpaperList) {
            if (wallpaper.isCurrentWallpaper()) {
                wallpaper.getImageView().setVisibility(View.VISIBLE);
                createSelectStateAnimations(wallpaper.getImageView());
            } else {
                wallpaper.getImageView().setVisibility(View.INVISIBLE);
            }
        }
    }

    private void createSelectStateAnimations(final View view) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1.1f);
        scaleXAnimator.setDuration(250);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1.1f);
        scaleYAnimator.setDuration(250);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                ObjectAnimator resetScaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f);
                resetScaleXAnimator.setDuration(250);

                ObjectAnimator resetScaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f);
                resetScaleYAnimator.setDuration(250);
                AnimatorSet resetAnimatorSet = new AnimatorSet();
                resetAnimatorSet.playTogether(resetScaleXAnimator, resetScaleYAnimator);
                resetAnimatorSet.start();
            }
        });
        animatorSet.start();
    }
}
