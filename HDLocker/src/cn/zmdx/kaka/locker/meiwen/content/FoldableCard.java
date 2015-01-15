
package cn.zmdx.kaka.locker.meiwen.content;

import java.util.Calendar;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.meiwen.HDApplication;
import cn.zmdx.kaka.locker.meiwen.ImageLoaderManager;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.content.ServerImageDataManager.IDownloadListener;
import cn.zmdx.kaka.locker.meiwen.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.meiwen.content.box.FoldablePage;
import cn.zmdx.kaka.locker.meiwen.content.box.IFoldablePage;
import cn.zmdx.kaka.locker.meiwen.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.meiwen.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.meiwen.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.meiwen.widget.TypefaceTextView;

import com.android.volley.misc.ImageUtils;
import com.nineoldandroids.view.ViewHelper;

public class FoldableCard extends Card {

    private ServerImageData mData;

    private IFoldablePage mBox;

    // px
    protected static final int MAX_HEIGHT_IMAGE_VIEW = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 180);

    protected static final int MIN_HEIGHT_IMAGE_VIEW = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 120);

    public FoldableCard(Context context, int innerLayout) {
        super(context, innerLayout);
        init();
    }

    public FoldableCard(Context context, IFoldablePage box, ServerImageData data) {
        this(context, Res.layout.foldable_card_item_layout);
        mData = data;
        mBox = box;
    }

    private void init() {
        setBackgroundResourceId(Res.drawable.pandora_box_item_selector);
        setSwipeable(false);
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                if (mBox instanceof FoldablePage) {
                    FoldablePage box = (FoldablePage) mBox;
                    box.openDetails(view.findViewById(Res.id.card_item_layout_large), mData);
                    if (null != mData) {
                        String cloudId = mData.getCloudId();
                        UmengCustomEventManager.statisticalSeeContentDetails(cloudId);
                    }
                }
            }
        });
    }

    public void doSwipeOut(boolean isRight, int duration, int delay, AnimatorListener listener) {
        final CardView cv = getCardView();
        if (cv == null) {
            return;
        }
        final ViewPropertyAnimator vpa = cv.animate();
        vpa.setStartDelay(delay);
        vpa.translationX(isRight ? cv.getMeasuredWidth() : -cv.getMeasuredWidth());
        vpa.setDuration(duration);
        if (listener != null) {
            vpa.setListener(listener);
        }
        vpa.start();
    }

    public String getDataType() {
        return mData.getDataType();
    }

    public ServerImageData getData() {
        return mData;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        ImageView imageView = null;
        TypefaceTextView titleView = null;
        imageView = (ImageView) view.findViewById(Res.id.card_item_large_imageview);
        titleView = (TypefaceTextView) view.findViewById(Res.id.card_item_large_title);
        TextView dayView = (TextView) view.findViewById(Res.id.card_date_day);
        TextView monthView = (TextView) view.findViewById(Res.id.card_date_month);

        long collectTime = 0;
        try {
            collectTime = Long.valueOf(mData.getCollectTime());
        } catch (Exception e) {
            collectTime = System.currentTimeMillis();
        }
        dayView.setText("" + getDayByTime(collectTime, Calendar.DAY_OF_MONTH));
        int month = getDayByTime(collectTime, Calendar.MONTH) + 1;
        int xq = getDayByTime(collectTime, Calendar.DAY_OF_WEEK);

        monthView.setText(month + "月\n" + getWeekStr(xq));
        final Options opt = setImageViewSize(imageView, mData.getUrl());

        titleView.setText(mData.getTitle());
        opt.inJustDecodeBounds = false;
        if (mData.getDataType().equals(ServerDataMapping.S_DATATYPE_HTML)) {
            imageView.setImageResource(Res.drawable.html_icon_default);
        } else {
            Bitmap cacheBmp = getCoverImageFromCache(mData.getUrl());
            if (cacheBmp == null) {
                new ImageAsyncLoadTask(imageView, opt, mData).execute(mData.getUrl());
            } else {
                imageView.setImageBitmap(cacheBmp);
            }
        }
    }

    private String getWeekStr(int xq) {
        if (xq == 1) {
            return mContext.getString(Res.string.sunday);
        } else if (xq == 2) {
            return mContext.getString(Res.string.monday);
        } else if (xq == 3) {
            return mContext.getString(Res.string.tuesday);
        } else if (xq == 4) {
            return mContext.getString(Res.string.wednesday);
        } else if (xq == 5) {
            return mContext.getString(Res.string.thursday);
        } else if (xq == 6) {
            return mContext.getString(Res.string.friday);
        } else if (xq == 7) {
            return mContext.getString(Res.string.saturday);
        } else {
            return "";
        }
    }

    private int getDayByTime(long time, int field) {
        Calendar cal = Calendar.getInstance();
        if (time > 0) {
            cal.setTimeInMillis(time);
        }
        return cal.get(field);
    }

    private Bitmap getCoverImageFromCache(String url) {
        return ImageLoaderManager.getImageMemCache().getBitmap(url);
    }

    private static class ImageAsyncLoadTask extends AsyncTask<String, Integer, Bitmap> {

        private ImageView mImageView;

        private Options mOpt;

        private ServerImageData mData;

        public ImageAsyncLoadTask(ImageView imageView, Options opt, ServerImageData data) {
            mImageView = imageView;
            mOpt = opt;
            mData = data;
        }

        // 获得封面图片的Bitmap。此方法会优先从内存缓存中寻找，如果没有，会从磁盘decode
        @Override
        protected Bitmap doInBackground(String... params) {
            String imgUrl = params[0];
            Bitmap bmp = DiskImageHelper.getBitmapByUrl(imgUrl, mOpt);
            if (bmp != null) {
                ImageLoaderManager.getImageMemCache().putBitmap(imgUrl, bmp);
            } else {
                // TODO download from web
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap coverBmp) {
            if (coverBmp == null) {
                ServerImageDataManager.getInstance().downloadImage(mData, new IDownloadListener() {

                    @Override
                    public void onSuccess(String filePath) {
                        Bitmap map = DiskImageHelper.getBitmapByUrl(mData.getUrl(), null);
                        mImageView.setImageBitmap(map);
                        ServerImageDataModel.getInstance().markAlreadyDownload(mData.getId());
                    }

                    @Override
                    public void onFailed() {
                    }
                });
            } else {
                ViewHelper.setAlpha(mImageView, 0);
                mImageView.setImageBitmap(coverBmp);
                mImageView.animate().alpha(1).setDuration(200).start();
            }
        }
    }

    private Options setImageViewSize(ImageView iv, String url) {
        Options opt = new Options();
        opt.inJustDecodeBounds = true;
        DiskImageHelper.getBitmapByUrl(url, opt);
        opt.inSampleSize = ImageUtils.calculateInSampleSize(opt,
                BaseInfoHelper.getRealWidth(mContext), BaseInfoHelper.getRealWidth(mContext));

        int bmpWidth = 0;
        int bmpHeight = 0;
        if (opt != null) {
            bmpWidth = opt.outWidth;
            bmpHeight = opt.outHeight;
        }
        ViewGroup.LayoutParams lp = iv.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        int screenWidth = BaseInfoHelper.getRealWidth(mContext);
        try {
            float rate = (float) screenWidth / (float) bmpWidth;
            lp.height = (int) (rate * bmpHeight);
        } catch (Exception e) {
            lp.height = LayoutParams.MATCH_PARENT;
        }
        if (lp.height >= MAX_HEIGHT_IMAGE_VIEW) {
            lp.height = MAX_HEIGHT_IMAGE_VIEW;
        } else if (lp.height < MIN_HEIGHT_IMAGE_VIEW) {
            lp.height = MIN_HEIGHT_IMAGE_VIEW;
        }
        iv.setLayoutParams(lp);
        return opt;
    }
}
