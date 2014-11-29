
package cn.zmdx.kaka.locker.wallpaper;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBHashUtils;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils.ILoadBitmapCallback;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.misc.Utils;
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

    private ProgressBar mPreviewProgressBar;

    private ArrayList<ServerOnlineWallpaper> list;

    private ServerOnlineWallpaper mCurrentItem;

    private Bitmap mPreviewBitmap;

    private TypefaceTextView mWeatherView;

    private TypefaceTextView mDateView;

    private IOnlineWallpaper mListener;

    private Theme mCurTheme;

    private ProgressBar mProgressBar;

    private LinearLayout mContentView;

    private static final boolean PREFER_QUALITY_OVER_SPEED = false;

    public interface IOnlineWallpaper {
        void applyOnlinePaper(String filePath);
    }

    public void setOnWallpaperListener(IOnlineWallpaper listener) {
        mListener = listener;
    }

    public OnlineWallpaperView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initRootView();
    }

    public OnlineWallpaperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initRootView();
    }

    public OnlineWallpaperView(Context context) {
        super(context);
        mContext = context;
        initRootView();
    }

    private void initRootView() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.pandora_online_wallpaper, null);
        addView(mRootView);
        mContentView = (LinearLayout) mRootView.findViewById(R.id.pandora_online_wallpaper_content);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.pandora_online_wallpaper_pb);
        setVisibility(false);
    }

    private void setVisibility(boolean isShowContent) {
        if (isShowContent) {
            mContentView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mContentView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

    }

    public void initContentView() {
        setVisibility(true);
        mPreview = (ImageView) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_imageview);
        mDesc = (TypefaceTextView) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_desc);
        mAuthor = (TypefaceTextView) mRootView
                .findViewById(R.id.pandora_online_wallpaper_preview_author);
        mPreviewProgressBar = (ProgressBar) mRootView
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
                ImageUtils.saveImageToFile(mPreviewBitmap, OnlineWallpaperManager.getInstance()
                        .getFilePath(mCurrentItem.getImageNAME()));
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
        if ((curTime - lastPullTime) >= PandoraPolicy.MIN_PULL_WALLPAPER_ORIGINAL_TIME
                || TextUtils.isEmpty(lastPullJson)) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("满足获取数据条件，获取网路壁纸数据中...");
            }

            if (!PandoraConfig.newInstance(mContext).isMobileNetwork()
                    && !HDBNetworkState.isWifiNetwork()) {
                return;
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
                            Toast.makeText(mContext, mContext.getString(R.string.network_error),
                                    Toast.LENGTH_SHORT).show();
                            mGVPb.setVisibility(View.GONE);
                            mListener.applyOnlinePaper("");
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
                mListener.applyOnlinePaper("");
            }

        }
    }

    private void initPreview() {
        if (null != mCurTheme) {
            if (mCurTheme.isDefaultTheme()) {
                mPreview.setImageResource(mCurTheme.getmBackgroundResId());
            } else {
                WallpaperUtils.loadBackgroundBitmap(mContext, mCurTheme.getFilePath(),
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
        if (!PandoraConfig.newInstance(mContext).isMobileNetwork()
                && !HDBNetworkState.isWifiNetwork()) {
            return;
        }
        mPreviewProgressBar.setVisibility(View.VISIBLE);
        Bitmap cacheBitmap = ImageLoaderManager.getOnlineImageCache(mContext).getBitmap(
                HDBHashUtils.getStringMD5(mCurrentItem.getImageURL()));
        if (null == cacheBitmap) {
            OnlineWallpaperManager.getInstance().downloadImage(mCurrentItem.getImageURL(),
                    mCurrentItem.getImageNAME(), new Listener<byte[]>() {

                        @Override
                        public void onResponse(byte[] data) {
                            mPreviewBitmap = doParse(data, BaseInfoHelper.getWidth(mContext),
                                    BaseInfoHelper.getRealHeight(mContext));
                            if (null != mPreviewBitmap && null != mCurrentItem) {
                                setPreView();
                                ImageLoaderManager.getOnlineImageCache(mContext).putBitmap(
                                        HDBHashUtils.getStringMD5(mCurrentItem.getImageURL()),
                                        mPreviewBitmap);
                            }
                        }
                    }, new ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mPreviewProgressBar.setVisibility(View.GONE);
                            Toast.makeText(mContext, "", Toast.LENGTH_LONG).show();
                            mListener.applyOnlinePaper("");
                        }
                    });
        } else {
            mPreviewBitmap = cacheBitmap;
            setPreView();
        }
    }

    private void setPreView() {
        mDesc.setText(mCurrentItem.getDesc());
        mAuthor.setText(mCurrentItem.getAuthor());
        mPreview.setImageDrawable(new BitmapDrawable(getResources(), mPreviewBitmap));
        mPreviewProgressBar.setVisibility(View.GONE);
    }

    /**
     * Scales one side of a rectangle to fit aspect ratio.
     * 
     * @param maxPrimary Maximum size of the primary dimension (i.e. width for
     *            max width), or zero to maintain aspect ratio with secondary
     *            dimension
     * @param maxSecondary Maximum size of the secondary dimension, or zero to
     *            maintain aspect ratio with primary dimension
     * @param actualPrimary Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     */
    public static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
            int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling
        // ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    private Bitmap doParse(byte[] data, int mMaxWidth, int mMaxHeight) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inInputShareable = true;
        decodeOptions.inPurgeable = true;
        decodeOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = null;
        if (mMaxWidth == 0 && mMaxHeight == 0) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        } else {
            // If we have to resize this image, first get the natural bounds.
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            // Then compute the dimensions we would ideally like to decode to.
            int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight, actualWidth, actualHeight);
            int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth, actualHeight,
                    actualWidth);

            // Decode to the nearest power of two scaling factor.
            decodeOptions.inJustDecodeBounds = false;

            // TODO(ficus): Do we need this or is it okay since API 8 doesn't
            // support it?
            if (Utils.hasGingerbreadMR1()) {
                decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
            }

            decodeOptions.inSampleSize = ImageUtils.findBestSampleSize(actualWidth, actualHeight,
                    desiredWidth, desiredHeight);
            Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);

            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null
                    && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }
        return bitmap;
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
