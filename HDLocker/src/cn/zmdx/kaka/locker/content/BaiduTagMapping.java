
package cn.zmdx.kaka.locker.content;

public class BaiduTagMapping {

    public static final String S_TAG1_MINGXING = "明星";

    public static final String S_TAG1_MEINV = "美女";

    public static final String S_TAG1_BIZHI = "壁纸";

    public static final String S_TAG1_GAOXIAO = "搞笑";

    public static final String S_TAG1_SHEYING = "摄影";

    public static final String S_TAG2_ALL = "全部";

    public static final int INT_ERROR = -1;

    public static final int INT_TAG2_ALL = 0;

    public static final int INT_TAG1_MINGXING = 1;

    public static final int INT_TAG1_MEINV = 2;

    public static final int INT_TAG1_BIZHI = 3;

    public static final int INT_TAG1_GAOXIAO = 4;

    public static final int INT_TAG1_SHEYING = 5;

    public static String getStringTag1(int tag1) {
        switch (tag1) {
            case INT_TAG1_MINGXING:
                return S_TAG1_MINGXING;
            case INT_TAG1_MEINV:
                return S_TAG1_MEINV;
            case INT_TAG1_BIZHI:
                return S_TAG1_BIZHI;
            case INT_TAG1_GAOXIAO:
                return S_TAG1_GAOXIAO;
            case INT_TAG1_SHEYING:
                return S_TAG1_SHEYING;
            default:
                return null;
        }
    }

    public static String getStringTag2(int tag2) {
        switch (tag2) {
            case INT_TAG2_ALL:
                return S_TAG2_ALL;
            default:
                return null;
        }
    }

    public static int getIntTag1(String tag1) {
        if (tag1.equals(S_TAG1_MINGXING)) {
            return INT_TAG1_MINGXING;
        } else if (tag1.equals(S_TAG1_MEINV)) {
            return INT_TAG1_MEINV;
        } else if (tag1.equals(S_TAG1_BIZHI)) {
            return INT_TAG1_BIZHI;
        } else if (tag1.equals(S_TAG1_GAOXIAO)) {
            return INT_TAG1_GAOXIAO;
        } else if (tag1.equals(S_TAG1_SHEYING)) {
            return INT_TAG1_SHEYING;
        } else {
            return INT_ERROR;
        }
    }

    public static int getIntTag2(String tag2) {
        if (tag2.equals(S_TAG2_ALL)) {
            return INT_TAG2_ALL;
        } else {
            return INT_ERROR;
        }
    }
}
