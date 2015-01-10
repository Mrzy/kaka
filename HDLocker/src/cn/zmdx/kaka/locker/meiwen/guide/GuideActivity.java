
package cn.zmdx.kaka.locker.meiwen.guide;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import cn.zmdx.kaka.locker.meiwen.Res;

public class GuideActivity extends BaseGuideActivity {

    private Button btn;

    @Override
    protected View getContentView() {
        View view = LayoutInflater.from(this).inflate(Res.layout.pandora_guide, null);
        View rootView = view.findViewById(Res.id.guide_lock_top);
        initBackground(rootView);
        btn = (Button) view.findViewById(Res.id.go_setting);
        btn.setBackgroundResource(Res.drawable.guide_button_background_selector);
        return view;
    }

    @Override
    protected Button getNextButton() {
        return btn;
    }

    @Override
    protected void onFinish() {

    }

    @Override
    public void onBackPressed() {
        return;
    }
}
