
package cn.zmdx.kaka.locker.settings;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperManager;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperManager.OnlineWallpaper;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView;
import cn.zmdx.kaka.locker.widget.BaseButton;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout;
import cn.zmdx.kaka.locker.widget.SlidingUpPanelLayout.PanelSlideListener;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.ui.NetworkImageView;

@SuppressWarnings("deprecation")
public class OnlineWallpaperActivity extends Activity {

    private GridView mGridView;

    private WallpaperAdpter mWallpaperAdpter;

    private LinearLayout mBackground;

    private ImageView mPreview;

    private BaseButton mBaseButton;

    private ProgressBar mProgressBar;

    private ArrayList<OnlineWallpaper> list = new ArrayList<OnlineWallpaper>();

    private int mCurrentPostion;

    private String mCurrentFileName;

    private Bitmap mPreviewBitmap;

    private String[] urls = {
            "http://img3.cache.netease.com/photo/0001/2014-11-05/AA9DV3SV00AP0001.jpg",
            "http://img1.cache.netease.com/catchpic/7/7D/7DA5B6210B04DBE738F9C6F5874094DE.jpg",
            "http://img2.cache.netease.com/photo/0001/2014-11-08/AAHDP7J000AO0001.jpg",
            "http://img6.cache.netease.com/photo/0001/2014-11-08/AAHCGI1I00AO0001.jpg",
            "http://img3.cache.netease.com/photo/0001/2014-11-08/AAG5C7U119BR0001.jpg",
            "http://img5.cache.netease.com/photo/0001/2014-11-08/AAG5C5RE19BR0001.jpg",
            "http://img3.cache.netease.com/photo/0001/2014-11-08/AAG5BF5419BR0001.jpg",
            "http://img3.cache.netease.com/photo/0001/2014-11-08/AAG5BTMF19BR0001.jpg",
            "http://img2.cache.netease.com/photo/0001/2014-11-08/AAG5BDC519BR0001.jpg",
            "http://img2.cache.netease.com/photo/0001/2014-11-08/AAG5BU8A19BR0001.jpg",
            "http://img2.cache.netease.com/photo/0001/2014-11-08/AAG5BU8A19BR0001.jpg",
            "http://img2.cache.netease.com/photo/0001/2014-11-08/AAG5BU8A19BR0001.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.down_activity);
        for (int i = 0; i < urls.length; i++) {
            OnlineWallpaper onlineWallpaper = new OnlineWallpaper();
            onlineWallpaper.setUrl(urls[i]);
            list.add(onlineWallpaper);
        }
        SlidingUpPanelLayout mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d("syc", "onPanelSlide, offset " + slideOffset);
                // setActionBarTranslation(mLayout.getCurrentParalaxOffset());
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.d("syc", "onPanelExpanded");

            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.d("syc", "onPanelCollapsed");

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.d("syc", "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.d("syc", "onPanelHidden");
            }
        });
        mPreview = (ImageView) findViewById(R.id.pandora_online_wallpaper_preview_imageview);
        mBackground = (LinearLayout) findViewById(R.id.pandora_online_wallpaper_background);
        mProgressBar = (ProgressBar) findViewById(R.id.pandora_online_wallpaper_preview_progress);
        mBaseButton = (BaseButton) findViewById(R.id.pandora_online_wallpaper_preview_button);
        mBaseButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OnlineWallpaperManager.getInstance().saveThemeId(OnlineWallpaperActivity.this,
                        ThemeManager.THEME_ID_ONLINE);
                OnlineWallpaperManager.getInstance().saveOnlineWallpaperFileName(
                        OnlineWallpaperActivity.this, mCurrentFileName);
                Bitmap bitmap = PandoraUtils.getBitmap(OnlineWallpaperManager.getInstance()
                        .getOnlineWallpaperFilePath(mCurrentFileName));
                if (null != bitmap) {
                    mBackground.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                }
                // OnlineWallpaperManager.getInstance().downloadImage(
                // list.get(mCurrentPostion).getUrl(), mCurrentFileName, new
                // Listener<String>() {
                //
                // @Override
                // public void onResponse(String response) {
                // mProgressBar.setVisibility(View.GONE);
                // Bitmap bitmap = PandoraUtils.getBitmap(response);
                // if (null != bitmap) {
                // mBackground.setBackgroundDrawable(new BitmapDrawable(
                // getResources(), bitmap));
                // }
                // }
                // }, new ErrorListener() {
                //
                // @Override
                // public void onErrorResponse(VolleyError error) {
                // mProgressBar.setVisibility(View.GONE);
                // }
                // });
            }
        });

        mGridView = (GridView) findViewById(R.id.pandora_online_wallpaper_gridview);
        initWallpaper();
        mWallpaperAdpter = new WallpaperAdpter(mGridView);
        mGridView.setAdapter(mWallpaperAdpter);
    }

    private void initWallpaper() {
        Theme theme = ThemeManager.getCurrentTheme();
        if (theme.isDefaultTheme()) {
            mBackground.setBackgroundResource(theme.getmBackgroundResId());
            mPreview.setImageResource(theme.getmThumbnailResId());
        } else {
            BitmapDrawable drawable = theme.getmBitmap();
            if (null == drawable) {
                mBackground.setBackgroundResource(theme.getmBackgroundResId());
                mPreview.setImageResource(theme.getmThumbnailResId());
            } else {
                mBackground.setBackgroundDrawable(drawable);
                mPreview.setBackgroundDrawable(theme.getThumbBitmap());
            }

        }
    }

    class WallpaperAdpter extends BaseAdapter implements OnItemClickListener {// 上下文对象
        private ViewHolder viewHolder = null;

        private GridView mGridView;

        public WallpaperAdpter(GridView gridView) {
            mGridView = gridView;
            mGridView.setOnItemClickListener(this);
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
                convertView = LayoutInflater.from(OnlineWallpaperActivity.this).inflate(
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
            viewHolder.mImageView.setImageUrl(list.get(position).getUrl(), new ImageLoader(
                    RequestManager.getRequestQueue()));
            viewHolder.mImageView.setImageListener(new Listener<Bitmap>() {

                @Override
                public void onResponse(Bitmap response) {
                    list.get(position).setBitmap(response);
                }
            });

            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() == position) {
                    if (null != list.get(i).getSelectView()) {
                        list.get(i).getSelectView()
                                .setBackgroundResource(R.drawable.setting_wallpaper_border);
                        mCurrentPostion = i;
                        downloadImage();
                    }
                    // if (null != list.get(i).getBitmap()) {
                    // mPreview.setImageBitmap(list.get(i).getBitmap());
                    // }
                } else {
                    if (null != list.get(i).getSelectView()) {
                        list.get(i).getSelectView().setBackgroundResource(0);
                    }
                }
            }

        }

    }

    private void downloadImage() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCurrentFileName = PandoraUtils.getRandomString();
        OnlineWallpaperManager.getInstance().deleteFile(
                OnlineWallpaperManager.getInstance().getOnlineWallpaperFileName(
                        OnlineWallpaperActivity.this));
        OnlineWallpaperManager.getInstance().downloadImage(list.get(mCurrentPostion).getUrl(),
                mCurrentFileName, new Listener<String>() {

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
                            mPreviewBitmap = PandoraUtils.getAdaptBitmap(
                                    OnlineWallpaperActivity.this, response, realWidth, realHeight);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (null != mPreviewBitmap) {
                            mPreview.setImageDrawable(new BitmapDrawable(getResources(),
                                    mPreviewBitmap));
                        }
                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }
}
