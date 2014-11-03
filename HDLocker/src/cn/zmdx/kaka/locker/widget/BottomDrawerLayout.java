
package cn.zmdx.kaka.locker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class BottomDrawerLayout extends FrameLayout {

    public BottomDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BottomDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomDrawerLayout(Context context) {
        this(context, null);
    }

    private void init() {

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        if (childCount != 2) {
            throw new RuntimeException("the child must be 2");
        }
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
        }
        super.onLayout(changed, left, top, right, bottom);
    }
}
