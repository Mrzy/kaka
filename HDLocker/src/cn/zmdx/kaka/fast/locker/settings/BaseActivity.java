
package cn.zmdx.kaka.fast.locker.settings;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.fast.locker.theme.ThemeManager;
import cn.zmdx.kaka.fast.locker.theme.ThemeManager.Theme;

/**
 * 
 * @author syc
 */
public class BaseActivity extends ActionBarActivity {

    @SuppressLint("InlinedApi")
    @SuppressWarnings("deprecation")
    protected void initBackground(final View rootView) {
//        getWindow().getAttributes().flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//        Theme theme = ThemeManager.getCurrentTheme();
//        rootView.setBackgroundDrawable(theme.getCurDrawable());
        // if (theme.isDefaultTheme()) {
        // rootView.setBackgroundResource(theme.getmBackgroundResId());
        // } else {
        // WallpaperUtils.loadBackgroundBitmap(this, theme.getFilePath(),
        // new ILoadBitmapCallback() {
        //
        // @SuppressWarnings("deprecation")
        // @Override
        // public void imageLoaded(Bitmap bitmap, String filePath) {
        // rootView.setBackgroundDrawable(new BitmapDrawable(getResources(),
        // bitmap));
        // }
        // });
        // }
        if (Build.VERSION.SDK_INT >= 19) {
            Window window = getWindow();
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    protected void initTitleHeight(View titleView) {
        int statusBarHeight = PandoraUtils.getStatusBarHeight(this);
        titleView.setPadding(0, statusBarHeight, 0, 0);
    }
}
