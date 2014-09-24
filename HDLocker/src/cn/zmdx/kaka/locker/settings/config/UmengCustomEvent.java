
package cn.zmdx.kaka.locker.settings.config;

public class UmengCustomEvent {
    
    public static final String EVENT_FIXED_TIMES = "fixedTimes";//拉开锁屏固定住，计数一次；
    
    public static final String EVENT_FIXED_UNLOCK_TIMES = "fixedUnLockTimes";//固定后，点击解锁计数一次；
    
    public static final String EVENT_UNLOCK_TIMES = "unLockTimes";//未固定，直接解锁，计数一次；
    
    public static final String EVENT_GUESTURE_LOCK = "guestureLock";//若用户开启了手势锁，每日上报一次；
    
    public static final String EVENT_GUESTURE_UNLOCK_SUCCESS_TIMES = "guestureUnLockSuccessTimes";//手势锁成功解锁的次数；
    
    public static final String EVENT_GUESTURE_UNLOCK_FAIL_TIMES = "guestureUnLockFailTimes";//手势锁成功解锁的次数；
    
    public static final String EVENT_USE_THEME_TIMES = "userThemeTimes";//每日上报一次使用的主题事件
    public static final String EVENT_USE_BLUE_THEME_TIMES = "userBlueThemeTimes"; //每日上报一次使用的主题事件；蓝色
    public static final String EVENT_USE_PINK_THEME_TIMES = "userPinkThemeTimes"; //每日上报一次使用的主题事件；粉色
    public static final String EVENT_USE_JEAN_THEME_TIMES = "userJeanThemeTimes"; //每日上报一次使用的主题事件；牛仔
    public static final String EVENT_USE_WOOD_GRAIN_THEME_TIMES = "userWoodGrainThemeTimes"; //每日上报一次使用的主题事件；木纹
    
    public static final String EVENT_ENTER_LOCK_TIME = "enterLock"; //进入锁屏页，上报一次事件，作为统计活跃，每日一次；
    
    public static final String EVENT_LOCK_TIME = "lockTime"; //计算拉开锁屏到解锁的时间，及当前内容的id,dataType，website信息； 只计算开锁到固定解锁的时间
    
    public static final String EVENT_PANDORA_SWITCH_OPEN_TIMES = "pandoraSwitchOpen"; //打开/关闭锁屏开关，上报对应事件；
    public static final String EVENT_PANDORA_SWITCH_CLOSE_TIMES = "pandoraSwitchClose"; //打开/关闭锁屏开关，上报对应事件；
    
    public static final String EVENT_WALLPAPER_BLUE_TIMES = "blue"; //点击某个主题壁纸时上报一次事件，包括当前选中的壁纸信息；
    public static final String EVENT_WALLPAPER_PINK_TIMES = "pink"; //点击某个主题壁纸时上报一次事件，包括当前选中的壁纸信息；
    public static final String EVENT_WALLPAPER_JEAN_TIMES = "jean"; //点击某个主题壁纸时上报一次事件，包括当前选中的壁纸信息；
    public static final String EVENT_WALLPAPER_WOOD_GRAIN_TIMES = "woodGrain"; //点击某个主题壁纸时上报一次事件，包括当前选中的壁纸信息；
    
    public static final String EVENT_GUIDE_TIME = "guideTime"; //计算引导页的总展示时间；
    
}
