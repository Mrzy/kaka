package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;


public abstract class StateSwitch extends SwitchBase {
    protected static final int STATE_DISABLED = 0;
    protected static final int STATE_ENABLED = 1;
    protected static final int STATE_TURNING_ON = 2;
    protected static final int STATE_TURNING_OFF = 3;
    protected static final int STATE_UNKNOWN = 4;
    protected static final int STATE_INTERMEDIATE = 5;

    int mState = STATE_UNKNOWN;

    public StateSwitch(int switchId) {
        super(switchId);
    }

    @Override
    public int getIndicatorState() {
        if (mState == STATE_ENABLED) {
            return INDICATOR_STATE_ENABLED;
        } else if (mState == STATE_TURNING_ON || mState == STATE_TURNING_OFF
                || mState == STATE_INTERMEDIATE) {
            return INDICATOR_STATE_INTERMEDIATE;
        } else {
            return INDICATOR_STATE_DISABLED;
        }
    }

}
