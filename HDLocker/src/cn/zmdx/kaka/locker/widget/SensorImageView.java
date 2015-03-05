
package cn.zmdx.kaka.locker.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SensorImageView extends ImageView {

    /**
     * Delay between a pair of frames at a 100 FPS frame rate. modified by
     * zhangyan. 由60修改为100，实际运行fps不会达到100fps，因为自身绘制需要时间。估计实际运行效率在60fps之上
     */
    private static final long FRAME_DELAY = 1000 / 60;

    /** Matrix used to perform all the necessary transition transformations. */
    private final Matrix mMatrix = new Matrix();

    /** The rect that holds the bounds of this view. */
    private final RectF mViewportRect = new RectF();

    public SensorImageView(Context context) {
        this(context, null);
    }

    public SensorImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SensorImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Attention to the super call here!
        super.setScaleType(ImageView.ScaleType.MATRIX);
        mTransSpeed = 1;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        // It'll always be matrix by default.
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        handleImageChange();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        handleImageChange();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        handleImageChange();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        handleImageChange();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private boolean mInit = false;

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable d = getDrawable();
        if (d == null) {
            return;
        }
        if (!mInit) {
            updateScaleRate();

            int drawableWidth = getDrawable().getIntrinsicWidth();
            mDrawableWidth = (int) (drawableWidth * mScaleRate);
            mInit = true;
        }

        mMatrix.reset();
        mMatrix.postScale(mScaleRate, mScaleRate);

        if (!isTurnOnTransition() || !ensureEnoughWidth()) {
            mMatrix.postTranslate(-(mDrawableWidth / 2 - getWidth() / 2), 0);
            setImageMatrix(mMatrix);
            return;
        }

        if (mAutoPlay) {
            updateTransX();
            mMatrix.postTranslate(mCurTransX, 0);
        } else {
            // TODO 增加根据sensor动态调整
        }
        setImageMatrix(mMatrix);
        postInvalidateDelayed(FRAME_DELAY);
        super.onDraw(canvas);
    }

    private void updateTransX() {
        int maxTransX = mDrawableWidth - getWidth();
        if (mCurTransX == 0 || mCurTransX == -maxTransX) {
            mTransSpeed = -mTransSpeed;
        }

        if (mCurTransX + mTransSpeed > 0) {
            mCurTransX = 0;
        } else if (mCurTransX + mTransSpeed < -maxTransX) {
            mCurTransX = -maxTransX;
        } else {
            mCurTransX += mTransSpeed;
        }
    }

    /**
     * 表示getDrawable()返回的drawable在经过放大或缩小处理，使得能够撑满本view后，这个drawable的宽度，
     * 而不是drawable本来的宽度
     */
    private int mDrawableWidth;

    /**
     * 每绘制一帧，移动的偏移量，单位px
     */
    private int mTransSpeed;

    private int mCurTransX = 0;

    // 确保将背景drawable调整到合适大小后，其宽度比view容器宽，这样才可以做平移的动画
    private boolean ensureEnoughWidth() {
        float width = getDrawable().getIntrinsicWidth() * mScaleRate;
        return width > getWidth();
    }

    public boolean isTurnOnTransition() {
        return mTurnOnTransition;
    }

    public void turnOffTransition() {
        mTurnOnTransition = false;
    }

    public void turnOnTransition() {
        mTurnOnTransition = true;
    }

    private boolean mTurnOnTransition = true;

    private boolean mAutoPlay = true;

    private float mScaleRate;

    private void updateScaleRate() {
        try {
            float wscale = (float) getWidth() / (float) getDrawable().getIntrinsicWidth();
            float hscale = (float) getHeight() / (float) getDrawable().getIntrinsicHeight();
            mScaleRate = Math.max(wscale, hscale);
        } catch (Exception e) {
        }
    }

    /**
     * This method is called every time the underlying image is changed.
     */
    private void handleImageChange() {
        updateScaleRate();
    }
}
