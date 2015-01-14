
package cn.zmdx.kaka.locker.meiwen.content.box;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.meiwen.LockScreenManager;
import cn.zmdx.kaka.locker.meiwen.LockScreenManager.IMainPanelListener;
import cn.zmdx.kaka.locker.meiwen.LockScreenManager.OnBackPressedListener;
import cn.zmdx.kaka.locker.meiwen.Res;
import cn.zmdx.kaka.locker.meiwen.content.FoldableCard;
import cn.zmdx.kaka.locker.meiwen.content.PandoraBoxDispatcher;
import cn.zmdx.kaka.locker.meiwen.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.meiwen.content.ServerDataMapping;
import cn.zmdx.kaka.locker.meiwen.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.meiwen.content.favorites.FavoritesManager;
import cn.zmdx.kaka.locker.meiwen.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.meiwen.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.meiwen.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.meiwen.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.meiwen.share.PandoraShareManager;
import cn.zmdx.kaka.locker.meiwen.utils.HDBThreadUtils;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.alexvasilkov.foldablelayout.UnfoldableView.OnFoldingListener;
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading;
import com.nineoldandroids.animation.ObjectAnimator;

public class FoldablePage implements IFoldablePage, OnFoldingListener, View.OnClickListener,
        OnRefreshListener {
    private Context mContext;

    private List<ServerImageData> mData;

    private CardListView mListView;

    private ImageButton mImageButtonBack;

    private ImageButton mImageButtonCollect;

    private UnfoldableView mUnfoldableView;

    private ViewGroup mDetailLayout, mContentContainerView, mFrameLayout;

    private View mContainerView, mListTouchInterceptor;

    private FoldableBoxAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

//    private ImageView mMenuIcon, mShareIcon;

    private ViewStub mShareViewStub;

    private View mShareViewLayout;

    private LinearLayout mShareWechatCircle, mShareWechat, mShareSina, mShareQQ;

    private ServerImageData mCurCardData;

    public FoldablePage(Context context, List<ServerImageData> cards) {
        mContext = context;
        mData = cards;
        LockScreenManager.getInstance().registBackPressedListener(mBackPressedListener);
        LockScreenManager.getInstance().registMainPanelListener(mMainPanelListener);
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
        if (mAdapter.getCount() <= 0) {
            return null;
        }
        mContainerView = LayoutInflater.from(mContext).inflate(
                Res.layout.pandora_flodable_box_layout, null);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mContainerView
                .findViewById(Res.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mFrameLayout = (ViewGroup) mContainerView.findViewById(Res.id.frameLayout);
        mListView = (CardListView) mContainerView.findViewById(Res.id.cardListView);
        mUnfoldableView = (UnfoldableView) mContainerView.findViewById(Res.id.unfoldable_view);
        mUnfoldableView.setOnFoldingListener(this);
        mContentContainerView = (ViewGroup) mContainerView.findViewById(Res.id.cardDetailContainer);
        Bitmap glance = ((BitmapDrawable) mContext.getResources().getDrawable(
                Res.drawable.unfold_glance)).getBitmap();
        mUnfoldableView.setFoldShading(new GlanceFoldShading(mContext, glance));
        mUnfoldableView.setGesturesEnabled(false);
        mDetailLayout = (ViewGroup) mContainerView.findViewById(Res.id.cardDetailLayout);
        mDetailLayout.setVisibility(View.INVISIBLE);
        mListTouchInterceptor = mContainerView.findViewById(Res.id.touch_interceptor_view);
        mListTouchInterceptor.setClickable(false);
        mListView.setAdapter(mAdapter);
        mImageButtonBack = (ImageButton) mContainerView.findViewById(Res.id.toolbar_imgButtonBack);
        mImageButtonCollect = (ImageButton) mContainerView
                .findViewById(Res.id.toolbar_imgButtonCollect);
        mImageButtonBack.setOnClickListener(this);
        mImageButtonCollect.setOnClickListener(this);

        mShareViewStub = (ViewStub) mContainerView
                .findViewById(Res.id.pandora_card_share_view_stub);
//        mMenuIcon = (ImageView) mContainerView.findViewById(Res.id.pandora_function_menu);
//        mMenuIcon.setOnClickListener(this);
//        mShareIcon = (ImageView) mContainerView.findViewById(Res.id.pandora_share);
//        mShareIcon.setOnClickListener(this);

        createGuidePageIfNeed();
        return mContainerView;

    }

    private boolean mGuidePageVisibility = true;

    public void setGuidePageVisibility(boolean visibility) {
        mGuidePageVisibility = visibility;
    }

    private ObjectAnimator mFingerAnim;

    private void createGuidePageIfNeed() {
        if (!mGuidePageVisibility) {
            return;
        }
        if (!PandoraConfig.newInstance(mContext).getFlagDisplayBoxGuide()) {
            final View guideView = mContainerView
                    .findViewById(Res.id.card_item_layout_guide_finger);
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
        mAdapter = adapter;
    }

    public CardArrayAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onFinish() {
        try {
            LockScreenManager.getInstance().unRegistBackPressedListener(mBackPressedListener);
            LockScreenManager.getInstance().unRegistMainPanelListener(mMainPanelListener);
        } catch (Exception e) {
        }
        if (mFingerAnim != null) {
            mFingerAnim.cancel();
            mFingerAnim = null;
        }
    }

    /**
     * 锁屏页右划解锁的监听器
     */
    private IMainPanelListener mMainPanelListener = new IMainPanelListener() {

        @Override
        public void onMainPanelOpened() {

        }

        @Override
        public void onMainPanelClosed() {
//            resetState();
        }

    };

    private OnBackPressedListener mBackPressedListener = new OnBackPressedListener() {

        @Override
        public void onBackPressed() {
            if (mUnfoldableView != null && mUnfoldableView.isUnfolded()) {
                foldBack();
            }
        }
    };

    public boolean isDataValidate() {
        if (mData != null && mData.size() > 0) {
            int id = mData.get(0).getId();
            ServerImageData sid = ServerImageDataModel.getInstance().queryById(id);
            return sid != null;
        }
        return true;
    }

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
        mCurCardData = data;
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

    public boolean isFoldBack() {
        if (mUnfoldableView == null) {
            return false;
        }
        return mUnfoldableView.isUnfolded();
    }

    private void renderDetailView(View contentView, int id) {
        mContentContainerView.removeAllViews();
        mContentContainerView.addView(contentView, 0);
        mImageButtonCollect.setTag(id);
        boolean favoritedState = isFavoritedState(id);
        setButtonCollectState(favoritedState);
    }

    private boolean isFavoritedState(int id) {
        boolean favorited = mFavoritesManager.isFavorited(String.valueOf(id));
        return favorited;
    }

    private void setButtonCollectState(boolean favorited) {
        if (favorited) {
            mImageButtonCollect.setBackgroundResource(Res.drawable.pandora_share_icon_collected);
        } else {
            mImageButtonCollect
                    .setBackgroundResource(Res.drawable.pandora_card_collect_button_selector);
        }
    }

    FavoritesManager mFavoritesManager = new FavoritesManager(mContext);

    boolean isOperating = false;

    @Override
    public void onClick(View v) {
        if (v == mImageButtonBack) {
            foldBack();
        } else if (v == mImageButtonCollect) {
            if (isOperating)
                return;
            isOperating = true;
            int id = (Integer) v.getTag();
            boolean isFavorited = isFavoritedState(id);
            boolean result = !isFavorited;
            ServerImageDataModel mServerImageDataModel = ServerImageDataModel.getInstance();
            boolean markIsFavorited = mServerImageDataModel.markIsFavorited(id, result);
            ServerImageData mServerImageData = mServerImageDataModel.queryById(id);
            String cloudId = mServerImageData.getCloudId();
            if (markIsFavorited) {
                UmengCustomEventManager.statisticalCardIsFavorited(cloudId);
            }
            setButtonCollectState(result);
            isOperating = false;
        } 
//        else if (v == mMenuIcon) {
//            if (isShareLayoutVisible) {
//                mImageButtonCollect.setVisibility(View.GONE);
//                mShareIcon.setVisibility(View.GONE);
//                mShareViewStub.setVisibility(View.GONE);
//                isMenuVisible = false;
//            } else {
//                mImageButtonCollect.setVisibility(View.VISIBLE);
//                mShareIcon.setVisibility(View.VISIBLE);
//            }
//            isShareLayoutVisible = !isShareLayoutVisible;
//        } else if (v == mShareIcon) {
//            if (isMenuVisible) {
//                mShareViewStub.setVisibility(View.GONE);
//            } else {
//                if (null == mShareViewLayout) {
//                    initShareView();
//                }
//                mShareViewStub.setVisibility(View.VISIBLE);
//            }
//            isMenuVisible = !isMenuVisible;
//        }
    }

    private boolean isShareLayoutVisible = false;

    private boolean isMenuVisible = false;

//    private void setShareViewGone() {
//        mImageButtonCollect.setVisibility(View.GONE);
//        mShareIcon.setVisibility(View.GONE);
//        mShareViewStub.setVisibility(View.GONE);
//    }

    @SuppressLint("ClickableViewAccessibility")
    private void initShareView() {
        mShareViewLayout = mShareViewStub.inflate();
        if (PandoraShareManager.isAvilible(mContext, PandoraShareManager.PACKAGE_SINA_STRING)) {
            mShareSina = (LinearLayout) mShareViewLayout.findViewById(Res.id.pandora_share_sina);
            mShareSina.setOnTouchListener(mShareTouchListener);
            mShareSina.setVisibility(View.VISIBLE);
        }
        if (PandoraShareManager.isAvilible(mContext, PandoraShareManager.PACKAGE_WECHAR_STRING)) {
            mShareWechatCircle = (LinearLayout) mShareViewLayout
                    .findViewById(Res.id.pandora_share_wechat_circle);
            mShareWechatCircle.setOnTouchListener(mShareTouchListener);
            mShareWechat = (LinearLayout) mShareViewLayout
                    .findViewById(Res.id.pandora_share_wechat);
            mShareWechat.setOnTouchListener(mShareTouchListener);
            mShareWechatCircle.setVisibility(View.VISIBLE);
            mShareWechat.setVisibility(View.VISIBLE);
        }
        if (PandoraShareManager.isAvilible(mContext, PandoraShareManager.PACKAGE_QQ_STRING)) {
            mShareQQ = (LinearLayout) mShareViewLayout.findViewById(Res.id.pandora_share_qq);
            mShareQQ.setOnTouchListener(mShareTouchListener);
            mShareQQ.setVisibility(View.VISIBLE);
        }

    }

    private OnTouchListener mShareTouchListener = new OnTouchListener() {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            view.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (view == mShareWechatCircle) {
                    mShareWechatCircle.setBackgroundResource(Res.drawable.pandora_share_line_press);
                    PandoraShareManager.shareContent(mContext, mCurCardData,
                            PandoraShareManager.TYPE_SHARE_WECHAT_CIRCLE);
                } else if (view == mShareWechat) {
                    mShareWechat.setBackgroundResource(Res.drawable.pandora_share_line_press);
                    PandoraShareManager.shareContent(mContext, mCurCardData,
                            PandoraShareManager.TYPE_SHARE_WECHAT);
                } else if (view == mShareSina) {
                    mShareSina.setBackgroundResource(Res.drawable.pandora_share_line_press);
                    PandoraShareManager.shareContent(mContext, mCurCardData,
                            PandoraShareManager.TYPE_SHARE_SINA);
                } else if (view == mShareQQ) {
                    mShareQQ.setBackgroundResource(Res.drawable.pandora_share_line_press);
                    PandoraShareManager.shareContent(mContext, mCurCardData,
                            PandoraShareManager.TYPE_SHARE_QQ);
                }
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                resetState();
            }
            return true;
        }
    };

    private void resetState() {
        if (null != mShareWechatCircle) {
            mShareWechatCircle.setBackgroundResource(Res.drawable.pandora_share_line_normal);
        }
        if (null != mShareWechat) {
            mShareWechat.setBackgroundResource(Res.drawable.pandora_share_line_normal);
        }
        if (null != mShareSina) {
            mShareSina.setBackgroundResource(Res.drawable.pandora_share_line_normal);
        }
        if (null != mShareQQ) {
            mShareQQ.setBackgroundResource(Res.drawable.pandora_share_line_normal);
        }
        LockScreenManager.getInstance().setRunnableAfterUnLock(null);
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
//        isMenuVisible = false;
//        isShareLayoutVisible = false;
//        setShareViewGone();
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
