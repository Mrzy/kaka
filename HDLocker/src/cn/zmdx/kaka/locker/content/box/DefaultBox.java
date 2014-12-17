
package cn.zmdx.kaka.locker.content.box;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;

public class DefaultBox implements IPandoraBox {
    private Context mContext;

    private ViewGroup mLayoutView;

    private PandoraData mData;

    private RelativeLayout mDefaultRl;

    private ImageView mCustomImageView;

    private ImageView mCustomSetImageView;

    private boolean isCustomSetViewShow = false;

    private TextView mTextView3;

    private boolean isSetCustomImage = false;
    public DefaultBox(Context context, PandoraData data) {
        mContext = context;
        mData = data;
        mLayoutView = (ViewGroup) LayoutInflater.from(context).inflate(
                R.layout.pandora_box_nodata_show, null);
        mTextView3 = (TextView) mLayoutView.findViewById(R.id.pandora_box_no_net_prompt);
        if (!HDBNetworkState.isNetworkAvailable()) {
            mTextView3.setVisibility(View.VISIBLE);
            mTextView3.setText(mContext.getText(R.string.pandora_box_no_net_tip));
        } else {
            mTextView3.setVisibility(View.INVISIBLE);
        }
        mDefaultRl = (RelativeLayout) mLayoutView.findViewById(R.id.pandora_box_nodata_default);

        mCustomImageView = (ImageView) mLayoutView
                .findViewById(R.id.pandora_box_nodata_custom_show_imageview);
        mCustomImageView.setOnClickListener(clickListener);

        mCustomSetImageView = (ImageView) mLayoutView
                .findViewById(R.id.pandora_box_custom_set_default_image);
        mCustomSetImageView.setOnClickListener(clickListener);
        initDefaultImage(context);
    }

    private void initDefaultImage(Context context) {
        BitmapDrawable drawable = PandoraUtils.getLockDefaultBitmap(context);
        if (null != drawable) {
            isSetCustomImage = true;
            mCustomImageView.setImageDrawable(drawable);
            mDefaultRl.setVisibility(View.GONE);
        } else {
            isSetCustomImage = false;
            mDefaultRl.setVisibility(View.VISIBLE);
        }
    }

    public boolean isSetCustomImage() {
        return isSetCustomImage;
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.pandora_box_nodata_custom_show_imageview) {
                isCustomSetViewShow = !isCustomSetViewShow;
                if (isCustomSetViewShow) {
                    mCustomSetImageView.setVisibility(View.VISIBLE);
                } else {
                    mCustomSetImageView.setVisibility(View.GONE);
                }
            } else {
                UmengCustomEventManager.statisticalSetLockScreenWallpaperTimes();
                LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {

                    @Override
                    public void run() {
                        LockScreenManager.getInstance().onInitDefaultImage();
                    }
                });
                LockScreenManager.getInstance().unLock();
            }
        }
    };

    @Override
    public int getCategory() {
        return IPandoraBox.CATEGORY_DEFAULT;
    }

    @Override
    public PandoraData getData() {
        return mData;
    }

    @Override
    public View getContainer() {
        return mLayoutView;
    }

    @Override
    public View getRenderedView() {
        return mLayoutView;
    }

}
