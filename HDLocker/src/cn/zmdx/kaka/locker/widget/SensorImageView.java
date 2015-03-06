
package cn.zmdx.kaka.locker.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class SensorImageView extends ImageView {

    /**
     * Delay between a pair of frames at a 60 FPS frame rate.
     * 实际达不到60fps,因为自身绘制需要时间
     */
    private static final long FRAME_DELAY = 1000 / 60;

    /** Matrix used to perform all the necessary transition transformations. */
    private final Matrix mMatrix = new Matrix();

    public static final int TRANSITION_MODE_AUTO = 1;

    public static final int TRANSITION_MODE_SENSOR = 2;

    public static final int TRANSITION_MODE_STATIC = 3;

    private static final int DEFAULT_TRANSITION_SPEED = 1;

    private int mCurMode = TRANSITION_MODE_SENSOR;

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
        mTransSpeed = DEFAULT_TRANSITION_SPEED;
    }

    public SensorImageView(Context context, int mode) {
        this(context, null);
        ensureValidMode(mode);
        mCurMode = mode;
    }

    private void ensureValidMode(int mode) {
        if (mode != TRANSITION_MODE_AUTO && mode != TRANSITION_MODE_SENSOR
                && mode != TRANSITION_MODE_STATIC) {
            throw new IllegalArgumentException(
                    "非法参数，mode值必须为TRANSITION_MODE_AUTO或TRANSITION_MODE_SENSOR");
        }
    }

    public void setTransitionMode(int mode) {
        ensureValidMode(mode);
        mCurMode = mode;
    }

    public int getCurTransitionMode() {
        return mCurMode;
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

        if (mCurMode == TRANSITION_MODE_STATIC || !ensureEnoughWidth()) {
            mMatrix.postTranslate(-(mDrawableWidth / 2 - getWidth() / 2), 0);
            setImageMatrix(mMatrix);
            super.onDraw(canvas);
            return;
        }

        updateTransX();
        mMatrix.postTranslate(mCurTransX, 0);
        setImageMatrix(mMatrix);
        postInvalidateDelayed(FRAME_DELAY);
        super.onDraw(canvas);
        Log.e("zy", "...........");
    }

    private SensorManager mSensor;

    private void registSensorListener() {
        if (mSensor == null) {
            mSensor = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        }
        Sensor gravitySensor = mSensor.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensor.registerListener(mSensorEventListener, gravitySensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unRegistSensorListener() {
        if (mSensor != null) {
            mSensor.unregisterListener(mSensorEventListener);
            mSensor = null;
        }
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];

            if (x > 1) {
                mTransSpeed = DEFAULT_TRANSITION_SPEED;
            } else if (x < -1) {
                mTransSpeed = -DEFAULT_TRANSITION_SPEED;
            } else {
                mTransSpeed = 0;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    protected void onAttachedToWindow() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        getContext().registerReceiver(mScreenReceiver, filter);
    };

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(mScreenReceiver);
        super.onDetachedFromWindow();
    }

    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                unRegistSensorListener();
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                if (mCurMode == TRANSITION_MODE_SENSOR) {
                    registSensorListener();
                }
            }
        }
    };

    private void updateTransX() {
        int maxTransX = mDrawableWidth - getWidth();
        if (mCurMode == TRANSITION_MODE_AUTO) {
            if (mCurTransX == 0 || mCurTransX == -maxTransX) {
                mTransSpeed = -mTransSpeed;
            }
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
