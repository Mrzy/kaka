
package cn.zmdx.kaka.locker.content;

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
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.ImageLoaderManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.box.FoldablePage;
import cn.zmdx.kaka.locker.content.box.IFoldableBox;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

import com.android.volley.misc.ImageUtils;
import com.nineoldandroids.view.ViewHelper;

public class FoldableCard extends Card {

    private ServerImageData mData;

    private IFoldableBox mBox;

    // px
    protected static final int MAX_HEIGHT_IMAGE_VIEW = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 180);

    protected static final int MIN_HEIGHT_IMAGE_VIEW = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 120);

    public FoldableCard(Context context, int innerLayout) {
        super(context, innerLayout);
        init();
    }

    public FoldableCard(Context context, IFoldableBox box, ServerImageData data) {
        this(context, R.layout.foldable_card_item_layout);
        mData = data;
        mBox = box;
    }

    private void init() {
        setBackgroundResourceId(R.drawable.pandora_box_item_selector);
        setSwipeable(false);
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                if (mBox instanceof FoldablePage) {
                    FoldablePage box = (FoldablePage) mBox;
                    box.openDetails(view.findViewById(R.id.card_item_layout_large), mData);
                    UmengCustomEventManager.statisticalSeeContentDetails();
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
        Options opt = new Options();
        opt.inJustDecodeBounds = true;
        DiskImageHelper.getBitmapByUrl(mData.getUrl(), opt);
        ImageView imageView = null;
        TextView titleView = null;
        imageView = (ImageView) view.findViewById(R.id.card_item_large_imageview);
        titleView = (TextView) view.findViewById(R.id.card_item_large_title);
        opt.inSampleSize = ImageUtils.calculateInSampleSize(opt, BaseInfoHelper.getRealWidth(mContext),
                BaseInfoHelper.getRealWidth(mContext));
        setImageViewSize(imageView, opt);

        titleView.setText(mData.getTitle());
        opt.inJustDecodeBounds = false;
        if (mData.getDataType().equals(ServerDataMapping.S_DATATYPE_HTML)) {
            imageView.setImageResource(R.drawable.html_icon_default);
        } else {
            Bitmap cacheBmp = getCoverImageFromCache(mData.getUrl());
            if (cacheBmp == null) {
                new ImageAsyncLoadTask(imageView, opt).execute(mData.getUrl());
            } else {
                imageView.setImageBitmap(cacheBmp);
            }
        }
    }

    private Bitmap getCoverImageFromCache(String url) {
        return ImageLoaderManager.getImageMemCache().getBitmap(url);
    }

    private static class ImageAsyncLoadTask extends AsyncTask<String, Integer, Bitmap> {

        private ImageView mImageView;

        private Options mOpt;

        public ImageAsyncLoadTask(ImageView imageView, Options opt) {
            mImageView = imageView;
            mOpt = opt;
        }

        // 获得封面图片的Bitmap。此方法会优先从内存缓存中寻找，如果没有，会从磁盘decode
        @Override
        protected Bitmap doInBackground(String... params) {
            String imgUrl = params[0];
            Bitmap bmp = DiskImageHelper.getBitmapByUrl(imgUrl, mOpt);
            ImageLoaderManager.getImageMemCache().putBitmap(imgUrl, bmp);
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap coverBmp) {
            ViewHelper.setAlpha(mImageView, 0);
            mImageView.setImageBitmap(coverBmp);
            mImageView.animate().alpha(1).setDuration(200).start();
        }
    }

    private void setImageViewSize(ImageView iv, Options opt) {
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
    }
}
