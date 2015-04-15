
package cn.zmdx.kaka.locker.content.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.widget.FloatingActionButton;

public class CircleSpiritButton extends FloatingActionButton {

    private FrameLayout mContainer;

    private Animator mMsgComingAnimator;

    private Animator mAppearAnimator;

    private ImageView mArrawView;

    private int mArrowRes;
    public CircleSpiritButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CircleSpiritButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleSpiritButton(Context context) {
        this(context, null);
    }

    private void init() {
        setGravity(Gravity.CENTER);
        mContainer = new FrameLayout(getContext());
        int size = getDimension(getType() == TYPE_NORMAL ? R.dimen.fab_size_normal
                : R.dimen.fab_size_mini) - BaseInfoHelper.dip2px(getContext(), 25);
        mArrowRes = R.drawable.arrow_down;
        mArrawView = new ImageView(getContext());
        int paddint = BaseInfoHelper.dip2px(getContext(), 6);
        mArrawView.setPadding(paddint, paddint, paddint, paddint);
        mArrawView.setImageResource(mArrowRes);
        mContainer.addView(mArrawView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        addView(mContainer, new LayoutParams(size, size));
        mMsgComingAnimator = initDefaultMsgComingAnimator();
        mAppearAnimator = initDefaultAppearAnimator();
    }

    public void notifyNewFeed(Bitmap icon) {
        if (icon == null) {
            return;
        }
        mArrawView.setImageBitmap(icon);
        startShakeAnimator();
    }

    public void startShakeAnimator() {
        if (mMsgComingAnimator != null) {
            if (mMsgComingAnimator.isRunning()) {
                mMsgComingAnimator.cancel();
            }
            mMsgComingAnimator.start();
        }
    }

    public void startAppearAnimator() {
        if (mAppearAnimator != null) {
            mAppearAnimator.start();
        }
    }

    public void setAppearAnimator(Animator animator) {
        mAppearAnimator = animator;
    }

    private Animator initDefaultMsgComingAnimator() {
        Animator animator1 = ObjectAnimator.ofFloat(this, "scaleX", 1.2f, 1f);
        Animator animator2 = ObjectAnimator.ofFloat(this, "scaleY", 1.2f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator1, animator2);
        set.setDuration(2000);
        set.setInterpolator(new BounceInterpolator());
        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                HDBThreadUtils.postOnUiDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mArrawView.setImageResource(mArrowRes);
                    }
                }, 5000);
                super.onAnimationEnd(animation);
            }
        });
        return set;
    }

    private Animator initDefaultAppearAnimator() {
        Animator animator = ObjectAnimator.ofFloat(this, "translationY",
                BaseInfoHelper.dip2px(getContext(), 100), 0);
        animator.setDuration(800);
        animator.setInterpolator(new OvershootInterpolator());
        return animator;
    }
}
