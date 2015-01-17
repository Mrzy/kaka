
package cn.zmdx.kaka.fast.locker.wallpaper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
        CustomWallpaperManager.getInstance().setCustomWallpaperList(mContext, mCustomContainer,
                listener, pWallpaperList);
        OnlineWallpaperManager.getInstance().setOnlineWallpaperList(mContext, mOnlineContainer,
                listener, pWallpaperList);
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
