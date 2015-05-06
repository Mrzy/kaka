
package cn.zmdx.kaka.locker.network;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

public class UrlBuilder {

    private static String sBaseDebugUrl = "http://nb.hdlocker.com/pandora/";

    private static String sBaseProdUrl = "http://pandora.hdlocker.com/pandora/";

    public static String getBaseUrl(String action) {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.DEBUG ? sBaseDebugUrl : sBaseProdUrl);
        if (TextUtils.isEmpty(action)) {
            throw new IllegalArgumentException("action must not be empty");
        } else {
            sb.append(action);
            if (action.contains("?") && !action.endsWith("?")) {
                sb.append("&");
            } else if (action.endsWith("?")) {
                throw new IllegalArgumentException("action must not end with '?'");
            } else {
                sb.append("?");
            }
            sb.append(getPrefixParam());
        }
        return sb.toString();
    }

    private static SoftReference<String> sPrefixParams;

    private static String getPrefixParam() {
        if (sPrefixParams != null && sPrefixParams.get() != null) {
            return sPrefixParams.get();
        } else {
            Context context = HDApplication.getContext();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pkgName", BaseInfoHelper.getPkgName(context)));
            params.add(new BasicNameValuePair("vCode", BaseInfoHelper.getPkgVersionCode(context)
                    + ""));
            params.add(new BasicNameValuePair("vName", BaseInfoHelper.getPkgVersionName(context)));
            params.add(new BasicNameValuePair("model", BaseInfoHelper.getModel(context)));
            params.add(new BasicNameValuePair("manuf", BaseInfoHelper.getManufacturer(context)));// MANUFACTURER
            params.add(new BasicNameValuePair("imei", BaseInfoHelper.getIMEI(context)));
            params.add(new BasicNameValuePair("imsi", BaseInfoHelper.getIMSI(context)));
            params.add(new BasicNameValuePair("andVer", BaseInfoHelper.getAndroidVersion(context)));
            params.add(new BasicNameValuePair("dpi", BaseInfoHelper.getDpi(context)));
            params.add(new BasicNameValuePair("loc", BaseInfoHelper.getLocale(context)));
            params.add(new BasicNameValuePair("sign", BaseInfoHelper.getSignature(context)));
            params.add(new BasicNameValuePair("w", BaseInfoHelper.getRealWidth(context) + ""));
            params.add(new BasicNameValuePair("h", BaseInfoHelper.getRealHeight(context) + ""));

            String pp = URLEncodedUtils.format(params, "UTF-8");
            sPrefixParams = new SoftReference<String>(pp);
            return pp;
        }
    }
}
