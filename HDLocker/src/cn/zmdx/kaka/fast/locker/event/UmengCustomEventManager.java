
package cn.zmdx.kaka.fast.locker.event;

import cn.zmdx.kaka.fast.locker.HDApplication;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;

import com.umeng.analytics.MobclickAgent;

public class UmengCustomEventManager {

    public static final String EVENT_PULL_DOWN = "pullDown";// 锁屏页下拉并成功展开；

    public static final String EVENT_GUESTURE_LOCK_ENABLED_DAILY = "guestureLockEnabledDaily";// 若用户开启了手势锁，每日上报一次；

    public static final String EVENT_GUESTURE_UNLOCK_SUCCESS_TIMES = "guestureUnLockSuccessTimes";// 手势锁成功解锁的次数；

    public static final String EVENT_GUESTURE_UNLOCK_FAIL_TIMES = "guestureUnLockFailTimes";// 手势锁成功解锁的次数；

    public static final String EVENT_TYPE_DEFAULT = "default"; // 没有数据时显示的默认页

    public static final String EVENT_PANDORA_SWITCH_OPEN_TIMES = "pandoraSwitchOpen"; // 打开/关闭锁屏开关，上报对应事件；

    public static final String EVENT_PANDORA_SWITCH_CLOSE_TIMES = "pandoraSwitchClose"; // 打开/关闭锁屏开关，上报对应事件；

    public static final String EVENT_SET_CUSTOM_WALLPAPER_TIMES = "setCustomWallpaperTimes"; // 用户通过在个性化设置界面中自定义壁纸功能时上报一次事件；

    public static final String EVENT_SET_CUSTOM_WALLPAPER_SUCCESS_TIMES = "setCustomWallpaperSuccessTimes"; // 成功设置自定义主题壁纸时上报一次事件；

    public static final String EVENT_CLICK_OR_DRAG_ROPE_TIMES = "clickOrDragRopeTimes";// 用户点击或者拉动绳索

    public static final String EVENT_APPLY_LOCKSCREEN_WALLPAPER_TIMES = "applyLockScreenWallpaperTimes";// 用户点击应用壁纸按钮进行设置壁纸

    public static final String EVENT_SELECT_LOCKSCREEN_WALLPAPER_COUNT = "selectLockScreenWallpaperCount";// 在个性化设置界面选择某张壁纸的次数

    public static final String EVENT_SHOW_NOTIFY_TIMES = "showNotifyTimes";// 用户设置显示通知栏次数

    public static final String EVENT_CLOSE_NOTIFY_TIMES = "closeNotifyTimes";// 用户设置关闭通知栏次数

    public static final String EVENT_ENABLE_LOCKSCREEN_SOUND = "enableLockScreenSound";// 音效开关打开

    public static final String EVENT_DISABLE_LOCKSCREEN_SOUND = "disableLockScreenSound";// 音效开关关闭

    public static final String EVENT_ENTER_CAMERA_FROM_LOCKER = "enterCamera";// 进入相机

    public static final String EVENT_POST_NOTIFICATION = "postNotification";// 锁屏页弹出一个通知

    public static final String EVENT_REMOVE_NOTIFICATION = "removeNotification";// 双击移除一个通知

    public static final String EVENT_OPEN_NOTIFICATION = "openNotification";// 右划打开一个通知

    public static final String EVENT_ALLOW_NOTIFICATION = "allowNotification";// 开启通知提醒

    public static final String EVENT_DISALLOW_NOTIFICATION = "disallowNotification";// 关闭通知提醒

    public static final String EVENT_HIDE_NOTIFICATION = "hideNotification";// 隐藏通知内容

    public static final String EVENT_SHOW_NOTIFICATION = "showNotification";// 显示通知内容

    public static final String EVENT_COMMENT = "comment";// 点击评价按钮

    public static final String EVENT_CLICK_SHORTCUT = "clickShortcut";// 点击锁屏页快捷应用

    public static final String EVENT_CLICK_TOOL_ICON = "clickToolIcon";// 点击工具条快捷图标

    public static final String EVENT_NOTIFY_FILTER_APP = "notifyFilterApps";// 通知筛选的应用名称

    public static final String EVENT_ALL_SHORTCUT = "allShortcut";// 所有的快捷应用名称

    /**
     * 统计开启通知提醒
     */
    public static void statisticalEnableAllowNotification() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_ALLOW_NOTIFICATION);
    }

    /**
     * 统计关闭通知提醒
     */
    public static void statisticalDisallowNotification() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_DISALLOW_NOTIFICATION);
    }

    /**
     * 隐藏通知内容
     */
    public static void statisticalHideNotification() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_HIDE_NOTIFICATION);
    }

    /**
     * 显示通知内容
     */
    public static void statisticalShowNotification() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_SHOW_NOTIFICATION);
    }

    /**
     * 点击评价按钮
     */
    public static void statisticalCommentTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(), UmengCustomEventManager.EVENT_COMMENT);
    }

    /**
     * 点击锁屏页快捷应用
     */
    public static void statisticalClickShortcut(String pkgName) {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_CLICK_SHORTCUT, pkgName);
    }

    /**
     * 点击工具条快捷图标
     */
    public static void statisticalClickToolIcon() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_CLICK_TOOL_ICON);
    }

    /**
     * 通知筛选的应用名称
     */
    public static void statisticalNotifyFilterApps(String notifyFilterApps) {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_NOTIFY_FILTER_APP, notifyFilterApps);
    }

    /**
     * 所有的快捷应用名称
     */
    public static void statisticalAllShortcut(String allShortcutApps) {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_ALL_SHORTCUT, allShortcutApps);
    }

    public static void statisticalEnterCamera() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_ENTER_CAMERA_FROM_LOCKER);
    }

    public static void statisticalPostNotification(int id, String pkgName, int type) {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_POST_NOTIFICATION, id + "|" + pkgName + "|" + type);
    }

    public static void statisticalRemoveNotification(int id, String pkgName, int type) {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_REMOVE_NOTIFICATION, id + "|" + pkgName + "|" + type);
    }

    public static void statisticalOpenNotification(int id, String pkgName, int type) {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_OPEN_NOTIFICATION, id + "|" + pkgName + "|" + type);
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
     * 统计用户点击或者拉动绳索的次数
     */
    public static void statisticalClickOrDragRopeTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(),
                UmengCustomEventManager.EVENT_CLICK_OR_DRAG_ROPE_TIMES);
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
    public static void statisticalPullDownTimes() {
        MobclickAgent.onEvent(HDApplication.getContext(), UmengCustomEventManager.EVENT_PULL_DOWN);
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
