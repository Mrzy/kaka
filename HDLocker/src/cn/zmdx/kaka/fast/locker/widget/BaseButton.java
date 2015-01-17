
package cn.zmdx.kaka.fast.locker.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import cn.zmdx.kaka.fast.locker.font.FontManager;
import cn.zmdx.kaka.fast.locker.R;

public class BaseButton extends Button {

    public BaseButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BaseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseButton(Context context) {
        this(context, null);
    }

    private void init() {
        setBackgroundResource(R.drawable.base_button_selector);
        setTextColor(Color.parseColor("#ffffff"));
        Typeface typeface = FontManager.getCurrentTypeface(getContext());
        if (null != typeface) {
            setTypeface(typeface);
        }
    }
}
