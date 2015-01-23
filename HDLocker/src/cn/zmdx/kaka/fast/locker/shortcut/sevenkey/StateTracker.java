package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;


/**
 * The state machine for a setting's toggling, tracking reality versus the
 * user's intent. This is necessary because reality moves relatively slowly
 * (turning on &amp; off radio drivers), compared to user's expectations.
 */
public abstract class StateTracker extends StateSwitch {
    public static final String TAG = "SevenKeyWidgetTracker";
    public static final boolean DEBUG = false;

    // Is the state in the process of changing?
    private boolean mInTransition = false;
    protected Boolean mActualState = null; // initially not set
    private Boolean mIntendedState = null; // initially not set

    // Did a toggle request arrive while a state update was
    // already in-flight? If so, the mIntendedState needs to be
    // requested when the other one is done, unless we happened to
    // arrive at that state already.
    private boolean mDeferredStateChangeRequestNeeded = false;

    public StateTracker(int switchId) {
        super(switchId);
    }

    protected int getState(boolean enabled) {
        return enabled ? STATE_ENABLED : STATE_DISABLED;
    }

    public void reset() {
        mInTransition = false;
        mIntendedState = null;
        mDeferredStateChangeRequestNeeded = false;
    }

    /**
     * Actually make the desired change to the underlying radio API.
     */
    public abstract void requestStateChange(Context cxt, boolean enabled);

    /**
     * Update internal state from a broadcast state change.
     */
    public void onActualStateChange(Context cxt, Intent intent) {
        refreshActualState(cxt);
        setCurrentState(cxt, mState);
    }

    /**
     * Sets the value that we're now in. To be called from onActualStateChange.
     *
     * @param newState one of STATE_DISABLED, STATE_ENABLED, STATE_TURNING_ON,
     *            STATE_TURNING_OFF, STATE_UNKNOWN
     */
    protected final void setCurrentState(Context context, int newState) {
        if (DEBUG) {
//            LogHelper.d(TAG, getDumpString("setCurrentState"));
        }

        final boolean wasInTransition = mInTransition;

        switch (newState) {
            case STATE_DISABLED:
                mInTransition = false;
                mActualState = false;
                break;
            case STATE_ENABLED:
                mInTransition = false;
                mActualState = true;
                break;
            case STATE_TURNING_ON:
                mInTransition = true;
                mActualState = false;
                break;
            case STATE_TURNING_OFF:
                mInTransition = true;
                mActualState = true;
                break;
        }

        if (wasInTransition && !mInTransition) {
            if (mDeferredStateChangeRequestNeeded) {
//                LogHelper.i(TAG, "processing deferred state change");
                if (mActualState != null && mIntendedState != null
                        && mIntendedState.equals(mActualState)) {
//                    LogHelper.i(TAG, "... but intended state matches, so no changes.");
                } else if (mIntendedState != null) {
                    mInTransition = true;
                    requestStateChange(context, mIntendedState);
                }
                mDeferredStateChangeRequestNeeded = false;
            }
        }
    }

    /**
     * User pressed a button to change the state. Something should immediately
     * appear to the user afterwards, even if we effectively do nothing. Their
     * press must be heard.
     */
    @Override
    public void toggleState(Context context, WidgetConfig config, Rect sourceBounds) {
        if (DEBUG) {
//            LogHelper.d(TAG, getDumpString("toggleState"));
        }
        int currentState = getTriState(context);

        boolean newState = false;
        switch (currentState) {
            case STATE_ENABLED:
                newState = false;
                break;
            case STATE_DISABLED:
                newState = true;
                break;
            case STATE_INTERMEDIATE:
                if (mIntendedState != null) {
                    newState = !mIntendedState;
                }
                break;
        }

        mIntendedState = newState;
        if (mInTransition) {
            // We don't send off a transition request if we're
            // already transitioning. Makes our state tracking
            // easier, and is probably nicer on lower levels.
            // (even though they should be able to take it...)
            mDeferredStateChangeRequestNeeded = true;
        } else {
            mInTransition = true;
            requestStateChange(context, newState);
        }
    }

    /**
     * Returns simplified 3-state value from underlying 5-state.
     *
     * @param context
     * @return STATE_ENABLED, STATE_DISABLED, or STATE_INTERMEDIATE
     */
    private int getTriState(Context cxt) {
        if (DEBUG) {
//            LogHelper.d(TAG, getDumpString("getTriState"));
        }

        if (mInTransition) {
            // If we know we just got a toggle request recently
            // (which set mInTransition), don't even ask the
            // underlying interface for its state. We know we're
            // changing. This avoids blocking the UI thread
            // during UI refresh post-toggle if the underlying
            // service state accessor has coarse locking on its
            // state (to be fixed separately).
            return STATE_INTERMEDIATE;
        }

        refreshActualState(cxt);

        switch (mState) {
            case STATE_DISABLED:
                return STATE_DISABLED;
            case STATE_ENABLED:
                return STATE_ENABLED;
            default:
                return STATE_INTERMEDIATE;
        }
    }

    /**
     * For debug only
     */
    protected String getDumpString(String prompt) {
        StringBuilder sb = new StringBuilder();
        sb.append(prompt);
        sb.append(": switchId=").append(mSwitchId);
        sb.append(", state{mInTransition=").append(mInTransition);
        sb.append(", mIntendedState=").append(mIntendedState);
        sb.append(", mDeferredStateChangeRequestNeeded=");
        sb.append(mDeferredStateChangeRequestNeeded);
        sb.append(", mActualState=").append(mActualState);
        sb.append("}");
        return sb.toString();
    }
}
