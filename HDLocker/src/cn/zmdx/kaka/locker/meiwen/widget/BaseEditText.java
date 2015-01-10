
package cn.zmdx.kaka.locker.meiwen.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.font.FontManager;

public class BaseEditText extends EditText {

    public BaseEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BaseEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseEditText(Context context) {
        this(context, null);
    }

    private void init() {
        setBackgroundResource(Res.drawable.individualization_edittext_divider_line);
        Typeface typeface = FontManager.getCurrentTypeface(getContext());
        if (null != typeface) {
            setTypeface(typeface);
        }
    }
}
