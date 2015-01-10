package cn.zmdx.kaka.locker.meiwen.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;


/**
 * Utility to obtain the system memory info (RAM).
 */
public class MemoryUtils {

    private static final String TAG = "MemoryUtils";
    private static final boolean DEBUG = HDBConfig.LOGE_ENABLED;

    /**
     * Get memory usage of one process.
     * @param context
     * @param pid The process ID
     * @return Memory usage in KB
     */
    public static int getProcessMemUsage(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        return getProcessMemUsage(am, pid);
    }

    /**
     * Get memory usage of one process.
     * @param am
     * @param pid The process ID
     * @return Memory usage in KB
     */
    public static int getProcessMemUsage(ActivityManager am, int pid) {
        Debug.MemoryInfo[] memInfo = am.getProcessMemoryInfo(new int[] { pid });
        return memInfo[0].getTotalPss(); // We should use "PSS" value
    }

    /**
     * Get private memory usage of one process.
     * @param am
     * @param pid The process ID
     * @return Memory usage in KB
     */
    public static int getPrivateProcessMemUsage(ActivityManager am, int pid) {
        Debug.MemoryInfo[] memInfo = am.getProcessMemoryInfo(new int[] { pid });
        return memInfo[0].getTotalPrivateDirty();
    }

    /**
     * Get memory usage of processes
     * @param context
     * @param pids The process IDs
     * @return Memory usage in KB for all processes
     */
    public static int[] getProcessMemUsage(Context context, int[] pids) {
        ActivityManager am = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        return getProcessMemUsage(am, pids);
    }

    /**
     * Get memory usage of processes
     * @param am
     * @param pids The process IDs
     * @return Memory usage in KB for all processes
     */
    public static int[] getProcessMemUsage(ActivityManager am, int[] pids) {
        Debug.MemoryInfo[] memInfo = am.getProcessMemoryInfo(pids);
        int[] result = new int[memInfo.length];
        for (int i = 0; i < memInfo.length; ++i) {
            // we should use "PSS" value
            result[i] = memInfo[i].getTotalPss();
        }
        return result;
    }

    /**
     * get default input method package name, we will never stop this one
     * @param ctx
     * @return the package name, if nothing, return ""
     */
    public static String getCurrentImePackage(Context ctx) {
        String imeid = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodProperties = imm.getEnabledInputMethodList();
        final int N = mInputMethodProperties.size();
        for (int i = 0; i < N; i++) {
            InputMethodInfo imi = mInputMethodProperties.get(i);
            if (imi.getId().equals(imeid)) {
                return imi.getPackageName();
            }
        }
        return "";
    }

    /**
     * Get system memory info in KB.
     * @return An array with two elements: the first one is available memory in KB;
     *         the second one is total memory in KB.
     */
    public static int[] getSystemMemory() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/meminfo"));
            int memAvail = 0;
            int memTotal = 0;
            String line = null;
            int matchCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains("MemTotal")) {
                    matchCount++;
                    memTotal = StringUtils.extractPositiveInteger(line, 0);
                } else if (line.contains("MemFree")) {
                    matchCount++;
                    memAvail += StringUtils.extractPositiveInteger(line, 0);
                } else if (line.contains("Cached")) {
                    matchCount++;
                    memAvail += StringUtils.extractPositiveInteger(line, 0);
                }
                if (matchCount == 3) {
                    break;
                }
            }
            if (memAvail > 0 && memTotal > 0) {
                return new int[] {memAvail, memTotal};
            }
        } catch (Exception e) {
            // ignore the exception
        } finally {
            FileHelper.close(reader);
        }
        return new int[] {0, 0};
    }

    // for regular time clean
    public static boolean isLowerMemory() {
        int[] memoryInfo = MemoryUtils.getSystemMemory();
        int percent = (int)(memoryInfo[0] * 1f / (memoryInfo[1] > 0 ? memoryInfo[1] : 1) * 100);
        if (percent <= 20) {
            return true;
        }
        return false;
    }

    /**
     * Get LowerMemeKillerThreshold memory info in KB.
     * @return memory size in KB.
     */
    public static int getLowerMemeKillerThreshold() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/sys/module/lowmemorykiller/parameters/minfree"));
            String line = null;
            if ((line = reader.readLine()) != null) {
                String tArray[] = line.split(",");
                if (tArray != null && tArray.length >0) {
                    if (DEBUG) {
                        HDBLOG.logE("line-----" + line + " :" + tArray[tArray.length-1]);
                    }
                    return StringUtils.parseInt(tArray[tArray.length-1]);
                }
            }
        } catch (java.io.FileNotFoundException e) {
            // ignore the exception
        } catch (java.io.IOException e) {
            // ignore the exception
        } finally {
            FileHelper.close(reader);
        }
        return 0;
    }
}
