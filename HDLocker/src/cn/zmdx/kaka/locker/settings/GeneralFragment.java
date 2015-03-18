
package cn.zmdx.kaka.locker.settings;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.initialization.InitializationManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.widget.SwitchButton;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class GeneralFragment extends Fragment implements OnCheckedChangeListener, OnClickListener {

    private View mEntireView;

    private SwitchButton mPandoraLockerSButton;

    private SwitchButton mShowNotifySButton;

    private SwitchButton mOpenSoundSButton;

    private LinearLayout mCloseSystemLock;

    private LinearLayout mFloatingWindow;

    private LinearLayout mTrust;

    private LinearLayout mFAQ;

    private LinearLayout mFeedback;

    private LinearLayout mCheckNewVersion;

    private LinearLayout mAboutPandora;

    private Context mContext;

    private PandoraConfig mPandoraConfig;

    private Button mEvaluationPraise;

    private Button mEvaluationBadReview;

    private static boolean isMeizu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mPandoraConfig = PandoraConfig.newInstance(mContext);
        isMeizu = PandoraUtils.isMeizu(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.general_fragment, container, false);
        initView();
        initSwitchButtonState();
        return mEntireView;
    }

    private void initView() {
        mPandoraLockerSButton = (SwitchButton) mEntireView
                .findViewById(R.id.setting_pandoralocker_switch_button);
        mPandoraLockerSButton.setOnCheckedChangeListener(this);

        mShowNotifySButton = (SwitchButton) mEntireView
                .findViewById(R.id.setting_show_notify_switch_button);
        mShowNotifySButton.setOnCheckedChangeListener(this);

        mOpenSoundSButton = (SwitchButton) mEntireView
                .findViewById(R.id.setting_open_lock_sound_switch_button);
        mOpenSoundSButton.setOnCheckedChangeListener(this);

        initSettingMeizu();

        initSettingXiaomi();

        mFAQ = (LinearLayout) mEntireView.findViewById(R.id.setting_faq_item);
        mFAQ.setOnClickListener(this);

        mFeedback = (LinearLayout) mEntireView.findViewById(R.id.setting_feedback_prompt);
        mFeedback.setOnClickListener(this);

        mCheckNewVersion = (LinearLayout) mEntireView
                .findViewById(R.id.setting_checkout_new_version_prompt);
        mCheckNewVersion.setOnClickListener(this);

        mAboutPandora = (LinearLayout) mEntireView.findViewById(R.id.setting_about_pandora_item);
        mAboutPandora.setOnClickListener(this);

        mEvaluationPraise = (Button) mEntireView.findViewById(R.id.setting_evaluation_praise);
        mEvaluationPraise.setOnClickListener(this);

        mEvaluationBadReview = (Button) mEntireView
                .findViewById(R.id.setting_evaluation_bad_review);
        mEvaluationBadReview.setOnClickListener(this);

    }

    private void initSettingMeizu() {
        mCloseSystemLock = (LinearLayout) mEntireView
                .findViewById(R.id.setting_close_system_lock_item);
        if (isMeizu) {
            mCloseSystemLock.setVisibility(View.GONE);
            mEntireView.findViewById(R.id.setting_close_system_lock_item).setVisibility(View.GONE);
        } else {
            mCloseSystemLock.setOnClickListener(this);
        }
    }

    private void initSettingXiaomi() {
        if (PandoraUtils.isMIUI(mContext)) {
            mEntireView.findViewById(R.id.setting_xiaomi).setVisibility(View.VISIBLE);
            mFloatingWindow = (LinearLayout) mEntireView
                    .findViewById(R.id.setting_allow_floating_window_item);
            mFloatingWindow.setOnClickListener(this);

            mTrust = (LinearLayout) mEntireView.findViewById(R.id.setting_trust_item);
            mTrust.setOnClickListener(this);
        }
    }

    private void initSwitchButtonState() {
        mPandoraLockerSButton.setChecked(isPandoraLockerOn());
        mShowNotifySButton.setChecked(isNotifyFunctionOn());
        mOpenSoundSButton.setChecked(isLockSoundOn());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mPandoraLockerSButton) {
            if (isChecked) {
                enablePandoraLocker();
                UmengCustomEventManager.statisticalPandoraSwitchOpenTimes();
            } else {
                disablePandoraLocker();
                UmengCustomEventManager.statisticalPandoraSwitchCloseTimes();
            }
        } else if (buttonView == mShowNotifySButton) {
            if (isChecked) {
                enableNotifyFunction();
                UmengCustomEventManager.statisticalShowNotifyTimes();
            } else {
                disableNotifyFunction();
                UmengCustomEventManager.statisticalCloseNotifyTimes();
            }
        } else if (buttonView == mOpenSoundSButton) {
            if (isChecked) {
                enableLockSound();
                UmengCustomEventManager.statisticalEnableLockScreenSound();
            } else {
                disableLockSound();
                UmengCustomEventManager.statisticalDisableLockScreenSound();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("GeneralFragment");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("GeneralFragment");
    }

    @Override
    public void onClick(View view) {
        if (view == mCloseSystemLock) {
            InitializationManager.getInstance(getActivity()).initializationLockScreen(
                    InitializationManager.TYPE_CLOSE_SYSTEM_LOCKER);
        } else if (view == mFloatingWindow) {
            InitializationManager.getInstance(getActivity()).initializationLockScreen(
                    InitializationManager.TYPE_ALLOW_FOLAT_WINDOW);
        } else if (view == mTrust) {
            InitializationManager.getInstance(getActivity()).initializationLockScreen(
                    InitializationManager.TYPE_TRUST);
        } else if (view == mFAQ) {
            startActivity(FAQActivity.class);
        } else if (view == mFeedback) {
            startActivity(FeedbackActivity.class);
        } else if (view == mCheckNewVersion) {
            UmengUpdateAgent.forceUpdate(mContext);
            UmengUpdateAgent.setUpdateAutoPopup(false);
            UmengUpdateAgent.setUpdateListener(mUpdateListener);
        } else if (view == mAboutPandora) {
            startActivity(AboutActivity.class);
        } else if (view == mEvaluationPraise) {
            gotoEvaluationPraise();
        } else if (view == mEvaluationBadReview) {
            startActivity(FeedbackActivity.class);
        }
    }

    private void startActivity(Class<?> mClass) {
        Intent in = new Intent();
        in.setClass(getActivity(), mClass);
        startActivity(in);
        getActivity().overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private UmengUpdateListener mUpdateListener = new UmengUpdateListener() {
        @Override
        public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
            switch (updateStatus) {
                case 0: // has update
                    UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
                    break;
                case 1: // has no update
                    Toast.makeText(mContext,
                            mContext.getResources().getString(R.string.update_prompt_no_update),
                            Toast.LENGTH_LONG).show();
                    break;
                case 2: // none wifi
                    Toast.makeText(mContext,
                            mContext.getResources().getString(R.string.update_prompt_no_wify),
                            Toast.LENGTH_LONG).show();
                    break;
                case 3: // time out
                    Toast.makeText(mContext,
                            mContext.getResources().getString(R.string.update_prompt_no_internet),
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private void gotoEvaluationPraise() {
        String locale = BaseInfoHelper.getLocale(getActivity());
        if (locale.equals(Locale.CHINA.toString())) {
            Uri uri = Uri.parse("market://details?id=" + BaseInfoHelper.getPkgName(getActivity()));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="
                                + BaseInfoHelper.getPkgName(getActivity())));
                browserIntent.setClassName("com.android.vending",
                        "com.android.vending.AssetBrowserActivity");
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(browserIntent);
            } catch (Exception e) {
                Uri uri = Uri.parse("market://details?id="
                        + BaseInfoHelper.getPkgName(getActivity()));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

    }

    private boolean isPandoraLockerOn() {
        return mPandoraConfig.isPandolaLockerOn();
    }

    private void enablePandoraLocker() {
        mPandoraConfig.savePandolaLockerState(true);
    }

    private void disablePandoraLocker() {
        mPandoraConfig.savePandolaLockerState(false);
    }

    private boolean isNotifyFunctionOn() {
        return mPandoraConfig.isNotifyFunctionOn();
    }

    private void enableNotifyFunction() {
        mPandoraConfig.saveNotifyFunctionState(true);
    }

    private void disableNotifyFunction() {
        mPandoraConfig.saveNotifyFunctionState(false);
    }

    private boolean isLockSoundOn() {
        return mPandoraConfig.isLockSoundOn();
    }

    private void enableLockSound() {
        mPandoraConfig.saveLockSoundState(true);
    }

    private void disableLockSound() {
        mPandoraConfig.saveLockSoundState(false);
    }

}
