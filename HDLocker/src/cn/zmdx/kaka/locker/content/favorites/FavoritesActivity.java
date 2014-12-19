
package cn.zmdx.kaka.locker.content.favorites;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.content.box.FoldablePage;
import cn.zmdx.kaka.locker.content.box.IFoldablePage;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils.ILoadBitmapCallback;

public class FavoritesActivity extends FragmentActivity {

    private LinearLayout layout;

    private View mRootView;

    private FoldablePage mFoldablePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_favorites);
        getWindow().getAttributes().flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        initViewFavorites();
        initTitleHeight();
        initWallpaper();
    }

    private void initTitleHeight() {
        int statusBarHeight = PandoraUtils.getStatusBarHeight(this);
        LinearLayout titleLayout = (LinearLayout) mRootView
                .findViewById(R.id.pandora_favorite_title_layout);
        titleLayout.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initViewFavorites() {
        layout = (LinearLayout) this.findViewById(R.id.llPandoraPageCards);
        mRootView = this.findViewById(R.id.pandoraPageCards);

        PandoraBoxManager manager = PandoraBoxManager.newInstance(this);
        IFoldablePage foldablePage = manager.getFavoriteFoldablePage();
        mFoldablePage = (FoldablePage) foldablePage;
        mFoldablePage.setGuidePageVisibility(false);
        View renderedView = foldablePage.getRenderedView();
        mFoldablePage.setSwipeRefreshEnabled(false);
        if (null != renderedView) {
            layout.addView(renderedView);
        } else {
            TextView tv = (TextView) layout.findViewById(R.id.emptyView);
            tv.setText(R.string.pandora_favorite_state_nodata);
            tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mFoldablePage != null && mFoldablePage.isFoldBack()) {
            mFoldablePage.foldBack();
        } else {
            finish();
            overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                    R.anim.umeng_fb_slide_out_from_right);
        }
    }

    private void initWallpaper() {
        Theme theme = ThemeManager.getCurrentTheme();
        if (theme.isDefaultTheme()) {
            mRootView.setBackgroundResource(theme.getmBackgroundResId());
        } else {
            WallpaperUtils.loadBackgroundBitmap(FavoritesActivity.this, theme.getFilePath(),
                    new ILoadBitmapCallback() {

                        @SuppressWarnings("deprecation")
                        @Override
                        public void imageLoaded(Bitmap bitmap, String filePath) {
                            mRootView.setBackgroundDrawable(new BitmapDrawable(getResources(),
                                    bitmap));
                        }
                    });
        }
    }
    @Override
    protected void onDestroy() {
        mFoldablePage.onFinish();
        super.onDestroy();
    }
}
