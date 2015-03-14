
package cn.zmdx.kaka.locker.initialization;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.InitPromptActivity;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;

public class InitializationManager {

    private static Context mContext;

    private static InitializationManager mInstance;

    private static InitializationHandler mInitializationHandler;

    private static boolean isMIUI;

    private static boolean isMeizu;

    private static String mMIUIVersion;

    public static final int TYPE_CLOSE_SYSTEM_LOCKER = 0;

    public static final int TYPE_ALLOW_FOLAT_WINDOW = 1;

    public static final int TYPE_TRUST = 2;

    public static final int TYPE_READ_NOTIFICATION = 3;

    private static final int MSG_SETTING_DELAY = 500;

    public static InitializationManager getInstance(Context context) {
        if (null == mInstance) {
            mContext = context;
            mInstance = new InitializationManager();
            mInitializationHandler = new InitializationHandler((Activity) mContext);
            isMIUI = PandoraUtils.isMIUI(mContext);
            mMIUIVersion = PandoraUtils.getSystemProperty();
            isMeizu = PandoraUtils.isMeizu(mContext);
        }
        return mInstance;
    }

    public void initializationLockScreen(int type) {
        if (type == TYPE_CLOSE_SYSTEM_LOCKER) {
            PandoraUtils.closeSystemLocker(mContext, isMIUI);
            if (mInitializationHandler.hasMessages(TYPE_CLOSE_SYSTEM_LOCKER)) {
                mInitializationHandler.removeMessages(TYPE_CLOSE_SYSTEM_LOCKER);
            }
            Message closeSystemLocker = Message.obtain();
            closeSystemLocker.what = TYPE_CLOSE_SYSTEM_LOCKER;
            mInitializationHandler.sendMessageDelayed(closeSystemLocker, MSG_SETTING_DELAY);
        } else if (type == TYPE_ALLOW_FOLAT_WINDOW) {
            PandoraUtils.setAllowFolatWindow(mContext, mMIUIVersion);
            if (mInitializationHandler.hasMessages(TYPE_ALLOW_FOLAT_WINDOW)) {
                mInitializationHandler.removeMessages(TYPE_ALLOW_FOLAT_WINDOW);
            }
            Message allowFloatWindow = Message.obtain();
            allowFloatWindow.what = TYPE_ALLOW_FOLAT_WINDOW;
            mInitializationHandler.sendMessageDelayed(allowFloatWindow, MSG_SETTING_DELAY);
        } else if (type == TYPE_TRUST) {
            PandoraUtils.setTrust(mContext, mMIUIVersion);
            if (mInitializationHandler.hasMessages(TYPE_TRUST)) {
                mInitializationHandler.removeMessages(TYPE_TRUST);
            }
            Message setTrust = Message.obtain();
            setTrust.what = TYPE_TRUST;
            mInitializationHandler.sendMessageDelayed(setTrust, MSG_SETTING_DELAY);
        } else if (type == TYPE_READ_NOTIFICATION) {
            PandoraUtils.setAllowReadNotification(mContext, isMIUI, mMIUIVersion, isMeizu);
            if (mInitializationHandler.hasMessages(TYPE_READ_NOTIFICATION)) {
                mInitializationHandler.removeMessages(TYPE_READ_NOTIFICATION);
            }
            Message readNotification = Message.obtain();
            readNotification.what = TYPE_READ_NOTIFICATION;
            mInitializationHandler.sendMessageDelayed(readNotification, MSG_SETTING_DELAY);
        }
    }

    private static class InitializationHandler extends Handler {
        WeakReference<Activity> mActivity;

        public InitializationHandler(Activity context) {
            mActivity = new WeakReference<Activity>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TYPE_CLOSE_SYSTEM_LOCKER:
                    showPromptActicity(isMIUI, mMIUIVersion,
                            InitPromptActivity.PROMPT_CLOSE_SYSTEM_LOCKER);
                    break;
                case TYPE_ALLOW_FOLAT_WINDOW:
                    showPromptActicity(isMIUI, mMIUIVersion,
                            InitPromptActivity.PROMPT_ALLOW_FLOAT_WINDOW);
                    break;
                case TYPE_TRUST:
                    showPromptActicity(isMIUI, mMIUIVersion, InitPromptActivity.PROMPT_TRRST);
                    break;
                case TYPE_READ_NOTIFICATION:
                    showPromptActicity(isMIUI, mMIUIVersion,
                            InitPromptActivity.PROMPT_READ_NOTIFICATION);
                    break;
            }
            super.handleMessage(msg);
        }

        public void showPromptActicity(boolean isMIUI, String mMIUIVersion, int type) {
            Activity activity = mActivity.get();
            Intent in = new Intent();
            in.setClass(activity, InitPromptActivity.class);
            in.putExtra("isMIUI", isMIUI);
            in.putExtra("mMIUIVersion", mMIUIVersion);
            in.putExtra("type", type);
            activity.startActivity(in);
            activity.overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                    R.anim.umeng_fb_slide_out_from_left);
        }
    }
}
