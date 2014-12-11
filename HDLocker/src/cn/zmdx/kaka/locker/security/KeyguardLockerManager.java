
package cn.zmdx.kaka.locker.security;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.widget.PandoraLockPatternView;
import cn.zmdx.kaka.locker.widget.PandoraNumberLockView;

public class KeyguardLockerManager {

    public static final int UNLOCKER_TYPE_NONE = 0;

    public static final int UNLOCKER_TYPE_NUMBER_LOCK = 2;

    public static final int UNLOCKER_TYPE_LOCK_PATTERN = 1;

    private Context mContext;

    public KeyguardLockerManager(Context context) {
        mContext = context;
    }

    public int getCurrentLockerType() {
        return PandoraConfig.newInstance(mContext).getUnLockType();
    }

    public View getCurrentLockerView(IUnlockListener listener) {
        int type = getCurrentLockerType();
        if (type == UNLOCKER_TYPE_NONE) {
            return null;
        } else if (type == UNLOCKER_TYPE_LOCK_PATTERN) {
            return makeGestureLockView(listener);
        } else if (type == UNLOCKER_TYPE_NUMBER_LOCK) {
            return makeNumberLockView(listener);
        }
        return null;
    }

    private View makeGestureLockView(final IUnlockListener listener) {
        PandoraLockPatternView lockPatternView = new PandoraLockPatternView(mContext,
                PandoraLockPatternView.TYPE_LOCK_PATTERN_VERIFY,
                new PandoraLockPatternView.IVerifyListener() {

                    @Override
                    public void onVerifySuccess() {
                        listener.onSuccess();
                    }
                });
        lockPatternView.setGravity(Gravity.CENTER);
        return lockPatternView;
    }

    private View makeNumberLockView(final IUnlockListener listener) {
        PandoraNumberLockView numberLockView = new PandoraNumberLockView(mContext,
                PandoraNumberLockView.LOCK_NUMBER_TYPE_VERIFY,
                new PandoraNumberLockView.IVerifyListener() {

                    @Override
                    public void onVerifySuccess() {
                        listener.onSuccess();
                    }

                });
        numberLockView.setGravity(Gravity.CENTER);
        return numberLockView;
    }

    public static interface IUnlockListener {

        void onSuccess();

        void onFaild(View v);

    }
}
