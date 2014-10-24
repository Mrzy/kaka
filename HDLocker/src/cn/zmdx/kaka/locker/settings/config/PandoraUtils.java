
package cn.zmdx.kaka.locker.settings.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Toast;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.IndividualizationActivity;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.FileHelper;
import cn.zmdx.kaka.locker.utils.ImageUtils;

public class PandoraUtils {
    private PandoraUtils() {

    }

    private static final int DEFAULT_WIDTH = 1080;

    private static final int DEFAULT_BITMAP_WIDTH = 3000;

    public static final String MUIU_V5 = "V5";

    public static final String MUIU_V6 = "V6";

    public static Bitmap sCropBitmap;

    public static Bitmap sCropThumbBitmap;

    public static Bitmap sLockDefaultThumbBitmap;

    public static final int REQUEST_CODE_CROP_IMAGE = 0;

    public static final int REQUEST_CODE_GALLERY = 1;

    public static final int TIME_MORNING = 8;

    public static final int TIME_MORNING_WORK = 11;

    public static final int TIME_AFTERNOON = 13;

    public static final int TIME_AFTERNOON_WORK = 17;

    public static final int TIME_EVENING = 19;

    public static final int TIME_EVENING_WORK = 0;

    public static final int TIME_EVENING_WORK_24 = 24;

    public static Bitmap fastBlur(View decorView) {
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap bitmap = decorView.getDrawingCache();
        return mBlur(bitmap, decorView);

    }

    private static Bitmap mBlur(Bitmap bkg, View view) {
        float scaleFactor = 8;
        float radius = 20;
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        return FastBlur.doBlur(overlay, (int) radius, true);
    }

    public static void closeSystemLocker(Context context, boolean isMIUI) {
        try {
            if (isMIUI) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent("/");
                ComponentName cm = new ComponentName("com.android.settings",
                        "com.android.settings.ChooseLockGeneric");
                intent.setComponent(cm);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isMIUI(Context context) {
        String manufacturer = android.os.Build.MANUFACTURER;
        return "Xiaomi".equals(manufacturer);
    }

    public static boolean isMeizu(Context context) {
        String manufacturer = android.os.Build.MANUFACTURER;
        return "Meizu".equals(manufacturer);
    }

    @SuppressWarnings("unused")
    private static boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }

    public static String getSystemProperty() {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + "ro.miui.ui.version.name");
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }

    public static void setAllowFolatWindow(Context mContent, String version) {
        if (version.equals(MUIU_V5)) {
            Uri packageURI = Uri.parse("package:" + "cn.zmdx.kaka.locker");
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            mContent.startActivity(intent);
        } else if (version.equals(MUIU_V6)) {
            Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
            i.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            i.putExtra("extra_pkgname", mContent.getPackageName());
            mContent.startActivity(i);

        }
    }

    public static void setTrust(Context mContent, String version) {
        if (version.equals(MUIU_V5)) {
            PackageManager pm = mContent.getPackageManager();
            PackageInfo info = null;
            try {
                info = pm.getPackageInfo(mContent.getPackageName(), 0);

                Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
                i.setClassName("com.android.settings",
                        "com.miui.securitycenter.permission.AppPermissionsEditor");
                i.putExtra("extra_package_uid", info.applicationInfo.uid);
                mContent.startActivity(i);
            } catch (NameNotFoundException e1) {
                e1.printStackTrace();
            }
        } else if (version.equals(MUIU_V6)) {
            Intent i = new Intent();
            i.setClassName("com.miui.securitycenter", "com.miui.securitycenter.MainActivity");
            mContent.startActivity(i);
        }

    }

    /**
     * 获得程序版本号
     * 
     * @return
     */
    public static String getVersionCode(Context context) {
        String versionName = "v1.0.0";
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    public static int getVirtualKeyHeight(Activity activity) {
        int screenHeight = 0;
        int realScreenHeight = 0;
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        screenHeight = dm.heightPixels;
        Class<?> c;
        try {
            c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            realScreenHeight = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realScreenHeight - screenHeight;
    }

    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    public static String getWeekString(Context mContext, int weekInt) {
        String weekString = "";
        switch (weekInt) {
            case Calendar.MONDAY:
                weekString = mContext.getResources().getString(R.string.lock_week_monday);
                break;
            case Calendar.TUESDAY:
                weekString = mContext.getResources().getString(R.string.lock_week_tuesday);
                break;
            case Calendar.WEDNESDAY:
                weekString = mContext.getResources().getString(R.string.lock_week_wednesday);
                break;
            case Calendar.THURSDAY:
                weekString = mContext.getResources().getString(R.string.lock_week_thursday);
                break;
            case Calendar.FRIDAY:
                weekString = mContext.getResources().getString(R.string.lock_week_friday);
                break;
            case Calendar.SATURDAY:
                weekString = mContext.getResources().getString(R.string.lock_week_saturday);
                break;
            case Calendar.SUNDAY:
                weekString = mContext.getResources().getString(R.string.lock_week_sunday);
                break;

            default:
                break;
        }

        return weekString;
    }

    public static String getRandomString() {
        return UUID.randomUUID().toString();
    }

    public static void gotoCaptureActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void gotoGalleryActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    public static Bitmap zoomBitmap(Activity activity, Uri uri) throws FileNotFoundException {
        InputStream inputStream = activity.getContentResolver().openInputStream(uri);
        BitmapFactory.Options opts = new Options();
        opts.inJustDecodeBounds = true;// 设置为true时，BitmapFactory只会解析要加载的图片的边框的信息，但是不会为该图片分配内存
        BitmapFactory.decodeStream(inputStream, new Rect(), opts);
        int screenHeight = BaseInfoHelper.getRealHeight(activity);
        int screenWidth = BaseInfoHelper.getWidth(activity);
        BitmapFactory.Options realOpts = new Options();
        if (screenWidth == DEFAULT_WIDTH && opts.outWidth > DEFAULT_BITMAP_WIDTH) {
            realOpts.inSampleSize = 3;
        } else {
            realOpts.inSampleSize = computeSampleSize(opts, Math.min(screenWidth, screenHeight),
                    screenWidth * screenHeight);
        }
        realOpts.inJustDecodeBounds = false;
        realOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        realOpts.inPurgeable = true;
        realOpts.inInputShareable = true;
        InputStream realInputStream = activity.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(realInputStream, new Rect(), realOpts);
        return bitmap;

    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
            int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
            int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h
                / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static Bitmap zoomThumbBitmap(Context context, Bitmap cropBitmap, boolean isWallpaper) {
        int thumbWidth = 0;
        int thumbHeight = 0;
        if (isWallpaper) {
            thumbWidth = (int) context.getResources().getDimension(R.dimen.pandora_wallpaper_width);
            thumbHeight = (int) context.getResources().getDimension(
                    R.dimen.pandora_wallpaper_height);
        } else {
            thumbWidth = BaseInfoHelper.getWidth(context);
            thumbHeight = (int) (thumbWidth / (LockScreenManager.getInstance()
                    .getBoxWidthHeightRate()));
        }
        return ImageUtils.scaleTo(cropBitmap, thumbWidth, thumbHeight, false);
    }

    public static void saveBitmap(Bitmap bitmap, String path, String fileName) {
        try {
            File dirFile = new File(path);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File file = new File(path + fileName + ".jpg");
            file.createNewFile();
            FileOutputStream out;
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmap(String path) {
        return ImageUtils.getBitmapFromFile(path);
    }

    public static boolean isHaveCustomWallpaper(Context context) {
        return !TextUtils.isEmpty(PandoraConfig.newInstance(context).getCustomWallpaperFileName());
    }

    public static void deleteFile(String dirName, String fileName) {
        FileHelper.deleteFile(dirName, fileName + ".jpg");
    }

    public static void deleteFile(File file) {
        FileHelper.deleteFile(file);
    }

    public static BitmapDrawable getLockDefaultBitmap(Context context) {
        String fileName = PandoraConfig.newInstance(context).getLockDefaultFileName();
        String path = IndividualizationActivity.LOCK_DEFAULT_SDCARD_LOCATION + fileName + ".jpg";
        Bitmap bitmap = PandoraUtils.getBitmap(path);
        BitmapDrawable drawable = null;
        if (null != bitmap) {
            drawable = new BitmapDrawable(context.getResources(), bitmap);
        }
        return drawable;
    }

    private static int getTimeQuantum(int currentHour) {
        if (currentHour <= TIME_MORNING && currentHour > TIME_MORNING) {
            return TIME_MORNING;
        } else if (currentHour < TIME_MORNING_WORK && currentHour > TIME_MORNING) {
            return TIME_MORNING_WORK;
        } else if (currentHour < TIME_AFTERNOON && currentHour >= TIME_MORNING_WORK) {
            return TIME_AFTERNOON;
        } else if (currentHour < TIME_AFTERNOON_WORK && currentHour >= TIME_AFTERNOON) {
            return TIME_AFTERNOON_WORK;
        } else if (currentHour < TIME_EVENING && currentHour >= TIME_AFTERNOON_WORK) {
            return TIME_EVENING;
        } else if (currentHour < TIME_EVENING_WORK_24 && currentHour >= TIME_EVENING) {
            return TIME_EVENING_WORK;
        } else {
            return TIME_MORNING;
        }
    }

    public static String getTimeQuantumString(Context mContext, int currentHour) {
        String promptString = "";
        int currentQuantum = getTimeQuantum(currentHour);
        switch (currentQuantum) {
            case TIME_MORNING:
                promptString = mContext.getResources().getString(
                        R.string.individualization_welcome_text_default_morning);
                break;
            case TIME_MORNING_WORK:
                promptString = mContext.getResources().getString(
                        R.string.individualization_welcome_text_default_morning_work);
                break;
            case TIME_AFTERNOON:
                promptString = mContext.getResources().getString(
                        R.string.individualization_welcome_text_default_afternoon);
                break;
            case TIME_AFTERNOON_WORK:
                promptString = mContext.getResources().getString(
                        R.string.individualization_welcome_text_default_afternoon_work);
                break;
            case TIME_EVENING:
                promptString = mContext.getResources().getString(
                        R.string.individualization_welcome_text_default_evening);
                break;
            case TIME_EVENING_WORK:
                promptString = mContext.getResources().getString(
                        R.string.individualization_welcome_text_default_evening_work);
                break;

            default:
                break;
        }
        return promptString;
    }
}
