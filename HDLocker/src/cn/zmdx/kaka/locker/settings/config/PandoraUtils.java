
package cn.zmdx.kaka.locker.settings.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

public class PandoraUtils {
    private PandoraUtils() {

    }

    private static final String MUIU_V5 = "V5";

    private static final String MUIU_6 = "6";

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
    }

    public static boolean isMIUI(Context context) {
        boolean result = false;
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.android.settings",
                "com.miui.securitycenter.permission.AppPermissionsEditor");
        if (isIntentAvailable(context, intent)) {
            result = true;
        }
        return result;
    }

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
            Log.e("syc", "Unable to read sysprop " + "ro.miui.ui.version.name", ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e("syc", "Exception while closing InputStream", e);
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
        } else if (version.equals(MUIU_6)) {

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
        } else if (version.equals(MUIU_6)) {

        }

    }
}
