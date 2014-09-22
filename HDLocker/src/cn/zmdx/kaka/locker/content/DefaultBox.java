
package cn.zmdx.kaka.locker.content;

import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.IPandoraBox.PandoraData;
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


    public DefaultBox(Context context) {
        mContext = context;
        mLayoutView = (ViewGroup) LayoutInflater.from(context).inflate(
                R.layout.pandora_box_nodata_show, null);
        mImageView = (ImageView) mLayoutView.findViewById(R.id.pandora_box_nodata_show_imageview);
        mTextView1 = (TextView) mLayoutView.findViewById(R.id.pandora_box_nodata_show_textview);
        mTextView2 = (TextView) mLayoutView.findViewById(R.id.pandora_box_nodata_show_textview1);
    }

    @Override
    public int getCategory() {
        return IPandoraBox.CATEGORY_DEFAULT;
    }

    @Override
    public PandoraData getData() {
        // TODO Auto-generated method stub
        return null;
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
