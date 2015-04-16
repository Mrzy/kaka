
package cn.zmdx.kaka.locker.wallpaper;

import java.io.IOException;
import java.util.Calendar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.ImageLoaderManager;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.font.FontManager;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.utils.HDBHashUtils;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.wallpaper.WallpaperUtils.IDownLoadWallpaper;
import cn.zmdx.kaka.locker.widget.BaseButton;
import cn.zmdx.kaka.locker.widget.CheckBox;
import cn.zmdx.kaka.locker.widget.ProgressBarMaterial;
import cn.zmdx.kaka.locker.widget.SensorImageView;
import cn.zmdx.kaka.locker.widget.TextClockCompat;

public class WallpaperDetailView extends LinearLayout implements OnCheckedChangeListener {
    private Context mContext;

    private View mView;

    private ProgressBarMaterial mLoadingView;

    private FrameLayout mContentView;

    private SensorImageView mImageView;

    private CheckBox mWallpaperDesktop;

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

    private TextClockCompat mClock;

    private Bitmap mPreBitmap;

    private boolean isApplyDesktop;

    private WallpaperObserver mObserver;

    public WallpaperDetailView(Context context, boolean isScreen) {
        super(context);
        mContext = context;
        isLockScreen = isScreen;
        initView();
        if (isScreen) {
            UmengCustomEventManager.statisticalLockScreenWallpaperDetailTimes();
        }
    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.wallpaper_detail, null);
        addView(mView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mDate = (TextView) mView.findViewById(R.id.lock_date);
        mClock = (TextClockCompat) mView.findViewById(R.id.digitalClock);
        mClock.setTypeface(FontManager.getTypeface("fonts/Roboto-Thin.ttf"));
        setDate();
        mLoadingView = (ProgressBarMaterial) mView.findViewById(R.id.wallpaper_loading);
        mContentView = (FrameLayout) mView.findViewById(R.id.wallpaper_content);
        mImageView = (SensorImageView) mView.findViewById(R.id.wallpaper_detail_image);
        mImageView.setTransitionMode(SensorImageView.TRANSITION_MODE_AUTO);

        mWallpaperDesktop = (CheckBox) mView.findViewById(R.id.wallpaper_to_desktop);
        mWallpaperDesktop.setOnCheckedChangeListener(this);
        mWallpaperDesktop.setChecked(isApplyDesktopOn());

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
                if (null == mPreBitmap || null == mContext) {
                    return;
                }
                String md5Url = HDBHashUtils.getStringMD5(mImageUrl);
                saveOnlineWallpaperState(md5Url);
                ThemeManager.addBitmapToCache(mPreBitmap);
                saveBitmapToFile(md5Url);
                if (isLockScreen) {
                    UmengCustomEventManager.statisticalLockScreenWallpaperDetailApplyTimes();
                }
                if (null != mListener) {
                    mListener.onApplyWallpaper();
                    if (isLockScreen) {
                        LockScreenManager.getInstance().collapseNewsPanel();
                        HDBThreadUtils.postOnUiDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LockScreenManager.getInstance().initWallpaper();
                            }
                        }, 300);
                    }
                }
            }
        });

    }

    private void saveOnlineWallpaperState(String md5Url) {
        OnlineWallpaperManager.getInstance().mkDirs();
        OnlineWallpaperManager.getInstance().saveThemeId(mContext, ThemeManager.THEME_ID_ONLINE);
        OnlineWallpaperManager.getInstance().saveCurrentWallpaperFileName(mContext, md5Url);
        PandoraConfig.newInstance(mContext).saveOnlineWallPaperDesc(md5Url, mDesc);
    }

    private void saveBitmapToFile(final String url) {
        HDBThreadUtils.runOnWorker(new Runnable() {

            @Override
            public void run() {
                ImageUtils.saveImageToFile(mPreBitmap, OnlineWallpaperManager.getInstance()
                        .getFilePath(url));
                if (isApplyDesktop) {
                    @SuppressWarnings("deprecation")
                    IntentFilter filter = new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED);
                    mObserver = new WallpaperObserver();
                    HDApplication.getContext().registerReceiver(mObserver, filter);
                    HDBThreadUtils.runOnUi(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(mContext, "桌面壁纸设置中...", Toast.LENGTH_SHORT).show();
                        }
                    });
                    try {
                        WallpaperManager.getInstance(mContext).setBitmap(mPreBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class WallpaperObserver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            HDBThreadUtils.runOnUi(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(mContext, "桌面壁纸设置成功", Toast.LENGTH_SHORT).show();
                }
            });
            if (null != mObserver) {
                HDApplication.getContext().unregisterReceiver(mObserver);
            }
        }
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
        mImageUrl = imageUrl;
        mDesc = desc;
        showView(true);
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
                ImageLoadAsyn mDataAsyn = new ImageLoadAsyn();
                mDataAsyn.execute();
            }
        }, 300);
    }

    public void setWallpaperDetailListener(IWallpaperDetailListener listener) {
        mListener = listener;
    }

    private void setImageBitmap(final Bitmap bitmap) {
        ViewCompat.setAlpha(mImageView, 0);
        mImageView.setImageBitmap(bitmap);
        ObjectAnimator animatorAlphaVisible = ObjectAnimator.ofInt(mImageView, "alpha", 100, 255);
        animatorAlphaVisible.setDuration(300);
        animatorAlphaVisible.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int t = (Integer) animation.getAnimatedValue();
                ViewCompat.setAlpha(mImageView, t);
            }
        });
        animatorAlphaVisible.start();

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

    class ImageLoadAsyn extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap cacheBitmap = ImageLoaderManager.getOnlineImageCache(mContext).getBitmap(
                    HDBHashUtils.getStringMD5(mImageUrl));
            return cacheBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (null == result) {
                if (PandoraConfig.newInstance(mContext).isOnlyWifiLoadImage()
                        && !HDBNetworkState.isWifiNetwork()) {
                    return;
                }
                WallpaperUtils.downloadWallpaper(mContext, mImageUrl, new IDownLoadWallpaper() {

                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        showView(false);
                        if (null != bitmap) {
                            setImageBitmap(bitmap);
                            mPreBitmap = bitmap;
                            HDBThreadUtils.runOnWorker(new Runnable() {

                                @Override
                                public void run() {
                                    ImageLoaderManager.getOnlineImageCache(mContext).putBitmap(
                                            HDBHashUtils.getStringMD5(mImageUrl), mPreBitmap);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFail() {
                        if (null != mListener) {
                            mListener.onBack();
                        }
                        Toast.makeText(mContext, "下载壁纸失败，请重试", Toast.LENGTH_SHORT).show();
                        showView(false);
                    }

                    @Override
                    public void onProgress(String progress) {
                        if (null != mLoadingView) {
                            mLoadingView.setProgress(progress);
                        }
                    }
                });
            } else {
                setImageBitmap(result);
                mPreBitmap = result;
                showView(false);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mWallpaperDesktop) {
            if (isChecked) {
                enableApplyDesktop();
            } else {
                disableApplyDesktop();
            }
            isApplyDesktop = isChecked;
        }
    }

    private boolean isApplyDesktopOn() {
        return PandoraConfig.newInstance(getContext()).isApplyDesktopOn();
    }

    private void enableApplyDesktop() {
        PandoraConfig.newInstance(getContext()).saveApplyDesktopState(true);
    }

    private void disableApplyDesktop() {
        PandoraConfig.newInstance(getContext()).saveApplyDesktopState(false);
    }
}
