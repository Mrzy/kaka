
package cn.zmdx.kaka.fast.locker.network;

import java.util.Map;

import cn.zmdx.kaka.fast.locker.BuildConfig;

public class UrlBuilder {

    private static String sBaseDebugUrl = "http://nb.hdlocker.com/pandora/locker!";

    private static String sBaseProdUrl = "http://pandora.hdlocker.com/pandora/locker!";

    public static String getBaseUrl() {
        if (BuildConfig.DEBUG) {
            return sBaseDebugUrl;
        } else {
            return sBaseProdUrl;
        }
    }

    /**
     * @deprecated
     * @param params url参数
     * @return
     */
    public static String getUrl(Map<String, String> params) {
        // TODO
        return null;
    }
}
