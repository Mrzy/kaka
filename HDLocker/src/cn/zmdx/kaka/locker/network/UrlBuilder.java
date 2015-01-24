
package cn.zmdx.kaka.locker.network;

import java.util.Map;

import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

public class UrlBuilder {

    private static String sBaseDebugUrl = "http://nb.hdlocker.com/pandora/";

    private static String sBaseOldProdUrl = "http://pandora.hdlocker.com:8080/pandora/";

    private static String sBaseProdUrl = "http://pandora.hdlocker.com/pandora/";

    public static String getBaseUrl() {
        if (BuildConfig.DEBUG) {
            return sBaseDebugUrl;
        } else {
            if (BaseInfoHelper.getPkgVersionCode(HDApplication.getContext()) <= 85) {
                return sBaseOldProdUrl;
            } else {
                return sBaseProdUrl;
            }
        }
    }

    /**@deprecated
     * @param params url参数
     * @return
     */
    public static String getUrl(Map<String, String> params) {
        // TODO
        return null;
    }
}
