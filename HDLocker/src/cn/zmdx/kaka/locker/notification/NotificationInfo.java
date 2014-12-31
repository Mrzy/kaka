
package cn.zmdx.kaka.locker.notification;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;

public class NotificationInfo {

    public static final int NOTIFICATION_TYPE_SYSTEM = 1;

    public static final int NOTIFICATION_TYPE_CUSTOM = 2;

    private String pkg;

    private int id;

    private String tag;

    private long postTime;

    private String title;

    private String content;

    private Drawable smallIcon;

    private Bitmap largeIcon;

    private int type;

    private PendingIntent pendingIntent;

    private String key;

    public static NotificationInfo createCustomNotificationInfo(String title, String content,
            Bitmap largeIcon, Drawable smallIcon, String tag) {
        if (TextUtils.isEmpty(title)) {
            if (BuildConfig.DEBUG) {
                throw new NullPointerException("title must not be null");
            }
        }

        final NotificationInfo ni = new NotificationInfo();
        ni.setPostTime(System.currentTimeMillis());
        ni.setId(Long.valueOf(System.currentTimeMillis()).hashCode());
        ni.setTag(tag);
        ni.setTitle(title);
        ni.setContent(content);
        ni.setLargeIcon(largeIcon);
        ni.setSmallIcon(smallIcon);
        ni.setType(NOTIFICATION_TYPE_CUSTOM);
        return ni;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    public Drawable getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon(Drawable smallIcon) {
        this.smallIcon = smallIcon;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
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

    public Bitmap getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(Bitmap icon) {
        this.largeIcon = icon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
