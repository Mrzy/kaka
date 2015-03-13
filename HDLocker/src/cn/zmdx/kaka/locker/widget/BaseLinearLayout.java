
package cn.zmdx.kaka.locker.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;

public class BaseLinearLayout extends CardView {

    public BaseLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BaseLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseLinearLayout(Context context) {
        this(context, null);
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        setCardBackgroundColor(getContext().getResources().getColor(android.R.color.white));
        setRadius(5);
        setCardElevation(2);
        setMaxCardElevation(2);
//        setBackgroundDrawable(getContext().getResources().getDrawable(
//                R.drawable.password_item_background));
        setClickable(true);
    }
}
