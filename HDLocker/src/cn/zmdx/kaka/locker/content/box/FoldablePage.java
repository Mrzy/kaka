
package cn.zmdx.kaka.locker.content.box;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.LockScreenManager.OnBackPressedListener;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.content.ServerDataMapping;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.alexvasilkov.foldablelayout.UnfoldableView.OnFoldingListener;
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading;
import com.nineoldandroids.animation.ObjectAnimator;

public class FoldablePage implements IFoldableBox, OnFoldingListener, View.OnClickListener,
        OnRefreshListener {
    private Context mContext;

    private List<ServerImageData> mData;

    private CardListView mListView;

    private UnfoldableView mUnfoldableView;

    private ViewGroup mDetailLayout, mContentContainerView, mFrameLayout;

    private View mContainerView, mListTouchInterceptor;

    private FoldableBoxAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public FoldablePage(Context context, List<ServerImageData> cards) {
        mContext = context;
        mData = cards;
        LockScreenManager.getInstance().registBackPressedListener(mBackPressedListener);
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
        mSwipeRefreshLayout = (SwipeRefreshLayout) mContainerView
                .findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
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
        mListView.setAdapter(mAdapter);
        createGuidePageIfNeed();
        return mContainerView;
    }

    private ObjectAnimator mFingerAnim;

    private void createGuidePageIfNeed() {
        if (!PandoraConfig.newInstance(mContext).getFlagDisplayBoxGuide()) {
            final View guideView = mContainerView.findViewById(R.id.card_item_layout_guide_finger);
            guideView.setVisibility(View.VISIBLE);
            guideView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guideView.setVisibility(View.GONE);
                    PandoraConfig.newInstance(mContext).saveHasAlreadyDisplayBoxGuide();
                }
            });
        }
    }

    public void removeItemsByCategory(String category) {
        if (mAdapter == null || TextUtils.isEmpty(category)) {
            return;
        }

        List<Card> locData = mAdapter.getCardsData();
        if (locData != null && locData.size() > 0) {
            int length = locData.size();
            for (int i = length - 1; i >= 0; i--) {
                FoldableCard card = (FoldableCard) locData.get(i);
                if (category.equals(card.getDataType())) {
                    locData.remove(i);
                }
            }
            mAdapter.notifyDataSetChanged();
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

    public void setAdapter(FoldableBoxAdapter adapter) {
        if (mAdapter != null) {
            try {
                mAdapter.unregisterDataSetObserver(mObserver);
            } catch (Exception e) {
            }
        }
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mObserver);
    }

    public CardArrayAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onFinish() {
        try {
            mAdapter.unregisterDataSetObserver(mObserver);
            LockScreenManager.getInstance().unRegistBackPressedListener(mBackPressedListener);
        } catch (Exception e) {
        }
        if (mFingerAnim != null) {
            mFingerAnim.cancel();
            mFingerAnim = null;
        }
    }

    private OnBackPressedListener mBackPressedListener = new OnBackPressedListener() {

        @Override
        public void onBackPressed() {
            if (mUnfoldableView.isUnfolded()) {
                foldBack();
            }
        }
    };

    public boolean isTodayData() {
        if (mData != null && mData.size() > 0) {
            int id = mData.get(0).getId();
            ServerImageData sid = ServerImageDataModel.getInstance().queryById(id);
            return sid != null;
        }
        return true;
    }

    private DataSetObserver mObserver = new DataSetObserver() {
        public void onChanged() {
            int count = mAdapter.getCount();
            if (count == 0) {
                fadeInEmptyView();
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

    public static void markRead(Card card) {
        FoldableCard fCard = ((FoldableCard) card);
        ServerImageData data = fCard.getData();
        int id = data.getId();
        ServerImageDataModel.getInstance().markRead(id, true);
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
                    markRead(card);
                }
            });
            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    if (mBox instanceof FoldablePage) {
                        FoldablePage box = (FoldablePage) mBox;
                        box.openDetails(view.findViewById(R.id.card_item_layout_large), mData);
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

    @Override
    public void onRefresh() {
        HDBThreadUtils.postOnUiDelayed(mUpdateCardRunnable, 1000);
    }

    private Runnable mUpdateCardRunnable = new Runnable() {

        @Override
        public void run() {
            mSwipeRefreshLayout.setRefreshing(false);
            removeAllCardWithAnimation(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    changeNextGroupCard();
                };
            });
        }
    };

    /**
     * 换一批新的资讯
     */
    private void changeNextGroupCard() {
        final List<ServerImageData> data = PandoraBoxManager.newInstance(mContext)
                .getDataFormLocalDB(PandoraPolicy.MIN_COUNT_FOLDABLE_BOX);
        if (data.size() <= 0) {
            fadeInEmptyView();
            return;
        }
        mAdapter = null;
        mAdapter = new FoldableBoxAdapter(mContext, makeCardList(data));
        mListView.setAdapter(mAdapter);
        mListView.startLayoutAnimation();
    }

    private void removeAllCardWithAnimation(AnimatorListener listener) {
        int firstPos = mListView.getFirstVisiblePosition();
        int lastPos = mListView.getLastVisiblePosition();
        int i = firstPos;
        int delay = 0;
        while (i <= lastPos) {
            FoldableCard card = (FoldableCard) getAdapter().getItem(i);
            if (i == lastPos) {
                card.doSwipeOut(true, 300, delay, listener);
            } else {
                card.doSwipeOut(true, 300, delay, null);
            }
            markRead(card);
            delay += 50;
            i++;
        }
    }
}
