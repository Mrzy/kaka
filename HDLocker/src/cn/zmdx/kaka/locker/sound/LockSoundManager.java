
package cn.zmdx.kaka.locker.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;

public class LockSoundManager {

    private static Context sContext = HDApplication.getContext();

    public static int SOUND_ID_LOCK = 1;

    public static int SOUND_ID_UNLOCK = 2;

    private static SoundPool sPool = null;

    static {
        sPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        SOUND_ID_LOCK = sPool.load(sContext, R.raw.lock, 1);
        SOUND_ID_UNLOCK = sPool.load(sContext, R.raw.unlock, 1);
    }

    /**
     * 播放对应音频文件
     * 
     * @param soundId SOUND_ID_LOCK or SOUND_ID_UNLOCK
     */
    public static void play(int soundId) {
        int result = sPool.play(soundId, 1, 1, 0, 0, 1);
    }

    /**
     * 释放音频文件
     */
    private static void unLoad(int soundId) {
        sPool.unload(soundId);
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
