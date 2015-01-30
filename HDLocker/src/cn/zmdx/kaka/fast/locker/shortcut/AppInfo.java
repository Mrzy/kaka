
package cn.zmdx.kaka.fast.locker.shortcut;

import java.io.Serializable;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class AppInfo implements Serializable, Comparable<AppInfo> {

    /**
     * 
     */
    private static final long serialVersionUID = -7000227766462632344L;

    public static final int DISGUISE = 1;

    public static final int UNDISGUISE = 0;

    private String pkgName;

    private String appName;

    private Drawable defaultIcon;

    private boolean isShortcut;

    private boolean isDisguise;

    private Drawable disguiseDrawable;

    private int id;

    private Integer position;

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getPosition() {
        return position;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getDefaultIcon() {
        return defaultIcon;
    }

    public void setDefaultIcon(Drawable defaultIcon) {
        this.defaultIcon = defaultIcon;
    }

    public boolean isShortcut() {
        return isShortcut;
    }

    public void setShortcut(boolean isShortcut) {
        this.isShortcut = isShortcut;
    }

    public boolean isDisguise() {
        return isDisguise;
    }

    public void setDisguise(boolean isDisguise) {
        this.isDisguise = isDisguise;
    }

    public Drawable getDisguiseDrawable() {
        return disguiseDrawable;
    }

    public void setDisguiseDrawable(Drawable disguiseDrawable) {
        this.disguiseDrawable = disguiseDrawable;
    }

    public static AppInfo createAppInfo(Context context, String pkgName, boolean disguise,
            Drawable disguiseDrawable, int position) {
        final AppInfo ai = new AppInfo();
        ai.setPkgName(pkgName);
        String appName = null;
        final PackageManager pm = context.getPackageManager();
        try {
            appName = pm.getApplicationLabel(pm.getApplicationInfo(pkgName, 0)).toString();
        } catch (Exception e) {
        }
        ai.setAppName(appName);
        ai.setDefaultIcon(getIconByPkgName(context, pkgName));
        ai.setDisguise(disguise);
        ai.setDisguiseDrawable(disguiseDrawable);
        ai.setPosition(position);
        ai.setShortcut(true);
        return ai;
    }

    public static void clearAppInfo(AppInfo appInfo) {
        appInfo.setAppName("");
        appInfo.setDefaultIcon(null);
        appInfo.setDisguise(false);
        appInfo.setDisguiseDrawable(null);
        appInfo.setPosition(appInfo.getPosition());
        appInfo.setShortcut(true);
    }

    public static Drawable getIconByPkgName(Context context, String pkgName) {
        try {
            return context.getPackageManager().getApplicationIcon(pkgName);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public int compareTo(AppInfo another) {
        return this.position.compareTo(another.getPosition());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("pkgName:" + pkgName + ", appName:" + appName + ", postion:" + position
                + ", drawable:" + defaultIcon);
        return sb.toString();
    }
}
