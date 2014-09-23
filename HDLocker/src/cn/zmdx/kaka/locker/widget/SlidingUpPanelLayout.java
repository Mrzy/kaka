
package cn.zmdx.kaka.locker.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;

import com.nineoldandroids.view.animation.AnimatorProxy;

public class SlidingUpPanelLayout extends ViewGroup {

    private static final String TAG = SlidingUpPanelLayout.class.getSimpleName();

    private static final int MIN_DURATION_TO_FIXED = 300;

    /**
     * Default peeking out panel height
     */
    private static final int DEFAULT_PANEL_HEIGHT = 68; // dp;

    private static final int DEFAULT_TOP_PANEL_HEIGHT = 50;// dp

    /**
     * Default anchor point height
     */
    private static final float DEFAULT_ANCHOR_POINT = 1.0f; // In relative %

    /**
     * Default initial state for the component
     */
    private static SlideState DEFAULT_SLIDE_STATE = SlideState.EXPANDED;

    /**
     * Default height of the shadow above the peeking out panel
     */
    private static final int DEFAULT_SHADOW_HEIGHT = 4; // dp;

    /**
     * If no fade color is given by default it will fade to 80% gray.
     */
    private static final int DEFAULT_FADE_COLOR = 0x99000000;

    /**
     * Default Minimum velocity that will be detected as a fling
     */
    private static final int DEFAULT_MIN_FLING_VELOCITY = 400; // dips per
                                                               // second

    /**
     * Default is set to false because that is how it was written
     */
    private static final boolean DEFAULT_OVERLAY_FLAG = false;

    /**
     * Default attributes for layout
     */
    private static final int[] DEFAULT_ATTRS = new int[] {
        android.R.attr.gravity
    };

    /**
     * Minimum velocity that will be detected as a fling
     */
    private int mMinFlingVelocity = DEFAULT_MIN_FLING_VELOCITY;

    /**
     * The fade color used for the panel covered by the slider. 0 = no fading.
     */
    private int mCoveredFadeColor = DEFAULT_FADE_COLOR;

    /**
     * Default paralax length of the main view
     */
    private static final int DEFAULT_PARALAX_OFFSET = 0;

    /**
     * The paint used to dim the main layout when sliding
     */
    private final Paint mCoveredFadePaint = new Paint();

    /**
     * Drawable used to draw the shadow between panes.
     */
    private final Drawable mTopShadowDrawable, mBottomShadowDrawable;

    /**
     * The size of the overhang in pixels.
     */
    private int mPanelHeight = -1;

    private Drawable mForegroundDrawable = null;

    private Drawable mTopPanelBgDrawable;

    private Drawable mBottomPanelBgDrawable;

    private int mTopViewSeekOutHeight = -1;

    /**
     * The size of the shadow in pixels.
     */
    private int mShadowHeight = -1;

    /**
     * Paralax offset
     */
    private int mParallaxOffset = -1;

    /**
     * True if the collapsed panel should be dragged up.
     */
    private boolean mIsSlidingUp;

    /**
     * Panel overlays the windows instead of putting it underneath it.
     */
    private boolean mOverlayContent = DEFAULT_OVERLAY_FLAG;

    /**
     * If provided, the panel can be dragged by only this view. Otherwise, the
     * entire panel can be used for dragging.
     */
    private View mDragView;

    /**
     * If provided, the panel can be dragged by only this view. Otherwise, the
     * entire panel can be used for dragging.
     */
    private int mDragViewResId = -1;

    /**
     * The child view that can slide, if any.
     */
    private View mSlideableView;

    /**
     * The main view
     */
    private View mMainView;

    /**
     * The child view that position up
     */
    private View mTopView;

    /**
     * Current state of the slideable view.
     */
    private enum SlideState {
        EXPANDED, COLLAPSED, ANCHORED, HIDDEN, DRAGGING
    }

    private SlideState mSlideState = SlideState.EXPANDED;

    /**
     * How far the panel is offset from its expanded position. range [0, 1]
     * where 0 = collapsed, 1 = expanded.
     */
    private float mSlideOffset;

    /**
     * How far in pixels the slideable panel may move.
     */
    private int mSlideRange;

    private int mTopRange;

    /**
     * A panel view is locked into internal scrolling or another condition that
     * is preventing a drag.
     */
    private boolean mIsUnableToDrag;

    /**
     * 当滑动到页面底部停止时的开始时间
     */
    private long mPressOnStartTime;

    /**
     * 按住面板停住时，是否已经开始计时的标记
     */
    private boolean mIsStartKeepTime = false;

    /**
     * 当前面板是否已被固定住的标记
     */
    private boolean mIsFixed = false;

    /**
     * Flag indicating that sliding feature is enabled\disabled
     */
    private boolean mIsSlidingEnabled;

    /**
     * Flag indicating if a drag view can have its own touch events. If set to
     * true, a drag view can scroll horizontally and have its own click
     * listener. Default is set to false.
     */
    private boolean mIsUsingDragViewTouchEvents;

    private float mInitialMotionX;

    private float mInitialMotionY;

    private float mAnchorPoint = 1.f;

    private PanelSlideListener mPanelSlideListener;

    private final ViewDragHelper mDragHelper;

    private Context mContext;

    /**
     * Stores whether or not the pane was expanded the last time it was
     * slideable. If expand/collapse operations are invoked this state is
     * modified. Used by instance state save/restore.
     */
    private boolean mFirstLayout = true;

    private final Rect mTmpRect = new Rect();

    /**
     * Listener for monitoring events about sliding panes.
     */
    public interface PanelSlideListener {
        /**
         * Called when a sliding pane's position changes.
         * 
         * @param panel The child view that was moved
         * @param slideOffset The new offset of this sliding pane within its
         *            range, from 0-1
         */
        public void onPanelSlide(View panel, float slideOffset);

        /**
         * Called when a sliding panel becomes slid completely collapsed.
         * 
         * @param panel The child view that was slid to an collapsed position
         */
        public void onPanelCollapsed(View panel);

        /**
         * Called when a sliding panel becomes slid completely expanded.
         * 
         * @param panel The child view that was slid to a expanded position
         */
        public void onPanelExpanded(View panel);

        /**
         * Called when a sliding panel becomes anchored.
         * 
         * @param panel The child view that was slid to a anchored position
         */
        public void onPanelAnchored(View panel);

        /**
         * Called when a sliding panel becomes completely hidden.
         * 
         * @param panel The child view that was slid to a hidden position
         */
        public void onPanelHidden(View panel);

        public void onPanelFixed(View panel);

        public void onPanelClickedDuringFixed();

        public void onPanelStartDown(View view);

        public void onPanelHiddenEnd();
    }

    /**
     * No-op stubs for {@link PanelSlideListener}. If you only want to implement
     * a subset of the listener methods you can extend this instead of implement
     * the full interface.
     */
    public static class SimplePanelSlideListener implements PanelSlideListener {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
        }

        @Override
        public void onPanelCollapsed(View panel) {
        }

        @Override
        public void onPanelExpanded(View panel) {
        }

        @Override
        public void onPanelAnchored(View panel) {
        }

        @Override
        public void onPanelHidden(View panel) {
        }

        @Override
        public void onPanelFixed(View panel) {
        }

        @Override
        public void onPanelClickedDuringFixed() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPanelStartDown(View view) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPanelHiddenEnd() {
            // TODO Auto-generated method stub

        }
    }

    public SlidingUpPanelLayout(Context context) {
        this(context, null);
    }

    public SlidingUpPanelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        if (isInEditMode()) {
            mTopShadowDrawable = null;
            mBottomShadowDrawable = null;
            mDragHelper = null;
            return;
        }

        if (attrs != null) {
            TypedArray defAttrs = context.obtainStyledAttributes(attrs, DEFAULT_ATTRS);

            if (defAttrs != null) {
                // int gravity = defAttrs.getInt(0, Gravity.NO_GRAVITY);
                // if (gravity != Gravity.TOP && gravity != Gravity.BOTTOM) {
                // throw new IllegalArgumentException(
                // "gravity must be set to either top or bottom");
                // }
                // only support Gravity.BOTTOM;
                mIsSlidingUp = true;
            }

            defAttrs.recycle();

            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingUpPanelLayout);

            if (ta != null) {
                mPanelHeight = ta.getDimensionPixelSize(
                        R.styleable.SlidingUpPanelLayout_panelHeight, -1);
                mTopViewSeekOutHeight = ta.getDimensionPixelSize(
                        R.styleable.SlidingUpPanelLayout_topSeekoutHeight, -1);
                mShadowHeight = ta.getDimensionPixelSize(
                        R.styleable.SlidingUpPanelLayout_shadowHeight, -1);
                mParallaxOffset = ta.getDimensionPixelSize(
                        R.styleable.SlidingUpPanelLayout_paralaxOffset, -1);

                mMinFlingVelocity = ta.getInt(R.styleable.SlidingUpPanelLayout_flingVelocity,
                        DEFAULT_MIN_FLING_VELOCITY);
                mCoveredFadeColor = ta.getColor(R.styleable.SlidingUpPanelLayout_fadeColor,
                        DEFAULT_FADE_COLOR);

                mDragViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_dragView, -1);

                mOverlayContent = ta.getBoolean(R.styleable.SlidingUpPanelLayout_overlay,
                        DEFAULT_OVERLAY_FLAG);

                mAnchorPoint = ta.getFloat(R.styleable.SlidingUpPanelLayout_anchorPoint,
                        DEFAULT_ANCHOR_POINT);

                mSlideState = SlideState.values()[ta.getInt(
                        R.styleable.SlidingUpPanelLayout_initialState,
                        DEFAULT_SLIDE_STATE.ordinal())];
                mForegroundDrawable = ta
                        .getDrawable(R.styleable.SlidingUpPanelLayout_foregroundDrawable);
            }

            ta.recycle();
        }

        final float density = context.getResources().getDisplayMetrics().density;
        if (mPanelHeight == -1) {
            mPanelHeight = (int) (DEFAULT_PANEL_HEIGHT * density + 0.5f);
        }
        if (mTopViewSeekOutHeight == -1) {
            mTopViewSeekOutHeight = (int) (DEFAULT_TOP_PANEL_HEIGHT * density + 0.5f);
        }
        if (mShadowHeight == -1) {
            mShadowHeight = (int) (DEFAULT_SHADOW_HEIGHT * density + 0.5f);
        }
        if (mParallaxOffset == -1) {
            mParallaxOffset = (int) (DEFAULT_PARALAX_OFFSET * density);
        }
        // If the shadow height is zero, don't show the shadow
        if (mShadowHeight > 0) {
            if (mIsSlidingUp) {
                mTopShadowDrawable = getResources().getDrawable(R.drawable.below_shadow);
                mBottomShadowDrawable = getResources().getDrawable(R.drawable.above_shadow);
            } else {
                // unused code
                mTopShadowDrawable = getResources().getDrawable(R.drawable.below_shadow);
                mBottomShadowDrawable = getResources().getDrawable(R.drawable.below_shadow);
            }

        } else {
            mTopShadowDrawable = null;
            mBottomShadowDrawable = null;
        }

        setWillNotDraw(false);

        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
        mDragHelper.setMinVelocity(mMinFlingVelocity * density);

        mIsSlidingEnabled = true;
    }

    /**
     * Set the Drag View after the view is inflated
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mDragViewResId != -1) {
            setDragView(findViewById(mDragViewResId));
        }
    }

    /**
     * Set the color used to fade the pane covered by the sliding pane out when
     * the pane will become fully covered in the expanded state.
     * 
     * @param color An ARGB-packed color value
     */
    public void setCoveredFadeColor(int color) {
        mCoveredFadeColor = color;
        invalidate();
    }

    /**
     * @return The ARGB-packed color value used to fade the fixed pane
     */
    public int getCoveredFadeColor() {
        return mCoveredFadeColor;
    }

    /**
     * Set sliding enabled flag
     * 
     * @param enabled flag value
     */
    public void setSlidingEnabled(boolean enabled) {
        mIsSlidingEnabled = enabled;
    }

    private boolean mIsForeBackgroundCutOff = false;

    private void cutOffForegroundDrawable() {
        if (mIsForeBackgroundCutOff)
            return;
        int width = Integer.parseInt(BaseInfoHelper.getWidth(getContext()));
        int height = Integer.parseInt(BaseInfoHelper.getHeight(getContext()));
        Bitmap srcBmp = ImageUtils.drawable2Bitmap(mForegroundDrawable);
        srcBmp = ImageUtils.scaleTo(srcBmp, width, height);
        Bitmap topBmp = Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(),
                mTopView.getMeasuredHeight());
        Bitmap bottomBmp = Bitmap.createBitmap(srcBmp, 0, mTopView.getMeasuredHeight(),
                srcBmp.getWidth(), mSlideableView.getMeasuredHeight());
        mTopPanelBgDrawable = ImageUtils.bitmap2Drawable(getContext(), topBmp);
        mBottomPanelBgDrawable = ImageUtils.bitmap2Drawable(getContext(), bottomBmp);
        mIsForeBackgroundCutOff = true;
    }

    public boolean isSlidingEnabled() {
        return mIsSlidingEnabled && mSlideableView != null;
    }

    /**
     * Set the collapsed panel height in pixels
     * 
     * @param val A height in pixels
     */
    public void setPanelHeight(int val) {
        mPanelHeight = val;
        requestLayout();
    }

    /**
     * @return The current collapsed panel height
     */
    public int getPanelHeight() {
        return mPanelHeight;
    }

    /**
     * @return The current paralax offset
     */
    public int getCurrentParalaxOffset() {
        // Clamp slide offset at zero for parallax computation;
        int offset = (int) (mParallaxOffset * Math.max(mSlideOffset, 0));
        return mIsSlidingUp ? -offset : offset;
    }

    /**
     * Sets the panel slide listener
     * 
     * @param listener
     */
    public void setPanelSlideListener(PanelSlideListener listener) {
        mPanelSlideListener = listener;
    }

    /**
     * Set the draggable view portion. Use to null, to allow the whole panel to
     * be draggable
     * 
     * @param dragView A view that will be used to drag the panel.
     */
    public void setDragView(View dragView) {
        if (mDragView != null) {
            mDragView.setOnClickListener(null);
        }
        mDragView = dragView;
        if (mDragView != null) {
            mDragView.setClickable(true);
            mDragView.setFocusable(false);
            mDragView.setFocusableInTouchMode(false);
            mDragView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEnabled())
                        return;
                    if (!isPanelExpanded() && mIsFixed) {
                        dispatchOnPanelClickDuringFixed();
                    }
                }
            });
            ;
        }
    }

    /**
     * Set an anchor point where the panel can stop during sliding
     * 
     * @param anchorPoint A value between 0 and 1, determining the position of
     *            the anchor point starting from the top of the layout.
     */
    public void setAnchorPoint(float anchorPoint) {
        if (anchorPoint > 0 && anchorPoint <= 1) {
            mAnchorPoint = anchorPoint;
        }
    }

    public void setForegroundDrawable(Drawable drawable) {
        mForegroundDrawable = drawable;
        mIsForeBackgroundCutOff = false;
        invalidate();
    }

    public void setForegroundResource(int resId) {
        Drawable drawable = mContext.getResources().getDrawable(resId);
        setForegroundDrawable(drawable);
    }

    /**
     * Gets the currently set anchor point
     * 
     * @return the currently set anchor point
     */
    public float getAnchorPoint() {
        return mAnchorPoint;
    }

    /**
     * Sets whether or not the panel overlays the content
     * 
     * @param overlayed
     */
    public void setOverlayed(boolean overlayed) {
        mOverlayContent = overlayed;
    }

    /**
     * Check if the panel is set as an overlay.
     */
    public boolean isOverlayed() {
        return mOverlayContent;
    }

    void dispatchOnPanelHiddenEnd() {
        if (mPanelSlideListener != null) {
            mPanelSlideListener.onPanelHiddenEnd();
        }
    }

    void dispatchOnPanelClickDuringFixed() {
        if (mPanelSlideListener != null) {
            mPanelSlideListener.onPanelClickedDuringFixed();
        }
    }

    void dispatchOnPanelFixed(View panel) {
        if (mPanelSlideListener != null) {
            mPanelSlideListener.onPanelFixed(panel);
        }
    }

    void dispatchOnPanelSlide(View panel) {
        if (mPanelSlideListener != null) {
            mPanelSlideListener.onPanelSlide(panel, mSlideOffset);
        }
    }

    void dispatchOnPanelExpanded(View panel) {
        if (mPanelSlideListener != null) {
            mPanelSlideListener.onPanelExpanded(panel);
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    void dispatchOnPanelCollapsed(View panel) {
        if (mPanelSlideListener != null) {
            mPanelSlideListener.onPanelCollapsed(panel);
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    void dispatchOnPanelAnchored(View panel) {
        if (mPanelSlideListener != null) {
            mPanelSlideListener.onPanelAnchored(panel);
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    void dispatchOnPanelHidden(View panel) {
        if (mPanelSlideListener != null) {
            mPanelSlideListener.onPanelHidden(panel);
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    void updateObscuredViewVisibility() {
        if (getChildCount() == 0) {
            return;
        }
        final int leftBound = getPaddingLeft();
        final int rightBound = getWidth() - getPaddingRight();
        final int topBound = getPaddingTop();
        final int bottomBound = getHeight() - getPaddingBottom();
        final int left;
        final int right;
        final int top;
        final int bottom;
        if (mSlideableView != null && hasOpaqueBackground(mSlideableView)) {
            left = mSlideableView.getLeft();
            right = mSlideableView.getRight();
            top = mSlideableView.getTop();
            bottom = mSlideableView.getBottom();
        } else {
            left = right = top = bottom = 0;
        }
        View child = getChildAt(0);
        final int clampedChildLeft = Math.max(leftBound, child.getLeft());
        final int clampedChildTop = Math.max(topBound, child.getTop());
        final int clampedChildRight = Math.min(rightBound, child.getRight());
        final int clampedChildBottom = Math.min(bottomBound, child.getBottom());
        final int vis;
        if (clampedChildLeft >= left && clampedChildTop >= top && clampedChildRight <= right
                && clampedChildBottom <= bottom) {
            vis = INVISIBLE;
        } else {
            vis = VISIBLE;
        }
        child.setVisibility(vis);
    }

    void setAllChildrenVisible() {
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == INVISIBLE) {
                child.setVisibility(VISIBLE);
            }
        }
    }

    private static boolean hasOpaqueBackground(View v) {
        final Drawable bg = v.getBackground();
        return bg != null && bg.getOpacity() == PixelFormat.OPAQUE;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
        } else if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Height must have an exact value or MATCH_PARENT");
        }

        final int childCount = getChildCount();

        if (childCount != 3) {
            throw new IllegalStateException("Sliding up panel layout must have exactly 3 children!");
        }

        mMainView = getChildAt(0);
        mTopView = getChildAt(1);
        mSlideableView = getChildAt(2);
        if (mDragView == null) {
            setDragView(mSlideableView);
        }

        // If the sliding panel is not visible, then put the whole view in the
        // hidden state
        if (mSlideableView.getVisibility() == GONE) {
            mSlideState = SlideState.HIDDEN;
        }

        int layoutHeight = heightSize - getPaddingTop() - getPaddingBottom();

        int topPanelHeight = 0;
        // First pass. Measure based on child LayoutParams width/height.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            // We always measure the sliding panel in order to know it's height
            // (needed for show panel)
            if (child.getVisibility() == GONE && i == 0) {
                continue;
            }

            int height = layoutHeight;
            if (child == mMainView && !mOverlayContent) {
                height -= (mPanelHeight + mTopViewSeekOutHeight);
            } else if (child == mSlideableView) {
                height = layoutHeight - topPanelHeight;
            } else if (child == mTopView) {
                height = topPanelHeight = height / 2;
            }

            int childWidthSpec;
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
            } else if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            }

            int childHeightSpec;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
            } else if (lp.height == LayoutParams.MATCH_PARENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            }

            child.measure(childWidthSpec, childHeightSpec);

            if (child == mSlideableView) {
                mSlideRange = mSlideableView.getMeasuredHeight() - mPanelHeight;
            } else if (child == mTopView) {
                mTopRange = mTopView.getMeasuredHeight() - mTopViewSeekOutHeight;
            }
        }

        setMeasuredDimension(widthSize, heightSize);

        cutOffForegroundDrawable();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        final int childCount = getChildCount();

        if (mFirstLayout) {
            switch (mSlideState) {
                case EXPANDED:
                    mSlideOffset = 1.0f;
                    break;
                case ANCHORED:
                    mSlideOffset = mAnchorPoint;
                    break;
                case HIDDEN:
                    int newTop = computePanelTopPosition(0.0f)
                            + (mIsSlidingUp ? +mPanelHeight : -mPanelHeight);
                    mSlideOffset = computeSlideOffset(newTop);
                    break;
                default:
                    mSlideOffset = 0.f;
                    break;
            }
        }

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            // Always layout the sliding view on the first layout
            if (child.getVisibility() == GONE && (i == 0 || mFirstLayout)) {
                continue;
            }

            final int childHeight = child.getMeasuredHeight();
            int childTop = paddingTop;

            int childBottom = 0;
            int childLeft = 0;
            int childRight = 0;
            if (child == mSlideableView) {
                childTop = computePanelTopPosition(mSlideOffset);
                childBottom = childTop + childHeight;
            } else if (child == mTopView) {
                childBottom = computeTopPanelBottomPosition(mSlideOffset);
                childTop = childBottom - childHeight;
            } else if (child == mMainView) {
                childTop = mTopViewSeekOutHeight;
                childBottom = mTopViewSeekOutHeight + mMainView.getMeasuredHeight();
            }

            childLeft = paddingLeft;
            childRight = childLeft + child.getMeasuredWidth();
            child.layout(childLeft, childTop, childRight, childBottom);
        }

        if (mFirstLayout) {
            updateObscuredViewVisibility();
        }

        mFirstLayout = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Recalculate sliding panes and their details
        if (h != oldh) {
            mFirstLayout = true;
        }
    }

    /**
     * Set if the drag view can have its own touch events. If set to true, a
     * drag view can scroll horizontally and have its own click listener.
     * Default is set to false.
     */
    public void setEnableDragViewTouchEvents(boolean enabled) {
        mIsUsingDragViewTouchEvents = enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (!isEnabled() || !mIsSlidingEnabled
                || (mIsUnableToDrag && action != MotionEvent.ACTION_DOWN)) {
            mDragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsUnableToDrag = false;
                mInitialMotionX = x;
                mInitialMotionY = y;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                final int dragSlop = mDragHelper.getTouchSlop();

                // Handle any horizontal scrolling on the drag view.
                if (mIsUsingDragViewTouchEvents && adx > dragSlop && ady < dragSlop) {
                    return super.onInterceptTouchEvent(ev);
                }

                if ((ady > dragSlop && adx > ady)
                        || !isDragViewUnder((int) mInitialMotionX, (int) mInitialMotionY)) {
                    mDragHelper.cancel();
                    mIsUnableToDrag = true;
                    return false;
                }
                break;
            }
        }

        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    private static void printStackTrace() {
        StackTraceElement[] stackElements = new Throwable().getStackTrace();
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {
                System.out.println("" + stackElements[i]);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isSlidingEnabled()) {
            return super.onTouchEvent(ev);
        }
        mDragHelper.processTouchEvent(ev);
        return true;
    }

    private boolean isDragViewUnder(int x, int y) {
        if (mDragView == null)
            return false;
        int[] viewLocation = new int[2];
        mDragView.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + mDragView.getWidth()
                && screenY >= viewLocation[1] && screenY < viewLocation[1] + mDragView.getHeight();
    }

    private boolean expandPanel(View pane, int initialVelocity, float mSlideOffset) {
        return mFirstLayout || smoothSlideTo(mSlideOffset, initialVelocity);
    }

    private boolean collapsePanel(View pane, int initialVelocity) {
        return mFirstLayout || smoothSlideTo(0.0f, initialVelocity);
    }

    private boolean collapsePanel(View pane, int initialVelocity, int duration) {
        return mFirstLayout || smoothSlideTo(0.0f, initialVelocity, duration);
    }

    /*
     * Computes the top position of the panel based on the slide offset.
     */
    private int computePanelTopPosition(float slideOffset) {
        int slidingViewHeight = mSlideableView != null ? mSlideableView.getMeasuredHeight() : 0;
        int slidePixelOffset = (int) (slideOffset * mSlideRange);
        // Compute the top of the panel if its collapsed
        return mIsSlidingUp ? getMeasuredHeight() - getPaddingBottom() - mPanelHeight
                - slidePixelOffset : getPaddingTop() - slidingViewHeight + mPanelHeight
                + slidePixelOffset;
    }

    private int computeTopPanelBottomPosition(float slideOffset) {
        int slidePixelOffset = (int) (slideOffset * mTopRange);
        int a = getPaddingTop() + mTopViewSeekOutHeight + slidePixelOffset;
        return a;
    }

    /*
     * Computes the slide offset based on the top position of the panel
     */
    private float computeSlideOffset(int topPosition) {
        // Compute the panel top position if the panel is collapsed (offset 0)
        final int topBoundCollapsed = computePanelTopPosition(0);

        // Determine the new slide offset based on the collapsed top position
        // and the new required
        // top position
        return (mIsSlidingUp ? (float) (topBoundCollapsed - topPosition) / mSlideRange
                : (float) (topPosition - topBoundCollapsed) / mSlideRange);
    }

    private float computeTopPanelOffset(int bottomPosition) {
        final int bottomBoundCollapsed = computeTopPanelBottomPosition(0);
        return (float) (bottomBoundCollapsed - bottomPosition) / mSlideRange;
    }

    /**
     * Collapse the sliding pane if it is currently slideable. If first layout
     * has already completed this will animate.
     * 
     * @return true if the pane was slideable and is now collapsed/in the
     *         process of collapsing
     */
    public boolean collapsePanel() {
        if (mFirstLayout) {
            mSlideState = SlideState.COLLAPSED;
            return true;
        } else {
            if (mSlideState == SlideState.HIDDEN || mSlideState == SlideState.COLLAPSED)
                return false;
            return collapsePanel(mSlideableView, 0);
        }
    }

    public boolean collapsePanel(int duration) {
        if (mFirstLayout) {
            mSlideState = SlideState.COLLAPSED;
            return true;
        } else {
            if (mSlideState == SlideState.HIDDEN || mSlideState == SlideState.COLLAPSED)
                return false;
            return collapsePanel(mSlideableView, 0, duration);
        }
    }

    /**
     * Expand the sliding pane if it is currently slideable.
     * 
     * @return true if the pane was slideable and is now expanded/in the process
     *         of expading
     */
    public boolean expandPanel() {
        if (mFirstLayout) {
            mSlideState = SlideState.EXPANDED;
            return true;
        } else {
            return expandPanel(1.0f);
        }
    }

    /**
     * Expand the sliding pane to the anchor point if it is currently slideable.
     * 
     * @return true if the pane was slideable and is now expanded/in the process
     *         of expading
     */
    public boolean anchorPanel() {
        if (mFirstLayout) {
            mSlideState = SlideState.ANCHORED;
            return true;
        } else {
            return expandPanel(mAnchorPoint);
        }
    }

    /**
     * Partially expand the sliding panel up to a specific offset
     * 
     * @param mSlideOffset Value between 0 and 1, where 0 is completely
     *            expanded.
     * @return true if the pane was slideable and is now expanded/in the process
     *         of expanding
     */
    public boolean expandPanel(float mSlideOffset) {
        if (mSlideableView == null || mSlideState == SlideState.EXPANDED)
            return false;
        mSlideableView.setVisibility(View.VISIBLE);
        return expandPanel(mSlideableView, 0, mSlideOffset);
    }

    /**
     * Check if the sliding panel in this layout is fully expanded.
     * 
     * @return true if sliding panel is completely expanded
     */
    public boolean isPanelExpanded() {
        return mSlideState == SlideState.EXPANDED;
    }

    /**
     * Check if the sliding panel in this layout is anchored.
     * 
     * @return true if sliding panel is anchored
     */
    public boolean isPanelAnchored() {
        return mSlideState == SlideState.ANCHORED;
    }

    /**
     * Check if the sliding panel in this layout is currently visible.
     * 
     * @return true if the sliding panel is visible.
     */
    public boolean isPanelHidden() {
        return mSlideState == SlideState.HIDDEN;
    }

    /**
     * Shows the panel from the hidden state
     */
    public void showPanel() {
        if (mFirstLayout) {
            mSlideState = SlideState.COLLAPSED;
        } else {
            if (mSlideableView == null || mSlideState != SlideState.HIDDEN)
                return;
            mSlideableView.setVisibility(View.VISIBLE);
            requestLayout();
            smoothSlideTo(0, 0);
        }
    }

    /**
     * Hides the sliding panel entirely.
     */
    public void hidePanel() {
        if (mFirstLayout) {
            mSlideState = SlideState.HIDDEN;
        } else {
            if (mSlideState == SlideState.DRAGGING || mSlideState == SlideState.HIDDEN)
                return;
            int newTop = computePanelTopPosition(0.0f)
                    + (mIsSlidingUp ? +mPanelHeight : -mPanelHeight);
            int delayTime = 300;
            smoothSlideTo(computeSlideOffset(newTop), 0, delayTime);
            HDBThreadUtils.postOnUiDelayed(new Runnable() {

                @Override
                public void run() {
                    dispatchOnPanelHiddenEnd();
                }

            }, delayTime);
        }
    }

    @SuppressLint("NewApi")
    private void onPanelDragged(int newTop) {
        mSlideState = SlideState.DRAGGING;
        // Recompute the slide offset based on the new top position
        float slideOffset = computeSlideOffset(newTop);
        if (slideOffset <= 0.03) {
            mSlideOffset = 0;
        } else {
            mSlideOffset = slideOffset;
        }
        // 处理面板是否需要固定在页面底部的逻辑
        processPanelFixed();

        int topPanelBottomPosition = computeTopPanelBottomPosition(mSlideOffset);
        mTopView.offsetTopAndBottom(topPanelBottomPosition - mTopView.getBottom());
        // computeTopPanelBottomPosition(mSlideOffset);
        // Update the parallax based on the new slide offset
        if (mParallaxOffset > 0 && mSlideOffset >= 0) {
            int mainViewOffset = getCurrentParalaxOffset();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mMainView.setTranslationY(mainViewOffset);
            } else {
                AnimatorProxy.wrap(mMainView).setTranslationY(mainViewOffset);
            }
        }
        // Dispatch the slide event
        dispatchOnPanelSlide(mSlideableView);
        // If the slide offset is negative, and overlay is not on, we need to
        // increase the
        // height of the main content
    }

    private void processPanelFixed() {
        if (mSlideOffset == 0 && !mIsStartKeepTime && !mIsFixed) {
            mPressOnStartTime = System.currentTimeMillis();
            mIsStartKeepTime = true;
        } else if (mSlideOffset != 0 && mIsStartKeepTime && !mIsFixed) {
            mIsStartKeepTime = false;
        } else if (mSlideOffset == 0 && mIsStartKeepTime && !mIsFixed) {
            final long curTime = System.currentTimeMillis();
            if (curTime - mPressOnStartTime > MIN_DURATION_TO_FIXED) {
                mIsFixed = true;
                dispatchOnPanelFixed(mSlideableView);
            }
        }
    }

    public void recovery() {
        // TODO
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result;
        final int save = canvas.save(Canvas.CLIP_SAVE_FLAG);

        if (isSlidingEnabled() && mSlideableView != child && mTopView != child) {
            // Clip against the slider; no sense drawing what will immediately
            // be covered,
            // Unless the panel is set to overlay content
            if (!mOverlayContent) {
                canvas.getClipBounds(mTmpRect);
                if (mIsSlidingUp) {
                    mTmpRect.top = mTopView.getBottom();
                    mTmpRect.bottom = Math.min(mTmpRect.bottom, mSlideableView.getTop());
                } else {
                    mTmpRect.top = Math.max(mTmpRect.top, mSlideableView.getBottom());
                }
                canvas.clipRect(mTmpRect);
            }
        }

        if (child == mTopView) {
            child.setBackgroundDrawable(mTopPanelBgDrawable);
        } else if (child == mSlideableView) {
            child.setBackgroundDrawable(mBottomPanelBgDrawable);
        }

        result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(save);

        if (mCoveredFadeColor != 0 && mSlideOffset > 0) {
            final int baseAlpha = (mCoveredFadeColor & 0xff000000) >>> 24;
            final int imag = (int) (baseAlpha * mSlideOffset);
            final int color = imag << 24 | (mCoveredFadeColor & 0xffffff);
            mCoveredFadePaint.setColor(color);
            canvas.drawRect(mTmpRect, mCoveredFadePaint);
        }

        return result;
    }

    /**
     * Smoothly animate mDraggingPane to the target X position within its range.
     * 
     * @param slideOffset position to animate to
     * @param velocity initial velocity in case of fling, or 0.
     */
    boolean smoothSlideTo(float slideOffset, int velocity) {
        if (!isSlidingEnabled()) {
            // Nothing to do.
            return false;
        }

        int panelTop = computePanelTopPosition(slideOffset);
        if (mDragHelper.smoothSlideViewTo(mSlideableView, mSlideableView.getLeft(), panelTop, 300)) {
            setAllChildrenVisible();
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    boolean smoothSlideTo(float slideOffset, int velocity, int duration) {
        if (!isSlidingEnabled()) {
            // Nothing to do.
            return false;
        }

        int panelTop = computePanelTopPosition(slideOffset);
        if (mDragHelper.smoothSlideViewTo(mSlideableView, mSlideableView.getLeft(), panelTop,
                duration)) {
            setAllChildrenVisible();
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper != null && mDragHelper.continueSettling(true)) {
            if (!isSlidingEnabled()) {
                mDragHelper.abort();
                return;
            }

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);

        if (!isSlidingEnabled()) {
            // No need to draw a shadow if we don't have one.
            return;
        }

        if (mSlideState == SlideState.EXPANDED) {
            return;
        }

        drawTopPanelShadow(c);
        drawBottomPanelShadow(c);
    }

    private void drawTopPanelShadow(Canvas c) {
        final int right = mTopView.getRight();
        final int top;
        final int bottom;
        final int left = mTopView.getLeft();
        top = mTopView.getBottom();
        bottom = mTopView.getBottom() + mShadowHeight;
        if (mTopShadowDrawable != null) {
            mTopShadowDrawable.setBounds(left, top, right, bottom);
            mTopShadowDrawable.draw(c);
        }
    }

    private void drawBottomPanelShadow(Canvas c) {
        final int right = mSlideableView.getRight();
        final int top;
        final int bottom;
        top = mSlideableView.getTop() - mShadowHeight;
        bottom = mSlideableView.getTop();
        final int left = mSlideableView.getLeft();

        if (mBottomShadowDrawable != null) {
            mBottomShadowDrawable.setBounds(left, top, right, bottom);
            mBottomShadowDrawable.draw(c);
        }
    }

    /**
     * Tests scrollability within child views of v given a delta of dx.
     * 
     * @param v View to test for horizontal scrollability
     * @param checkV Whether the view v passed should itself be checked for
     *            scrollability (true), or just its children (false).
     * @param dx Delta scrolled in pixels
     * @param x X coordinate of the active touch point
     * @param y Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            // Count backwards - let topmost views consume scroll distance
            // first.
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft()
                        && x + scrollX < child.getRight()
                        && y + scrollY >= child.getTop()
                        && y + scrollY < child.getBottom()
                        && canScroll(child, true, dx, x + scrollX - child.getLeft(), y + scrollY
                                - child.getTop())) {
                    return true;
                }
            }
        }
        return checkV && ViewCompat.canScrollHorizontally(v, -dx);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams ? new LayoutParams((MarginLayoutParams) p)
                : new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.mSlideState = mSlideState;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mSlideState = ss.mSlideState;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (mIsUnableToDrag) {
                return false;
            }

            return child == mSlideableView;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                mSlideOffset = computeSlideOffset(mSlideableView.getTop());

                if (mSlideOffset == 1) {
                    if (mSlideState != SlideState.EXPANDED) {
                        updateObscuredViewVisibility();
                        mSlideState = SlideState.EXPANDED;
                        dispatchOnPanelExpanded(mSlideableView);
                    }
                } else if (mSlideOffset == 0) {
                    if (mSlideState != SlideState.COLLAPSED) {
                        mSlideState = SlideState.COLLAPSED;
                        if (!mIsFixed)
                            dispatchOnPanelCollapsed(mSlideableView);
                    }
                } else if (mSlideOffset < 0) {
                    mSlideState = SlideState.HIDDEN;
                    mSlideableView.setVisibility(View.GONE);
                    dispatchOnPanelHidden(mSlideableView);
                } else if (mSlideState != SlideState.ANCHORED) {
                    updateObscuredViewVisibility();
                    mSlideState = SlideState.ANCHORED;
                    dispatchOnPanelAnchored(mSlideableView);
                }
            }
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            setAllChildrenVisible();
            if (mPanelSlideListener != null) {
                mPanelSlideListener.onPanelStartDown(capturedChild);
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            onPanelDragged(top);

            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mSlideOffset == 0 && mIsFixed) {
                return;
            } else if (mSlideOffset != 0 && mIsFixed) {
                mIsFixed = false;
            }

            if (mSlideOffset != 0) {
                int target = computePanelTopPosition(1.0f);
                mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), target);
                invalidate();
            }
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mSlideRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int collapsedTop = computePanelTopPosition(0.f);
            final int expandedTop = computePanelTopPosition(1.0f);
            if (mIsSlidingUp) {
                return Math.min(Math.max(top, expandedTop), collapsedTop);
            } else {
                return Math.min(Math.max(top, collapsedTop), expandedTop);
            }
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final int[] ATTRS = new int[] {
            android.R.attr.layout_weight
        };

        public LayoutParams() {
            super(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, ATTRS);
            a.recycle();
        }

    }

    static class SavedState extends BaseSavedState {
        SlideState mSlideState;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            try {
                mSlideState = Enum.valueOf(SlideState.class, in.readString());
            } catch (IllegalArgumentException e) {
                mSlideState = SlideState.COLLAPSED;
            }
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(mSlideState.toString());
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
