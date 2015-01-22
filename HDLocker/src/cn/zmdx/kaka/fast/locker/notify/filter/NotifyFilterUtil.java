
package cn.zmdx.kaka.fast.locker.notify.filter;

import java.util.ArrayList;

import android.text.TextUtils;

public class NotifyFilterUtil {
    private NotifyFilterUtil() {

    }

    public static String getChinesePinyinStr(String chineseStr) {
        return getPinyinStr(chineseStr);
    }

    private static String getPinyinStr(String srcString) {
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(srcString);
        if (tokens != null && tokens.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (HanziToPinyin.Token token : tokens) {
                if (HanziToPinyin.Token.PINYIN == token.getType()) {
                    if (!TextUtils.isEmpty(token.getTarget())) {
                        sb.append(token.getTarget());
                    }
                } else {
                    sb.append(token.getSource());
                }
            }
            return sb.toString();
        }
        return srcString;
    }
}
