
package cn.zmdx.kaka.locker.sound;

import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class LockSoundManager {

    private static Context mContext = HDApplication.getContext();

    public static int SOUND_ID_LOCK = 1;

    public static int SOUND_ID_UNLOCK = 2;

    public static SoundPool sPool = null;

    static {
        initSoundPool();
    }

    /**
     * 初始化SoundPool
     */
    private static void initSoundPool() {
        // TODO
        sPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        load(R.raw.lock);
        load(R.raw.unlock);
    }

    /**
     * 播放对应音频文件
     * 
     * @param soundId
     */
    public static void play(int soundId) {
        // TODO
        sPool.play(soundId, 1, 1, 0, 0, 1);
    }

    /**
     * 加载音频文件
     */
    private static void load(int resId) {
        SOUND_ID_LOCK = sPool.load(mContext, resId, 1);
    }

    /**
     * 释放音频文件
     */
    private static void unLoad(int soundId) {
        // TODO
        sPool.unload(soundId);

    }

    /**
     * 释放音频文件
     */
    public static void release() {
        // TODO
        unLoad(SOUND_ID_LOCK);
        unLoad(SOUND_ID_UNLOCK);
        sPool.release();
    }
}
