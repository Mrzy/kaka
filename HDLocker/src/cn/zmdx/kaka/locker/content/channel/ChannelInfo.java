
package cn.zmdx.kaka.locker.content.channel;

public class ChannelInfo {

    private int channelId;

    private String channelName;

    private String channelImgUrl;

    private int channelImgResId;

    private boolean isSelected;

    private String channelEnName;

    public String getChannelEnName() {
        return channelEnName;
    }

    public void setChannelEnName(String channelEnName) {
        this.channelEnName = channelEnName;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelImgUrl() {
        return channelImgUrl;
    }

    public void setChannelImgUrl(String channelImgUrl) {
        this.channelImgUrl = channelImgUrl;
    }

    public int getChannelImgResId() {
        return channelImgResId;
    }

    public void setChannelImgResId(int channelImgResId) {
        this.channelImgResId = channelImgResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChannelInfo) {
            ChannelInfo ci = (ChannelInfo) obj;
            return ci.getChannelId() == getChannelId();
        } else {
            return super.equals(obj);
        }
    }
}
