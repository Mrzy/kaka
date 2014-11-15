
package cn.zmdx.kaka.locker.content.box;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.content.ServerDataMapping;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.alexvasilkov.foldablelayout.UnfoldableView.OnFoldingListener;
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading;
import com.nineoldandroids.animation.ObjectAnimator;

public class FoldablePage implements IFoldableBox, OnFoldingListener, View.OnClickListener {
    private Context mContext;

    private List<ServerImageData> mData;

    private CardListView mListView;

    private UnfoldableView mUnfoldableView;

    private ViewGroup mDetailLayout, mContentContainerView, mFrameLayout;

    private View mContainerView, mListTouchInterceptor;

    private CardArrayAdapter mAdapter;

    public FoldablePage(Context context, List<ServerImageData> cards) {
        mContext = context;
        mData = cards;
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
        if (mData == null || mData.size() <= 0) {
            return null;
        }
        mContainerView = LayoutInflater.from(mContext).inflate(
                R.layout.pandora_flodable_box_layout, null);
        mFrameLayout = (ViewGroup) mContainerView.findViewById(R.id.frameLayout);
        mListView = (CardListView) mContainerView.findViewById(R.id.cardListView);
        mUnfoldableView = (UnfoldableView) mContainerView.findViewById(R.id.unfoldable_view);
        mUnfoldableView.setOnFoldingListener(this);
        mContentContainerView = (ViewGroup) mContainerView.findViewById(R.id.cardDetailContainer);
        Bitmap glance = ((BitmapDrawable) mContext.getResources().getDrawable(
                R.drawable.unfold_glance)).getBitmap();
        mUnfoldableView.setFoldShading(new GlanceFoldShading(mContext, glance));
        mUnfoldableView.setGesturesEnabled(false);
        mDetailLayout = (ViewGroup) mContainerView.findViewById(R.id.cardDetailLayout);
        mDetailLayout.setVisibility(View.INVISIBLE);
        mListTouchInterceptor = mContainerView.findViewById(R.id.touch_interceptor_view);
        mListTouchInterceptor.setClickable(false);
        // mAdapter = new FoldableBoxAdapter(mContext, cards);
        mAdapter.registerDataSetObserver(mObserver);
        mListView.setAdapter(mAdapter);
        createGuidePageIfNeed();
        return mContainerView;
    }

    private void createGuidePageIfNeed() {
        if (!PandoraConfig.newInstance(mContext).getFlagDisplayBoxGuide()) {
            final View guideView = mContainerView.findViewById(R.id.card_item_layout_guide_finger);
            guideView.setVisibility(View.VISIBLE);
            View fingerView = mContainerView.findViewById(R.id.guide_finger);
            final ObjectAnimator anim = ObjectAnimator.ofFloat(fingerView, "translationX",
                    BaseInfoHelper.dip2px(mContext, 100));
            anim.setRepeatCount(-1);
            anim.setDuration(1500);
            anim.start();
            guideView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    anim.cancel();
                    guideView.setVisibility(View.GONE);
                }
            });
        }
    }

    public List<Card> makeCardList(List<ServerImageData> oriData) {
        List<Card> cards = new ArrayList<Card>();
        int size = oriData.size();
        FoldableCard card = null;
        for (int i = 0; i < size; i++) {
            card = new FoldableCard(mContext, this, oriData.get(i));
            cards.add(card);
        }
        return cards;
    }

    public void setAdapter(CardArrayAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        mAdapter = adapter;
    }

    public CardArrayAdapter getAdatper() {
        return mAdapter;
    }

    @Override
    public void onFinish() {
        mAdapter.unregisterDataSetObserver(mObserver);
    }

    private DataSetObserver mObserver = new DataSetObserver() {
        public void onChanged() {
            int count = mAdapter.getCount();
            if (count == 0) {
                fadeInEmptyView();
                PandoraConfig.newInstance(mContext).saveHasAlreadyDisplayBoxGuide();
            }
        };
    };

    private void fadeInEmptyView() {
        IPandoraBox box = PandoraBoxManager.newInstance(mContext).getDefaultBox();
        View defaultView = box.getRenderedView();
        defaultView.setAlpha(0);
        mFrameLayout.addView(defaultView);
        defaultView.animate().alpha(1).setDuration(500).start();
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
                    FoldableCard fCard = ((FoldableCard) card);
                    ServerImageData data = fCard.getData();
                    int id = data.getId();
                    ServerImageDataModel.getInstance().markRead(id, true);
                }
            });
            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    if (mBox instanceof FoldablePage) {
                        FoldablePage box = (FoldablePage) mBox;
                        FoldableCard fCard = (FoldableCard) card;
                        if (fCard.getDataType().equals(ServerDataMapping.S_DATATYPE_GUIDE)) {
                            return;
                        }
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
            HtmlBox htmlBox = new HtmlBox(mContext, this, HtmlBox.convertFormServerImageData(data));
            View v = htmlBox.getRenderedView();
            renderDetailView(v);
        } else if (type.equals(ServerDataMapping.S_DATATYPE_GIF)) {
            GifBox box = new GifBox(mContext, this, GifBox.convertFormServerImageData(data));
            View v = box.getRenderedView();
            if (v != null) {
                renderDetailView(v);
                box.startGif();
            }
        } else if (type.equals(ServerDataMapping.S_DATATYPE_NEWS)
                || type.equals(ServerDataMapping.S_DATATYPE_JOKE)) {
            SingleImageBox box = new SingleImageBox(mContext, this,
                    SingleImageBox.convertFromServerData(data));
            View view = box.getRenderedView();
            if (view != null) {
                renderDetailView(view);
            }
        } else if (type.equals(ServerDataMapping.S_DATATYPE_GUIDE)) {

        }
        mUnfoldableView.unfold(coverView, mDetailLayout);
    }

    public void foldBack() {
        mUnfoldableView.foldBack();
    }

    private void renderDetailView(View contentView) {
        mContentContainerView.removeAllViews();
        mContentContainerView.addView(contentView, 0);
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
    }
}
