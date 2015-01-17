
package cn.zmdx.kaka.fast.locker.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import cn.zmdx.kaka.fast.locker.HDApplication;
import cn.zmdx.kaka.fast.locker.R;

public class LockSoundManager {

    private static Context mContext = HDApplication.getContext();

    public static int SOUND_ID_LOCK = 1;

    public static int SOUND_ID_UNLOCK = 2;

    private static SoundPool sPool = null;

    /**
     * 初始化SoundPool
     */
    public static void initSoundPool() {
        if (sPool == null) {
            sPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            load(R.raw.lock);
            load(R.raw.unlock);
        }
    }

    /**
     * 播放对应音频文件
     * 
     * @param soundId SOUND_ID_LOCK or SOUND_ID_UNLOCK
     */
    public static void play(int soundId) {
        if (sPool != null) {
            sPool.play(soundId, 1, 1, 0, 0, 1);
        } else {
            initSoundPool();
        }
    }

    /**
     * 加载音频文件
     */
    private static void load(int resId) {
        if (sPool != null) {
            SOUND_ID_LOCK = sPool.load(mContext, resId, 1);
        }
    }

    /**
     * 释放音频文件
     */
    private static void unLoad(int soundId) {
        if (sPool != null) {
            sPool.unload(soundId);
        }
    }

    /**
     * 释放音频文件
     */
    public static void release() {
        unLoad(SOUND_ID_LOCK);
        unLoad(SOUND_ID_UNLOCK);
        if (sPool != null) {
            sPool.release();
            sPool = null;
        }
    }
}
