
package cn.zmdx.kaka.locker.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;

public class MIUISettingFragment extends Fragment implements OnClickListener {

    private String mMIUIVersion;

    private View mRootView;

    private Button mFolatfingWindowBtn;

    private Button mTrustBtn;

    private PandoraConfig mPandoraConfig;

    private int mThemeId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.setting_miui_fragment, container, false);
        initView();
        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMIUIVersion = PandoraUtils.getSystemProperty();
        mPandoraConfig = PandoraConfig.newInstance(activity);
        mThemeId = mPandoraConfig.getCurrentThemeId();
    }

    private void initView() {
//        int resId = ThemeManager.getThemeById(mThemeId).getmBackgroundResId();
//        mRootView.findViewById(R.id.setting_miui_background).setBackgroundResource(resId);
        mFolatfingWindowBtn = (Button) mRootView
                .findViewById(R.id.setting_MIUI_allow_floating_window_to_set);
        mFolatfingWindowBtn.setOnClickListener(this);
        mTrustBtn = (Button) mRootView.findViewById(R.id.setting_MIUI_trust_to_set);
        mTrustBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_MIUI_allow_floating_window_to_set:
                PandoraUtils.setAllowFolatWindow(getActivity(), mMIUIVersion);
                break;
            case R.id.setting_MIUI_trust_to_set:
                PandoraUtils.setTrust(getActivity(), mMIUIVersion);
                break;

            default:
                break;
        }
    }

    private void showToast() {
        Toast toast = new Toast(getActivity());
        toast.setText("");
    }

    @Override
    public void onDestroyView() {
        getFragmentManager().beginTransaction().remove(this).commit();
        super.onDestroyView();
    }
}
