
package cn.zmdx.kaka.locker.utils;

import java.io.File;
import java.lang.reflect.Field;

import android.content.pm.PackageInfo;

public class PackageInfoCompat {
    private static Field fieldFirstInstallTime = HDBReflectionUtils.findFieldNoThrow(PackageInfo.class, "firstInstallTime");
    private static Field fieldLastUpdateTime = HDBReflectionUtils.findFieldNoThrow(PackageInfo.class, "lastUpdateTime");

    public static long getFirstInstallTime(PackageInfo pi) {
        if (pi == null) {
            return -1;
        }

        if (fieldFirstInstallTime != null) {
            try {
                return (Long) fieldFirstInstallTime.get(pi);
            } catch (Exception e) {
            }
        }

        File f = new File(pi.applicationInfo.sourceDir);
        return f.lastModified();
    }

    public static long getLastUpdateTime(PackageInfo pi) {
        if (pi == null) {
            return -1;
        }

        if (fieldLastUpdateTime != null) {
            try {
                return (Long) fieldLastUpdateTime.get(pi);
            } catch (Exception e) {
            }
        }

        File f = new File(pi.applicationInfo.sourceDir);
        return f.lastModified();
    }
}
