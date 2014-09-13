
package cn.zmdx.kaka.locker;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import cn.zmdx.kaka.locker.content.IPandoraBox;
import cn.zmdx.kaka.locker.content.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.database.DatabaseModel;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout.PanelSlideListener;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout.SimplePanelSlideListener;

public class LockScreenManager {

    // private LockerViewGroup mEntireView;
    private SlidingUpPanelLayout mSliderView;

    private View mEntireView;

    private ViewGroup mMainView;

    private static LockScreenManager INSTANCE = null;

    private WindowManager mWinManager = null;

    private boolean mIsLocked = false;

    private IPandoraBox mPandoraBox = null;

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
        params.windowAnimations = R.style.anim_locker_window;
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        params.gravity = Gravity.TOP | Gravity.LEFT;

        initLockScreenViews();

        refreshContent();
        mWinManager.addView(mEntireView, params);

        mIsLocked = true;
    }

    private void refreshContent() {
        mPandoraBox = PandoraBoxManager.newInstance(HDApplication.getInstannce())
                .getNextPandoraData();
        View contentView = mPandoraBox.getRenderedView();
        mMainView.removeAllViews();
        mMainView.addView(contentView);
    }

    private void initLockScreenViews() {
        mEntireView = LayoutInflater.from(HDApplication.getInstannce()).inflate(
                R.layout.pandora_lockscreen, null);
        mMainView = (ViewGroup) mEntireView.findViewById(R.id.mainView);
        mSliderView = (SlidingUpPanelLayout) mEntireView.findViewById(R.id.locker_view);
        mSliderView.setPanelSlideListener(mSlideListener);
    }

    public void unLock() {
        if (!mIsLocked)
            return;

        mWinManager.removeView(mEntireView);
        mIsLocked = false;
        releaseResource();
    }

    private void releaseResource() {
        PandoraData data = mPandoraBox.getData();
        if (data != null) {
            Bitmap bmp = data.getmImage();
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
        }
        DatabaseModel.getInstance().deleteById(data.getmId());
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    private PanelSlideListener mSlideListener = new SimplePanelSlideListener() {

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
        }

        @Override
        public void onPanelCollapsed(View panel) {
            Log.e("zy", "onPanelCollapsed");
            mSliderView.hidePanel();
        }

        @Override
        public void onPanelExpanded(View panel) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPanelHidden(View panel) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPanelFixed(View panel) {
            Log.e("zy", "onPanelFixed");

        }

        @Override
        public void onPanelClickedDuringFixed() {
            // 判断是否开启密码解锁
            mSliderView.hidePanel();
        }

        public void onPanelStartDown(View view) {
            Log.e("zy", "onPanelStartDown");
        };

        public void onPanelHiddenEnd() {
            Log.e("zy", "onPanelHiddenEnd");
            unLock();
        };
    };
}
