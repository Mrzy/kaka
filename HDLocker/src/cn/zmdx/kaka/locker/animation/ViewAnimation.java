
package cn.zmdx.kaka.locker.animation;

import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ViewAnimation extends Animation {

    private ViewGroup mView;

    private LayoutParams mParams;

    private int mHeight;

    private boolean isVisible;

    public ViewAnimation(ViewGroup view, int height, boolean visible) {
        this.mView = view;
        this.mHeight = height;
        this.isVisible = visible;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (isVisible) {
            mParams = mView.getLayoutParams();
            mParams.height = (int) (mHeight * interpolatedTime);
            mView.setLayoutParams(mParams);
        } else {
            mParams = mView.getLayoutParams();
            mParams.height = (int) (mHeight * (1 - interpolatedTime));
            mView.setLayoutParams(mParams);
        }

    }
}
