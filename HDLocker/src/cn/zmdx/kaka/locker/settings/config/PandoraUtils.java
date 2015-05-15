
package cn.zmdx.kaka.locker.settings.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Toast;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;

public class PandoraUtils {
    private PandoraUtils() {

    }

    public static final String MUIU_V5 = "V5";

    public static final String MUIU_V6 = "V6";

    public static final int REQUEST_CODE_CROP_IMAGE = 0;

    public static final int REQUEST_CODE_GALLERY = 1;

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
        return "Xiaomi".equals(manufacturer) || !TextUtils.isEmpty(getSystemProperty());
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
                i.setClassName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity");
                mContent.startActivity(i);
            }
        } catch (Exception e) {
            Toast.makeText(mContent, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    public static void setAllowReadNotification(Context mContext, boolean isMIUI, String version,
            boolean isMeizu) {
        if (isMIUI) {
            setMIUIAllowReadNotification(mContext, version);
        } else if (isMeizu) {
            setMeizuAllowReadNotification(mContext);
        } else {
            setRegularAllowReadNotification(mContext);
        }
    }

    private static void setMIUIAllowReadNotification(Context mContext, String version) {
        try {
            if (version.equals(MUIU_V5)) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Intent i = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    mContext.startActivity(i);
                } else {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    mContext.startActivity(intent);
                }
            } else if (version.equals(MUIU_V6)) {
                Intent i = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                mContext.startActivity(i);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private static void setMeizuAllowReadNotification(Context mContext) {
        try {
            if (Build.VERSION.SDK_INT >= 18) {
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                mContext.startActivity(intent);
            } else {
                Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
                mContext.startActivity(intent);
            }

        } catch (Exception e) {
            Toast.makeText(mContext, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private static void setRegularAllowReadNotification(Context mContext) {
        try {
            if (Build.VERSION.SDK_INT >= 18) {
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                mContext.startActivity(intent);
            } else {
                Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    public static void launchReadNotificationPermissionActivity(Context context) {
        Intent intent = getReadNotificationPermissionIntent(context);
        context.startActivity(intent);
    }

    public static Intent getReadNotificationPermissionIntent(Context context) {
        Intent intent = null;
        final boolean meizu = isMeizu(context);
        final boolean miui = isMIUI(context);
        if (miui) {
            String miuiVersion = PandoraUtils.getSystemProperty();
            if (miuiVersion.equals(MUIU_V5)) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                } else {
                    // TODO
                    intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                }
            } else if (miuiVersion.equals(MUIU_V6)) {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
        } else if (meizu) {
            if (Build.VERSION.SDK_INT >= 18) {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            } else {
                intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            } else {
                if (BuildConfig.DEBUG) {
                    throw new IllegalStateException("只有4.3及以上设备才具备读取通知的功能");
                }
            }
        }
        return intent;
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

    /**
     * 获取application中指定的meta-data
     * 
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = "";
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                        ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }

    public static final int OP_SYSTEM_ALERT_WINDOW = 24;// 悬浮窗权限

    public static boolean isMiuiFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            return checkOp(context, OP_SYSTEM_ALERT_WINDOW);
        } else {
            if (PandoraConfig.newInstance(context).isMIUILow19Prompt()) {
                return true;
            } else {
                PandoraConfig.newInstance(context).saveMIUILow19PromptState(true);
                return false;
            }
        }
    }

    private static boolean checkOp(Context context, int op) {
        if (AppOpsManager.MODE_ALLOWED == invokeMethod(context, op)) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    private static int invokeMethod(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            Object object = context.getSystemService("appops");
            Class c = object.getClass();
            try {
                Class[] cArg = new Class[3];
                cArg[0] = int.class;
                cArg[1] = int.class;
                cArg[2] = String.class;
                Method method = c.getDeclaredMethod("checkOp", cArg);
                return (Integer) method.invoke(object, op, Binder.getCallingUid(),
                        context.getPackageName());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }
}
