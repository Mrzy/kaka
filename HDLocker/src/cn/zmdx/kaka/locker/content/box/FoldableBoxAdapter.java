
package cn.zmdx.kaka.locker.content.box;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.DiskImageHelper;
import cn.zmdx.kaka.locker.content.ServerDataMapping;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.box.FoldableBox.FoldableCard;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

import com.android.volley.misc.ImageUtils;

public class FoldableBoxAdapter extends CardArrayAdapter {

    public FoldableBoxAdapter(Context context, List<Card> cards) {
        super(context, cards);
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
        if (position == 0 && !card.getDataType().equals(ServerDataMapping.S_DATATYPE_HTML)) {
            view.findViewById(R.id.card_item_layout_simple).setVisibility(View.GONE);
            View largeView = view.findViewById(R.id.card_item_layout_large);
            largeView.setVisibility(View.VISIBLE);
            imageView = (ImageView) largeView.findViewById(R.id.card_item_large_imageview);
            titleView = (TextView) view.findViewById(R.id.card_item_large_title);
            opt.inSampleSize = ImageUtils.calculateInSampleSize(opt, BaseInfoHelper.getWidth(mContext), BaseInfoHelper.getWidth(mContext));

        } else {
            View simpleView = view.findViewById(R.id.card_item_layout_simple);
            view.findViewById(R.id.card_item_layout_large).setVisibility(View.GONE);
            simpleView.setVisibility(View.VISIBLE);
            imageView = (ImageView) simpleView.findViewById(R.id.card_item_simple_imageview);
            titleView = (TextView) simpleView.findViewById(R.id.card_item_simple_title);
            opt.inSampleSize = ImageUtils.calculateInSampleSize(opt, 180, 180);
        }

        titleView.setText(data.getTitle());
        opt.inJustDecodeBounds = false;
        Bitmap bmp = DiskImageHelper.getBitmapByUrl(data.getUrl(), opt);
        imageView.setImageBitmap(bmp);
        // ImageView iv = (ImageView) view.findViewById(R.id.card_imageview);
        // TextView tv = (TextView) view.findViewById(R.id.card_title);
        // RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
        // tv.getLayoutParams();
        // if (card.getDataType().equals(ServerDataMapping.S_DATATYPE_HTML)) {
        //
        // // card.getCardView().setGravity(Gravity.CENTER_VERTICAL);
        // lp.height = 200;
        // tv.setGravity(Gravity.CENTER_VERTICAL);
        // // tv.setTextSize(20);
        // tv.setTextColor(Color.DKGRAY);
        // tv.setBackgroundColor(Color.TRANSPARENT);
        //
        // // iv.setBackgroundColor(Color.parseColor("#aaaaaa"));
        // } else {
        // lp.height = LayoutParams.WRAP_CONTENT;
        // tv.setTextColor(Color.WHITE);
        // tv.setBackgroundResource(R.drawable.card_list_item_title_bg);
        // }
        // tv.setLayoutParams(lp);
        return view;
    }
}
