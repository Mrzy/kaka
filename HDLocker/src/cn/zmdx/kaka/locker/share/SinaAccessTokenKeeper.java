
package cn.zmdx.kaka.locker.share;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * 该类定义了sina微博授权时所需要的参数。
 */
public class SinaAccessTokenKeeper {
    private static final String PREFERENCES_NAME = "sina";

    private static final String KEY_UID = "uid";

    private static final String KEY_ACCESS_TOKEN = "access_token";

    private static final String KEY_EXPIRES_IN = "expires_in";

    private static final String KEY_USER_SSO_TIME = "userSSOTime";

    /**
     * 保存 Token 对象到 SharedPreferences。
     * 
     * @param context 应用程序上下文环境
     * @param token Token 对象
     */
    public static void writeAccessToken(Context context, Oauth2AccessToken token, long curTime) {
        if (null == context || null == token) {
            return;
        }

        SharedPreferences pref = context
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putString(KEY_UID, token.getUid());
        editor.putString(KEY_ACCESS_TOKEN, token.getToken());
        editor.putLong(KEY_EXPIRES_IN, token.getExpiresTime());
        editor.putLong(KEY_USER_SSO_TIME, curTime);
        editor.commit();
    }

    /**
     * 从 SharedPreferences 读取 Token 信息。
     * 
     * @param context 应用程序上下文环境
     * @return 返回 Token 对象
     */
    public static Oauth2AccessToken readAccessToken(Context context) {
        if (null == context) {
            return null;
        }

        Oauth2AccessToken token = new Oauth2AccessToken();
        SharedPreferences pref = context
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        token.setUid(pref.getString(KEY_UID, ""));
        token.setToken(pref.getString(KEY_ACCESS_TOKEN, ""));
        token.setExpiresTime(pref.getLong(KEY_EXPIRES_IN, 0));
        return token;
    }

    public static String getToken(Context context) {
        SharedPreferences pref = context
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        return pref.getString(KEY_ACCESS_TOKEN, "");
    }

    public static long getUserSSOTime(Context context) {
        SharedPreferences pref = context
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        return pref.getLong(KEY_USER_SSO_TIME, 0);
    }

    /**
     * 清空 SharedPreferences 中 Token信息。
     * 
     * @param context 应用程序上下文环境
     */
    public static void clear(Context context) {
        if (null == context) {
            return;
        }

        SharedPreferences pref = context
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
