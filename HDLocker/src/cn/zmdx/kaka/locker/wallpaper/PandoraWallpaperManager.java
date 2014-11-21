
package cn.zmdx.kaka.locker.wallpaper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PandoraWallpaperManager {

    public interface IWallpaperClickListener {
        void onCustomClickListener(String fileName, Bitmap bitmap);

        void onDefaultClickListener(int themeId);

        void onDelClickListener(RelativeLayout layout, String fileName);
    }

    public static List<PandoraWallpaper> getWallpaperList(Context mContext,
            ViewGroup mDefaultContainer, ViewGroup mCustomContainer,
            IWallpaperClickListener listener) {
        List<PandoraWallpaper> pWallpaperList = new ArrayList<PandoraWallpaper>();
        Log.d("syc", "one  "+System.currentTimeMillis());
        CustomWallpaperManager.getInstance().setCustomWallpaperList(mContext, mCustomContainer,
                listener, pWallpaperList);
        Log.d("syc", "two  "+System.currentTimeMillis());
        DefaultWallpaperManager.getInstance().setDefaultWallpaperList(mContext, mDefaultContainer,
                listener, pWallpaperList);
        Log.d("syc", "three  "+System.currentTimeMillis());
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
