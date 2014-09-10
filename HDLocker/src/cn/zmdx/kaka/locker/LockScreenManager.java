
package cn.zmdx.kaka.locker;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout.PanelSlideListener;

public class LockScreenManager {

    // private LockerViewGroup mEntireView;
    private SlidingUpPanelLayout mSliderView;

    private View mEntireView;

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

        // if (mEntireView == null) {
        initLockScreenViews();
        // }

        refreshContent();
        mWinManager.addView(mEntireView, params);

        mIsLocked = true;

    }

    private void refreshContent() {

    }

    private void initLockScreenViews() {
        // mEntireView = (LockerViewGroup)
        // LayoutInflater.from(HDApplication.getInstannce()).inflate(
        // R.layout.pandora_lockscreen, null);
        // mEntireView.setForegroundResource(R.drawable.locker_foreground);
        // mEntireView.setOnLockScreenListener(mLockScreenListener);
        mEntireView = LayoutInflater.from(HDApplication.getInstannce()).inflate(
                R.layout.pandora_lockscreen, null);
        mSliderView = (SlidingUpPanelLayout) mEntireView.findViewById(R.id.locker_view);
        mSliderView.setPanelSlideListener(mSlideListener);
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

    private PanelSlideListener mSlideListener = new PanelSlideListener() {

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
        }

        @Override
        public void onPanelCollapsed(View panel) {
            unLock();
        }

        @Override
        public void onPanelExpanded(View panel) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPanelAnchored(View panel) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPanelHidden(View panel) {
            // TODO Auto-generated method stub

        }

    };
}
