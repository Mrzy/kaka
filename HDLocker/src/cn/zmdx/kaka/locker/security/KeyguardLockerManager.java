
package cn.zmdx.kaka.locker.security;

import android.content.Context;
import android.view.View;

public class KeyguardLockerManager {

    public static final int LOCKER_TYPE_NON = 0;

    public static final int LOCKER_TYPE_GESTURE = 1;

    public static final int LOCKER_TYPE_NUMBER = 2;

    private Context mContext;

    public KeyguardLockerManager(Context context) {
        mContext = context;
    }

    public static int getCurrentLockerType() {
        return LOCKER_TYPE_NON;
    }

    public View getCurrentLockerView(IUnlockListener listener) {
        int type = getCurrentLockerType();
        if (type == LOCKER_TYPE_NON) {
            return null;
        } else if (type == LOCKER_TYPE_GESTURE) {
            return makeGestureLockView(listener);
        } else if (type == LOCKER_TYPE_NUMBER) {
            return makeNumberLockView(listener);
        }
        return null;
    }

    private View makeGestureLockView(IUnlockListener listener) {
        // TODO
        return null;
    }

    private View makeNumberLockView(IUnlockListener listener) {
        // TODO
        return null;
    }

    public static interface IUnlockListener {

        void onSuccess();

        void onFaild(View v);

    }
}
