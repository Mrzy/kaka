
package cn.zmdx.kaka.locker.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout.LayoutParams;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.wallpaper.WallpaperDetailView;
import cn.zmdx.kaka.locker.wallpaper.WallpaperDetailView.IWallpaperDetailListener;

import com.umeng.analytics.MobclickAgent;

public class WallpaperDetailActivity extends Activity {

    private String mImageUrl;

    private String mDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mImageUrl = getIntent().getStringExtra("imageUrl");
        mDesc = getIntent().getStringExtra("desc");
        WallpaperDetailView detailView = new WallpaperDetailView(this, null);
        detailView.setData(mImageUrl, mDesc);
        detailView.setWallpaperDetailListener(new IWallpaperDetailListener() {

            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onApplyWallpaper() {
                LockScreenManager.getInstance().lock();
                LockScreenManager.getInstance().startShimmer();
                onBackPressed();
            }
        });
        setContentView(detailView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WallpaperDetailActivity");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WallpaperDetailActivity");
        MobclickAgent.onPause(this);
    }
}
