
package cn.zmdx.kaka.locker.content.box;

import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DefaultBox implements IPandoraBox {
    private Context mContext;

    private ViewGroup mLayoutView;

    private ImageView mImageView;

    private TextView mTextView1;

    private TextView mTextView2;

    private PandoraData mData;

    public DefaultBox(Context context, PandoraData data) {
        mContext = context;
        mData = data;
        mLayoutView = (ViewGroup) LayoutInflater.from(context).inflate(
                R.layout.pandora_box_nodata_show, null);
        mImageView = (ImageView) mLayoutView.findViewById(R.id.pandora_box_nodata_show_imageview);
        mTextView1 = (TextView) mLayoutView.findViewById(R.id.pandora_box_nodata_show_textview);
        mTextView2 = (TextView) mLayoutView.findViewById(R.id.pandora_box_nodata_show_tip);
        if (!HDBNetworkState.isNetworkAvailable()) {
            mTextView2.setText(mContext.getText(R.string.pandora_box_no_net_tip));
        }
    }

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
