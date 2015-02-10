
package cn.zmdx.kaka.fast.locker.shortcut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import cn.zmdx.kaka.fast.locker.notification.Constants;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;

public class ShortcutUtil {

    public static List<AppInfo> initDefaultShotrcutInfo(Context context) {
        List<AppInfo> data = new ArrayList<AppInfo>();
        List<String> defaultPkgName = new ArrayList<String>();
        String locale = BaseInfoHelper.getLocale(context);
//        if (locale.equals(Locale.CHINA.toString())) {
            if (isAvilible(context, Constants.PKGNAME_WEIXIN)) {
                defaultPkgName.add(Constants.PKGNAME_WEIXIN);
            }
            if (isAvilible(context, Constants.PKGNAME_QQ)) {
                defaultPkgName.add(Constants.PKGNAME_QQ);
            }

            defaultPkgName.add(getSystemDialerPkgName(context));

            defaultPkgName.add(getBrowserApp(context));
            addRecentTasksPkgName(context, defaultPkgName);

            if (defaultPkgName.size() < 6) {
                defaultPkgName.add(getMusicApp(context));
            }

            for (int i = 0; i < defaultPkgName.size(); i++) {
                String pkgName = defaultPkgName.get(i);
                AppInfo appInfo = AppInfo.createAppInfo(context, pkgName, false, null, i);
                data.add(appInfo);
            }
//        } else {
//
//        }

        return data;
    }

    public static String getBrowserApp(Context context) {
        String default_browser = "android.intent.category.DEFAULT";
        String browsable = "android.intent.category.BROWSABLE";
        String view = "android.intent.action.VIEW";
        Intent intent = new Intent(view);
        intent.addCategory(default_browser);
        intent.addCategory(browsable);
        Uri uri = Uri.parse("http://");
        intent.setDataAndType(uri, null);

        String systemBrowserPkg = "";
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(
                intent, PackageManager.GET_INTENT_FILTERS);
        for (int i = 0; i < resolveInfoList.size(); i++) {
            ActivityInfo activityInfo = resolveInfoList.get(i).activityInfo;
            String packageName = activityInfo.packageName;
            if (isSystemApp(context, packageName)) {
                systemBrowserPkg = packageName;
                break;
            }
        }
        ResolveInfo mInfo = context.getPackageManager().resolveActivity(intent, 0);
        String packageName = mInfo.activityInfo.packageName;
        if (packageName.equals("android")) {
            return getBrowserPkgName(context, systemBrowserPkg);
        } else {
            return packageName;
        }

    }

    private static String getBrowserPkgName(Context context, String systemBrowserPkg) {
        if (isAvilible(context, Constants.PKGNAME_CHROME)) {
            return Constants.PKGNAME_CHROME;
        }
        if (isAvilible(context, Constants.PKGNAME_UC)) {
            return Constants.PKGNAME_UC;
        }
        if (isAvilible(context, Constants.PKGNAME_MTT)) {
            return Constants.PKGNAME_MTT;
        }
        if (isAvilible(context, Constants.PKGNAME_SOGOU)) {
            return Constants.PKGNAME_SOGOU;
        }
        if (isAvilible(context, Constants.PKGNAME_BAIDU)) {
            return Constants.PKGNAME_BAIDU;
        }
        return systemBrowserPkg;
    }

    public static String getMusicApp(Context context) {
        String default_browser = "android.intent.category.DEFAULT";
        String view = "android.intent.action.VIEW";
        Intent intent = new Intent(view);
        intent.addCategory(default_browser);
        Uri uri = Uri.parse("file://");
        intent.setDataAndType(uri, "audio/*");

        String systemMusicPkg = "";
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(
                intent, PackageManager.GET_INTENT_FILTERS);
        for (int i = 0; i < resolveInfoList.size(); i++) {
            ActivityInfo activityInfo = resolveInfoList.get(i).activityInfo;
            String packageName = activityInfo.packageName;
            if (isSystemApp(context, packageName)) {
                systemMusicPkg = packageName;
                break;
            }
        }

        ResolveInfo mInfo = context.getPackageManager().resolveActivity(intent, 0);
        String packageName = mInfo.activityInfo.packageName;
        if (packageName.equals("android")) {
            return getMusicPkgName(context, systemMusicPkg);
        } else {
            return packageName;
        }

    }

    public static String getMusicPkgName(Context context, String systemMusicPkg) {
        if (isAvilible(context, Constants.PKGNAME_QQMUSIC)) {
            return Constants.PKGNAME_QQMUSIC;
        }
        if (isAvilible(context, Constants.PKGNAME_KUGOU)) {
            return Constants.PKGNAME_KUGOU;
        }
        if (isAvilible(context, Constants.PKGNAME_KUWO)) {
            return Constants.PKGNAME_KUWO;
        }
        if (isAvilible(context, Constants.PKGNAME_DUOMI)) {
            return Constants.PKGNAME_DUOMI;
        }
        if (isAvilible(context, Constants.PKGNAME_BAIDUMUSIC)) {
            return Constants.PKGNAME_BAIDUMUSIC;
        }
        if (isAvilible(context, Constants.PKGNAME_XIAMI)) {
            return Constants.PKGNAME_XIAMI;
        }
        if (isAvilible(context, Constants.PKGNAME_NETEASE)) {
            return Constants.PKGNAME_NETEASE;
        }
        return systemMusicPkg;
    }

    public static void addRecentTasksPkgName(Context context, List<String> defaultPkgName) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<RecentTaskInfo> recentTaskInfo = activityManager.getRecentTasks(6,
                ActivityManager.RECENT_WITH_EXCLUDED);
        int count = 0;
        if (recentTaskInfo.size() != 0) {
            for (RecentTaskInfo recentInfo : recentTaskInfo) {
                Intent intent = recentInfo.baseIntent;
                if (!intent.toString().contains(Intent.CATEGORY_LAUNCHER)) {
                    return;
                }
                ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent,
                        PackageManager.GET_INTENT_FILTERS);
                if (resolveInfo == null || resolveInfo.getIconResource() == 0) {
                    return;
                }
                String pkgName = resolveInfo.activityInfo.packageName;
                if (!defaultPkgName.contains(pkgName)) {
                    if (count >= 2) {
                        return;
                    }
                    defaultPkgName.add(pkgName);
                    count++;
                }
            }
        }
    }

    private static String getSystemDialerPkgName(Context context) {
        Set<String> dialerPkgNameSet = getDialerPkgName(context, Intent.ACTION_DIAL);
        String dialerPkgName = "";
        if (dialerPkgNameSet != null) {
            for (String str : dialerPkgNameSet) {
                boolean systemApp = isSystemApp(context, str);
                if (systemApp) {
                    // 显示系统级别的拨号软件包名
                    dialerPkgName = str;
                }
            }
        }
        return dialerPkgName;
    }

    // 得到所有拨号程序的包名
    private static Set<String> getDialerPkgName(Context context, String intentStr) {
        PackageManager sPackageManager = context.getPackageManager();
        Intent dialerIntent = new Intent(intentStr);
        List<ResolveInfo> intentResolveInfos = sPackageManager.queryIntentActivities(dialerIntent,
                PackageManager.GET_RECEIVERS);
        int size = intentResolveInfos.size();
        if (size < 1) {
            return null;
        }
        Set<String> result = new HashSet<String>();
        for (int i = 0; i < size; i++) {
            String packageName = intentResolveInfos.get(i).activityInfo.packageName;
            result.add(packageName);
        }
        return result;
    }

    private static boolean isSystemApp(Context context, String packageName) {
        try {
            PackageInfo mPackageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if ((mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return true;
    }

    private static List<String> pkgNameList = null;

    /**
     * 判断该包名对用的应用是否安装在手机上
     * 
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (null == pkgNameList) {
            pkgNameList = new ArrayList<String>();
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName;
                    pkgNameList.add(pn);
                }
            }
        }
        return pkgNameList.contains(packageName);
    }
}
