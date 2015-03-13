
package cn.zmdx.kaka.locker.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import cn.zmdx.kaka.locker.widget.LockPatternView;

public class LockPatternManager {

    public static final int LOCK_PATTERN_STYLE_White = -1;

    public static final int LOCK_PATTERN_STYLE_PURE = 0;

    public static final int LOCK_PATTERN_STYLE_NEON = 1;

    public static final int LOCK_PATTERN_STYLE_FLORESCENCE = 2;

    public static final int LOCK_PATTERN_STYLE_TOUCH = 3;

    public static final int LOCK_PATTERN_STYLE_DEEPSEA = 4;

    public static final int LOCK_PATTERN_STYLE_MIDSUMMER = 5;

    private static LockPatternManager mInstance;

    public static LockPatternManager getInstance() {
        if (null == mInstance) {
            mInstance = new LockPatternManager();
        }
        return mInstance;
    }

    private String[][] mWhiteColors = {
            {
                    "#ffffff", "#ffffff", "#ffffff"
            }, {
                    "#ffffff", "#ffffff", "#ffffff"
            }, {
                    "#ffffff", "#ffffff", "#ffffff"
            },
    };

    private String[][] mPureColors = {
            {
                    "#d5d5d5", "#d5d5d5", "#d5d5d5"
            }, {
                    "#d5d5d5", "#d5d5d5", "#d5d5d5"
            }, {
                    "#d5d5d5", "#d5d5d5", "#d5d5d5"
            },
    };

    private String[][] mNeonColors = {
            {
                    "#f34743", "#ec932b", "#eecc2d"
            }, {
                    "#9ed74a", "#31dedc", "#18aae6"
            }, {
                    "#9f48e4", "#de28b9", "#244980"
            },
    };

    private String[][] mFlorescenceColors = {
            {
                    "#f20d5f", "#ee739f", "#ffcde0"
            }, {
                    "#ffb9d3", "#c7386d", "#ec739e"
            }, {
                    "#ba748e", "#ffcde0", "#ffa0c2"
            },
    };

    private String[][] mTouchColors = {
            {
                    "#a06845", "#ebd9ab", "#619c8a"
            }, {
                    "#ddc07a", "#94d1bf", "#bf9b45"
            }, {
                    "#a5b064", "#d78966", "#eab651"
            },
    };

    private String[][] mDeepSeaColors = {
            {
                    "#54edff", "#0082ce", "#00d8ff"
            }, {
                    "#00d8ff", "#54edff", "#0082ce"
            }, {
                    "#0082ce", "#54edff", "#00d8ff"
            },
    };

    private String[][] mMidsummerColors = {
            {
                    "#b0e511", "#e75819", "#e7ba19"
            }, {
                    "#e72c19", "#f09a11", "#6fbd35"
            }, {
                    "#d7de76", "#8b9131", "#c95725"
            },
    };

    public int[][] getLockPatternStyle(Context mContext, int style) {
        int mRegularDotColor[][] = new int[3][3];
        if (style == LOCK_PATTERN_STYLE_PURE) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    mRegularDotColor[i][j] = Color.parseColor(mPureColors[i][j]);
                }
            }
        } else if (style == LOCK_PATTERN_STYLE_NEON) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    mRegularDotColor[i][j] = Color.parseColor(mNeonColors[i][j]);
                }
            }
        } else if (style == LOCK_PATTERN_STYLE_FLORESCENCE) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    mRegularDotColor[i][j] = Color.parseColor(mFlorescenceColors[i][j]);
                }
            }
        } else if (style == LOCK_PATTERN_STYLE_TOUCH) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    mRegularDotColor[i][j] = Color.parseColor(mTouchColors[i][j]);
                }
            }
        } else if (style == LOCK_PATTERN_STYLE_DEEPSEA) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    mRegularDotColor[i][j] = Color.parseColor(mDeepSeaColors[i][j]);
                }
            }
        } else if (style == LOCK_PATTERN_STYLE_MIDSUMMER) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    mRegularDotColor[i][j] = Color.parseColor(mMidsummerColors[i][j]);
                }
            }
        } else if (style == LOCK_PATTERN_STYLE_White) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    mRegularDotColor[i][j] = Color.parseColor(mWhiteColors[i][j]);
                }
            }
        }

        return mRegularDotColor;
    }

    /**
     * Deserialize a pattern.
     * 
     * @param string The pattern serialized with {@link #patternToString}
     * @return The pattern.
     */
    public List<LockPatternView.Cell> stringToPattern(String string) {
        List<LockPatternView.Cell> result = new ArrayList<LockPatternView.Cell>();

        final byte[] bytes = string.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            result.add(LockPatternView.Cell.of(b / 3, b % 3));
        }
        return result;
    }

    /**
     * Serialize a pattern.
     * 
     * @param pattern The pattern.
     * @return The pattern in string form.
     */
    public String patternToString(List<LockPatternView.Cell> pattern) {
        if (pattern == null) {
            return "";
        }
        final int patternSize = pattern.size();

        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            LockPatternView.Cell cell = pattern.get(i);
            res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
        }
        return Arrays.toString(res);
    }

}
