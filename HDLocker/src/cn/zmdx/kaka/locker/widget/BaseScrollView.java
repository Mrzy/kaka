
package cn.zmdx.kaka.locker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class BaseScrollView extends ScrollView {

    private IScrollListener mListener;

    public interface IScrollListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    public BaseScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BaseScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseScrollView(Context context) {
        this(context, null);
    }

    public void setOnScrollListener(IScrollListener listener) {
        mListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mListener != null) {
            mListener.onScrollChanged(l, t, oldl, oldt);
        }
    }
    public boolean isAtTop() {
        return getScaleY() <= 0;
    }

    public boolean isAtBottom() {
        return getScrollY() == getChildAt(0).getBottom() + getPaddingBottom() - getHeight();
    }
}
