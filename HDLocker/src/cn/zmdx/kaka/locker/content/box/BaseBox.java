
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

public abstract class BaseBox implements IPandoraBox {

    private boolean isShare = true;

    private View mRootView;

    private Button mShareBtn;

    private View mPlatformLayout;

    private ImageView mPQzone;

    private ImageView mPSina;

    private ImageView mPWechat;

    private ImageView mPWxcircle;

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
        mPSina = (ImageView) mRootView.findViewById(R.id.platforms_sina);
        mPWechat = (ImageView) mRootView.findViewById(R.id.platforms_wechat);
        mPWxcircle = (ImageView) mRootView.findViewById(R.id.platforms_wxcircle);
        mShareBtn.setOnClickListener(mShareBtnListener);
        mPQzone.setOnClickListener(mShareBtnListener);
        mPSina.setOnClickListener(mShareBtnListener);
        mPWechat.setOnClickListener(mShareBtnListener);
        mPWxcircle.setOnClickListener(mShareBtnListener);
        return mRootView;
    }

    private View.OnClickListener mShareBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mShareBtn) {
                int visibility = mPlatformLayout.getVisibility();
                if (visibility == View.VISIBLE) {
                    mPlatformLayout.setVisibility(View.INVISIBLE);
                } else {
                    mPlatformLayout.setVisibility(View.VISIBLE);
                }
            } else if (v == mPQzone) {
                share(PandoraShareManager.Tencent);
            } else if (v == mPSina) {
                share(PandoraShareManager.Sina);
            } else if (v == mPWechat) {
                share(PandoraShareManager.Weixin);
            } else if (v == mPWxcircle) {
                share(PandoraShareManager.WeixinCircle);
            }
        }
    };

    private void share(final int platform) {
        LockScreenManager.getInstance().setWindowAnimations(android.R.anim.slide_in_left);
        LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(FakeActivity.ACTION_PANDORA_SHARE);
                intent.setPackage(mContext.getPackageName());

                intent.putExtra("platform", platform);
                String imageUrl = getData().getmImageUrl();
                String path = DiskImageHelper.getFileByUrl(imageUrl).getAbsolutePath();
                intent.putExtra("imagePath", path);
                intent.putExtra("imagetitle", getData().getmTitle());
                intent.putExtra("isHtml", getCategory() == IPandoraBox.CATEGORY_HTML);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        });
        LockScreenManager.getInstance().unLock(false);
    }
}
