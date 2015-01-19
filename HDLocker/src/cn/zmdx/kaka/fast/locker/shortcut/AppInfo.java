
package cn.zmdx.kaka.fast.locker.shortcut;

import android.graphics.drawable.Drawable;

public class AppInfo {

    private String pkgName;

    private String appName;

    private Drawable defaultIcon;

    private boolean isShortcut;

    private boolean isDisguise;

    private Drawable disguiseDrawable;

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

}
