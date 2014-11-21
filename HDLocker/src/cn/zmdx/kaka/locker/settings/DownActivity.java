
package cn.zmdx.kaka.locker.settings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout.PanelSlideListener;

public class DownActivity extends Activity {
    private SlidingUpPanelLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.down_activity);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d("syc", "onPanelSlide, offset " + slideOffset);
                // setActionBarTranslation(mLayout.getCurrentParalaxOffset());
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.d("syc", "onPanelExpanded");

            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.d("syc", "onPanelCollapsed");

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.d("syc", "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.d("syc", "onPanelHidden");
            }
        });
    }
}
