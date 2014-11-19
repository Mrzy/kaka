
package cn.zmdx.kaka.locker.content.box;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.DiskImageHelper;
import cn.zmdx.kaka.locker.content.ServerDataMapping;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.box.FoldablePage.FoldableCard;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

import com.android.volley.misc.ImageUtils;

public class FoldableBoxAdapter extends CardArrayAdapter {

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
        ImageView imageView;
        TextView titleView;
        if (position == 0 && !card.getDataType().equals(ServerDataMapping.S_DATATYPE_HTML)
                && !card.getDataType().equals(ServerDataMapping.S_DATATYPE_GUIDE)) {
            view.findViewById(R.id.card_item_layout_simple).setVisibility(View.GONE);
            View largeView = view.findViewById(R.id.card_item_layout_large);
            largeView.setVisibility(View.VISIBLE);
            imageView = (ImageView) largeView.findViewById(R.id.card_item_large_imageview);
            titleView = (TextView) view.findViewById(R.id.card_item_large_title);
            opt.inSampleSize = ImageUtils.calculateInSampleSize(opt,
                    BaseInfoHelper.getWidth(mContext), BaseInfoHelper.getWidth(mContext));
            animateLargeView(largeView);
        } else if (card.getDataType().equals(ServerDataMapping.S_DATATYPE_GUIDE)) {
            View simpleView = view.findViewById(R.id.card_item_layout_simple);
            view.findViewById(R.id.card_item_layout_large).setVisibility(View.GONE);
            simpleView.setVisibility(View.VISIBLE);
            imageView = (ImageView) simpleView.findViewById(R.id.card_item_simple_imageview);
            titleView = (TextView) simpleView.findViewById(R.id.card_item_simple_title);
        } else {
            View simpleView = view.findViewById(R.id.card_item_layout_simple);
            view.findViewById(R.id.card_item_layout_large).setVisibility(View.GONE);
            simpleView.setVisibility(View.VISIBLE);
            imageView = (ImageView) simpleView.findViewById(R.id.card_item_simple_imageview);
            titleView = (TextView) simpleView.findViewById(R.id.card_item_simple_title);
            int reqWidth = BaseInfoHelper.dip2px(getContext(), 100);
            opt.inSampleSize = ImageUtils.calculateInSampleSize(opt, reqWidth, reqWidth);
        }

        titleView.setText(data.getTitle());
        opt.inJustDecodeBounds = false;
        Bitmap bmp = DiskImageHelper.getBitmapByUrl(data.getUrl(), opt);
        if (bmp == null
                && (card.getDataType().equals(ServerDataMapping.S_DATATYPE_HTML))) {// html类型没有缩略图，使用默认图
            imageView.setImageResource(R.drawable.html_icon_default);
        } else {
            imageView.setImageBitmap(bmp);
        }
        return view;
    }

    private void animateLargeView(View largeView) {
        largeView.setAlpha(0);
        largeView.animate().alpha(1).setDuration(500).start();
    }

    public List<Card> getCardsData() {
        return mCards;
    }
}
