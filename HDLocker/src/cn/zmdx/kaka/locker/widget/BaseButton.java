package cn.zmdx.kaka.locker.widget;

import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.font.FontManager;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

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
        setTypeface(FontManager.getChineseTypeface(getContext()));
    }
}
