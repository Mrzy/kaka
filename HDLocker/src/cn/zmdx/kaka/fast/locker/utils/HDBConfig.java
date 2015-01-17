
package cn.zmdx.kaka.fast.locker.utils;

import cn.zmdx.kaka.fast.locker.BuildConfig;

public class HDBConfig {
    public static String ENV = "test";
    public static boolean IS_DEBUG = BuildConfig.DEBUG;
    public static boolean SHOULD_LOG = IS_DEBUG;
    public static boolean LOGE_ENABLED = IS_DEBUG;
}
