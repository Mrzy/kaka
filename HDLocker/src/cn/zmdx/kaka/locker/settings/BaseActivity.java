
package cn.zmdx.kaka.locker.settings;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;

/**
 * 基础Activity 所有子类需要调用initBackground()和initTitleHeight()这两个方法，以保证背景和当前壁纸一致并且不变形
 * 
 * @author syc
 */
public class BaseActivity extends Activity {

    @SuppressWarnings("deprecation")
    protected void initBackground(final View rootView) {
        getWindow().getAttributes().flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        Theme theme = ThemeManager.getCurrentTheme();
        rootView.setBackgroundDrawable(theme.getCurDrawable());
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
    }

    protected void initTitleHeight(View titleView) {
        int statusBarHeight = PandoraUtils.getStatusBarHeight(this);
        titleView.setPadding(0, statusBarHeight, 0, 0);
    }
}
