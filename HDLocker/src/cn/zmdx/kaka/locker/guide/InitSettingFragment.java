
package cn.zmdx.kaka.locker.guide;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.initialization.InitializationManager;
import cn.zmdx.kaka.locker.utils.BlurUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

public class InitSettingFragment extends Fragment implements OnClickListener {

    private View mEntireView;

    private View mLayout;

    private TypefaceTextView mSkipBtn;

    private RelativeLayout mFolatfingWindowLayout;

    private RelativeLayout mTrustLayout;

    public interface ISettingFragmentListener {
        void onInitSettingSkip();
    }

    private ISettingFragmentListener mCallBack;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallBack = (ISettingFragmentListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.init_setting_guide, container, false);
        mLayout = mEntireView.findViewById(R.id.background);
        initView();
        renderScreenLockerBlurEffect(ImageUtils.drawable2Bitmap(getResources().getDrawable(
                R.drawable.pandora_default_background)));
        return mEntireView;
    }

    private void initView() {

        mSkipBtn = (TypefaceTextView) mEntireView.findViewById(R.id.init_setting_skip);
        mSkipBtn.setOnClickListener(this);

        mFolatfingWindowLayout = (RelativeLayout) mEntireView
                .findViewById(R.id.init_setting_MIUI_allow_floating_window_guide);
        mFolatfingWindowLayout.setOnClickListener(this);
        mTrustLayout = (RelativeLayout) mEntireView
                .findViewById(R.id.init_setting_MIUI_trust_guide);
        mTrustLayout.setOnClickListener(this);

    }

    private Bitmap mBlurBmp;

    private void renderScreenLockerBlurEffect(Bitmap bmp) {
        if (mBlurBmp != null && !mBlurBmp.isRecycled()) {
            mBlurBmp.recycle();
            mBlurBmp = null;
        }
        mBlurBmp = BlurUtils.doFastBlur(getActivity(), bmp, mLayout, 40);
    }

    @Override
    public void onClick(View view) {
        if (view == mFolatfingWindowLayout) {
            InitializationManager.getInstance(getActivity()).initializationLockScreen(
                    InitializationManager.TYPE_ALLOW_FOLAT_WINDOW);
        } else if (view == mTrustLayout) {
            InitializationManager.getInstance(getActivity()).initializationLockScreen(
                    InitializationManager.TYPE_TRUST);
        } else if (view == mSkipBtn) {
            if (null != mCallBack) {
                mCallBack.onInitSettingSkip();
            }
        }
    }
}
