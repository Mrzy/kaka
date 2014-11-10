
package cn.zmdx.kaka.locker.custom.wallpaper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.custom.wallpaper.CustomWallpaperManager.CustomWallpaper;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;

public class PandoraWallpaperManager {

    public static final int THEME_INT_KEY_NO_NEED = -100;

    public static final String THEME_STRING_KEY_NO_NEED = "";

    public interface IWallpaperClickListener {
        void onCustomClickListener(String fileName, Bitmap bitmap);

        void onDefaultClickListener(int themeId);

        void onDelClickListener(RelativeLayout layout, String fileName);
    }

    public static List<PandoraWallpaper> getWallpaperList(Context mContext,
            ViewGroup mDefaultContainer, ViewGroup mCustomContainer,
            IWallpaperClickListener listener) {
        List<PandoraWallpaper> pWallpaperList = new ArrayList<PandoraWallpaper>();
        setCustomWallpaperList(mContext, mCustomContainer, listener, pWallpaperList);
        setDefaultWallpaperList(mContext, mDefaultContainer, listener, pWallpaperList);
        return pWallpaperList;
    }

    public static void setCustomWallpaperList(Context mContext, ViewGroup mCustomContainer,
            final IWallpaperClickListener listener, List<PandoraWallpaper> pWallpaperList) {
        if (CustomWallpaperManager.isHaveCustomThumbWallpaper()) {
            final String currentFileName = PandoraConfig.newInstance(mContext)
                    .getCustomWallpaperFileName();
            List<CustomWallpaper> wallpaperList = CustomWallpaperManager.getCustomThumbWallpaper();
            for (int i = 0; i < wallpaperList.size(); i++) {
                final Bitmap bitmap = PandoraUtils.getBitmap(wallpaperList.get(i).getFilePath());
                final String fileName = wallpaperList.get(i).getFileName();
                setCustomWallpaperItem(mContext, mCustomContainer, bitmap, fileName,
                        currentFileName, listener, pWallpaperList);
            }
        }
    }

    public static void setCustomWallpaperItem(Context mContext, ViewGroup mCustomContainer,
            final Bitmap bitmap, final String fileName, final String currentFileName,
            final IWallpaperClickListener listener, List<PandoraWallpaper> pWallpaperList) {
        final RelativeLayout mWallpaperRl = (RelativeLayout) LayoutInflater.from(
                HDApplication.getContext()).inflate(R.layout.pandora_wallpaper_item, null);
        RelativeLayout mWallpaperIvRl = (RelativeLayout) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_iamge_rl);
        ImageView mWallpaperIv = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_iamge);
        ImageView mWallpaperSelect = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_select);
        final ImageView mWallpaperDel = (ImageView) mWallpaperRl
                .findViewById(R.id.pandora_wallpaper_item_delete);
        mWallpaperIv.setImageBitmap(bitmap);
        mWallpaperIv.setBackgroundResource(R.drawable.setting_wallpaper_border_default);
        mWallpaperIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onCustomClickListener(fileName, bitmap);
                }
            }
        });
        mWallpaperDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (null != listener) {
                    listener.onDelClickListener(mWallpaperRl, fileName);
                }
            }
        });
        mCustomContainer.addView(mWallpaperRl, Math.min(1, mCustomContainer.getChildCount()));
        LayoutParams params = mWallpaperIvRl.getLayoutParams();
        int width = (int) mContext.getResources().getDimension(R.dimen.pandora_wallpaper_width);
        int height = (int) mContext.getResources().getDimension(R.dimen.pandora_wallpaper_height);
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
        pWallpaper.setDefaultWallpaper(false);
        pWallpaper.setCurrentWallpaper(currentFileName.equals(fileName));
        pWallpaper.setImageView(mWallpaperSelect);
        pWallpaper.setImageStringKeyName(fileName);
        pWallpaper.setImageIntKey(THEME_INT_KEY_NO_NEED);
        pWallpaperList.add(pWallpaper);

    }

    private static List<PandoraWallpaper> setDefaultWallpaperList(Context mContext,
            ViewGroup mDefaultContainer, final IWallpaperClickListener listener,
            List<PandoraWallpaper> pWallpaperList) {
        List<Theme> mThemeList = ThemeManager.getAllTheme();
        Theme currentTheme = ThemeManager.getCurrentTheme();
        boolean isCustomWallpaper = currentTheme.isCustomWallpaper();
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
            mWallpaperIv.setBackgroundResource(R.drawable.setting_wallpaper_border_default);
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
            pWallpaper.setCurrentWallpaper(!isCustomWallpaper && currentThemeId == themeId);
            pWallpaper.setImageView(mWallpaperSelect);
            pWallpaper.setImageIntKey(themeId);
            pWallpaper.setImageStringKeyName(THEME_STRING_KEY_NO_NEED);
            pWallpaperList.add(pWallpaper);
        }
        return pWallpaperList;
    }

    public static final class PandoraWallpaper {
        private ImageView mImageView;

        private String mImageStringKeyName;

        private int mImageIntKey;

        private boolean isDefaultWallpaper;

        private boolean isCurrentWallpaper;

        public ImageView getImageView() {
            return mImageView;
        }

        public void setImageView(ImageView mImageView) {
            this.mImageView = mImageView;
        }

        public String getImageStringKeyName() {
            return mImageStringKeyName;
        }

        public void setImageStringKeyName(String mImageStringKeyName) {
            this.mImageStringKeyName = mImageStringKeyName;
        }

        public int getImageIntKey() {
            return mImageIntKey;
        }

        public void setImageIntKey(int mImageIntKey) {
            this.mImageIntKey = mImageIntKey;
        }

        public boolean isDefaultWallpaper() {
            return isDefaultWallpaper;
        }

        public void setDefaultWallpaper(boolean isDefaultWallpaper) {
            this.isDefaultWallpaper = isDefaultWallpaper;
        }

        public boolean isCurrentWallpaper() {
            return isCurrentWallpaper;
        }

        public void setCurrentWallpaper(boolean isCurrentWallpaper) {
            this.isCurrentWallpaper = isCurrentWallpaper;
        }

    }
}
