
package cn.zmdx.kaka.locker.content;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

public class SingleImageBox implements IPandoraBox {

    private Context mContext;

    private PandoraData mData;

    private ImageView mEntireView;

    private boolean mIsRendered = false;

    public SingleImageBox(Context context, PandoraData data) {
        mData = data;
        mContext = context;
        mEntireView = new ImageView(mContext);
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
        mEntireView.setImageBitmap(mData.getmImage());
        return true;
    }
}
