
package cn.zmdx.kaka.locker.content.box;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.ImageLoaderManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.DiskImageHelper;
import cn.zmdx.kaka.locker.content.ServerDataMapping;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.box.FoldablePage.FoldableCard;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

import com.android.volley.misc.ImageUtils;
import com.nineoldandroids.view.ViewHelper;

public class FoldableBoxAdapter extends CardArrayAdapter {
    // px
    protected static final int MAX_HEIGHT_IMAGE_VIEW = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 180);

    protected static final int MIN_HEIGHT_IMAGE_VIEW = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 120);

    private List<Card> mCards;

    public FoldableBoxAdapter(Context context, List<Card> cards) {
        super(context, cards);
        mCards = cards;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        FoldableCard card = (FoldableCard) getItem(position);
        ServerImageData data = card.getData();

        Options opt = new Options();
        opt.inJustDecodeBounds = true;
        DiskImageHelper.getBitmapByUrl(data.getUrl(), opt);
        ImageView imageView = null;
        TextView titleView = null;
        imageView = (ImageView) view.findViewById(R.id.card_item_large_imageview);
        titleView = (TextView) view.findViewById(R.id.card_item_large_title);
        opt.inSampleSize = ImageUtils.calculateInSampleSize(opt, BaseInfoHelper.getWidth(mContext),
                BaseInfoHelper.getWidth(mContext));
        setImageViewSize(imageView, opt);

        titleView.setText(data.getTitle());
        opt.inJustDecodeBounds = false;
        if (card.getDataType().equals(ServerDataMapping.S_DATATYPE_HTML)) {
            imageView.setImageResource(R.drawable.html_icon_default);
        } else {
            Bitmap cacheBmp = getCoverImageFromCache(data.getUrl());
            if (cacheBmp == null) {
                new ImageAsyncLoadTask(imageView, opt).execute(data.getUrl());
            } else {
                imageView.setImageBitmap(cacheBmp);
            }
        }
        return view;
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
        int screenWidth = BaseInfoHelper.getWidth(mContext);
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

    public List<Card> getCardsData() {
        return mCards;
    }
}
