
package cn.zmdx.kaka.locker.meiwen.wallpaper;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.meiwen.BuildConfig;
import cn.zmdx.kaka.locker.meiwen.ImageLoaderManager;
import cn.zmdx.kaka.locker.meiwen.RequestManager;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.meiwen.network.ByteArrayRequest;
import cn.zmdx.kaka.locker.meiwen.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.meiwen.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.meiwen.theme.ThemeManager;
import cn.zmdx.kaka.locker.meiwen.theme.ThemeManager.Theme;
import cn.zmdx.kaka.locker.meiwen.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.meiwen.utils.HDBHashUtils;
import cn.zmdx.kaka.locker.meiwen.utils.HDBLOG;
import cn.zmdx.kaka.locker.meiwen.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.meiwen.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.meiwen.utils.ImageUtils;
import cn.zmdx.kaka.locker.meiwen.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;
import cn.zmdx.kaka.locker.meiwen.widget.ProgressBarCircularIndeterminate;
import cn.zmdx.kaka.locker.meiwen.widget.TypefaceTextView;

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

    private ProgressBarCircularIndeterminate mGVPb;

    private WallpaperAdpter mWallpaperAdpter;

    private ImageView mPreview;

    private TypefaceTextView mDesc;

    private TypefaceTextView mAuthor;

    private TypefaceTextView mApplyButton;

    private ProgressBarCircularIndeterminate mPreviewProgressBar;

    private ArrayList<ServerOnlineWallpaper> list;

    private ServerOnlineWallpaper mCurrentItem;

    private Bitmap mPreviewBitmap;

    private TypefaceTextView mWeatherView;

    private TypefaceTextView mDateView;

    private TypefaceTextView mTemperature;

    private IOnlineWallpaper mListener;

    private LinearLayout mContentView;

    private TypefaceTextView mPromptTextView;

    private ProgressBarCircularIndeterminate mPromptPb;

    private static final boolean PREFER_QUALITY_OVER_SPEED = false;

    private static final int DELAY_PROMPT_TEXTVIEW_GONE = 1000 * 3;

    private static final long DURATION_PROMPT_TEXTVIEW_ANIMATOR = 500;

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
        mRootView = LayoutInflater.from(mContext)
                .inflate(Res.layout.pandora_online_wallpaper, null);
        addView(mRootView);
        mContentView = (LinearLayout) mRootView
                .findViewById(Res.id.pandora_online_wallpaper_content);
        mPromptPb = (ProgressBarCircularIndeterminate) mRootView
                .findViewById(Res.id.pandora_online_wallpaper_prompt_pb);
        mPromptTextView = (TypefaceTextView) mRootView
                .findViewById(Res.id.pandora_online_wallpaper_prompt_text_view);
        mPreview = (ImageView) mRootView
                .findViewById(Res.id.pandora_online_wallpaper_preview_imageview);
        setVisibility(false);
        initPreview();
    }

    private void setVisibility(boolean isShowContent) {
        if (isShowContent) {
            mContentView.setVisibility(View.VISIBLE);
            mPromptPb.setVisibility(View.GONE);
        } else {
            mContentView.setVisibility(View.INVISIBLE);
            mPromptPb.setVisibility(View.VISIBLE);
        }

    }

    public void initContentView() {
        setVisibility(true);
        UmengCustomEventManager.statisticalClickOrDragRopeTimes();
        mDesc = (TypefaceTextView) mRootView
                .findViewById(Res.id.pandora_online_wallpaper_preview_desc);
        mDesc.setMovementMethod(new ScrollingMovementMethod());
        int themeId = ThemeManager.getCurrentThemeId();
        if (themeId == ThemeManager.THEME_ID_ONLINE) {
            String fileName = OnlineWallpaperManager.getInstance().getCurrentWallpaperFileName(
                    mContext);
            String curWallpaperDesc = PandoraConfig.newInstance(mContext).getOnlineWallPaperDesc(
                    fileName);
            mDesc.setText(curWallpaperDesc);
        }
        mAuthor = (TypefaceTextView) mRootView
                .findViewById(Res.id.pandora_online_wallpaper_preview_author);
        mPreviewProgressBar = (ProgressBarCircularIndeterminate) mRootView
                .findViewById(Res.id.pandora_online_wallpaper_preview_progress);
        mApplyButton = (TypefaceTextView) mRootView
                .findViewById(Res.id.pandora_online_wallpaper_apply_button);
        mApplyButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPreviewBitmap == null || mCurrentItem == null) {
                    return;
                }
                OnlineWallpaperManager.getInstance().mkDirs();
                String md5ImageUrl = HDBHashUtils.getStringMD5(mCurrentItem.getImageURL());
                OnlineWallpaperManager.getInstance().saveThemeId(mContext,
                        ThemeManager.THEME_ID_ONLINE);
                ThemeManager.addBitmapToCache(mPreviewBitmap);
                OnlineWallpaperManager.getInstance().saveCurrentWallpaperFileName(mContext,
                        md5ImageUrl);
                PandoraConfig.newInstance(mContext).saveOnlineWallPaperDesc(md5ImageUrl,
                        mCurrentItem.getDesc());
                ImageUtils.saveImageToFile(mPreviewBitmap, OnlineWallpaperManager.getInstance()
                        .getFilePath(md5ImageUrl));
                mListener.applyOnlinePaper(OnlineWallpaperManager.getInstance().getFilePath(
                        md5ImageUrl));
                UmengCustomEventManager.statisticalApplyLockScreenWallpaperTimes();
            }
        });

        OnlineWallpaperManager.getInstance().mkDirs();
        mWeatherView = (TypefaceTextView) mRootView
                .findViewById(Res.id.pandora_online_wallpaper_preview_weather);
        mDateView = (TypefaceTextView) mRootView
                .findViewById(Res.id.pandora_online_wallpaper_preview_date);
        mTemperature = (TypefaceTextView) mRootView
                .findViewById(Res.id.pandora_online_wallpaper_preview_temperature);
        mGVPb = (ProgressBarCircularIndeterminate) mRootView
                .findViewById(Res.id.pandora_online_wallpaper_gridview_pb);
        mGridView = (GridView) mRootView.findViewById(Res.id.pandora_online_wallpaper_gridview);
        mGridView.setVerticalFadingEdgeEnabled(true);
        mGridView.setFadingEdgeLength(BaseInfoHelper.dip2px(mContext, 20));

        readyPullWallpaperFromServer();
    }

    private void readyPullWallpaperFromServer() {
        long curTime = System.currentTimeMillis();
        long lastPullTime = PandoraConfig.newInstance(mContext).getLastOnlinePullTime();
        final String lastPullJson = PandoraConfig.newInstance(mContext)
                .getLastOnlineServerJsonData();
        if ((curTime - lastPullTime) >= PandoraPolicy.MIN_PULL_WALLPAPER_ORIGINAL_TIME
                || TextUtils.isEmpty(lastPullJson)) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("满足获取数据条件，获取网路壁纸数据中...");
            }
            if (!PandoraConfig.newInstance(mContext).isMobileNetwork()
                    && !HDBNetworkState.isWifiNetwork()) {
                pullWallpaperFromSP(lastPullJson);
            } else {
                mGVPb.setVisibility(View.VISIBLE);
                pullWallpaperFromServer(lastPullJson);
            }
        } else {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("未满足获取数据条件，加载本地缓存数据");
            }
            mGVPb.setVisibility(View.GONE);
            pullWallpaperFromSP(lastPullJson);
        }
    }

    private void pullWallpaperFromServer(final String lastPullJson) {
        OnlineWallpaperManager.getInstance().pullWallpaperFromServer(new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("成功获取网路壁纸数据");
                }
                mGVPb.setVisibility(View.GONE);
                list = ServerOnlineWallpaperManager.parseJson(response);
                if (list == null) {
                    String promptString = mContext.getString(Res.string.data_error);
                    showTextPrompt(false, promptString);
                    return;
                }
                if (!TextUtils.isEmpty(lastPullJson)) {
                    try {
                        ArrayList<ServerOnlineWallpaper> spJsonlist = ServerOnlineWallpaperManager.parseJson(new JSONObject(
                                lastPullJson));
                        for (int i = 0; i < list.size(); i++) {
                            String imageUrl = list.get(i).getImageURL();
                            for (int j = 0; j < spJsonlist.size(); j++) {
                                String spImageUrl = spJsonlist.get(j).getImageURL();
                                if (!imageUrl.equals(spImageUrl)) {
                                    list.get(i).setNewData(true);
                                } else {
                                    list.get(i).setNewData(false);
                                    break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setNewData(true);
                    }
                }

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
                if (!TextUtils.isEmpty(lastPullJson)) {
                    pullWallpaperFromSP(lastPullJson);
                } else {
                    String promptString = mContext.getString(Res.string.network_error);
                    showTextPrompt(false, promptString);
                }
                mGVPb.setVisibility(View.GONE);
            }
        });
    }

    private void pullWallpaperFromSP(String lastPullJson) {
        try {
            list = ServerOnlineWallpaperManager.parseJson(new JSONObject(lastPullJson));
            if (null == mWallpaperAdpter) {
                mWallpaperAdpter = new WallpaperAdpter();
                mGridView.setAdapter(mWallpaperAdpter);
            }
            mWallpaperAdpter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            String promptString = mContext.getString(Res.string.error);
            showTextPrompt(true, promptString);
        }
    }

    private void initPreview() {
        Theme curTheme = ThemeManager.getCurrentTheme();
        mPreview.setImageDrawable(curTheme.getCurDrawable());
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

            private ImageView mNewView;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        Res.layout.pandora_online_wallpaper_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mImageViewRl = (ImageView) convertView
                        .findViewById(Res.id.pandora_online_wallpaper_item_rl);
                viewHolder.mImageView = (NetworkImageView) convertView
                        .findViewById(Res.id.pandora_online_wallpaper_item_imageview);
                viewHolder.mNewView = (ImageView) convertView
                        .findViewById(Res.id.pandora_online_wallpaper_item_new);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ServerOnlineWallpaper item = list.get(position);
            item.setPosition(position);

            if (item.isCurItem()) {
                viewHolder.mImageViewRl
                        .setBackgroundResource(Res.drawable.setting_wallpaper_border);
            } else {
                viewHolder.mImageViewRl.setBackgroundResource(0);
            }

            if (item.isNewData()) {
                viewHolder.mNewView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mNewView.setVisibility(View.GONE);
            }

            viewHolder.mImageView.setImageUrl(item.getThumbURL(),
                    ImageLoaderManager.getImageLoader());
            viewHolder.mImageView.setFadeInImage(true);
            viewHolder.mImageView.setErrorImageResId(Res.drawable.online_wallpaper_default);
            viewHolder.mImageView.setDefaultImageResId(Res.drawable.online_wallpaper_default);
            viewHolder.mImageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (ServerOnlineWallpaper sItem : list) {
                        if (sItem.getPosition() == position) {
                            sItem.setCurItem(true);
                            ServerOnlineWallpaper curItem = list.get(position);
                            downloadImage(curItem);
                        } else {
                            sItem.setCurItem(false);
                        }
                    }

                    mWallpaperAdpter.notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }

    private void downloadImage(final ServerOnlineWallpaper serverOnlineWallpaper) {
        Bitmap cacheBitmap = ImageLoaderManager.getOnlineImageCache(mContext).getBitmap(
                HDBHashUtils.getStringMD5(serverOnlineWallpaper.getImageURL()));
        if (null == cacheBitmap) {
            if (!PandoraConfig.newInstance(mContext).isMobileNetwork()
                    && !HDBNetworkState.isWifiNetwork()) {
                String promptString = mContext.getResources().getString(
                        Res.string.setting_network_error);
                showTextPrompt(false, promptString);
                return;
            }
            mPreviewProgressBar.setVisibility(View.VISIBLE);
            ByteArrayRequest mRequest = new ByteArrayRequest(serverOnlineWallpaper.getImageURL(),
                    new Listener<byte[]>() {

                        @Override
                        public void onResponse(byte[] data) {
                            mPreviewBitmap = doParse(data, BaseInfoHelper.getRealWidth(mContext),
                                    BaseInfoHelper.getRealHeight(mContext));
                            if (null != mPreviewBitmap) {
                                mCurrentItem = serverOnlineWallpaper;
                                setPreView(serverOnlineWallpaper);
                                ImageLoaderManager.getOnlineImageCache(mContext).putBitmap(
                                        HDBHashUtils.getStringMD5(serverOnlineWallpaper
                                                .getImageURL()), mPreviewBitmap);
                            }
                            mPreviewProgressBar.setVisibility(View.GONE);
                        }
                    }, new ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mPreviewProgressBar.setVisibility(View.GONE);
                            String promptString = mContext.getResources().getString(
                                    Res.string.network_error);
                            showTextPrompt(true, promptString);
                        }
                    });
            mRequest.setShouldCache(false);
            RequestManager.getRequestQueue().add(mRequest);
        } else {
            mPreviewProgressBar.setVisibility(View.VISIBLE);
            mPreviewBitmap = cacheBitmap;
            mCurrentItem = serverOnlineWallpaper;
            setPreView(serverOnlineWallpaper);
            mPreviewProgressBar.setVisibility(View.GONE);
        }
    }

    private void showTextPrompt(final boolean isNeedClose, String promptString) {
        mPromptTextView.setText(promptString);
        mPromptTextView.setVisibility(View.VISIBLE);
        showTextPromptAnimations(false);
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
                showTextPromptAnimations(true);
                if (isNeedClose) {
                    mListener.applyOnlinePaper("");
                }
            }
        }, DELAY_PROMPT_TEXTVIEW_GONE);
    }

    private void showTextPromptAnimations(boolean isGone) {
        if (null != mPromptTextView) {
            if (isGone) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(mPromptTextView, "alpha", 1, 0.1f);
                animator.setDuration(DURATION_PROMPT_TEXTVIEW_ANIMATOR);
                animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator anim) {
                        mPromptTextView.setText("");
                        mPromptTextView.setVisibility(View.GONE);
                    }
                });
                animator.start();
            } else {
                ObjectAnimator animator = ObjectAnimator.ofFloat(mPromptTextView, "alpha", 0, 1);
                animator.setDuration(DURATION_PROMPT_TEXTVIEW_ANIMATOR);
                animator.start();
            }
        }
    }

    private void setPreView(ServerOnlineWallpaper serverOnlineWallpaper) {
        mDesc.setText(serverOnlineWallpaper.getDesc());
        mAuthor.setText(serverOnlineWallpaper.getAuthor());
        mPreview.setImageDrawable(new BitmapDrawable(getResources(), mPreviewBitmap));
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

    public void setTemperature(String temperature) {
        if (null != mTemperature) {
            mTemperature.setText(temperature);
        }
    }

    public void setWeatherString(String weatherString) {
        if (null != mWeatherView) {
            mWeatherView.setText(weatherString);
        }
    }

}
