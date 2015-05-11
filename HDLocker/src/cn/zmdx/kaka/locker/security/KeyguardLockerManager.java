
package cn.zmdx.kaka.locker.security;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import cn.zmdx.kaka.locker.pattern.LockPatternManager;
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
        int style = PandoraConfig.newInstance(mContext).getLockPatternStyle(
                LockPatternManager.LOCK_PATTERN_STYLE_PURE);
        PandoraLockPatternView lockPatternView = new PandoraLockPatternView(mContext,
                PandoraLockPatternView.TYPE_LOCK_PATTERN_VERIFY, style,
                new PandoraLockPatternView.ILockPatternListener() {

                    @Override
                    public void onComplete(int type, boolean success) {
                        listener.onSuccess();
                    }
                }, true);
        lockPatternView.setGravity(Gravity.CENTER);
        lockPatternView.setShouldPath(PandoraConfig.newInstance(mContext).isHiddenLineOn());
        return lockPatternView;
    }

    private View makeNumberLockView(final IUnlockListener listener) {
        PandoraNumberLockView numberLockView = new PandoraNumberLockView(mContext,
                PandoraNumberLockView.LOCK_NUMBER_TYPE_VERIFY,
                new PandoraNumberLockView.INumberLockListener() {

                    @Override
                    public void onComplete(int type, boolean success) {
                        listener.onSuccess();
                    }

                }, true);
        numberLockView.setGravity(Gravity.CENTER);
        return numberLockView;
    }

    public static interface IUnlockListener {

        void onSuccess();

        void onFaild(View v);

    }
}
