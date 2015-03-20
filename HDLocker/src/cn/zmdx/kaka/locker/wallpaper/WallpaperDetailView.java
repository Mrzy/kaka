
package cn.zmdx.kaka.locker.wallpaper;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zmdx.kaka.locker.ImageLoaderManager;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.network.ByteArrayRequest;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBHashUtils;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.widget.BaseButton;
import cn.zmdx.kaka.locker.widget.ProgressBarMaterial;
import cn.zmdx.kaka.locker.widget.SensorImageView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;

public class WallpaperDetailView extends LinearLayout {
    private Context mContext;

    private View mView;

    private ProgressBarMaterial mLoadingView;

    private FrameLayout mContentView;

    private SensorImageView mImageView;

    private BaseButton mBackButton;

    private BaseButton mApplyButton;

    // private ServerOnlineWallpaper mData;

    private String mImageUrl;

    private String mDesc;

    private boolean isLockScreen;

    public interface IWallpaperDetailListener {
        void onBack();

        void onApplyWallpaper();
    }

    private IWallpaperDetailListener mListener;

    private TextView mDate;

    public WallpaperDetailView(Context context, boolean isScreen) {
        super(context);
        mContext = context;
        isLockScreen = isScreen;
        initView();
    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.wallpaper_detail, null);
        addView(mView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mDate = (TextView) mView.findViewById(R.id.lock_date);
        setDate();
        mLoadingView = (ProgressBarMaterial) mView.findViewById(R.id.wallpaper_loading);
        mContentView = (FrameLayout) mView.findViewById(R.id.wallpaper_content);
        mImageView = (SensorImageView) mView.findViewById(R.id.wallpaper_detail_image);
        mImageView.setTransitionMode(SensorImageView.TRANSITION_MODE_AUTO);
        mBackButton = (BaseButton) mView.findViewById(R.id.wallpaper_return);
        mBackButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onBack();
                }
            }
        });
        mApplyButton = (BaseButton) mView.findViewById(R.id.wallpaper_apply);
        mApplyButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OnlineWallpaperManager.getInstance().mkDirs();
                String md5ImageUrl = HDBHashUtils.getStringMD5(mImageUrl);
                OnlineWallpaperManager.getInstance().saveThemeId(mContext,
                        ThemeManager.THEME_ID_ONLINE);
                Bitmap previewBitmap = ImageLoaderManager.getOnlineImageCache(mContext).getBitmap(
                        md5ImageUrl);
                ThemeManager.addBitmapToCache(previewBitmap);
                OnlineWallpaperManager.getInstance().saveCurrentWallpaperFileName(mContext,
                        md5ImageUrl);
                PandoraConfig.newInstance(mContext).saveOnlineWallPaperDesc(md5ImageUrl, mDesc);
                ImageUtils.saveImageToFile(previewBitmap, OnlineWallpaperManager.getInstance()
                        .getFilePath(md5ImageUrl));
                UmengCustomEventManager.statisticalApplyLockScreenWallpaperTimes();
                if (null != mListener) {
                    mListener.onApplyWallpaper();
                    if (isLockScreen) {
                        LockScreenManager.getInstance().initWallpaper();
                        LockScreenManager.getInstance().collapseNewsPanel();
                    }
                }
            }
        });
    }

    public void setDate() {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String weekString = PandoraUtils.getWeekString(mContext, week);
        String dateString = "" + month + "月" + "" + day + "日 " + weekString;
        mDate.setText(dateString);
    }

    public void setData(String imageUrl, String desc) {
        // mData = serverOnlineWallpaper;
        mImageUrl = imageUrl;
        mDesc = desc;
        downloadImage();
    }

    public void setWallpaperDetailListener(IWallpaperDetailListener listener) {
        mListener = listener;
    }

    private void setImageBitmap(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }

    private void showView(boolean isLoading) {
        if (isLoading) {
            mLoadingView.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.GONE);
        } else {
            mLoadingView.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
        }

    }

    private void downloadImage() {
        Bitmap cacheBitmap = ImageLoaderManager.getOnlineImageCache(mContext).getBitmap(
                HDBHashUtils.getStringMD5(mImageUrl));
        if (null == cacheBitmap) {
            if (PandoraConfig.newInstance(mContext).isOnlyWifiLoadImage()
                    && !HDBNetworkState.isWifiNetwork()) {
                String promptString = mContext.getResources().getString(
                        R.string.setting_network_error);
                return;
            }
            showView(true);
            ByteArrayRequest mRequest = new ByteArrayRequest(mImageUrl, new Listener<byte[]>() {

                @Override
                public void onResponse(byte[] data) {
                    Bitmap previewBitmap = doParse(data, BaseInfoHelper.getRealWidth(mContext),
                            BaseInfoHelper.getRealHeight(mContext));
                    if (null != previewBitmap) {
                        setImageBitmap(previewBitmap);
                        ImageLoaderManager.getOnlineImageCache(mContext).putBitmap(
                                HDBHashUtils.getStringMD5(mImageUrl), previewBitmap);
                    }
                    showView(false);
                }
            }, new ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    mLoadingView.setVisibility(View.GONE);
                }
            });
            mRequest.setShouldCache(false);
            RequestManager.getRequestQueue().add(mRequest);
        } else {
            showView(true);
            setImageBitmap(cacheBitmap);
            showView(false);
        }
    }

    private Bitmap doParse(byte[] data, int mMaxWidth, int mMaxHeight) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inInputShareable = true;
        decodeOptions.inPurgeable = true;
        decodeOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        return bitmap;
    }

}
