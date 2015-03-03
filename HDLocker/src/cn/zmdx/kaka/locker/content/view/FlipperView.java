
package cn.zmdx.kaka.locker.content.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class FlipperView extends FrameLayout {

    public FlipperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlipperView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipperView(Context context) {
        this(context, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (getChildCount() != 2) {
            throw new IllegalStateException("暂时只支持2个view之间的切换");
        }
        init();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init() {
        if (!mOpened) {
            closeUpperView(false);
        }
    }

    private boolean mOpened = true;

    public void openUpperView(boolean smooth) {
        if (mOpened)
            return;
        View upperView = getChildAt(1);
        if (smooth) {
            upperView.setTranslationY(-upperView.getMeasuredHeight());
            upperView.animate().translationY(0).setDuration(300)
                    .setInterpolator(new DecelerateInterpolator()).start();
        } else {
            upperView.setTranslationY(0);
        }
        mOpened = true;
    }

    public void closeUpperView(boolean smooth) {
        if (!mOpened)
            return;
        View upperView = getChildAt(1);
        if (smooth) {
            upperView.setTranslationY(0);
            upperView.animate().translationY(-upperView.getMeasuredHeight())
                    .setInterpolator(new AccelerateInterpolator()).setDuration(300).start();
        } else {
            upperView.setTranslationY(-upperView.getMeasuredHeight());
        }
        mOpened = false;
    }
}
