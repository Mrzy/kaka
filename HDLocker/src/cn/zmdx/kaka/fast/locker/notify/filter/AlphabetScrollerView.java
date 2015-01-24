
package cn.zmdx.kaka.fast.locker.notify.filter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ExpandableListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import cn.zmdx.kaka.fast.locker.R;

public class AlphabetScrollerView extends View {
    private String mCurrentLetter = null;

    private String mCurrentDisplayLetter = null;

    private String mLastDisplayLetter = null;

    private AbsListView mList = null;

    private Object[] mSections;

    private int mListOffset = 0;

    private SectionIndexer mSectionIndexer = null;

    private int mAlphabetFontSize;

    private int mAlphabetOffsetX;

    private int mAlphabetOffsetY;

    private float mAplhabetSpace;

    private int LETTER_VIEW_DISPLAY_TIME = 3000;

    private static final String ALPHABET[] = {
            "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private Drawable mAlphabetFocusDrawable = null;

    private int mCurrentTouchMoveY;

    private boolean isDown;

    // private View letterView;
    // private TextView mLetter = null;
    private int mTopOffset;

    private OnEventListener mOnEventListener = null;
    
    private View mLayout;

    public void setOnEventListener(OnEventListener onEventListener) {
        mOnEventListener = onEventListener;
    }

    public String getCurrentLetter() {
        return mCurrentLetter;
    }

    public AlphabetScrollerView(Context context) {
        super(context);

        mAlphabetFontSize = (int) context.getResources().getDimension(
                R.dimen.local_alphabet_font_size);
        mTopOffset = (int) context.getResources().getDimension(R.dimen.local_alphabet_top_offset);
    }

    public AlphabetScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mAlphabetFontSize = (int) context.getResources().getDimension(
                R.dimen.local_alphabet_font_size);
        mTopOffset = (int) context.getResources().getDimension(R.dimen.local_alphabet_top_offset);
    }

    public AlphabetScrollerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mAlphabetFontSize = (int) context.getResources().getDimension(
                R.dimen.local_alphabet_font_size);
        mTopOffset = (int) context.getResources().getDimension(R.dimen.local_alphabet_top_offset);
    }

    public void init(RelativeLayout mContentLayout, AbsListView list, Activity activity) {
        mLayout = mContentLayout;
        mList = list;
        isDown = false;
        getSectionsFromIndexer();
    }

    private void getSectionsFromIndexer() {
        Adapter adapter = mList.getAdapter();
        mSectionIndexer = null;

        if (adapter instanceof HeaderViewListAdapter) {
            mListOffset = ((HeaderViewListAdapter) adapter).getHeadersCount();
            adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        }

        if (adapter instanceof SectionIndexer) {
            mSectionIndexer = (SectionIndexer) adapter;
            mSections = mSectionIndexer.getSections();

        } else {
            mSections = new String[] {
                " "
            };
        }

        if (mSections != null && mSections.length > 0 && mSections[0].equals(" ")) {
            mSections[0] = "#";
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
            default:
                onTouchUp(event);
                break;
        }

        return true;
    }

    public interface ReslveFreshListener {
        public void freshDrawer();
    }

    ReslveFreshListener listener;

    public void setNewDrawerTool(ReslveFreshListener freshListener) {
        this.listener = freshListener;
    }

    public void onTouchDown(MotionEvent event) {
        mToastHandler.removeMessages(0);
        // removeMyMessage();
        isDown = true;
        refreshView(event);
    }

    public void onTouchMove(MotionEvent event) {
        refreshView(event);
    }

    private void refreshView(MotionEvent event) {
        if (listener != null) {
            listener.freshDrawer();
        }
        mCurrentTouchMoveY = (int) event.getY();
        invalidate();

        mCurrentLetter = computeCurrentLetter(event);
        if (mCurrentLetter == null) {
            return;
        }

        invalidate();

        mCurrentDisplayLetter = mCurrentLetter;

        if (mCurrentDisplayLetter == null)
            return;

        if (isCurrentDisplayLetterChanged()) {
            mLastDisplayLetter = mCurrentDisplayLetter;

            float position = computeListPostion(mCurrentDisplayLetter);
            position = event.getY() / (mList.getHeight() - mAlphabetFontSize);
            scrollTo(position);
        }
    }

    private boolean isCurrentDisplayLetterChanged() {
        return mCurrentDisplayLetter != null && !mCurrentDisplayLetter.equals(mLastDisplayLetter);
    }

    private boolean isCurrentLetterValid() {
        Object[] mSectionsoff = {
                "A", "H", "R", "Z"
        };// zhihui

        boolean isLetterValid = false;
        getSectionsFromIndexer();

        String letter = mCurrentLetter;

        if (letter != null && letter.equals("#")) {
            letter = "{";
        }

        for (int i = 0; i < mSectionsoff.length; i++) {
            if (letter != null && letter.equals(mSectionsoff[i])) {
                isLetterValid = true;
                break;
            }
        }
        return isLetterValid;
    }

    private boolean isCurrentLetterValid(String letter) {
        if (TextUtils.isEmpty(letter)) {
            return false;
        }
        Object[] mSectionsoff = {
                "A", "H", "R", "Z"
        };
        boolean isLetterValid = false;
        getSectionsFromIndexer();

        if (letter.equals("#")) {
            letter = "{";
        }

        if (mSectionsoff != null) {
            for (int i = 0; i < mSectionsoff.length; i++) {
                if (letter != null && letter.equals(mSectionsoff[i])) {
                    isLetterValid = true;
                    break;
                }
            }
        }
        return isLetterValid;
    }

    public void onTouchUp(MotionEvent event) {
        if (mOnEventListener != null) {
            mOnEventListener.onTouchDown();
        }
        mToastHandler.sendEmptyMessageDelayed(0, LETTER_VIEW_DISPLAY_TIME);
    }

    private Handler mToastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            reset();
        }
    };

    public void reset() {
        mToastHandler.removeMessages(0);
        isDown = false;
        mCurrentLetter = null;
        mCurrentDisplayLetter = null;
        mLastDisplayLetter = null;
        AlphabetScrollerView.this.setVisibility(View.VISIBLE);
        invalidate();
    }

    private String computeCurrentLetter(MotionEvent event) {
        float y = event.getY();

        for (int i = 0; i < ALPHABET.length; i++) {
            float postionY = mAlphabetOffsetY + (mAlphabetFontSize + mAplhabetSpace) * (i - 1);
            float nextPostionY = mAlphabetOffsetY + (mAlphabetFontSize + mAplhabetSpace) * (i);
            if (y > postionY && (y < nextPostionY || i == ALPHABET.length - 1)) {
                return ALPHABET[i];
            }
        }

        return null;
    }

    private String computeCurrentLetter(float y) {

        for (int i = 0; i < ALPHABET.length; i++) {
            float postionY = mAlphabetOffsetY + (mAlphabetFontSize + mAplhabetSpace) * (i - 1);
            float nextPostionY = mAlphabetOffsetY + (mAlphabetFontSize + mAplhabetSpace) * (i);
            if (y > postionY && (y < nextPostionY || i == ALPHABET.length - 1)) {
                return ALPHABET[i];
            }
        }

        return null;
    }

    private float computeListPostion(String mCurrentLetter2) {
        return 0;
    }

    private void scrollTo(float position) {
        getSectionsFromIndexer();

        int count = mList.getCount();
        final Object[] sections = mSections;
        String letter = mCurrentDisplayLetter;
        if (letter != null && letter.equals("#")) {
            letter = "{";
        }

        if (sections != null && sections.length > 1) {
            final int nSections = sections.length;
            int section = -1;

            for (int i = 0; i < nSections; i++) {
                if (letter.equals(sections[i])) {
                    section = i;
                    break;
                }
            }

            if (section == -1) {
                return;
            }

            int index = mSectionIndexer.getPositionForSection(section);

            // Don't overflow
            if (index > count - 1) {
                index = count - 1;
            }

            ((ListView) mList).setSelectionFromTop(index + mListOffset, 0);
        } else {
            int index = (int) (position * count);
            if (mList instanceof ExpandableListView) {
                ExpandableListView expList = (ExpandableListView) mList;
                expList.setSelectionFromTop(expList.getFlatListPosition(ExpandableListView
                        .getPackedPositionForGroup(index + mListOffset)), 0);
            } else if (mList instanceof ListView) {
                ((ListView) mList).setSelectionFromTop(index + mListOffset, 0);
            } else {
                mList.setSelection(index + mListOffset);
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mList.getHeight() > 0) {
            mAlphabetOffsetX = getWidth() / 2;
            mAplhabetSpace = (mLayout.getHeight() - mTopOffset - mAlphabetFontSize * ALPHABET.length)
                    / ((ALPHABET.length - 1) * 1.0f);
        }

        if (mAplhabetSpace > mAlphabetFontSize * 2) {
            mAplhabetSpace = mAlphabetFontSize * 2;
        }

        mAlphabetOffsetY = (int) (mLayout.getHeight() - mAlphabetFontSize * ALPHABET.length - (ALPHABET.length - 1)
                * mAplhabetSpace)
                / 2 + mTopOffset;

        drawBackGround(canvas);
    }

    public void drawBackGround(Canvas canvas) {
        Paint paint = new Paint();

        int color = getResources().getColor(R.color.alphabet_color);
        paint.setColor(color);
        paint.setTextSize(mAlphabetFontSize);
        paint.setAntiAlias(true);
        paint.setTextAlign(Align.CENTER);

        String letter = isDown ? computeCurrentLetter(mCurrentTouchMoveY) : null;

        int i = 0;
        for (i = 0; i < ALPHABET.length; i++) {
            boolean isLetter = isCurrentLetterValid(ALPHABET[i]);

            if (letter != null && letter.equals(ALPHABET[i])) {
                // drawCurrentLetter(canvas, i);
                color = getResources().getColor(R.color.alphabet_color_select);
                paint.setColor(color);
                paint.setTextSize(mAlphabetFontSize);
                canvas.drawText(ALPHABET[i], mAlphabetOffsetX, mAlphabetOffsetY
                        + (mAlphabetFontSize + mAplhabetSpace) * i, paint);
            } else {
                if (!isLetter) {
                    color = getResources().getColor(R.color.alphabet_color);
                    paint.setColor(color);
                    paint.setTextSize(mAlphabetFontSize);
                } else {
                    color = getResources().getColor(R.color.alphabet_invalid_color);
                    paint.setColor(color);
                    paint.setTextSize(mAlphabetFontSize);
                }
                canvas.drawText(ALPHABET[i], mAlphabetOffsetX, mAlphabetOffsetY
                        + (mAlphabetFontSize + mAplhabetSpace) * i, paint);
            }
        }

    }

    // private void drawCurrentLetter(Canvas canvas, int i) {
    // if (mCurrentLetter == null) {
    // return;
    // }
    //
    // if (mAlphabetFocusDrawable == null) {
    // mAlphabetFocusDrawable = getContext().getResources().getDrawable(
    // R.drawable.alpha_playbackground);
    // }
    //
    // float wScale = 0.9f;
    // int width = (int)(mAlphabetFontSize * wScale);
    // int leftOffset = (int)(mAlphabetOffsetX - width / 2.0f);
    // int rightOffset = (int)(mAlphabetOffsetX - width / 2.0f) + width;
    //
    // // float hScale= 1.0f;
    // int height = (int)(mAlphabetFontSize * wScale);
    // int hOther =
    // (getResources().getInteger(R.integer.alphabet_bg_heigit_float))/10;
    // int topOffset = (int)(mAlphabetOffsetY + mAlphabetFontSize * i + (i - 1)
    // * mAplhabetSpace - height/hOther ) ;
    // int bottomOffset = (int)(topOffset + height);
    //
    // mAlphabetFocusDrawable.setBounds(leftOffset, topOffset, rightOffset,
    // bottomOffset);
    // mAlphabetFocusDrawable.draw(canvas);
    // }

    public void initSectionsFromIndexer() {
        getSectionsFromIndexer();
    }

    public interface OnEventListener {
        void onTouchDown();
    }
}
