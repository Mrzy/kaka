
package cn.zmdx.kaka.locker.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class PackageCompat {
    private static final boolean DEBUG = HDBConfig.LOGE_ENABLED;
    private static final String TAG = "PackageCompat";
    private static final String CLASSNAME_PACKAGEPARSER = "android.content.pm.PackageParser";
    private static final String CLASSNAME_PACKAGEPARSER_ACTIVITY = "android.content.pm.PackageParser$Activity";
    private static final String CLASSNAME_PACKAGEPARSER_PACKAGE = "android.content.pm.PackageParser$Package";
    private static final String CLASSNAME_PACKAGEPARSER_ACTIVITYINTENTINFO = "android.content.pm.PackageParser$ActivityIntentInfo";

    private static final String CLASSNAME_IPACKAGEMANAGER = "android.content.pm.IPackageManager";
    private static final String CLASSNAME_IPACKAGEMANAGER_STUB = "android.content.pm.IPackageManager$Stub";

    public static final int INSTALL_LOCATION_AUTO = 0;
    public static final int INSTALL_LOCATION_INTERNAL_ONLY = 1;
    public static final int INSTALL_LOCATION_PREFER_EXTERNAL = 2;
    public static final int INSTALL_LOCATION_UNSPECIFIED = -1;
    public static final int APP_INSTALL_EXTERNAL = 2;

    public static final int MOVE_INTERNAL = 0x00000001;
    public static final int MOVE_EXTERNAL_MEDIA = 0x00000002;
    public static final int MOVE_SUCCEEDED = 1;

    public static final int FLAG_FORWARD_LOCK = 1<<29;

    private static final int VERSION_1 = 1;
    private static final int VERSION_2 = 2;
    private static final int VERSION_3 = 3;

    private static Class<?> sPackageParserClass;
    private static Class<?> sPackageParserActivityClass;
    private static Class<?> sPackageParserPackageClass;
    private static Class<?> sPackageParserActivityIntentInfoClass;
    private static Constructor<?> sPackageParserConstructor;
    private static Method sParsePackageMethod;
    private static Method sPackageParser_collectCertificatesMethod;
    private static Method sCountActionsMethod;
    private static Method sGetActionMethod;
    private static Method sGetPackageSizeInfoMethod;
    private static Method sFreeStorageAndNotifyMethod;
    private static Method sSetPackageNameMethod;
    private static Field sPackageParser_SignaturesField;
    private static Field sInstallLocationField;
    private static Field sReceiversField;
    private static Field sApplicationInfoField;
    private static Field sMVersionCodeField;
    private static Field sPackageNameField;
    private static Field sIntentsField;
    private static Field sInfoField;

    /**
     * PackageParser.requestedPermissions
     */
    private static Field sPackageParser_RequestedPermissionsField;

    private static Class<?> sIPackageManagerClass;
    private static Method sAsInterfaceMethod;

    /**
     * IPackageManager.getInstallLocation
     */
    private static Method sGetInstallLocationMethod;

    /**
     * IPackageManager.movePackage
     */
    private static Method sMovePackageMethod;

    /**
     * IPackageManager.setComponentEnabledSetting
     */
    private static Method sSetComponentEnabledSettingMethod;
    private static int sSetComponentEnabledSettingMethodVersion = VERSION_1;

    /**
     * IPackageManager.setApplicationEnabledSetting
     */
    private static Method sSetApplicationEnabledSettingMethod;
    private static int sSetApplicationEnabledSettingMethodVersion = VERSION_1;

    /**
     * IPackageManager.installPackage
     */
    private static Method sInstallPackageMethod;

    /**
     * IPackageManager.deletePackage or IPackageManager.deletePackageAsUser
     */
    private static Method sDeletePackageMethod;
    private static int sDeletePackageMethodVersion = VERSION_1;

    static {
        try {
            sInstallLocationField = PackageInfo.class.getField("installLocation");
        } catch (SecurityException e) {
            if (DEBUG) e.printStackTrace();
            sInstallLocationField = null;
        } catch (NoSuchFieldException e) {
            if (DEBUG) e.printStackTrace();
            sInstallLocationField = null;
        }

        try {
            sPackageParserClass = Class.forName(CLASSNAME_PACKAGEPARSER, false,
                    Thread.currentThread().getContextClassLoader());
            sPackageParserActivityClass = Class.forName(CLASSNAME_PACKAGEPARSER_ACTIVITY, false,
                    Thread.currentThread().getContextClassLoader());
            sPackageParserActivityIntentInfoClass = Class.forName(CLASSNAME_PACKAGEPARSER_ACTIVITYINTENTINFO, false,
                    Thread.currentThread().getContextClassLoader());
            sPackageParserPackageClass = Class.forName(CLASSNAME_PACKAGEPARSER_PACKAGE, false,
                    Thread.currentThread().getContextClassLoader());
            Class<?>[] arrayOfClass = new Class[] { String.class };
            sPackageParserConstructor = sPackageParserClass.getConstructor(arrayOfClass);
            Class<?>[] arrayOfClass2 = new Class[] { File.class, String.class, DisplayMetrics.class,
                    int.class};
            sParsePackageMethod = sPackageParserClass.getDeclaredMethod("parsePackage",
                    arrayOfClass2);
            sPackageParser_collectCertificatesMethod = sPackageParserClass.getDeclaredMethod(
                    "collectCertificates", new Class[] { sPackageParserPackageClass, int.class });
            sCountActionsMethod = sPackageParserActivityIntentInfoClass.getMethod("countActions",
                    new Class[0]);
            sGetActionMethod = sPackageParserActivityIntentInfoClass.getMethod("getAction",
                    new Class[] { int.class });
            sGetPackageSizeInfoMethod = PackageManager.class.getMethod("getPackageSizeInfo",
                    new Class[] { String.class, IPackageStatsObserver.class} );
            sFreeStorageAndNotifyMethod = PackageManager.class.getMethod("freeStorageAndNotify",
                    new Class[] { long.class, IPackageDataObserver.class} );
            sSetPackageNameMethod = sPackageParserPackageClass.getDeclaredMethod("setPackageName",
                    new Class[] { String.class} );
            sPackageParser_SignaturesField = sPackageParserPackageClass.getField("mSignatures");
            sReceiversField = sPackageParserPackageClass.getField("receivers");
            sPackageNameField = sPackageParserPackageClass.getField("packageName");
            sApplicationInfoField = sPackageParserPackageClass.getField("applicationInfo");
            sMVersionCodeField = sPackageParserPackageClass.getField("mVersionCode");
            sIntentsField = sPackageParserActivityClass.getField("intents");
            sInfoField = sPackageParserActivityClass.getField("info");
            if (DEBUG) Log.d(TAG, "==== good, it works");
        } catch (ClassNotFoundException e) {
            if (DEBUG) e.printStackTrace();
            sPackageParserClass = null;
            sPackageParserActivityClass = null;
            sPackageParserActivityIntentInfoClass = null;
        } catch (NoSuchMethodException e) {
            if (DEBUG) e.printStackTrace();
            sPackageParserConstructor = null;
            sParsePackageMethod = null;
            sGetPackageSizeInfoMethod = null;
            sFreeStorageAndNotifyMethod = null;
            sSetPackageNameMethod = null;
        } catch (NoSuchFieldException e) {
            if (DEBUG) e.printStackTrace();
            sReceiversField = null;
            sIntentsField = null;
            sInfoField = null;
            sApplicationInfoField = null;
            sMVersionCodeField = null;
            sPackageNameField = null;
        }

        try {
            if (sPackageParserClass != null) {
                sPackageParser_RequestedPermissionsField = sPackageParserPackageClass.getField("requestedPermissions");
            }
        } catch (Exception e) {
            if (DEBUG) HDBLOG.logE( "unexpected exception", e);
        }
        try {
            sIPackageManagerClass = Class.forName(CLASSNAME_IPACKAGEMANAGER);
            Class<?> clazz = Class.forName(CLASSNAME_IPACKAGEMANAGER_STUB);
            sAsInterfaceMethod = clazz.getMethod("asInterface", new Class[] { IBinder.class });
        } catch (Exception e) {
            if (DEBUG) HDBLOG.logE( "unexpected exception", e);
            sIPackageManagerClass = null;
            sAsInterfaceMethod = null;
        }

        // IPackageManager.getInstallLocation
        try {
            sGetInstallLocationMethod = sIPackageManagerClass.getMethod("getInstallLocation");
        } catch (Exception e) {
            if (DEBUG) HDBLOG.logE( "not supported by system", e);
        }

        // IPackageManager.movePackage
        try {
            sMovePackageMethod = sIPackageManagerClass.getMethod("movePackage",
                    String.class, IPackageMoveObserver.class, int.class);
        } catch (Exception e) {
            if (DEBUG) HDBLOG.logE( "not supported by system", e);
        }

        // IPackageManager.setComponentEnabledSetting
        try {
            sSetComponentEnabledSettingMethod = sIPackageManagerClass.getMethod(
                    "setComponentEnabledSetting", ComponentName.class, int.class, int.class);
            sSetComponentEnabledSettingMethodVersion = VERSION_1;
        } catch (Exception e) {
            try {
                // Android 4.0
                sSetComponentEnabledSettingMethod = sIPackageManagerClass.getMethod(
                        "setComponentEnabledSetting", ComponentName.class, int.class, int.class, int.class);
                sSetComponentEnabledSettingMethodVersion = VERSION_2;
            } catch (Exception e1) {
                if (DEBUG) HDBLOG.logE( "not supported by system", e1);
            }
        }

        // IPackageManager.setApplicationEnabledSetting
        try {
            sSetApplicationEnabledSettingMethod = sIPackageManagerClass.getMethod(
                    "setApplicationEnabledSetting", String.class, int.class, int.class);
            sSetApplicationEnabledSettingMethodVersion = VERSION_1;
        } catch (Exception e) {
            try {
                // Android 4.0
                sSetApplicationEnabledSettingMethod = sIPackageManagerClass.getMethod(
                        "setApplicationEnabledSetting", String.class, int.class, int.class, int.class);
                sSetApplicationEnabledSettingMethodVersion = VERSION_2;
            } catch (Exception e1) {
                try {
                    // android 4.3
                    sSetApplicationEnabledSettingMethod = sIPackageManagerClass.getMethod(
                            "setApplicationEnabledSetting", String.class, int.class,
                                    int.class, int.class, String.class);
                    sSetApplicationEnabledSettingMethodVersion = VERSION_3;
                } catch (Exception e2) {
                    if (DEBUG) HDBLOG.logE( "not supported by system", e2);
                }
            }
        }

        // IPackageManager.installPackage
        try {
            sInstallPackageMethod = sIPackageManagerClass.getMethod("installPackage",
                    Uri.class, IPackageInstallObserver.class, int.class, String.class);
        } catch (Exception e) {
            if (DEBUG) HDBLOG.logE( "not supported by system", e);
        }

        // IPackageManager.deletePackage or IPackageManager.deletePackageAsUser
        try {
            sDeletePackageMethod = sIPackageManagerClass.getMethod("deletePackage",
                    String.class, IPackageDeleteObserver.class, int.class);
            sDeletePackageMethodVersion = VERSION_1;
        } catch (Exception e) {
            try {
                // Android 4.3
                sDeletePackageMethod = sIPackageManagerClass.getMethod("deletePackageAsUser",
                        String.class, IPackageDeleteObserver.class, int.class, int.class);
                sDeletePackageMethodVersion = VERSION_2;
            } catch (Exception e1) {
                if (DEBUG) HDBLOG.logE( "not supported by system", e1);
            }
        }

    }

    public static Object asInterface(IBinder binder) {
        if (sAsInterfaceMethod != null) {
            try {
                Method localMethod = sAsInterfaceMethod;
                Object[] arrayOfObject = new Object[] {binder};
                Object ret = localMethod.invoke(null, arrayOfObject);
                return ret;
            } catch (IllegalAccessException e) {
                if (DEBUG) e.printStackTrace();
            } catch (InvocationTargetException e) {
                if (DEBUG) e.printStackTrace();
            }
        }
        return null;
    }

    public static int getInstallLocation() {
        if (sGetInstallLocationMethod != null) {
            try {
                Object pm = asInterface(
                        ServiceManagerCompat.getService("package"));
                Method localMethod = sGetInstallLocationMethod;
                Object[] arrayOfObject = new Object[0];
                Object ret = localMethod.invoke(pm, arrayOfObject);
                return (Integer) ret;
            } catch (Exception e) {
                if (DEBUG) Log.w(TAG, "Failed to get default install location: " + e);
            }
        }
        return INSTALL_LOCATION_AUTO;
    }

    public static void setComponentEnabledSetting(Object pm, ComponentName cn, int newState,
            int flags) {
        if (sSetComponentEnabledSettingMethod != null) {
            try {
                if (sSetComponentEnabledSettingMethodVersion == VERSION_1) {
                    sSetComponentEnabledSettingMethod.invoke(pm, cn, newState, flags);
                } else if (sSetComponentEnabledSettingMethodVersion == VERSION_2) {
                    // Android 4.0
                    sSetComponentEnabledSettingMethod.invoke(pm, cn, newState, flags, UserIdCompat.myUserId());
                } else {
                    HDBLOG.logE("bad logic, please check");
                }
            } catch (Exception e) {
                if (DEBUG) HDBLOG.logE( "failed to invoke setComponentEnabledSetting", e);
            }
        } else {
            if (DEBUG) HDBLOG.logE( "setComponentEnabledSetting not supported by system");
        }
    }

    public static void setApplicationEnabledSetting(Context cxt, Object pm, String pkgName, int newState,
            int flags) {
        if (sSetApplicationEnabledSettingMethod != null) {
            try {
                if (sSetApplicationEnabledSettingMethodVersion == VERSION_1) {
                    sSetApplicationEnabledSettingMethod.invoke(pm, pkgName, newState, flags);
                } else if (sSetApplicationEnabledSettingMethodVersion == VERSION_2) {
                    // Android 4.0
                    sSetApplicationEnabledSettingMethod.invoke(pm, pkgName, newState, flags,
                            UserIdCompat.myUserId());
                } else if (sSetApplicationEnabledSettingMethodVersion == VERSION_3) {
                    // Android 4.3
                    sSetApplicationEnabledSettingMethod.invoke(pm, pkgName, newState, flags,
                            UserIdCompat.myUserId(), cxt.getPackageName());
                } else {
                    HDBLOG.logE("bad logic, please check");
                }
            } catch (Exception e) {
                if (DEBUG) HDBLOG.logE( "failed to invoke setApplicationEnabledSetting", e);
            }
        } else {
            if (DEBUG) HDBLOG.logE( "setApplicationEnabledSetting not supported by system");
        }
    }

    public static boolean installPackage(Object pm, Uri packageUri, IPackageInstallObserver observer,
            int flags, String name) {
        if (sInstallPackageMethod != null) {
            try {
                Method localMethod = sInstallPackageMethod;
                Object[] arrayOfObject = new Object[] { packageUri, observer, flags, name };
                localMethod.invoke(pm, arrayOfObject);
                return true;
            } catch (Exception e) {
                if (DEBUG) HDBLOG.logE( "failed to invoke installPackage", e);
            }
        } else {
            if (DEBUG) HDBLOG.logE( "installPackage not supported by system");
        }
        return false;
    }

    public static boolean movePackage(Object pm, String packageName, IPackageMoveObserver observer,
            int flags) {
        if (sMovePackageMethod != null) {
            try {
                Method localMethod = sMovePackageMethod;
                Object[] arrayOfObject = new Object[] { packageName, observer, flags };
                localMethod.invoke(pm, arrayOfObject);
                return true;
            } catch (Exception e) {
                if (DEBUG) HDBLOG.logE( "failed to invoke movePackage", e);
            }
        } else {
            if (DEBUG) HDBLOG.logE( "movePackage not supported by system");
        }
        return false;
    }

    public static boolean deletePackage(Object pm, String pkgName, IPackageDeleteObserver observer,
            int flags) {
        if (sDeletePackageMethod != null) {
            try {
                if (sDeletePackageMethodVersion == VERSION_1) {
                    sDeletePackageMethod.invoke(pm, pkgName, observer, flags);
                } else if (sDeletePackageMethodVersion == VERSION_2) {
                    // Android 4.3
                    sDeletePackageMethod.invoke(pm, pkgName, observer, UserIdCompat.myUserId(), flags);
                } else {
                    HDBLOG.logE("bad logic, please check");
                }
                return true;
            } catch (Exception e) {
                if (DEBUG) HDBLOG.logE( "failed to invoke deletePackage", e);
            }
        } else {
            if (DEBUG) HDBLOG.logE( "deletePackage not supported by system");
        }
        return false;
    }

    public static boolean deletePackageCache(Object pm, String pkgName, IPackageDataObserver observer) {
        try {
            Method deleteCache = pm.getClass().getMethod("deleteApplicationCacheFiles",
                    String.class, IPackageDataObserver.class);
            deleteCache.invoke(pm, new Object[] { pkgName, observer });
            return true;
        } catch (IllegalArgumentException e) {
            if (DEBUG) e.printStackTrace();
        } catch (IllegalAccessException e) {
            if (DEBUG) e.printStackTrace();
        } catch (InvocationTargetException e) {
            if (DEBUG) e.printStackTrace();
        } catch (NoSuchMethodException e) {
            if (DEBUG) e.printStackTrace();
        }
        return false;
    }

    public static int packageInfo_installLocation(PackageInfo obj) {
        if (sInstallLocationField != null) {
            try {
                Field localField = sInstallLocationField;
                Object ret = localField.get(obj);
                return (Integer) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            }
        }
        if (DEBUG) Log.e(TAG, "packageInfo_installLocation failure");
        return INSTALL_LOCATION_UNSPECIFIED;
    }

    public static Object createPackageParser(String path) {
        if (sPackageParserConstructor != null) {
            try {
                Constructor<?> constructor = sPackageParserConstructor;
                return constructor.newInstance(path);
            } catch (IllegalArgumentException e) {
                if (DEBUG) e.printStackTrace();
            } catch (InstantiationException e) {
                if (DEBUG) e.printStackTrace();
            } catch (IllegalAccessException e) {
                if (DEBUG) e.printStackTrace();
            } catch (InvocationTargetException e) {
                if (DEBUG) e.printStackTrace();
            }
        }
        if (DEBUG) Log.e(TAG, "fail createPackageParser");
        return null;
    }

    public static Object packageParser_parsePackage(Object obj, File sourceFile,
            String destCodePath, DisplayMetrics metrics, int flags) {
        if (sParsePackageMethod != null) {
            try {
                Method localMethod = sParsePackageMethod;
                Object[] arrayOfObject = new Object[] {sourceFile, destCodePath, metrics, flags};
                Object ret = localMethod.invoke(obj, arrayOfObject);
                return ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        // if anything wrong, will be here
        if (DEBUG) Log.e(TAG, "packageParser_parsePackage failure");
        return null;
    }

    public static Object packageParser_collectCertificates(Object obj, Object pkg, int flags) {
        if (sPackageParser_collectCertificatesMethod != null) {
            try {
                Method localMethod = sPackageParser_collectCertificatesMethod;
                Object[] arrayOfObject = new Object[] {pkg, flags};
                Object ret = localMethod.invoke(obj, arrayOfObject);
                return ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        if (DEBUG) Log.e(TAG, "packageParser_collectCertificates failure");
        return null;
    }

    /**
     * Cannot called in UI thread (ANR)
     */
    public static Signature[] getPackageArchiveSignature(Object packageParser, Object pkg) {
        if (pkg == null) {
            return null;
        }
        packageParser_collectCertificates(packageParser, pkg, 0);
        return package_signatures(pkg);
    }

    /**
     * Cannot called in UI thread (ANR)
     */
    public static Signature[] getPackageArchiveSignature(String archiveFilePath) {
        Object packageParser = createPackageParser(archiveFilePath);
        if (packageParser == null) {
            return null;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        final File sourceFile = new File(archiveFilePath);
        Object pkg = packageParser_parsePackage(packageParser, sourceFile,
                archiveFilePath, metrics, 0);
        return getPackageArchiveSignature(packageParser, pkg);
    }

    public static boolean packageManager_getPackageSizeInfo(PackageManager obj, String packageName,
            IPackageStatsObserver observer) {
        if (sGetPackageSizeInfoMethod != null) {
            try {
                Method localMethod = sGetPackageSizeInfoMethod;
                Object[] arrayOfObject = new Object[] {packageName, observer};
                localMethod.invoke(obj, arrayOfObject);
                return true;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        // if anything wrong, will be here
        if (DEBUG) Log.e(TAG, "packageManager_getPackageSizeInfo failure");
        return false;
    }

    public static boolean packageManager_freeStorageAndNotify(PackageManager obj, long size,
            IPackageDataObserver observer) {
        if (sFreeStorageAndNotifyMethod != null) {
            try {
                Method localMethod = sFreeStorageAndNotifyMethod;
                Object[] arrayOfObject = new Object[] {size, observer};
                localMethod.invoke(obj, arrayOfObject);
                return true;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        // if anything wrong, will be here
        if (DEBUG) Log.e(TAG, "packageManager_freeStorageAndNotify failure");
        return false;
    }

    public static int activityIntentInfo_countActions(Object obj) {
        if (sCountActionsMethod != null) {
            try {
                Method localMethod = sCountActionsMethod;
                Object[] arrayOfObject = new Object[0];
                Object ret = localMethod.invoke(obj, arrayOfObject);
                return (Integer) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        // if anything wrong, will be here
        if (DEBUG) Log.e(TAG, "activityIntentInfo_countActions failure");
        return 0;
    }

    public static String activityIntentInfo_getAction(Object obj, int i) {
        if (sGetActionMethod != null) {
            try {
                Method localMethod = sGetActionMethod;
                Object[] arrayOfObject = new Object[] { i };
                Object ret = localMethod.invoke(obj, arrayOfObject);
                return (String) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
        // if anything wrong, will be here
        if (DEBUG) Log.e(TAG, "activityIntentInfo_getAction failure");
        return null;
    }

    public static void package_setPackageName(Object obj, String pkgName) {
        if (sSetPackageNameMethod != null) {
            try {
                Method localMethod = sSetPackageNameMethod;
                Object[] arrayOfObjetc = new Object[] { pkgName };
                localMethod.invoke(obj, arrayOfObjetc);
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            } catch (InvocationTargetException localInvocationTargetException) {
                // ignore this, will to the final
            }
        }
    }

    public static ArrayList<?> package_receivers(Object obj) {
        if (sReceiversField != null) {
            try {
                Field localField = sReceiversField;
                Object ret = localField.get(obj);
                return (ArrayList<?>) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            }
        }
        if (DEBUG) Log.e(TAG, "package_receivers failure");
        return null;
    }

    public static Signature[] package_signatures(Object obj) {
        if (sPackageParser_SignaturesField != null) {
            try {
                Field localField = sPackageParser_SignaturesField;
                Object ret = localField.get(obj);
                return (Signature[]) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            }
        }
        if (DEBUG) Log.e(TAG, "package_signatures failure");
        return null;
    }

    public static ArrayList<String> package_permissions(Object obj) {
        if (sPackageParser_RequestedPermissionsField != null) {
            try {
                ArrayList<String> permissions = (ArrayList<String>) sPackageParser_RequestedPermissionsField.get(obj);
                return permissions;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            }
        }
        if (DEBUG) Log.e(TAG, "package_permissions failure");
        return null;
    }

    public static ApplicationInfo package_applicationInfo(Object obj) {
        if (sApplicationInfoField != null) {
            try {
                Field localField = sApplicationInfoField;
                Object ret = localField.get(obj);
                return (ApplicationInfo) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            }
        }
        if (DEBUG) Log.e(TAG, "package_applicationInfo failure");
        return null;
    }

    public static String package_packageName(Object obj) {
        if (sPackageNameField != null) {
            try {
                Field localField = sPackageNameField;
                Object ret = localField.get(obj);
                return (String) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            }
        }
        if (DEBUG) Log.e(TAG, "package_packageName failure");
        return null;
    }

    public static int package_mVersionCode(Object obj) {
        if (sMVersionCodeField != null) {
            try {
                Field localField = sMVersionCodeField;
                Object ret = localField.get(obj);
                return (Integer) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            }
        }
        if (DEBUG)
            Log.e(TAG, "package_mVersionCode failure");
        return -1;
    }

    public static ArrayList<?> activity_intents(Object obj) {
        if (sIntentsField != null) {
            try {
                Field localField = sIntentsField;
                Object ret = localField.get(obj);
                return (ArrayList<?>) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            }
        }
        if (DEBUG) Log.e(TAG, "activity_intents failure");
        return null;
    }

    public static ActivityInfo activity_info(Object obj) {
        if (sInfoField != null) {
            try {
                Field localField = sInfoField;
                Object ret = localField.get(obj);
                return (ActivityInfo) ret;
            } catch (IllegalAccessException localIllegalAccessException) {
                // ignore this, will to the final
            }
        }
        if (DEBUG) Log.d(TAG, "activity_info failure");
        return null;
    }

}