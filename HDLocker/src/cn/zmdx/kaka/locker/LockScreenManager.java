
package cn.zmdx.kaka.locker;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import cn.zmdx.kaka.locker.widget.LockerViewGroup;
import cn.zmdx.kaka.locker.widget.LockerViewGroup.LockScreenListener;

public class LockScreenManager {

    private LockerViewGroup mEntireView;

    private static LockScreenManager INSTANCE = null;

    private WindowManager mWinManager = null;

    private boolean mIsLocked = false;

    private LockScreenManager() {
        mWinManager = (WindowManager) HDApplication.getInstannce().getSystemService(
                Context.WINDOW_SERVICE);

    }

    public static LockScreenManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LockScreenManager();
        }
        return INSTANCE;
    }

    public void lock() {
        if (mIsLocked)
            return;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = LayoutParams.FLAG_NOT_FOCUSABLE;

        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        params.x = 0;
        params.y = 0;
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        params.gravity = Gravity.TOP | Gravity.LEFT;

        if (mEntireView == null) {
            initLockScreenViews();
        }

        refreshContent();
        mWinManager.addView(mEntireView, params);

        mIsLocked = true;

    }

    private void refreshContent() {

    }

    private void initLockScreenViews() {
        mEntireView = (LockerViewGroup) LayoutInflater.from(HDApplication.getInstannce()).inflate(
                R.layout.pandora_lockscreen, null);
        mEntireView.setForegroundResource(R.drawable.locker_foreground);
        mEntireView.setOnLockScreenListener(mLockScreenListener);
    }

    public void unLock() {
        if (!mIsLocked)
            return;

        mWinManager.removeView(mEntireView);
        mIsLocked = false;
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    private LockScreenListener mLockScreenListener = new LockScreenListener() {

        @Override
        public void onStartTouch() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDeadLine() {
            unLock();
        }

        @Override
        public void onFixed() {
            // TODO Auto-generated method stub

        }

    };
}
