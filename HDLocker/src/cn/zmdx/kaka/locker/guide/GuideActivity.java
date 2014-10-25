
package cn.zmdx.kaka.locker.guide;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import cn.zmdx.kaka.locker.R;

public class GuideActivity extends BaseGuideActivity {

    private Button btn;

    @Override
    protected View getContentView() {
        View view = LayoutInflater.from(this).inflate(R.layout.pandora_guide, null);
        btn = (Button) view.findViewById(R.id.go_setting);
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
