
package cn.zmdx.kaka.locker.wallpaper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PandoraWallpaperManager {

    public interface IWallpaperClickListener {
        void onClickListener(String fileName, String filePath, boolean isCustom);

        void onDelClickListener(String fileName, String filePath);
    }

    public static List<PandoraWallpaper> getWallpaperList(Context mContext,
            ViewGroup mOnlineContainer, ViewGroup mCustomContainer, IWallpaperClickListener listener) {
        List<PandoraWallpaper> pWallpaperList = new ArrayList<PandoraWallpaper>();
        Log.d("syc", "one    " + System.currentTimeMillis());
        CustomWallpaperManager.getInstance().setCustomWallpaperList(mContext, mCustomContainer,
                listener, pWallpaperList);
        Log.d("syc", "two  " + System.currentTimeMillis());
        OnlineWallpaperManager.getInstance().setOnlineWallpaperList(mContext, mOnlineContainer,
                listener, pWallpaperList);
        Log.d("syc", "three  " + System.currentTimeMillis());
        return pWallpaperList;
    }

    public static final class PandoraWallpaper {
        private ImageView mImageView;

        private String mFileName;

        private boolean isCurrentWallpaper;

        public ImageView getImageView() {
            return mImageView;
        }

        public void setImageView(ImageView mImageView) {
            this.mImageView = mImageView;
        }

        public String getFileName() {
            return mFileName;
        }

        public void setFileName(String fileName) {
            this.mFileName = fileName;
        }

        public boolean isCurrentWallpaper() {
            return isCurrentWallpaper;
        }

        public void setCurrentWallpaper(boolean isCurrentWallpaper) {
            this.isCurrentWallpaper = isCurrentWallpaper;
        }

    }
}
