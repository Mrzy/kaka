
package cn.zmdx.kaka.locker.notify.filter;

import java.lang.Character.UnicodeBlock;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.provider.ContactsContract.FullNameStyle;

@SuppressLint("DefaultLocale")
public class NameStyle {

    private static final String JAPANESE_LANGUAGE = Locale.JAPANESE.getLanguage().toLowerCase();

    private static final String KOREAN_LANGUAGE = Locale.KOREAN.getLanguage().toLowerCase();

    // This includes simplified and traditional Chinese
    private static final String CHINESE_LANGUAGE = Locale.CHINESE.getLanguage().toLowerCase();

    public static final int UNDEFINED = 0;

    public static final int WESTERN = 1;

    /**
     * Used if the name is written in Hanzi/Kanji/Hanja and we could not
     * determine which specific language it belongs to: Chinese, Japanese or
     * Korean.
     */
    public static final int CJK = 2;

    public static final int CHINESE = 3;

    public static final int JAPANESE = 4;

    public static final int KOREAN = 5;

    private String mLanguage;

    private static NameStyle sSingleton;

    private NameStyle() {
        setLocale(null);
    }

    public void setLocale(Locale currentLocale) {
        if (currentLocale == null) {
            mLanguage = Locale.getDefault().getLanguage().toLowerCase();
        } else {
            mLanguage = currentLocale.getLanguage().toLowerCase();
        }
    }

    public static synchronized NameStyle getIntance() {
        if (sSingleton == null) {
            sSingleton = new NameStyle();
        }
        return sSingleton;
    }

    public int guessNameStyle(String name) {
        if (name == null) {
            return NameStyle.UNDEFINED;
        }

        int nameStyle = NameStyle.UNDEFINED;
        int length = name.length();
        int offset = 0;
        while (offset < length) {
            int codePoint = Character.codePointAt(name, offset);
            if (Character.isLetter(codePoint)) {
                UnicodeBlock unicodeBlock = UnicodeBlock.of(codePoint);

                if (!isLatinUnicodeBlock(unicodeBlock)) {

                    if (isCJKUnicodeBlock(unicodeBlock)) {
                        // We don't know if this is Chinese, Japanese or Korean
                        // -
                        // trying to figure out by looking at other characters
                        // in the name
                        return guessCJKNameStyle(name, offset + Character.charCount(codePoint));
                    }

                    if (isJapanesePhoneticUnicodeBlock(unicodeBlock)) {
                        return NameStyle.JAPANESE;
                    }

                    if (isKoreanUnicodeBlock(unicodeBlock)) {
                        return NameStyle.KOREAN;
                    }
                }
                nameStyle = NameStyle.WESTERN;
            }
            offset += Character.charCount(codePoint);
        }
        return nameStyle;
    }

    /**
     * If the supplied name style is undefined, returns a default based on the
     * language, otherwise returns the supplied name style itself.
     * 
     * @param nameStyle See {@link FullNameStyle}.
     */
    public int getAdjustedNameStyle(int nameStyle) {
        if (nameStyle == NameStyle.UNDEFINED) {
            if (JAPANESE_LANGUAGE.equals(mLanguage)) {
                return NameStyle.JAPANESE;
            } else if (KOREAN_LANGUAGE.equals(mLanguage)) {
                return NameStyle.KOREAN;
            } else if (CHINESE_LANGUAGE.equals(mLanguage)) {
                return NameStyle.CHINESE;
            } else {
                return NameStyle.WESTERN;
            }
        } else if (nameStyle == NameStyle.CJK) {
            if (JAPANESE_LANGUAGE.equals(mLanguage)) {
                return NameStyle.JAPANESE;
            } else if (KOREAN_LANGUAGE.equals(mLanguage)) {
                return NameStyle.KOREAN;
            } else {
                return NameStyle.CHINESE;
            }
        }
        return nameStyle;
    }

    private int guessCJKNameStyle(String name, int offset) {
        int length = name.length();
        while (offset < length) {
            int codePoint = Character.codePointAt(name, offset);
            if (Character.isLetter(codePoint)) {
                UnicodeBlock unicodeBlock = UnicodeBlock.of(codePoint);
                if (isJapanesePhoneticUnicodeBlock(unicodeBlock)) {
                    return NameStyle.JAPANESE;
                }
                if (isKoreanUnicodeBlock(unicodeBlock)) {
                    return NameStyle.KOREAN;
                }
            }
            offset += Character.charCount(codePoint);
        }

        return NameStyle.CJK;
    }

    private boolean isLatinUnicodeBlock(UnicodeBlock unicodeBlock) {
        if (unicodeBlock == UnicodeBlock.BASIC_LATIN
                || unicodeBlock == UnicodeBlock.LATIN_1_SUPPLEMENT
                || unicodeBlock == UnicodeBlock.LATIN_EXTENDED_A) {
            return true;
        }
        if (unicodeBlock == UnicodeBlock.LATIN_EXTENDED_B
                || unicodeBlock == UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) {
            return true;
        }

        return false;
    }

    private boolean isCJKUnicodeBlock(UnicodeBlock block) {
        if (block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B) {
            return true;
        }
        if (block == UnicodeBlock.CJK_RADICALS_SUPPLEMENT
                || block == UnicodeBlock.CJK_COMPATIBILITY
                || block == UnicodeBlock.CJK_COMPATIBILITY_FORMS) {
            return true;
        }
        if (block == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || block == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
                || block == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) {
            return true;
        }

        return false;
    }

    private boolean isKoreanUnicodeBlock(UnicodeBlock unicodeBlock) {
        return unicodeBlock == UnicodeBlock.HANGUL_SYLLABLES
                || unicodeBlock == UnicodeBlock.HANGUL_JAMO
                || unicodeBlock == UnicodeBlock.HANGUL_COMPATIBILITY_JAMO;
    }

    private boolean isJapanesePhoneticUnicodeBlock(UnicodeBlock unicodeBlock) {
        if (unicodeBlock == UnicodeBlock.KATAKANA
                || unicodeBlock == UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS
                || unicodeBlock == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        if (unicodeBlock == UnicodeBlock.HIRAGANA) {
            return true;
        } else {
            return false;
        }
    }
}
