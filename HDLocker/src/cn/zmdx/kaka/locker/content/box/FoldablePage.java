
package cn.zmdx.kaka.locker.content.box;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.LockScreenManager.OnBackPressedListener;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.FoldableCard;
import cn.zmdx.kaka.locker.content.PandoraBoxDispatcher;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.content.ServerDataMapping;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.favorites.FavoritesManager;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
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

    private TextView tvEmpty;

    private ImageButton mImageButtonBack;

    private ImageButton mImageButtonCollect;

    private UnfoldableView mUnfoldableView;

    private ViewGroup mDetailLayout, mContentContainerView, mFrameLayout;

    private View mContainerView, mListTouchInterceptor;

    private FoldableBoxAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int imgButtonCollectClickCount = 0;// 判断点击收藏按钮的次数

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
        mImageButtonBack = (ImageButton) mContainerView.findViewById(R.id.toolbar_imgButtonBack);
        mImageButtonCollect = (ImageButton) mContainerView
                .findViewById(R.id.toolbar_imgButtonCollect);
        mImageButtonBack.setOnClickListener(this);
        mImageButtonCollect.setOnClickListener(this);
        if (mData == null || mData.size() <= 0) {
            tvEmpty = (TextView) mContainerView.findViewById(R.id.tvEmpty);
            tvEmpty.setText("暂无收藏");
            mListView.setEmptyView(tvEmpty);
        }
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

    public void setSwipeRefreshEnabled(boolean enabled) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enabled);
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
            if (mUnfoldableView != null && mUnfoldableView.isUnfolded()) {
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

    public void openDetails(View coverView, ServerImageData data) {
        String type = data.getDataType();
        if (TextUtils.isEmpty(type)) {
            return;
        }
        final int id = data.getId();

        if (type.equals(ServerDataMapping.S_DATATYPE_HTML)) {
            HtmlBox htmlBox = new HtmlBox(mContext, this, HtmlBox.convertFormServerImageData(data));
            View v = htmlBox.getRenderedView();
            renderDetailView(v, id);
        } else if (type.equals(ServerDataMapping.S_DATATYPE_GIF)) {
            GifBox box = new GifBox(mContext, this, GifBox.convertFormServerImageData(data));
            View v = box.getRenderedView();
            if (v != null) {
                renderDetailView(v, id);
                box.startGif();
            }
        } else if (type.equals(ServerDataMapping.S_DATATYPE_NEWS)
                || type.equals(ServerDataMapping.S_DATATYPE_JOKE)
                || type.equals(ServerDataMapping.S_DATATYPE_SINGLEIMG)) {
            SingleImageBox box = new SingleImageBox(mContext, this,
                    SingleImageBox.convertFromServerData(data));
            View view = box.getRenderedView();
            if (view != null) {
                renderDetailView(view, id);
            }
        } else if (type.equals(ServerDataMapping.S_DATATYPE_MULTIIMG)) {
            final MultiImgBox box = new MultiImgBox(mContext, this,
                    MultiImgBox.convertToMultiBox(data));
            View view = box.getRenderedView();
            if (view != null) {
                renderDetailView(view, id);
            }
        }
        mUnfoldableView.unfold(coverView, mDetailLayout);
    }

    public void foldBack() {
        mUnfoldableView.foldBack();
    }

    private void renderDetailView(View contentView, int id) {
        mContentContainerView.removeAllViews();
        mContentContainerView.addView(contentView, 0);
        mImageButtonCollect.setTag(id);
        isFavoritedState(id);
    }

    private boolean isFavoritedState(int id) {
        boolean favorited = manager.isFavorited(String.valueOf(id));
        if (favorited) {
            mImageButtonCollect.setBackgroundResource(R.drawable.pandora_card_collected);
            favorited = true;
        } else {
            mImageButtonCollect
                    .setBackgroundResource(R.drawable.pandora_card_collect_button_selector);
            favorited = false;
        }
        return favorited;
    }

    FavoritesManager manager = new FavoritesManager(mContext);

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        // 返回按钮
            case R.id.toolbar_imgButtonBack:
                foldBack();
                break;
            // 收藏按钮
            case R.id.toolbar_imgButtonCollect:
                int id = (Integer) v.getTag();
                boolean isFavorited = isFavoritedState(id);
                ++imgButtonCollectClickCount;
                judgeFavoriteButtonState(id, isFavorited);
                break;
        }
    }

    private void judgeFavoriteButtonState(int id, boolean isFavorited) {
        if (isFavorited) {
            mImageButtonCollect
                    .setBackgroundResource(R.drawable.pandora_card_collect_button_selector);
            boolean removeFavorite = manager.removeFavorite(String.valueOf(id));
            if (removeFavorite) {
                Toast.makeText(mContext, "取消收藏" + id, Toast.LENGTH_SHORT).show();
            } else if (removeFavorite && imgButtonCollectClickCount % 2 == 0) {
                mImageButtonCollect.setBackgroundResource(R.drawable.pandora_card_collected);
                boolean addFavorite = manager.addFavorite(String.valueOf(id));
                if (addFavorite) {
                    Toast.makeText(mContext, "收藏成功" + id, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            mImageButtonCollect.setBackgroundResource(R.drawable.pandora_card_collected);
            boolean addFavorite = manager.addFavorite(String.valueOf(id));
            if (addFavorite) {
                Toast.makeText(mContext, "收藏成功" + id, Toast.LENGTH_SHORT).show();
            } else if (addFavorite && imgButtonCollectClickCount % 2 == 0) {
                mImageButtonCollect
                        .setBackgroundResource(R.drawable.pandora_card_collect_button_selector);
                boolean removeFavorite = manager.removeFavorite(String.valueOf(id));
                if (removeFavorite) {
                    Toast.makeText(mContext, "取消收藏" + id, Toast.LENGTH_SHORT).show();
                }
            }
        }
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
        UmengCustomEventManager.statisticalPullToRefreshTimes();
    }

    private Runnable mUpdateCardRunnable = new Runnable() {

        @Override
        public void run() {
            mSwipeRefreshLayout.setRefreshing(false);
            removeAllCardWithAnimation(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    // 将刚刚移除的卡片新闻全部标记为已读
                    List<Card> cards = mAdapter.getCardsData();
                    for (Card card : cards) {
                        markRead(card);
                    }
                    PandoraBoxDispatcher.getInstance().pullData();
                    // 更换一批新的数据
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
            delay += 50;
            i++;
        }
    }
}
