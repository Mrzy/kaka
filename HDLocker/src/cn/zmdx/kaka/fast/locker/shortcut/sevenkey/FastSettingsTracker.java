package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import cn.zmdx.kaka.fast.locker.LockScreenManager;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.settings.MainSettingsActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;

public class FastSettingsTracker extends SwitchBase {

    public FastSettingsTracker(int switchId) {
        super(switchId);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void refreshActualState(Context cxt) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActualStateChange(Context cxt, Intent intent) {
        // TODO Auto-generated method stub
    }

    @Override
    public void toggleState(Context cxt, WidgetConfig config, Rect sourceBounds) {
        Intent intent = new Intent(cxt, MainSettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cxt.startActivity(intent);
        LockScreenManager.getInstance().unLock();
    }

    @Override
    public int getIconResId(Context cxt, int themeType) {
        return R.drawable.icon_toolbar_setting;
    }

    @Override
    public int getIndicatorState() {
        // TODO Auto-generated method stub
        return 0;
    }

}
