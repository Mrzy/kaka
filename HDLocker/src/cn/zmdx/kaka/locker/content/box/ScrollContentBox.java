package cn.zmdx.kaka.locker.content.box;

import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class ScrollContentBox implements IPandoraBox {

    private PandoraData mData;
    private Context mContext;
    public ScrollContentBox(PandoraData data) {
        mData = data;
        mContext = HDApplication.getInstannce();
    }

    @Override
    public int getCategory() {
        return CATEGORY_SCROLL_CONTENT;
    }

    @Override
    public PandoraData getData() {
        return mData;
    }

    @Override
    public View getContainer() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.pandora_box_scrollcontent, null);
        return null;
    }

    @Override
    public View getRenderedView() {
        // TODO Auto-generated method stub
        return null;
    }
}
