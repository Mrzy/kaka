
package cn.zmdx.kaka.locker.sound;

import android.media.AudioManager;
import android.media.SoundPool;

public class LockSoundManager {

    public static final int SOUND_ID_LOCK = 1;

    public static final int SOUND_ID_UNLOCK = 2;

    public static SoundPool sPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

    static {
        initSoundPool();
    }

    /**
     * 初始化SoundPool
     */
    private static void initSoundPool() {
        // TODO
    }

    /**
     * 播放对应音频文件
     * 
     * @param soundId
     */
    public static void play(int soundId) {
        // TODO
    }

    /**
     * 加载音频文件
     */
    public static void load() {
        // TODO
    }

    /**
     * 释放音频文件
     */
    public static void unLoad() {
        // TODO
    }

    /**
     * 释放音频文件
     */
    public static void release() {
        // TODO
    }
}
