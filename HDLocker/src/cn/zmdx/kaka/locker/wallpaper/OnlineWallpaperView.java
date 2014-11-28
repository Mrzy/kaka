
package cn.zmdx.kaka.locker.wallpaper;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
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
import android.widget.Toast;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.ImageLoaderManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils.ILoadBitmapCallback;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;
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

    private ProgressBar mGVPb;

    private WallpaperAdpter mWallpaperAdpter;

    private ImageView mPreview;

    private TypefaceTextView mDesc;

    private TypefaceTextView mAuthor;

    private Button mApplyButton;

    private ProgressBar mProgressBar;

    private ArrayList<ServerOnlineWallpaper> list;

    private ServerOnlineWallpaper mCurrentItem;

    private Bitmap mPreviewBitmap;

    private TypefaceTextView mWeatherView;

    private TypefaceTextView mDateView;

    private IOnlineWallpaper mListener;

    private Theme mCurTheme;

    public interface IOnlineWallpaper {
        void applyOnlinePaper(String filePath);
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
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.pandora_online_wallpaper, null);
        addView(mRootView);
        mPreview = (ImageView) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_imageview);
        mDesc = (TypefaceTextView) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_desc);
        mAuthor = (TypefaceTextView) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_author);
        mProgressBar = (ProgressBar) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_progress);
        mApplyButton = (Button) mRootView.findViewById(R.id.pandora_online_wallpaper_apply_button);
        mApplyButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPreviewBitmap == null || mCurrentItem == null) {
                    return;
                }
                OnlineWallpaperManager.getInstance().saveThemeId(mContext,
                        ThemeManager.THEME_ID_ONLINE);
                OnlineWallpaperManager.getInstance().saveCurrentWallpaperFileName(mContext,
                        mCurrentItem.getImageNAME());
                OnlineWallpaperManager.getInstance().renameFile(mCurrentItem.getImageNAME());
                mListener.applyOnlinePaper(OnlineWallpaperManager.getInstance().getFilePath(
                        mCurrentItem.getImageNAME()));
            }
        });

        OnlineWallpaperManager.getInstance().mkDirs();

        mWeatherView = (TypefaceTextView) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_weather);
        mDateView = (TypefaceTextView) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_date);

        mGVPb = (ProgressBar) mRootView.findViewById(R.id.pandora_online_wallpaper_gridview_pb);
        mGridView = (GridView) mRootView.findViewById(R.id.pandora_online_wallpaper_gridview);

        pullWallpaperFromServer();
    }

    private void pullWallpaperFromServer() {
        long curTime = System.currentTimeMillis();
        long lastPullTime = PandoraConfig.newInstance(mContext).getLastOnlinePullTime();
        String lastPullJson = PandoraConfig.newInstance(mContext).getLastOnlineServerJsonData();
        mGVPb.setVisibility(View.VISIBLE);
        if ((curTime - lastPullTime) >= 60 * 60 * 3 || TextUtils.isEmpty(lastPullJson)) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("满足获取数据条件，获取网路壁纸数据中...");
            }
            OnlineWallpaperManager.getInstance().pullWallpaperFromServer(
                    new Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            if (BuildConfig.DEBUG) {
                                HDBLOG.logD("成功获取网路壁纸数据");
                            }
                            mGVPb.setVisibility(View.GONE);
                            list = ServerOnlineWallpaperManager.parseJson(response);
                            if (null == mWallpaperAdpter) {
                                mWallpaperAdpter = new WallpaperAdpter();
                                mGridView.setAdapter(mWallpaperAdpter);
                            }
                            mWallpaperAdpter.notifyDataSetChanged();
                            PandoraConfig.newInstance(mContext).saveLastOnlinePullTime(
                                    System.currentTimeMillis());
                            PandoraConfig.newInstance(mContext).saveLastOnlineServerJsonData(
                                    response.toString());
                        }
                    }, new ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mContext, mContext.getString(R.string.error),
                                    Toast.LENGTH_SHORT).show();
                            mListener.applyOnlinePaper(null);
                        }
                    });
        } else {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("未满足获取数据条件，加载本地缓存数据");
            }
            mGVPb.setVisibility(View.GONE);
            try {
                list = ServerOnlineWallpaperManager.parseJson(new JSONObject(lastPullJson));
                if (null == mWallpaperAdpter) {
                    mWallpaperAdpter = new WallpaperAdpter();
                    mGridView.setAdapter(mWallpaperAdpter);
                }
                mWallpaperAdpter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mContext, mContext.getString(R.string.error), Toast.LENGTH_SHORT)
                        .show();
                mListener.applyOnlinePaper(null);
            }

        }
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
            ServerOnlineWallpaper item = list.get(position);
            if (null == item.getSelectView()) {
                item.setPosition(position);
                item.setSelectView(viewHolder.mImageViewRl);
            }

            viewHolder.mImageView.setImageUrl(item.getThumbURL(),
                    ImageLoaderManager.getImageLoader());
            // viewHolder.mImageView.setFadeInImage(true);
            viewHolder.mImageView.setDefaultImageResId(R.drawable.online_wallpaper_default);
            viewHolder.mImageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (ServerOnlineWallpaper sItem : list) {
                        if (sItem.getPosition() == position) {
                            if (null != sItem.getSelectView()) {
                                sItem.getSelectView().setBackgroundResource(
                                        R.drawable.setting_wallpaper_border);
                                mCurrentItem = list.get(position);
                                downloadImage();
                            }
                        } else {
                            if (null != sItem.getSelectView()) {
                                sItem.getSelectView().setBackgroundResource(0);
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
        OnlineWallpaperManager.getInstance().downloadImage(mCurrentItem.getImageURL(),
                mCurrentItem.getImageNAME(), new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        mProgressBar.setVisibility(View.GONE);
                        if (null != mPreviewBitmap && !mPreviewBitmap.isRecycled()) {
                            mPreviewBitmap.recycle();
                            System.gc();
                        }
                        mDesc.setText(mCurrentItem.getDesc());
                        mAuthor.setText(mCurrentItem.getAuthor());
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
