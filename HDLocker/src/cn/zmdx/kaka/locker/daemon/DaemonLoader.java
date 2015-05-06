
package cn.zmdx.kaka.locker.daemon;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.text.TextUtils;


public class DaemonLoader extends DaemonBase {
    // --------------------------------------------------------------------------------------------------------------
    // cn.zmdx.kaka.locker process
    // --------------------------------------------------------------------------------------------------------------

    public static void startDaemonIfNeeded() {
        startDaemonIfNeeded(false);
    }

    public synchronized static void startDaemonIfNeeded(boolean allowSurvivalMode) {
        if (Build.VERSION.SDK_INT >= 20) {
            // not possible on android 5.0, do not try, do not waste power
            return;
        }

        final Context ctx = Utilities.getApplicationContext();
        if (ctx == null) {
            return;
        }

        final File filesRoot = ctx.getFilesDir();
        if (!filesRoot.exists()) {
            return;
        }

        boolean shouldStartDaemon = false;

        // 0. write service pid to file
        updateServicePID();

        // 1. check for huawei phones: using survival mode
        if (allowSurvivalMode) {
            // HuaWei Phones only
            // when swipped from history list or clean all from history list, following 28763/28794/28826 process will be killed
            // so we need always start new daemon to restore correct state to survive from murder
            //
            //    u0_a185   28763 2441  926384 61556 ffffffff 00000000 S com.yeecall.app
            //    u0_a185   28794 2441  943336 63940 ffffffff 00000000 S com.yeecall.voice
            //    u0_a185   28826 28794 928    328   ffffffff 00000000 S /system/bin/sh
            //    u0_a185   28843 28826 829952 23908 ffffffff 00000000 S /system/bin/app_process

            if (Build.FINGERPRINT.toLowerCase().contains("huawei")) {
                LOG.logI("huawei phones detected, always restart daemon");
                shouldStartDaemon = true;
            }
        }

        // 2. read & check app hash
        if (!shouldStartDaemon) {
            final File appHashFile = new File(filesRoot, PATH_APP_VERSION_FILE);
            String appHashFromFile = readFromFile(appHashFile);
            String appHashExpected = getAppHash();
            if (TextUtils.isEmpty(appHashFromFile) || !appHashFromFile.equals(appHashExpected)) {
                LOG.logI("app hash not match: " + appHashFromFile + ", expected: " + appHashExpected);
                shouldStartDaemon = true;
            }
        }

        // 3. read & check daemon pid
        if (!shouldStartDaemon) {
            final File daemonPidFile = new File(filesRoot, PATH_DAEMON_PID_FILE);
            shouldStartDaemon = !isProcessRunning(daemonPidFile);
            if (shouldStartDaemon) {
                LOG.logI("daemon process not found");
            }
        }

        // finally
        if (shouldStartDaemon) {
            startDaemon();
        }

        if (allowSurvivalMode && shouldStartDaemon) {
            LOG.logI("survival mode enabled, do not create process too fast");
            try {
                Thread.sleep(15 * 1000);
            } catch (InterruptedException e) {
            }
        }
    }

    private static void startDaemon() {

        // prepare startup parameters, this runs in com.yeecall.voice process
        final Context ctx = Utilities.getApplicationContext();
        if (ctx == null) {
            return;
        }

        final File filesRoot = ctx.getFilesDir();
        if (!filesRoot.exists()) {
            return;
        }

        PackageInfo pi = null;
        try {
            pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
        } catch (Throwable e) {
            pi = null;
        }

        if (pi == null || pi.applicationInfo == null) {
            return;
        }

        final String paraAPKFile = pi.applicationInfo.sourceDir;

        try {
            startDaemon(filesRoot.getCanonicalPath(), paraAPKFile);
        } catch (Throwable e) {
            LOG.logI("error starting daemon", e);
        }
    }

    private static void startDaemon(String dataPath, String apkFile) {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec("/system/bin/sh");
        } catch (IOException e) {
            e.printStackTrace();
            proc = null;
        }

        if (proc == null) {
            return;
        }

        PackageInfo pi = null;
        try {
            Context ctx = Utilities.getApplicationContext();
            PackageManager pm = ctx.getPackageManager();
            pi = pm.getPackageInfo(ctx.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        if (pi == null) {
            return;
        }

        // write pid tag before starting process
        updateServicePID();

        String apkPath = pi.applicationInfo.sourceDir;
        int pos = apkPath.lastIndexOf('/');
        String apkDir = pos >= 0 ? apkPath.substring(0, pos) : apkPath;

        String cmdline = "/system/bin/app_process " + apkDir + " " + DaemonBase.class.getCanonicalName()
                + " " + dataPath + " " + apkFile + " " + getAppHash();

        PrintWriter pw = new PrintWriter(proc.getOutputStream());
        pw.println("export CLASSPATH=" + pi.applicationInfo.sourceDir);
        pw.println(cmdline);
        pw.println("exit");
        pw.close();

        LOG.logI("daemon started, wait for execution ...");
    }

    public static void stopDaemon() {

        // check if daemon running
        final Context ctx = Utilities.getApplicationContext();
        if (ctx == null) {
            return;
        }

        final File filesRoot = ctx.getFilesDir();
        if (!filesRoot.exists()) {
            return;
        }

        // delete daemon file to ask it stop
        final File daemonFile = new File(filesRoot, PATH_DAEMON_PID_FILE);
        daemonFile.delete();
    }

    public static boolean isDaemonRunning() {
        final Context ctx = Utilities.getApplicationContext();
        if (ctx == null) {
            return true;
        }

        final File filesRoot = ctx.getFilesDir();
        if (!filesRoot.exists()) {
            return true;
        }

        final File paraDaemonPidFile = new File(filesRoot, PATH_DAEMON_PID_FILE);
        if (!paraDaemonPidFile.exists()) {
            return false;
        }

        String daemonPidString = readFromFile(paraDaemonPidFile);
        LOG.logI("file: " + paraDaemonPidFile);
        if (!TextUtils.isEmpty(daemonPidString)) {
            int pid = -1;
            try {
                pid = Integer.parseInt(daemonPidString);
            } catch (Throwable e) {
                LOG.logI("error parse file: " + daemonPidString);
                return true;
            }

            LOG.logI("daemon pid: " + pid);
            return isProcessRunning(pid);
        }

        return true;
    }

    static void updateServicePID() {
        final Context ctx = Utilities.getApplicationContext();
        if (ctx == null) {
            return;
        }

        final File filesRoot = ctx.getFilesDir();
        if (!filesRoot.exists()) {
            return;
        }

        // write pid tag before starting process
        final File paraServicePidFile = new File(filesRoot, PATH_SERVICE_PID_FILE);
        writePidFile(paraServicePidFile, android.os.Process.myPid());
    }

    static String sAppHash = null;

    static String getAppHash() {
        if (sAppHash == null) {
            // no randomness allowed here
//            try {
//                Context ctx = Utilities.getApplicationContext();
//                String packageName = ctx.getPackageName();
//                PackageInfo pi = ctx.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
//                String cert = HashUtils.getBytesSHA1(pi.signatures[0].toByteArray());
//
//                String hashTarget = packageName + "_" + cert + "_" + pi.versionCode + "_"
//                        + PackageInfoCompat.getLastUpdateTime(pi);
//
//                sAppHash = HashUtils.getStringUTF8SHA1(hashTarget);
//            } catch (Throwable e) {
//                sAppHash = "E01CC792-4275-423A-A0CD-2ED6249E872F_" + PackageUtils.getVersionCode();
//            }
        }

        return sAppHash;
    }

    // --------------------------------------------------------------------------------------------------------------
}
