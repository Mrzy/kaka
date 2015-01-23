package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;


@SuppressWarnings("static-access")
public abstract class SwitchBase {

    protected static final int INDICATOR_STATE_ENABLED = 1;
    protected static final int INDICATOR_STATE_DISABLED = 2;
    protected static final int INDICATOR_STATE_INTERMEDIATE = 3;

    protected int mSwitchId = 0;

    public int getSwitchId() {
        return mSwitchId;
    }

    public SwitchBase(int switchId) {
        mSwitchId = switchId;
    }

    public abstract void refreshActualState(Context cxt);

    public abstract void onActualStateChange(Context cxt, Intent intent);

    public abstract void toggleState(Context cxt, WidgetConfig config, Rect sourceBounds);

    public abstract int getIconResId(Context cxt, int themeType);

    public abstract int getIndicatorState();


}
