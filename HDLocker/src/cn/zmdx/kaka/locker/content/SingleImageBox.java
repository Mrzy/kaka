
package cn.zmdx.kaka.locker.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;

public class SingleImageBox implements IPandoraBox {

    private Context mContext;

    private PandoraData mData;

    private ViewGroup mEntireView;

    private ImageView mSingleImgView;

    private TextView mDescView;

    private boolean mIsRendered = false;

    public SingleImageBox(Context context, PandoraData data) {
        mData = data;
        mContext = context;
        mEntireView = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.pandora_box_single_image, null);
        mSingleImgView = (ImageView) mEntireView.findViewById(R.id.single_img);
        mDescView = (TextView) mEntireView.findViewById(R.id.desc);
//        mBoxContainer = (ViewGroup) mEntireView.findViewById(R.id.boxContainer);
    }

    @Override
    public int getCategory() {
        return IPandoraBox.CATEGORY_SINGLE_IMG;
    }

    @Override
    public PandoraData getData() {
        return mData;
    }

    @Override
    public View getContainer() {
        return mEntireView;
    }

    @Override
    public View getRenderedView() {
        if (mIsRendered) {
            return mEntireView;
        }
        boolean result = render();
        return result ? mEntireView : null;
    }

    private boolean render() {
        if (mData == null || mData.getmImage() == null) {
            return false;
        }
        final Bitmap bmp = mData.getmImage();
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        final float rate = width / height;
//        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams)mBoxContainer.getLayoutParams();
//        final boolean keepWidth = width >= height;
//        if (keepWidth) {
//            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        } else {
//            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
//        }
//        mBoxContainer.setLayoutParams(lp);

        mSingleImgView.setImageBitmap(mData.getmImage());

        mDescView.setText(mData.getmDesc());
        return true;
    }
}
