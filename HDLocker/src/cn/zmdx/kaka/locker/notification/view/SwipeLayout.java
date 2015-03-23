
package cn.zmdx.kaka.locker.notification.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SwipeLayout extends ViewPager {

    private FrameLayout mLeftView, mRightView, mUpperView;

    public static final int OPEN_DIRECTION_RIGHT = 1;

    public static final int OPEN_DIRECTION_LEFT = 2;

    public interface OnSwipeLayoutListener {

        public void onOpened(SwipeLayout swipeLayout, int direction);

        public void onClosed();

        /**
         * @param offset Value from [0, 1)
         */
        public void onSlide(SwipeLayout layout,int position, float offset);
    }

    private OnSwipeLayoutListener mCallback;

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mLeftView = new FrameLayout(getContext());
        mRightView = new FrameLayout(getContext());
        mUpperView = new FrameLayout(getContext());
        List<View> pages = new ArrayList<View>(3);
        pages.add(mLeftView);
        pages.add(mUpperView);
        pages.add(mRightView);

        SwipePagerAdapter adapter = new SwipePagerAdapter(pages);
        setAdapter(adapter);
        setOnPageChangeListener(mPageChangeListener);
        setCurrentItem(1);
    }

    public void setOnSwipeLayoutListener(OnSwipeLayoutListener callback) {
        this.mCallback = callback;
    }

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public boolean isOpen() {
        return getCurrentItem() != 1;
    }

    public void open(int direction, boolean smooth) {
        if (isOpen())
            return;

        if (direction == OPEN_DIRECTION_LEFT) {
            setCurrentItem(0, smooth);
        } else if (direction == OPEN_DIRECTION_RIGHT) {
            setCurrentItem(2, smooth);
        } else {
            throw new IllegalArgumentException("bad direction!");
        }
    }

    public void close(boolean smooth) {
        if (!isOpen())
            return;

        setCurrentItem(1, smooth);
    }

    public void addLeftView(View leftView) {
        mLeftView.addView(leftView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }

    public void addRightView(View view) {
        mRightView.addView(view, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }

    public void addUpperView(View view) {
        mUpperView.addView(view, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }

    public View getRightView() {
        return mRightView;
    }

    public View getLeftView() {
        return mLeftView;
    }

    public View getUpperView() {
        return mUpperView;
    }

    private static class SwipePagerAdapter extends PagerAdapter {

        private List<View> mPages;

        public SwipePagerAdapter(List<View> pages) {
            this.mPages = pages;
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mPages.get(position);
            if (view.getParent() != null) {
                ViewGroup vg = (ViewGroup) view.getParent();
                vg.removeView(view);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeViewAt(position);
        }
    }

    private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float offset, int positionOffsetPixels) {
            if (mCallback != null) {
                mCallback.onSlide(SwipeLayout.this, position, offset);
            }

            if (position == 0 && offset == 0) {
                if (mCallback != null) {
                    mCallback.onOpened(SwipeLayout.this, OPEN_DIRECTION_LEFT);
                }
            } else if (position == 2 && offset == 0) {
                if (mCallback != null) {
                    mCallback.onOpened(SwipeLayout.this, OPEN_DIRECTION_RIGHT);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 1) {
                if (mCallback != null) {
                    mCallback.onClosed();
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public void reset() {
        setCurrentItem(1, false);
    }
}
