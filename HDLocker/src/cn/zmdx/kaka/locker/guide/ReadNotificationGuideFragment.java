
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
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.initialization.InitializationManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

public class ReadNotificationGuideFragment extends Fragment implements OnClickListener {

    private View mEntireView;

    private View mLayout;

    private TypefaceTextView mReadNotify;

    private TypefaceTextView mNextStep;

    public interface IReadNotificationListener {
        void onReadNotificationBack();
    }

    private IReadNotificationListener mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (IReadNotificationListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.read_notification_guide, container, false);
        mLayout = mEntireView.findViewById(R.id.background);
        mReadNotify = (TypefaceTextView) mEntireView.findViewById(R.id.read_notification);
        mReadNotify.setOnClickListener(this);
        mNextStep = (TypefaceTextView) mEntireView.findViewById(R.id.close_system_lock_next_step);
        mNextStep.setOnClickListener(this);
        renderScreenLockerBlurEffect(ImageUtils.drawable2Bitmap(getResources().getDrawable(
                R.drawable.pandora_default_background)));
        PandoraConfig.newInstance(getActivity()).saveReadNotifitionGuidedState(true);
        // PandoraConfig.newInstance(getActivity()).saveHasGuided();
        return mEntireView;
    }

    private void renderScreenLockerBlurEffect(Bitmap bmp) {
        GuideUtil.renderScreenLockerBlurEffect(getActivity(), mLayout, bmp);
    }

    @Override
    public void onClick(View view) {
        if (view == mReadNotify) {
            InitializationManager.getInstance(getActivity()).initializationLockScreen(
                    InitializationManager.TYPE_READ_NOTIFICATION);
        } else if (view == mNextStep) {
            if (null != mCallback) {
                mCallback.onReadNotificationBack();
            }
        }
    }

}
