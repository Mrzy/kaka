
package cn.zmdx.kaka.locker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

public class BaseLinearLayout extends LinearLayout {

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
        int padding = BaseInfoHelper.dip2px(getContext(), 14);
        setPadding(padding, 0, padding, 0);
        setBackgroundDrawable(getContext().getResources().getDrawable(
                R.drawable.setting_item_selector));
        setClickable(true);
    }
}
