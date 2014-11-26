
package cn.zmdx.kaka.locker.network;

import java.util.Map;

import cn.zmdx.kaka.locker.BuildConfig;

public class UrlBuilder {

    private static String sBaseDebugUrl = "http://nb.hdlocker.com/pandora/locker!queryDataImgTable.action?";

    private static String sBaseProdUrl = "http://pandora.hdlocker.com:8080/pandora/locker!queryDataImgTable.action?";

    public static String getBaseUrl() {
        if (BuildConfig.DEBUG) {
            return sBaseDebugUrl;
        } else {
            return sBaseProdUrl;
        }
    }

    /**
     * @param params url参数
     * @return
     */
    public static String getUrl(Map<String, String> params) {
        // TODO
        return null;
    }
}
