
package cn.zmdx.kaka.locker.wallpaper;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperManager.OnlineWallpaper;
import cn.zmdx.kaka.locker.widget.BaseButton;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.ui.NetworkImageView;

public class OnlineWallpaperView extends LinearLayout {
    private Context mContext;

    private View mRootView;

    private GridView mGridView;

    private WallpaperAdpter mWallpaperAdpter;

    private LinearLayout mBackground;

    private ImageView mPreview;

    private BaseButton mBaseButton;

    private ProgressBar mProgressBar;

    private ArrayList<OnlineWallpaper> list = new ArrayList<OnlineWallpaper>();

    private int mCurrentPostion;

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
            "http://img2.cache.netease.com/photo/0001/2014-11-08/AAG5BU8A19BR0001.jpg"
    };

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
        for (int i = 0; i < 10; i++) {
            OnlineWallpaper onlineWallpaper = new OnlineWallpaper();
            onlineWallpaper.setUrl(urls[i]);
            list.add(onlineWallpaper);
        }
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.pandora_online_wallpaper, null);
        mPreview = (ImageView) mRootView.findViewById(R.id.pandora_online_wallpaper_preview_imageview);
        mBackground = (LinearLayout) mRootView.findViewById(R.id.pandora_online_wallpaper_background);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.pandora_online_wallpaper_preview_progress);
        mBaseButton = (BaseButton) mRootView.findViewById(R.id.pandora_online_wallpaper_preview_button);
        mBaseButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                String fileName = PandoraUtils.getRandomString();
                OnlineWallpaperManager.getInstance().saveThemeId(mContext,
                        ThemeManager.THEME_ID_ONLINE);
                OnlineWallpaperManager.getInstance().deleteFile(
                        OnlineWallpaperManager.getInstance().getOnlineWallpaperFileName(mContext));
                OnlineWallpaperManager.getInstance()
                        .saveOnlineWallpaperFileName(mContext, fileName);
                OnlineWallpaperManager.getInstance().downloadImage(
                        list.get(mCurrentPostion).getUrl(), fileName, new Listener<String>() {

                            @SuppressWarnings("deprecation")
                            @Override
                            public void onResponse(String response) {
                                mProgressBar.setVisibility(View.GONE);
                                Bitmap bitmap = PandoraUtils.getBitmap(response);
                                if (null != bitmap) {
                                    mBackground.setBackgroundDrawable(new BitmapDrawable(
                                            getResources(), bitmap));
                                }
                            }
                        }, new ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });

            }
        });
        mGridView = (GridView) mRootView.findViewById(R.id.pandora_online_wallpaper_gridview);
        mWallpaperAdpter = new WallpaperAdpter(mGridView);
        mGridView.setAdapter(mWallpaperAdpter);
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
                    list.get(i).getSelectView()
                            .setBackgroundResource(R.drawable.setting_wallpaper_border);
                    if (null != list.get(i).getBitmap()) {
                        mPreview.setImageBitmap(list.get(i).getBitmap());
                    }
                    mCurrentPostion = i;
                } else {
                    list.get(i).getSelectView().setBackgroundResource(0);
                }
            }

        }
    }

}
