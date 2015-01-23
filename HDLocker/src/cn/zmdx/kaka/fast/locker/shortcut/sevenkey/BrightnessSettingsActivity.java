
package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class BrightnessSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hanldeToggleBrightness(this);
        finishActivity();
    }

    /**
     * 处理点击了亮度按扭
     * 
     * @param activity
     */
    public static void hanldeToggleBrightness(Activity activity) {
        if (activity == null) {
            return;
        }
        BrightnessSettings settings = new BrightnessSettings(activity);
        final int brightness = settings.toggleBrightness(activity);

        setWindowBrightness(activity, brightness);

        BrightnessStateTracker.updateWidget(activity);
    }

    private static void setWindowBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = brightness / 255f;
        activity.getWindow().setAttributes(lp);
    }

    private void finishActivity() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                BrightnessSettingsActivity.this.finish();
            }
        }, 500);
    }

}
