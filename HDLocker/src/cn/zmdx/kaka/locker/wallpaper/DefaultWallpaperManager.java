
package cn.zmdx.kaka.locker.wallpaper;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.wallpaper.PandoraWallpaperManager.IWallpaperClickListener;
import cn.zmdx.kaka.locker.wallpaper.PandoraWallpaperManager.PandoraWallpaper;

public class DefaultWallpaperManager {
    public static final String THEME_STRING_KEY_NO_NEED = "";

    private static DefaultWallpaperManager mInstance;

    public static DefaultWallpaperManager getInstance() {
        if (null == mInstance) {
            mInstance = new DefaultWallpaperManager();
        }
        return mInstance;
    }

    public List<PandoraWallpaper> setDefaultWallpaperList(Context mContext,
            ViewGroup mDefaultContainer, final IWallpaperClickListener listener,
            List<PandoraWallpaper> pWallpaperList) {
        List<Theme> mThemeList = ThemeManager.getAllTheme();
        Theme currentTheme = ThemeManager.getCurrentTheme();
        boolean isDefaultTmeme = currentTheme.isDefaultTheme();
        int currentThemeId = currentTheme.getmThemeId();
        for (int i = 0; i < mThemeList.size(); i++) {
            final int themeId = mThemeList.get(i).getmThemeId();
            final RelativeLayout mWallpaperRl = (RelativeLayout) LayoutInflater.from(
                    HDApplication.getContext()).inflate(R.layout.pandora_wallpaper_item, null);
            RelativeLayout mWallpaperIvRl = (RelativeLayout) mWallpaperRl
                    .findViewById(R.id.pandora_wallpaper_item_iamge_rl);
            ImageView mWallpaperIv = (ImageView) mWallpaperRl
                    .findViewById(R.id.pandora_wallpaper_item_iamge);
            ImageView mWallpaperSelect = (ImageView) mWallpaperRl
                    .findViewById(R.id.pandora_wallpaper_item_select);
            mWallpaperRl.findViewById(R.id.pandora_wallpaper_item_delete).setVisibility(View.GONE);
            mWallpaperIv.setTag(i);
            mWallpaperIv.setImageResource(mThemeList.get(i).getmThumbnailResId());
            mWallpaperIvRl.setBackgroundResource(R.drawable.setting_wallpaper_border_default);
            mWallpaperIv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (null != listener) {
                        listener.onDefaultClickListener(themeId);
                    }
                }
            });
            mDefaultContainer.addView(mWallpaperRl, mDefaultContainer.getChildCount());

            LayoutParams params = mWallpaperIvRl.getLayoutParams();
            int width = (int) mContext.getResources().getDimension(R.dimen.pandora_wallpaper_width);
            int height = (int) mContext.getResources().getDimension(
                    R.dimen.pandora_wallpaper_height);
            params.width = width;
            params.height = height;
            mWallpaperIvRl.setLayoutParams(params);

            LayoutParams layoutParams = mWallpaperRl.getLayoutParams();
            int layoutWidth = (int) mContext.getResources().getDimension(
                    R.dimen.pandora_wallpaper_layout_width);
            int layoutHeight = (int) mContext.getResources().getDimension(
                    R.dimen.pandora_wallpaper_layout_height);
            layoutParams.width = layoutWidth;
            layoutParams.height = layoutHeight;
            mWallpaperRl.setLayoutParams(layoutParams);

            PandoraWallpaper pWallpaper = new PandoraWallpaper();
            pWallpaper.setDefaultWallpaper(true);
            pWallpaper.setCurrentWallpaper(isDefaultTmeme && currentThemeId == themeId);
            pWallpaper.setImageView(mWallpaperSelect);
            pWallpaper.setImageIntKey(themeId);
            pWallpaper.setImageStringKeyName(THEME_STRING_KEY_NO_NEED);
            pWallpaperList.add(pWallpaper);
        }
        return pWallpaperList;
    }
}
