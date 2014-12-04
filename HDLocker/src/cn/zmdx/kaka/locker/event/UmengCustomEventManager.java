
package cn.zmdx.kaka.locker.event;

import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.wallpaper.CustomWallpaperManager.CustomWallpaper;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;

import com.umeng.analytics.MobclickAgent;

public class UmengCustomEventManager {

    public static final String EVENT_FIXED_TIMES = "fixedTimes";// 拉开锁屏固定住，计数一次；

    public static final String EVENT_CLICK_WHEN_FIXED = "clickWhenFixed";// 固定后，点击解锁计数一次；

    public static final String EVENT_DIRECT_UNLOCK = "directUnlock";// 未固定，直接解锁，计数一次；

    public static final String EVENT_GUESTURE_LOCK_ENABLED_DAILY = "guestureLockEnabledDaily";// 若用户开启了手势锁，每日上报一次；

    public static final String EVENT_GUESTURE_UNLOCK_SUCCESS_TIMES = "guestureUnLockSuccessTimes";// 手势锁成功解锁的次数；

    public static final String EVENT_GUESTURE_UNLOCK_FAIL_TIMES = "guestureUnLockFailTimes";// 手势锁成功解锁的次数；

    public static final String EVENT_TYPE_DEFAULT = "default"; // 没有数据时显示的默认页

    public static final String EVENT_PANDORA_SWITCH_OPEN_TIMES = "pandoraSwitchOpen"; // 打开/关闭锁屏开关，上报对应事件；

    public static final String EVENT_PANDORA_SWITCH_CLOSE_TIMES = "pandoraSwitchClose"; // 打开/关闭锁屏开关，上报对应事件；

    public static final String EVENT_SET_CUSTOM_WALLPAPER_TIMES = "setCustomWallpaperTimes"; // 用户通过在个性化设置界面中自定义壁纸功能时上报一次事件；

    public static final String EVENT_SET_CUSTOM_WALLPAPER_SUCCESS_TIMES = "setCustomWallpaperSuccessTimes"; // 成功设置自定义主题壁纸时上报一次事件；

    public static final String EVENT_CLICK_OR_DRAG_ROPE_TIMES = "clickOrDragRopeTimes";// 用户点击或者拉动绳索

    public static final String EVENT_SET_LOCKSCREEN_WALLPAPER_TIMES = "setLockScreenWallpaperTimes";// 用户在新闻屏设置壁纸

    public static final String EVENT_APPLY_LOCKSCREEN_WALLPAPER_TIMES = "applyLockScreenWallpaperTimes";// 用户点击应用壁纸按钮进行设置壁纸

    public static final String EVENT_SELECT_LOCKSCREEN_WALLPAPER_COUNT = "selectLockScreenWallpaperCount";// 在个性化设置界面选择某张壁纸的次数

    public static final String EVENT_PULL_TO_REFRESH_TIMES = "pullToRefreshTimes";// 用户下拉刷新

    public static final String EVENT_SHOW_NOTIFY_TIMES = "showNotifyTimes";// 用户设置显示通知栏次数

    public static final String EVENT_CLOSE_NOTIFY_TIMES = "closeNotifyTimes";// 用户设置关闭通知栏次数

    public static final String EVENT_ALLOW_AUTO_DOWNLOAD = "allowAutoDownload";// 用户允许3G/4G时缓存图文

    public static final String EVENT_DISALLOW_AUTO_DOWNLOAD = "disallowAutoDownload";// 用户不允许3G/4G时缓存图文

    /**
     * 统计用户点击或者拉动绳索的次数
     */
    public static void statisticalClickOrDragRopeTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_CLICK_OR_DRAG_ROPE_TIMES);
    }

    /**
     * 统计用户在新闻屏设置壁纸的次数
     */
    public static void statisticalSetLockScreenWallpaperTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_SET_LOCKSCREEN_WALLPAPER_TIMES);
    }

    /**
     * 统计用户点击应用壁纸按钮进行设置壁纸的次数
     */
    public static void statisticalApplyLockScreenWallpaperTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_APPLY_LOCKSCREEN_WALLPAPER_TIMES);
    }

    /**
     * 统计个性化设置界面某张壁纸的选择次数
     */
    public static void statisticalSelectLockScreenWallpaperCount(String fileName) {
        if (null != fileName) {
            MobclickAgent.onEvent(HDApplication.getContext(),
                    UmengCustomEventManager.EVENT_SELECT_LOCKSCREEN_WALLPAPER_COUNT, fileName);
        }
    }

    /**
     * 统计用户下拉刷新的次数
     */
    public static void statisticalPullToRefreshTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_PULL_TO_REFRESH_TIMES);
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
     * 统计未固定，直接解锁次数
     */
    public static void statisticalUnLockTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_DIRECT_UNLOCK);
    }

    /**
     * 统计固定的次数
     */
    public static void statisticalFixedTimes() {
        MobclickAgent
                .onEvent(HDApplication.getContext(), UmengCustomEventManager.EVENT_FIXED_TIMES);
    }

    /**
     * 统计固定之后解锁次数
     */
    public static void statisticalFixedUnLockTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_CLICK_WHEN_FIXED);
    }

    /**
     * 统计点击自定义壁纸按钮的次数
     * 
     * @param themeId
     */
    public static void statisticalClickCustomButtonTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_SET_CUSTOM_WALLPAPER_TIMES);
    }

    /**
     * 统计成功设置自定义壁纸的次数
     * 
     * @param themeId
     */
    public static void statisticalSuccessSetCustomTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_SET_CUSTOM_WALLPAPER_SUCCESS_TIMES);
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

}
