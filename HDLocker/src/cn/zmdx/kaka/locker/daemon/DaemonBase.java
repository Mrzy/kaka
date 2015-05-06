
package cn.zmdx.kaka.locker.daemon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class DaemonBase {
    // --------------------------------------------------------------------------------------------------------------

    private static final boolean SHOW_LOG = false;

    protected static String PATH_APP_VERSION_FILE = "APP1.ver"; // versionCode + lastUpdate
    protected static String PATH_SERVICE_PID_FILE = "VOICE1.pid";
    protected static String PATH_DAEMON_PID_FILE = "DAEMON1.pid";

    // --------------------------------------------------------------------------------------------------------------

    final static Thread.UncaughtExceptionHandler sErrorReportHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            String stackTrace = null;
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                pw.flush();
                pw.close();

                stackTrace = sw.getBuffer().toString();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            log("ZDaemon Crashed\n" + stackTrace);

            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };

    // --------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(sErrorReportHandler);

        // Log.i("[YC]ZDaemon", "ZayhuDaemon is starting");

        // parameter list
        // 0 Data path: check for uninstall
        // 1 APK full path: check for apk update / uninstall
        // 2 Token: additional information to tag the apk version, install time, signature, etc

        //    03-18 21:34:19.370 I/[YC]ZDaemon(32196): args[0] - /data/data/com.yeecall.app/
        //    03-18 21:34:19.370 I/[YC]ZDaemon(32196): args[1] - /data/app/com.yeecall.app-2.apk
        //    03-18 21:34:19.370 I/[YC]ZDaemon(32196): args[2] - 66f5c258cf7073d9f7586093c4e960156763b55e

        final int myPid = android.os.Process.myPid();
        final String paraStrDataDir = args[0];
        final String paraStrApkPath = args[1];
        final String paraAppHash = args[2];

        log("ZDaemon is starting, pid=" + myPid + ", data:" + paraStrDataDir + ", apk:" + paraStrApkPath + ", hash:"
                + paraAppHash);

        final File apkFile = new File(paraStrApkPath);
        final File dataPath = new File(paraStrDataDir);

        final File servicePidFile = new File(dataPath, PATH_SERVICE_PID_FILE);
        final File daemonPidFile = new File(dataPath, PATH_DAEMON_PID_FILE);
        final File appVersionFile = new File(dataPath, PATH_APP_VERSION_FILE);

        // initial known version/pid for this daemon
        writePidFile(appVersionFile, paraAppHash);
        writePidFile(daemonPidFile, myPid);

        while (true) {
            // 1. check for data directory first
            if (!dataPath.exists()) {
                if (apkFile.exists()) {
                    log("data is cleared by user, just exit");
                } else {
                    log("data path gone, and apk is uninstalled. exit");
                    // showUninstallSurvey();
                }

                return;
            }

            // 2. apk version hash
            String appVersionFromFile = readFromFile(appVersionFile);
            if (!paraAppHash.equals(appVersionFromFile)) {
                log("apk is upgraded, another daemon is started");
                return;
            }

            // 3. check for daemon pid
            int newPid = readPidFile(daemonPidFile);
            if (myPid != newPid) {
                log("another daemon is started, or daemon is requested to shutdown, exit: " + newPid);
                return;
            }

            // 4. check for apk file
            if (!apkFile.exists()) {
                log("apk file is missing, apk is upgrading, activate process & exit");

                // read again to confirm another daemon is not started
                newPid = readPidFile(daemonPidFile);
                if (myPid == newPid) {
                    daemonPidFile.delete();
                    // if another daemon started & write its pid during previous read/delete interval (10ms~100ms),
                    // both daemon will exit. it is a rare case, just ignore it, or wait for next SvcConnection.performLogin
                }

                // wait 15 seconds to re-activate apk process
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // check daemon again: exit directly if another is loaded
                newPid = readPidFile(daemonPidFile);
                if (newPid < 0 || newPid == myPid) {
                    activateProcess();
                }

                return;
            }

            // 5. finally, check target service
            if (!isProcessRunning(servicePidFile)) {
                activateProcess();
            }

            // log("I = " + i + ", sleeping ..." + newPid);

            try {
                Thread.sleep(15 * 1000); // 15 seconds
            } catch (InterruptedException e) {
            }
        }
    }

    private static void activateProcess() {
        log("now activating process ...");
        // re-activate service via following 2 commands
        // am start --user 0 -n com.yeecall.app/com.zayhu.ui.ZayhuSplashActivity --es from daemon --ez zayhu.extra.no_operation true --include-stopped-packages com.yeecall.app
        // am broadcast --user 0 -a zayhu.actions.ACTION_KEEP_ALIVE --es from daemon --include-stopped-packages com.yeecall.app

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

        String activateService = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activateService = "am broadcast --user 0 -a " + "zayhu.actions.ACTION_KEEP_ALIVE" + " --es from daemon "
                    + " --include-stopped-packages com.yeecall.app";
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            activateService = "am broadcast -a " + "zayhu.actions.ACTION_KEEP_ALIVE" + " --es from daemon "
                    + " --include-stopped-packages com.yeecall.app";
        } else {
            activateService = "am broadcast -a " + "zayhu.actions.ACTION_KEEP_ALIVE" + " --es from daemon";
        }

        PrintWriter pw = new PrintWriter(proc.getOutputStream());
        pw.println(activateService);
        pw.flush();
        pw.println("exit");
        pw.close();
    }


    // --------------------------------------------------------------------------------------------------------------
    // above this: daemon process
    // --------------------------------------------------------------------------------------------------------------
    // below this: common util functions for both com.yeecall.voice process & daemon
    // --------------------------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------------------------
    // common utils

    static void log(String str) {
        if (SHOW_LOG) {
            Log.i("[YC]ZayhuDaemon", "ZDaemon " + str);
        }
    }

    static void writePidFile(String filePath, int pid) {
        File f = new File(filePath);
        writePidFile(f, "" + pid);
    }

    static void writePidFile(File f, int pid) {
        writePidFile(f, "" + pid);
    }

    static void writePidFile(String filePath, String content) {
        File f = new File(filePath);
        writePidFile(f, content);
    }

    static void writePidFile(File f, String content) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write((content).getBytes());
            fos.flush();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    static int readPidFile(File pidFile) {
        int pid = -1;
        String pidString = readFromFile(pidFile);

        if (!TextUtils.isEmpty(pidString)) {
            try {
                pid = Integer.parseInt(pidString);
            } catch (Throwable e) {
            }
            return pid;
        }

        return pid;
    }

    static int readPidFile(String pidFile) {
        int pid = -1;
        String pidString = readFromFile(pidFile);

        if (!TextUtils.isEmpty(pidString)) {
            try {
                pid = Integer.parseInt(pidString);
            } catch (Throwable e) {
            }
            return pid;
        }

        return pid;
    }

    static boolean isProcessRunning(String pidFile) {
        int pid = -1;
        String pidString = readFromFile(pidFile);

        if (!TextUtils.isEmpty(pidString)) {
            try {
                pid = Integer.parseInt(pidString);
            } catch (Throwable e) {
            }
            return isProcessRunning(pid);
        }

        return false;
    }

    static boolean isProcessRunning(File pidFile) {
        int pid = -1;
        String pidString = readFromFile(pidFile);

        if (!TextUtils.isEmpty(pidString)) {
            // log("pidStr:" + pidString);

            try {
                pid = Integer.parseInt(pidString);
            } catch (Throwable e) {
                return false;
            }

            // log("check pid: " + pid + ", pidStr:" + pidString);

            return isProcessRunning(pid);
        }

        return false;
    }

    static String readFromFile(String file) {
        File f = new File(file);
        return readFromFile(f);
    }

    static String readFromFile(File f) {
        if (f.exists() && f.isFile()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(f));
                return br.readLine();
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Throwable e) {
                        // e.printStackTrace();
                    }
                }
            }
        }

        return "";
    }

    static boolean isProcessRunning(int pid) {
        if (pid <= 0) {
            return false;
        }

        // check file '/proc/${PID}/status'
        String statusFile = "/proc/" + pid + "/status";
        File f = new File(statusFile);

        // log(f + " exists: " + f.exists() + ", isFile: " + f.isFile());

        return (f.exists() && f.isFile());
    }

    // --------------------------------------------------------------------------------------------------------------
}
