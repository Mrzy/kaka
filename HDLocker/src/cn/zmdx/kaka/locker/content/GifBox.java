
package cn.zmdx.kaka.locker.content;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;

public class GifBox implements IPandoraBox {
    private View mEntireView;

    private PandoraData mData;

    private ImageView mImageView;

    private TextView mTextView;

    private boolean mIsRendered = false;

    private boolean mIsHide = false;

    public GifBox(Context context, PandoraData data) {
        mData = data;
        mEntireView = LayoutInflater.from(context).inflate(R.layout.pandora_box_gif_image, null);
        mImageView = (ImageView) mEntireView.findViewById(R.id.gif_image);
        mTextView = (TextView) mEntireView.findViewById(R.id.gif_desc);
    }

    @Override
    public int getCategory() {

        return IPandoraBox.CATEGORY_GIF;
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
        mImageView.setImageBitmap(mData.getmImage());
        mTextView.setText(mData.getmContent());
        mImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mIsHide) {
                    mTextView.setVisibility(View.VISIBLE);
                } else {
                    mTextView.setVisibility(View.INVISIBLE);
                }
                mIsHide = !mIsHide;
            }
        });
        mIsRendered = true;
        return true;

    }

}
