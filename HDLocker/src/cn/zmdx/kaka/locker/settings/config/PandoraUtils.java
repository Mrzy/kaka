
package cn.zmdx.kaka.locker.settings.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Toast;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.CropImageActivity;
import cn.zmdx.kaka.locker.settings.IndividualizationActivity;
import cn.zmdx.kaka.locker.utils.ImageUtils;

public class PandoraUtils {
    private PandoraUtils() {

    }

    public static final String MUIU_V5 = "V5";

    public static final String MUIU_V6 = "V6";

    public static Bitmap sCropBitmap;

    public static Bitmap sLockDefaultThumbBitmap;

    public static final int REQUEST_CODE_CROP_IMAGE = 0;

    public static final int REQUEST_CODE_GALLERY = 1;

    public static final int WARM_PROMPT_6 = 6;

    public static final int WARM_PROMPT_10 = 10;

    public static final int WARM_PROMPT_12 = 12;

    public static final int WARM_PROMPT_13 = 13;

    public static final int WARM_PROMPT_15 = 15;

    public static final int WARM_PROMPT_17 = 17;

    public static final int WARM_PROMPT_19 = 19;

    public static final int WARM_PROMPT_23 = 23;

    public static final int WARM_PROMPT_1 = 1;

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
        String line = "";
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + "ro.miui.ui.version.name");
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return line;
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
        try {
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
        } catch (Exception e) {
            Toast.makeText(mContent, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    public static void setTrust(Context mContent, String version) {
        try {
            if (version.equals(MUIU_V5)) {
                PackageManager pm = mContent.getPackageManager();
                PackageInfo info = null;
                info = pm.getPackageInfo(mContent.getPackageName(), 0);

                Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
                i.setClassName("com.android.settings",
                        "com.miui.securitycenter.permission.AppPermissionsEditor");
                i.putExtra("extra_package_uid", info.applicationInfo.uid);
                mContent.startActivity(i);
            } else if (version.equals(MUIU_V6)) {
                Intent i = new Intent();
                i.setClassName("com.miui.securitycenter", "com.miui.securitycenter.MainActivity");
                mContent.startActivity(i);
            }
        } catch (Exception e) {
            Toast.makeText(mContent, R.string.error, Toast.LENGTH_SHORT).show();
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
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void gotoGalleryActivity(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        // 根据版本号不同使用不同的Action
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    public static void gotoCropActivity(Activity activity, Uri uri, int mAspectRatioX,
            int mAspectRatioY, boolean isWallpaper) {
        Intent intent = new Intent();
        intent.setClass(activity, CropImageActivity.class);
        intent.setData(uri);
        Bundle bundle = new Bundle();
        bundle.putInt(CropImageActivity.KEY_BUNDLE_ASPECTRATIO_X, mAspectRatioX);
        bundle.putInt(CropImageActivity.KEY_BUNDLE_ASPECTRATIO_Y, mAspectRatioY);
        bundle.putBoolean(CropImageActivity.KEY_BUNDLE_IS_WALLPAPER, isWallpaper);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, PandoraUtils.REQUEST_CODE_CROP_IMAGE);
        activity.overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    public static BitmapDrawable getLockDefaultBitmap(Context context) {
        String fileName = PandoraConfig.newInstance(context).getLockDefaultFileName();
        String path = IndividualizationActivity.LOCK_DEFAULT_SDCARD_LOCATION + fileName + ".jpg";
        Bitmap bitmap = ImageUtils.getBitmapFromFile(path, null);
        BitmapDrawable drawable = null;
        if (null != bitmap) {
            drawable = new BitmapDrawable(context.getResources(), bitmap);
        }
        return drawable;
    }

    private static int getTimeQuantum(int currentHour) {
        if (currentHour < WARM_PROMPT_10 && currentHour >= WARM_PROMPT_6) {
            return WARM_PROMPT_6;
        } else if (currentHour < WARM_PROMPT_12 && currentHour >= WARM_PROMPT_10) {
            return WARM_PROMPT_10;
        } else if (currentHour < WARM_PROMPT_13 && currentHour >= WARM_PROMPT_12) {
            return WARM_PROMPT_12;
        } else if (currentHour < WARM_PROMPT_15 && currentHour >= WARM_PROMPT_13) {
            return WARM_PROMPT_13;
        } else if (currentHour < WARM_PROMPT_17 && currentHour >= WARM_PROMPT_15) {
            return WARM_PROMPT_15;
        } else if (currentHour < WARM_PROMPT_19 && currentHour >= WARM_PROMPT_17) {
            return WARM_PROMPT_17;
        } else if (currentHour < WARM_PROMPT_23 && currentHour >= WARM_PROMPT_19) {
            return WARM_PROMPT_19;
        } else if (currentHour < WARM_PROMPT_1 || currentHour >= WARM_PROMPT_23) {
            return WARM_PROMPT_23;
        } else if (currentHour < WARM_PROMPT_6 || currentHour >= WARM_PROMPT_1) {
            return WARM_PROMPT_1;
        } else {
            return WARM_PROMPT_6;
        }
    }

    public static String getTimeQuantumString(Context mContext, int currentHour) {
        String promptString = "";
        int currentQuantum = getTimeQuantum(currentHour);
        int random = (int) Math.round(Math.random());
        switch (currentQuantum) {
            case WARM_PROMPT_6:
                if (random == 0) {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_6_10_1);
                } else {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_6_10_2);
                }
                break;
            case WARM_PROMPT_10:
                if (random == 0) {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_10_12_1);
                } else {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_10_12_2);
                }
                break;
            case WARM_PROMPT_12:
                if (random == 0) {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_12_13_1);
                } else {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_12_13_2);
                }
                break;
            case WARM_PROMPT_13:
                if (random == 0) {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_13_15_1);
                } else {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_13_15_2);
                }
                break;
            case WARM_PROMPT_15:
                if (random == 0) {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_15_17_1);
                } else {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_15_17_2);
                }
                break;
            case WARM_PROMPT_17:
                if (random == 0) {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_17_19_1);
                } else {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_17_19_2);
                }
                break;
            case WARM_PROMPT_19:
                if (random == 0) {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_19_23_1);
                } else {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_19_23_2);
                }
                break;

            case WARM_PROMPT_23:
                if (random == 0) {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_23_1_1);
                } else {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_23_1_2);
                }
                break;
            case WARM_PROMPT_1:
                if (random == 0) {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_1_6_1);
                } else {
                    promptString = mContext.getResources().getString(R.string.warm_prompt_1_6_2);
                }
                break;

            default:
                break;
        }
        return promptString;
    }

    public static boolean isHaveFile(String path) {
        boolean isHave = false;
        try {
            File file = new File(path);
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                isHave = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isHave;
    }

}
