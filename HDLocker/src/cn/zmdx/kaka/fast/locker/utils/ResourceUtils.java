package cn.zmdx.kaka.fast.locker.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/** 
 * 
 */
public class ResourceUtils {

    public static Drawable getIcon(Context context, ComponentName component) {
        if (component != null) {
            PackageManager pkgManager = context.getPackageManager();
            try {
                return pkgManager.getActivityIcon(component);
            } catch (NameNotFoundException e) {
                String pkg = component.getPackageName();
                try {
                    PackageInfo packageInfo = pkgManager.getPackageInfo(pkg, 0);
                    return packageInfo.applicationInfo.loadIcon(pkgManager);
                } catch (NameNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Drawable getDrawableFromPakcage(Context context, String packageName, String resName) {
        if (context == null || packageName == null || resName == null) {
            return null;
        }
        try {
            final Resources themeResource = context.getPackageManager().getResourcesForApplication(packageName);
            int resId = themeResource.getIdentifier(resName,
                    "drawable", packageName);
            if (resId != 0) {
                Drawable drawable = themeResource.getDrawable(resId);
                return drawable;
            }
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        } catch (Resources.NotFoundException e) {
        }
        return null;
    }

    public static Drawable getDrawableFromPakcage(Context context, String packageName, int resId) {
        if (context == null || packageName == null || resId < 1) {
            return null;
        }
        try {
            final Resources themeResource = context.getPackageManager().getResourcesForApplication(packageName);
            if (resId != 0) {
                Drawable drawable = themeResource.getDrawable(resId);
                return drawable;
            }
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        } catch (Resources.NotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
