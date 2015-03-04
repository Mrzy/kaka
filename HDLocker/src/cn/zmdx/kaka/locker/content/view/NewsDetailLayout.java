
package cn.zmdx.kaka.locker.content.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class NewsDetailLayout extends FrameLayout {

    public NewsDetailLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NewsDetailLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsDetailLayout(Context context) {
        this(context, null);
    }

    private void init() {
        setVisibility(View.INVISIBLE);
    }

    public void addDetailView(View view) {
        if (view == null) {
            throw new NullPointerException("view must not be null");
        }
        removeAllViews();
        addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void open() {
        if (getChildCount() == 0) {
            throw new IllegalStateException("请先调用addDetailView(view)方法设置详细页的view");
        }
        bringToFront();
        setVisibility(View.VISIBLE);
        // TODO 执行出场动画
    }

    public void close() {
        setVisibility(View.INVISIBLE);
        removeAllViews();
    }
}
