
package cn.zmdx.kaka.fast.locker.guide;

import android.view.LayoutInflater;
import android.view.View;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.widget.RippleView;
import cn.zmdx.kaka.fast.locker.widget.TypefaceTextView;

public class GuideActivity extends BaseGuideActivity {

    private RippleView btn;

    private TypefaceTextView mTextView;

    @Override
    protected View getContentView() {
        View view = LayoutInflater.from(this).inflate(R.layout.pandora_guide, null);
        btn = (RippleView) view.findViewById(R.id.go_setting);
        mTextView = (TypefaceTextView) view.findViewById(R.id.go_setting_prompt);
        return view;
    }

    @Override
    protected RippleView getNextRippleView() {
        return btn;
    }

    @Override
    protected TypefaceTextView getNextTypefaceTextView() {
        return mTextView;
    }

    @Override
    protected void onFinish() {

    }

    @Override
    public void onBackPressed() {
        return;
    }

}
