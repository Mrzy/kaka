
package cn.zmdx.kaka.locker.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import cn.zmdx.kaka.locker.R;

public class FAQActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.action_bar_bg));
        setContentView(R.layout.activity_faq);
    }

}
