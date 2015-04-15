
package cn.zmdx.kaka.locker.content.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.widget.FloatingActionButton;

public class HeaderCircleButton extends FloatingActionButton {

    public HeaderCircleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HeaderCircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderCircleButton(Context context) {
        super(context);
    }

    @Override
    protected Drawable createBackgroundDrawable() {
        Drawable d = createDrawable(Color.parseColor("#30ffffff"));
        Drawable d1 = createDrawable(Color.parseColor("#60ffffff"));
        Drawable[] drawables = new Drawable[] {
                d, d1
        };
        TransitionDrawable td = new TransitionDrawable(drawables);
        return td;
    }

    public void startTransitionDrawable(int duration) {
        final int time = duration / 3;
        Drawable bg = getBackground();
        if (bg instanceof TransitionDrawable) {
            final TransitionDrawable td = (TransitionDrawable) bg;
            td.startTransition(time * 2);
            HDBThreadUtils.postOnUiDelayed(new Runnable() {
                @Override
                public void run() {
                    td.reverseTransition(time);
                }
            }, time * 2);
        }
    }
}
