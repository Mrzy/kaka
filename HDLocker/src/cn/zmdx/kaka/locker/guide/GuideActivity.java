
package cn.zmdx.kaka.locker.guide;

import android.view.View;
import android.widget.Button;

public class GuideActivity extends BaseGuideActivity {

    private Button btn;

    @Override
    protected View getContentView() {
        //TODO
        btn = new Button(this);
        btn.setText("前往初始设置");
        return btn;
    }

    @Override
    protected Button getNextButton() {
        //TODO
        return btn;
    }

    @Override
    protected void onFinish() {

    }
}
