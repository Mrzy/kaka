
package cn.zmdx.kaka.locker.splash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.BlurUtils;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

public class SplashFragment extends Fragment {

    private View mEntireView;

    private TypefaceTextView mVersion, mAppName;

    private ImageView mIcon;

    private ImageView mStar1, mStar2, mStar3;

    private int mScreenWidth, mStarWidth;

    private static final int DELAY_STAR1 = 200;

    private static final int DELAY_STAR2 = 1300;

    private static final int DELAY_STAR3 = 2200;

    private static final int DURATION_STAR1_TIME = 800;

    private static final int DURATION_STAR2_TIME = 400;

    private static final int DURATION_STAR3_TIME = 400;

    private ImageView mBlurView;

    private boolean isDestroy = false;

    public interface ISplashFragmentListener {
        void onSplashEnd();
    }

    private ISplashFragmentListener mCallBack;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallBack = (ISplashFragmentListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.pandora_splash, container, false);
        mScreenWidth = BaseInfoHelper.getRealWidth(HDApplication.getContext());
        mBlurView = (ImageView) mEntireView.findViewById(R.id.splash_blur);
        mVersion = (TypefaceTextView) mEntireView.findViewById(R.id.splash_version);
        mAppName = (TypefaceTextView) mEntireView.findViewById(R.id.splash_appname);
        mIcon = (ImageView) mEntireView.findViewById(R.id.splash_logo);
        mStar1 = (ImageView) mEntireView.findViewById(R.id.splash_star1);
        mStar2 = (ImageView) mEntireView.findViewById(R.id.splash_star2);
        mStar3 = (ImageView) mEntireView.findViewById(R.id.splash_star3);
        initVersion();
        invisiableViews(mIcon, mAppName, mVersion, mStar1, mStar2, mStar3);
        processAnimations();
        ViewTreeObserver vto2 = mStar1.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                mStar1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mStarWidth = mStar1.getWidth();
                int paddingLeft = mStarWidth + BaseInfoHelper.dip2px(HDApplication.getContext(), 10);
                mStar2.setPadding(paddingLeft, 0, 0, 0);
                mStar3.setPadding(2 * paddingLeft, 0, 0, 0);
            }
        });
        return mEntireView;
    }

    private void splashEnd() {
        HDBThreadUtils.postOnUiDelayed(new Runnable() {

            @Override
            public void run() {
                if (null != mCallBack) {
                    mCallBack.onSplashEnd();
                }
            }
        }, 200);
    }

    private AnimatorSet mIconAnimatorSet;

    private void processAnimations() {
        ObjectAnimator iconAlpha = ObjectAnimator.ofFloat(mIcon, "alpha", 0, 1);
        ObjectAnimator iconTrans = ObjectAnimator.ofFloat(mIcon, "translationY",
                DATE_WIDGET_TRANSLATIONY_DISTANCE, 0);
        AnimatorSet iconSet = new AnimatorSet();
        iconSet.setStartDelay(100);
        iconSet.playTogether(iconAlpha, iconTrans);

        ObjectAnimator appNameAlpha = ObjectAnimator.ofFloat(mAppName, "alpha", 0, 1);
        ObjectAnimator appNameTrans = ObjectAnimator.ofFloat(mAppName, "translationY",
                DATE_WIDGET_TRANSLATIONY_DISTANCE_1, 0);
        AnimatorSet appNameSet = new AnimatorSet();
        appNameSet.setStartDelay(300);
        appNameSet.playTogether(appNameAlpha, appNameTrans);

        ObjectAnimator versionAlpha = ObjectAnimator.ofFloat(mVersion, "alpha", 0, 1);
        ObjectAnimator versionTrans = ObjectAnimator.ofFloat(mVersion, "translationY",
                DATE_WIDGET_TRANSLATIONY_DISTANCE_2, 0);
        AnimatorSet versionSet = new AnimatorSet();
        versionSet.setStartDelay(500);
        versionSet.playTogether(versionAlpha, versionTrans);

        mIconAnimatorSet = new AnimatorSet();
        mIconAnimatorSet.playTogether(iconSet, appNameSet, versionSet);
        mIconAnimatorSet.setDuration(800);
        mIconAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mIconAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                processStarAnimations(mStar1, DELAY_STAR1, DURATION_STAR1_TIME, mStar1.getY(),
                        false);
                processStarAnimations(mStar2, DELAY_STAR2, DURATION_STAR2_TIME, mStar2.getY(),
                        false);
                processStarAnimations(mStar3, DELAY_STAR3, DURATION_STAR3_TIME, mStar3.getY(), true);
            }
        });
        mIconAnimatorSet.start();
    }

    private void invisiableViews(View... views) {
        for (View view : views) {
            if (view != null)
                view.setAlpha(0);
        }
    }

    private static final int DATE_WIDGET_TRANSLATIONY_DISTANCE = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 100);

    private static final int DATE_WIDGET_TRANSLATIONY_DISTANCE_1 = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 90);

    private static final int DATE_WIDGET_TRANSLATIONY_DISTANCE_2 = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 80);

    private void initVersion() {
        String versionName = BaseInfoHelper.getPkgVersionName(HDApplication.getContext());
        mVersion.setText("v" + versionName);
    }

    private ObjectAnimator mStarTrans;

    private AnimatorSet mStarAnimatorSet;

    private void processStarAnimations(final View view, int startDelay, int duration,
            final float param, boolean isEnd) {
        ObjectAnimator starAlpha = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        starAlpha.setInterpolator(new DecelerateInterpolator());
        mStarTrans = ObjectAnimator.ofFloat(view, "translationX", 0, mScreenWidth);
        mStarTrans.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) animation.getAnimatedValue();
                float y = (float) (0.5 * x) + param;
                view.setY(y);
            }
        });

        mStarAnimatorSet = new AnimatorSet();
        mStarAnimatorSet.setStartDelay(startDelay);
        mStarAnimatorSet.setDuration(duration);
        mStarAnimatorSet.playTogether(starAlpha, mStarTrans);
        if (isEnd) {
            mStarAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isDestroy) {
                        renderScreenLockerBlurEffect(ImageUtils.drawable2Bitmap(getResources()
                                .getDrawable(R.drawable.pandora_default_background)));
                    }

                }
            });
        }

        mStarAnimatorSet.start();
    }

    private ObjectAnimator mBlurBmpAlpha;

    private Bitmap mBlurBmp;

    private void renderScreenLockerBlurEffect(Bitmap bmp) {
        mBlurBmp = BlurUtils.doFastBlur(getActivity(), bmp, mBlurView, 30);
        mBlurView.setVisibility(View.VISIBLE);
        mBlurBmpAlpha = ObjectAnimator.ofFloat(mBlurView, "alpha", 0, 1);
        mBlurBmpAlpha.setDuration(1000);
        mBlurBmpAlpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                splashEnd();
            }
        });
        mBlurBmpAlpha.start();
    }

    @Override
    public void onDestroy() {
        isDestroy = true;
        if (mBlurBmp != null && !mBlurBmp.isRecycled()) {
            mBlurBmp.recycle();
            mBlurBmp = null;
        }
        if (null != mIconAnimatorSet) {
            mIconAnimatorSet.removeAllListeners();
            mIconAnimatorSet.getChildAnimations().clear();
            mIconAnimatorSet.cancel();
            mIconAnimatorSet = null;
        }
        if (null != mStarTrans) {
            mStarTrans.removeAllListeners();
            mStarTrans.cancel();
            mStarTrans = null;
        }
        if (null != mStarAnimatorSet) {
            mStarAnimatorSet.removeAllListeners();
            mStarAnimatorSet.getChildAnimations().clear();
            mStarAnimatorSet.cancel();
            mStarAnimatorSet = null;
        }
        if (null != mBlurBmpAlpha) {
            mBlurBmpAlpha.removeAllListeners();
            mBlurBmpAlpha.cancel();
            mBlurBmpAlpha = null;
        }
        super.onDestroy();
    }
}
