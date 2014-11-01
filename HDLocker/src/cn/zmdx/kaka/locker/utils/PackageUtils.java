/**
 * Copyright (c) Tapas Mobile.  All Rights Reserved.
 *
 * @author Qiongzhu Wan <qiongzhu@dianxinos.com / qiongzhu@gmail.com>
 * @version 1.0
 */

package cn.zmdx.kaka.locker.utils;

import java.io.File;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

public class PackageUtils {
    static int sMyVersionCode = -1;
    static String sMyversionName = null;
    static PackageManager sPackageManager = null;
    static String sSelfBuildTime = null;

    private static void getMyversion(Context context) {
        if (sMyVersionCode < 0 || sMyversionName == null) {

            try {
                PackageInfo info = sPackageManager.getPackageInfo(getPackageName(context),
                        PackageManager.GET_UNINSTALLED_PACKAGES);
                sMyversionName = info.versionName;
                sMyVersionCode = info.versionCode;
            } catch (Exception e) {
            }
        }
    }

    public static String getPackageName(Context context) {
        return context.getApplicationContext().getPackageName();
    }

    public static int getVersionCode() {
        return sMyVersionCode;
    }

    public static int getVersionCode(String pkgName) {
        try {
            PackageInfo info = sPackageManager.getPackageInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return info.versionCode;
        } catch (Exception e) {
        }

        return -1;
    }

    public static String getVersionName() {
        return sMyversionName;
    }

    public static String getVersionName(String pkgName) {
        try {
            PackageInfo info = sPackageManager.getPackageInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return info.versionName;
        } catch (Exception e) {
        }

        return "";
    }

    public static long guessBuildTime(String pkgName) {
        try {
            ApplicationInfo ai = sPackageManager.getApplicationInfo(pkgName, 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            return ze.getTime();
        } catch (Exception e) {
        }

        return -1;
    }

    public static boolean isLegacyDevice() {
        return Build.VERSION_CODES.GINGERBREAD_MR1 >= Build.VERSION.SDK_INT;
    }

    public static boolean installPackage(Context context, String path) {
        return installPackage(context, path, null);
    }

    public static boolean installPackage(Context context, String path, String installerPackage) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(path);
            if (!file.exists())
                return false;
            Uri uri = Uri.fromFile(file);
            String type = "application/vnd.android.package-archive";
            intent.setDataAndType(uri, type);
            if (installerPackage != null) {
                intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, installerPackage);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isPkgInstalled(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isActivityInstalled(Context context, String packageName, String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, className));
        List<ResolveInfo> activitys = context.getPackageManager().queryIntentActivities(intent, PackageManager.GET_META_DATA);
        return activitys.size() > 0;
    }

    public static boolean isReceiverInstalled(Context context, String packageName, String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, className));
        List<ResolveInfo> broadcastReceivers = context.getPackageManager().queryBroadcastReceivers(intent, PackageManager.GET_META_DATA);
        return broadcastReceivers.size() > 0;
    }

    public static boolean isPkgInstalledByAction(Context context, String pkgName,
            String action) {
        try {
            Intent intent = new Intent(action);
            if (pkgName != null) {
                intent.setPackage(pkgName);
            }
            List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(intent, 0);
            return apps != null && apps.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPkgInstalledByIntent(Context context,
            Intent intent) {
        try {
            List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(intent, 0);
            return apps != null && apps.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
