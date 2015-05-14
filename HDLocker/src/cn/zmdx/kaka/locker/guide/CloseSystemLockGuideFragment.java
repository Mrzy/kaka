
package cn.zmdx.kaka.locker.guide;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.initialization.InitializationManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.BlurUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

public class CloseSystemLockGuideFragment extends Fragment implements OnClickListener {

    private View mEntireView;

    private View mLayout;

    private TypefaceTextView mCloseSystemLock;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.close_system_lock_guide, container, false);
        mLayout = mEntireView.findViewById(R.id.background);
        mCloseSystemLock = (TypefaceTextView) mEntireView.findViewById(R.id.close_system_lock);
        mCloseSystemLock.setOnClickListener(this);
        renderScreenLockerBlurEffect(ImageUtils.drawable2Bitmap(getResources().getDrawable(
                R.drawable.pandora_default_background)));
        PandoraConfig.newInstance(getActivity()).saveCloseSystemLockState(true);
        return mEntireView;
    }

    private Bitmap mBlurBmp;

    private void renderScreenLockerBlurEffect(Bitmap bmp) {
        if (mBlurBmp != null && !mBlurBmp.isRecycled()) {
            mBlurBmp.recycle();
            mBlurBmp = null;
        }
        mBlurBmp = BlurUtils.doFastBlur(getActivity(), bmp, mLayout, 30);
    }

    @Override
    public void onClick(View view) {
        if (view == mCloseSystemLock) {
            InitializationManager.getInstance(getActivity()).initializationLockScreen(
                    InitializationManager.TYPE_CLOSE_SYSTEM_LOCKER);
        }
    }
}
