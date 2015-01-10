
package cn.zmdx.kaka.locker.meiwen.utils;

class ShowExceptionRunnable implements Runnable {
    private final long MIN_JOB_DURATION_TO_LOG = 1000;

    private boolean mCheckDuration = false;

    private final Runnable mOrigin;
    private Exception mStackTrace;

    public ShowExceptionRunnable(Runnable ori) {
        if (ori == null) {
            throw new NullPointerException("invalid argument: ori=null");
        }

        mOrigin = ori;
        mCheckDuration = HDBConfig.SHOULD_LOG;
        mStackTrace = new Exception("Stack trace of " + ori);
    }

    public ShowExceptionRunnable(Runnable ori, boolean checkDuration) {
        if (ori == null) {
            throw new NullPointerException("invalid argument: ori=null");
        }

        mOrigin = ori;
        mCheckDuration = checkDuration;
        if (mCheckDuration) {
            mStackTrace = new Exception("Stack trace of " + ori);
        }
    }

    @Override
    public void run() {
        long start = mCheckDuration ? System.currentTimeMillis() : 0;
        try {
            mOrigin.run();
        } catch (Throwable e) {
            if (HDBConfig.SHOULD_LOG) {
                HDBLOG.logE("++++++++++++++++++ Throwable catched during execution: " + mOrigin, e);
                if (mCheckDuration) {
                    HDBLOG.logE("++++++++++++++++++ Job posted in: ", mStackTrace);
                }
            }

            throw new RuntimeException(e);
        } finally {
            if (mCheckDuration && HDBConfig.SHOULD_LOG) {
                long end = System.currentTimeMillis();
    
                if (end - start > MIN_JOB_DURATION_TO_LOG) {
                    StackTraceElement[] traces = mStackTrace.getStackTrace();
                    if (traces == null || traces.length < 3) {
                        return;
                    }

                    StackTraceElement target = traces[2];
                    if (target == null) {
                        return;
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append(target.getClassName()).append('.').append(target.getMethodName()).append('[')
                            .append(target.getFileName()).append(':').append(target.getLineNumber()).append(']');

                    HDBLOG.logI("Job created at: " + sb + " takes too long to complete: " + (end - start) + "ms.");
                }
            }

            mStackTrace = null;
        }
    }

    @Override
    public String toString() {
        return "SER: {" + mOrigin.toString() + "}";
    }
}