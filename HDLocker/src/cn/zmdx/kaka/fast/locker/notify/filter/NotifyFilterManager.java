
package cn.zmdx.kaka.fast.locker.notify.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import cn.zmdx.kaka.fast.locker.R;

public class NotifyFilterManager {

    public final static String RECENT_TASK_MARK = "#";

    public final static String RECENT_TASK_MARK_PROMPT = "最近使用";

    public final static int MAX_NOTIFY_FILTER_COUNT = 6;

    public final static int MAX_RECENT_TASK_COUNT = 7;

    public static ArrayList<NotifyFilterEntity> prepareInterceptList(Context mContext,
            Set<String> pkgNameSet) {
        PackageManager packageManager = mContext.getPackageManager();
        ArrayList<NotifyFilterEntity> list = new ArrayList<NotifyFilterEntity>();
        for (String pkgName : pkgNameSet) {
            try {
//                if (list.size() <= MAX_NOTIFY_FILTER_COUNT) {
                    NotifyFilterEntity interceptEntity = new NotifyFilterEntity();
                    Drawable icon = packageManager.getApplicationIcon(pkgName);
                    String appName = packageManager.getApplicationLabel(
                            packageManager
                                    .getApplicationInfo(pkgName, PackageManager.GET_META_DATA))
                            .toString();
                    interceptEntity.setNotifyCHName(appName);
                    interceptEntity.setNotifyUSName(NotifyFilterUtil.getChinesePinyinStr(appName));
                    interceptEntity.setPkgName(pkgName);
                    interceptEntity.setSelect(true);
                    interceptEntity.setNotifyIcon(icon);
                    list.add(interceptEntity);
//                } else {
//                    break;
//                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        NotifyFilterEntity interceptEntity = new NotifyFilterEntity();
        interceptEntity.setNotifyIcon(mContext.getResources().getDrawable(
                R.drawable.notify_filter_frame));
        list.add(list.size(), interceptEntity);
        return list;
    }

    public static ArrayList<NotifyFilterEntity> prepareAppList(Context mContext,
            Set<String> pkgNameSet, boolean isNeedSelect) {
        ArrayList<NotifyFilterEntity> list = new ArrayList<NotifyFilterEntity>();
        PackageManager pm = mContext.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
                PackageManager.GET_INTENT_FILTERS);
        for (ResolveInfo reInfo : resolveInfos) {
            String pkgName = reInfo.activityInfo.packageName;
            String appLabel = (String) reInfo.loadLabel(pm);
            Drawable icon = reInfo.loadIcon(pm);
            NotifyFilterEntity myAppInfo = new NotifyFilterEntity();
            myAppInfo.setNotifyCHName(appLabel);
            myAppInfo.setNotifyUSName(NotifyFilterUtil.getChinesePinyinStr(appLabel));
            myAppInfo.setPkgName(pkgName);
            myAppInfo.setNotifyIcon(icon);
            myAppInfo.setSelect(isNeedSelect == true ? pkgNameSet.contains(pkgName) : false);
            list.add(myAppInfo);
        }

        list.addAll(prepareRecentTaskInfo(mContext, pkgNameSet, isNeedSelect));
        return list;
    }

    private static ArrayList<NotifyFilterEntity> prepareRecentTaskInfo(Context mContext,
            Set<String> pkgNameSet, boolean isNeedSelect) {
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<RecentTaskInfo> recentTaskInfo = activityManager.getRecentTasks(10,
                ActivityManager.RECENT_WITH_EXCLUDED);

        ArrayList<NotifyFilterEntity> list = new ArrayList<NotifyFilterEntity>();
        for (RecentTaskInfo recentInfo : recentTaskInfo) {
            if (list.size() <= MAX_RECENT_TASK_COUNT) {
                NotifyFilterEntity recentEntity = new NotifyFilterEntity();
                Intent intent = recentInfo.baseIntent;
                if (intent.toString().contains(Intent.CATEGORY_LAUNCHER)) {
                    ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(intent,
                            PackageManager.GET_INTENT_FILTERS);
                    if (resolveInfo != null) {
                        if (resolveInfo.getIconResource() != 0) {
                            String labelString = resolveInfo
                                    .loadLabel(mContext.getPackageManager()).toString();
                            recentEntity.setNotifyCHName(labelString);
                            recentEntity.setNotifyIcon(resolveInfo.loadIcon(mContext
                                    .getPackageManager()));
                            recentEntity.setNotifyUSName(RECENT_TASK_MARK
                                    + NotifyFilterUtil.getChinesePinyinStr(labelString));
                            recentEntity.setPkgName(resolveInfo.activityInfo.packageName);
                            recentEntity.setSelect(isNeedSelect == true ? pkgNameSet
                                    .contains(resolveInfo.activityInfo.packageName) : false);
                            list.add(recentEntity);
                        }
                    }
                }
            } else {
                break;
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
