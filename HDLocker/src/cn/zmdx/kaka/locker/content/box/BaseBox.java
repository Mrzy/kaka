
package cn.zmdx.kaka.locker.content.box;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.FakeActivity;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.DiskImageHelper;
import cn.zmdx.kaka.locker.share.PandoraShareManager;

public abstract class BaseBox implements IPandoraBox{

    private boolean isShare = true;

    private View mRootView;

    private Button mShareBtn;

    private View mPlatformLayout;

    private ImageView mPQzone;

    private Context mContext;

    public BaseBox() {
        mContext = HDApplication.getInstannce();
    }

    public void enableShare() {
        isShare = true;
    }

    public void disableShare() {
        isShare = false;
    }

    public View createShareView() {
        if (!isShare) {
            throw new IllegalStateException("没有开启分享功能。请先调用enableShare()方法开启分享功能");
        }

        if (mRootView != null) {
            return mRootView;
        }
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.pandora_box_share_layout, null);
        mShareBtn = (Button) mRootView.findViewById(R.id.shareBtn);
        mPlatformLayout = mRootView.findViewById(R.id.platforms_layout);
        mPQzone = (ImageView) mRootView.findViewById(R.id.platforms_qzone);
        mShareBtn.setOnClickListener(mShareBtnListener);
        return mRootView;
    }

    private View.OnClickListener mShareBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mPlatformLayout) {
                int visibility = mPlatformLayout.getVisibility();
                if (visibility == View.VISIBLE) {
                    mPlatformLayout.setVisibility(View.INVISIBLE);
                } else {
                    mPlatformLayout.setVisibility(View.VISIBLE);
                }
            } else if (v == mPQzone) {
                share(PandoraShareManager.Tencent);
            }
        }
    };

    private void share(int platform) {
        Intent intent = new Intent();
        intent.setAction(FakeActivity.ACTION_PANDORA_SHARE);
        intent.setPackage(mContext.getPackageName());

        intent.putExtra("platform", platform);
        String imageUrl = getData().getmImageUrl();
        String path = DiskImageHelper.getFileByUrl(imageUrl).getAbsolutePath();
        intent.putExtra("imagePath", path);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        LockScreenManager.getInstance().setWindowAnimations(android.R.anim.slide_in_left);
        LockScreenManager.getInstance().unLock(false);
    }
}
