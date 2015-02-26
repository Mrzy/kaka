
package cn.zmdx.kaka.locker.notify.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.text.TextUtils;

/**
 * This utility class provides customized sort key and name lookup key according
 * the locale.
 */
public final class LocaleUtils {

    /**
     * This class is the default implementation.
     * <p>
     * It should be the base class for other locales' implementation.
     */
    public class MediaLocaleUtilsBase {
        public String getSortKey(String displayName) {
            return displayName;
        }
    }

    /**
     * The classes to generate the Chinese style sort and search keys.
     * <p>
     * The sorting key is generated as each Chinese character' pinyin proceeding
     * with space and character itself. If the character's pinyin unable to
     * find, the character itself will be used.
     * <p>
     * The below additional name lookup keys will be generated. a. Chinese
     * character's pinyin and pinyin's initial character. b. Latin word and the
     * initial character for Latin word. The name lookup keys are generated to
     * make sure the name can be found by from any initial character.
     */
    private class ChineseContactUtils extends MediaLocaleUtilsBase {
        @Override
        public String getSortKey(String displayName) {

            ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(displayName);
            if (tokens != null && tokens.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (HanziToPinyin.Token token : tokens) {
                    // Put Chinese character's pinyin, then proceed with the
                    // character itself.
                    if (HanziToPinyin.Token.PINYIN == token.getType()) {
                        if (sb.length() > 0) {
                            sb.append(' ');
                        }
                        sb.append(token.getTarget());
                        // sb.append(' ');
                        // sb.append(token.source);
                    } else {
                        if (sb.length() > 0) {
                            sb.append(' ');
                        }
                        sb.append(token.getSource());
                    }
                }
                return sb.toString();
            }
            return super.getSortKey(displayName);
        }

        public String getFirstCharStr(String srcString) {
            ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(srcString);
            if (tokens != null && tokens.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (HanziToPinyin.Token token : tokens) {
                    if (HanziToPinyin.Token.PINYIN == token.getType()) {
                        if (!TextUtils.isEmpty(token.getTarget())) {
                            sb.append(token.getTarget().charAt(0));
                        } else {
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

        public String getPinyinStr(String srcString) {
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

    private static final String JAPANESE_LANGUAGE = Locale.JAPANESE.getLanguage().toLowerCase();

    private static final String KOREAN_LANGUAGE = Locale.KOREAN.getLanguage().toLowerCase();

    private static LocaleUtils sSingleton;

    private HashMap<Integer, MediaLocaleUtilsBase> mUtils = new HashMap<Integer, MediaLocaleUtilsBase>();

    private MediaLocaleUtilsBase mBase = new MediaLocaleUtilsBase();

    private String mLanguage;

    private LocaleUtils() {
        setLocale(null);
    }

    public void setLocale(Locale currentLocale) {
        if (currentLocale == null) {
            mLanguage = Locale.getDefault().getLanguage().toLowerCase();
        } else {
            mLanguage = currentLocale.getLanguage().toLowerCase();
        }
    }

    public String getSortKey(String displayName, int nameStyle) {
        return getForSort(Integer.valueOf(nameStyle)).getSortKey(displayName);
    }

    /**
     * Determine which utility should be used for generating NameLookupKey.
     * <p>
     * a. For Western style name, if the current language is Chinese, the
     * ChineseContactUtils should be used. b. For Chinese and CJK style name if
     * current language is neither Japanese or Korean, the ChineseContactUtils
     * should be used.
     */
    private synchronized MediaLocaleUtilsBase get(Integer nameStyle) {
        MediaLocaleUtilsBase utils = mUtils.get(nameStyle);
        if (utils == null && nameStyle.intValue() == NameStyle.CHINESE
                || nameStyle.intValue() == NameStyle.JAPANESE) {
            utils = new ChineseContactUtils();
            mUtils.put(nameStyle, utils);
        }
        return (utils == null) ? mBase : utils;
    }

    /**
     * Determine the which utility should be used for generating sort key.
     * <p>
     * For Chinese and CJK style name if current language is neither Japanese or
     * Korean, the ChineseContactUtils should be used.
     */
    private MediaLocaleUtilsBase getForSort(Integer nameStyle) {
        return get(Integer.valueOf(getAdjustedStyle(nameStyle.intValue())));
    }

    public static synchronized LocaleUtils getIntance() {
        if (sSingleton == null) {
            sSingleton = new LocaleUtils();
        }
        return sSingleton;
    }

    private int getAdjustedStyle(int nameStyle) {
        if (nameStyle == NameStyle.CJK && !JAPANESE_LANGUAGE.equals(mLanguage)
                && !KOREAN_LANGUAGE.equals(mLanguage)) {
            return NameStyle.CHINESE;
        } else {
            return nameStyle;
        }
    }

    public String getChinesePinyinFirstCharStr(String srcString) {
        return new ChineseContactUtils().getFirstCharStr(srcString);
    }

    public String getChinesePinyinStr(String srcString) {
        return new ChineseContactUtils().getPinyinStr(srcString);
    }
}
