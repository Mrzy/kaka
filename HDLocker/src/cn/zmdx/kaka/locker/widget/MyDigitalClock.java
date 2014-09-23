
package cn.zmdx.kaka.locker.widget;

import java.util.Calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.DigitalClock;
import cn.zmdx.kaka.locker.R;

@SuppressWarnings("deprecation")
public class MyDigitalClock extends DigitalClock {

    public MyDigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(attrs);
    }

    // FIXME: implement separate views for hours/minutes/seconds, so
    // proportional fonts don't shake rendering

    Calendar mCalendar;

    private final static String m12 = "h:mm aa";

    private final static String m24 = "k:mm";

    // We must keep a reference to this observer
    private FormatChangeObserver mFormatChangeObserver;

    private Runnable mTicker;

    private Handler mHandler;

    private boolean mTickerStopped = false;

    private String mFormat;

    private int m24HourMode;

    private void initClock(AttributeSet attrs) {
        Typeface face = Typeface
                .createFromAsset(getContext().getAssets(), "fonts/Roboto-Thin.ttf");
        setTypeface(face);
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI,
                true, mFormatChangeObserver);
        if (attrs != null && getContext() != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.MyDigialClock);
            if (typedArray != null) {
                m24HourMode = typedArray.getInteger(R.styleable.MyDigialClock_format, 24);
                typedArray.recycle();
            }
        }
        setFormat();
    }

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped)
                    return;
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                setText(DateFormat.format(mFormat, mCalendar));
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }

    // /**
    // * Pulls 12/24 mode from system settings
    // */
    // private boolean get24HourMode() {
    // return android.text.format.DateFormat.is24HourFormat(getContext());
    // }

    private boolean get24HourMode() {
        return m24HourMode == 12 ? true : false;
    }

    private void setFormat() {
        if (get24HourMode()) {
            mFormat = m12;
        } else {
            mFormat = m24;
        }
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            setFormat();
        }
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        // noinspection deprecation
        event.setClassName(DigitalClock.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        // noinspection deprecation
        info.setClassName(DigitalClock.class.getName());
    }

}
