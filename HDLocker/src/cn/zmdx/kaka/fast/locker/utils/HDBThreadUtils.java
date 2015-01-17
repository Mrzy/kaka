
package cn.zmdx.kaka.fast.locker.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class HDBThreadUtils {

    public static void ensureUiThread() {
        if (!HDBThreadUtils.isUiThread()) {
            throw new IllegalStateException("ensureUiThread: thread check failed");
        }
    }

    public static void ensureNonUiThread() {
        if (HDBThreadUtils.isUiThread()) {
            throw new IllegalStateException("ensureNonUiThread: thread check failed");
        }
    }

    public static boolean isUiThread() {
        final Looper myLooper = Looper.myLooper();
        final Looper mainLooper = Looper.getMainLooper(); // never null
    
        return mainLooper.equals(myLooper);
    }

    private static Handler sUiHandler = null;
    private static HandlerThread sHandlerThread = null;
    private static Handler sWorkerHandler = null;

    static {
        // ui thread runner
        sUiHandler = new Handler(Looper.getMainLooper());

        // handler based thread runner
        sHandlerThread = new HandlerThread("thread");
        sHandlerThread.setPriority(Thread.NORM_PRIORITY - 2);
        sHandlerThread.start();
        sWorkerHandler = new Handler(sHandlerThread.getLooper());
    }

    public static void runOnUi(Runnable r) {
        if (HDBConfig.IS_DEBUG) {
            sUiHandler.post(new ShowExceptionRunnable(r));
        } else {
            sUiHandler.post(r);
        }
    }

    public static void postOnUiDelayed(Runnable r, int delay) {
        if (HDBConfig.IS_DEBUG) {
            sUiHandler.postDelayed(new ShowExceptionRunnable(r), delay);
        } else {
            sUiHandler.postDelayed(r, delay);
        }
    }

    public static void runOnWorkerWithPriority(Runnable r) {
        if (HDBConfig.IS_DEBUG) {
            sWorkerHandler.postAtFrontOfQueue(new ShowExceptionRunnable(r));
        } else {
            sWorkerHandler.postAtFrontOfQueue(r);
        }
    }

    public static void runOnWorker(Runnable r) {
        if (HDBConfig.IS_DEBUG) {
            sWorkerHandler.post(new ShowExceptionRunnable(r));
        } else {
            sWorkerHandler.post(r);
        }
    }

    public static void postOnWorkerDelayed(Runnable r, int delay) {
        if (HDBConfig.IS_DEBUG) {
            sWorkerHandler.postDelayed(new ShowExceptionRunnable(r), delay);
        } else {
            sWorkerHandler.postDelayed(r, delay);
        }
    }

    public static Looper getWorkerLooper() {
        return sHandlerThread.getLooper();
    }

    public static Handler getWorkerHandler() {
        return sWorkerHandler;
    }
}
