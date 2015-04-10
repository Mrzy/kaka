
package com.yepstudio.android.library.feedback.uninstall;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class AppUninstall {

    static {
        System.loadLibrary("feedback-uninstall");
    }

    public static void openUrlWhenUninstall(Context context, String openUrl) {
        String dirStr = context.getApplicationInfo().dataDir;
        String activity = null;

        Intent intent = new Intent();
        ComponentName[] activities = new ComponentName[] {
                new ComponentName("com.android.chrome", "com.google.android.apps.chrome.Main"),
                new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"),
                new ComponentName("com.UCMobile", "com.UCMobile.main.UCMobile"),
                new ComponentName("com.tencent.mtt", "com.tencent.mtt.SplashActivity"),
                new ComponentName("com.qihoo.browser", "com.qihoo.browser.BrowserActivity"),
                new ComponentName("com.baidu.browser.apps",
                        "com.baidu.browser.framework.BdBrowserActivity"),
                new ComponentName("sogou.mobile.explorer", "sogou.mobile.explorer.BrowserActivity"),
                new ComponentName("com.ijinshan.browser_fast",
                        "com.ijinshan.browser.screen.BrowserActivity")
        };
        for (ComponentName cn : activities) {
            intent.setComponent(cn);
            List<ResolveInfo> result = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (result.size() > 0) {
                activity = cn.getPackageName() + "/" + cn.getClassName();
                break;
            }
        }

        String action = "android.intent.action.VIEW";
        String data = openUrl;
        if (activity != null) {
            onUninstall(dirStr, activity, action, data);
        }
    }

    public static void startActionWhenUninstall(Context context, String action, String data) {
        String dirStr = context.getApplicationInfo().dataDir;
        onUninstall(dirStr, null, action, data);
    }

    public static void startActivityWhenUninstall(Context context, String packageName,
            String activityName) {
        String dirStr = context.getApplicationInfo().dataDir;
        onUninstall(dirStr, String.format("%s/%s", packageName, activityName), null, null);
    }

    private static native void onUninstall(String dirStr, String activity, String action,
            String data);

}
