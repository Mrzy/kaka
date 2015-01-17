
package cn.zmdx.kaka.fast.locker.network;

import java.util.Map;

import cn.zmdx.kaka.fast.locker.HDApplication;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.fast.locker.BuildConfig;

public class UrlBuilder {

    private static String sBaseDebugUrl = "http://nb.hdlocker.com/pandora/locker!";

    private static String sBaseOldProdUrl = "http://pandora.hdlocker.com:8080/pandora/locker!";

    private static String sBaseProdUrl = "http://pandora.hdlocker.com/pandora/locker!";

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
