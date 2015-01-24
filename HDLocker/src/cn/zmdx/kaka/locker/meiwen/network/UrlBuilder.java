
package cn.zmdx.kaka.locker.meiwen.network;

import java.util.Map;

import cn.zmdx.kaka.locker.meiwen.BuildConfig;
import cn.zmdx.kaka.locker.meiwen.HDApplication;
import cn.zmdx.kaka.locker.meiwen.utils.BaseInfoHelper;

public class UrlBuilder {

    private static String sBaseDebugUrl = "http://nb.hdlocker.com:8080/essay/";

    private static String sBaseProdUrl = "http://pandora.hdlocker.com:8080/essay/";

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
