
package cn.zmdx.kaka.fast.locker.notify.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import cn.zmdx.kaka.fast.locker.R;

public class NotifyFilterManager {

    public final static String RECENT_TASK_MARK = "#";

    public final static String RECENT_TASK_MARK_PROMPT = "最近使用";

    public static ArrayList<NotifyFilterEntity> prepareInterceptList(Context mContext,
            Set<String> pkgNameSet) {
        PackageManager packageManager = mContext.getPackageManager();
        ArrayList<NotifyFilterEntity> list = new ArrayList<NotifyFilterEntity>();
        for (String pkgName : pkgNameSet) {
            try {
                NotifyFilterEntity interceptEntity = new NotifyFilterEntity();
                Drawable icon = packageManager.getApplicationIcon(pkgName);
                String appName = packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(pkgName, PackageManager.GET_META_DATA))
                        .toString();
                interceptEntity.setNotifyCHName(appName);
                interceptEntity.setNotifyUSName(NotifyFilterUtil.getChinesePinyinStr(appName));
                interceptEntity.setPkgName(pkgName);
                interceptEntity.setSelect(true);
                interceptEntity.setNotifyIcon(icon);
                list.add(interceptEntity);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        NotifyFilterEntity interceptEntity = new NotifyFilterEntity();
        interceptEntity.setNotifyIcon(mContext.getResources().getDrawable(R.drawable.ic_launcher));
        list.add(list.size(), interceptEntity);
        return list;
    }

    public static ArrayList<NotifyFilterEntity> prepareAppList(Context mContext,
            Set<String> pkgNameSet) {
        List<PackageInfo> packageInfos = mContext.getPackageManager().getInstalledPackages(
                PackageManager.GET_UNINSTALLED_PACKAGES);
        ArrayList<NotifyFilterEntity> list = new ArrayList<NotifyFilterEntity>();
        for (PackageInfo info : packageInfos) {
            NotifyFilterEntity myAppInfo = new NotifyFilterEntity();
            ApplicationInfo appInfo = info.applicationInfo;
            Drawable icon = appInfo.loadIcon(mContext.getPackageManager());
            String appName = appInfo.loadLabel(mContext.getPackageManager()).toString();
            myAppInfo.setNotifyCHName(appName);
            myAppInfo.setNotifyUSName(NotifyFilterUtil.getChinesePinyinStr(appName));
            myAppInfo.setPkgName(appInfo.packageName);
            myAppInfo.setNotifyIcon(icon);
            myAppInfo.setSelect(pkgNameSet.contains(appInfo.packageName));
            list.add(myAppInfo);
        }
        list.addAll(prepareRecentTaskInfo(mContext, pkgNameSet));
        return list;
    }

    private static ArrayList<NotifyFilterEntity> prepareRecentTaskInfo(Context mContext,
            Set<String> pkgNameSet) {
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<RecentTaskInfo> recentTaskInfo = activityManager.getRecentTasks(10,
                ActivityManager.RECENT_WITH_EXCLUDED);

        ArrayList<NotifyFilterEntity> list = new ArrayList<NotifyFilterEntity>();
        for (RecentTaskInfo recentInfo : recentTaskInfo) {
            NotifyFilterEntity recentEntity = new NotifyFilterEntity();
            Intent intent = recentInfo.baseIntent;
            ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(intent, 0);
            if (resolveInfo != null) {
                String labelString = resolveInfo.loadLabel(mContext.getPackageManager()).toString();
                recentEntity.setNotifyCHName(labelString);
                recentEntity.setNotifyIcon(resolveInfo.loadIcon(mContext.getPackageManager()));
                recentEntity.setNotifyUSName(RECENT_TASK_MARK
                        + NotifyFilterUtil.getChinesePinyinStr(labelString));
                recentEntity.setPkgName(resolveInfo.activityInfo.packageName);
                recentEntity.setSelect(pkgNameSet.contains(resolveInfo.activityInfo.packageName));
                list.add(recentEntity);
            }
        }
        return list;
    }

    public static class NotifyFilterEntity {

        private String mNotifyUSName;

        private String mNotifyCHName;

        private Drawable mNotifyIcon;

        private String mPkgName;

        private boolean isSelect = false;

        public String getNotifyUSName() {
            return mNotifyUSName;
        }

        public void setNotifyUSName(String mNotifyUSName) {
            this.mNotifyUSName = mNotifyUSName;
        }

        public String getNotifyCHName() {
            return mNotifyCHName;
        }

        public void setNotifyCHName(String mNotifyCHName) {
            this.mNotifyCHName = mNotifyCHName;
        }

        public String getPkgName() {
            return mPkgName;
        }

        public void setPkgName(String mPkgName) {
            this.mPkgName = mPkgName;
        }

        public Drawable getNotifyIcon() {
            return mNotifyIcon;
        }

        public void setNotifyIcon(Drawable mNotifyIcon) {
            this.mNotifyIcon = mNotifyIcon;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean isSelect) {
            this.isSelect = isSelect;
        }

    }
}
