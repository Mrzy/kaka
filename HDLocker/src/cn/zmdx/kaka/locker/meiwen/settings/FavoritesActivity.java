
package cn.zmdx.kaka.locker.meiwen.settings;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.meiwen.content.box.FoldablePage;
import cn.zmdx.kaka.locker.meiwen.content.box.IFoldablePage;

public class FavoritesActivity extends BaseActivity {

    private LinearLayout layout;

    private View mRootView;

    private FoldablePage mFoldablePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(Res.layout.activity_favorites);
        initViewFavorites();
    }

    private void initViewFavorites() {
        layout = (LinearLayout) findViewById(Res.id.llPandoraPageCards);
        mRootView = findViewById(Res.id.pandoraPageCards);
        LinearLayout titleView = (LinearLayout) findViewById(Res.id.pandora_favorite_title_layout);
        initBackground(mRootView);
        initTitleHeight(titleView);
        PandoraBoxManager manager = PandoraBoxManager.newInstance(this);
        IFoldablePage foldablePage = manager.getFavoriteFoldablePage();
        mFoldablePage = (FoldablePage) foldablePage;
        mFoldablePage.setGuidePageVisibility(false);
        View renderedView = foldablePage.getRenderedView();
        mFoldablePage.setSwipeRefreshEnabled(false);
        if (null != renderedView) {
            layout.addView(renderedView);
        } else {
            TextView tv = (TextView) layout.findViewById(Res.id.emptyView);
            tv.setText(Res.string.pandora_favorite_state_nodata);
            tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mFoldablePage != null && mFoldablePage.isFoldBack()) {
            mFoldablePage.foldBack();
        } else {
            finish();
            overridePendingTransition(Res.anim.umeng_fb_slide_in_from_left,
                    Res.anim.umeng_fb_slide_out_from_right);
        }
    }

    @Override
    protected void onDestroy() {
        mFoldablePage.onFinish();
        super.onDestroy();
    }
}
