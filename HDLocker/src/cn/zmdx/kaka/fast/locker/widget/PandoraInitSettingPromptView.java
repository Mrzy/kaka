
package cn.zmdx.kaka.fast.locker.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;

public class PandoraInitSettingPromptView extends LinearLayout implements OnClickListener {

    private LinearLayout mCloseSystemLockerView;

    private LinearLayout mV5CloseSystemLockerView;

    private LinearLayout mV5AllowFloatWindowView;

    private LinearLayout mV5TrustView;

    private LinearLayout mV6CloseSystemLockerView;

    private LinearLayout mV6AllowFloatWindowView;

    private LinearLayout mV6TrustView;

    private LinearLayout mReadNotificationView;

    private boolean isMIUI;

    private String mMIUIVersion;

    public static final int PROMPT_CLOSE_SYSTEM_LOCKER = 1;

    public static final int PROMPT_ALLOW_FLOAT_WINDOW = 2;

    public static final int PROMPT_TRRST = 3;

    public static final int PROMPT_READ_NOTIFICATION = 4;

    private int mPromptType;

    private Context mContext;

    private View mRootView;

    private RippleView mV6CloseSystemLockerButton;

    private RippleView mV6AllowFloatWindowButton;

    private RippleView mV6TrustButton;

    private RippleView mReadNotificationButton;

    private RippleView mCloseSystemLockerButton;

    private IPromptViewListener mListener;

    public static double SCALE_CLOSE_SYSTEM_LOCKER_WIDTH = 0.8;

    public static double SCALE_CLOSE_SYSTEM_LOCKER_PADDING = 0.05;

    public interface IPromptViewListener {
        void onButtonClickListener();
    }

    public PandoraInitSettingPromptView(Context context) {
        super(context);
        mContext = context;
    }

    public void initType(boolean ismiui, String MIUIVersion, int promptType,
            IPromptViewListener listener) {
        isMIUI = ismiui;
        mMIUIVersion = MIUIVersion;
        mPromptType = promptType;
        mListener = listener;
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.init_prompt_view, null);
        addView(mRootView);
        initView();
        showView();
    }

    private void initView() {
        if (isMIUI) {
            if (PandoraUtils.MUIU_V6.equals(mMIUIVersion)) {
                mRootView.findViewById(R.id.init_setting_close_systemlocker_prompt).setVisibility(
                        View.GONE);
                mRootView.findViewById(R.id.init_setting_MIUI_V5).setVisibility(View.GONE);
                mRootView.findViewById(R.id.init_setting_MIUI_V6).setVisibility(View.VISIBLE);
                mV6CloseSystemLockerView = (LinearLayout) mRootView
                        .findViewById(R.id.init_setting_V6_close_systemlocker_prompt);
                mV6CloseSystemLockerButton = (RippleView) mRootView
                        .findViewById(R.id.init_setting_V6_close_systemlocker_prompt_button);
                mV6CloseSystemLockerButton.setOnClickListener(this);
                mV6AllowFloatWindowView = (LinearLayout) mRootView
                        .findViewById(R.id.init_setting_V6_allow_floating_window_prompt);
                mV6AllowFloatWindowButton = (RippleView) mRootView
                        .findViewById(R.id.init_setting_V6_allow_floating_window_prompt_button);
                mV6AllowFloatWindowButton.setOnClickListener(this);
                mV6TrustView = (LinearLayout) mRootView
                        .findViewById(R.id.init_setting_V6_trust_prompt);
                mV6TrustButton = (RippleView) mRootView
                        .findViewById(R.id.init_setting_V6_trust_prompt_button);
                mV6TrustButton.setOnClickListener(this);
                mReadNotificationView = (LinearLayout) mRootView
                        .findViewById(R.id.init_setting_read_notification_prompt);
                mReadNotificationButton = (RippleView) mRootView
                        .findViewById(R.id.init_setting_V6_read_notification_prompt_button);
                mReadNotificationButton.setOnClickListener(this);
            } else {
                mRootView.findViewById(R.id.init_setting_close_systemlocker_prompt).setVisibility(
                        View.GONE);
                mRootView.findViewById(R.id.init_setting_MIUI_V5).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.init_setting_MIUI_V6).setVisibility(View.GONE);
                mV5CloseSystemLockerView = (LinearLayout) mRootView
                        .findViewById(R.id.init_setting_V5_close_systemlocker_prompt_miui);
                mV5AllowFloatWindowView = (LinearLayout) mRootView
                        .findViewById(R.id.init_setting_V5_allow_floating_window_prompt);
                mV5TrustView = (LinearLayout) mRootView
                        .findViewById(R.id.init_setting_V5_trust_prompt);
                mReadNotificationView = (LinearLayout) mRootView
                        .findViewById(R.id.init_setting_read_notification_prompt);
                mReadNotificationButton = (RippleView) mRootView
                        .findViewById(R.id.init_setting_V6_read_notification_prompt_button);
            }
        } else {
            mRootView.findViewById(R.id.init_setting_MIUI_V5).setVisibility(View.GONE);
            mRootView.findViewById(R.id.init_setting_MIUI_V6).setVisibility(View.GONE);
            int screenWidth = BaseInfoHelper.getRealWidth(mContext);
            int padding = (int) (screenWidth * SCALE_CLOSE_SYSTEM_LOCKER_PADDING);
            mCloseSystemLockerView = (LinearLayout) mRootView
                    .findViewById(R.id.init_setting_close_systemlocker_prompt);
            mCloseSystemLockerView.setPadding(padding, 0, padding, 0);
            int viewWidth = (int) (screenWidth * SCALE_CLOSE_SYSTEM_LOCKER_WIDTH);
            ViewGroup.LayoutParams params = mCloseSystemLockerView.getLayoutParams();
            params.width = viewWidth + padding;
            mCloseSystemLockerView.setLayoutParams(params);
            mCloseSystemLockerButton = (RippleView) mRootView
                    .findViewById(R.id.init_setting_close_systemlocker_prompt_button);
            mCloseSystemLockerButton.setOnClickListener(this);
            mReadNotificationView = (LinearLayout) mRootView
                    .findViewById(R.id.init_setting_read_notification_prompt);
            mReadNotificationButton = (RippleView) mRootView
                    .findViewById(R.id.init_setting_V6_read_notification_prompt_button);
        }

    }

    private void showView() {
        if (isMIUI) {
            if (PandoraUtils.MUIU_V6.equals(mMIUIVersion)) {

                switch (mPromptType) {
                    case PROMPT_CLOSE_SYSTEM_LOCKER:
                        mV6CloseSystemLockerView.setVisibility(View.VISIBLE);
                        break;
                    case PROMPT_ALLOW_FLOAT_WINDOW:
                        mV6AllowFloatWindowView.setVisibility(View.VISIBLE);
                        break;
                    case PROMPT_TRRST:
                        mV6TrustView.setVisibility(View.VISIBLE);
                        break;
                    case PROMPT_READ_NOTIFICATION:
                        mReadNotificationView.setVisibility(View.VISIBLE);
                        break;

                    default:
                        break;
                }

            } else {

                switch (mPromptType) {
                    case PROMPT_CLOSE_SYSTEM_LOCKER:
                        mV5CloseSystemLockerView.setVisibility(View.VISIBLE);
                        break;
                    case PROMPT_ALLOW_FLOAT_WINDOW:
                        mV5AllowFloatWindowView.setVisibility(View.VISIBLE);
                        break;
                    case PROMPT_TRRST:
                        mV5TrustView.setVisibility(View.VISIBLE);
                        break;
                    case PROMPT_READ_NOTIFICATION:
                        mReadNotificationView.setVisibility(View.VISIBLE);
                        break;

                    default:
                        break;
                }

            }
        } else {
            switch (mPromptType) {
                case PROMPT_CLOSE_SYSTEM_LOCKER:
                    mCloseSystemLockerView.setVisibility(View.VISIBLE);
                    break;
                case PROMPT_READ_NOTIFICATION:
                    mReadNotificationView.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }

    }

    @Override
    public void onClick(View v) {
        mListener.onButtonClickListener();
    }

}
