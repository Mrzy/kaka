
package cn.zmdx.kaka.locker.content.box;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.DiskImageHelper;
import cn.zmdx.kaka.locker.content.ServerDataMapping;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.alexvasilkov.foldablelayout.UnfoldableView.OnFoldingListener;
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading;

public class FoldableBox implements IFoldableBox, OnFoldingListener, View.OnClickListener {
    private Context mContext;

    private List<ServerImageData> mData;

    private CardListView mListView;

    private UnfoldableView mUnfoldableView;

    private ViewGroup mDetailLayout, mContentContainerView;

    private View mContainerView, mListTouchInterceptor;

    private TextView mTitleView, mContentView;

    private ImageView mImageView;

    public FoldableBox(Context context, List<ServerImageData> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCategory() {
        return IPandoraBox.CATEGORY_FOLDABLE;
    }

    @Override
    public List<ServerImageData> getData() {
        return mData;
    }

    @Override
    public View getRenderedView() {
        mContainerView = LayoutInflater.from(mContext).inflate(
                R.layout.pandora_flodable_box_layout, null);
        mListView = (CardListView) mContainerView.findViewById(R.id.cardListView);
        mUnfoldableView = (UnfoldableView) mContainerView.findViewById(R.id.unfoldable_view);
        mUnfoldableView.setOnFoldingListener(this);
        mTitleView = (TextView) mContainerView.findViewById(R.id.card_detail_title);
        mContentView = (TextView) mContainerView.findViewById(R.id.card_detail_content);
        mContentContainerView = (ViewGroup) mContainerView.findViewById(R.id.cardDetailContainer);
        mImageView = (ImageView) mContainerView.findViewById(R.id.card_detail_image);
        Bitmap glance = ((BitmapDrawable) mContext.getResources().getDrawable(
                R.drawable.unfold_glance)).getBitmap();
        mUnfoldableView.setFoldShading(new GlanceFoldShading(mContext, glance));
        mUnfoldableView.setGesturesEnabled(false);
        mDetailLayout = (ViewGroup) mContainerView.findViewById(R.id.cardDetailLayout);
        mDetailLayout.setVisibility(View.INVISIBLE);
        mListTouchInterceptor = mContainerView.findViewById(R.id.touch_interceptor_view);
        mListTouchInterceptor.setClickable(false);
        List<Card> cards = new ArrayList<Card>();
        loadData(cards, mData);
        CardArrayAdapter adapter = new FoldableBoxAdapter(mContext, cards);
        mListView.setAdapter(adapter);
        return mContainerView;
    }

    private void loadData(List<Card> cards, List<ServerImageData> data) {
        int size = data.size();
        FoldableCard card = null;
        for (int i = 0; i < size; i++) {
            card = new FoldableCard(mContext, this, data.get(i));
            cards.add(card);
        }
    }

    public static class FoldableCard extends Card {

        private ServerImageData mData;

        private IFoldableBox mBox;

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
            setSwipeable(true);
            setOnSwipeListener(new OnSwipeListener() {
                @Override
                public void onSwipe(Card card) {
                }
            });
            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    if (mBox instanceof FoldableBox) {
                        FoldableBox box = (FoldableBox) mBox;
                        View v = view.findViewById(R.id.card_item_layout_simple);
                        if (v != null && v.getVisibility() == View.VISIBLE) {
                            box.openDetails(v, mData);
                        } else {
                            box.openDetails(view.findViewById(R.id.card_item_layout_large), mData);
                        }
                    }
                }
            });
        }

        public String getDataType() {
            return mData.getDataType();
        }

        public ServerImageData getData() {
            return mData;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);
        }
    }

    public void openDetails(View coverView, ServerImageData data) {
        String type = data.getDataType();
        if (TextUtils.isEmpty(type)) {
            return;
        }
        if (type.equals(ServerDataMapping.S_DATATYPE_HTML)) {
            HtmlBox htmlBox = new HtmlBox(HtmlBox.convertFormServerImageData(data));
            View v = htmlBox.getRenderedView();
            renderDetailView(v);
        } else if (type.equals(ServerDataMapping.S_DATATYPE_GIF)) {
            GifBox box = new GifBox(mContext, GifBox.convertFormServerImageData(data));
            View v = box.getRenderedView();
            renderDetailView(v);
        } else if (type.equals(ServerDataMapping.S_DATATYPE_NEWS)
                || type.equals(ServerDataMapping.S_DATATYPE_JOKE)) {
            SingleImageBox box = new SingleImageBox(mContext,
                    SingleImageBox.convertFromServerData(data));

            View view = box.getRenderedView();
            view.findViewById(R.id.single_img).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mUnfoldableView.foldBack();
                }
            });
            view.findViewById(R.id.pandora_box_single_back_btn).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mUnfoldableView.foldBack();
                }
            });
            renderDetailView(view);
        } else {
            mTitleView.setText(data.getTitle());
            mContentView.setText(data.getImageDesc());
            Bitmap bmp = DiskImageHelper.getBitmapByUrl(data.getUrl(), null);
            resizeImageViewForImage(mImageView, bmp);
            mImageView.setImageBitmap(bmp);
        }
        mUnfoldableView.unfold(coverView, mDetailLayout);
    }

    private void renderDetailView(View contentView) {
        mContentContainerView.removeAllViews();
        mContentContainerView.addView(contentView, 0);
    }

    private void resizeImageViewForImage(ImageView imageView, Bitmap bmp) {
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        if (bmp != null) {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int viewWidth = imageView.getWidth();
            float rate = (float) viewWidth / (float) width;
            height = (int) ((float) height * rate);
            lp.height = height;
            imageView.setLayoutParams(lp);
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onUnfolding(UnfoldableView unfoldableView) {
        mListTouchInterceptor.setClickable(true);
        mDetailLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUnfolded(UnfoldableView unfoldableView) {
        mListTouchInterceptor.setClickable(false);

    }

    @Override
    public void onFoldingBack(UnfoldableView unfoldableView) {
        mListTouchInterceptor.setClickable(true);
    }

    @Override
    public void onFoldedBack(UnfoldableView unfoldableView) {
        mListTouchInterceptor.setClickable(false);
        mDetailLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFoldProgress(UnfoldableView unfoldableView, float progress) {
        // TODO Auto-generated method stub

    }
}
