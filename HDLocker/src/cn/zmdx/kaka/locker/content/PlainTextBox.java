
package cn.zmdx.kaka.locker.content;

import cn.zmdx.kaka.locker.R;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class PlainTextBox implements IPandoraBox {

    private View mEntireView;

    private TextView mTextView1, mTextView2;

    private PandoraData mData;

    private boolean mIsRendered = false;

    public PlainTextBox(Context context, PandoraData data) {
        mData = data;
        mEntireView = LayoutInflater.from(context).inflate(R.layout.pandora_box_plain_text, null);
        mTextView1 = (TextView) mEntireView.findViewById(R.id.text1);
        mTextView2 = (TextView) mEntireView.findViewById(R.id.text2);
    }

    @Override
    public int getCategory() {
        return IPandoraBox.CATEGORY_PLAIN_TEXT;
    }

    @Override
    public View getContainer() {
        return mEntireView;
    }

    @Override
    public PandoraData getData() {
        return mData;
    }

    /**
     * 如果数据为空，返回null，否则返回已渲染内容的view对象
     */
    @Override
    public View getRenderedView() {
        if (mIsRendered) {
            return mEntireView;
        }
        boolean result = render();
        return result ? mEntireView : null;
    }

    private boolean render() {
        final String title = mData.getmTitle();
        final String content = mData.getmContent();
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            return false;
        }

        if (!TextUtils.isEmpty(title)) {
            mTextView1.setText(title);
        } else {
            mTextView1.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(content)) {
            mTextView2.setText(content);
        } else {
            mTextView2.setVisibility(View.GONE);
        }
        mIsRendered = true;
        return true;
    }
}
