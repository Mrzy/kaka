
package cn.zmdx.kaka.locker.layout;


public class LayoutInfo {

    private int position;

    private String coverUrl;

    private int coverResId;

    private String fontUrl;

    private int layoutId;

    public int getCoverResId() {
        return coverResId;
    }

    public void setCoverResId(int coverResId) {
        this.coverResId = coverResId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getFontUrl() {
        return fontUrl;
    }

    public void setFontUrl(String fontUrl) {
        this.fontUrl = fontUrl;
    }
}
