
package cn.zmdx.kaka.locker.meiwen.notification;

public class NotificationEntity {

    private int id;

    private int cloudId;

    private String title;

    private String content;

    private int level;

    private int type;

    private String targetUrl;

    private String targetApp;

    private int times;

    private String icon;

    private long startTime;

    private long endTime;

    private String extra;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCloudId() {
        return cloudId;
    }

    public void setCloudId(int cloudId) {
        this.cloudId = cloudId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getTargetApp() {
        return targetApp;
    }

    public void setTargetApp(String targetApp) {
        this.targetApp = targetApp;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id:" + id);
        sb.append(" title:" + title);
        sb.append(" content:" + content);
        sb.append(" type:" + type);
        sb.append(" startTime:" + startTime);
        sb.append(" endTime:" + endTime);
        sb.append(" level:" + level);
        sb.append(" extra:" + extra);
        sb.append(" icon:" + icon);
        sb.append(" targetApp:" + targetApp);
        sb.append(" targetUrl:" + targetUrl);
        sb.append(" cloudId:" + cloudId);
        return sb.toString();
    }
}
