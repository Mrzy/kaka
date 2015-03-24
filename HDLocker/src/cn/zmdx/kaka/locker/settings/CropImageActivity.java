
package cn.zmdx.kaka.locker.settings;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.wallpaper.CustomWallpaperManager;
import cn.zmdx.kaka.locker.widget.BaseButton;

public class CropImageActivity extends Activity implements OnClickListener {

    private ImageView mImage;

    private BaseButton mBackButton;

    private BaseButton mApplyButton;

    private Bitmap mCropBitmap;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 19) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_crop_image);
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
        initView();
    }

    private void initView() {
        mImage = (ImageView) findViewById(R.id.crop_image);
        mImage.setImageBitmap(mCropBitmap);
        mBackButton = (BaseButton) findViewById(R.id.crop_image_return);
        mBackButton.setOnClickListener(this);
        mApplyButton = (BaseButton) findViewById(R.id.crop_image_apply);
        mApplyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mBackButton) {
            setResult(Activity.RESULT_CANCELED);
        } else if (view == mApplyButton) {
            UmengCustomEventManager.statisticalSuccessSetCustomTimes();
            ThemeManager.saveTheme(ThemeManager.THEME_ID_CUSTOM);
            ThemeManager.addBitmapToCache(mCropBitmap);
            saveBitmapForWallpaper();
            setResult(Activity.RESULT_OK);
        }
        onBackPressed();
    }

    private void saveBitmapForWallpaper() {
        String fileName = PandoraUtils.getRandomString();
        PandoraConfig.newInstance(this).saveCurrentWallpaperFileName(fileName);
        CustomWallpaperManager.getInstance().mkDirs();
        Drawable curDrawable = ThemeManager.getCurrentTheme().getCurDrawable();
        ImageUtils.saveImageToFile(ImageUtils.drawable2Bitmap(curDrawable), CustomWallpaperManager
                .getInstance().getFilePath(fileName));
        LockScreenManager.getInstance().lock();
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NewCropImageActivity");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NewCropImageActivity");
        MobclickAgent.onPause(this);
    }
}
