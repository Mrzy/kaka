
package cn.zmdx.kaka.locker.meiwen.utils;

import android.app.ActivityManager;
import android.content.Context;

public class ProcessUtils {
    static String sMyProcessName = null;
    static String sMyProcessTag = null;

    public static String getProcessName(Context context) {
        if (ProcessUtils.sMyProcessName != null) {
            return ProcessUtils.sMyProcessName;
        }

        if (sMyProcessName == null) {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) context.getApplicationContext().getSystemService(
                    Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    ProcessUtils.sMyProcessName = appProcess.processName;
                    return ProcessUtils.sMyProcessName;
                }
            }
        }

        return "unknown";
    }

    public static String getProcessTag(Context context) {
        if (sMyProcessTag == null) {
            sMyProcessTag = HDBHashUtils.getStringMD5(getProcessName(context)).substring(0, 4);
        }
        return sMyProcessTag;
    }

    public static String getSafeProcessName(Context context) {
        String proc = getProcessName(context);
        return proc.replace(':', '-');
    }

    public static void killSelf() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
