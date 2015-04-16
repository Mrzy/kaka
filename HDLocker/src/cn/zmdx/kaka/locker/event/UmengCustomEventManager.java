
package cn.zmdx.kaka.locker.event;

import java.util.HashMap;
import java.util.Set;

import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.notification.NotificationPreferences;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

import com.umeng.analytics.MobclickAgent;

public class UmengCustomEventManager {

    public static final String EVENT_GUESTURE_LOCK_ENABLED_DAILY = "guestureLockEnabledDaily";// 若用户开启了手势锁，每日上报一次；

    public static final String EVENT_GUESTURE_UNLOCK_SUCCESS_TIMES = "guestureUnLockSuccessTimes";// 手势锁成功解锁的次数；

    public static final String EVENT_GUESTURE_UNLOCK_FAIL_TIMES = "guestureUnLockFailTimes";// 手势锁失败解锁的次数；

    public static final String EVENT_PANDORA_SWITCH_OPEN_TIMES = "pandoraSwitchOpen"; // 打开/关闭锁屏开关，上报对应事件；

    public static final String EVENT_PANDORA_SWITCH_CLOSE_TIMES = "pandoraSwitchClose"; // 打开/关闭锁屏开关，上报对应事件；

    public static final String EVENT_LOCKSCREEN_WALLPAPER_DETAIL_TIMES = "lockScreenWallpaperDetailTimes";// 用户在新闻屏进入壁纸详情页的次数

    public static final String EVENT_LOCKSCREEN_WALLPAPER_DETAIL_APPLY_TIMES = "applyLockScreenWallpaperDetailTimes";// 用户点击应用壁纸按钮进行设置壁纸

    public static final String EVENT_SHOW_NOTIFY_TIMES = "showNotifyTimes";// 用户设置显示通知栏次数

    public static final String EVENT_CLOSE_NOTIFY_TIMES = "closeNotifyTimes";// 用户设置关闭通知栏次数

    public static final String EVENT_ALLOW_AUTO_DOWNLOAD = "allowAutoDownload";// 用户允许3G/4G时缓存图文

    public static final String EVENT_DISALLOW_AUTO_DOWNLOAD = "disallowAutoDownload";// 用户不允许3G/4G时缓存图文

    public static final String EVENT_ENABLE_LOCKSCREEN_SOUND = "enableLockScreenSound";// 音效开关打开

    public static final String EVENT_DISABLE_LOCKSCREEN_SOUND = "disableLockScreenSound";// 音效开关关闭

    public static final String EVENT_SEE_CONTENT_DETAILS = "seeContentDetails";// 查看新闻条目的详情

    public static final String EVENT_POST_NOTIFICATION = "postNotification";// 锁屏页弹出一个通知

    public static final String EVENT_REMOVE_NOTIFICATION = "removeNotification";// 左划移除一个通知

    public static final String EVENT_OPEN_NOTIFICATION = "openNotification";// 右划打开一个通知

    public static final String EVENT_PULL_REFRESH_NEWS = "pullRefreshNews";// 下拉刷新

    private static final String EVENT_OPEN_NOTIFICATION_REMIND = "openNotificationRemind";// 开启通知提醒

    private static final String EVENT_CLOSE_NOTIFICATION_REMIND = "closeNotificationRemind";// 关闭通知提醒

    private static final String EVENT_SHOW_NOTIFY_CONTENT = "showNotifyContent";// 显示通知内容

    private static final String EVENT_HIDE_NOTIFY_CONTENT = "hideNotifyContent";// 隐藏通知内容

    private static final String EVENT_NEED_INTERCEPT_APP = "needInterceptApp";// 被用户拦截的应用

    private static final String EVENT_OPEN_GRAVITY_SENOR = "openGravitySenor";// 打开重力感应

    private static final String EVENT_CLOSE_GRAVITY_SENOR = "closeGravitySenor";// 关闭重力感应

    private static final String EVENT_SYCCESS_SET_LOCAL_WALLPAPER = "successSetLocalWallpaperTimes";// 成功设置本地壁纸

    private static final String EVENT_OPEN_PANDORA_PROTECT = "openPandoraProtect";// 开启潘多拉守护神

    private static final String EVENT_CLOSE_PANDORA_PROTECT = "closePandoraProtect";// 关闭潘多拉守护神

    private static final String EVENT_OPEN_DELAY_LOCKSCREEN = "openDelayLockScreen";// 开启延迟锁定屏幕

    private static final String EVENT_CLOSE_DELAY_LOCKSCREEN = "closeDelayLockScreen";// 关闭延迟锁定屏幕
    
    private static final String EVENT_OPEN_AUTO_CHANGE_WALLPAPER = "openAutoChangeWallpaper";// 开启每天自动更换壁纸

    private static final String EVENT_CLOSE_AUTO_CHANGE_WALLPAPER = "closeAutoChangeWallpaper";// 关闭每天自动更换壁纸

    public static void statisticalOpenNewsDetail(int id, String newsType) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("newsId", String.valueOf(id));
        map.put("newsType", String.valueOf(newsType));
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_SEE_CONTENT_DETAILS, map);
    }

    public static void statisticalPullRefreshNews(String newsType) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("newsType", newsType);
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_PULL_REFRESH_NEWS, map);
    }

    public static void statisticalPostNotification(int id, String pkgName, int type) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pkgName", pkgName);
        map.put("type", String.valueOf(type));
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_POST_NOTIFICATION, map);
    }

    public static void statisticalRemoveNotification(int id, String pkgName, int type) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pkgName", pkgName);
        map.put("type", String.valueOf(type));
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_REMOVE_NOTIFICATION, map);
    }

    public static void statisticalOpenNotification(int id, String pkgName, int type) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pkgName", pkgName);
        map.put("type", String.valueOf(type));
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_OPEN_NOTIFICATION, map);
    }

    /**
     * 统计哪张卡片被收藏
     * 
     * @param mCloudId
     */
    // public static void statisticalCardIsFavorited(String mCloudId) {
    // if (null != mCloudId) {
    // MobclickAgent.onEvent(HDApplication.getContext(),
    // UmengCustomEventManager.EVENT_CARD_IS_FAVORITED, mCloudId);
    // }
    // }

    /**
     * 统计查看新闻条目的详情
     * 
     * @param mCloudId
     */
    public static void statisticalSeeContentDetails(String mCloudId) {
        if (null != mCloudId) {
            MobclickAgent.onEvent(HDApplication.getContext(),
                    UmengCustomEventManager.EVENT_SEE_CONTENT_DETAILS, mCloudId);
        }
    }

    /**
     * 统计用户音效开关打开
     */
    public static void statisticalEnableLockScreenSound() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_ENABLE_LOCKSCREEN_SOUND);
    }

    /**
     * 统计用户音效开关关闭
     */
    public static void statisticalDisableLockScreenSound() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_DISABLE_LOCKSCREEN_SOUND);
    }

    /**
     * 统计用户在新闻屏进入壁纸详情的次数
     */
    public static void statisticalLockScreenWallpaperDetailTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_LOCKSCREEN_WALLPAPER_DETAIL_TIMES);
    }

    /**
     * 统计用户在新闻屏进入壁纸详情并应用壁纸的次数
     */
    public static void statisticalLockScreenWallpaperDetailApplyTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_LOCKSCREEN_WALLPAPER_DETAIL_APPLY_TIMES);
    }

    /**
     * 统计用户显示通知栏
     */
    public static void statisticalShowNotifyTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_SHOW_NOTIFY_TIMES);
    }

    /**
     * 统计用户关闭通知栏
     */
    public static void statisticalCloseNotifyTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_CLOSE_NOTIFY_TIMES);
    }

    /**
     * 统计用户允许3G/4G时缓存图文
     */
    public static void statisticalAllowAutoDownload() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_ALLOW_AUTO_DOWNLOAD);
    }

    /**
     * 统计用户不允许3G/4G时缓存图文
     */
    public static void statisticalDisallowAutoDownload() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_DISALLOW_AUTO_DOWNLOAD);
    }

    /**
     * 统计 若用户开启了手势锁，每日上报一次
     * 
     * @param pandoraConfig
     * @param currentDate
     */
    public static void statisticalGuestureLockTime(PandoraConfig pandoraConfig, String currentDate) {
        String saveDate = pandoraConfig.getEventGuestureLockEnabledDailyString();
        if (!currentDate.equals(saveDate)) {
            if (pandoraConfig.isPandolaLockerOn()) {
                MobclickAgent.onEvent(HDApplication.getContext(),
                        UmengCustomEventManager.EVENT_GUESTURE_LOCK_ENABLED_DAILY);
                pandoraConfig.saveEventGuestureLockEnabledDaily(currentDate);
            }
        }
    }

    /**
     * 统计手势锁成功解锁的次数
     */
    public static void statisticalGuestureUnLockSuccess() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_GUESTURE_UNLOCK_SUCCESS_TIMES);
    }

    /**
     * 统计手势锁失败解锁的次数
     */
    public static void statisticalGuestureUnLockFail() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_GUESTURE_UNLOCK_FAIL_TIMES);
    }

    /**
     * 统计打开锁屏开关次数
     */
    public static void statisticalPandoraSwitchOpenTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_PANDORA_SWITCH_OPEN_TIMES);
    }

    /**
     * 统计关闭锁屏开关次数
     */
    public static void statisticalPandoraSwitchCloseTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_PANDORA_SWITCH_CLOSE_TIMES);
    }

    /**
     * 统计用户开启通知提醒
     */
    public static void statisticalOpenNotificationRemindTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_OPEN_NOTIFICATION_REMIND);
    }

    /**
     * 统计用户关闭通知提醒
     */
    public static void statisticalCloseNotificationRemindTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_CLOSE_NOTIFICATION_REMIND);
    }

    /**
     * 统计用户显示通知内容
     */
    public static void statisticalshowNotifyContentTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_SHOW_NOTIFY_CONTENT);
    }

    /**
     * 统计用户隐藏通知内容
     */
    public static void statisticalHideNotifyContentTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_HIDE_NOTIFY_CONTENT);
    }

    /**
     * 统计用户需要拦截消息的App 每日一次
     */
    public static void statisticalNeedInterceptApp() {
        String saveDate = PandoraConfig.newInstance(HDApplication.getContext())
                .getEventNeedInterceptAppDailyString();
        String currentDate = BaseInfoHelper.getCurrentDate();
        if (!currentDate.equals(saveDate)) {
            Set<String> pkgNameSet = NotificationPreferences
                    .getInstance(HDApplication.getContext()).getInterceptPkgNames();
            HashMap<String, String> map = new HashMap<String, String>();
            for (String pkgName : pkgNameSet) {
                map.put("pkgName", pkgName);
            }
            MobclickAgent.onEvent(HDApplication.getContext(),
                    UmengCustomEventManager.EVENT_NEED_INTERCEPT_APP, map);
            PandoraConfig.newInstance(HDApplication.getContext())
                    .saveEventNeedInterceptAppDailyData(currentDate);
        }

    }

    /**
     * 统计用户开启重力感应壁纸
     */
    public static void statisticalOpenGravitySenorTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_OPEN_GRAVITY_SENOR);
    }

    /**
     * 统计用户关闭重力感应壁纸
     */
    public static void statisticalCloseGravitySenorTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_CLOSE_GRAVITY_SENOR);
    }

    /**
     * 统计用户成功设置本地壁纸的次数
     */
    public static void statisticalSuccessSetLocalWallpaperTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_SYCCESS_SET_LOCAL_WALLPAPER);
    }
    
    /**
     * 统计用户开启潘多拉守护神的次数
     */
    public static void statisticalOpenPandoraProtect() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_OPEN_PANDORA_PROTECT);
    }
    /**
     * 统计用户关闭潘多拉守护神的次数
     */
    public static void statisticalClosePandoraProtect() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_CLOSE_PANDORA_PROTECT);
    }
    
    /**
     * 统计用户开启延迟锁定屏幕的次数
     */
    public static void statisticalOpenDelayLockScreen() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_OPEN_DELAY_LOCKSCREEN);
    }
    /**
     * 统计用户关闭延迟锁定屏幕的次数
     */
    public static void statisticalCloseDelayLockScreen() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_CLOSE_DELAY_LOCKSCREEN);
    }
    
    /**
     * 统计用户开启每天自动更换壁纸的次数
     */
    public static void statisticalOpenAutoChangeWallpaper() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_OPEN_AUTO_CHANGE_WALLPAPER);
    }
    /**
     * 统计用户关闭每天自动更换壁纸的次数
     */
    public static void statisticalCloseAutoChangeWallpaper() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_CLOSE_AUTO_CHANGE_WALLPAPER);
    }
}
