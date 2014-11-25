
package cn.zmdx.kaka.locker.wallpaper;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import cn.zmdx.kaka.locker.ImageLoaderManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils.ILoadBitmapCallback;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperManager.OnlineWallpaper;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.ui.NetworkImageView;

@SuppressLint("InflateParams")
public class OnlineWallpaperView extends LinearLayout {
    private Context mContext;

    private View mRootView;

    private GridView mGridView;

    private WallpaperAdpter mWallpaperAdpter;

    private ImageView mPreview;

    private Button mBaseButton;

    private ProgressBar mProgressBar;

    private ArrayList<OnlineWallpaper> list = new ArrayList<OnlineWallpaper>();

    private OnlineWallpaper mCurrentItem;

    private Bitmap mPreviewBitmap;

    private TypefaceTextView mWeatherView;

    private TypefaceTextView mDateView;

    private IOnlineWallpaper mListener;

    private Theme mCurTheme;

    private String[] urls = {
            "http://cos.myqcloud.com/11000436/bucket_1/image/1.jpg",
            "http://cos.myqcloud.com/11000436/bucket_1/image/2.jpg",
            "http://cos.myqcloud.com/11000436/bucket_1/image/3.jpg",
            "http://cos.myqcloud.com/11000436/bucket_1/image/4.jpg",
            "http://cos.myqcloud.com/11000436/bucket_1/image/5.jpg",
            "http://cos.myqcloud.com/11000436/bucket_1/image/6.jpg",
            "http://cos.myqcloud.com/11000436/bucket_1/image/7.jpg",
            "http://cos.myqcloud.com/11000436/bucket_1/image/8.jpg",
            "http://cos.myqcloud.com/11000436/bucket_1/image/9.jpg",
            "http://cos.myqcloud.com/11000436/bucket_1/image/10.jpg",
    };

    public interface IOnlineWallpaper {
        void setWallpaper(Bitmap bitmap);
    }

    public void setOnWallpaperListener(IOnlineWallpaper listener) {
        mListener = listener;
    }

    public OnlineWallpaperView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public OnlineWallpaperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public OnlineWallpaperView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public void init() {
        for (int i = 0; i < urls.length; i++) {
            OnlineWallpaper onlineWallpaper = new OnlineWallpaper();
            onlineWallpaper.setUrl(urls[i]);
            onlineWallpaper.setFileName(PandoraUtils.getStringMD5(urls[i].substring(
                    urls[i].lastIndexOf("/") + 1, urls[i].lastIndexOf("."))));
            onlineWallpaper.setExt(".jpg");
            list.add(onlineWallpaper);
        }
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.pandora_online_wallpaper, null);
        addView(mRootView);
        mPreview = (ImageView) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_imageview);
        mProgressBar = (ProgressBar) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_progress);
        mBaseButton = (Button) mRootView.findViewById(R.id.pandora_online_wallpaper_preview_button);
        mBaseButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // if (mPreviewBitmap == null || mCurrentItem == null) {
                // return;
                // }
                OnlineWallpaperManager.getInstance().saveThemeId(mContext,
                        ThemeManager.THEME_ID_ONLINE);
                OnlineWallpaperManager.getInstance().saveCurrentWallpaperFileName(mContext,
                        mCurrentItem.getFileName());
                OnlineWallpaperManager.getInstance().renameFile(mCurrentItem.getFileName());
                Bitmap bitmap = PandoraUtils.getBitmap(OnlineWallpaperManager.getInstance()
                        .getFilePath(mCurrentItem.getFileName()));
                if (null != bitmap) {
                    mListener.setWallpaper(bitmap);
                }
            }
        });

        OnlineWallpaperManager.getInstance().mkDirs();

        mWeatherView = (TypefaceTextView) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_weather);
        mDateView = (TypefaceTextView) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_date);

        mGridView = (GridView) mRootView.findViewById(R.id.pandora_online_wallpaper_gridview);
        mWallpaperAdpter = new WallpaperAdpter(mGridView);
        mGridView.setAdapter(mWallpaperAdpter);
    }

    private void initPreview() {
        if (null != mCurTheme) {
            if (mCurTheme.isDefaultTheme()) {
                mPreview.setImageResource(mCurTheme.getmBackgroundResId());
            } else {
                PandoraUtils.loadBitmap(mContext, mCurTheme.getFilePath(),
                        new ILoadBitmapCallback() {

                            @Override
                            public void imageLoaded(Bitmap bitmap, String filePath) {
                                mPreview.setImageBitmap(bitmap);
                            }
                        });
            }
        } else {
            mPreview.setImageResource(R.drawable.online_wallpaper_default);
        }
    }

    class WallpaperAdpter extends BaseAdapter {// 上下文对象
        private ViewHolder viewHolder = null;

        public WallpaperAdpter(GridView gridView) {
            mGridView = gridView;
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        private class ViewHolder {
            private ImageView mImageViewRl;

            private NetworkImageView mImageView;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.pandora_online_wallpaper_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mImageViewRl = (ImageView) convertView
                        .findViewById(R.id.pandora_online_wallpaper_item_rl);
                viewHolder.mImageView = (NetworkImageView) convertView
                        .findViewById(R.id.pandora_online_wallpaper_item_imageview);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (null == list.get(position).getSelectView()) {
                list.get(position).setId(position);
                list.get(position).setSelectView(viewHolder.mImageViewRl);
            }

            viewHolder.mImageView.setImageUrl(list.get(position).getUrl(), ImageLoaderManager.getImageLoader());
            viewHolder.mImageView.setDefaultImageResId(R.drawable.online_wallpaper_default);
            viewHolder.mImageView.setImageListener(new Listener<Bitmap>() {

                @Override
                public void onResponse(Bitmap response) {
                    list.get(position).setBitmap(response);
                }
            });
            viewHolder.mImageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getId() == position) {
                            if (null != list.get(i).getSelectView()) {
                                list.get(i).getSelectView()
                                        .setBackgroundResource(R.drawable.setting_wallpaper_border);
                                mCurrentItem = list.get(position);
                                downloadImage();
                            }
                        } else {
                            if (null != list.get(i).getSelectView()) {
                                list.get(i).getSelectView().setBackgroundResource(0);
                            }
                        }
                    }

                }
            });

            return convertView;
        }
    }

    private void downloadImage() {
        mProgressBar.setVisibility(View.VISIBLE);
        OnlineWallpaperManager.getInstance().clearTmpFolderFile();
        OnlineWallpaperManager.getInstance().downloadImage(mCurrentItem.getUrl(),
                mCurrentItem.getFileName(), new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        mProgressBar.setVisibility(View.GONE);
                        if (null != mPreviewBitmap && !mPreviewBitmap.isRecycled()) {
                            mPreviewBitmap.recycle();
                            System.gc();
                        }
                        int realWidth = (int) getResources().getDimension(
                                R.dimen.pandora_online_wallpaper_preview_imageview_width);
                        int realHeight = (int) getResources().getDimension(
                                R.dimen.pandora_online_wallpaper_preview_imageview_height);
                        try {
                            mPreviewBitmap = PandoraUtils.getAdaptBitmap(response, realWidth,
                                    realHeight);
                            if (null != mPreviewBitmap) {
                                mPreview.setImageDrawable(new BitmapDrawable(getResources(),
                                        mPreviewBitmap));
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void setDate(String dateString) {
        if (null != mDateView) {
            mDateView.setText(dateString);
        }

    }

    public void setDateAppend(String appendString) {
        if (null != mDateView) {
            mDateView.append(appendString);
        }
    }

    public void setWeatherString(String weatherString) {
        if (null != mWeatherView) {
            mWeatherView.setText(weatherString);
        }
    }

    public void setTheme(Theme curTheme) {
        mCurTheme = curTheme;
        initPreview();
    }
}
