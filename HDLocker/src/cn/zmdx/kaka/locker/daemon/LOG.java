
package cn.zmdx.kaka.locker.daemon;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

import android.util.Log;

public class LOG {

    public static boolean IS_DEBUG = true;
    public static final int INFO = 0;
    public static final int DEBUG = 1;
    public static final int ERROR = 2;
    public static final int FILE_ONLY = 3;
    public static final int ALL = -1;

    public static int CURRENT_STATE = ALL;

    private static int sLogCount = 0;

    static ThreadLocal<StringBuilder> sLocalLogBuilder = new ThreadLocal<StringBuilder>();

    private static void logInternal(int logLevel, String msg) {
        final Throwable t = IS_DEBUG ? new Throwable() : null;
        final StackTraceElement[] elements = t != null ? t.getStackTrace() : null;

        String callerClassName = t != null ? elements[2].getClassName() : "N/A";
        String callerMethodName = t != null ? elements[2].getMethodName() : "N/A";

        if (IS_DEBUG) {
            int pos = callerClassName.lastIndexOf('.');
            if (pos >= 0) {
                callerClassName = callerClassName.substring(pos + 1);
            }
        }

        final String tag = callerClassName;
        final String message;

        StringBuilder logBuilder = sLocalLogBuilder.get();

        if (logBuilder == null) {
            logBuilder = new StringBuilder();
            sLocalLogBuilder.set(logBuilder);
        }

        synchronized (logBuilder) {
            logBuilder.setLength(0);
            logBuilder.append("[").append(sLogCount).append("][").append(callerMethodName).append("] ").append(msg);
            message = logBuilder.toString();
            sLogCount++;
        }

        switch (logLevel) {
            case INFO:
                Log.i(tag, message);
                break;
            case DEBUG:
                Log.d(tag, message);
                break;
            case ERROR:
                Log.e(tag, message);
                break;
        }
    }

    /**
     * Informational message
     * 
     * @param msg
     */
    public static void logI(String msg) {
        switch (CURRENT_STATE) {
            case ALL:
            case INFO:
                logInternal(INFO, msg);
        }
    }

    /**
     * debug message
     * 
     * @param msg
     */
    public static void logD(String msg) {
        switch (CURRENT_STATE) {
            case ALL:
            case INFO:
            case DEBUG:
                logInternal(DEBUG, msg);
        }
    }

    /**
     * Error message, would always show in logcat; use with care
     * 
     * @param msg
     */
    public static void logE(String msg) {
        switch (CURRENT_STATE) {
            case ALL:
            case INFO:
            case DEBUG:
            case ERROR:
                logInternal(ERROR, msg);
        }
    }

    public static void logI(String msg, Throwable tr) {
        switch (CURRENT_STATE) {
            case ALL:
            case INFO:
                logInternal(INFO, msg + "\n" + getStackTraceString(tr));
        }

    }

    public static void logD(String msg, Throwable tr) {
        switch (CURRENT_STATE) {
            case ALL:
            case INFO:
            case DEBUG:
                logInternal(DEBUG, msg + "\n" + getStackTraceString(tr));
        }
    }

    public static void logE(String msg, Throwable tr) {
        switch (CURRENT_STATE) {
            case ALL:
            case INFO:
            case DEBUG:
            case ERROR:
                logInternal(ERROR, msg + "\n" + getStackTraceString(tr));
        }
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

}
