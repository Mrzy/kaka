package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioManager;
import cn.zmdx.kaka.fast.locker.R;

public class SoundStateTracker extends SwitchBase {
    private static final int[] IMG_NORMAL = {
        R.drawable.ic_dxhome_sound_ring_on,        // DX Home theme
    };

    private static final int[] IMG_VIBRATE = {
        R.drawable.ic_dxhome_sound_vibrate_on,     // DX Home theme
    };

    private static final int[] IMG_SILENT = {
        R.drawable.ic_dxhome_sound_silent,         // DX Home theme
    };

    private int mRingerMode = AudioManager.RINGER_MODE_NORMAL;

    public SoundStateTracker() {
        super(WidgetConfig.SWITCH_ID_SOUND);
    }

    @Override
    public int getIconResId(Context cxt, int themeType) {
        switch (mRingerMode) {
            case AudioManager.RINGER_MODE_SILENT:
                return IMG_SILENT[0];
            case AudioManager.RINGER_MODE_VIBRATE:
                return IMG_VIBRATE[0];
            case AudioManager.RINGER_MODE_NORMAL:
                return IMG_NORMAL[0];
            default:
                return 0;
        }
    }

    @Override
    public int getIndicatorState() {
        if (mRingerMode == AudioManager.RINGER_MODE_NORMAL) {
            return INDICATOR_STATE_ENABLED;
        } else {
            return INDICATOR_STATE_DISABLED;
        }
    }

    @Override
    public void refreshActualState(Context cxt) {
        AudioManager audioMgr = (AudioManager) cxt.getSystemService(Context.AUDIO_SERVICE);
        if (audioMgr != null) {
            try{
                mRingerMode = audioMgr.getRingerMode();
            }catch(Exception e){
            }
        }
    }

    @Override
    public void onActualStateChange(Context cxt, Intent intent) {
        // TODO tyc
        mRingerMode = ((AudioManager)cxt.getSystemService(Context.AUDIO_SERVICE)).getMode();
    }

    @Override
    public void toggleState(Context cxt, WidgetConfig config, Rect sourceBounds) {
        AudioManager audioMgr = (AudioManager) cxt.getSystemService(Context.AUDIO_SERVICE);
//        int msgid = Res.string.no_access_of_ringer;
        int silentMode = audioMgr.getRingerMode();
        boolean tryNormal = false;
        if (silentMode == AudioManager.RINGER_MODE_SILENT) {
            audioMgr.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            // if still silent mode, try make it normal, should happened in PAD like Nexus 7,
            // which has no vibrate mode.
            if (silentMode == audioMgr.getRingerMode()) {
                tryNormal = true;
            } else {
//                msgid = Res.string.switchwidget_sound_vibrate;
            }
        }
        if (silentMode == AudioManager.RINGER_MODE_VIBRATE || tryNormal) {
            audioMgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            if (silentMode != audioMgr.getRingerMode()) {
//                msgid = Res.string.switchwidget_sound_normal;
            }
        }
        if (silentMode == AudioManager.RINGER_MODE_NORMAL) {
            audioMgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            if (silentMode != audioMgr.getRingerMode()) {
//                msgid = Res.string.switchwidget_sound_silent;
            }
        }
//        OptimizerApp.toast(msgid, Toast.LENGTH_SHORT);
    }

}
