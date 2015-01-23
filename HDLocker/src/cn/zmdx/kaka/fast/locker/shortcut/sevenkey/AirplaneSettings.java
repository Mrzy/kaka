package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import cn.zmdx.kaka.fast.locker.LockScreenManager;
import cn.zmdx.kaka.fast.locker.utils.TelephonyUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

/*七键开关
 * 飞行模式开关
 * */
public class AirplaneSettings {
    public Context context;

    public AirplaneSettings(Context context) {
        this.context = context;
    }

    public boolean getAirplaneState() {
        return TelephonyUtils.isAirPlaneMode(context);
    }

    public boolean setAirplaneState(final boolean b) {
        final int mode = b ? 1 : 0;
     // when Android 4.2, airplane mode can not be changed any more
        if (Build.VERSION.SDK_INT >= 17) {
                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                LockScreenManager.getInstance().unLock();
        } else {
            Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, mode);
            Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
            intent.putExtra("state", b);
            context.sendBroadcast(intent);
        }
        return true;
    }
}
