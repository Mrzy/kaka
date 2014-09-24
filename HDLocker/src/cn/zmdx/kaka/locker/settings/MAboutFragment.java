
package cn.zmdx.kaka.locker.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;

import com.umeng.analytics.MobclickAgent;

public class MAboutFragment extends Fragment {
    private View mRootView;

    private TextView mVersion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.setting_about_us, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        int height = (int) getActivity().getResources().getDimension(
                R.dimen.setting_about_us_height);
        int statusBarHeight = PandoraUtils.getStatusBarHeight(getActivity());
        LinearLayout titleLayout = (LinearLayout) mRootView.findViewById(R.id.setting_about_top);
        LayoutParams params = titleLayout.getLayoutParams();
        params.height = height + PandoraUtils.getStatusBarHeight(getActivity());
        titleLayout.setLayoutParams(params);
        titleLayout.setPadding(0, statusBarHeight, 0, 0);
        mVersion = (TextView) mRootView.findViewById(R.id.setting_about_version);
        String version = PandoraUtils.getVersionCode(getActivity());
        mVersion.setText(version);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); // 统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
